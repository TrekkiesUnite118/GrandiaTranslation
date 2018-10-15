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
	//The value at offset 0x20 seems to be a common HWRAM location on the Saturn, and shouldn't be modified.
	private static final int OFFSET_0X20 = 32;
	//The last two entries do not actually follow the usual pattern of offset/size. It's instead 2 different offsets.
	private static final int LAST_OFFSET = 504;
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
			
			Map<String, File> postScriptFiles = new HashMap<>();
			FileUtils.populateFileMap(postScriptFiles, inputFilePath, ".POSTSCRIPT");
			
			//For each file, we get it's remaining parts, calculate the new pointer table, and put it back together.
			for(String key : headerFiles.keySet()) {
				try {
					
					//Parse in the pointer table.
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					byte[] headerBytes = Files.readAllBytes(headerFiles.get(key).toPath());
					pointerTable.parsePointerTableFromByteArray(headerBytes, true);
					PointerTableEntry scriptHeaderEntry = pointerTable.getPointerTableEntry(SCRIPT_HEADER_OFFSET);
					PointerTableEntry scriptEntry = pointerTable.getPointerTableEntry(SCRIPT_OFFSET);
					//printPointerTable();
					
					//Determine the new script size.
					byte[] scriptBytes = Files.readAllBytes(scriptFiles.get(key).toPath());
					int newScriptSize = scriptBytes.length;
					
					//Determine the difference in size between the old script and the new script.
					int sizeDiff = scriptBytes.length - scriptEntry.getSize();
					
					if(sizeDiff > 0) {
						scriptEntry.setSize(newScriptSize);
					} else {
						sizeDiff = 0;
						ByteBuffer newScript = ByteBuffer.allocate(scriptEntry.getSize());
						newScript.put(scriptBytes);
						scriptBytes = newScript.array();
					}
					
					//Calculate the new pointer table entries.
					ByteBuffer bb = ByteBuffer.allocate(4);
					for(int i = 0; i < 512; i +=8) {
						//For the Script and Script header entries we keep the sizes we've already set.
						if(i == SCRIPT_HEADER_OFFSET) {
							bb.putInt(0, scriptHeaderEntry.getOffset());
							out.write(bb.array());
							bb.putInt(0, scriptHeaderEntry.getSize());
							out.write(bb.array());
							
						}else if(i == SCRIPT_OFFSET) {
							bb.putInt(0, scriptEntry.getOffset());
							out.write(bb.array());
							bb.putInt(0, scriptEntry.getSize());
							out.write(bb.array());
							
						//If offset 0x20, leave it alone	
						}else if(i == OFFSET_0X20) {
							PointerTableEntry pte = pointerTable.getPointerTableEntry(i);
							
							bb.putInt(0, pte.getOffset());
							out.write(bb.array());
							bb.putInt(0, pte.getSize());
							out.write(bb.array());
							
						//If last offset, treat as 2 different offsets rather than offset/size.	
						}else if (i == LAST_OFFSET) {
							PointerTableEntry pte = pointerTable.getPointerTableEntry(i);
							if(!Integer.toHexString(pte.getOffset()).equals("ffffffff")
									&& !Integer.toHexString(pte.getSize()).equals("ffffffff")) {
								pte.setOffset(pte.getOffset() + sizeDiff);
								pte.setSize(pte.getSize() + sizeDiff);
								bb.putInt(0, pte.getOffset());
								out.write(bb.array());
								bb.putInt(0, pte.getSize());
								out.write(bb.array());
							}
						}else {
							//For all other entries, we update their entires if they come after the script in the file.
							PointerTableEntry pte = pointerTable.getPointerTableEntry(i);
							if(!Integer.toHexString(pte.getOffset()).equals("ffffffff")) {
								if(pte.getOffset() > scriptEntry.getOffset()) {
									pte.setOffset(pte.getOffset() + sizeDiff);
								}
							}
							bb.putInt(0, pte.getOffset());
							out.write(bb.array());
							bb.putInt(0, pte.getSize());
							out.write(bb.array());
							
						}
						
					}
					
					//Read the rest of the file pieces in.
					byte[] preScriptBytes = Files.readAllBytes(preScriptFiles.get(key).toPath());
					byte[] scriptHeaderBytes = Files.readAllBytes(scriptHeaderFiles.get(key).toPath());
					byte[] postScriptBytes = Files.readAllBytes(postScriptFiles.get(key).toPath());
					
					//Write them to the output stream.
					out.write(preScriptBytes);
					out.write(scriptHeaderBytes);
					out.write(scriptBytes);
					out.write(postScriptBytes);
					
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
