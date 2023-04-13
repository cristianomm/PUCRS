package br.pucrs.fppd.t1.philosophers;

public class SequentialPhilosopher implements Philosopher {
    private final int id;
    private long requestTime;
    private final int rounds;
    private boolean isFinished;

    public SequentialPhilosopher(int id, int rounds) {
        this.id = id;
        this.requestTime = 0;
        this.rounds = rounds;
    }

    @Override
    public void run() {
        for (int i = 0; i < this.rounds; i++) {
        	System.out.println("SEQ - Rodada " + i + "-".repeat(60) + " Filosofo " + this.id);
            try {
                think();
                long ti = System.currentTimeMillis();
                eat();
                long tf = System.currentTimeMillis();
                this.requestTime += (tf - ti);
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
