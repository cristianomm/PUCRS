package br.pucrs.redes;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Logger;

public class FileReceiver extends AbstractSR {


	public FileReceiver(String address, int sendPort, int receivePort) throws SocketException {
		super();
		this.running = true;
		this.address = address;
		this.sendPort = sendPort;
		this.receivePort = receivePort;
		this.utils = new Utils(28, 10);
		
		this.sendSocket = new DatagramSocket();
		
		this.logger = Logger.getLogger(FileReceiver.class.getName());
	}
	
	@Override
	public void run() {
		readSocket();
	}

	@Override
	public void send(String fileName) {
		// TODO Auto-generated method stub
		
	}
	
}
