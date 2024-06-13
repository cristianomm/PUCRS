package br.pucrs.redes;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class FileTools {
	
	private static Logger logger = Logger.getLogger(FileTools.class.getName());
	
	private long calcCRC(byte[] data) {
		Checksum checksum = new CRC32();
		checksum.update(data);
		return checksum.getValue();
	}
	
	public List<Packet> splitFile(String fileName) {
		List<Packet> packets = new ArrayList<>();
		
		try {
			
			File file = new File(fileName);
			if(file.exists() && file.isFile()) {
				FileInputStream fis = new FileInputStream(file);
				
				int sequence = 0;
				byte[] data = new byte[10];
				
				//adiciona pacote com informacoes sobre o arquivo
				Packet info = new Packet();
				info.setSequence(sequence++);
				info.setConfirmSequence(sequence);
				info.setData(file.getName().getBytes());
				
				packets.add(info);
				
				//cria os pacotes a partir do arquivo
				while(fis.read(data) > 0) {
					Packet pack = new Packet();
					pack.setSequence(sequence++);
					pack.setInfo("d");
					pack.setData(data);
					pack.setCrc(calcCRC(data));
					
					packets.add(pack);
				}
				
				fis.close();
			}
			
		}catch (Exception e) {
			logger.warning(e.getMessage());
		}
		
		return packets;
	}
	
	public void buildFile(List<Packet> packets) {
		
	}
	

}
