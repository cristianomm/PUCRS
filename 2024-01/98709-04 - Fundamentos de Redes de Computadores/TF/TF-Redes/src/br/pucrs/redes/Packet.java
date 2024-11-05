package br.pucrs.redes;

import java.util.Arrays;

public class Packet {
	
	private int sequence;
	private int confirmSequence;
	private long crc;
	
	/**
	 * s - system
	 * a - ack
	 * i - info
	 * d - data
	 * 
	 */
	private char type;
	private byte[] data;
	
	public Packet() {
		this.data = new byte[10];
	}
	
	public Packet(char type, byte[] data) {
		this.type = type;
		this.data = data;
	}
	
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
	public char getType() {
		return type;
	}
	public void setType(char type) {
		this.type = type;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		return "#" + sequence + "#" + confirmSequence + "#" + type + "#" + (data != null ? Arrays.toString(data) : "") + "#" + crc;
	}

}
