package com.grandia.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.grandia.file.structure.PointerTableEntry;
import com.grandia.file.structure.ScriptPointerTable;
import com.grandia.file.utils.FileUtils;

/**
 * 
 * ScriptParser
 * 
 * This class is able to take the Script Header and break the script down further into its individual
 * text sequences. This is to make it easier to look at different parts of the script.
 * 
 * @author TrekkiesUnite118
 *
 */
public class ScriptParser {
    
    private String inputScriptFilePath;
    private String inputScriptHeaderFilePath;
    private String outputFilePath;
    
    private static final Logger log = Logger.getLogger(ScriptParser.class.getName());
    
    private ScriptPointerTable pointerTable = new ScriptPointerTable();
    
    public ScriptParser inputScriptFilePath(String inputScriptFilePath) {
        this.inputScriptFilePath = inputScriptFilePath;
        return this;
    }
    
    public ScriptParser inputScriptHeaderFilePath(String inputScriptHeaderFilePath) {
        this.inputScriptHeaderFilePath = inputScriptHeaderFilePath;
        return this;
    }
    
    public ScriptParser outputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
        return this;
    }
    
    /**
     * Parse method
     * 
     * This method will parse the script into its indivdiual pieces based on the information obtained
     * from the script header file.
     */
    public void parse() {
        if(inputScriptFilePath == null || inputScriptHeaderFilePath == null) {
            log.log(Level.WARNING, "Input File path is null, aborting parsing.");
        } else {
            File header = new File(inputScriptHeaderFilePath);
            File script = new File(inputScriptFilePath);
            
            List<String> fileNames = new ArrayList<>();
            
            try {
                byte[] headerByteArray = Files.readAllBytes(header.toPath());
                byte[] scriptByteArray = Files.readAllBytes(script.toPath());
                pointerTable.parsePointerTableFromByteArray(headerByteArray, true);
                
                for(int i = 0; i < pointerTable.getSize(); i++) {
                    if(i+1 != pointerTable.getSize()) {
                        PointerTableEntry pte = pointerTable.getPointerTableEntry(i);
                        if(!Integer.toHexString(pte.getId()).equals("ffff")) {
                            if(!fileNames.contains(pte.getStringId())) {
                                fileNames.add(pte.getStringId());
                            }else {
                                pte.setStringId(pte.getStringId() + i);
                                fileNames.add(pte.getStringId());
                            }
                            if(pte.getSize() == 0) {
                                pte.setSize(scriptByteArray.length - pte.getOffset());
                            }
                            byte[] scriptPieceArray = Arrays.copyOfRange(scriptByteArray, pte.getOffset(), pte.getOffset() + pte.getSize());
                            FileUtils.writeToFile(scriptPieceArray, pte.getStringId(), outputFilePath + "\\");
                        }
                    }
                }
                
                
            } catch (IOException e) {
                log.log(Level.SEVERE, "Caught IOException attempting to read bytes.", e);
                e.printStackTrace();
            }
            
            
        }
    }
    
    @SuppressWarnings("unused")
    private void printPointerTable() {
        System.out.println(pointerTable.toString());
    }
}
