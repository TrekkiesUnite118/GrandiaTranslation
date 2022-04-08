package com.grandia.file.structure;

import java.util.ArrayList;
import java.util.List;

public class SCNSection {
    
    private List<byte[]> nonTextPortions = new ArrayList<>();
    
    private List<TextPortion> textPortions= new ArrayList<>();
    
    private int sectionSize = 0;
    
    private int sizeIndex = 0;
    
    private boolean hasSize = false;
    
    List<String> sectionOrder = new ArrayList<>();
    
    /**
     * Adds a text portion to the section.
     * 
     * @param tp the Text Portion to add.
     */
    public void addOrder(String order) {
        sectionOrder.add(order);
    }
    
    /**
     * Adds a nontext portion to the section.
     * 
     * @param ntp Nontext Portion to add.
     */
    public void addNonTextPortion(byte[] ntp) {
        nonTextPortions.add(ntp);
    }
    
    /**
     * Adds a text portion to the section.
     * 
     * @param tp the Text Portion to add.
     */
    public void addTextPortion(TextPortion tp) {
        textPortions.add(tp);
    }

    /**
     * The getter for nonTextPortions.
     *
     * @return the nonTextPortions.
     */
    public List<byte[]> getNonTextPortions() {
        return nonTextPortions;
    }

    /**
     * The setter for nonTextPortions.
     *
     * @param nonTextPortions the nonTextPortions to set.
     */
    public void setNonTextPortions(List<byte[]> nonTextPortions) {
        this.nonTextPortions = nonTextPortions;
    }

    /**
     * The getter for textPortions.
     *
     * @return the textPortions.
     */
    public List<TextPortion> getTextPortions() {
        return textPortions;
    }

    /**
     * The setter for textPortions.
     *
     * @param textPortions the textPortions to set.
     */
    public void setTextPortions(List<TextPortion> textPortions) {
        this.textPortions = textPortions;
    }

    /**
     * The getter for sectionSize.
     *
     * @return the sectionSize.
     */
    public int getSectionSize() {
        return sectionSize;
    }

    /**
     * The setter for sectionSize.
     *
     * @param sectionSize the sectionSize to set.
     */
    public void setSectionSize(int sectionSize) {
        this.sectionSize = sectionSize;
    }

    /**
     * The getter for sizeIndex.
     *
     * @return the sizeIndex.
     */
    public int getSizeIndex() {
        return sizeIndex;
    }

    /**
     * The setter for sizeIndex.
     *
     * @param sizeIndex the sizeIndex to set.
     */
    public void setSizeIndex(int sizeIndex) {
        this.sizeIndex = sizeIndex;
    }

    /**
     * The getter for hasSize.
     *
     * @return the hasSize.
     */
    public boolean isHasSize() {
        return hasSize;
    }

    /**
     * The setter for hasSize.
     *
     * @param hasSize the hasSize to set.
     */
    public void setHasSize(boolean hasSize) {
        this.hasSize = hasSize;
    }

    /**
     * The getter for sectionOrder.
     *
     * @return the sectionOrder.
     */
    public List<String> getSectionOrder() {
        return sectionOrder;
    }

    /**
     * The setter for sectionOrder.
     *
     * @param sectionOrder the sectionOrder to set.
     */
    public void setSectionOrder(List<String> sectionOrder) {
        this.sectionOrder = sectionOrder;
    }
    
    

}
