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

public class CMDMNFileEditor {
    private static final Logger log = Logger.getLogger(CMDMNFileEditor.class.getName());
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String HEX_MATCH_PATTERN_B = "060B";
    private static String HEX_MATCH_PATTERN_C = "060C";
    
    private static int MEM_OFFSET_START = 101392384;
    
    //8x16 offsets
    private static int x16_ITEMS_OFFSET = 90164;
    private static int x16_EQUIP_OFFSET = 90169;
    private static int x16_MAGIC_OFFSET = 90174;
    private static int x16_MOVES_OFFSET = 90182;
    private static int x16_STATUS_OFFSET = 90187;
    private static int x16_LIST_OFFSET = 90194;
    private static int x16_MAGIC2_OFFSET = 90199;
    private static int x16_SKILL_OFFSET = 90204;
    private static int x16_USE_OFFSET = 90207;
    private static int x16_GIVE_OFFSET = 90212;
    private static int x16_DISCARD_OFFSET = 90217;
    private static int x16_CHANGE_OFFSET = 90222;
    private static int x16_TRADE_OFFSET = 90228;
    private static int x16_SKILL2_OFFSET = 90258;
    private static int x16_WAS_LEARNED_OFFSET = 90264;
    private static int x16_SKILL_LVL_WENT_UP_OFFSET = 90271;
    private static int x16_LEVEL1_OFFSET = 90283;
    private static int x16_LEVEL2_OFFSET = 90292;
    private static int x16_LEVEL3_OFFSET = 90301;
    private static int x16_SKILL3_OFFSET = 90310;
    
    //8x8 offsets
    //private static int KNIFE_OFFSET = 87820;
    //private static int SWORD_OFFSET = 87828;
    //private static int MACE_OFFSET = 87836;
    //private static int AXE_OFFSET = 87844;
    //private static int WHIP_OFFSET = 87852;
    //private static int THROW_OFFSET = 87860;
    private static int FIRE_OFFSET = 87876;
    private static int WATER_OFFSET = 87884;
    private static int WIND_OFFSET = 87892;
    private static int EARTH_OFFSET = 87900;
    //private static int LOCKER_OFFSET = 87908;
    //private static int VERSION_OFFSET = 87920;
    private static int ATK_OFFSET = 88896;
    private static int DEF_OFFSET = 88904;
    private static int ACT_OFFSET = 88912;
    private static int MOV_OFFSET = 88920;
    private static int STR_OFFSET = 88928;
    private static int VIT_OFFSET = 88936;
    private static int WIT_OFFSET = 88944;
    private static int AGI_OFFSET = 88952;
    private static int WEAPON_OFFSET = 88960;
    private static int SHIELD_OFFSET = 88972;
    private static int ARMOR_OFFSET = 88984;
    private static int HELMET_OFFSET = 88996;
    private static int SHOES_OFFSET = 89008;
    private static int JEWELRY_OFFSET = 89020;
    private static int WPN_SKILL_WINDOW_OFFSET = 89060;
    private static int LEVEL_WINDOW_OFFSET = 89064;
    private static int MAGIC_SKILL_WINDOW_OFFSET = 89068;
    private static int GOLD_PCS_OFFSET = 89164;
    private static int ACQ_OFFSET = 89228;
    private static int SKILL_OFFSET = 89236;
    private static int STR_OFFSET_2 = 89288;
    private static int VIT_OFFSET_2 = 89292;
    private static int WIT_OFFSET_2 = 89300;
    private static int AGI_OFFSET_2 = 89308;
    private static int MAGIC_OFFSET_2 = 89332;
    private static int WEAPON_OFFSET_2 = 89336;
    private static int SKILL_OFFSET_2 = 89340;
    private static int LV_UP_PARAM_OFFSET = 89360;
    
    private static int NAMES_STRING_OFFSET = 91764;
    
    //8x16 offsets
    private static String x16_ITEMS_VALUE;
    private static String x16_EQUIP_VALUE;
    private static String x16_MAGIC_VALUE;
    private static String x16_MOVES_VALUE;
    private static String x16_STATUS_VALUE;
    private static String x16_LIST_VALUE;
    private static String x16_MAGIC2_VALUE;
    private static String x16_SKILL_VALUE;
    private static String x16_USE_VALUE;
    private static String x16_GIVE_VALUE;
    private static String x16_DISCARD_VALUE;
    private static String x16_CHANGE_VALUE;
    private static String x16_TRADE_VALUE;
    private static String x16_SKILL2_VALUE;
    private static String x16_WAS_LEARNED_VALUE;
    private static String x16_SKILL_LVL_WENT_UP_VALUE;
    private static String x16_LEVEL1_VALUE;
    private static String x16_LEVEL2_VALUE;
    private static String x16_LEVEL3_VALUE;
    private static String x16_SKILL3_VALUE;
    
    //8x8 offsets
    //private static String KNIFE_VALUE = "knife";
    //private static String SWORD_VALUE = "sword";
    //private static String MACE_VALUE = "mace";
    //private static String AXE_VALUE = "axe";
    //private static String WHIP_VALUE = "whip:;
    //private static String THROW_VALUE = "throw";
    private static String FIRE_VALUE;
    private static String WATER_VALUE;
    private static String WIND_VALUE;
    private static String EARTH_VALUE;
    //private static String LOCKER_VALUE = "locker";
    //private static String VERSION_VALUE = "version";
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
    private static String WPN_SKILL_WINDOW_VALUE;
    private static String LEVEL_WINDOW_VALUE;
    private static String MAGIC_SKILL_WINDOW_VALUE;
    private static String GOLD_PCS_VALUE;
    private static String ACQ_VALUE;
    private static String SKILL_VALUE;
    private static String STR_VALUE_2;
    private static String VIT_VALUE_2;
    private static String WIT_VALUE_2;
    private static String AGI_VALUE_2;
    private static String MAGIC_VALUE_2;
    private static String WEAPON_VALUE_2;
    private static String SKILL_VALUE_2;
    private static String LV_UP_PARAM_VALUE;
    
    private static String NAMES_STRING_VALUE;
    
   
    private static int NUM_OF_NAMES = 8;
    private static byte[] cmdmnBytes;
    
    private String inputFilePath;
    private String outputFilePath;
    private Map<String, Integer> fieldToOffsetMap;
    private Map<Integer, String> orderToFieldNameMap;
    private Map<String, String> fieldToNewValueMap;
    private Map<String, byte[]> fieldToValueMap;
    private Map<Integer, Integer> fileOffsetToMemOffsetValuesMap;
    private static Properties properties;
    
    public CMDMNFileEditor inputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
        return this;
    }
    
    public CMDMNFileEditor outputFilePath(String outputFilePath) {
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
        if(newString == null  || newString.isEmpty()) {
            hexValue = "00";
        } else if(newString.equals("X2_NULL")){
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
        
        if(key.equals("NAMES")) {
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
        FileUtils.writeToFile(cmdmnBytes, "CMDMN", ".BIN", ".BIN", outputFilePath);
    }
    
    private void updateCmdmnByteArray(int offset, byte[] oldValue, byte[] newValue) {
        byte[] cmdmnBytesA = Arrays.copyOfRange(cmdmnBytes, 0, offset);
        byte[] cmdmnBytesB = Arrays.copyOfRange(cmdmnBytes, offset + oldValue.length, cmdmnBytes.length);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(cmdmnBytesA);
            baos.write(newValue);
            baos.write(cmdmnBytesB);
            
            cmdmnBytes = baos.toByteArray();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Caught IOException attempting to write bytes.", e);
            e.printStackTrace();
        }
    }
    
    private void populateFieldToOffsetMap() {
        fieldToOffsetMap = new HashMap<>();
        
        //8x16 offsets
        fieldToOffsetMap.put("8x16 ITEMS", x16_ITEMS_OFFSET);
        fieldToOffsetMap.put("8x16 EQUIP", x16_EQUIP_OFFSET);
        fieldToOffsetMap.put("8x16 MAGIC", x16_MAGIC_OFFSET);
        fieldToOffsetMap.put("8x16 MOVES", x16_MOVES_OFFSET);
        fieldToOffsetMap.put("8x16 STATUS", x16_STATUS_OFFSET);
        fieldToOffsetMap.put("8x16 LIST", x16_LIST_OFFSET);
        fieldToOffsetMap.put("8x16 MAGIC2", x16_MAGIC2_OFFSET);
        fieldToOffsetMap.put("8x16 SKILL", x16_SKILL_OFFSET);
        fieldToOffsetMap.put("8x16 USE", x16_USE_OFFSET);
        fieldToOffsetMap.put("8x16 GIVE", x16_GIVE_OFFSET);
        fieldToOffsetMap.put("8x16 DISCARD", x16_DISCARD_OFFSET);
        fieldToOffsetMap.put("8x16 CHANGE", x16_CHANGE_OFFSET);
        fieldToOffsetMap.put("8x16 TRADE", x16_TRADE_OFFSET);
        fieldToOffsetMap.put("8x16 SKILL2", x16_SKILL2_OFFSET);
        fieldToOffsetMap.put("8x16 WAS LEARNED", x16_WAS_LEARNED_OFFSET);
        fieldToOffsetMap.put("8x16 SKILL LVL WENT UP", x16_SKILL_LVL_WENT_UP_OFFSET);
        fieldToOffsetMap.put("8x16 LEVEL1", x16_LEVEL1_OFFSET);
        fieldToOffsetMap.put("8x16 LEVEL2", x16_LEVEL2_OFFSET);
        fieldToOffsetMap.put("8x16 LEVEL3", x16_LEVEL3_OFFSET);
        fieldToOffsetMap.put("8x16 SKILL3", x16_SKILL3_OFFSET);
        
        //8x8 offsets
//        fieldToOffsetMap.put("KNIFE", KNIFE_OFFSET);
//        fieldToOffsetMap.put("SWORD", SWORD_OFFSET);
//        fieldToOffsetMap.put("MACE", MACE_OFFSET);
//        fieldToOffsetMap.put("AXE", AXE_OFFSET);
//        fieldToOffsetMap.put("WHIP", WHIP_OFFSET);
//        fieldToOffsetMap.put("THROW", THROW_OFFSET);
        fieldToOffsetMap.put("FIRE", FIRE_OFFSET);
        fieldToOffsetMap.put("WATER", WATER_OFFSET);
        fieldToOffsetMap.put("WIND", WIND_OFFSET);
        fieldToOffsetMap.put("EARTH", EARTH_OFFSET);
//        fieldToOffsetMap.put("LOCKER?", LOCKER_OFFSET);
//        fieldToOffsetMap.put("VERSION?", VERSION_OFFSET);
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
        fieldToOffsetMap.put("WEAPON SKILL WINDOW", WPN_SKILL_WINDOW_OFFSET);
        fieldToOffsetMap.put("LEVEL WINDOW", LEVEL_WINDOW_OFFSET);
        fieldToOffsetMap.put("MAGIC SKILL WINDOW", MAGIC_SKILL_WINDOW_OFFSET);
        fieldToOffsetMap.put("GOLD PCS", GOLD_PCS_OFFSET);
        fieldToOffsetMap.put("ACQ", ACQ_OFFSET);
        fieldToOffsetMap.put("SKILL", SKILL_OFFSET);
        fieldToOffsetMap.put("STR2", STR_OFFSET_2);
        fieldToOffsetMap.put("VIT2", VIT_OFFSET_2);
        fieldToOffsetMap.put("WIT2", WIT_OFFSET_2);
        fieldToOffsetMap.put("AGI2", AGI_OFFSET_2 );
        fieldToOffsetMap.put("MAGIC2", MAGIC_OFFSET_2 );
        fieldToOffsetMap.put("WEAPON2", WEAPON_OFFSET_2 );
        fieldToOffsetMap.put("SKILL2", SKILL_OFFSET_2 );
        fieldToOffsetMap.put("LV UP PARAM", LV_UP_PARAM_OFFSET  );
        fieldToOffsetMap.put("NAMES", NAMES_STRING_OFFSET  );
    }
    
    /**
     * This map is needed for Auto-Updating. IF the offsets are updated out of order, incorrect values can be put in for HWRAM offsets.
     */
    private void populateOrderToFieldNameMap() {
        orderToFieldNameMap = new HashMap<>();
        
       
        
        orderToFieldNameMap.put(0, "FIRE");
        orderToFieldNameMap.put(1, "WATER");
        orderToFieldNameMap.put(2, "WIND");
        orderToFieldNameMap.put(3, "EARTH");
        orderToFieldNameMap.put(4, "ATK");
        orderToFieldNameMap.put(5, "DEF");
        orderToFieldNameMap.put(6, "ACT");
        orderToFieldNameMap.put(7, "MOV");
        orderToFieldNameMap.put(8, "STR");
        orderToFieldNameMap.put(9, "VIT");
        orderToFieldNameMap.put(10, "WIT");
        orderToFieldNameMap.put(11, "AGI");
        orderToFieldNameMap.put(12, "WEAPON");
        orderToFieldNameMap.put(13, "SHIELD");
        orderToFieldNameMap.put(14, "ARMOR");
        orderToFieldNameMap.put(15, "HELMET");
        orderToFieldNameMap.put(16, "SHOES");
        orderToFieldNameMap.put(17, "JEWELRY");
        orderToFieldNameMap.put(18, "WEAPON SKILL WINDOW");
        orderToFieldNameMap.put(19, "LEVEL WINDOW");
        orderToFieldNameMap.put(20, "MAGIC SKILL WINDOW");
        orderToFieldNameMap.put(21, "GOLD PCS");
        orderToFieldNameMap.put(22, "ACQ");
        orderToFieldNameMap.put(23, "SKILL");
        orderToFieldNameMap.put(24, "STR2");
        orderToFieldNameMap.put(25, "VIT2");
        orderToFieldNameMap.put(26, "WIT2");
        orderToFieldNameMap.put(27, "AGI2");
        orderToFieldNameMap.put(28, "MAGIC2");
        orderToFieldNameMap.put(29, "WEAPON2");
        orderToFieldNameMap.put(30, "SKILL2");
        orderToFieldNameMap.put(31, "LV UP PARAM");
        
        orderToFieldNameMap.put(32, "8x16 ITEMS");
        orderToFieldNameMap.put(33, "8x16 EQUIP");
        orderToFieldNameMap.put(34, "8x16 MAGIC");
        orderToFieldNameMap.put(35, "8x16 MOVES");
        orderToFieldNameMap.put(36, "8x16 STATUS");
        orderToFieldNameMap.put(37, "8x16 LIST");
        orderToFieldNameMap.put(38, "8x16 MAGIC2");
        orderToFieldNameMap.put(39, "8x16 SKILL");
        orderToFieldNameMap.put(40, "8x16 USE");
        orderToFieldNameMap.put(41, "8x16 GIVE");
        orderToFieldNameMap.put(42, "8x16 DISCARD");
        orderToFieldNameMap.put(43, "8x16 CHANGE");
        orderToFieldNameMap.put(44, "8x16 TRADE");
        orderToFieldNameMap.put(45, "8x16 SKILL2");
        orderToFieldNameMap.put(46, "8x16 WAS LEARNED");
        orderToFieldNameMap.put(47, "8x16 SKILL LVL WENT UP");
        orderToFieldNameMap.put(48, "8x16 LEVEL1");
        orderToFieldNameMap.put(49, "8x16 LEVEL2");
        orderToFieldNameMap.put(50, "8x16 LEVEL3");
        orderToFieldNameMap.put(51, "8x16 SKILL3");
        
        orderToFieldNameMap.put(52, "NAMES");
        
    }
    
    private void populateFieldToNewValueMap() {
        fieldToNewValueMap = new HashMap<>();
        
        //8x16 offsets
        fieldToNewValueMap.put("8x16 ITEMS", x16_ITEMS_VALUE);
        fieldToNewValueMap.put("8x16 EQUIP", x16_EQUIP_VALUE);
        fieldToNewValueMap.put("8x16 MAGIC", x16_MAGIC_VALUE);
        fieldToNewValueMap.put("8x16 MOVES", x16_MOVES_VALUE);
        fieldToNewValueMap.put("8x16 STATUS", x16_STATUS_VALUE);
        fieldToNewValueMap.put("8x16 LIST", x16_LIST_VALUE);
        fieldToNewValueMap.put("8x16 MAGIC2", x16_MAGIC2_VALUE);
        fieldToNewValueMap.put("8x16 SKILL", x16_SKILL_VALUE);
        fieldToNewValueMap.put("8x16 USE", x16_USE_VALUE);
        fieldToNewValueMap.put("8x16 GIVE", x16_GIVE_VALUE);
        fieldToNewValueMap.put("8x16 DISCARD", x16_DISCARD_VALUE);
        fieldToNewValueMap.put("8x16 CHANGE", x16_CHANGE_VALUE);
        fieldToNewValueMap.put("8x16 TRADE", x16_TRADE_VALUE);
        fieldToNewValueMap.put("8x16 SKILL2", x16_SKILL2_VALUE);
        fieldToNewValueMap.put("8x16 WAS LEARNED", x16_WAS_LEARNED_VALUE);
        fieldToNewValueMap.put("8x16 SKILL LVL WENT UP", x16_SKILL_LVL_WENT_UP_VALUE);
        fieldToNewValueMap.put("8x16 LEVEL1", x16_LEVEL1_VALUE);
        fieldToNewValueMap.put("8x16 LEVEL2", x16_LEVEL2_VALUE);
        fieldToNewValueMap.put("8x16 LEVEL3", x16_LEVEL3_VALUE);
        fieldToNewValueMap.put("8x16 SKILL3", x16_SKILL3_VALUE);
        
        //8x8 offsets
//        fieldToNewValueMap.put("KNIFE", KNIFE_VALUE);
//        fieldToNewValueMap.put("SWORD", SWORD_VALUE);
//        fieldToNewValueMap.put("MACE", MACE_VALUE);
//        fieldToNewValueMap.put("AXE", AXE_VALUE);
//        fieldToNewValueMap.put("WHIP", WHIP_VALUE);
//        fieldToNewValueMap.put("THROW", THROW_VALUE);
        fieldToNewValueMap.put("FIRE", FIRE_VALUE);
        fieldToNewValueMap.put("WATER", WATER_VALUE);
        fieldToNewValueMap.put("WIND", WIND_VALUE);
        fieldToNewValueMap.put("EARTH", EARTH_VALUE);
//        fieldToNewValueMap.put("LOCKER?", LOCKER_VALUE);
//        fieldToNewValueMap.put("VERSION?", VERSION_VALUE);
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
        fieldToNewValueMap.put("WEAPON SKILL WINDOW", WPN_SKILL_WINDOW_VALUE);
        fieldToNewValueMap.put("LEVEL WINDOW", LEVEL_WINDOW_VALUE);
        fieldToNewValueMap.put("MAGIC SKILL WINDOW", MAGIC_SKILL_WINDOW_VALUE);
        fieldToNewValueMap.put("GOLD PCS", GOLD_PCS_VALUE);
        fieldToNewValueMap.put("ACQ", ACQ_VALUE);
        fieldToNewValueMap.put("SKILL", SKILL_VALUE);
        fieldToNewValueMap.put("STR2", STR_VALUE_2);
        fieldToNewValueMap.put("VIT2", VIT_VALUE_2);
        fieldToNewValueMap.put("WIT2", WIT_VALUE_2);
        fieldToNewValueMap.put("AGI2", AGI_VALUE_2 );
        fieldToNewValueMap.put("MAGIC2", MAGIC_VALUE_2 );
        fieldToNewValueMap.put("WEAPON2", WEAPON_VALUE_2 );
        fieldToNewValueMap.put("SKILL2", SKILL_VALUE_2 );
        fieldToNewValueMap.put("LV UP PARAM", LV_UP_PARAM_VALUE  );
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
        int length = cmdmnBytes.length;
        int i = 0;
        while (i + 4 < length) {
            //Read 4 bytes out of the Array into a temporary buffer.
            ByteBuffer bb = ByteBuffer.wrap(cmdmnBytes, i, 4);
            
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
            File cmdmn = new File(inputFilePath);
            try {
                cmdmnBytes = Files.readAllBytes(cmdmn.toPath());
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
            buffer = Arrays.copyOfRange(cmdmnBytes, i, i + 1);
            i++;
            if(bytesToHex(buffer).equals("00")) {
                size = i - offset;
                endOfValue = true;
            }
        }
        
        byte[] value = Arrays.copyOfRange(cmdmnBytes, offset, offset + size);
        
        return value;
    }
    
    private byte[] readNamesValueFromFileBytes(int offset) {
        byte[] buffer = new byte[1];
        
        boolean endOfValue = false;
        int i = offset;
        int size = 0;
        int numOfNames = 0;
        while(!endOfValue) {
            buffer = Arrays.copyOfRange(cmdmnBytes, i, i + 1);
            i++;
            if(bytesToHex(buffer).equals("00") && numOfNames == NUM_OF_NAMES) {
                size = i - offset;
                endOfValue = true;
            } else if(bytesToHex(buffer).equals("00") && numOfNames < NUM_OF_NAMES) {
                numOfNames++;
            }
        }
        
        byte[] value = Arrays.copyOfRange(cmdmnBytes, offset, offset + size);
        
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
        //8x16 offsets
        x16_ITEMS_VALUE = properties.getProperty("cmdmn.8x16_ITEMS_VALUE").replace("\"", "");
        x16_EQUIP_VALUE = properties.getProperty("cmdmn.8x16_EQUIP_VALUE").replace("\"", "");
        x16_MAGIC_VALUE = properties.getProperty("cmdmn.8x16_MAGIC_VALUE").replace("\"", "");
        x16_MOVES_VALUE = properties.getProperty("cmdmn.8x16_MOVES_VALUE").replace("\"", "");
        x16_STATUS_VALUE = properties.getProperty("cmdmn.8x16_STATUS_VALUE").replace("\"", "");
        x16_LIST_VALUE = properties.getProperty("cmdmn.8x16_LIST_VALUE").replace("\"", "");
        x16_MAGIC2_VALUE = properties.getProperty("cmdmn.8x16_MAGIC2_VALUE").replace("\"", "");
        x16_SKILL_VALUE = properties.getProperty("cmdmn.8x16_SKILL_VALUE").replace("\"", "");
        x16_USE_VALUE = properties.getProperty("cmdmn.8x16_USE_VALUE").replace("\"", "");
        x16_GIVE_VALUE = properties.getProperty("cmdmn.8x16_GIVE_VALUE").replace("\"", "");
        x16_DISCARD_VALUE = properties.getProperty("cmdmn.8x16_DISCARD_VALUE").replace("\"", "");
        x16_CHANGE_VALUE = properties.getProperty("cmdmn.8x16_CHANGE_VALUE").replace("\"", "");
        x16_TRADE_VALUE = properties.getProperty("cmdmn.8x16_TRADE_VALUE").replace("\"", "");
        x16_SKILL2_VALUE = properties.getProperty("cmdmn.8x16_SKILL2_VALUE").replace("\"", "");
        x16_WAS_LEARNED_VALUE = properties.getProperty("cmdmn.8x16_WAS_LEARNED_VALUE").replace("\"", "");
        x16_SKILL_LVL_WENT_UP_VALUE = properties.getProperty("cmdmn.8x16_SKILL_LVL_WENT_UP_VALUE").replace("\"", "");
        x16_LEVEL1_VALUE = properties.getProperty("cmdmn.8x16_LEVEL1_VALUE").replace("\"", "");
        x16_LEVEL2_VALUE = properties.getProperty("cmdmn.8x16_LEVEL2_VALUE").replace("\"", "");
        x16_LEVEL3_VALUE = properties.getProperty("cmdmn.8x16_LEVEL3_VALUE").replace("\"", "");
        x16_SKILL3_VALUE = properties.getProperty("cmdmn.8x16_SKILL3_VALUE").replace("\"", "");
        
        FIRE_VALUE = properties.getProperty("cmdmn.FIRE_VALUE").replace("\"", "");
        WATER_VALUE = properties.getProperty("cmdmn.WATER_VALUE").replace("\"", "");
        WIND_VALUE = properties.getProperty("cmdmn.WIND_VALUE").replace("\"", "");
        EARTH_VALUE = properties.getProperty("cmdmn.EARTH_VALUE").replace("\"", "");
        ATK_VALUE = properties.getProperty("cmdmn.ATK_VALUE").replace("\"", "");
        DEF_VALUE = properties.getProperty("cmdmn.DEF_VALUE").replace("\"", "");
        ACT_VALUE = properties.getProperty("cmdmn.ACT_VALUE").replace("\"", "");
        MOV_VALUE = properties.getProperty("cmdmn.MOV_VALUE").replace("\"", "");
        STR_VALUE = properties.getProperty("cmdmn.STR_VALUE").replace("\"", "");
        VIT_VALUE = properties.getProperty("cmdmn.VIT_VALUE").replace("\"", "");
        WIT_VALUE = properties.getProperty("cmdmn.WIT_VALUE").replace("\"", "");
        AGI_VALUE = properties.getProperty("cmdmn.AGI_VALUE").replace("\"", "");
        WEAPON_VALUE = properties.getProperty("cmdmn.WEAPON_VALUE").replace("\"", "");
        SHIELD_VALUE = properties.getProperty("cmdmn.SHIELD_VALUE").replace("\"", "");
        ARMOR_VALUE = properties.getProperty("cmdmn.ARMOR_VALUE").replace("\"", "");
        HELMET_VALUE = properties.getProperty("cmdmn.HELMET_VALUE").replace("\"", "");
        SHOES_VALUE = properties.getProperty("cmdmn.SHOES_VALUE").replace("\"", "");
        JEWELRY_VALUE = properties.getProperty("cmdmn.JEWELRY_VALUE").replace("\"", "");
        WPN_SKILL_WINDOW_VALUE = properties.getProperty("cmdmn.WPN_SKILL_WINDOW_VALUE").replace("\"", "");
        LEVEL_WINDOW_VALUE = properties.getProperty("cmdmn.LEVEL_WINDOW_VALUE").replace("\"", "");
        MAGIC_SKILL_WINDOW_VALUE = properties.getProperty("cmdmn.MAGIC_SKILL_WINDOW_VALUE").replace("\"", "");
        GOLD_PCS_VALUE = properties.getProperty("cmdmn.GOLD_PCS_VALUE").replace("\"", "");
        ACQ_VALUE = properties.getProperty("cmdmn.ACQ_VALUE").replace("\"", "");
        SKILL_VALUE = properties.getProperty("cmdmn.SKILL_VALUE").replace("\"", "");
        STR_VALUE_2 = properties.getProperty("cmdmn.STR_VALUE_2").replace("\"", "");
        VIT_VALUE_2 = properties.getProperty("cmdmn.VIT_VALUE_2").replace("\"", "");
        WIT_VALUE_2 = properties.getProperty("cmdmn.WIT_VALUE_2").replace("\"", "");
        AGI_VALUE_2 = properties.getProperty("cmdmn.AGI_VALUE_2").replace("\"", "");
        MAGIC_VALUE_2 = properties.getProperty("cmdmn.MAGIC_VALUE_2").replace("\"", "");
        WEAPON_VALUE_2 = properties.getProperty("cmdmn.WEAPON_VALUE_2").replace("\"", "");
        SKILL_VALUE_2 = properties.getProperty("cmdmn.SKILL_VALUE_2").replace("\"", "");
        LV_UP_PARAM_VALUE = properties.getProperty("cmdmn.LV_UP_PARAM_VALUE").replace("\"", "");
        
        NAMES_STRING_VALUE = properties.getProperty("cmdmn.NAMES_STRING_VALUE").replace("\"", "");
    
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
