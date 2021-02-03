package com.grandia.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

import com.grandia.file.utils.FileUtils;

public class ITGETFileEditor {
    private static final Logger log = Logger.getLogger(ITGETFileEditor.class.getName());
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String HEX_MATCH_PATTERN_B = "060B";
    private static String HEX_MATCH_PATTERN_C = "060C";
    
    private static int MEM_OFFSET_START = 101392384;
    
    //8x8 offsets
    private static int ATK_OFFSET = 53076;
    private static int DEF_OFFSET = 53084;
    private static int ACT_OFFSET = 53092;
    private static int MOV_OFFSET = 53100;
    private static int STR_OFFSET = 53108;
    private static int VIT_OFFSET = 53116;
    private static int WIT_OFFSET = 53124;
    private static int AGI_OFFSET = 53132;
    private static int WEAPON_OFFSET = 53140;
    private static int SHIELD_OFFSET = 53152;
    private static int ARMOR_OFFSET = 53164;
    private static int HELMET_OFFSET = 53176;
    private static int SHOES_OFFSET = 53188;
    private static int JEWELRY_OFFSET = 53200;
    
    //8x16 offsets
    private static int x16_AUTO_OFFSET = 54104;
    private static int x16_MANUAL_OFFSET = 54112;
    private static int x16_USE_OFFSET = 54121;
    private static int x16_DISCARD_ITEM_OFFSET = 54127;
    private static int x16_DISCARD_INVN_OFFSET = 54133;
    private static int x16_FOUND_ITEMS_OFFSET = 54139;
    private static int x16_ACQUIRED_OFFSET = 54151;
    private static int x16_GIVE_ITEM_TO_OFFSET = 54162;
    
    private static int NAMES_STRING_OFFSET = 55424;
    
    //8x16 values
    private static String x16_AUTO_VALUE;
    private static String x16_MANUAL_VALUE;
    private static String x16_USE_VALUE;
    private static String x16_DISCARD_ITEM_VALUE;
    private static String x16_DISCARD_INVN_VALUE;
    private static String x16_FOUND_ITEMS_VALUE;
    private static String x16_ACQUIRED_VALUE;
    private static String x16_GIVE_ITEM_TO_VALUE;
    
    private static String ATK_VALUE;
    private static String DEF_VALUE;
    private static String ACT_VALUE;
    private static String MOV_VALUE;
    private static String STR_VALUE;
    private static String VIT_VALUE;
    private static String WIT_VALUE;
    private static String AGI_VALUE;
    private static String WEAPON_VALUE;
    private static String SHIELD_VALUE;
    private static String ARMOR_VALUE;
    private static String HELMET_VALUE;
    private static String SHOES_VALUE;
    private static String JEWELRY_VALUE;
    
    private static String NAMES_STRING_VALUE;
    
   
    private static int NUM_OF_NAMES = 8;
    private static byte[] itgetBytes;
    
    private String inputFilePath;
    private String outputFilePath;
    private Map<String, Integer> fieldToOffsetMap;
    private Map<String, String> fieldToNewValueMap;
    private Map<String, byte[]> fieldToValueMap;
    private Map<Integer, Integer> fileOffsetToMemOffsetValuesMap;
    private static Properties properties;
    private Map<Integer, String> orderToFieldNameMap;
    
    public ITGETFileEditor inputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
        return this;
    }
    
    public ITGETFileEditor outputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
        return this;
    }
    
    public void init() {
        try {
            InputStream in = new FileInputStream("translation.properties");
            properties = new Properties();
            properties.load(in);
            updateReplacementValues();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        parse();
        populateOrderToFieldNameMap();
        populateFieldToOffsetMap();
        populateFieldToValueMap();
        populateFileOffsetToMemOffsetValuesMap();
        populateFieldToNewValueMap();
    }
    
    public void autoReplace() {
        for(int i = 0; i < orderToFieldNameMap.size(); i++) {
            String key = orderToFieldNameMap.get(i);
            replaceFieldValue(key, fieldToNewValueMap.get(key));
        }
    }
    
    public void replaceFieldValue(String key, String newString) {
        
        String hexValue;
        if(newString == null) {
            hexValue = "00";
        } else if(newString == "X2_NULL"){
            hexValue = "0000";
        }
        else {
            hexValue = bytesToHex(newString.getBytes());
        }
        
        String hexString = new String();
        if(key.contains("8x16")) {
            hexString = "03" + hexValue + "00";
        } else{
            hexString = hexValue + "00";
        }
        
        if(key == "NAMES") {
            hexString = hexString.replace("20", "00");
        }
        byte[] newValue = DatatypeConverter.parseHexBinary(hexString);
        byte[] oldValue = fieldToValueMap.get(key);
      
        
        
        int delta = newValue.length - oldValue.length;
        int anchorOffset = fieldToOffsetMap.get(key) + MEM_OFFSET_START;
        
        if(delta != 0) {
            updateOffsets(delta, anchorOffset);
        }
        
        updateCmdmnByteArray(fieldToOffsetMap.get(key), oldValue, newValue);
        List<Integer> offsetsToRemove = new ArrayList<>();
        
        for(Integer fileOffset : fileOffsetToMemOffsetValuesMap.keySet()) {
            int memOffset = fileOffsetToMemOffsetValuesMap.get(fileOffset);
            String hexMemOffset = String.format("%08X", memOffset).toUpperCase();
            
            byte[] memOffsetByte = DatatypeConverter.parseHexBinary(hexMemOffset);
            
            updateCmdmnByteArray(fileOffset, memOffsetByte, memOffsetByte);
            
            if(memOffset < anchorOffset) {
                offsetsToRemove.add(fileOffset);
            }
        }

        for(int remove : offsetsToRemove) {
            fileOffsetToMemOffsetValuesMap.remove(remove);
        }
        
        fieldToValueMap.put(key, newValue);
    }
    
    public void writeToFile() {
        FileUtils.writeToFile(itgetBytes, "ITGET", ".BIN", ".BIN", outputFilePath);
    }
    
    private void updateCmdmnByteArray(int offset, byte[] oldValue, byte[] newValue) {
        byte[] itgetBytesA = Arrays.copyOfRange(itgetBytes, 0, offset);
        byte[] itgetBytesB = Arrays.copyOfRange(itgetBytes, offset + oldValue.length, itgetBytes.length);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(itgetBytesA);
            baos.write(newValue);
            baos.write(itgetBytesB);
            
            itgetBytes = baos.toByteArray();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Caught IOException attempting to write bytes.", e);
            e.printStackTrace();
        }
    }
    
    private void populateFieldToOffsetMap() {
        fieldToOffsetMap = new HashMap<>();
        
        //8x16 offsets
        fieldToOffsetMap.put("8x16 AUTO", x16_AUTO_OFFSET);
        fieldToOffsetMap.put("8x16 MANUAL", x16_MANUAL_OFFSET);
        fieldToOffsetMap.put("8x16 USE", x16_USE_OFFSET);
        fieldToOffsetMap.put("8x16 DISCARD ITEM", x16_DISCARD_ITEM_OFFSET);
        fieldToOffsetMap.put("8x16 DISCARD INVN", x16_DISCARD_INVN_OFFSET);
        fieldToOffsetMap.put("8x16 FOUND ITEMS", x16_FOUND_ITEMS_OFFSET);
        fieldToOffsetMap.put("8x16 ACQUIRED", x16_ACQUIRED_OFFSET);
        fieldToOffsetMap.put("8x16 GIVE ITEM TO", x16_GIVE_ITEM_TO_OFFSET);
        
        //8x8 offsets
        fieldToOffsetMap.put("ATK", ATK_OFFSET);
        fieldToOffsetMap.put("DEF", DEF_OFFSET);
        fieldToOffsetMap.put("ACT", ACT_OFFSET);
        fieldToOffsetMap.put("MOV", MOV_OFFSET);
        fieldToOffsetMap.put("STR", STR_OFFSET);
        fieldToOffsetMap.put("VIT", VIT_OFFSET);
        fieldToOffsetMap.put("WIT", WIT_OFFSET);
        fieldToOffsetMap.put("AGI", AGI_OFFSET);
        fieldToOffsetMap.put("WEAPON", WEAPON_OFFSET);
        fieldToOffsetMap.put("SHIELD", SHIELD_OFFSET);
        fieldToOffsetMap.put("ARMOR", ARMOR_OFFSET);
        fieldToOffsetMap.put("HELMET", HELMET_OFFSET);
        fieldToOffsetMap.put("SHOES", SHOES_OFFSET);
        fieldToOffsetMap.put("JEWELRY", JEWELRY_OFFSET);
        fieldToOffsetMap.put("NAMES", NAMES_STRING_OFFSET  );
    }
    
    private void populateFieldToNewValueMap() {
        fieldToNewValueMap = new HashMap<>();
        
        //8x16 offsets
        fieldToNewValueMap.put("8x16 AUTO", x16_AUTO_VALUE);
        fieldToNewValueMap.put("8x16 MANUAL", x16_MANUAL_VALUE);
        fieldToNewValueMap.put("8x16 USE", x16_USE_VALUE);
        fieldToNewValueMap.put("8x16 DISCARD ITEM", x16_DISCARD_ITEM_VALUE);
        fieldToNewValueMap.put("8x16 DISCARD INVN", x16_DISCARD_INVN_VALUE);
        fieldToNewValueMap.put("8x16 FOUND ITEMS", x16_FOUND_ITEMS_VALUE);
        fieldToNewValueMap.put("8x16 ACQUIRED", x16_ACQUIRED_VALUE);
        fieldToNewValueMap.put("8x16 GIVE ITEM TO", x16_GIVE_ITEM_TO_VALUE);
        
        //8x8 offsets
        fieldToNewValueMap.put("ATK", ATK_VALUE);
        fieldToNewValueMap.put("DEF", DEF_VALUE);
        fieldToNewValueMap.put("ACT", ACT_VALUE);
        fieldToNewValueMap.put("MOV", MOV_VALUE);
        fieldToNewValueMap.put("STR", STR_VALUE);
        fieldToNewValueMap.put("VIT", VIT_VALUE);
        fieldToNewValueMap.put("WIT", WIT_VALUE);
        fieldToNewValueMap.put("AGI", AGI_VALUE);
        fieldToNewValueMap.put("WEAPON", WEAPON_VALUE);
        fieldToNewValueMap.put("SHIELD", SHIELD_VALUE);
        fieldToNewValueMap.put("ARMOR", ARMOR_VALUE);
        fieldToNewValueMap.put("HELMET", HELMET_VALUE);
        fieldToNewValueMap.put("SHOES", SHOES_VALUE);
        fieldToNewValueMap.put("JEWELRY", JEWELRY_VALUE);
        fieldToNewValueMap.put("NAMES", NAMES_STRING_VALUE  );
    }
    
    private void populateFieldToValueMap() {
       fieldToValueMap = new HashMap<>();
       for(String key : fieldToOffsetMap.keySet()) {
           if(!key.equals("NAMES")) {
               int offset = fieldToOffsetMap.get(key);
               byte[] value = readValueFromFileBytes(offset);
               fieldToValueMap.put(key, value);
           }else {
               int offset = fieldToOffsetMap.get(key);
               byte[] value = readNamesValueFromFileBytes(offset);
               fieldToValueMap.put(key, value);
           }
       }
    }
    
    private void populateFileOffsetToMemOffsetValuesMap() {
        fileOffsetToMemOffsetValuesMap = new HashMap<>();
        int length = itgetBytes.length;
        int i = 0;
        while (i + 4 < length) {
            //Read 4 bytes out of the Array into a temporary buffer.
            ByteBuffer bb = ByteBuffer.wrap(itgetBytes, i, 4);
            
            int value = bb.getInt();
            
            String hexString = String.format("%08X", value).toUpperCase();
            if(hexString.startsWith(HEX_MATCH_PATTERN_B) || hexString.startsWith(HEX_MATCH_PATTERN_C)) {
                System.out.println(hexString);
                fileOffsetToMemOffsetValuesMap.put(i, value);
            }
            i+=4;
        }

    }
    
    private void parse() {
        
        if(inputFilePath == null) {
            log.log(Level.WARNING, "Input File path is null, aborting parsing.");
        } else {
            File cmdmn = new File(inputFilePath);
            try {
                itgetBytes = Files.readAllBytes(cmdmn.toPath());
            } catch (IOException e) {
                log.log(Level.SEVERE, "Caught IOException attempting to read bytes.", e);
                e.printStackTrace();
            }
        }
    }
    
    private byte[] readValueFromFileBytes(int offset) {
        byte[] buffer = new byte[1];
        
        boolean endOfValue = false;
        int i = offset;
        int size = 0;
        while(!endOfValue) {
            buffer = Arrays.copyOfRange(itgetBytes, i, i + 1);
            i++;
            if(bytesToHex(buffer).equals("00")) {
                size = i - offset;
                endOfValue = true;
            }
        }
        
        byte[] value = Arrays.copyOfRange(itgetBytes, offset, offset + size);
        
        return value;
    }
    
    private byte[] readNamesValueFromFileBytes(int offset) {
        byte[] buffer = new byte[1];
        
        boolean endOfValue = false;
        int i = offset;
        int size = 0;
        int numOfNames = 0;
        while(!endOfValue) {
            buffer = Arrays.copyOfRange(itgetBytes, i, i + 1);
            i++;
            if(bytesToHex(buffer).equals("00") && numOfNames == NUM_OF_NAMES) {
                size = i - offset;
                endOfValue = true;
            } else if(bytesToHex(buffer).equals("00") && numOfNames < NUM_OF_NAMES) {
                numOfNames++;
            }
        }
        
        byte[] value = Arrays.copyOfRange(itgetBytes, offset, offset + size);
        
        return value;
    }
    
    private void updateOffsets(int delta, int anchorOffset) {
        int fileAnchorOffset = anchorOffset - MEM_OFFSET_START;
        List<Integer> keys = new ArrayList<>();
        keys.addAll(fileOffsetToMemOffsetValuesMap.keySet());
        for(Integer key : keys) {
            Integer memOffset = fileOffsetToMemOffsetValuesMap.get(key);
            if(memOffset > anchorOffset) {
                memOffset = memOffset + delta;
                fileOffsetToMemOffsetValuesMap.put(key, memOffset);
            }
            
            if(key > fileAnchorOffset) {
                fileOffsetToMemOffsetValuesMap.remove(key);
                key = key + delta;
                fileOffsetToMemOffsetValuesMap.put(key,  memOffset);
            }
        }
        
        for(String key : fieldToOffsetMap.keySet()) {
            Integer fileOffset = fieldToOffsetMap.get(key);
            if(fileOffset > fileAnchorOffset) {
                fileOffset = fileOffset + delta;
                fieldToOffsetMap.put(key, fileOffset);
            }
        }
    }
    
    /**
     * This map is needed for Auto-Updating. IF the offsets are updated out of order, incorrect values can be put in for HWRAM offsets.
     */
    private void populateOrderToFieldNameMap() {
        orderToFieldNameMap = new HashMap<>();
        
        orderToFieldNameMap.put(0, "ATK");
        orderToFieldNameMap.put(1, "DEF");
        orderToFieldNameMap.put(2, "ACT");
        orderToFieldNameMap.put(3, "MOV");
        orderToFieldNameMap.put(4, "STR");
        orderToFieldNameMap.put(5, "VIT");
        orderToFieldNameMap.put(6, "WIT");
        orderToFieldNameMap.put(7, "AGI");
        orderToFieldNameMap.put(8, "WEAPON");
        orderToFieldNameMap.put(9, "SHIELD");
        orderToFieldNameMap.put(10, "ARMOR");
        orderToFieldNameMap.put(11, "HELMET");
        orderToFieldNameMap.put(12, "SHOES");
        orderToFieldNameMap.put(13, "JEWELRY");
        orderToFieldNameMap.put(14, "8x16 AUTO");
        orderToFieldNameMap.put(15, "8x16 MANUAL");
        orderToFieldNameMap.put(16, "8x16 USE");
        orderToFieldNameMap.put(17, "8x16 DISCARD ITEM");
        orderToFieldNameMap.put(18, "8x16 DISCARD INVN");
        orderToFieldNameMap.put(19, "8x16 FOUND ITEMS");
        orderToFieldNameMap.put(20, "8x16 ACQUIRED");
        orderToFieldNameMap.put(21, "8x16 GIVE ITEM TO");
        orderToFieldNameMap.put(22, "NAMES");
    }
    
    private static void updateReplacementValues() {
        
        x16_AUTO_VALUE = properties.getProperty("itget.x16_AUTO_VALUE").replace("\"", "");
        x16_MANUAL_VALUE = properties.getProperty("itget.x16_MANUAL_VALUE").replace("\"", "");
        x16_USE_VALUE = properties.getProperty("itget.x16_USE_VALUE").replace("\"", "");
        x16_DISCARD_ITEM_VALUE = properties.getProperty("itget.x16_DISCARD_ITEM_VALUE").replace("\"", "");
        x16_DISCARD_INVN_VALUE = properties.getProperty("itget.x16_DISCARD_INVN_VALUE").replace("\"", "");
        x16_FOUND_ITEMS_VALUE = properties.getProperty("itget.x16_FOUND_ITEMS_VALUE").replace("\"", "");
        x16_ACQUIRED_VALUE = properties.getProperty("itget.x16_ACQUIRED_VALUE").replace("\"", "");
        x16_GIVE_ITEM_TO_VALUE = properties.getProperty("itget.x16_GIVE_ITEM_TO_VALUE").replace("\"", "");
        
        ATK_VALUE = properties.getProperty("itget.ATK_VALUE").replace("\"", "");
        DEF_VALUE = properties.getProperty("itget.DEF_VALUE").replace("\"", "");
        ACT_VALUE = properties.getProperty("itget.ACT_VALUE").replace("\"", "");
        MOV_VALUE = properties.getProperty("itget.MOV_VALUE").replace("\"", "");
        STR_VALUE = properties.getProperty("itget.STR_VALUE").replace("\"", "");
        VIT_VALUE = properties.getProperty("itget.VIT_VALUE").replace("\"", "");
        WIT_VALUE = properties.getProperty("itget.WIT_VALUE").replace("\"", "");
        AGI_VALUE = properties.getProperty("itget.AGI_VALUE").replace("\"", "");
        WEAPON_VALUE = properties.getProperty("itget.WEAPON_VALUE").replace("\"", "");
        SHIELD_VALUE = properties.getProperty("itget.SHIELD_VALUE").replace("\"", "");
        ARMOR_VALUE = properties.getProperty("itget.ARMOR_VALUE").replace("\"", "");
        HELMET_VALUE = properties.getProperty("itget.HELMET_VALUE").replace("\"", "");
        SHOES_VALUE = properties.getProperty("itget.SHOES_VALUE").replace("\"", "");
        JEWELRY_VALUE = properties.getProperty("itget.JEWELRY_VALUE").replace("\"", "");
        
        NAMES_STRING_VALUE = properties.getProperty("itget.NAMES_STRING_VALUE").replace("\"", "");
    }
    
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
