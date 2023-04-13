package br.pucrs.fppd.t1.philosophers;

public interface Philosopher extends Runnable {
	long getTotalRequestTime();
	boolean isFinished();
}
