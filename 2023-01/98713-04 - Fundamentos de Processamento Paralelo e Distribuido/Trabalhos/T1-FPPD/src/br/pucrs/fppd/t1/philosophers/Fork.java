package br.pucrs.fppd.t1.philosophers;

public class Fork {
	
	private boolean isInUse;
	private final int number;

	public Fork(int number) {
		this.number = number;
		this.isInUse = false;
	}

	public int getNumber() {
		return number;
	}
	
	public boolean isInUse() {
		return isInUse;
	}
	
	public void setInUse(boolean isInUse) {
		this.isInUse = isInUse;
	}
}
