package br.pucrs.fppd.t1.philosophers.arbitrator;

import br.pucrs.fppd.t1.philosophers.Fork;

public class Arbitrator {
	private final int numPhilosophers;
	private final Fork[] forks;

	public Arbitrator(int numPhilosophers) {
		this.numPhilosophers = numPhilosophers;
		this.forks = new Fork[numPhilosophers];
		for (int i = 0; i < numPhilosophers; i++) {
			forks[i] = new Fork(i);
			forks[i].setInUse(false); // Todos os garfos comecam livres.
		}
	}

	public synchronized void requestForks(int philosopherId, int leftFork, int rightFork) throws InterruptedException {
		while (forks[leftFork].isInUse() || forks[rightFork].isInUse()) {
			// Espere ate que ambos os garfos estejam disponiveis.
			wait();
		}

		forks[leftFork].setInUse(true);
		forks[rightFork].setInUse(true);

		System.out.println(String.format("Filosofo %d está utilizando os garfos %d e %d", philosopherId, leftFork, rightFork));
	}

	public synchronized void releaseForks(int philosopherId, int leftFork, int rightFork) {
		forks[leftFork].setInUse(false);
		forks[rightFork].setInUse(false);

		System.out.println(String.format("Filosofo %d liberou os garfos %d e %d", philosopherId, leftFork, rightFork));

		notifyAll(); // Notifique todos os filosofos que os garfos foram liberados.
	}
}
