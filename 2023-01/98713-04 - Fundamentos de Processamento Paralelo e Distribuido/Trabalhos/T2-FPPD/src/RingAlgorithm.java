import java.util.*;

public class RingAlgorithm {

    static class Process {
        int id;
        Process next;
        boolean isActive;
        Random rand;

        public Process(int id) {
            this.id = id;
            this.isActive = true;
            this.rand = new Random();
        }
        
        /**
         * Realiza a simulaçao de processamento
         * @return
         * @throws InterruptedException 
         */
        public String process() throws InterruptedException {
        	String retMessage = "";
        	System.out.println(String.format("Process %d is processing...", this.id));
        	
        	Thread.sleep(rand.longs(1, 1000, 2000).findFirst().getAsLong());
        	
        	return retMessage;
        }
        
        public void fail() {
        	this.isActive = false;
        	System.out.println(String.format("Process %d is fail... restarting", this.id));
        	try {
				Thread.sleep(rand.longs(1, 2000, 4000).findFirst().getAsLong());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	System.out.println(String.format("Process %d recovered", this.id));
        }

        void startElection(List<Integer> ids) {
            ids.add(id);
            if (next.isActive) {
                next.receiveElectionMessage(ids);
            } else {
                this.startElection(ids);
            }
        }

        void receiveElectionMessage(List<Integer> ids) {
            if (ids.contains(id)) { // se este processo iniciou a votação
                int coordinatorId = Collections.max(ids);
                sendCoordinatorMessage(coordinatorId);
            } else {
                ids.add(id);
                if (next.isActive) { // se o próximo está ativo, envia a mensagem
                    next.receiveElectionMessage(ids);
                } else {
                    this.receiveElectionMessage(ids); // envia para os próximos
                }
            }
        }

        void sendCoordinatorMessage(int coordinatorId) {
            System.out.println("Process " + id + ": novo coordenador: " + coordinatorId);
            if (next.id != id) { // Prevent infinite loop
                if (next.isActive) {
                    next.sendCoordinatorMessage(coordinatorId);
                } else {
                    this.sendCoordinatorMessage(coordinatorId);
                }
            }
        }
    }
    
    /**
     * 
     * Envia trabalhos para os processos.
     *
     */
    static class WorkSender implements Runnable{
    	
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
				for (Process process : processes) {
					try {
						process.process();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
    }
    
    /**
     * Envia falhas para os processos, para simular uma falha
     * de tempos em tempos nos processos, escolhendo um processo de forma aleatória.
     *
     */
    static class FailSender implements Runnable {
    	
    	private Random rand;
    	private List<Process> processes;
    	    	
    	public FailSender(List<Process> processes) {
    		this.rand = new Random();
			this.processes = processes;
		}
    	
		@Override
		public void run() {
			while (true) {
				int position = rand.ints(1, 0, processes.size()).findFirst().getAsInt();
				
				this.processes.get(position).fail();
				try {
					Thread.sleep(rand.longs(1, 4000, 5000).findFirst().getAsLong());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
    }

    public static void main(String[] args) {
        int numProcesses = 5;
        List<Process> processes = new ArrayList<>();
        
        // Cria os processos
        for (int i = 1; i <= numProcesses; i++) {
            processes.add(new Process(i));
        }

        // Inicia o anel com os processos
        for (int i = 0; i < numProcesses; i++) {
            processes.get(i).next = processes.get((i + 1) % numProcesses);
        }
        
        // Inicia o processo que envia os trabalhos
        Thread wSender = new Thread(new WorkSender(processes));
        wSender.start();
        
        // Inicia o processo que envia as falhas
        Thread fSender = new Thread(new FailSender(processes));
        fSender.start();
        
        // Start election from process 1
        processes.get(0).startElection(new ArrayList<>());
        
        System.out.println("");
        
        //cada processo é um sistema idependente e deve ter comunicaçao para o próximo e o anterior
        //envia mensagem de resultado de eleicao, informando o processo que ganhou
        //como coordenador, escolhe o de maior id
        //mensagens:
        //eleicao
        //resultado de eleicao
        //coordenador em falha
        //status do coordenador
        //retorno de um dos processos
        //mensagem de termino
        
    }
}
