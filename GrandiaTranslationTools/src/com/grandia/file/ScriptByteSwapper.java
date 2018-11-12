package com.grandia.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.grandia.file.utils.FileUtils;

/**
 * 
 * ScriptByteSwapper
 * 
 *  This class will take the extracted Script files from the Playstation version
 * of Grandia and swap their byte order from Little Endian to Big Endian. This is necessary for 
 * the script to be usable in the Saturn version.
 * 
 * @author TrekkiesUnite118
 *
 */
public class ScriptByteSwapper {

    private static final Logger log = Logger.getLogger(ScriptByteSwapper.class.getName());
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private List<File> fileList = new ArrayList<>();
    private String inputFilePath;
    private String outputFilePath;
    private String fileExtension;
    
    public ScriptByteSwapper inputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
        return this;
    }
    
    public ScriptByteSwapper outputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
        return this;
    }
    
    public ScriptByteSwapper fileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
        return this;
    }
    
    public List<File> getFileList() {
        return fileList;
    }

    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
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

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }
    
    /**
     * Swaps the byte order of the files it reads in from the inputFilePath.
     * Currently only supports changing from Little Endian to Big Endian.
     * 
     */
    public void swapBytes() {
        if(inputFilePath == null) {
            log.log(Level.WARNING, "Input File path is null, aborting parsing.");
        } else {
            populateFileArray();
            for(File f : fileList) {
                try {
                    byte[] fileBytes = Files.readAllBytes(f.toPath());
                    fileBytes = converting(fileBytes);
                    FileUtils.writeToFile(fileBytes, f.getName(), outputFilePath);
                }catch (IOException e) {
                    log.log(Level.SEVERE, "Caught IOException attempting to read bytes.", e);
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Swaps the byte order for the text portions of the script back to their original order.
     * 
     * The swapBytes method swaps the entire file's order, however the text and control code
     * portions of the script were already in the proper order. So this method will switch
     * those portions back to their proper order.
     */
    public void swapTextBytes() {
        if(inputFilePath == null) {
            log.log(Level.WARNING, "Input File path is null, aborting parsing.");
        } else {
            populateFileArray();
            for(File f : fileList) {
                try {
                    byte[] fileBytes = Files.readAllBytes(f.toPath());
                    ByteBuffer.wrap(fileBytes);
                    int length = fileBytes.length;

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    int i = 0;
                    while (i + 1 < length) {
                        byte[] sizeByte = new byte[2];
                        sizeByte[0] = fileBytes[i];
                        sizeByte[1] = fileBytes[i+1];
                        
                        out.write(sizeByte, 0, sizeByte.length);
                        
                        //There's one edge case that happens where we can mistake data for a size value and throw the whole thing off.
                        //To account for that we need to check if the next bytes are 0xF000. If they are, we do nothing and move on.
                        byte[] nextByte = new byte[2];
                        if((i+2 < length) && (i+3 < length)) {
                            nextByte[0] = fileBytes[i+2];
                            nextByte[1] = fileBytes[i+3];
                        }
                        String nextByteString = bytesToHex(nextByte);
                        
                        String hexValue = bytesToHex(sizeByte);
                        log.log(Level.INFO, "HexValue: " + hexValue);
                        if((hexValue.startsWith("9") || hexValue.startsWith("2")) && !nextByteString.equals("F000")) {
                            log.log(Level.INFO, "HexValue Matched!");
                            int size = Integer.parseInt(hexValue.substring(1), 16);
                            log.log(Level.INFO, "Size is : " + size);
                            byte[] textPortion = new byte[size];
                            new String(textPortion);
                            if(i + 2 < length) {
                                copyBytes(fileBytes, textPortion, i+2, 0, size);
                                log.log(Level.INFO, "TextPortion: " + new String(textPortion));
                                textPortion = converting(textPortion);
                                log.log(Level.INFO, "Converted TextPortion: " + new String(textPortion));
                                
                                out.write(textPortion, 0, size);
                            }
                            
                            size +=2;
                            i+=size;
                        }else {
                            i+=2;
                        }
                        
                        
                    }
            
                    FileUtils.writeToFile(out.toByteArray(), f.getName(), outputFilePath);
                }catch (IOException e) {
                    log.log(Level.SEVERE, "Caught IOException attempting to read bytes.", e);
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void copyBytes(byte[] from, byte[] to, int startFrom , int startTo, int size) {
    
        for(int i = startFrom; i <= startFrom + size ; i++) {
            if(i < from.length && startTo < to.length) {
                //System.out.println("Copying Bytes...");
                to[startTo] = from[i];
                startTo++;
            }
        }
        
    }

    private static byte[] converting(byte[] value) {
        final int length = value.length;
        byte[] res = new byte[length];
        int i = 0;
        while(i < length) {
            if(i+1 >= length) {
                res[i] = value[i];
            }else {
                res[i] = value[i+1];
                res[i+1] = value[i];
            }
            
            i += 2;
        }
        return res;
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
    
    private void populateFileArray() {
        FileUtils.populateFileArray(fileList, inputFilePath, fileExtension);
    }
}
