package br.pucrs.redes;

import java.util.Arrays;

public class Packet {
	
	private int sequence;
	private int confirmSequence;
	private long crc;
	private long length;
	
	/**
	 * a - ack
	 * i - info
	 * d - data
	 * 
	 */
	private String info;
	private byte[] data;
	
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	public int getConfirmSequence() {
		return confirmSequence;
	}
	public void setConfirmSequence(int confirmSequence) {
		this.confirmSequence = confirmSequence;
	}
	public long getCrc() {
		return crc;
	}
	public void setCrc(long crc) {
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
		return "#" + sequence + "#" + confirmSequence + "#" + (info != null ? info : " ") + "#" + (data != null ? Arrays.toString(data) : "") + "#" + crc;
	}

}
