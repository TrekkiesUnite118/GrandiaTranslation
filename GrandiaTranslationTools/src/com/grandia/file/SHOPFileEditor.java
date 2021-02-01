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
 * SHOPFileEditor
 * 
 * This utility will replace text values in the SHOP.BIN file with new values.
 * It will then update all the HWRAM offsets in the file accordingly.
 * 
 * @author TrekkiesUnite118
 *
 */
public class SHOPFileEditor {
    private static final Logger log = Logger.getLogger(SHOPFileEditor.class.getName());
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    //HWRAM Prefixes
    private static String HEX_MATCH_PATTERN_B = "060B";
    private static String HEX_MATCH_PATTERN_C = "060C";
    
    private static int MEM_OFFSET_START = 101392384;
    
    //8x16 offsets
    private static int x16_BUY_OFFSET = 58896;
    private static int x16_BUY_CANCEL_OFFSET = 58901;
    private static int x16_SELL_OFFSET = 58905;
    private static int x16_SELL_CANCEL_OFFSET = 58910;
    private static int x16_STASH_OFFSET = 58914;
    private static int x16_STASH_CANCEL_OFFSET = 58919;
    private static int x16_GET_OFFSET = 58923;
    private static int x16_GET_CANCEL_OFFSET = 58930;
    private static int x16_EQUIP_OFFSET = 58935;
    private static int x16_TRADE_OFFSET = 58941;
    private static int x16_BUY2_OFFSET = 58947;
    private static int x16_WILL_TRADE_OFFSET = 58952;
    private static int x16_SPACE_OFFSET = 58962;
    private static int x16_CANNOT_TRADE_OFFSET = 58965;
    private static int x16_WELCOME_OFFSET = 58976;
    private static int x16_PICK_AN_ITEM_TO_BUY_OFFSET = 58985;
    private static int x16_WHO_WILL_BUY_OFFSET = 58995;
    private static int x16_PICK_AN_ITEM_TO_SELL_OFFSET = 59005;
    private static int x16_BUY_WHICH_ATTRIBUTE_OFFSET = 59015;
    private static int x16_PICK_AN_ITEM_TO_STASH_OFFSET = 59029;
    private static int x16_PICK_AN_ITEM_TO_GET_OFFSET = 59043;
    private static int x16_WHO_IS_BUYING_OFFSET = 59060;
    private static int x16_YOU_HAVE_NO_MANA_EGGS_OFFSET = 59072;
    private static int x16_YOU_HAVE_NO_ITEMS_TO_GET_OFFSET = 59086;
    private static int x16_WAS_LEARNED_OFFSET = 59102;
    
    //8x8 offsets
    private static int TRADE_VALUE_OFFSET = 57700;
    private static int MANA_EGGS_OFFSET = 57720;
    private static int EGGS_OFFSET = 57732;
    private static int ATK_OFFSET = 57740;
    private static int DEF_OFFSET = 57748;
    private static int ACT_OFFSET = 57756;
    private static int MOV_OFFSET = 57764;
    private static int STR_OFFSET = 57772;
    private static int VIT_OFFSET = 57780;
    private static int WIT_OFFSET = 57788;
    private static int AGI_OFFSET = 57796;
    private static int WEAPON_OFFSET = 57804;
    private static int SHIELD_OFFSET = 57816;
    private static int ARMOR_OFFSET = 57828;
    private static int HELMET_OFFSET = 57840;
    private static int SHOES_OFFSET = 57852;
    private static int JEWELRY_OFFSET = 57864;;
    private static int GOLD_PCS_OFFSET = 57952;
    private static int STR_OFFSET_2 = 58000;
    private static int WIT_OFFSET_2 = 58020;
    private static int AGI_OFFSET_2 = 58028;
    private static int MAGIC_OFFSET = 58040;
    private static int LV_UP_PARAM_OFFSET = 58052;
    private static int MIX_ATTRIBUTE_OFFSET = 58068;
    private static int THUNDER_OFFSET = 58088;
    private static int SNOW_OFFSET = 58096;
    private static int FOREST_OFFSET = 58104;
    private static int EXPLOSION_OFFSET = 58112;
    
    private static int NAMES_STRING_OFFSET = 60360;
    
    //8x16 values
    private static String x16_BUY_VALUE;
    private static String x16_BUY_CANCEL_VALUE;
    private static String x16_SELL_VALUE;
    private static String x16_SELL_CANCEL_VALUE;
    private static String x16_STASH_VALUE;
    private static String x16_STASH_CANCEL_VALUE;
    private static String x16_GET_VALUE;
    private static String x16_GET_CANCEL_VALUE;
    private static String x16_EQUIP_VALUE;
    private static String x16_TRADE_VALUE;
    private static String x16_BUY2_VALUE;
    private static String x16_WILL_TRADE_VALUE;
    private static String x16_SPACE_VALUE;
    private static String x16_CANNOT_TRADE_VALUE;
    private static String x16_WELCOME_VALUE;
    private static String x16_PICK_AN_ITEM_TO_BUY_VALUE;
    private static String x16_WHO_WILL_BUY_VALUE;
    private static String x16_PICK_AN_ITEM_TO_SELL_VALUE;
    private static String x16_BUY_WHICH_ATTRIBUTE_VALUE;
    private static String x16_PICK_AN_ITEM_TO_STASH_VALUE;
    private static String x16_PICK_AN_ITEM_TO_GET_VALUE;
    private static String x16_WHO_IS_BUYING_VALUE;
    private static String x16_YOU_HAVE_NO_MANA_EGGS_VALUE;
    private static String x16_YOU_HAVE_NO_ITEMS_TO_GET_VALUE;
    private static String x16_WAS_LEARNED_VALUE;
    
    //8x8 values
    private static String TRADE_VALUE_VALUE;
    private static String MANA_EGGS_VALUE;
    private static String EGGS_VALUE;
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
    private static String GOLD_PCS_VALUE;
    private static String STR_VALUE_2;
    private static String WIT_VALUE_2;
    private static String AGI_VALUE_2;
    private static String MAGIC_VALUE;
    private static String LV_UP_PARAM_VALUE;
    private static String MIX_ATTRIBUTE_VALUE;
    private static String THUNDER_VALUE;
    private static String SNOW_VALUE;
    private static String FOREST_VALUE;
    private static String EXPLOSION_VALUE;
    
    private static String NAMES_STRING_VALUE;
    private static int NUM_OF_NAMES = 8;
    private static byte[] shopBytes;
    
    private String inputFilePath;
    private String outputFilePath;
    private Map<String, Integer> fieldToOffsetMap;
    private Map<Integer, String> orderToFieldNameMap;
    private Map<String, String> fieldToNewValueMap;
    private Map<String, byte[]> fieldToValueMap;
    private Map<Integer, Integer> fileOffsetToMemOffsetValuesMap;
    private static Properties properties;
    
    public SHOPFileEditor inputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
        return this;
    }
    
    public SHOPFileEditor outputFilePath(String outputFilePath) {
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
     * @param key
     * @param newString
     */
    public void replaceFieldValue(String key, String newString) {
        
        String hexValue;
        
        //If the new value is null just put in 00.
        if(newString == null) {
            hexValue = "00";
        } else if(newString == "X2_NULL"){
          //If X2_null, put in 0000
            hexValue = "0000";
        }
        else {
            hexValue = bytesToHex(newString.getBytes());
        }
        
        String hexString = new String();
        //If 8x16 append 0x03
        if(key.contains("8x16")) {
            hexString = "03" + hexValue + "00";
        } else{
            hexString = hexValue + "00";
        }
        
        //If names replace spaces with 00 delimiter
        if(key == "NAMES") {
            hexString = hexString.replace("20", "00");
        }
        
        byte[] newValue = DatatypeConverter.parseHexBinary(hexString);
        byte[] oldValue = fieldToValueMap.get(key);
      
        int delta = newValue.length - oldValue.length;
        
        //Determine the anchor point and update the offsets.
        int anchorOffset = fieldToOffsetMap.get(key) + MEM_OFFSET_START;
        if(delta != 0) {
            updateOffsets(delta, anchorOffset);
        }
        
        //Update byte array with new value.
        updateShopByteArray(fieldToOffsetMap.get(key), oldValue, newValue);
        List<Integer> offsetsToRemove = new ArrayList<>();
        
        //Update byte array with new offset values.
        for(Integer fileOffset : fileOffsetToMemOffsetValuesMap.keySet()) {
            int memOffset = fileOffsetToMemOffsetValuesMap.get(fileOffset);
            String hexMemOffset = String.format("%08X", memOffset).toUpperCase();
            
            byte[] memOffsetByte = DatatypeConverter.parseHexBinary(hexMemOffset);
            
            updateShopByteArray(fileOffset, memOffsetByte, memOffsetByte);
                        
            if(memOffset < anchorOffset) {
                offsetsToRemove.add(fileOffset);
            }
        }
        
        for(int remove : offsetsToRemove) {
            fileOffsetToMemOffsetValuesMap.remove(remove);
        }
        
        //Update fieldToValueMap.
        fieldToValueMap.put(key, newValue);
    }
    
    /**
     * Writes to file.
     */
    public void writeToFile() {
        FileUtils.writeToFile(shopBytes, "SHOP", ".BIN", ".BIN", outputFilePath);
    }
    
    /**
     * Injects the new value into the byte array.
     * @param offset
     * @param oldValue
     * @param newValue
     */
    private void updateShopByteArray(int offset, byte[] oldValue, byte[] newValue) {
        byte[] shopBytesA = Arrays.copyOfRange(shopBytes, 0, offset);
        byte[] shopBytesB = Arrays.copyOfRange(shopBytes, offset + oldValue.length, shopBytes.length);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(shopBytesA);
            baos.write(newValue);
            baos.write(shopBytesB);
            
            shopBytes = baos.toByteArray();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Caught IOException attempting to write bytes.", e);
            e.printStackTrace();
        }
    }
    
    private void populateFieldToOffsetMap() {
        fieldToOffsetMap = new HashMap<>();
        
        //8x16 offsets
        fieldToOffsetMap.put("8x16 BUY", x16_BUY_OFFSET);
        fieldToOffsetMap.put("8x16 BUY CANCEL", x16_BUY_CANCEL_OFFSET);
        fieldToOffsetMap.put("8x16 SELL", x16_SELL_OFFSET);
        fieldToOffsetMap.put("8x16 SELL CANCEL", x16_SELL_CANCEL_OFFSET);
        fieldToOffsetMap.put("8x16 STASH", x16_STASH_OFFSET);
        fieldToOffsetMap.put("8x16 STASH CANCEL", x16_STASH_CANCEL_OFFSET);
        fieldToOffsetMap.put("8x16 GET", x16_GET_OFFSET);
        fieldToOffsetMap.put("8x16 GET CANCEL", x16_GET_CANCEL_OFFSET);
        fieldToOffsetMap.put("8x16 EQUIP", x16_EQUIP_OFFSET);
        fieldToOffsetMap.put("8x16 TRADE", x16_TRADE_OFFSET);
        fieldToOffsetMap.put("8x16 BUY2", x16_BUY2_OFFSET);
        fieldToOffsetMap.put("8x16 WILL TRADE", x16_WILL_TRADE_OFFSET);
        fieldToOffsetMap.put("8x16 SPACE", x16_SPACE_OFFSET);
        fieldToOffsetMap.put("8x16 CANNOT_TRADE", x16_CANNOT_TRADE_OFFSET);
        fieldToOffsetMap.put("8x16 WELCOME", x16_WELCOME_OFFSET);
        fieldToOffsetMap.put("8x16 PICK AN ITEM TO BUY", x16_PICK_AN_ITEM_TO_BUY_OFFSET);
        fieldToOffsetMap.put("8x16 WHO WILL BUY", x16_WHO_WILL_BUY_OFFSET);
        fieldToOffsetMap.put("8x16 PICK AN ITEM TO SELL", x16_PICK_AN_ITEM_TO_SELL_OFFSET);
        fieldToOffsetMap.put("8x16 BUY WHICH ATTRIBUTE", x16_BUY_WHICH_ATTRIBUTE_OFFSET);
        fieldToOffsetMap.put("8x16 PICK AN ITEM TO STASH", x16_PICK_AN_ITEM_TO_STASH_OFFSET);
        fieldToOffsetMap.put("8x16 PICK AN ITEM TO GET", x16_PICK_AN_ITEM_TO_GET_OFFSET);
        fieldToOffsetMap.put("8x16 WHO IS BUYING", x16_WHO_IS_BUYING_OFFSET);
        fieldToOffsetMap.put("8x16 YOU HAVE NO MANA EGGS", x16_YOU_HAVE_NO_MANA_EGGS_OFFSET);
        fieldToOffsetMap.put("8x16 YOU HAVE NO ITEMS TO GET", x16_YOU_HAVE_NO_ITEMS_TO_GET_OFFSET);
        fieldToOffsetMap.put("8x16 WAS LEARNED", x16_WAS_LEARNED_OFFSET);
        
        
        //8x8 offsets
        fieldToOffsetMap.put("TRADE_VALUE", TRADE_VALUE_OFFSET);
        fieldToOffsetMap.put("MANA EGGS", MANA_EGGS_OFFSET);
        fieldToOffsetMap.put("EGGS", EGGS_OFFSET);
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
        fieldToOffsetMap.put("GOLD PCS", GOLD_PCS_OFFSET);
        fieldToOffsetMap.put("STR2", STR_OFFSET_2);
        fieldToOffsetMap.put("WIT2", WIT_OFFSET_2);
        fieldToOffsetMap.put("AGI2", AGI_OFFSET_2 );
        fieldToOffsetMap.put("MAGIC", MAGIC_OFFSET );
        fieldToOffsetMap.put("LV UP PARAM", LV_UP_PARAM_OFFSET  );
        fieldToOffsetMap.put("MIX ATTRIBUTE", MIX_ATTRIBUTE_OFFSET);
        fieldToOffsetMap.put("THUNDER", THUNDER_OFFSET);
        fieldToOffsetMap.put("SNOW", SNOW_OFFSET);
        fieldToOffsetMap.put("FOREST", FOREST_OFFSET);
        fieldToOffsetMap.put("EXPLOSION", EXPLOSION_OFFSET);
        
        fieldToOffsetMap.put("NAMES", NAMES_STRING_OFFSET  );
    }
    
    private void populateFieldToNewValueMap() {
        fieldToNewValueMap = new HashMap<>();
        
        //8x16 offsets
        fieldToNewValueMap.put("8x16 BUY", x16_BUY_VALUE);
        fieldToNewValueMap.put("8x16 BUY CANCEL", x16_BUY_CANCEL_VALUE);
        fieldToNewValueMap.put("8x16 SELL", x16_SELL_VALUE);
        fieldToNewValueMap.put("8x16 SELL CANCEL", x16_SELL_CANCEL_VALUE);
        fieldToNewValueMap.put("8x16 STASH", x16_STASH_VALUE);
        fieldToNewValueMap.put("8x16 STASH CANCEL", x16_STASH_CANCEL_VALUE);
        fieldToNewValueMap.put("8x16 GET", x16_GET_VALUE);
        fieldToNewValueMap.put("8x16 GET CANCEL", x16_GET_CANCEL_VALUE);
        fieldToNewValueMap.put("8x16 EQUIP", x16_EQUIP_VALUE);
        fieldToNewValueMap.put("8x16 TRADE", x16_TRADE_VALUE);
        fieldToNewValueMap.put("8x16 BUY2", x16_BUY2_VALUE);
        fieldToNewValueMap.put("8x16 WILL TRADE", x16_WILL_TRADE_VALUE);
        fieldToNewValueMap.put("8x16 SPACE", x16_SPACE_VALUE);
        fieldToNewValueMap.put("8x16 CANNOT_TRADE", x16_CANNOT_TRADE_VALUE);
        fieldToNewValueMap.put("8x16 WELCOME", x16_WELCOME_VALUE);
        fieldToNewValueMap.put("8x16 PICK AN ITEM TO BUY", x16_PICK_AN_ITEM_TO_BUY_VALUE);
        fieldToNewValueMap.put("8x16 WHO WILL BUY", x16_WHO_WILL_BUY_VALUE);
        fieldToNewValueMap.put("8x16 PICK AN ITEM TO SELL", x16_PICK_AN_ITEM_TO_SELL_VALUE);
        fieldToNewValueMap.put("8x16 BUY WHICH ATTRIBUTE", x16_BUY_WHICH_ATTRIBUTE_VALUE);
        fieldToNewValueMap.put("8x16 PICK AN ITEM TO STASH", x16_PICK_AN_ITEM_TO_STASH_VALUE);
        fieldToNewValueMap.put("8x16 PICK AN ITEM TO GET", x16_PICK_AN_ITEM_TO_GET_VALUE);
        fieldToNewValueMap.put("8x16 WHO IS BUYING", x16_WHO_IS_BUYING_VALUE);
        fieldToNewValueMap.put("8x16 YOU HAVE NO MANA EGGS", x16_YOU_HAVE_NO_MANA_EGGS_VALUE);
        fieldToNewValueMap.put("8x16 YOU HAVE NO ITEMS TO GET", x16_YOU_HAVE_NO_ITEMS_TO_GET_VALUE);
        fieldToNewValueMap.put("8x16 WAS LEARNED", x16_WAS_LEARNED_VALUE);
        
        
        //8x8 offsets
        fieldToNewValueMap.put("TRADE_VALUE", TRADE_VALUE_VALUE);
        fieldToNewValueMap.put("MANA EGGS", MANA_EGGS_VALUE);
        fieldToNewValueMap.put("EGGS", EGGS_VALUE);
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
        fieldToNewValueMap.put("GOLD PCS", GOLD_PCS_VALUE);
        fieldToNewValueMap.put("STR2", STR_VALUE_2);
        fieldToNewValueMap.put("WIT2", WIT_VALUE_2);
        fieldToNewValueMap.put("AGI2", AGI_VALUE_2 );
        fieldToNewValueMap.put("MAGIC", MAGIC_VALUE );
        fieldToNewValueMap.put("LV UP PARAM", LV_UP_PARAM_VALUE  );
        fieldToNewValueMap.put("MIX ATTRIBUTE", MIX_ATTRIBUTE_VALUE);
        fieldToNewValueMap.put("THUNDER", THUNDER_VALUE);
        fieldToNewValueMap.put("SNOW", SNOW_VALUE);
        fieldToNewValueMap.put("FOREST", FOREST_VALUE);
        fieldToNewValueMap.put("EXPLOSION", EXPLOSION_VALUE);
        
        fieldToNewValueMap.put("NAMES", NAMES_STRING_VALUE  );
    }
    
    /**
     * This map is needed for Auto-Updating. IF the offsets are updated out of order, incorrect values can be put in for HWRAM offsets.
     */
    private void populateOrderToFieldNameMap() {
        orderToFieldNameMap = new HashMap<>();
        
        orderToFieldNameMap.put(0, "TRADE_VALUE");
        orderToFieldNameMap.put(1, "MANA EGGS");
        orderToFieldNameMap.put(2, "EGGS");
        orderToFieldNameMap.put(3, "ATK");
        orderToFieldNameMap.put(4, "DEF");
        orderToFieldNameMap.put(5, "ACT");
        orderToFieldNameMap.put(6, "MOV");
        orderToFieldNameMap.put(7, "STR");
        orderToFieldNameMap.put(8, "VIT");
        orderToFieldNameMap.put(9, "WIT");
        orderToFieldNameMap.put(10, "AGI");
        orderToFieldNameMap.put(11, "WEAPON");
        orderToFieldNameMap.put(12, "SHIELD");
        orderToFieldNameMap.put(13, "ARMOR");
        orderToFieldNameMap.put(14, "HELMET");
        orderToFieldNameMap.put(15, "SHOES");
        orderToFieldNameMap.put(16, "JEWELRY");
        orderToFieldNameMap.put(17, "GOLD PCS");
        orderToFieldNameMap.put(18, "STR2");
        orderToFieldNameMap.put(19, "WIT2");
        orderToFieldNameMap.put(20, "AGI2");
        orderToFieldNameMap.put(21, "MAGIC");
        orderToFieldNameMap.put(22, "LV UP PARAM");
        orderToFieldNameMap.put(23, "MIX ATTRIBUTE");
        orderToFieldNameMap.put(24, "THUNDER");
        orderToFieldNameMap.put(25, "SNOW");
        orderToFieldNameMap.put(26, "FOREST");
        orderToFieldNameMap.put(27, "EXPLOSION");
        orderToFieldNameMap.put(28, "8x16 BUY");
        orderToFieldNameMap.put(29, "8x16 BUY CANCEL");
        orderToFieldNameMap.put(30, "8x16 SELL");
        orderToFieldNameMap.put(31, "8x16 SELL CANCEL");
        orderToFieldNameMap.put(32, "8x16 STASH");
        orderToFieldNameMap.put(33, "8x16 STASH CANCEL");
        orderToFieldNameMap.put(34, "8x16 GET");
        orderToFieldNameMap.put(35, "8x16 GET CANCEL");
        orderToFieldNameMap.put(36, "8x16 EQUIP");
        orderToFieldNameMap.put(37, "8x16 TRADE");
        orderToFieldNameMap.put(38, "8x16 BUY2");
        orderToFieldNameMap.put(39, "8x16 WILL TRADE");
        orderToFieldNameMap.put(40, "8x16 SPACE");
        orderToFieldNameMap.put(41, "8x16 CANNOT_TRADE");
        orderToFieldNameMap.put(42, "8x16 WELCOME");
        orderToFieldNameMap.put(43, "8x16 PICK AN ITEM TO BUY");
        orderToFieldNameMap.put(44, "8x16 WHO WILL BUY");
        orderToFieldNameMap.put(45, "8x16 PICK AN ITEM TO SELL");
        orderToFieldNameMap.put(46, "8x16 BUY WHICH ATTRIBUTE");
        orderToFieldNameMap.put(47, "8x16 PICK AN ITEM TO STASH");
        orderToFieldNameMap.put(48, "8x16 PICK AN ITEM TO GET");
        orderToFieldNameMap.put(49, "8x16 WHO IS BUYING");
        orderToFieldNameMap.put(50, "8x16 YOU HAVE NO MANA EGGS");
        orderToFieldNameMap.put(51, "8x16 YOU HAVE NO ITEMS TO GET");
        orderToFieldNameMap.put(52, "8x16 WAS LEARNED");
        orderToFieldNameMap.put(53, "NAMES");
        
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
        int length = shopBytes.length;
        int i = 0;
        while (i + 4 < length) {
            //Read 4 bytes out of the Array into a temporary buffer.
            ByteBuffer bb = ByteBuffer.wrap(shopBytes, i, 4);
            
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
            File shop = new File(inputFilePath);
            try {
                shopBytes = Files.readAllBytes(shop.toPath());
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
            buffer = Arrays.copyOfRange(shopBytes, i, i + 1);
            i++;
            if(bytesToHex(buffer).equals("00")) {
                size = i - offset;
                endOfValue = true;
            }
        }
        
        byte[] value = Arrays.copyOfRange(shopBytes, offset, offset + size);
        
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
            buffer = Arrays.copyOfRange(shopBytes, i, i + 1);
            i++;
            if(bytesToHex(buffer).equals("00") && numOfNames == NUM_OF_NAMES) {
                size = i - offset;
                endOfValue = true;
            } else if(bytesToHex(buffer).equals("00") && numOfNames < NUM_OF_NAMES) {
                numOfNames++;
            }
        }
        
        byte[] value = Arrays.copyOfRange(shopBytes, offset, offset + size);
        
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
    
    private static void updateReplacementValues() {
        //8x16 values
        x16_BUY_VALUE = properties.getProperty("shop.8x16.BUY_VALUE").replace("\"", "");
        x16_BUY_CANCEL_VALUE = properties.getProperty("shop.8x16.BUY_CANCEL_VALUE").replace("\"", "");
        x16_SELL_VALUE = properties.getProperty("shop.8x16.SELL_VALUE").replace("\"", "");
        x16_SELL_CANCEL_VALUE = properties.getProperty("shop.8x16.SELL_CANCEL_VALUE").replace("\"", "");
        x16_STASH_VALUE = properties.getProperty("shop.8x16.STASH_VALUE").replace("\"", "");
        x16_STASH_CANCEL_VALUE = properties.getProperty("shop.8x16.STASH_CANCEL_VALUE").replace("\"", "");
        x16_GET_VALUE = properties.getProperty("shop.8x16.GET_VALUE").replace("\"", "");
        x16_GET_CANCEL_VALUE = properties.getProperty("shop.8x16.GET_CANCEL_VALUE").replace("\"", "");
        x16_EQUIP_VALUE = properties.getProperty("shop.8x16.EQUIP_VALUE").replace("\"", "");
        x16_TRADE_VALUE = properties.getProperty("shop.8x16.TRADE_VALUE").replace("\"", "");
        x16_BUY2_VALUE = properties.getProperty("shop.8x16.BUY2_VALUE").replace("\"", "");
        x16_WILL_TRADE_VALUE = properties.getProperty("shop.8x16.WILL_TRADE_VALUE").replace("\"", "");
        x16_SPACE_VALUE = properties.getProperty("shop.8x16.SPACE_VALUE").replace("\"", "");
        x16_CANNOT_TRADE_VALUE = properties.getProperty("shop.8x16.CANNOT_TRADE_VALUE").replace("\"", "");
        x16_WELCOME_VALUE = properties.getProperty("shop.8x16.WELCOME_VALUE").replace("\"", "");
        x16_PICK_AN_ITEM_TO_BUY_VALUE = properties.getProperty("shop.8x16.PICK_AN_ITEM_TO_BUY_VALUE").replace("\"", "");
        x16_WHO_WILL_BUY_VALUE = properties.getProperty("shop.8x16.WHO_WILL_BUY_VALUE").replace("\"", "");
        x16_PICK_AN_ITEM_TO_SELL_VALUE = properties.getProperty("shop.8x16.PICK_AN_ITEM_TO_SELL_VALUE").replace("\"", "");
        x16_BUY_WHICH_ATTRIBUTE_VALUE = properties.getProperty("shop.8x16.BUY_WHICH_ATTRIBUTE_VALUE").replace("\"", "");
        x16_PICK_AN_ITEM_TO_STASH_VALUE = properties.getProperty("shop.8x16.PICK_AN_ITEM_TO_STASH_VALUE").replace("\"", "");
        x16_PICK_AN_ITEM_TO_GET_VALUE = properties.getProperty("shop.8x16.PICK_AN_ITEM_TO_GET_VALUE").replace("\"", "");
        x16_WHO_IS_BUYING_VALUE = properties.getProperty("shop.8x16.WHO_IS_BUYING_VALUE").replace("\"", "");
        x16_YOU_HAVE_NO_MANA_EGGS_VALUE = properties.getProperty("shop.8x16.YOU_HAVE_NO_MANA_EGGS_VALUE").replace("\"", "");
        x16_YOU_HAVE_NO_ITEMS_TO_GET_VALUE = properties.getProperty("shop.8x16.YOU_HAVE_NO_ITEMS_TO_GET_VALUE").replace("\"", "");
        x16_WAS_LEARNED_VALUE = properties.getProperty("shop.8x16.WAS_LEARNED_VALUE").replace("\"", "");
        
        //8x8 values
        TRADE_VALUE_VALUE = properties.getProperty("shop.8x8.TRADE_VALUE_VALUE").replace("\"", "");
        MANA_EGGS_VALUE = properties.getProperty("shop.8x8.MANA_EGGS_VALUE").replace("\"", "");
        EGGS_VALUE = properties.getProperty("shop.8x8.EGGS_VALUE").replace("\"", "");
        ATK_VALUE = properties.getProperty("shop.8x8.ATK_VALUE").replace("\"", "");
        DEF_VALUE = properties.getProperty("shop.8x8.DEF_VALUE").replace("\"", "");
        ACT_VALUE = properties.getProperty("shop.8x8.ACT_VALUE").replace("\"", "");
        MOV_VALUE = properties.getProperty("shop.8x8.MOV_VALUE").replace("\"", "");
        STR_VALUE = properties.getProperty("shop.8x8.STR_VALUE").replace("\"", "");
        VIT_VALUE = properties.getProperty("shop.8x8.VIT_VALUE").replace("\"", "");
        WIT_VALUE = properties.getProperty("shop.8x8.WIT_VALUE").replace("\"", "");
        AGI_VALUE = properties.getProperty("shop.8x8.AGI_VALUE").replace("\"", "");
        WEAPON_VALUE = properties.getProperty("shop.8x8.WEAPON_VALUE").replace("\"", "");
        SHIELD_VALUE = properties.getProperty("shop.8x8.SHIELD_VALUE").replace("\"", "");
        ARMOR_VALUE =  properties.getProperty("shop.8x8.ARMOR_VALUE").replace("\"", "");
        HELMET_VALUE = properties.getProperty("shop.8x8.HELMET_VALUE").replace("\"", "");
        SHOES_VALUE =  properties.getProperty("shop.8x8.SHOES_VALUE").replace("\"", "");
        JEWELRY_VALUE = properties.getProperty("shop.8x8.JEWELRY_VALUE").replace("\"", "");
        GOLD_PCS_VALUE = properties.getProperty("shop.8x8.GOLD_PCS_VALUE").replace("\"", "");
        STR_VALUE_2 = properties.getProperty("shop.8x8.STR_VALUE_2").replace("\"", "");
        WIT_VALUE_2 = properties.getProperty("shop.8x8.WIT_VALUE_2").replace("\"", "");
        AGI_VALUE_2 = properties.getProperty("shop.8x8.AGI_VALUE_2").replace("\"", "");
        MAGIC_VALUE = properties.getProperty("shop.8x8.MAGIC_VALUE").replace("\"", "");
        LV_UP_PARAM_VALUE = properties.getProperty("shop.8x8.LV_UP_PARAM_VALUE").replace("\"", "");
        MIX_ATTRIBUTE_VALUE = properties.getProperty("shop.8x8.MIX_ATTRIBUTE_VALUE").replace("\"", "");
        THUNDER_VALUE = properties.getProperty("shop.8x8.THUNDER_VALUE").replace("\"", "");
        SNOW_VALUE = properties.getProperty("shop.8x8.SNOW_VALUE").replace("\"", "");
        FOREST_VALUE = properties.getProperty("shop.8x8.FOREST_VALUE").replace("\"", "");
        EXPLOSION_VALUE = properties.getProperty("shop.8x8.EXPLOSION_VALUE").replace("\"", "");
        
        NAMES_STRING_VALUE = properties.getProperty("shop.NAMES_STRING_VALUE").replace("\"", "");
    }
}
