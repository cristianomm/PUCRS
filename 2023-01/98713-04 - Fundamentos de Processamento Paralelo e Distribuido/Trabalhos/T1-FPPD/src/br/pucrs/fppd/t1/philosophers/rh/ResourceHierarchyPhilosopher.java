package br.pucrs.fppd.t1.philosophers.rh;

import br.pucrs.fppd.t1.philosophers.Fork;
import br.pucrs.fppd.t1.philosophers.Philosopher;

public class ResourceHierarchyPhilosopher implements Philosopher {
	private final int id;
	private final Fork lowerFork;
	private final Fork higherFork;
	private long requestTime;
	private final int rounds;
	private boolean isFinished;

	public ResourceHierarchyPhilosopher(int id, Fork leftFork, Fork rightFork, int rounds) {
		this.id = id;
		this.requestTime = 0;
		this.rounds = rounds;
		this.isFinished = false;
		
		if (leftFork.getNumber() < rightFork.getNumber()) {
			this.lowerFork = leftFork;
			this.higherFork = rightFork;
		} else {
			this.lowerFork = rightFork;
			this.higherFork = leftFork;
		}
	}

	@Override
	public void run() {
		for (int i = 0; i < this.rounds; i++) {
			System.out.println("RH - Rodada " + i + "-".repeat(60) + " Filosofo " + this.id);
			try {
				think();
				long ti = System.currentTimeMillis();
				long tf = 0;
				synchronized (lowerFork) {
					synchronized (higherFork) {
						tf = System.currentTimeMillis();
						eat();
					}
				}
				
				this.requestTime += (tf - ti);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		this.isFinished = true;
	}

	private void think() throws InterruptedException {
		// Simula o pensamento do filosofo.
		String tabs = "\t".repeat(this.id);
		System.out.println(String.format("%sFilosofo %d esta pensando...", tabs, this.id));
		Thread.sleep(500);
	}

	private void eat() throws InterruptedException {
		// Simula o filosofo comendo.
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
