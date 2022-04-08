package com.grandia.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.grandia.file.utils.FileUtils;

public class VideoMuxer {
    
    //Where the data starts in the MOV file.
    private static final int DATA_START_OFFSET = 16384;
    //Location of table that says how big each chunk of video data (frames?).
    private static final int FRAME_TABLE_OFFSET = 640;
    //Initial chunk of ADX audio. Each one starts with 0x18000 bytes of ADX.
    private static final int INIT_ADX_CHUNK_SIZE = 98304;
    //Every chunk of video data is interleaved with 0x6000 bytes of ADX.
    private static final int ADX_CHUNK_SIZE = 24576;
    //Max Size of Frame Table.
    private static final int FRAME_TABLE_SIZE = 15744;
    
    //Each run of table entries for a chunk of video data encs with 0x000C
    private static final String HEX_END_FRAME_CHUNK = "c";
    /// Table ends with 0x0000
    private static final String HEX_END_FRAME_DATA = "0";
    
    //Brute force files.
    private static String inputFile = "D:\\FMVDecompression\\DemuxTest\\MOV13.MOV";
    private static String adxFile = "D:\\FMVDecompression\\DemuxTest\\MOV13.ADX";
    private static String outputVid = "D:\\FMVDecompression\\DemuxTest\\Frames13New\\";
    private static String output = "D:\\FMVDecompression\\MuxTest\\MOV13.MOV";
    
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    
 public static void main(String[] args) {
        
        File f = new File(inputFile);
        File fAdx = new File(adxFile);
        
      //Byte array for the file.
        try {
            List<String> numFramesSet = new ArrayList<>();
            
            byte[] MOVByteArray = Files.readAllBytes(f.toPath());
            
            byte[] quicktimeHeader = Arrays.copyOfRange(MOVByteArray, 0, FRAME_TABLE_OFFSET);
            
            byte[] frameTableArray = Arrays.copyOfRange(MOVByteArray, FRAME_TABLE_OFFSET, DATA_START_OFFSET);
            
            byte[] adxDataArray = Files.readAllBytes(fAdx.toPath());
            
            
            Map<String, File> frameFiles = new HashMap<>();
            FileUtils.populateFileMap(frameFiles, outputVid, "");
            
            int i = 0;
            int chunkNum = 0;
            int framecount = 0;
            int framesPerRun = 0;

            Map<Integer, Integer> frameDataHeaders = new HashMap<>();
            System.out.println("Reading Table...");
            while(i < frameTableArray.length) {
                ByteBuffer bb = ByteBuffer.wrap(frameTableArray, i, 4);
                int val = bb.getInt();
                String hexVal = Integer.toHexString(val);
                if(hexVal.startsWith(HEX_END_FRAME_CHUNK)) {
                    frameDataHeaders.put(chunkNum, val);
                    chunkNum++;
                    numFramesSet.add(Integer.toString(framesPerRun));
                    framesPerRun = 0;
                    i+=4;
                    
                } else if(hexVal.equals(HEX_END_FRAME_DATA)) {
                    numFramesSet.add(Integer.toString(framesPerRun));
                    framesPerRun = 0;
                    break;
                } else {
                    i+=4;
                    framecount++;
                    framesPerRun++;
                }
                
            }
            System.out.println("Done Reading Table...");
            
            ByteArrayOutputStream frameTableBaos = new ByteArrayOutputStream();
            
            int frameRate = 0;
            for(String fps : numFramesSet) {
                if(Integer.parseInt(fps) > frameRate) {
                    frameRate = Integer.parseInt(fps);
                }
            }
            
            frameRate = frameRate * 2;
            
            
            
            int frameNum = 0;
            for(int frameRuns = 0; frameRuns < frameDataHeaders.size(); frameRuns++) {

                int frameSectorSize = 0;
                List<byte[]> frameByteList = new ArrayList<>();
                for(int frameCounter = 0; frameCounter < frameRate; frameCounter++) {
                    if(frameNum < frameFiles.size()) {
                        File frameFile = frameFiles.get(Integer.toString(frameNum));
                        byte[] frameBytes = Files.readAllBytes(frameFile.toPath());
                        int remainder = frameBytes.length % 2048;
                        int paddingsize = 0;
                        if( remainder != 0) {
                            paddingsize = 2048 - remainder;
                        }
                        
                        short frameLength = (short) frameBytes.length;
                        frameSectorSize += frameLength + paddingsize;
                        ByteBuffer bb2 = ByteBuffer.allocate(2);
                        bb2.putShort(frameLength);
                        
                        frameByteList.add(bb2.array());
                        
                        frameNum++;
                    }
                }
                
                ByteBuffer bb = ByteBuffer.allocate(2);
                ByteBuffer bb3 = ByteBuffer.allocate(2);
                
                bb.putShort((short) 0x000C);
                short sectors = (short) (frameSectorSize / 2048);
                bb3.putShort(sectors);
                frameTableBaos.write(bb.array());
                frameTableBaos.write(bb3.array());
                
                for(int j = 0; j < frameByteList.size(); j++) {
                    frameTableBaos.write(frameByteList.get(j));
                }

                //System.out.println("Frame size: " + Integer.toHexString(frameSectorSize));
                System.out.println("Frame Sectors: " + Integer.toHexString(frameSectorSize / 2048));
                
            }

            
            byte[] frameTableByteArray = frameTableBaos.toByteArray();
            
            int paddingSize = FRAME_TABLE_SIZE - frameTableByteArray.length;
            
            byte[] padding = new byte[paddingSize];
            
            ByteArrayOutputStream vidBaos = new ByteArrayOutputStream();
            
            int currPos = 0;
            frameNum = 0;
            for(int j = 0; j < numFramesSet.size(); j++) {
                
                if(!(currPos > adxDataArray.length)) {
                    byte[] adxChunk;
                    if(j == 0) {
                        adxChunk = Arrays.copyOfRange(adxDataArray, currPos, currPos + INIT_ADX_CHUNK_SIZE);
                        currPos += INIT_ADX_CHUNK_SIZE;
                        vidBaos.write(adxChunk);
                    } else {
                        adxChunk = Arrays.copyOfRange(adxDataArray, currPos, currPos + ADX_CHUNK_SIZE);
                        currPos += ADX_CHUNK_SIZE;
                        vidBaos.write(adxChunk);
                    }
                }
               
                for(int frameCounter = 0; frameCounter < frameRate; frameCounter++) {
                    if(frameNum < frameFiles.size()) {
                        File frameFile = frameFiles.get(Integer.toString(frameNum));
                        byte[] frameBytes = Files.readAllBytes(frameFile.toPath());
                        
                        int remainder = frameBytes.length % 2048;
                        int paddingsize = 0;
                        if( remainder != 0) {
                            paddingsize = 2048 - remainder;
                        }
                        
                        byte[] paddingArray = new byte[paddingsize];
                        
                        
                        vidBaos.write(frameBytes);
                        vidBaos.write(paddingArray);
                        frameNum++;
                    }
                }
                
            }
            
            
            byte[] vidBytes = vidBaos.toByteArray();
            
            ByteArrayOutputStream outBaos = new ByteArrayOutputStream();
            
            outBaos.write(quicktimeHeader);
            outBaos.write(frameTableByteArray);
            outBaos.write(padding);
            outBaos.write(vidBytes);
            
            Path outPath = Paths.get(output);
            
            try {
                Files.write(outPath, outBaos.toByteArray());
                
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            

            Set<String> frameSet = new HashSet<>();
            for(String fpr : numFramesSet) {
                frameSet.add(fpr);
            }
            
            System.out.println("Remuxed " + framecount * 2 + " frames @" + frameRate  + "fps.");
            
            int minutes = numFramesSet.size() / 60;
            int seconds = numFramesSet.size() % 60;
            System.out.println("Video Length is " + minutes + " minutes and " + seconds + " seconds");
            
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
    
}
