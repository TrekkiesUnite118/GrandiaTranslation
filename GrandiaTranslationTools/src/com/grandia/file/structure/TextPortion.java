package com.grandia.file.structure;

public class TextPortion {
    
    String[] textData;
    
    byte[] sizePortion;
    
    
    /**
     * Constructor 
     * @param text Text Portion.
     * @param size Size.
     */
    public TextPortion() {
    }

    
    /**
     * Constructor 
     * @param text Text Portion.
     * @param size Size.
     */
    public TextPortion(String[] text, byte[] size) {
        this.textData = text;
        this.sizePortion = size;
    }

    /**
     * The getter for sizePortion.
     *
     * @return the sizePortion.
     */
    public byte[] getSizePortion() {
        return sizePortion;
    }

    /**
     * The setter for sizePortion.
     *
     * @param sizePortion the sizePortion to set.
     */
    public void setSizePortion(byte[] sizePortion) {
        this.sizePortion = sizePortion;
    }

    /**
     * The getter for textData.
     *
     * @return the textData.
     */
    public String[] getTextData() {
        return textData;
    }

    /**
     * The setter for textData.
     *
     * @param textData the textData to set.
     */
    public void setTextData(String[] textData) {
        this.textData = textData;
    }
    
    
    
}
