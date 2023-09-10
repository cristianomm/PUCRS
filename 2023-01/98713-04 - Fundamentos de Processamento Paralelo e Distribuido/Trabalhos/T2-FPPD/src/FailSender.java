import java.util.List;
import java.util.Random;

/**
     * Envia falhas para os processos, para simular uma falha
     * de tempos em tempos nos processos, escolhendo um processo de forma aleat�ria.
     *
     */
    public class FailSender implements Runnable {
    	
    	private Random rand;
    	private boolean isRunning;
    	private List<Process> processes;
    	    	
    	public FailSender(List<Process> processes) {
    		this.rand = new Random();
    		this.isRunning = true;
			this.processes = processes;
		}
    	
    	public void setRunning(boolean isRunning) {
			this.isRunning = isRunning;
		}
    	
		@Override
		public void run() {
			while (this.isRunning) {
				int position = rand.ints(1, 0, processes.size()).findFirst().getAsInt();
				
				this.processes.get(position).setActive(false);
				try {
					Thread.sleep(rand.longs(1, 5000, 6000).findFirst().getAsLong());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
    }