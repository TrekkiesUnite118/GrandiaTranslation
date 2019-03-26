package com.grandia.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.grandia.file.structure.PortraitCode;
import com.grandia.file.utils.FileUtils;

/**
 * ScriptPortraitCorrector
 * 
 * The PS1 version changed the portrait codes for some of the script files.
 * This code attempts to compare each script file to it's Original Japanese Saturn counterpart
 * and correct the changed portrait codes to align with the Original Saturn version.
 * 
 * This is designed so it can go back and forth, so an variable with truth in it's 
 * name is intended to be the the script you want your new script to match. Input 
 * refers to your new script.
 * 
 * So for taking PS1 scripts into the Saturn it's set up like this:
 * 
 * Saturn = Truth
 * PS1 = Input
 * 
 * @author TrekkiesUnite118
 *
 */
public class ScriptPortraitCorrector {
    
    private static final Logger log = Logger.getLogger(ScriptPortraitCorrector.class.getName());
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    
    //Portrait Control Code
    private static String PORTRAIT_CODE = "0F";
    
    //Portait Alignment Codes
    private static String PORTRAIT_CODE_LEFT = "03";
    private static String PORTRAIT_CODE_MIDDLE = "12";
    private static String PORTRAIT_CODE_RIGHT = "21";
    private static String VERTICAL_ALIGN_CODE = "0A";
    
    private String truthFilePath;
    private String inputFilePath;
    private String outputFilePath;
    private Map<String, File> truthFileMap = new HashMap<>();
    private Map<String, File> inputFileMap = new HashMap<>();

    private String fileExtension = ".SCRIPT";
    
    
    public ScriptPortraitCorrector truthFilePath(String truthFilePath) {
        this.truthFilePath = truthFilePath;
        return this;
    }
    
    public ScriptPortraitCorrector inputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
        return this;
    }
    
    public ScriptPortraitCorrector outputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
        return this;
    }
    
    public String getTruthFilePath() {
        return truthFilePath;
    }

    public void setTruthFilePath(String truthFilePath) {
        this.truthFilePath = truthFilePath;
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

    public Map<String, File> getTruthFileMap() {
        return truthFileMap;
    }

    public void setTruthFileMap(Map<String, File> truthFileMap) {
        this.truthFileMap = truthFileMap;
    }

    public Map<String, File> getInputFileMap() {
        return inputFileMap;
    }

    public void setInputFileMap(Map<String, File> inputFileMap) {
        this.inputFileMap = inputFileMap;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    /**
     * Scans the files for portrait codes, updates the input file's codes to match the 
     * Truth codes if they don't match.
     */
    public void scanForPortraitCodes() {
        
        if(inputFilePath == null && truthFilePath == null) {
            log.log(Level.WARNING, "Input Files path are null, aborting parsing.");
        } else {
            populateFileArrays();
            for(String key : truthFileMap.keySet()) {
                log.log(Level.INFO, "Comparing Script: " + key);
                
                //Setting up maps.
                File truth = truthFileMap.get(key);
                File input = inputFileMap.get(key);
                Map<Integer, PortraitCode> truePortraitCodes = new HashMap<>();
                Map<Integer, PortraitCode> inputPortraitCodes = new HashMap<>(); 
                try {
                    
                    byte[] truthBytes = Files.readAllBytes(truth.toPath());
                    byte[] inputBytes = Files.readAllBytes(input.toPath());
                    
                    int length = truthBytes.length;
                    int inputLength = inputBytes.length;
                    
                    //Counters for portraits found.
                    int trueCodeNum = 0;
                    int inputCodeNum = 0;
                    
                    int i = 0;
                    //Check each byte of the truth file for portrait codes, if you find one add it to the map.
                    while (i + 1 < length) {
                        byte[] codeByte = new byte[1];
                        codeByte[0] = truthBytes[i];
                        
                        byte[] posByte = new byte[1];
                        if( i + 1 != length) {
                            posByte[0] = truthBytes[i+1];
                        } 
                        
                        byte[] vAlignByte = new byte[1];
                        if(i+2 != length) {
                            vAlignByte[0] = truthBytes[i+2];
                        }
                        
                        String codeString = bytesToHex(codeByte);
                        String posString = bytesToHex(posByte);
                        String vAlignString = bytesToHex(vAlignByte);
                        
                        // 0x0F isn't enough, check that the alignment code is there too.
                        if(codeString.equals(PORTRAIT_CODE) && checkPosString(posString) && checkVAlignString(vAlignString)) {
                            log.log(Level.INFO, "Found True Portrait Code!: " + codeString);
                            byte[] portraitCode = new byte[6];
                            
                            portraitCode[0] = truthBytes[i];
                            portraitCode[1] = truthBytes[i + 1];
                            portraitCode[2] = truthBytes[i + 2];
                            portraitCode[3] = truthBytes[i + 3];
                            portraitCode[4] = truthBytes[i + 4];
                            portraitCode[5] = truthBytes[i + 5];
                            
                            PortraitCode trueCode = new PortraitCode(portraitCode, i);
                            truePortraitCodes.put(trueCodeNum, trueCode);
                            i += 6;
                            trueCodeNum++;
                            
                        } else {
                            i++;
                        }
                    }
                    
                    int j = 0;
                    // Check each byte of the input file for portrait codes. if you find one add it to the map.
                    while (j + 1 < inputLength) {
                        byte[] codeByte = new byte[1];
                        codeByte[0] = inputBytes[j];
                        
                        byte[] posByte = new byte[1];
                        if( j + 1 != inputLength) {
                            posByte[0] = inputBytes[j+1];
                        } 
                        
                        byte[] vAlignByte = new byte[1];
                        if(j + 2 != inputLength) {
                            vAlignByte[0] = inputBytes[j+2];
                        }
                        
                        String codeString = bytesToHex(codeByte);
                        String posString = bytesToHex(posByte);
                        String vAlignString = bytesToHex(vAlignByte);
                        
                        // 0x0F isn't enough, check that the alignment code is there too.
                        if(codeString.equals(PORTRAIT_CODE) && checkPosString(posString) && checkVAlignString(vAlignString)) {
                            log.log(Level.INFO, "Found Input Portrait Code!: " + codeString);
                            byte[] portraitCode = new byte[6];
                            
                            portraitCode[0] = inputBytes[j];
                            portraitCode[1] = inputBytes[j + 1];
                            portraitCode[2] = inputBytes[j + 2];
                            portraitCode[3] = inputBytes[j + 3];
                            portraitCode[4] = inputBytes[j + 4];
                            portraitCode[5] = inputBytes[j + 5];
                            
                            PortraitCode inputCode = new PortraitCode(portraitCode, j);
                            inputPortraitCodes.put(inputCodeNum, inputCode);
                            j += 6;
                            inputCodeNum++;
                        }else {
                            j++;
                        }
                    }
                    
                    //If we found the same number of portrait codes, attempt to update.
                    if(inputCodeNum == trueCodeNum) {
                        log.log(Level.INFO, "Number of Portraits Match: " + trueCodeNum);
                        inputBytes = compareAndUpdateCodes(truePortraitCodes, inputPortraitCodes, inputBytes);
                    }
                    
                    //Write out to file.
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    out.write(inputBytes);
                    log.log(Level.INFO, "Writing to file...");
                    FileUtils.writeToFile(out.toByteArray(), input.getName(), outputFilePath);
                } catch (IOException e) {
                    log.log(Level.SEVERE, "Caught IOException attempting to read bytes.", e);
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * CompareAndUpdateCodes
     * 
     * This method compares the portrait codes. If they don't match, we replace the code in the
     * byte array with the code from the Truth map.
     * 
     * @param trueCodes
     * @param inputCodes
     * @param inputFileBytes
     * @return update byte array
     */
    public byte[] compareAndUpdateCodes(Map<Integer, PortraitCode> trueCodes, Map<Integer, PortraitCode> inputCodes, byte[] inputFileBytes) {
        log.log(Level.INFO, "Comparing and updateing Portraits...");
        for(int i = 0; i < trueCodes.size(); i++) {
            PortraitCode truth = trueCodes.get(i);
            PortraitCode input = inputCodes.get(i);
            
            log.log(Level.INFO, "Truth: " + bytesToHex(truth.getCode()));
            log.log(Level.INFO, "Input: " + bytesToHex(input.getCode()));
            
            if(truth.getCode() != input.getCode()) {
                int inputOffset = input.getOffset();
                byte[] truthCode = truth.getCode();
                
                inputFileBytes[inputOffset] = truthCode[0];
                inputFileBytes[inputOffset + 1] = truthCode[1];
                inputFileBytes[inputOffset + 2] = truthCode[2];
                inputFileBytes[inputOffset + 3] = truthCode[3];
                inputFileBytes[inputOffset + 4] = truthCode[4];
                inputFileBytes[inputOffset + 5] = truthCode[5];
            }
        }
        
        log.log(Level.INFO, "Complete!");
        return inputFileBytes;
    }
   
    private boolean checkPosString(String posString) {
        if(posString.equals(PORTRAIT_CODE_LEFT) || posString.equals(PORTRAIT_CODE_MIDDLE) || posString.equals(PORTRAIT_CODE_RIGHT)) {
            return true;
        } else {
            return false;
        }
    }
    
    private boolean checkVAlignString(String vAlignString) {
        if(vAlignString.equals(VERTICAL_ALIGN_CODE)) {
            return true;
        } else {
            return false;
        }
    }
    
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    private void populateFileArrays() {
        FileUtils.populateFileMap(truthFileMap, truthFilePath, fileExtension);
        FileUtils.populateFileMap(inputFileMap, inputFilePath, fileExtension);
    }

}
