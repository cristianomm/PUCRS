import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RingAlgorithm {

	public static void main(String[] args) throws InterruptedException {
		int numProcesses = 5;
		List<Process> processes = new ArrayList<>();

		// Cria os processos
		for (int i = 1; i <= numProcesses; i++) {
			processes.add(new Process(i, numProcesses));
		}

		// Inicia o anel com os processos
		for (int i = 0; i < numProcesses; i++) {
			processes.get(i).setNext(processes.get((i + 1) % numProcesses));
		}
				
		ExecutorService service = Executors.newFixedThreadPool(numProcesses + 2);
		
		// Inicia o processo que envia os trabalhos
		WorkSender wSender = new WorkSender(processes);
		service.execute(wSender);
		
		// Inicia o processo que envia as falhas
		FailSender fSender = new FailSender(processes);
		service.execute(fSender);
		
		// Inicia os processos 
		for(Process process : processes){
			service.execute(process);
		}
		
		Thread.sleep(5000);
		
		wSender.setStart(false);
		fSender.setRunning(false);
		
		Process process = processes.get(0);
		
		Message message = new Message();
		message.messageType = 'S';
		message.initiatorId = process.getId();
		processes.get(0).receive(message);
		
		System.out.println("Awaiting for finish...");
		service.shutdown();

	}
}
