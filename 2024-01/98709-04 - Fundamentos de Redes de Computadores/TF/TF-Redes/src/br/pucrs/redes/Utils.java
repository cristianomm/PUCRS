package br.pucrs.redes;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class Utils {
	
	private int dataLength;
	private int packetLength;
	private static Logger logger = Logger.getLogger(Utils.class.getName());

	public Utils(int packetLength, int dataLength) {
		
		this.dataLength = dataLength;
		this.packetLength = packetLength;
	}

	public int getPacketLength() {
		return packetLength;
	}

	public int getDataLength() {
		return dataLength;
	}



	public Packet bytesToPacket(byte[] data) {
		Packet packet = null;

		int sequence = ByteBuffer.wrap(data).getInt(0);
		int confirmSequence = ByteBuffer.wrap(data).getInt(4);
		char type = ByteBuffer.wrap(data).getChar(8);
		byte[] packetData = new byte[dataLength];
		ByteBuffer.wrap(data).get(10, packetData);
		long crc = ByteBuffer.wrap(data).getLong(20);

		packet = new Packet();
		packet.setSequence(sequence);
		packet.setConfirmSequence(confirmSequence);
		packet.setType(type);
		packet.setData(packetData);
		packet.setCrc(crc);

		return  packet;
	}

	public byte[] packetToBytes(Packet pack) throws IOException {
		byte[] message = new byte[packetLength];

		/*
		 * 1(4 bytes) - sequence
		 * 2(4 bytes) - confirm sequence
		 * 3(2 bytes) - packet type		 
		 * 4(10 bytes) - data
		 * 5(8 bytes) - crc
		 *   
		 */
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(packetLength);
		DataOutputStream dos = new DataOutputStream(outputStream);

		dos.writeInt(pack.getSequence());
		dos.writeInt(pack.getConfirmSequence());
		dos.writeShort(pack.getType());
		dos.write(pack.getData());		
		dos.writeLong(pack.getCrc());

		return outputStream.toByteArray();
	}

	public long calcCRC(byte[] data) {
		Checksum checksum = new CRC32();
		checksum.update(data);
		return checksum.getValue();
	}

	/**
	 * faz a criacao dos pacotes contendo informacao sobre o arquivo enviado
	 * @param infoData
	 * @return
	 */
	private List<Packet> createInfoPackets(String infoData){
		List<Packet> packets = new ArrayList<>();

		int size = infoData.length();
		int splits = (int) Math.ceil(((double)size / dataLength));

		int start = 0;
		while(start <= size) {

			int end = start + dataLength;
			end = end > size? size: end;

			Packet info = new Packet();
			info.setType('i');
			info.setData(infoData.substring(start, end).getBytes());

			start += dataLength;

			packets.add(info);
		}

		return packets;
	}

	/**
	 * Divide o arquivo em pacotes, gerando uma lista de pacotes para serem enviados
	 * @param fileName
	 * @return
	 */
	public List<Packet> splitFile(String fileName) {
		List<Packet> packets = new ArrayList<>();

		try {

			File file = new File(fileName);
			if(file.exists() && file.isFile()) {
				FileInputStream fis = new FileInputStream(file);

				//gera o num inicial de seq aleatorio para os pacotes
				int sequence = 0;
				
				//adiciona pacote com informacoes sobre o arquivo
				//divide as  informacoes em partes de 10 bytes para serem enviadas na area de dados 
				fileName = file.getName();//fileName.substring(fileName.lastIndexOf('/')+1, fileName.length());
				List<Packet> infoPackets = createInfoPackets(fileName);

				//ajusta os ns de sequencia dos pacotes de informacao
				for(Packet info : infoPackets) {
					info.setSequence(sequence++);

					packets.add(info);
				}

				//cria os pacotes de dados a partir do arquivo
				int bytesRead = 0;
				byte[] buffer = new byte[dataLength];
				while((bytesRead = fis.read(buffer)) != -1) {

					byte[] data = new byte[dataLength];
					System.arraycopy(buffer, 0, data, 0, bytesRead);

					Packet pack = new Packet();
					pack.setSequence(sequence++);
					pack.setConfirmSequence(0);
					pack.setType('d');
					pack.setData(data);
					pack.setCrc(calcCRC(data));

					packets.add(pack);
				}

				fis.close();
								
				Packet packet = new Packet('i', "ef".getBytes());
				packet.setSequence(sequence++);
				packets.add(packet);
				
			}

		}catch (Exception e) {
			logger.warning(e.getMessage());
		}

		return packets;
	}

	/**
	 * Faz a uniao de todos os pacotes recebidos para gerar um arquivo
	 * @param path
	 * @param packets
	 * @throws IOException
	 */
	public void buildFile(String path, List<Packet> packets) throws IOException {

		String fileName = path + "/received_";
		
		//ajusta o nome do arquivo
		for(Packet packet : packets) {
			String partName = new String(packet.getData()).trim();
			if (packet.getType() == 'i' && !partName.equalsIgnoreCase("ef")) {
				fileName += partName;
			}
		}
				
		try {
			
			File file = new File(fileName);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			logger.info("saving file: " + fileName);
			for(Packet packet : packets) {
				if (packet.getType() == 'd') {
					fos.write(packet.getData());
				}
			}
			
			fos.close();
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.severe("error creating file " + fileName + ": " + e.getMessage());
		}

	}


}
