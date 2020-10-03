package com.grandia.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
    private static String inputFile = "D:\\MOV20.MOV";
    private static String outputAdx = "D:\\MOV20.ADX";
    private static String outputVid = "D:\\MOV20.VID";
    
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    
    
    public static void main(String[] args) {
        
        File f = new File(inputFile);
        
      //Byte array for the file.
        try {
            byte[] MOVByteArray = Files.readAllBytes(f.toPath());
            
            byte[] frameTableArray = Arrays.copyOfRange(MOVByteArray, FRAME_TABLE_OFFSET, DATA_START_OFFSET);
            
            byte[] dataArray = Arrays.copyOfRange(MOVByteArray, DATA_START_OFFSET, MOVByteArray.length);
            
            int i = 0;
            int vidChunkSize = 0;
            int chunkNum = 0;
            
            Map<Integer, Integer> chunkSizeMap = new HashMap<>();
            System.out.println("Reading Table...");
            while(i < frameTableArray.length) {
                //Read 4 bytes out of the Header Array into a temporary buffer.
                ByteBuffer bb = ByteBuffer.wrap(frameTableArray, i, 2);
                short val = bb.getShort();
                String hexVal = Integer.toHexString(val);
                System.out.println(hexVal);
                if(hexVal.equals(HEX_END_FRAME_CHUNK)) {
                    chunkSizeMap.put(chunkNum, vidChunkSize);
                    vidChunkSize = 0;
                    chunkNum++;
                    i+=4;
                    
                } else if(hexVal.equals(HEX_END_FRAME_DATA)) {
                    vidChunkSize = 0;
                    break;
                } else {
                    vidChunkSize += val;
                    System.out.println("Chunk Size " + vidChunkSize);
                    i+=2;
                }
                
            }
            System.out.println("Done Reading Table...");
            File adxOut = new File(outputAdx);
            File vidOut = new File(outputVid);
            
            ByteArrayOutputStream adxBaos = new ByteArrayOutputStream();
            ByteArrayOutputStream vidBaos = new ByteArrayOutputStream();
            
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
                
                byte[] vidChunk = Arrays.copyOfRange(dataArray, currPos, currPos + chunkSizeMap.get(j));
                
                currPos += chunkSizeMap.get(j);
                
                adxBaos.write(adxChunk);
                vidBaos.write(vidChunk);
            }
            
            Path adxPath = Paths.get(outputAdx);
            Path vidPath = Paths.get(outputVid);
            try {
                Files.write(adxPath, adxBaos.toByteArray());
                Files.write(vidPath, vidBaos.toByteArray());
                
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            
            
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
