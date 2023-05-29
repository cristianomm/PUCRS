import java.util.List;

/**
 * 
 * Envia trabalhos para os processos.
 *
 */
public class WorkSender implements Runnable {

	private boolean isStart;
	private List<Process> processes;

	public WorkSender(List<Process> processes) {
		this.isStart = true;
		this.processes = processes;
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	public boolean isStart() {
		return isStart;
	}

	@Override
	public void run() {
		while (this.isStart) {
			try {
				for (Process process : processes) {
					process.setHasWork(true);
				}
				
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}