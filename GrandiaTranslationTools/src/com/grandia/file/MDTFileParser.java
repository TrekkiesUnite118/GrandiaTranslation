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
                try {
                    //Byte array for the file.
                    byte[] MDTByteArray = Files.readAllBytes(f.toPath());
                    //Byte array for the header
                    byte[] headerArray = Arrays.copyOfRange(MDTByteArray, 0, 512);
                    
                    //Parse the Header into a PointerTable object.
                    pointerTable.parsePointerTableFromByteArray(headerArray, bigEndian);
                    
                    //Debug code if you want to see what is being read in from the pointe rtable.
                    //printPointerTable();
                    
                    //Get the pointer table entries for the script and script header.
                    PointerTableEntry scriptHeaderEntry = pointerTable.getPointerTableEntry(SCRIPT_HEADER_OFFSET);
                    PointerTableEntry scriptEntry = pointerTable.getPointerTableEntry(SCRIPT_OFFSET);
                    
                    //If the offset is all xFF, we move to the next one. Other wise...
                    if(!Integer.toHexString(scriptEntry.getOffset()).equals("ffffffff")){
                        
                        //Write the header to the header file.
                        FileUtils.writeToFile(headerArray, f.getName(), fileExtension, ".HEADER", outputFilePath);
                        
                        //Get the PreScript portion of the file and write it to the PreScript file.
                        byte[] preScriptPortionArray = Arrays.copyOfRange(MDTByteArray, 512, scriptHeaderEntry.getOffset());
                        FileUtils.writeToFile(preScriptPortionArray, f.getName(), fileExtension, ".PRESCRIPT", outputFilePath);
                        
                        //Get the Script Header portion of the file and write it to the ScriptHeader file.
                        int scriptHeaderEndPosition = scriptHeaderEntry.getOffset() + scriptHeaderEntry.getSize();
                        byte[] scriptHeaderPortionArray = Arrays.copyOfRange(MDTByteArray, scriptHeaderEntry.getOffset() ,scriptHeaderEndPosition);
                        FileUtils.writeToFile(scriptHeaderPortionArray, f.getName(), fileExtension, ".SCRIPTHEADER", outputFilePath);
                        
                        //Get the Script portion of the file and write it to the Script file.
                        int endPosition = scriptEntry.getOffset() + scriptEntry.getSize();
                        byte[] scriptArray = Arrays.copyOfRange(MDTByteArray, scriptEntry.getOffset(), endPosition);
                        FileUtils.writeToFile(scriptArray, f.getName(), fileExtension, ".SCRIPT", outputFilePath);
                        
                        //Get the PostScript portion of the file and write it to the PostScript file.
                        byte[] postScriptPortionArray = Arrays.copyOfRange(MDTByteArray, endPosition, MDTByteArray.length);
                        FileUtils.writeToFile(postScriptPortionArray, f.getName(), fileExtension, ".POSTSCRIPT", outputFilePath);
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
