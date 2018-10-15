package com.grandia.file.structure;

/**
 * PointerTableEntry
 * 
 * This class represents the entries used in pointer tables for both the MDT/MDP files
 * as well as the Script pointer tables.
 * 
 * @author TrekkiesUnite118
 *
 */
public class PointerTableEntry {

	// Offset value in the table for this entry
	private int tableOffset;
	// Offset for the actual data in the file
	private int offset;
	// Size of the data in the file
	private int size;
	//ID for the entry
	private int id;
	
	public PointerTableEntry tableOffset(int tableOffset) {
		this.tableOffset = tableOffset;
		return this;
	}
	
	public PointerTableEntry offset(int offset) {
		this.offset = offset;
		return this;
	}
	
	public PointerTableEntry size(int size) {
		this.size = size;
		return this;
	}
	
	public PointerTableEntry id(int id) {
		this.id = id;
		return this;
	}

	/**
	 * Returns the table Offset;
	 * @return tableOffset
	 */
	public int getTableOffset() {
		return tableOffset;
	}

	/**
	 * Sets the tableOffset to the passed in value.
	 * @param tableOffset
	 */
	public void setTableOffset(int tableOffset) {
		this.tableOffset = tableOffset;
	}

	/**
	 * Returns the offset.
	 * @return offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Sets the offset to the passed in value.
	 * @param offset
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * Returns the size
	 * @return size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Sets the size to the passed in value.
	 * @param size
	 */
	public void setSize(int size) {
		this.size = size;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("POINTER TABLE ENTRY: \n");
		sb.append("TABLE HEX OFFSET: " +  Integer.toHexString(tableOffset) + "\n");
		sb.append("ENTRY HEX OFFSET: " +  Integer.toHexString(offset) + "\n");
		sb.append("ENTRY HEX SIZE: " +  Integer.toHexString(size) + "\n");
		sb.append("ENTRY ID: " +  Integer.toHexString(id) + "\n");
		
		return sb.toString();
	}
	
}
