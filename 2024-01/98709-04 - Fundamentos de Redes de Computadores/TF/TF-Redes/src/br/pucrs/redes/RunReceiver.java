package br.pucrs.redes;


public class RunReceiver {
	public static void main(String[] args) throws Exception {

		String path = "D:/Disco";
		
		//------------------------------------------------------
		FileReceiver receiver = new FileReceiver("localhost", 8001, 8000, path);
		//new Thread(receiver).start();
		
		receiver.run();
	}
}
