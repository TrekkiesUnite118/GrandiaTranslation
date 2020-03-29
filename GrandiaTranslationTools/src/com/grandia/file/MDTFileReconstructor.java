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
 * MDTFileReconstructor
 * 
 * This class takes the 5 pieces from the MDTFileParser and reconstructs them back
 * into a complete MDT file again. This will also recalulate header offsets incase the script has changed in size.
 * 
 * @author TrekkiesUnite118
 *
 */
public class MDTFileReconstructor {
    
    private static final int SCRIPT_OFFSET = 96;
    private static final int SCRIPT_HEADER_OFFSET = 88;
    private static final int ASM_CODE_OFFSET = 72;
    //The value at offset 0x20 seems to be a common HWRAM location on the Saturn, and shouldn't be modified.
    private static final int OFFSET_0X20 = 32;
    //The value at offset 0x68 seems to be a common HWRAM location on the Saturn, and shouldn't be modified.
    private static final int OFFSET_0X68 = 104;
    // The values at offset 0x180 and 0x1F4 represent the size of the data in CD Sectors and will need to be updated.
    private static final int SECTOR_SIZE_OFFSET_A = 384;
    private static final int SECTOR_SIZE_OFFSET_B = 496;
    //These values represent any special data that needs to be at specific sector based offsets.
    private static final int SPECIAL_DATA_1_START_OFFSET = 400;
    private static final int SPECIAL_DATA_2_START_OFFSET = 408;
    private static final int SPECIAL_DATA_3_START_OFFSET = 416;
    private static final int SPECIAL_DATA_4_START_OFFSET = 424;
    private static final int SPECIAL_DATA_5_START_OFFSET = 432;
    private static final int SPECIAL_DATA_6_START_OFFSET = 440;
    private static final int SPECIAL_DATA_7_START_OFFSET = 448;
    private static final int SPECIAL_DATA_8_START_OFFSET = 456;
    private static final int SPECIAL_DATA_9_START_OFFSET = 464;
    private static final int SPECIAL_DATA_10_START_OFFSET = 472;
    private static final int SPECIAL_DATA_11_START_OFFSET = 480;
    private static final int SPECIAL_DATA_12_START_OFFSET = 488;
    //The last two entries do not actually follow the usual pattern of offset/size. It's instead 2 different offsets.
    private static final int LAST_OFFSET = 504;
    private static final int SECTOR_SIZE = 2048;
    private static final Logger log = Logger.getLogger(MDTFileParser.class.getName());
    private PointerTable pointerTable = new PointerTable();;
    private String inputFilePath;
    private String outputFilePath;
    
    public MDTFileReconstructor inputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
        return this;
    }
    
    public MDTFileReconstructor outputFilePath(String outputFilePath) {
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
     * Reconstructs the Saturn MDT Files from their 5 individual pieces. Will do this for all
     * files in the Input directory. Files are written out to the output directory.
     */
    public void reconstruct() {
        if(inputFilePath == null) {
            log.log(Level.WARNING, "Input Files path are null, aborting parsing.");
        } else {
            
            //Read in our files
            Map<String, File> headerFiles = new HashMap<>();
            FileUtils.populateFileMap(headerFiles, inputFilePath, ".HEADER");
            
            Map<String, File> preScriptFiles = new HashMap<>();
            FileUtils.populateFileMap(preScriptFiles, inputFilePath, ".PRESCRIPT");
            
            Map<String, File> scriptHeaderFiles = new HashMap<>();
            FileUtils.populateFileMap(scriptHeaderFiles, inputFilePath, ".SCRIPTHEADER");
            
            Map<String, File> scriptFiles = new HashMap<>();
            FileUtils.populateFileMap(scriptFiles, inputFilePath, ".SCRIPT");
            
            Map<String, File> gfx1Files = new HashMap<>();
            FileUtils.populateFileMap(gfx1Files, inputFilePath, ".GFX1");
            
            Map<String, File> asmFiles = new HashMap<>();
            FileUtils.populateFileMap(asmFiles, inputFilePath, ".ASM");
            
            Map<String, File> gfx2Files = new HashMap<>();
            FileUtils.populateFileMap(gfx2Files, inputFilePath, ".GFX2");
            
            Map<String, File> gfx3Files = new HashMap<>();
            FileUtils.populateFileMap(gfx3Files, inputFilePath, ".GFX3");
            
            Map<String, File> graphicsFiles = new HashMap<>();
            FileUtils.populateFileMap(graphicsFiles, inputFilePath, ".GRAPHICS");
            
            Map<String, File> footerFiles = new HashMap<>();
            FileUtils.populateFileMap(footerFiles, inputFilePath, ".FOOTER");
            
            //For each file, we get it's remaining parts, calculate the new pointer table, and put it back together.
            for(String key : headerFiles.keySet()) {
                try {
                    
                    //Parse in the pointer table.
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] headerBytes = Files.readAllBytes(headerFiles.get(key).toPath());
                    pointerTable.parsePointerTableFromByteArray(headerBytes, true, true);
                    PointerTableEntry scriptHeaderEntry = pointerTable.getPointerTableEntry(SCRIPT_HEADER_OFFSET);
                    PointerTableEntry scriptEntry = pointerTable.getPointerTableEntry(SCRIPT_OFFSET);
                    PointerTableEntry asmEntry = pointerTable.getPointerTableEntry(ASM_CODE_OFFSET);
                    //printPointerTable();
                    
                    //Read the rest of the file pieces in.
                    byte[] preScriptBytes = Files.readAllBytes(preScriptFiles.get(key).toPath());
                    byte[] scriptHeaderBytes = Files.readAllBytes(scriptHeaderFiles.get(key).toPath());
                    byte[] graphicsBytes = Files.readAllBytes(graphicsFiles.get(key).toPath());
                    byte[] footerBytes = Files.readAllBytes(footerFiles.get(key).toPath());
                    
                    //Determine the new script size.
                    byte[] scriptBytes = Files.readAllBytes(scriptFiles.get(key).toPath());
                    int newScriptSize = scriptBytes.length;
                    
                    //Determine the new asm size.
                    int newAsmSize = 0;
                    byte[] asmBytes = null;
                    byte[] gfx1Bytes = null;
                    byte[] gfx2Bytes = null;
                    byte[] gfx3Bytes = null;
                    int gfx1Size = 0;
                    int gfx2Size = 0;
                    int gfx3Size = 0;
                    if(asmFiles.containsKey(key)) {
                        asmBytes = Files.readAllBytes(asmFiles.get(key).toPath());
                        newAsmSize = asmBytes.length;
                        
                        gfx1Bytes = Files.readAllBytes(gfx1Files.get(key).toPath());
                        gfx1Size = gfx1Bytes.length;
                        if(gfx2Files.containsKey(key)) {
                            gfx2Bytes = Files.readAllBytes(gfx2Files.get(key).toPath());
                            gfx2Size = gfx2Bytes.length;
                        }
                        if(gfx3Files.containsKey(key)) {
                            gfx3Bytes = Files.readAllBytes(gfx3Files.get(key).toPath());
                            gfx3Size = gfx3Bytes.length;
                        }
                        
                    }
                    
                    //Determine the difference in size between the old script and the new script.
                    int sizeDiff = scriptBytes.length - scriptEntry.getSize();
                    if(sizeDiff < 0) {
                        
                        sizeDiff = 0;
                        ByteBuffer newScript = ByteBuffer.allocate(scriptEntry.getSize());
                        newScript.put(scriptBytes);
                        scriptBytes = newScript.array();
                        newScriptSize = scriptBytes.length;
                    }
                    scriptEntry.setSize(newScriptSize);
                    
                    //Determine the difference in size between the old asm and the new asm.
                    int asmSize = asmEntry.getSize();
                    int asmSizeDiff = 0;
                    if(!Integer.toHexString(asmSize).equals("ffffffff") && asmBytes != null) {
                        asmSizeDiff = asmBytes.length - asmEntry.getSize();
                        if(asmSizeDiff < 0) {
                            
                            asmSizeDiff = 0;
                            ByteBuffer newAsm = ByteBuffer.allocate(asmEntry.getSize());
                            newAsm.put(asmBytes);
                            asmBytes = newAsm.array();
                            newAsmSize = asmBytes.length;
                        }
                        asmEntry.setSize(newAsmSize);
                    }
                    
                    //Prepare for calculating new sector Size.
                    int originalSectorSize = pointerTable.getPointerTableEntry(SECTOR_SIZE_OFFSET_A).getOffset();
                    int finalSectorSize = originalSectorSize;
                    int paddingSize = 0;
                    int sectorSizeDiff = 0;
                    //The data must end on a value divisible by 2048 so it fits cleanly into CD Sectors.
                    int finalDataSize = 0;
                    if(asmBytes == null) {
                        finalDataSize = headerBytes.length + preScriptBytes.length + scriptHeaderBytes.length + scriptBytes.length + graphicsBytes.length;
                    } else {
                        finalDataSize = headerBytes.length + preScriptBytes.length + scriptHeaderBytes.length + scriptBytes.length + gfx1Size + asmBytes.length + gfx2Size + gfx3Size;
                    }
                    //Find out how far away we are from the end of the next sector.
                    int sectorRemainder = finalDataSize % SECTOR_SIZE;
                    int distanceToNearestsector = SECTOR_SIZE - sectorRemainder;
                   
                    //Round our data size up to the nearest sector.
                    finalDataSize += distanceToNearestsector;
                    
                    //Finally determine how many sectors our data takes.
                    finalSectorSize = finalDataSize / SECTOR_SIZE;
                    
                    //Since there was old padding data, we need to figure out exactly how much more padding, if any, we need to add.
                    paddingSize = distanceToNearestsector;
                    sectorSizeDiff = finalSectorSize - originalSectorSize;
                    
                    
                    //Calculate the new pointer table entries.
                    ByteBuffer bb = ByteBuffer.allocate(4);
                    for(int i = 0; i < SECTOR_SIZE_OFFSET_A; i +=8) {
                        
                        PointerTableEntry pte = pointerTable.getPointerTableEntry(i);
                        switch(i) {
                            //For the Script and Script header entries we keep the sizes we've already set.
                            case SCRIPT_HEADER_OFFSET:
                                bb.putInt(0, scriptHeaderEntry.getOffset());
                                out.write(bb.array());
                                bb.putInt(0, scriptHeaderEntry.getSize());
                                out.write(bb.array());
                                break;
                            case SCRIPT_OFFSET:
                                bb.putInt(0, scriptEntry.getOffset());
                                out.write(bb.array());
                                bb.putInt(0, scriptEntry.getSize());
                                out.write(bb.array());
                                break;
                            //If offset 0x20, leave it alone    
                            case OFFSET_0X20:
                                bb.putInt(0, pte.getOffset());
                                out.write(bb.array());
                                bb.putInt(0, pte.getSize());
                                out.write(bb.array());
                                break;
                                //If offset 0x68, leave it alone    
                            case OFFSET_0X68:
                                bb.putInt(0, pte.getOffset());
                                out.write(bb.array());
                                bb.putInt(0, pte.getSize());
                                out.write(bb.array());
                                break;
                            //For all other entries, we update their entires if they come after the script in the file.
                            default:
                                if(!Integer.toHexString(pte.getOffset()).equals("ffffffff")) {
                                    if(pte.getOffset() > scriptEntry.getOffset()) {
                                        pte.setOffset(pte.getOffset() + sizeDiff);
                                    } else if(pte.getOffset() > asmEntry.getOffset()) {
                                        pte.setOffset(pte.getOffset() + sizeDiff + asmSizeDiff);
                                    }
                                }
                                bb.putInt(0, pte.getOffset());
                                out.write(bb.array());
                                bb.putInt(0, pte.getSize());
                                out.write(bb.array());
                                break;
                        }
                    }
                    
                    for(int i = SECTOR_SIZE_OFFSET_A; i < 512; i+=8) {
                        PointerTableEntry pte = pointerTable.getPointerTableEntry(i);
                        switch(i) {
                            // We need to update both Sector Size offsets And the Last offset correctly.
                            case SECTOR_SIZE_OFFSET_A:
                                pte.setOffset(finalSectorSize);
                                bb.putInt(0, pte.getOffset());
                                out.write(bb.array());
                                bb.putInt(0, pte.getSize());
                                out.write(bb.array());
                                break;
                            case SECTOR_SIZE_OFFSET_B:
                                pte.setSize(finalSectorSize);
                                bb.putInt(0, pte.getOffset());
                                out.write(bb.array());
                                bb.putInt(0, pte.getSize());
                                out.write(bb.array());
                                break;
                            case LAST_OFFSET:
                                if(!Integer.toHexString(pte.getOffset()).equals("ffffffff")
                                        && !Integer.toHexString(pte.getSize()).equals("ffffffff")) {
                                    pte.setOffset(pte.getOffset() + sizeDiff + asmSizeDiff);
                                    pte.setSize(pte.getSize() + sizeDiff + asmSizeDiff);
                                    bb.putInt(0, pte.getOffset());
                                    out.write(bb.array());
                                    bb.putInt(0, pte.getSize());
                                    out.write(bb.array());
                                }
                                break;
                            //Next, if any of our special data offsets are not 0xffffffff, then we need to update the offset sector value.
                            case SPECIAL_DATA_1_START_OFFSET:
                            case SPECIAL_DATA_2_START_OFFSET:
                            case SPECIAL_DATA_3_START_OFFSET:
                            case SPECIAL_DATA_4_START_OFFSET:
                            case SPECIAL_DATA_5_START_OFFSET:
                            case SPECIAL_DATA_6_START_OFFSET:
                            case SPECIAL_DATA_7_START_OFFSET:
                            case SPECIAL_DATA_8_START_OFFSET:
                            case SPECIAL_DATA_9_START_OFFSET:
                            case SPECIAL_DATA_10_START_OFFSET:
                            case SPECIAL_DATA_11_START_OFFSET:
                            case SPECIAL_DATA_12_START_OFFSET:
                                if(!Integer.toHexString(pte.getOffset()).equals("ffffffff")) {
                                    pte.setOffset(pte.getOffset() + sectorSizeDiff);
                                }
                                bb.putInt(0, pte.getOffset());
                                out.write(bb.array());
                                bb.putInt(0, pte.getSize());
                                out.write(bb.array());
                                break;
                            //Any other values we leave alone.    
                            default:
                                bb.putInt(0, pte.getOffset());
                                out.write(bb.array());
                                bb.putInt(0, pte.getSize());
                                out.write(bb.array());
                                break;
                        }
                    }
                    
                    //Write them to the output stream.
                    out.write(preScriptBytes);
                    out.write(scriptHeaderBytes);
                    out.write(scriptBytes);
                    
                    if(asmBytes == null) {
                        out.write(graphicsBytes);
                    } else {
                        out.write(gfx1Bytes);
                        out.write(asmBytes);
                        if(gfx2Bytes != null) {
                            out.write(gfx2Bytes);
                        }
                        if(gfx3Bytes != null) {
                            out.write(gfx3Bytes);
                        }
                    }
                    
                    //If we need to add padding to get the footer to start in the next sector, add it.
                    if(paddingSize != 0) {
                        byte[] paddingBytes = new byte[paddingSize];
                        out.write(paddingBytes);
                    }
                    out.write(footerBytes);
                    
                    //Write out the new MDT file.
                    FileUtils.writeToFile(out.toByteArray(), key, ".MDT", ".MDT", outputFilePath);
                    
                } catch (IOException e) {
                    log.log(Level.SEVERE, "Caught IOException attempting to read bytes.", e);
                    e.printStackTrace();
                }
            }
        }
    }

}
