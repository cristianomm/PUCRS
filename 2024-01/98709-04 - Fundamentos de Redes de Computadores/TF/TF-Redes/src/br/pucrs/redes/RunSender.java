package br.pucrs.redes;

import java.net.SocketException;

public class RunSender {
	public static void main(String[] args) throws SocketException, InterruptedException {

		String file = "D:\\Disco\\comprovante_pgto_sinal.pdf";

		//------------------------------------------------------
		FileSender sender = new FileSender("localhost", 8000, 8001, 16);
		new Thread(sender).start();
		
		while(!sender.isConnected()) {
			sender.connect();
		}
		
		System.out.println(sender.isConnected());
		if(sender.isConnected()) {
			sender.send(file);
		}

	}
}
