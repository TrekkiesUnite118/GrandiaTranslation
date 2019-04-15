package com.grandia.file.structure;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * ScriptPointerTable Class
 * 
 * This class represents the PointerTable used for the script portions of the MDT/MDP files.
 * 
 * @author TrekkiesUnite118
 *
 */
public class ScriptPointerTable {
    
    private Map<Integer, PointerTableEntry> pointerTable = new HashMap<>();
    private static final Logger log = Logger.getLogger(PointerTable.class.getName());
    
    public void addOrUpdatePointerTableEntry(int key, PointerTableEntry pte) {
        pointerTable.put(key, pte);
    }
    
    public PointerTableEntry getPointerTableEntry(Integer key) {
        return pointerTable.get(key);
    }
    
    public int getSize() {
        return pointerTable.size();
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("POINTER TABLE: \n");
        
        for(Integer key : pointerTable.keySet()) {
            sb.append("KEY: " + Integer.toHexString(key.intValue()) + "\n");
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
     */
    public void parsePointerTableFromByteArray(byte[] headerArray, boolean bigEndian) {
        
        log.log(Level.INFO, "Header size is : " + headerArray.length);
        
        //First we check that the header array is divisible by 4. If it's not, then we've made a mistake in getting it out of the file.
        if(headerArray.length % 4 == 0) {
            int i = 0;
            int key = 0;
            while(i < headerArray.length) {
                
                //Create a new pointer table entry
                PointerTableEntry pte = new PointerTableEntry().tableOffset(i);
                
                //Read 2 bytes out of the Header Array into a temporary buffer.
                ByteBuffer bb = ByteBuffer.wrap(headerArray, i, 2);
                
                /*
                 * If we're not dealing with the Saturn (Big Endian), then we're dealing with the PS1.
                 * So it needs to  be Little Endian.
                 */
                if(!bigEndian) {
                    bb.order(ByteOrder.LITTLE_ENDIAN);
                }
                
                //Set the id in the pointer table entry to the bytes we just read off.
                int id = Short.toUnsignedInt(bb.getShort());
                pte.id(id);
                pte.stringId(Integer.toHexString(id));
                
                //Move ahead 2 bytes and read.
                i += 2;
                bb = ByteBuffer.wrap(headerArray, i, 2);
                
                /*
                 * Set the endianess.
                 */
                if(!bigEndian) {
                    bb.order(ByteOrder.LITTLE_ENDIAN);
                }
                
                //Set the offset to the bytes we just read.
                pte.offset(Short.toUnsignedInt(bb.getShort()));
                
                //Move ahead 2 bytes.
                i += 2;
                
                //Add the new PointerTableEntry to the PointerTable.
                this.addOrUpdatePointerTableEntry(key, pte);
                key++;
            }
            calculateEntrySize();
        } else {
            log.log(Level.SEVERE, "Header is not divisible by 4! We've made a mistake in parsing it.");
        }
    }
    
    public void calculateEntrySize() {
        for(int i = 0; i < pointerTable.size(); i++) {
            
            if(i + 1 < pointerTable.size()) {
                PointerTableEntry pte = pointerTable.get(i);
                PointerTableEntry nextPte = pointerTable.get(i+1);
                
                if(!Integer.toHexString(nextPte.getOffset()).equals("ffff")) {
                    int size = nextPte.getOffset() - pte.getOffset();
                    pte.setSize(size);
                    pointerTable.put(i,  pte);
                }
            }
        }
    }
    
    public void recacluateOffsets() {
        int offset = 0;
        for(int i = 0; i < pointerTable.size(); i++) {
            PointerTableEntry pte = pointerTable.get(i);
            if(!Integer.toHexString(pte.getId()).equals("ffff")) {
                pte.setOffset(offset);
                pointerTable.put(i, pte);
                offset += pte.getSize();
            }
        }
    }
}
