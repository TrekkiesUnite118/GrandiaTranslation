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

public class CNFIGFileEditor {
    private static final Logger log = Logger.getLogger(SVLDFileEditor.class.getName());
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String HEX_MATCH_PATTERN_B = "060B";
    private static String HEX_MATCH_PATTERN_C = "060C";
    
    private static int MEM_OFFSET_START = 101392384;
    private static int ACTION_BUTTON_STRING_OFFSET = 39088;
    private static int CAMERA_STRING_OFFSET = 39097;
    private static int SOUND_STRING_OFFSET = 39109;
    private static int A_BUTTON_STRING_OFFSET = 39119;
    private static int C_BUTTON_STRING_OFFSET = 39125;
    private static int NORMAL_STRING_OFFSET_2 = 39131;
    private static int INVERTED_STRING_OFFSET = 39133;
    private static int MONO_STRING_OFFSET = 39137;
    private static int STEREO_STRING_OFFSET = 39143;
    private static int SLASH_OFFSET = 39149;
    private static int OPTIONS_STRING_OFFSET = 39158;
    
    private static String ACTION_BUTTON_STRING;
    private static String CAMERA_STRING;
    private static String SOUND_STRING;
    private static String A_BUTTON_STRING;
    private static String C_BUTTON_STRING;
    private static String NORMAL_STRING;
    private static String REVERSE_STRING;
    private static String MONO_STRING;
    private static String STEREO_STRING;
    private static String SLASH_STRING;
    private static String OPTIONS_STRING;
    
   
    private static byte[] cnfigBytes;
    
    private String inputFilePath;
    private String outputFilePath;
    private Map<String, Integer> fieldToOffsetMap;
    private Map<String, byte[]> fieldToValueMap;
    private Map<Integer, Integer> fileOffsetToMemOffsetValuesMap;
    private Map<Integer, String> orderToFieldNameMap;
    private Map<String, String> fieldToNewValueMap;
    private static Properties properties;
    
    public CNFIGFileEditor inputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
        return this;
    }
    
    public CNFIGFileEditor outputFilePath(String outputFilePath) {
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

        String hexValue = bytesToHex(newString.getBytes());
        String hexString = new String();
        if(key != "TIME") {
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
        
        updateSvldByteArray(fieldToOffsetMap.get(key), oldValue, newValue);
        List<Integer> offsetsToRemove = new ArrayList<>();
        
        for(Integer fileOffset : fileOffsetToMemOffsetValuesMap.keySet()) {
            int memOffset = fileOffsetToMemOffsetValuesMap.get(fileOffset);
            String hexMemOffset = String.format("%08X", memOffset).toUpperCase();
            
            byte[] memOffsetByte = DatatypeConverter.parseHexBinary(hexMemOffset);
            
            updateSvldByteArray(fileOffset, memOffsetByte, memOffsetByte);
            
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
        FileUtils.writeToFile(cnfigBytes, "CNFIG", ".BIN", ".BIN", outputFilePath);
    }
    
    private void updateSvldByteArray(int offset, byte[] oldValue, byte[] newValue) {
        byte[] cnfigBytesA = Arrays.copyOfRange(cnfigBytes, 0, offset);
        byte[] cnfigBytesB = Arrays.copyOfRange(cnfigBytes, offset + oldValue.length, cnfigBytes.length);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(cnfigBytesA);
            baos.write(newValue);
            baos.write(cnfigBytesB);
            
            cnfigBytes = baos.toByteArray();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Caught IOException attempting to write bytes.", e);
            e.printStackTrace();
        }
    }
    
    private void populateFieldToOffsetMap() {
        fieldToOffsetMap = new HashMap<>();
        
        fieldToOffsetMap.put("ACTION", ACTION_BUTTON_STRING_OFFSET);
        fieldToOffsetMap.put("CAMERA", CAMERA_STRING_OFFSET);
        fieldToOffsetMap.put("SOUND", SOUND_STRING_OFFSET);
        fieldToOffsetMap.put("A BUTTON", A_BUTTON_STRING_OFFSET);
        fieldToOffsetMap.put("C BUTTON", C_BUTTON_STRING_OFFSET);
        fieldToOffsetMap.put("NORMAL", NORMAL_STRING_OFFSET_2);
        fieldToOffsetMap.put("INVERTED", INVERTED_STRING_OFFSET);
        fieldToOffsetMap.put("MONO", MONO_STRING_OFFSET);
        fieldToOffsetMap.put("STEREO", STEREO_STRING_OFFSET);
        fieldToOffsetMap.put("/", SLASH_OFFSET);
        fieldToOffsetMap.put("OPTIONS", OPTIONS_STRING_OFFSET);
      
    } 
    
    /**
     * This map is needed for Auto-Updating. IF the offsets are updated out of order, incorrect values can be put in for HWRAM offsets.
     */
    private void populateOrderToFieldNameMap() {
        orderToFieldNameMap = new HashMap<>();

        orderToFieldNameMap.put(0, "ACTION");
        orderToFieldNameMap.put(1, "CAMERA");
        orderToFieldNameMap.put(2, "SOUND");
        orderToFieldNameMap.put(3, "A BUTTON");
        orderToFieldNameMap.put(4, "C BUTTON");
        orderToFieldNameMap.put(5, "NORMAL");
        orderToFieldNameMap.put(6, "INVERTED");
        orderToFieldNameMap.put(7, "MONO");
        orderToFieldNameMap.put(8, "STEREO");
        orderToFieldNameMap.put(9, "/");
        orderToFieldNameMap.put(10, "OPTIONS");
    }
    
    private void populateFieldToNewValueMap() {
        fieldToNewValueMap = new HashMap<>();
        
        fieldToNewValueMap.put("ACTION", ACTION_BUTTON_STRING);
        fieldToNewValueMap.put("CAMERA", CAMERA_STRING);
        fieldToNewValueMap.put("SOUND", SOUND_STRING);
        fieldToNewValueMap.put("A BUTTON", A_BUTTON_STRING);
        fieldToNewValueMap.put("C BUTTON", C_BUTTON_STRING);
        fieldToNewValueMap.put("NORMAL", NORMAL_STRING);
        fieldToNewValueMap.put("INVERTED", REVERSE_STRING);
        fieldToNewValueMap.put("MONO", MONO_STRING);
        fieldToNewValueMap.put("STEREO", STEREO_STRING);
        fieldToNewValueMap.put("/", SLASH_STRING);
        fieldToNewValueMap.put("OPTIONS", OPTIONS_STRING);
        
    }
    
    private void populateFieldToValueMap() {
       fieldToValueMap = new HashMap<>();
       for(String key : fieldToOffsetMap.keySet()) {
           int offset = fieldToOffsetMap.get(key);
           byte[] value = readValueFromFileBytes(offset);
           fieldToValueMap.put(key, value);
       }
    }
    
    private void populateFileOffsetToMemOffsetValuesMap() {
        fileOffsetToMemOffsetValuesMap = new HashMap<>();
        int length = cnfigBytes.length;
        int i = 0;
        while (i + 4 < length) {
            //Read 4 bytes out of the Array into a temporary buffer.
            ByteBuffer bb = ByteBuffer.wrap(cnfigBytes, i, 4);
            
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
            File svld = new File(inputFilePath);
            try {
                svld.setReadable(true);
                System.out.println("Is " + svld.getAbsolutePath() + " Readable? " + svld.canRead());
                cnfigBytes = Files.readAllBytes(svld.toPath());
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
            buffer = Arrays.copyOfRange(cnfigBytes, i, i + 1);
            i++;
            if(bytesToHex(buffer).equals("00")) {
                size = i - offset;
                endOfValue = true;
            }
        }
        
        byte[] value = Arrays.copyOfRange(cnfigBytes, offset, offset + size);
        
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
    
    private static void updateReplacementValues() {
        
        ACTION_BUTTON_STRING = properties.getProperty("config.ACTION_BUTTON_STRING").replace("\"", "");
        CAMERA_STRING = properties.getProperty("config.CAMERA_STRING").replace("\"", "");
        SOUND_STRING = properties.getProperty("config.SOUND_STRING").replace("\"", "");
        A_BUTTON_STRING = properties.getProperty("config.A_BUTTON_STRING").replace("\"", "");
        C_BUTTON_STRING = properties.getProperty("config.C_BUTTON_STRING").replace("\"", "");
        NORMAL_STRING = properties.getProperty("config.NORMAL_STRING").replace("\"", "");
        REVERSE_STRING = properties.getProperty("config.REVERSE_STRING").replace("\"", "");
        MONO_STRING = properties.getProperty("config.MONO_STRING").replace("\"", "");
        STEREO_STRING = properties.getProperty("config.STEREO_STRING").replace("\"", "");
        SLASH_STRING = properties.getProperty("config.SLASH_STRING").replace("\"", "");
        OPTIONS_STRING = properties.getProperty("config.OPTIONS_STRING").replace("\"", "");
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
