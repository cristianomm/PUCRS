package br.pucrs.fppd.t1.philosophers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import br.pucrs.fppd.t1.philosophers.arbitrator.Arbitrator;
import br.pucrs.fppd.t1.philosophers.arbitrator.ArbitratorPhilosopher;
import br.pucrs.fppd.t1.philosophers.rh.ResourceHierarchyPhilosopher;

public class DiningPhilosophers {

	private long timeSequential;
	private long timeResourceHierarchy;
	private long timeArbitrator;

	public static void main(String[] args) throws InterruptedException {
		int numPhilosophers = 10;
		int rounds = 5;

		DiningPhilosophers diningPhilosophers = new DiningPhilosophers();

		System.out.println("Executando solucao sequencial...");
		diningPhilosophers.executeSequential(numPhilosophers, rounds);

		System.out.println("\n\nExecutando solucao Resource Hierarchy...");
		diningPhilosophers.executeResourceHierarchy(numPhilosophers, rounds);

		System.out.println("\n\nExecutando solucao Arbitrator...");
		diningPhilosophers.executeArbitrator(numPhilosophers, rounds);

		diningPhilosophers.printTime();
	}

	private void printTime() {
		System.out.println("\nTempo de cada solução:");
		System.out.println("\nSequencial..." + this.timeSequential);
		System.out.println("\nHierarquia de recursos..." + this.timeResourceHierarchy);
		System.out.println("\nÁrbitro..." + this.timeArbitrator);
	}

	private void executeSequential(int numPhilosophers, int rounds) throws InterruptedException {
		Philosopher[] philosophers = new Philosopher[numPhilosophers];

		for (int i = 0; i < numPhilosophers; i++) {
			philosophers[i] = new SequentialPhilosopher(i, rounds);
		}

		this.timeSequential = executePhilosophers(philosophers, 1);
	}


	private void executeResourceHierarchy(int numPhilosophers, int rounds) throws InterruptedException {
		Fork[] forks = new Fork[numPhilosophers];
		Philosopher[] philosophers = new Philosopher[numPhilosophers];

		for (int i = 0; i < numPhilosophers; i++) {
			forks[i] = new Fork(i);
		}

		for (int i = 0; i < numPhilosophers; i++) {
			philosophers[i] = new ResourceHierarchyPhilosopher(i, forks[i], forks[(i + 1) % numPhilosophers], rounds);
		}

		this.timeResourceHierarchy = executePhilosophers(philosophers, numPhilosophers);
	}

	private void executeArbitrator(int numPhilosophers, int rounds) throws InterruptedException {
		Fork[] forks = new Fork[numPhilosophers];
		Philosopher[] philosophers = new Philosopher[numPhilosophers];
		Arbitrator arbitrator = new Arbitrator(numPhilosophers);

		for (int i = 0; i < numPhilosophers; i++) {
			forks[i] = new Fork(i);
		}

		for (int i = 0; i < numPhilosophers; i++) {
			philosophers[i] = new ArbitratorPhilosopher(i, forks[i], forks[(i + 1) % numPhilosophers], arbitrator, rounds);
		}

		this.timeArbitrator = executePhilosophers(philosophers, numPhilosophers);
	}

	private long executePhilosophers(Philosopher[] philosophers, int poolSize) throws InterruptedException {
		int numPhilosophers = philosophers.length;
		List<Thread> threads = new ArrayList<>();

		for (int i = 0; i < numPhilosophers; i++) {
			threads.add(new Thread(philosophers[i]));
			threads.get(i).start();
			
			//para o caso sequencial, executa uma thread por fez até finalizar a execucao
			if(poolSize == 1) {
				while(threads.get(i).isAlive()) {}
			}
		}
		
		long totalRequestTime = 0;
		for (int i = 0; i < numPhilosophers; i++) {
			threads.get(i).join();
			totalRequestTime += philosophers[i].getTotalRequestTime();
		}

		System.out.println("Tempo total de solicitação de garfos: " + totalRequestTime + "ms");
		
		return totalRequestTime;
	}
}
