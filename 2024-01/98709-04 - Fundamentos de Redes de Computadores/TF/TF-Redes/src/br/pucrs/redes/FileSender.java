package br.pucrs.redes;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FileSender implements Runnable {

	private boolean running;
	private boolean connected;
	private String address;
	private int sendPort;
	private int receivePort;
	private Utils utils;
	private DatagramSocket sendSocket;
	private Logger logger;
	private List<Packet> receivedPackets;
	private List<Packet> sentPackets;
	private int congestionWindow;
	private int slowStartThreshold;


	public FileSender(String address, int sendPort, int receivePort, int slowStartThreshold) throws SocketException {
		super();
		this.running = true;
		this.address = address;
		this.sendPort = sendPort;
		this.receivePort = receivePort;
		this.congestionWindow = 1;
		this.slowStartThreshold = slowStartThreshold;
		this.utils = new Utils(28, 10);

		this.sendSocket = new DatagramSocket();
		this.sentPackets = new ArrayList<Packet>();
		this.receivedPackets = new ArrayList<Packet>();

		this.logger = Logger.getLogger(FileSender.class.getName());
	}

	public boolean isConnected() {
		return connected;
	}

	public void run() {

		while(true) {
			readSocket();
		}
	}

	public boolean readSocket() {
		boolean read = false;
		//logger.info("receiving packets on port: " + receivePort);
		try(DatagramSocket receiveSocket = new DatagramSocket(receivePort);) {

			byte[] data = new byte[utils.getPacketLength()];
			DatagramPacket receivePacket = new DatagramPacket(data, data.length);

			receiveSocket.receive(receivePacket);

			Packet packet = utils.bytesToPacket(receivePacket.getData());

			//verifica se o packet recebido pode ser utilizado
			if(checkPacket(packet)) {
				receivedPackets.add(packet);
				System.out.println("packet received: " + packet);

				readAckPacket(packet);
				read = true;
			}

		} catch (Exception e) {
			logger.severe(e.getMessage());
		}

		return read;
	}

	public Packet createConnectionPacket() {
		Packet packet = new Packet('a', "c".getBytes());

		return packet;
	}

	public Packet getLastReceivedPacket() {
		Packet packet = null;
		if(receivedPackets.size()>0) {
			packet = receivedPackets.get(receivedPackets.size() -1);
		}

		return packet; 
	}

	public Packet getLastSentPacket() {
		Packet packet = null;
		if(sentPackets.size()>0) {
			packet = sentPackets.get(sentPackets.size() -1);
		}

		return packet; 
	}

	public boolean checkPacket(Packet packet) {

		boolean isValid = false;

		//verifica o num se de sequencia conforme o ultimo recebido
		if(!sentPackets.isEmpty()) {
			//busca o ultimo pacote valido enviado
			Packet lastPckt = getLastSentPacket();
			//System.out.println("last packet: " + lastPckt);
			//System.out.println("actual: " + packet);
			if(lastPckt.getSequence()+1 == packet.getConfirmSequence()) {
				isValid = true;
			}
		}
		else if(packet.getSequence() == 0) {
			isValid = true;
		}

		//verifica crc para packets de dados
		if(packet.getType() == 'd') {
			long crc = utils.calcCRC(packet.getData());
			isValid = isValid && (packet.getCrc() == crc);
		}

		return isValid;
	}

	private void readAckPacket(Packet packet) {
		if(packet.getConfirmSequence() == 1) {
			connected = true;
		}
	}

	public void disconnect() {
		this.running = false;
		sendSocket.disconnect();
	}

	public boolean connect() {

		try {			
			Packet packet = createConnectionPacket();

			sendPacket(packet);

		} catch (Exception e) {
			logger.severe(e.getMessage());
		} 

		//espera a conexao por ate 10s
		long wTime = 0;
		while(!connected && wTime < 10000) {
			try {
				Thread.sleep(1000);
				wTime += 1000;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return connected;
	}

	public boolean checkReceivedAcks(int startSequence, int endSequence) {
		boolean valid = false;
		
		long count = receivedPackets.stream()
				.filter(p -> 
				p.getConfirmSequence()>= startSequence && p.getConfirmSequence() < endSequence
		).count();
		
		valid = (endSequence - startSequence) == count;
		
		return valid;
	}

	public void sendPacket(Packet packet) throws IOException {

		//ajusta a sequencia antes de enviar conforme o
		//ultimo numero de sequencia confirmado
		Packet lastPacket = getLastReceivedPacket();
		if(lastPacket != null) {
			packet.setSequence(lastPacket.getConfirmSequence());
		}

		byte[] sendData = utils.packetToBytes(packet);
		//System.out.println("sending packet to " + sendPort + " " + packet);
		logger.info(">>>>>>  sending packet to " + sendPort + " " + packet);
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(address), sendPort);
		this.sendSocket.send(sendPacket);
		sentPackets.add(packet);

		//return readSocket();
	}


	public void send(List<Packet> packets) throws Exception {

		boolean bool = false;
		int windowSize = 1;
		int segmentCount = 0;
		
		for(int i=0; i<packets.size(); ) {

			System.out.println("window size: " + windowSize);
			int startSequence = getLastReceivedPacket().getConfirmSequence();
			//realiza o envio de pacotes conforme a janela atual
			while(segmentCount < windowSize && i<packets.size()) {
				sendPacket(packets.get(i++));
				segmentCount++;
				Thread.sleep(200);
			}
			
			//depois de enviar, verifica se recebeu todos acks
			//System.out.println("ss: " + startSequence + " i:" + i + " sc: " + segmentCount);
			if(checkReceivedAcks(startSequence, startSequence + segmentCount)) {
				//caso tenha recebido todas confirmacoes, atualiza o tamanho da janela
				if(windowSize < slowStartThreshold) {
					//dobra a capacidade da janela no slowStart
					windowSize *= 2;
				}
				else {
					//incrementa no cong. avoidance
					windowSize++;
				}

			}else {
				//caso nao tenha recebido todas confirmacoes
				//reseta os indices para enviar novamente os mesmos pacotes
				i = 0;
				receivedPackets.subList(1, receivedPackets.size()).clear();
				System.out.println("falha de envio na janela " + windowSize);
			}

			//volta para o segmento inicial da janela atual
			segmentCount = 0;
		}
	}

	public void send(String fileName) {

		try {
			logger.info("creating the package list...");

			List<Packet> packets = utils.splitFile(fileName);

			logger.info(String.format("%d packets created for %s", packets.size(), fileName));

			logger.info(String.format("sending to %s:%d", address, sendPort));

			send(packets);
		}catch (Exception e) {
			logger.severe(e.getMessage());
		}
	}

}
