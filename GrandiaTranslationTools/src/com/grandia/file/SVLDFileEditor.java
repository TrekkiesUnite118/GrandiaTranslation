package com.grandia.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

import com.grandia.file.utils.FileUtils;

/**
 * SVLDFileEditor
 * 
 * This utility will replace text values in the SVLD.BIN file with new values.
 * It will then updates all the HWRAM offsets in the file accordingly.
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

    
    private static int NUM_OF_NAMES = 8;
    private static byte[] svldBytes;
    
    private String inputFilePath;
    private String outputFilePath;
    private Map<String, Integer> fieldToOffsetMap;
    private Map<String, byte[]> fieldToValueMap;
    private Map<Integer, Integer> fileOffsetToMemOffsetValuesMap;
    
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
    
    public void init() {
        parse();
        populateFieldToOffsetMap();
        populateFieldToValueMap();
        populateFileOffsetToMemOffsetValuesMap();
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
        
        //Determin the anchor point and call update Offsets if the size changed.
        if(delta != 0) {
            int anchorOffset = fieldToOffsetMap.get(key) + MEM_OFFSET_START;
            updateOffsets(delta, anchorOffset);
        }
        
        //Update the value in the byte array.
        updateSvldByteArray(fieldToOffsetMap.get(key), oldValue, newValue);
        
        //Update the offset values in the byte array.
        for(Integer fileOffset : fileOffsetToMemOffsetValuesMap.keySet()) {
            int memOffset = fileOffsetToMemOffsetValuesMap.get(fileOffset);
            String hexMemOffset = String.format("%08X", memOffset).toUpperCase();
            
            byte[] memOffsetByte = DatatypeConverter.parseHexBinary(hexMemOffset);
            
            updateSvldByteArray(fileOffset, memOffsetByte, memOffsetByte);
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
