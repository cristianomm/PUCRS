package br.pucrs.redes;

import java.util.Arrays;

public class Package {
	
	private int sequence;
	private String crc;
	private long length;
	private String info;
	private byte data[];
	
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	public String getCrc() {
		return crc;
	}
	public void setCrc(String crc) {
		this.crc = crc;
	}
	public long getLength() {
		return length;
	}
	public void setLength(long length) {
		this.length = length;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		return "Package [sequence=" + sequence + ", " 
				+ (crc != null ? "crc=" + crc + ", " : "") 
				+ "length=" + length + ", " 
				+ (info != null ? "info=" + info + ", " : "") 
				+ (data != null ? "data=" + Arrays.toString(data) : "") + "]";
	}

}
