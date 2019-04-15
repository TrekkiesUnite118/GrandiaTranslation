package com.grandia.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.grandia.file.structure.PointerTableEntry;
import com.grandia.file.structure.ScriptPointerTable;
import com.grandia.file.utils.FileUtils;

/**
 * 
 * ScriptReconstructor
 * 
 * This class has methods for reconstructing the Script from it's individual pieces and update the Script Header in
 * the event that any pieces have changed in size.
 * 
 * @author TrekkiesUnite118
 *
 */
public class ScriptReconstructor {
    
    private String inputScriptFilePath;
    private String inputScriptHeaderFilePath;
    private String outputFilePath;
    private Map<String, File> fileMap = new HashMap<>();
    
    private static final Logger log = Logger.getLogger(ScriptReconstructor.class.getName());
    
    private ScriptPointerTable pointerTable = new ScriptPointerTable();

    public ScriptReconstructor inputScriptFilePath(String inputScriptFilePath) {
        this.inputScriptFilePath = inputScriptFilePath;
        return this;
    }
    
    public ScriptReconstructor inputScriptHeaderFilePath(String inputScriptHeaderFilePath) {
        this.inputScriptHeaderFilePath = inputScriptHeaderFilePath;
        return this;
    }
    
    public ScriptReconstructor outputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
        return this;
    }
    
    /**
     * Reconstruct method
     * 
     * This method reads in the Script Header and then pieces together the Script File from it's indivdiual pieces.
     * It will also update the Script header in the event than any pieces have changed in size.
     */
    public void reconstruct() {
        
        if(inputScriptFilePath == null || inputScriptHeaderFilePath == null) {
            log.log(Level.WARNING, "Input File path is null, aborting parsing.");
        } else {
            
            //Read the directory to find out what files we need to parse
            FileUtils.populateFileMap(fileMap, inputScriptFilePath, "");
            File header = new File(inputScriptHeaderFilePath);
            List<String> fileNames = new ArrayList<>();
            
            //Create ByteArrayOutputStreams
            ByteArrayOutputStream outHeader = new ByteArrayOutputStream();
            ByteArrayOutputStream outScript = new ByteArrayOutputStream();
            
            try {
                //Read in the header file and create the pointer table.
                byte[] headerByteArray = Files.readAllBytes(header.toPath());
                pointerTable.parsePointerTableFromByteArray(headerByteArray, true);
                
                //For each entry in the pointer table, update the size if it's changed.
                for(int i = 0; i < pointerTable.getSize(); i++) {
                    PointerTableEntry pte = pointerTable.getPointerTableEntry(i);
                    
                    if(!Integer.toHexString(pte.getId()).equals("ffff")) {
                        if(!fileNames.contains(pte.getStringId())) {
                            fileNames.add(pte.getStringId());
                        }else {
                            pte.setStringId(pte.getStringId() + i);
                            fileNames.add(pte.getStringId());
                            pointerTable.addOrUpdatePointerTableEntry(i, pte);
                        }
                        File file = fileMap.get(pte.getStringId());
                        int size = Math.toIntExact(file.length());
                        pte.setSize(size);
                        pointerTable.addOrUpdatePointerTableEntry(i, pte);
                    }
                }
                
                //Recalculate the offsets.
                pointerTable.recacluateOffsets();
                
                //Create a byte buffer for writing the new table entries.
                ByteBuffer bb = ByteBuffer.allocate(2);
                //For each entry in the Pointer Table, write it to the byte buffer then write it to the output stream.
                for(int i = 0; i < pointerTable.getSize(); i++) {
                    
                    PointerTableEntry pte = pointerTable.getPointerTableEntry(i);
                    
                    byte[] idByte = new byte [2];
                    idByte[1] = (byte) (pte.getId() & 0xFF);
                    idByte[0] = (byte) ((pte.getId() >> 8) & 0xFF);
                    bb.put(idByte);
                    outHeader.write(bb.array());
                    
                    bb.position(0);
                    byte[] offsetByte = new byte [2];
                    offsetByte[1] = (byte) (pte.getOffset() & 0xFF);
                    offsetByte[0] = (byte) ((pte.getOffset() >> 8) & 0xFF);
                    bb.put(offsetByte);
                    outHeader.write(bb.array());
                    bb.position(0);
                }
                
                //Write out the new Script Header file.
                FileUtils.writeToFile(outHeader.toByteArray(), header.getName(), outputFilePath);
                
                //For each entry in the pointer table, read in it's corresponding script piece file and write it to the output stream.
                for(int i = 0; i < pointerTable.getSize(); i++) {
                    PointerTableEntry pte = pointerTable.getPointerTableEntry(i);
                    if(!Integer.toHexString(pte.getId()).equals("ffff")) {
                        File f = fileMap.get(pte.getStringId());
                        byte[] fBytes = Files.readAllBytes(f.toPath());
                        outScript.write(fBytes);
                    }
                }
                
                //Write out the new Script file.
                FileUtils.writeToFile(outScript.toByteArray(), header.getName(), ".SCRIPTHEADER", ".SCRIPT", outputFilePath);
                
            } catch (IOException e) {
                log.log(Level.SEVERE, "Caught IOException attempting to read bytes.", e);
                e.printStackTrace();
            }
            
        }
    }
    
}
