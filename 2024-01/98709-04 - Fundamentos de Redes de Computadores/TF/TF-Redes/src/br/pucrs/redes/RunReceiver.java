package br.pucrs.redes;

import java.net.SocketException;

public class RunReceiver {
	public static void main(String[] args) throws SocketException {

		String file = "D:/Disco/run_loaders.bat";


		//------------------------------------------------------
		FileReceiver receiver = new FileReceiver("localhost", 8001, 8000);
		new Thread(receiver).start();
	}
}
