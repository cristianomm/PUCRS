import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Classe principal que inicializa e controla a execução dos processos em um ambiente de anel.
 * Ela inicializa os processos, define a configuração do anel e controla a execução dos processos
 * auxiliares, WorkSender e FailSender. 
 * Após um determinado tempo, envia uma mensagem de shutdown para encerrar a execução.
 */
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
