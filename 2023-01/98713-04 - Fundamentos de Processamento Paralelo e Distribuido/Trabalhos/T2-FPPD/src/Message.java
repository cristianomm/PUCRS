import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
