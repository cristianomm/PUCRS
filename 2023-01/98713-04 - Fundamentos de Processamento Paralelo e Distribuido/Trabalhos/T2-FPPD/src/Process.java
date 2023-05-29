import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Representa um processo individual dentro do anel. 
 * Cada processo tem um ID único, conhece o próximo processo na sequência e mantém uma fila de mensagens 
 * a serem processadas. Contém a lógica para processar vários tipos de mensagens, incluindo mensagens de eleição, 
 * atualização do coordenador e mensagens de shutdown. 
 * O estado do processo pode ser ativo ou inativo, representando a condição normal ou falha, respectivamente. 
 * Um processo inativo não processa mensagens.
 */
public class Process implements Runnable {
	private int id;
	private int processCount;
	private int coordinatorId;
	private Process next;
	private boolean isActive;
	private boolean isRunning;
	private boolean hasWork;
	private boolean waitingElection;
	private Random rand;
	private Queue<Message> messages;

	public Process(int id, int processCount) {
		this.id = id;
		this.processCount = processCount;
		this.hasWork = false;
		this.isActive = true;
		this.isRunning = true;
		this.rand = new Random();
		this.messages = new ConcurrentLinkedQueue<>();
	}

	public int getId() {
		return id;
	}

	public Process getNext() {
		return next;
	}

	public void setNext(Process next) {
		this.next = next;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public void setHasWork(boolean hasWork) {
		this.hasWork = hasWork;
	}

	/**
	 * Realiza a simulacao de processamento. 
	 * Eh ativado pelo <code>WorkSender.java</code>
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	private void process() {
		if(this.hasWork) {
			
			try {
				//simula o processamento...
				System.out.println("Process Id:" + this.id + " is processing tasks...");
				Thread.sleep(rand.longs(1, 2000, 4000).findFirst().getAsLong());
			}catch(Exception e) {}
			
			//indica que terminou o processamento...
			this.hasWork = false;
		}
	}
	
	private void processMessages() {
		Message message = messages.poll();

		if(message != null) {
			//System.out.println("Process Id:" + this.id + " processing message: " + message);
			// caso esteja ativo, processa as msgs. Caso contrario apenas repassa a mensagem
			Message retMessage = null;
			if(this.isActive) {
				//System.out.println("Process Id:" + this.id + " - " + message);
				switch (message.messageType) {
				case 'E'://E-2-5-1-
					retMessage = processElection(message);
					break;
				case 'R'://R-2
					retMessage = updateCoordinator(message);
					break;
				case 'C'://C-T-4-2-
					retMessage = processCoordStatus(message);
					break;
				case 'S':
					retMessage = processShutdown(message);
					break;
				default:
					retMessage = null;
					//System.out.println("Message not recognized: " + message);
				}
			}else {
				retMessage = message;
			}

			//System.out.println("Process Id:" + this.id + "\n\tsending - " + retMessage);
			this.next.receive(retMessage);
		}
	}

	/**
	 * Realiza a simulacao de uma falha no processo eh ativado pelo
	 * <code>FailSender.java</code>
	 */
	private void fail() {
		if (!this.isActive && this.isRunning) {
			System.out.println(String.format("Process %d is in fail... restarting", this.id));
			try {
				Thread.sleep(rand.longs(1, 2000, 4000).findFirst().getAsLong());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			this.isActive = true;
			System.out.println(String.format("Process %d recovered", this.id));
			this.waitingElection = false;
			receive(processElection(null));
		}
	}	

	/**
	 * mensagens: 
	 * E - eleicao 
	 * R - resultado de eleicao 
	 * C - status do coordenador: T ou F
	 * S - mensagem de termino dos processos
	 * 
	 * @param message
	 */
	public void receive(Message message) {
		if(message != null) {
			if(!messages.contains(message)) {
				messages.add(message);
			}
		}
	}

	private Message processElection(Message message) {

		//todos processos iniciam aguardando uma eleicao para novo coordenador
		if(!this.waitingElection ) {
			message = new Message();
			message.messageType = 'E';
			message.initiatorId = this.id;
			message.votes.add(this.id);
			this.waitingElection = true;
		}
		else if(message != null) {
			int maxId = 0;
			// tenta buscar o proprio id e caso encontre, escolhe o coordenador
			//eh necessario que tenha mais de um voto para nao correr o risco de 
			//avaliar apenas o propio voto do processo
			if(message.initiatorId == this.id && message.votes.size() == this.processCount ) {
				for(int vote : message.votes) {
					if(maxId < vote) {
						maxId = vote;
					}
				}

				Message cordMsg = new Message();
				cordMsg.initiatorId = this.id;
				cordMsg.coordinatorStatus = 'T';
				cordMsg.messageType = 'R';
				cordMsg.coordinatorId = maxId;
				
				message = updateCoordinator(cordMsg);
			}
			// caso nao encontre, adiciona o seu proprio id e envia msg de eleicao
			else if(message.initiatorId != this.id && message.votes.size() < this.processCount){
				message.votes.add(this.id);
			}
		}

		// realiza o envio da mensagem para os proximos
		return message;
	}

	private Message updateCoordinator(Message message) {
		// 
		if(message.initiatorId == this.id && message.votes.size() == this.processCount) {
			// caso ja tenha percorrido todos processos, para de enviar a msg
			message.messageType = 'X';
		}
		
		this.coordinatorId = message.coordinatorId;
		message.votes.add(this.id);
		System.out.println("Process: " + this.id + " find coordinator: " + this.coordinatorId + " message:" + message);

		return message;
	}

	private Message processCoordStatus(Message message) {
		try {
			if(message.coordinatorStatus == 'F') {
				this.coordinatorId = 0;
				this.waitingElection = false;
			}
			// Altera o status do coordenador na msg caso este seja o coordenador
			else if (message.coordinatorStatus == '0') {
				if(this.coordinatorId == this.id) {
					if(this.isActive) {
						message.coordinatorStatus = 'T';
					}
					else {
						message.coordinatorStatus = 'F';
					}
					message.votes.add(this.id);
				}
			}

			//caso contenha o proprio id, significa que ja percorreu todos 
			if(message.initiatorId == this.id) {
				// caso nao tenha o status do coordenador, inicia eleicao
				if(message.coordinatorStatus == '0' || message.coordinatorStatus == 'F') {
					message = processElection(null);
				}
				else {
					message.messageType = 'X'; //para de enviar o status
				}
			}
			else {
				message.votes.add(this.id);
			}

		}catch(Exception e) {	}

		return message;
	}

	private Message processShutdown(Message message) {
	    if (message.initiatorId == this.id) {
	        // caso todos processos tenham recebido a msg de finalizacao
	        if (message.votes.size() == this.processCount && message.finished.size() == this.processCount) {
	            // para de enviar a msg
	            message.messageType = 'X';
	        } else {
	            // adiciona o voto deste, caso ainda nao tenha votado
	            message.votes.add(this.id);
	        }

	        // finaliza, caso ainda esteja rodando e ja foi sinalizado para parar
	        if (this.isRunning && message.votes.size() == this.processCount) {
	            
	            this.isRunning = false;
	            this.isActive = false;
	            System.out.println(String.format("************************\t\tProcess %d is finishing...\t\t************************", this.id));
	            message.finished.add(this.id);
	        }
	    } else {
	        // caso nao tenha sido este, adiciona o voto
	        message.votes.add(this.id);

	        if (message.votes.size() == this.processCount && this.isRunning) {
	            
	            this.isRunning = false;
	            this.isActive = false;
	            System.out.println(String.format("************************\t\tProcess %d is finishing...\t\t************************", this.id));
	            message.finished.add(this.id);
	        }
	    }

	    return message;
	}

	private void checkProcess() {
		if(this.coordinatorId <=0) {
			receive(processElection(null));
		}
	}

	private void printStatus() {
		System.out.println("Status: " + this.toString());
	}

	@Override
	public String toString() {
		return "Process [id=" + this.id + ", coordinatorId=" + this.coordinatorId 
				+ ", next=" + this.next.id + ", isActive=" + this.isActive
				+ ", isRunning=" + this.isRunning + ", hasWork=" + this.hasWork 
				+ ", queueSize=" + this.messages.size() + "]";
	}

	@Override
	public void run() {
		while (this.isRunning) {
			printStatus();
			//verifica se o processo tem um coordenador.
			// Caso nao, inicia uma eleicao
			checkProcess();
			
			//processa as mensagens
			processMessages();
			
			//simula processamento, caso tenha sido ativado
			process();

			//simula a falha, caso tenha sido ativada
			fail();

			try {
				Thread.sleep(2000);
			}catch(InterruptedException e) {}
		}
		this.isActive = false;
		this.isRunning = false;
		System.out.println("************************\t\tProcess " + this.id + " finished.\t\t************************");
	}
}