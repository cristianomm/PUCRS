package br.pucrs.redes;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FileTools {
	
	private static Logger logger = Logger.getLogger(FileTools.class.getName());
	
	public List<Package> splitFile(String fileName) {
		List<Package> packages = new ArrayList();
		
		try {
			
			File file = new File(fileName);
			if(file.exists() && file.isFile()) {
				FileInputStream fis = new FileInputStream(file);
				
				int sequence = 0;
				byte data[] = new byte[10];
				while(fis.read(data) > 0) {
					Package pack = new Package();
					pack.setSequence(sequence++);
					pack.setInfo(file.getName());
					pack.setData(data);
					
					packages.add(pack);
				}
			}
			
		}catch (Exception e) {
			logger.warning(e.getMessage());
		}
		
		return packages;
	}
	
	public void buildFile(List<Package> packages) {
		
	}
	

}
