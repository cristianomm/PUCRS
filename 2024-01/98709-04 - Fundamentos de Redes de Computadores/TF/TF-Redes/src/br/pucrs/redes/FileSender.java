package br.pucrs.redes;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;
import java.util.logging.Logger;

public class FileSender extends AbstractSR {


	public FileSender(String address, int sendPort, int receivePort) throws SocketException {
		super();
		this.running = true;
		this.address = address;
		this.sendPort = sendPort;
		this.receivePort = receivePort;
		this.utils = new Utils(28, 10);
		
		this.sendSocket = new DatagramSocket();
		
		this.logger = Logger.getLogger(FileSender.class.getName());
	}

	@Override
	public void run() {
		
		readSocket();
	}
	

	private void send(List<Packet> packets, String ip, int port) {

		try {
			if(connect(ip, port)) {
				for(Packet pack : packets) {

					logger.info("sending packet " + pack);
					sendPacket(pack);
				}

				disconnect();
			}
		}catch(Exception e) {
			logger.severe(e.getMessage());
		}
	}

	@Override
	public void send(String fileName) {

		logger.info("creating the package list...");

		List<Packet> packets = utils.splitFile(fileName);

		logger.info(String.format("%d packets created for %s", packets.size(), fileName));

		logger.info(String.format("sending to %s:%d", address, sendPort));

		send(packets, address, sendPort);

	}

}
