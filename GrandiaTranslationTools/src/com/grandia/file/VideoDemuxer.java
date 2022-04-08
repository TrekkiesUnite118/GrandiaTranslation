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

/**
 * Very crude and brute force Video Demuxer for Grandia.
 * 
 * This will parse out the video data and the ADX Audio from Grandia's MOV files.
 * @author TrekkiesUnite118
 *
 */
public class VideoDemuxer {
    
    //Where the data starts in the MOV file.
    private static final int DATA_START_OFFSET = 16384;
    //Location of table that says how big each chunk of video data (frames?).
    private static final int FRAME_TABLE_OFFSET = 644;
    //Initial chunk of ADX audio. Each one starts with 0x18000 bytes of ADX.
    private static final int INIT_ADX_CHUNK_SIZE = 98304;
    //Every chunk of video data is interleaved with 0x6000 bytes of ADX.
    private static final int ADX_CHUNK_SIZE = 24576;
    
    //Each run of table entries for a chunk of video data encs with 0x000C
    private static final String HEX_END_FRAME_CHUNK = "c";
    /// Table ends with 0x0000
    private static final String HEX_END_FRAME_DATA = "0";
    
    //Brute force files. 
    private static String inputFile = "D:\\FMVDecompression\\DemuxTest\\MOV13.MOV";
    private static String outputAdx = "D:\\FMVDecompression\\DemuxTest\\MOV13.ADX";
    private static String outputVid = "D:\\FMVDecompression\\DemuxTest\\Frames13\\";
    
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    
    
    public static void main(String[] args) {
        
        File f = new File(inputFile);
        
      //Byte array for the file.
        try {
            List<String> numFramesSet = new ArrayList<>();
            
            byte[] MOVByteArray = Files.readAllBytes(f.toPath());
            
            byte[] frameTableArray = Arrays.copyOfRange(MOVByteArray, FRAME_TABLE_OFFSET, DATA_START_OFFSET);
            
            byte[] dataArray = Arrays.copyOfRange(MOVByteArray, DATA_START_OFFSET, MOVByteArray.length);
            
            int i = 0;
            int vidChunkSize = 0;
            int chunkNum = 0;
            int framecount = 0;
            int framesPerRun = 0;
            Map<Integer, Integer> chunkSizeMap = new HashMap<>();

            Map<Integer, Short> frameSizeMap = new HashMap<>();
            System.out.println("Reading Table...");
            while(i < frameTableArray.length) {
                //Read 4 bytes out of the Header Array into a temporary buffer.
                ByteBuffer bb = ByteBuffer.wrap(frameTableArray, i, 2);
                short val = bb.getShort();
                String hexVal = Integer.toHexString(val);
                if(hexVal.equals(HEX_END_FRAME_CHUNK)) {
                    chunkSizeMap.put(chunkNum, vidChunkSize);
                    vidChunkSize = 0;
                    chunkNum++;
                    numFramesSet.add(Integer.toString(framesPerRun));
                    framesPerRun = 0;
                    i+=4;
                    
                } else if(hexVal.equals(HEX_END_FRAME_DATA)) {
                    vidChunkSize = 0;
                    numFramesSet.add(Integer.toString(framesPerRun));
                    framesPerRun = 0;
                    break;
                } else {
                    frameSizeMap.put(framecount, val);
                    vidChunkSize += val;
                    i+=2;
                    framecount++;
                    framesPerRun++;
                }
                
            }
            System.out.println("Done Reading Table...");
            
            int frameRate = 0;
            for(String fps : numFramesSet) {
                if(Integer.parseInt(fps) > frameRate) {
                    frameRate = Integer.parseInt(fps);
                }
            }
            
            ByteArrayOutputStream adxBaos = new ByteArrayOutputStream();
            
            int currPos = 0;
            
            for(int j = 0; j < chunkSizeMap.size(); j++) {
                
                byte[] adxChunk;
                if(j == 0) {
                    adxChunk = Arrays.copyOfRange(dataArray, currPos, currPos + INIT_ADX_CHUNK_SIZE);
                    currPos += INIT_ADX_CHUNK_SIZE;
                } else {
                    adxChunk = Arrays.copyOfRange(dataArray, currPos, currPos + ADX_CHUNK_SIZE);
                    currPos += ADX_CHUNK_SIZE;
                }
                
                currPos += chunkSizeMap.get(j);
                
                adxBaos.write(adxChunk);
                
                
            }
            currPos = INIT_ADX_CHUNK_SIZE;
            int frameNum = 0;
            List<Integer> compLvls = new ArrayList<>();
            for(int j = 0; j < frameSizeMap.size(); j++) {
                
                if(frameNum == frameRate) {
                    currPos += ADX_CHUNK_SIZE;
                    frameNum = 0;
                }
                
                byte[] vidChunk = Arrays.copyOfRange(dataArray, currPos, currPos + frameSizeMap.get(j));
                
                currPos += frameSizeMap.get(j);
                
                byte[] complvl = Arrays.copyOfRange(vidChunk, 0, 2);
               
               
                String compressionLevel = bytesToHex(complvl);
                
                compLvls.add(Integer.parseInt(compressionLevel.substring(3), 16));
                
                System.out.println("Frame " + frameNum + " compression Level: " + compressionLevel.charAt(3));
                Path vidPath = Paths.get(outputVid + j);
                try {
                    Files.write(vidPath, vidChunk);
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
                frameNum++;
            }
            
            Path adxPath = Paths.get(outputAdx);
            
            try {
                Files.write(adxPath, adxBaos.toByteArray());
                
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            System.out.println("Demuxed " + framecount + " frames @" + frameRate  + "fps.");
            
            int minutes = numFramesSet.size() / 60;
            int seconds = numFramesSet.size() % 60;
            System.out.println("Video Length is " + minutes + " minutes and " + seconds + " seconds");
            
            System.out.println(compLvls);
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
