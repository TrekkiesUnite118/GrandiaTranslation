package com.grandia.file.structure;

/**
 * This object represents a portrait code
 * 
 * It stores the code and the offset in the script where it occurs.
 * 
 * @author TrekkiesUnite118
 *
 */
public class PortraitCode {
    
    byte[] code;
    int offset;
    public byte[] getCode() {
        return code;
    }
    public void setCode(byte[] code) {
        this.code = code;
    }
    public int getOffset() {
        return offset;
    }
    public void setOffset(int offset) {
        this.offset = offset;
    }

    public PortraitCode(byte[] code, int offset) {
        this.code = code;
        this.offset = offset;
    }
}
