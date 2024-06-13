package br.pucrs.redes;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class FileSender implements Runnable {

	private int packetLength = 26;
	private boolean running;
	private DatagramSocket socket;
	private static Logger logger = Logger.getLogger(FileSender.class.getName());

	@Override
	public void run() {

		try {
			DatagramSocket receiveSocket = new DatagramSocket(34789);

			while(running) {

				byte[] data = new byte[packetLength];
				DatagramPacket receivePacket = new DatagramPacket(data, data.length);
				
				receiveSocket.receive(receivePacket);
				
				Packet packet = bytesToPacket(receivePacket.getData());
				
				switch (packet.getInfo()) {
				case "a":
					
					break;
				case "d":
					break;
				case "i":
					break;
				default:
					break;
				}

			}

		} catch (Exception e) {
			logger.severe(e.getMessage());
		}

	}
	
	private Packet bytesToPacket(byte[] data) {
		Packet packet = null;
		
		
		
		return  packet;
	}

	private byte[] packetToBytes(Packet pack) {
		byte[] message = new byte[packetLength];

		/*
		 * 1(4 bytes) - sequence
		 * 2(4 bytes) - confirm sequence
		 * 3(4 bytes) - info
		 * 4(8 bytes) - crc
		 * 5(10 bytes) - data
		 *   
		 */
		message = String.format("%d%d%s%d%s", 
				pack.getSequence(), 
				pack.getConfirmSequence(), 
				pack.getInfo(),
				pack.getCrc(),
				Arrays.toString(pack.getData())).getBytes();
		
		return message;
	}

	public boolean connect(String ip, int port) {

		try {
			this.socket = new DatagramSocket(port, InetAddress.getByName(ip));
		} catch (Exception e) {
			logger.severe(e.getMessage());
		} 

		return this.socket.isConnected();
	}

	public void disconnect() {
		this.running = false;
		this.socket.disconnect();
	}

	public void send(List<Packet> packets, String ip, int port) {

		try {
			if(connect(ip, port)) {
				for(Packet pack : packets) {
					
					logger.info("sending packet " + pack);
					byte[] sendData = packetToBytes(pack);

					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length);
					this.socket.send(sendPacket);
				}

				disconnect();
			}
		}catch(Exception e) {
			logger.severe(e.getMessage());
		}
	}

}
