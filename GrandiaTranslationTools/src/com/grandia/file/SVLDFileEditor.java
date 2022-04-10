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

/**
 * SVLDFileEditor
 * 
 * This utility will replace text values in the SVLD.BIN file with new values.
 * It will then update all the HWRAM offsets in the file accordingly.
 * 
 * @author TrekkiesUnite118
 *
 */
public class SVLDFileEditor {
    
    private static final Logger log = Logger.getLogger(SVLDFileEditor.class.getName());
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    //HWRAM Prefixes
    private static String HEX_MATCH_PATTERN_B = "060B";
    private static String HEX_MATCH_PATTERN_C = "060C";
    
    //Offsets where Text Lives
    private static int MEM_OFFSET_START = 101392384;
    private static int TIME_STRING_OFFSET = 44268;
    private static int LOAD_STRING_OFFSET = 45348;
    private static int SAVE_STRING_OFFSET = 45353;
    private static int SYSTEM_RAM_STRING_OFFSET = 45358;
    private static int CARTRIDGE_RAM_STRING_OFFSET = 45369;
    private static int CARTRIDGE_RAM_STRING_OFFSET_2 = 45380;
    private static int NOT_INITIALIZED_OFFSET = 45404;
    private static int NO_SAVE_DATA_OFFSET = 45418;
    private static int AREA_OFFSET = 45425;
    private static int SELECT_A_SAVE_FILE_1 = 45431;
    private static int SELECT_A_SAVE_FILE_2 = 45451;
    private static int CANNOT_CANCEL_OFFSET = 45471;
    private static int INSUFFICIENT_MEMORY_OFFSET = 45482;
    private static int DATA_CANNOT_BE_USED_OFFSET = 45497;
    private static int WRONG_DISC_NUMBER_OFFSET = 45513;
    private static int CORRUPT_DATA_OFFSET = 45535;
    private static int LOAD_CONFIRM_STRING_OFFSET = 45547;
    private static int LOAD_YES_STRING_OFFSET = 45557;
    private static int LOAD_SLASH_STRING_OFFSET = 45564;
    private static int LOAD_NO_STRING_OFFSET = 45567;
    private static int OVERWRITE_STRING_OFFSET = 45571;
    private static int OVERWRITE_YES_STRING_OFFSET = 45580;
    private static int OVERWRITE_SLASH_STRING_OFFSET = 45587;
    private static int OVERWRITE_NO_STRING_OFFSET = 45590;
    private static int NAMES_STRING_OFFSET = 46824;
    private static int DIGITAL_MUSEUM_OFFSET = 12;
    private boolean isDigitalMuseum = false;
    
    private static String TIME_STRING;
    private static String LOAD_STRING;
    private static String SAVE_STRING;
    private static String SYSTEM_RAM_STRING;
    private static String CARTRIDGE_RAM_STRING;
    private static String CARTRIDGE_RAM_STRING_2;
    private static String NOT_INITIALIZED;
    private static String NO_SAVE_DATA;
    private static String AREA;
    private static String SELECT_A_SAVE_FILE_1_VALUE;
    private static String SELECT_A_SAVE_FILE_2_VALUE;
    private static String CANNOT_CANCEL;
    private static String INSUFFICIENT_MEMORY;
    private static String DATA_CANNOT_BE_USED;
    private static String WRONG_DISC_NUMBER;
    private static String CORRUPT_DATA;
    private static String LOAD_CONFIRM_STRING;
    private static String LOAD_YES_STRING;
    private static String LOAD_SLASH_STRING;
    private static String LOAD_NO_STRING;
    private static String OVERWRITE_STRING;
    private static String OVERWRITE_YES_STRING;
    private static String OVERWRITE_SLASH_STRING;
    private static String OVERWRITE_NO_STRING;
    private static String NAMES_STRING;
        
    private static int NUM_OF_NAMES = 8;
    private static byte[] svldBytes;
    
    private String inputFilePath;
    private String outputFilePath;
    private Map<String, Integer> fieldToOffsetMap;
    private Map<Integer, String> orderToFieldNameMap;
    private Map<String, String> fieldToNewValueMap;
    private Map<String, byte[]> fieldToValueMap;
    private Map<Integer, Integer> fileOffsetToMemOffsetValuesMap;
    private static Properties properties;
    
    /**
     * 
     * @param inputFilePath
     * @return
     */
    public SVLDFileEditor inputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
        return this;
    }
    
    /**
     * 
     * @param outputFilePath
     * @return
     */
    public SVLDFileEditor outputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
        return this;
    }
    
    /**
     * 
     * @param isDigitalMuseum
     * @return
     */
    public SVLDFileEditor isDigitalMuseum(boolean isDigitalMuseum) {
        this.isDigitalMuseum = isDigitalMuseum;
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
        populateFieldToOffsetMap();
        populateFieldToValueMap();
        populateFileOffsetToMemOffsetValuesMap();
        populateFieldToNewValueMap();
        populateOrderToFieldNameMap();
    }
    
    /**
     * Auto replace method. This will iterate through the preset values and auto replace them.
     */
    public void autoReplace() {
        for(int i = 0; i < orderToFieldNameMap.size(); i++) {
            String key = orderToFieldNameMap.get(i);
            replaceFieldValue(key, fieldToNewValueMap.get(key));
        }
    }
    
    /**
     * Replaces a Field with a new value, then updates all the offsets accordingly.
     * 
     * 
     * @param key
     * @param newString
     */
    public void replaceFieldValue(String key, String newString) {

        String hexValue = bytesToHex(newString.getBytes());
        String hexString = new String();
        //If it's not the Character names, add the 0x03 prefix and append the 00 delimiter
        if(key != "TIME" && key != "NAMES") {
            hexString = "03" + hexValue + "00";
        } else{
            //If it's Time or Names, don't append the 0x03
            hexString = hexValue + "00";
        }
        
        //If names, replace spaces with delimiters.
        if(key == "NAMES") {
            hexString = hexString.replace("20", "00");
        }
        byte[] newValue = DatatypeConverter.parseHexBinary(hexString);
        byte[] oldValue = fieldToValueMap.get(key);
      
        //Determine how much we need to update the offsets by.
        int delta = newValue.length - oldValue.length;
        
        //Determine the anchor point and call update Offsets if the size changed.
        int anchorOffset = fieldToOffsetMap.get(key) + MEM_OFFSET_START;
        if(delta != 0) {
            updateOffsets(delta, anchorOffset);
        }
        
        //Update the value in the byte array.
        updateSvldByteArray(fieldToOffsetMap.get(key), oldValue, newValue);
        List<Integer> offsetsToRemove = new ArrayList<>();
        
        //Update the offset values in the byte array.
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
        
        //Update the fieldToValue Map.
        fieldToValueMap.put(key, newValue);
    }
    
    /**
     * Writes the bytes to a new file.
     */
    public void writeToFile() {
        FileUtils.writeToFile(svldBytes, "SVLD", ".BIN", ".BIN", outputFilePath);
    }
    
    /**
     * Injects the new value into the byte array.
     * @param offset
     * @param oldValue
     * @param newValue
     */
    private void updateSvldByteArray(int offset, byte[] oldValue, byte[] newValue) {
        byte[] svldBytesA = Arrays.copyOfRange(svldBytes, 0, offset);
        byte[] svldBytesB = Arrays.copyOfRange(svldBytes, offset + oldValue.length, svldBytes.length);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(svldBytesA);
            baos.write(newValue);
            baos.write(svldBytesB);
            
            svldBytes = baos.toByteArray();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Caught IOException attempting to write bytes.", e);
            e.printStackTrace();
        }
    }
    
    private void populateFieldToOffsetMap() {
        fieldToOffsetMap = new HashMap<>();
        
        fieldToOffsetMap.put("TIME", TIME_STRING_OFFSET);
        fieldToOffsetMap.put("LOAD", LOAD_STRING_OFFSET);
        fieldToOffsetMap.put("SAVE", SAVE_STRING_OFFSET);
        fieldToOffsetMap.put("SYSTEM RAM", SYSTEM_RAM_STRING_OFFSET);
        fieldToOffsetMap.put("BACKUP RAM", CARTRIDGE_RAM_STRING_OFFSET);
        fieldToOffsetMap.put("BACKUP RAM NOT FOUND", CARTRIDGE_RAM_STRING_OFFSET_2);
        fieldToOffsetMap.put("NOT INITIALIZED", NOT_INITIALIZED_OFFSET);
        fieldToOffsetMap.put("NO SAVE DATA", NO_SAVE_DATA_OFFSET);
        fieldToOffsetMap.put("AREA", AREA_OFFSET);
        fieldToOffsetMap.put("SELECT A SAVE FILE", SELECT_A_SAVE_FILE_1);
        fieldToOffsetMap.put("SELECT A SAVE FILE 2", SELECT_A_SAVE_FILE_2);
        fieldToOffsetMap.put("CANNOT CANCEL (?)", CANNOT_CANCEL_OFFSET);
        fieldToOffsetMap.put("INSUFFICIENT MEMORY", INSUFFICIENT_MEMORY_OFFSET);
        fieldToOffsetMap.put("THIS DATA CANNOT BE USED", DATA_CANNOT_BE_USED_OFFSET);
        fieldToOffsetMap.put("WRONG DISC NUMBER", WRONG_DISC_NUMBER_OFFSET);
        fieldToOffsetMap.put("DATA IS CORRUPT", CORRUPT_DATA_OFFSET);
        fieldToOffsetMap.put("LOAD?", LOAD_CONFIRM_STRING_OFFSET);
        fieldToOffsetMap.put("LOAD YES", LOAD_YES_STRING_OFFSET);
        fieldToOffsetMap.put("LOAD /", LOAD_SLASH_STRING_OFFSET);
        fieldToOffsetMap.put("LOAD NO", LOAD_NO_STRING_OFFSET);
        fieldToOffsetMap.put("OVERWRITE", OVERWRITE_STRING_OFFSET);
        fieldToOffsetMap.put("OVERWRITE YES", OVERWRITE_YES_STRING_OFFSET);
        fieldToOffsetMap.put("OVERWRITE /", OVERWRITE_SLASH_STRING_OFFSET);
        fieldToOffsetMap.put("OVERWRITE NO", OVERWRITE_NO_STRING_OFFSET);
        fieldToOffsetMap.put("NAMES", NAMES_STRING_OFFSET);
        
        if(isDigitalMuseum) {
            for(String key : fieldToOffsetMap.keySet()) {
                fieldToOffsetMap.put(key, fieldToOffsetMap.get(key) - DIGITAL_MUSEUM_OFFSET);
            }
        }
    }
    
    private void populateFieldToNewValueMap() {
        fieldToNewValueMap = new HashMap<>();
        
        fieldToNewValueMap.put("TIME", TIME_STRING);
        fieldToNewValueMap.put("LOAD", LOAD_STRING);
        fieldToNewValueMap.put("SAVE", SAVE_STRING);
        fieldToNewValueMap.put("SYSTEM RAM", SYSTEM_RAM_STRING);
        fieldToNewValueMap.put("BACKUP RAM", CARTRIDGE_RAM_STRING);
        fieldToNewValueMap.put("BACKUP RAM NOT FOUND", CARTRIDGE_RAM_STRING_2);
        fieldToNewValueMap.put("NOT INITIALIZED", NOT_INITIALIZED);
        fieldToNewValueMap.put("NO SAVE DATA", NO_SAVE_DATA);
        fieldToNewValueMap.put("AREA", AREA);
        fieldToNewValueMap.put("SELECT A SAVE FILE", SELECT_A_SAVE_FILE_1_VALUE);
        fieldToNewValueMap.put("SELECT A SAVE FILE 2", SELECT_A_SAVE_FILE_2_VALUE);
        fieldToNewValueMap.put("CANNOT CANCEL (?)", CANNOT_CANCEL);
        fieldToNewValueMap.put("INSUFFICIENT MEMORY", INSUFFICIENT_MEMORY);
        fieldToNewValueMap.put("THIS DATA CANNOT BE USED", DATA_CANNOT_BE_USED);
        fieldToNewValueMap.put("WRONG DISC NUMBER", WRONG_DISC_NUMBER);
        fieldToNewValueMap.put("DATA IS CORRUPT", CORRUPT_DATA);
        fieldToNewValueMap.put("LOAD?", LOAD_CONFIRM_STRING);
        fieldToNewValueMap.put("LOAD YES", LOAD_YES_STRING);
        fieldToNewValueMap.put("LOAD /", LOAD_SLASH_STRING);
        fieldToNewValueMap.put("LOAD NO", LOAD_NO_STRING);
        fieldToNewValueMap.put("OVERWRITE", OVERWRITE_STRING);
        fieldToNewValueMap.put("OVERWRITE YES", OVERWRITE_YES_STRING);
        fieldToNewValueMap.put("OVERWRITE /", OVERWRITE_SLASH_STRING);
        fieldToNewValueMap.put("OVERWRITE NO", OVERWRITE_NO_STRING);
        fieldToNewValueMap.put("NAMES", NAMES_STRING);
        
    }
    
    /**
     * This map is needed for Auto-Updating. IF the offsets are updated out of order, incorrect values can be put in for HWRAM offsets.
     */
    private void populateOrderToFieldNameMap() {
        orderToFieldNameMap = new HashMap<>();
        
        orderToFieldNameMap.put(0, "TIME");
        orderToFieldNameMap.put(1, "LOAD");
        orderToFieldNameMap.put(2, "SAVE");
        orderToFieldNameMap.put(3, "SYSTEM RAM");
        orderToFieldNameMap.put(4, "BACKUP RAM");
        orderToFieldNameMap.put(5, "BACKUP RAM NOT FOUND");
        orderToFieldNameMap.put(6, "NOT INITIALIZED");
        orderToFieldNameMap.put(7, "NO SAVE DATA");
        orderToFieldNameMap.put(8, "AREA");
        orderToFieldNameMap.put(9, "SELECT A SAVE FILE");
        orderToFieldNameMap.put(10, "SELECT A SAVE FILE 2");
        orderToFieldNameMap.put(11, "CANNOT CANCEL (?)");
        orderToFieldNameMap.put(12, "INSUFFICIENT MEMORY");
        orderToFieldNameMap.put(13, "THIS DATA CANNOT BE USED");
        orderToFieldNameMap.put(14, "WRONG DISC NUMBER");
        orderToFieldNameMap.put(15, "DATA IS CORRUPT");
        orderToFieldNameMap.put(16, "LOAD?");
        orderToFieldNameMap.put(17, "LOAD YES");
        orderToFieldNameMap.put(18, "LOAD /");
        orderToFieldNameMap.put(19, "LOAD NO");
        orderToFieldNameMap.put(20, "OVERWRITE");
        orderToFieldNameMap.put(21, "OVERWRITE YES");
        orderToFieldNameMap.put(22, "OVERWRITE /");
        orderToFieldNameMap.put(23, "OVERWRITE NO");
        orderToFieldNameMap.put(24, "NAMES");
    
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
    
    /**
     * Scans the file for HWRAM offsets that so we can update them.
     */
    private void populateFileOffsetToMemOffsetValuesMap() {
        fileOffsetToMemOffsetValuesMap = new HashMap<>();
        int length = svldBytes.length;
        int i = 0;
        while (i + 4 < length) {
            //Read 4 bytes out of the Array into a temporary buffer.
            ByteBuffer bb = ByteBuffer.wrap(svldBytes, i, 4);
            
            int value = bb.getInt();
            
            String hexString = String.format("%08X", value).toUpperCase();
            if(hexString.startsWith(HEX_MATCH_PATTERN_B) || hexString.startsWith(HEX_MATCH_PATTERN_C)) {
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
                svldBytes = Files.readAllBytes(svld.toPath());
            } catch (IOException e) {
                log.log(Level.SEVERE, "Caught IOException attempting to read bytes.", e);
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Reads the value at the passed in offset.
     * 
     * @param offset
     * @return
     */
    private byte[] readValueFromFileBytes(int offset) {
        byte[] buffer = new byte[1];
        
        boolean endOfValue = false;
        int i = offset;
        int size = 0;
        while(!endOfValue) {
            buffer = Arrays.copyOfRange(svldBytes, i, i + 1);
            i++;
            if(bytesToHex(buffer).equals("00")) {
                size = i - offset;
                endOfValue = true;
            }
        }
        
        byte[] value = Arrays.copyOfRange(svldBytes, offset, offset + size);
        
        return value;
    }
    
    /**
     * Names are stored at one offset but are a delimited list.
     * @param offset
     * @return
     */
    private byte[] readNamesValueFromFileBytes(int offset) {
        byte[] buffer = new byte[1];
        
        boolean endOfValue = false;
        int i = offset;
        int size = 0;
        int numOfNames = 0;
        while(!endOfValue) {
            buffer = Arrays.copyOfRange(svldBytes, i, i + 1);
            i++;
            if(bytesToHex(buffer).equals("00") && numOfNames == NUM_OF_NAMES) {
                size = i - offset;
                endOfValue = true;
            } else if(bytesToHex(buffer).equals("00") && numOfNames < NUM_OF_NAMES) {
                numOfNames++;
            }
        }
        
        byte[] value = Arrays.copyOfRange(svldBytes, offset, offset + size);
        
        return value;
    }
    
    /**
     * Updates the offsets that are after the anchor offset by the delta value.
     * 
     * @param delta
     * @param anchorOffset
     */
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
        TIME_STRING = properties.getProperty("save.TIME_VALUE").replace("\"", "");
        LOAD_STRING = properties.getProperty("save.LOAD_VALUE").replace("\"", "");
        SAVE_STRING = properties.getProperty("save.SAVE_VALUE").replace("\"", "");
        SYSTEM_RAM_STRING = properties.getProperty("save.SYSTEM_RAM_VALUE").replace("\"", "");
        CARTRIDGE_RAM_STRING = properties.getProperty("save.BACKUP_RAM_VALUE").replace("\"", "");
        CARTRIDGE_RAM_STRING_2 = properties.getProperty("save.NO_CART_PRESENT_VALUE").replace("\"", "");
        NOT_INITIALIZED = properties.getProperty("save.NOT_INITIALIZED_VALUE").replace("\"", "");
        NO_SAVE_DATA = properties.getProperty("save.NO_GAME_DATA_VALUE").replace("\"", "");
        AREA = properties.getProperty("save.AREA_VALUE").replace("\"", "");
        SELECT_A_SAVE_FILE_1_VALUE = properties.getProperty("save.SELECT_SAVE_FILE_VALUE").replace("\"", "");
        SELECT_A_SAVE_FILE_2_VALUE = properties.getProperty("save.SELECT_SAVE_FILE_2_VALUE").replace("\"", "");
        CANNOT_CANCEL = properties.getProperty("save.CANNOT_CANCEL_VALUE").replace("\"", "");
        INSUFFICIENT_MEMORY = properties.getProperty("save.NOT_ENOUGH_SPACE_VALUE").replace("\"", "");
        DATA_CANNOT_BE_USED = properties.getProperty("save.FILE_CANT_BE_USED_VALUE").replace("\"", "");
        WRONG_DISC_NUMBER = properties.getProperty("save.WRONG_DISC_VALUE").replace("\"", "");
        CORRUPT_DATA = properties.getProperty("save.DATA_CORRUPT_VALUE").replace("\"", "");
        LOAD_CONFIRM_STRING = properties.getProperty("save.LOAD_FILE_VALUE").replace("\"", "");
        LOAD_YES_STRING = properties.getProperty("save.YES_VALUE").replace("\"", "");
        LOAD_SLASH_STRING = properties.getProperty("save.BACKSLASH").replace("\"", "");
        LOAD_NO_STRING = properties.getProperty("save.NO_VALUE").replace("\"", "");
        OVERWRITE_STRING = properties.getProperty("save.OVERWRITE_VALUE").replace("\"", "");
        OVERWRITE_YES_STRING = properties.getProperty("save.YES_VALUE").replace("\"", "");
        OVERWRITE_SLASH_STRING = properties.getProperty("save.BACKSLASH").replace("\"", "");
        OVERWRITE_NO_STRING = properties.getProperty("save.NO_VALUE").replace("\"", "");
        NAMES_STRING = properties.getProperty("save.NAMES_STRING_VALUE").replace("\"", "");
        
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
