import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Representa uma única mensagem que pode ser passada entre processos. Cada mensagem tem um tipo 
 * que determina como ela deve ser processada pelos processos. Os tipos incluem 'E' para eleição, 
 * 'R' para resultado da eleição, 'C' para status do coordenador, 'S' para shutdown e 'X' para 
 * interromper a transmissão da mensagem. Além disso, a classe Message rastreia informações adicionais
 * como o status do coordenador e os votos recebidos por uma mensagem.
 */
public class Message {
	
	public int messageId;
	public char messageType;
	public char coordinatorStatus;
	public int coordinatorId;
	public int initiatorId;
	public Set<Integer> votes;
	public Set<Integer> finished;
	
	public Message() {
		this.messageId = new Random().nextInt(5000);
		this.coordinatorStatus = 'X';
		this.votes = new HashSet<>();
		this.finished = new HashSet<>();
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.messageId == ((Message)obj).messageId;
	}

	@Override
	public String toString() {
		return "Message [id=" + this.messageId + ", messageType=" + messageType + ", coordinatorStatus=" + coordinatorStatus + ", coordinatorId="
				+ coordinatorId + ", initiatorId=" + initiatorId + ", votes=" + votes + ", finished=" + finished + "]";
	}
}
