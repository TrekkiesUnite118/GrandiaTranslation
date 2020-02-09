package com.grandia.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.grandia.file.structure.PointerTable;
import com.grandia.file.structure.PointerTableEntry;
import com.grandia.file.utils.FileUtils;

/**
 * 
 * BBGFileReconstructor
 * 
 * This class takes the individual pieces from the BBGFileParser and reconstructs them back
 * into a complete BBG file again. This will also recalulate header offsets incase files have changed in size.
 * 
 * @author TrekkiesUnite118
 *
 */
public class BBGFileReconstructor {
    private static final int HEADER_SIZE = 40;
    private static final int COMP_GFX_OFFSET = 0;
    private static final int TEXT_OFFSET = 36;
    private static final int COMP_ORIG_SIZE = 109336;
    private static final Logger log = Logger.getLogger(MDTFileParser.class.getName());
    private PointerTable pointerTable = new PointerTable();;
    private String inputFilePath;
    private String outputFilePath;
    
    public BBGFileReconstructor inputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
        return this;
    }
    
    public BBGFileReconstructor outputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
        return this;
    }
    
    public String getInputFilePath() {
        return inputFilePath;
    }

    public void setInputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    /**
     * Reconstruct Method.
     * 
     * Reconstructs the Saturn BBG Files from their 3 individual pieces. Will do this for all
     * files in the Input directory. Files are written out to the output directory.
     * 
     * Note: Compressed graphics are the same across all BBGs. So this will just use the one from B001.BBG.
     */
    public void reconstruct() {
        if(inputFilePath == null) {
            log.log(Level.WARNING, "Input Files path are null, aborting parsing.");
        } else {
            
            //Read in our files
            Map<String, File> headerFiles = new HashMap<>();
            FileUtils.populateFileMap(headerFiles, inputFilePath, ".HEADER");
            
            Map<String, File> cgfxFiles = new HashMap<>();
            FileUtils.populateFileMap(cgfxFiles, inputFilePath, ".CGFX");
            
            File compressedGraphics = new File(inputFilePath + "\\B001.CGFX");
            
            Map<String, File> middleFiles = new HashMap<>();
            FileUtils.populateFileMap(middleFiles, inputFilePath, ".MIDDLE");
            
            Map<String, File> textFiles = new HashMap<>();
            FileUtils.populateFileMap(textFiles, inputFilePath, ".TXTPORTION");
            
            //For each file, we get it's remaining parts, calculate the new pointer table, and put it back together.
            for(String key : headerFiles.keySet()) {
                try {
                    
                    //Parse in the pointer table.
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] headerBytes = Files.readAllBytes(headerFiles.get(key).toPath());
                    pointerTable.parsePointerTableFromByteArray(headerBytes, true, false);
                    PointerTableEntry cgfxEntry = pointerTable.getPointerTableEntry(COMP_GFX_OFFSET);
                    //printPointerTable();
                    
                    //Read the rest of the file pieces in.
                    byte[] cgfxBytes = Files.readAllBytes(compressedGraphics.toPath());
                    byte[] middleBytes = Files.readAllBytes(middleFiles.get(key).toPath());
                    byte[] textBytes = Files.readAllBytes(textFiles.get(key).toPath());
                    
                    //Determine the new cgfx size.
                    cgfxEntry.setSize(COMP_ORIG_SIZE);

                    boolean bigger = true;
                    //Determine the difference in size between the old cgfx and the new cgfx.
                    int sizeDiff = 0;
                    if(cgfxBytes.length > cgfxEntry.getSize()) {
                        sizeDiff = cgfxBytes.length - cgfxEntry.getSize();
                    } else {
                        sizeDiff = cgfxEntry.getSize() - cgfxBytes.length;
                        bigger = false;
                        
                    }
                    System.out.println(sizeDiff);
                    
                    //Calculate the new pointer table entries.
                    ByteBuffer bb = ByteBuffer.allocate(4);
                    for(int i = 0; i < HEADER_SIZE; i +=4) {
                        PointerTableEntry pte = pointerTable.getPointerTableEntry(i);
                            System.out.println(Integer.toHexString(pte.getOffset()) + " > " + Integer.toHexString(cgfxEntry.getOffset()));
                            if(!Integer.toHexString(pte.getOffset()).equals("ffffffff")) {
                                if(pte.getOffset() > cgfxEntry.getOffset()) {
                                    if(bigger) {
                                        pte.setOffset(pte.getOffset() + sizeDiff);
                                    } else {
                                        pte.setOffset(pte.getOffset() - sizeDiff);
                                    }
                                }
                            }
                            bb.putInt(0, pte.getOffset());
                            out.write(bb.array());
                    }      
                    
                    
                    //Write them to the output stream.
                    out.write(cgfxBytes);
                    out.write(middleBytes);
                    out.write(textBytes);
                    
                    //Write out the new BBG file.
                    FileUtils.writeToFile(out.toByteArray(), key, ".BBG", ".BBG", outputFilePath);
                    
                } catch (IOException e) {
                    log.log(Level.SEVERE, "Caught IOException attempting to read bytes.", e);
                    e.printStackTrace();
                }
            }
        }
    }

}
