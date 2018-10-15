package com.grandia.file.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * 
 * FileUtils Class.
 * 
 * This is a general utilities class for common file operations.
 * 
 * @author TrekkiesUnite118
 *
 */
public abstract class FileUtils {
    
    private static final Logger log = Logger.getLogger(FileUtils.class.getName());
    
    /**
     * Creates a list of files from the passed in file path and file extension.
     * 
     * @param fileList
     * @param path
     * @param extension
     */
    public static void populateFileArray(List<File> fileList, String path, String extension) {
        File dir = new File(path);
        
        File[] dirArray = dir.listFiles();
        
        for(File f : dirArray) {
            if(f.getName().endsWith(extension)) {
                fileList.add(f);
            }
        }
    }
    
    /**
     * Creates a map of files from the passed in file path and extension. 
     * 
     * Returned map uses the file name for the Key.
     * 
     * @param fileMap
     * @param path
     * @param extension
     */
    public static void populateFileMap(Map<String, File> fileMap, String path, String extension) {
        File dir = new File(path);
        
        File[] dirArray = dir.listFiles();
        
        for(File f : dirArray) {
            if(f.getName().endsWith(extension)) {
                fileMap.put(f.getName().replaceAll(extension, ""), f);
            }
        }
    }
    
    /**
     * Writes the passed in byte array to a file in the output directory.
     * 
     * @param byteArray - Byte Array to write.
     * @param fileName - File Name to use in the output file.
     * @param fileExtension - File Extension of the original file if applicable.
     * @param newExtension - File Extension to use in the output file.
     * @param outputFilePath - Destination directory where you want the files written to.
     */
    public static void writeToFile(byte[] byteArray, String fileName, String fileExtension, String newExtension, String outputFilePath) {
        
        if(fileName.contains(fileExtension)) {
            fileName = fileName.replace(fileExtension, newExtension);
        }else {
            fileName = fileName + newExtension;
        }
        Path path = Paths.get(outputFilePath + fileName);
        try {
            Files.write(path, byteArray);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Caught IOException attempting to write bytes to file.", e);
            e.printStackTrace();
        }
    }
    
    /**
     * Writes the passed in byte array to a file. This one does not replace the passed in file names 
     * file extension. Use this if you don't need the file extension replaced.
     * 
     * @param byteArray
     * @param fileName
     * @param outputFilePath
     */
    public static void writeToFile(byte[] byteArray, String fileName, String outputFilePath) {
        Path path = Paths.get(outputFilePath + fileName);
        try {
            Files.write(path, byteArray);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Caught IOException attempting to write bytes to file.", e);
            e.printStackTrace();
        }
    }

}
