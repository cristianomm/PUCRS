package br.pucrs.redes;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Logger;

public abstract class AbstractSR implements Runnable {

	protected String address;
	protected int sendPort;
	protected int receivePort;
	protected Utils utils;
	protected boolean running;
	protected boolean connected;
	
	protected List<Packet> receivedPackets;
	
	protected DatagramSocket sendSocket;
	protected DatagramSocket receiveSocket;
	protected Logger logger;


	public AbstractSR() {
		super();
	}

	public abstract void send(String fileName);


	public Packet createConnectionPacket() {
		Packet packet = new Packet('a', "c".getBytes());

		return packet;
	}


	public Packet readAckPacket(Packet packet) {

		Packet returnPacket = null;
		String data = new String(packet.getData(), StandardCharsets.UTF_8);

		switch (data.trim()) {
		case "c": {
			returnPacket = createConnectionPacket();
			returnPacket.setData("ce".getBytes());
			break;
		}
		case "ce":{
			if(!this.connected) {
				this.connected = true;
			}
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + data);
		}

		return returnPacket;
	}

	public void readInfoPacket(Packet packet) {

	}

	public void readDataPacket(Packet packet) {

	}
	
	public boolean checkPacket(Packet packet) {
		
		boolean isValid = false;
		
		//verifica o num se de sequencia conforme o ultimo recebido
		if(!receivedPackets.isEmpty()) {
			Packet lastPckt = receivedPackets.get(receivedPackets.size()-1);
			
			if(lastPckt.getSequence()+1 != packet.getSequence()) {
				isValid = false;
			}
		}
		
		//verifica crc para packets de dados
		if(packet.getType() == 'd') {
			long crc = utils.calcCRC(packet.getData());
			isValid = isValid && (packet.getCrc() == crc);
		}
		
		return isValid;
	}

	public void readSocket() {
		logger.info("receiving packets on port: " + receivePort);
		try {

			receiveSocket = new DatagramSocket(receivePort);
			while(running) {

				byte[] data = new byte[utils.getPacketLength()];
				DatagramPacket receivePacket = new DatagramPacket(data, data.length);

				receiveSocket.receive(receivePacket);
				System.out.println("packet received");

				Packet packet = utils.bytesToPacket(receivePacket.getData());
				System.out.println(packet);

				Packet sendPacket = null;
				switch (packet.getType()) {
				case 'a':
					sendPacket = readAckPacket(packet);
					break;
				case 'd':
					readDataPacket(packet);
					break;
				case 'i':
					readInfoPacket(packet);
					break;
				default:
					break;
				}

				if(sendPacket != null) {
					sendPacket(sendPacket);
				}
			}

		} catch (Exception e) {
			//e.printStackTrace();
			logger.severe(e.getMessage());
		}
	}


	public void sendPacket(Packet packet) throws IOException {
		byte[] sendData = utils.packetToBytes(packet);
		System.out.println("sending packet... "+ packet);
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(address), sendPort);
		this.sendSocket.send(sendPacket);
	}

	public boolean connect(String ip, int port) {

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

	public void disconnect() {
		this.running = false;
		sendSocket.disconnect();
	}

}