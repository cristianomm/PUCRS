package br.pucrs.redes;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FileReceiver {
	
	private String address;
	private int sendPort;
	private int receivePort;
	private Utils utils;
	private String path;
	private int missing;
	private boolean running;
	private DatagramSocket sendSocket;
	private Logger logger;
	private DatagramSocket receiveSocket;
	private List<Packet> receivedPackets;
	
	public FileReceiver(String address, int sendPort, int receivePort, String path) throws SocketException {
		super();
		this.path = path;
		this.missing = 0;
		this.running = true;
		this.address = address;
		this.sendPort = sendPort;
		this.receivePort = receivePort;
		this.utils = new Utils(28, 10);
		
		this.sendSocket = new DatagramSocket();
		this.receivedPackets = new ArrayList<Packet>();
		
		this.logger = Logger.getLogger(FileReceiver.class.getName());
	}
	
	public void run() {
		logger.info("receiving packets on port: " + receivePort);
		try {

			receiveSocket = new DatagramSocket(receivePort);
			receiveSocket.setSoTimeout(0);
			while(running) {

				byte[] data = new byte[utils.getPacketLength()];
				DatagramPacket receivePacket = new DatagramPacket(data, data.length);
				
				//System.out.println("waiting new packets...");
				receiveSocket.receive(receivePacket);

				Packet packet = utils.bytesToPacket(receivePacket.getData());

				//verifica se o packet recebido pode ser utilizado
				if(checkPacket(packet)) {
					receivedPackets.add(packet);
					System.out.println("<<<<<< packet received: " + packet);
					
					//envia o ACK para o pacote recebido 
					Packet sendPacket = createAckPacket(packet.getSequence()+1);
					sendPacket(sendPacket);
				} else {
					missing++;
					if(missing > 2) {
						reset();
					}
					System.out.println("invalid packet: " + packet);
				}
				
				//verifica se ja pode montar o arquivo
				Packet lastPckt = getLastReceivedPacket();
				if(lastPckt != null && lastPckt.getType() == 'i') {
					if(new String(lastPckt.getData()).trim().equalsIgnoreCase("ef")) {
						utils.buildFile(path, receivedPackets);
						reset();
					}
				}
			}

		} catch (Exception e) {
			logger.severe(e.getMessage());
		}
	}
	
	private void reset() {
		System.out.println("reset - cleaning received packets");
		missing = 0;
		receivedPackets = new ArrayList<Packet>();
	}
	
	private Packet createAckPacket(int confirmSequence) {
		Packet packet = new Packet('a', "".getBytes());
		packet.setConfirmSequence(confirmSequence);
		
		return packet;
	}
	
	public void sendPacket(Packet packet) throws IOException {
		byte[] sendData = utils.packetToBytes(packet);
		System.out.println("sending packet to "+ sendPort + " " + packet);
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(address), sendPort);
		this.sendSocket.send(sendPacket);
	}
	
	public Packet getLastReceivedPacket() {
		Packet packet = null;
		if(receivedPackets.size()>0) {
			packet = receivedPackets.get(receivedPackets.size() -1);
		}
		
		return packet; 
	}
	
	public boolean checkPacket(Packet packet) {

		boolean isValid = false;

		//verifica o num se de sequencia conforme o ultimo recebido
		if(!receivedPackets.isEmpty()) {
			//busca o ultimo pacote valido recebido
			Packet lastPckt = getLastReceivedPacket();
			//System.out.println("last packet: " + lastPckt);
			//System.out.println("actual: " + packet);
			if(lastPckt.getSequence()+1 == packet.getSequence()) {
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
	
}
