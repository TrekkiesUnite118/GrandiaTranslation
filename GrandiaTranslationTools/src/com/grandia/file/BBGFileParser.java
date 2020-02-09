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
 * BBG File Parser
 * 
 * This class has methods for parsing BB files into 4 individual parts:
 * 
 * Header Portion
 * Compressed Graphics Portion
 * Middle Portion
 * Text Portion
 * 
 * This is useful for extracting the text and graphical data of the files to allow easier conversion.
 * 
 * @author TrekkiesUnite118
 *
 */
public class BBGFileParser {
    private static final int HEADER_SIZE = 40;
    private static final int COMP_GFX_OFFSET = 0;
    private static final int POST_GFX_OFFSET = 4;
    private static final int TEXT_OFFSET = 36;
    private static final Logger log = Logger.getLogger(BBGFileParser.class.getName());
    private List<File> fileList = new ArrayList<>();
    private PointerTable pointerTable = new PointerTable();
    private String inputFilePath;
    private String inputModifiedFilePath;
    private String outputFilePath;
    private String fileExtension;
    private boolean bigEndian;
    
    public BBGFileParser inputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
        return this;
    }
    
    public BBGFileParser inputModifiedFilePath(String inputFilePath) {
        this.inputModifiedFilePath = inputFilePath;
        return this;
    }
    
    public BBGFileParser outputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
        return this;
    }
    
    public BBGFileParser fileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
        return this;
    }
    
    public BBGFileParser bigEndian(boolean bigEndian) {
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
     * This method will iterate through all the BBG files in the specified input directory.
     * For each file it will parse out it's pointer table, then use that information to break 
     * the file down into the following parts:
     * 
     * Header - Pointer Table
     * Compressed Graphics - Portion of the file that contains the compressed battle graphics
     * Middle - The middle portion of the file, contains other graphics and info.
     * Text - The battle text data.
     * 
     * These 4 files are then written out to the output directory using the original file name 
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
                    byte[] headerArray = Arrays.copyOfRange(MDTByteArray, 0, HEADER_SIZE);
                    
                    //Parse the Header into a PointerTable object.
                    pointerTable.parsePointerTableFromByteArray(headerArray, bigEndian, false);
                    
                    //Debug code if you want to see what is being read in from the pointe rtable.
                    printPointerTable();
                    
                    
                    
                    //Get the pointer table entries.
                    PointerTableEntry compGfxEntry = pointerTable.getPointerTableEntry(COMP_GFX_OFFSET);
                    PointerTableEntry textEntry = pointerTable.getPointerTableEntry(TEXT_OFFSET);
                    PointerTableEntry postGfxEntry = pointerTable.getPointerTableEntry(POST_GFX_OFFSET);
                    compGfxEntry.setSize(postGfxEntry.getOffset() - compGfxEntry.getOffset());
                    textEntry.setSize(MDTByteArray.length - textEntry.getOffset());

                    //Write the header to the header file.
                    FileUtils.writeToFile(headerArray, f.getName(), fileExtension, ".HEADER", outputFilePath);
                    
                    //Get the CompGfx portion of the file and write it to the CGFX file.
                    int compGfxEndPosition = compGfxEntry.getOffset() + compGfxEntry.getSize();
                    byte[] compGfxArray = Arrays.copyOfRange(MDTByteArray, HEADER_SIZE, compGfxEndPosition);
                    FileUtils.writeToFile(compGfxArray, f.getName(), fileExtension, ".CGFX", outputFilePath);
                    
                    //Get the mid portion of the file and write it to the Middle file.
                    byte[] midSectionArray = Arrays.copyOfRange(MDTByteArray, compGfxEndPosition, textEntry.getOffset());
                    FileUtils.writeToFile(midSectionArray, f.getName(), fileExtension, ".MIDDLE", outputFilePath);
                    
                    //Get the Text portion of the file and write it to the Text file.
                    System.out.println("TEXT OFFSET: " + Integer.toHexString(textEntry.getOffset()));
                    byte[] textArray = Arrays.copyOfRange(MDTByteArray, textEntry.getOffset(), textEntry.getOffset() + textEntry.getSize());
                    FileUtils.writeToFile(textArray, f.getName(), fileExtension, ".TXTPORTION", outputFilePath);
                                        
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
