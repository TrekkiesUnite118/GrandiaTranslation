package com.grandia.file.structure;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * PointerTable class
 * 
 * This class represents the pointer tables used in MDT/MDP files.
 * 
 * @author TrekkiesUnite118
 *
 */
public class PointerTable {

    private Map<Integer, PointerTableEntry> pointerTable = new HashMap<>();
    private static final Logger log = Logger.getLogger(PointerTable.class.getName());
    
    
    public void addOrUpdatePointerTableEntry(PointerTableEntry pte) {
        pointerTable.put(pte.getTableOffset(), pte);
    }
    
    public PointerTableEntry getPointerTableEntry(Integer key) {
        return pointerTable.get(key);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("POINTER TABLE: \n");
        
        for(Integer key : pointerTable.keySet()) {
            sb.append("HEX KEY: " + Integer.toHexString(key.intValue()) + "\n");
            sb.append("TABLE ENTRY: " + pointerTable.get(key).toString());
        }
        
        sb.append("SIZE: " + pointerTable.size() + " entires.");
        
        return sb.toString();
    }
    
    /**
     * This Parses the pointer table into an easier to use object.
     * We convert the byte array into a ByteBuffer object, and then
     * use that to read off 4 bytes for the offset value.
     * Next we read the next 4 bytes for the size of the entry.
     * We then use those values plus the current value of the iterator
     * to create a PointerTableEntry Object to put in our PointerTable.
     * 
     * @param headerArray
     * @param bigEndian - True if pointers are in Big Endian
     * @param parseSize - True if pointer entry includes size values.
     */
    public void parsePointerTableFromByteArray(byte[] headerArray, boolean bigEndian, boolean parseSize) {
        
        log.log(Level.INFO, "Header size is : " + headerArray.length);
        int entrySize = 8;
        if(!parseSize) {
            entrySize = 4;
        }
        //First we check that the header array is divisible by 8. If it's not, then we've made a mistake in getting it out of the file.
        if(headerArray.length % entrySize == 0) {
            int i = 0;
            while(i < headerArray.length) {
                
                //Create a new pointer table entry
                PointerTableEntry pte = new PointerTableEntry().tableOffset(i);
                
                //Read 4 bytes out of the Header Array into a temporary buffer.
                ByteBuffer bb = ByteBuffer.wrap(headerArray, i, 4);
                
                /*
                 * If we're not dealing with the Saturn (Big Endian), then we're dealing with the PS1.
                 * So it needs to  be Little Endian.
                 */
                if(!bigEndian) {
                    bb.order(ByteOrder.LITTLE_ENDIAN);
                }
                
                //Set the offset in the pointer table entry to the bytes we just read off.
                pte.offset(bb.getInt());

                
               if(parseSize) {
                    
                    //Move ahead 4 bytes and read.
                    i += 4;
                    bb = ByteBuffer.wrap(headerArray, i, 4);
                    
                    /*
                     * Set the endianess.
                     */
                    if(!bigEndian) {
                        bb.order(ByteOrder.LITTLE_ENDIAN);
                    }
                    
                    //Set the size to the bytes we just read.
                    pte.size(bb.getInt());
                }
                //Move ahead 4 bytes.
                i += 4;
                //Add the new PointerTableEntry to the PointerTable.
                this.addOrUpdatePointerTableEntry(pte);
            }
        } else {
            log.log(Level.SEVERE, "Header is not divisible by 8! We've made a mistake in parsing it.");
        }
    }
}
