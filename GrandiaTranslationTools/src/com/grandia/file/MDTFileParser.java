package com.grandia.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.grandia.file.structure.PointerTable;
import com.grandia.file.structure.PointerTableEntry;
import com.grandia.file.utils.FileUtils;

/**
 * 
 * MDT File Parser
 * 
 * This class has methods for parsing MDT and MDP files into 5 individual parts:
 * 
 * Header Portion
 * Pre-Script Portion
 * Script Header Portion
 * Script Portion
 * Post Script Portion
 * 
 * This is useful for extracting the script portion of the files to allow easier conversion.
 * 
 * @author TrekkiesUnite118
 *
 */
public class MDTFileParser {

    private static final int SCRIPT_OFFSET = 96;
    private static final int SCRIPT_HEADER_OFFSET = 88;
    
    /*
     * It turns out that some files have embedded programs to deal with scripted events. 
     * If these are present the GRAPHICS file will be broken down into the following:
     * 
     * GFX1 = Data before the program
     * ASM = The program
     * GFX2 = Data after the program
     * GFX3 = More data after the program.
     */
    private static final int GFX_OFFSET = 112;
    private static final int GFX_OFFSET_2 = 192;
    private static final int GFX_OFFSET_3 = 216;
    private static final int ASM_CODE_OFFSET = 72;
    private static final int END_OF_DATA_OFFSET = 504;
    private static final int START_OF_SPECIAL_DATA = 384;
    private static final int SECTOR_SIZE = 2048;
    private static final Logger log = Logger.getLogger(MDTFileParser.class.getName());
    private List<File> fileList = new ArrayList<>();
    private PointerTable pointerTable = new PointerTable();
    private String inputFilePath;
    private String inputModifiedFilePath;
    private String outputFilePath;
    private String fileExtension;
    private boolean bigEndian;
    
    public MDTFileParser inputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
        return this;
    }
    
    public MDTFileParser inputModifiedFilePath(String inputFilePath) {
        this.inputModifiedFilePath = inputFilePath;
        return this;
    }
    
    public MDTFileParser outputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
        return this;
    }
    
    public MDTFileParser fileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
        return this;
    }
    
    public MDTFileParser bigEndian(boolean bigEndian) {
        this.bigEndian = bigEndian;
        return this;
    }
    
    public String getInputFilePath() {
        return inputFilePath;
    }

    public void setInputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }
    
    public String getInputModifiedFilePath() {
        return inputModifiedFilePath;
    }

    public void setInputModifiedFilePath(String inputFilePath) {
        this.inputModifiedFilePath = inputFilePath;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    /**
     * Parse method
     * 
     * This method will iterate through all the MDT/MDP files in the specified input directory.
     * For each file it will parse out it's pointer table, then use that information to break 
     * the file down into the following parts:
     * 
     * Header - Pointer Table
     * PreScript - Portion of the file before we hit the Script Header/Pointer Table
     * ScriptHeader - The pointer table for the script portion of the file.
     * Script - The Script data.
     * Post Script - The rest of the file that comes after the script.
     * 
     * These 5 files are then written out to the output directory using the original file name 
     * combined with a file extension for which part of the file they represent.
     */
    public void parse() {
        if(inputFilePath == null) {
            log.log(Level.WARNING, "Input File path is null, aborting parsing.");
        } else {
            //Read the directory to find out what files we need to parse
            populateFileArray();
            
            /*
             * For each file, read the header. Then use that information to parse
             * the file into it's 5 parts. Then write them to the output directory.
             */
            for(File f : fileList) {
                log.log(Level.INFO, "Parsing File " + f.getName());
                try {
                    //Byte array for the file.
                    byte[] MDTByteArray = Files.readAllBytes(f.toPath());
                    //Byte array for the header
                    byte[] headerArray = Arrays.copyOfRange(MDTByteArray, 0, 512);
                    
                    //Parse the Header into a PointerTable object.
                    pointerTable.parsePointerTableFromByteArray(headerArray, bigEndian, true);
                    
                    //Debug code if you want to see what is being read in from the pointer table.
                    //printPointerTable();
                    
                    //Get the pointer table entries for the script and script header.
                    PointerTableEntry scriptHeaderEntry = pointerTable.getPointerTableEntry(SCRIPT_HEADER_OFFSET);
                    PointerTableEntry scriptEntry = pointerTable.getPointerTableEntry(SCRIPT_OFFSET);
                    PointerTableEntry asmEntry = pointerTable.getPointerTableEntry(ASM_CODE_OFFSET);
                    PointerTableEntry gfx1Entry = pointerTable.getPointerTableEntry(GFX_OFFSET);
                    PointerTableEntry gfx2Entry = pointerTable.getPointerTableEntry(GFX_OFFSET_2);
                    PointerTableEntry gfx3Entry = pointerTable.getPointerTableEntry(GFX_OFFSET_3);
                    PointerTableEntry endOfDataEntry = pointerTable.getPointerTableEntry(END_OF_DATA_OFFSET);
                    PointerTableEntry startOfSpecialDataEntry = pointerTable.getPointerTableEntry(START_OF_SPECIAL_DATA);
                    
                    
                    int specialDataOffset = startOfSpecialDataEntry.getOffset();
                    if(!Integer.toHexString(specialDataOffset).equals("ffffffff")){
                        specialDataOffset = specialDataOffset * SECTOR_SIZE;
                    }else {
                        specialDataOffset = endOfDataEntry.getSize();
                    }
                    //If the offset is all xFF, we move to the next one. Other wise...
                    if(!Integer.toHexString(scriptEntry.getOffset()).equals("ffffffff")){
                        
                        //Write the header to the header file.
                        FileUtils.writeToFile(headerArray, f.getName(), fileExtension, ".HEADER", outputFilePath);
                        
                        //Get the PreScript portion of the file and write it to the PreScript file.
                        byte[] preScriptPortionArray = Arrays.copyOfRange(MDTByteArray, 512, scriptHeaderEntry.getOffset());
                        FileUtils.writeToFile(preScriptPortionArray, f.getName(), fileExtension, ".PRESCRIPT", outputFilePath);
                        
                        //Get the PreScript portion of the file and write it to the PreScript file.
                        int asmOffset = asmEntry.getOffset();
                        if(!Integer.toHexString(asmOffset).equals("ffffffff")){
                            int asmEntryEndPosition = asmEntry.getOffset() + asmEntry.getSize();
                            byte[] asmPortionArray = Arrays.copyOfRange(MDTByteArray, asmEntry.getOffset() ,asmEntryEndPosition);
                            FileUtils.writeToFile(asmPortionArray, f.getName(), fileExtension, ".ASM", outputFilePath);
                            
                            int gfx1Size = asmEntry.getOffset() - gfx1Entry.getOffset();
                            
                            int gfx1EntryEndPosition = gfx1Entry.getOffset() + gfx1Size;
                            byte[] gfx1PortionArray = Arrays.copyOfRange(MDTByteArray, gfx1Entry.getOffset() ,gfx1EntryEndPosition);
                            FileUtils.writeToFile(gfx1PortionArray, f.getName(), fileExtension, ".GFX1", outputFilePath);
                            
                            int gfx2Offset = gfx2Entry.getOffset();
                            if(!Integer.toHexString(gfx2Offset).equals("ffffffff")){
                                int gfx2EntryEndPosition = gfx2Entry.getOffset() + gfx2Entry.getSize();
                                byte[] gfx2PortionArray = Arrays.copyOfRange(MDTByteArray, gfx2Entry.getOffset() ,gfx2EntryEndPosition);
                                FileUtils.writeToFile(gfx2PortionArray, f.getName(), fileExtension, ".GFX2", outputFilePath);
                            }
                            
                            int gfx3Offset = gfx3Entry.getOffset();
                            if(!Integer.toHexString(gfx3Offset).equals("ffffffff")){
                                int gfx3EntryEndPosition = gfx3Entry.getOffset() + gfx3Entry.getSize();
                                byte[] gfx3PortionArray = Arrays.copyOfRange(MDTByteArray, gfx3Entry.getOffset() ,gfx3EntryEndPosition);
                                FileUtils.writeToFile(gfx3PortionArray, f.getName(), fileExtension, ".GFX3", outputFilePath);
                            }
                            
                        }
                        
                        //Get the Script Header portion of the file and write it to the ScriptHeader file.
                        int scriptHeaderEndPosition = scriptHeaderEntry.getOffset() + scriptHeaderEntry.getSize();
                        byte[] scriptHeaderPortionArray = Arrays.copyOfRange(MDTByteArray, scriptHeaderEntry.getOffset() ,scriptHeaderEndPosition);
                        FileUtils.writeToFile(scriptHeaderPortionArray, f.getName(), fileExtension, ".SCRIPTHEADER", outputFilePath);
                        
                        //Get the Script portion of the file and write it to the Script file.
                        int scriptEndPosition = scriptEntry.getOffset() + scriptEntry.getSize();
                        byte[] scriptArray = Arrays.copyOfRange(MDTByteArray, scriptEntry.getOffset(), scriptEndPosition);
                        FileUtils.writeToFile(scriptArray, f.getName(), fileExtension, ".SCRIPT", outputFilePath);
                        
                        //Get the Graphics portion of the file and write it to the Graphics file.
                        byte[] graphicsArray = Arrays.copyOfRange(MDTByteArray, scriptEndPosition, endOfDataEntry.getSize());
                        FileUtils.writeToFile(graphicsArray, f.getName(), fileExtension, ".GRAPHICS", outputFilePath);
                        
                        //Get the Footer portion of the file and write it to the Footer file.
                        byte[] footerArray = Arrays.copyOfRange(MDTByteArray, specialDataOffset, MDTByteArray.length);
                        FileUtils.writeToFile(footerArray, f.getName(), fileExtension, ".FOOTER", outputFilePath);
                    }
                    
                    
                } catch (IOException e) {
                    log.log(Level.SEVERE, "Caught IOException attempting to read bytes.", e);
                    e.printStackTrace();
                }
            }
        }
        
    }
    
    @SuppressWarnings("unused")
    private void printPointerTable() {
        System.out.println(pointerTable.toString());
    }
    
    /**
     * 
     */
    private void populateFileArray() {
        FileUtils.populateFileArray(fileList, inputFilePath, fileExtension);
    }
    
}
