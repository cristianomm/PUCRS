package br.pucrs.fppd.t1.philosophers.arbitrator;

import br.pucrs.fppd.t1.philosophers.Fork;
import br.pucrs.fppd.t1.philosophers.Philosopher;

import java.util.Random;

public class ArbitratorPhilosopher implements Philosopher {
	private final int id;
	private final Fork leftFork;
	private final Fork rightFork;
	private final Arbitrator arbitrator;
	private final Random rand;
	private long requestTime;
	private final int rounds;
	private boolean isFinished;

	public ArbitratorPhilosopher(int id, Fork leftFork, Fork rightFork, Arbitrator arbitrator, int rounds) {
		this.id = id;
		this.leftFork = leftFork;
		this.rightFork = rightFork;
		this.arbitrator = arbitrator;
		this.rand = new Random();
		this.requestTime = 0;
		this.rounds = rounds;
		this.isFinished = false;
	}

	@Override
	public void run() {
		for (int i = 0; i < this.rounds; i++) {
			System.out.println("AR - Rodada " + i + "-".repeat(60) + " Filosofo " + this.id);
			try {
				think();
				long ti = System.currentTimeMillis();
				arbitrator.requestForks(id, leftFork.getNumber(), rightFork.getNumber());
				long tf = System.currentTimeMillis();
				
				this.requestTime += (tf - ti);
				
				eat();
				arbitrator.releaseForks(id, leftFork.getNumber(), rightFork.getNumber());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.isFinished = true;
	}

	private void think() throws InterruptedException {
		String tabs = "\t".repeat(this.id);
		System.out.println(String.format("%sFilosofo %d esta pensando...", tabs, this.id));
		Thread.sleep(500);
	}

	private void eat() throws InterruptedException {
		String tabs = "\t".repeat(this.id);
		System.out.println(String.format("%sFilosofo %d esta comendo...", tabs, this.id));
		Thread.sleep(500);
	}
	
	@Override
	public long getTotalRequestTime() {
		return this.requestTime;
	}
	
	@Override
    public boolean isFinished() {
    	return this.isFinished;
    }
}
