package ldap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

public class Util {

	public static String randomPasword() {
		return new BigInteger(130, new SecureRandom()).toString(32);
	}
	
	public static void writeStringbufferToFile(String content ) {
		File file = new File("./personList.xml");
		
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			
			FileWriter fileWriter= new FileWriter(file.getAbsolutePath());
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(content);
			bufferedWriter.close();
			System.out.println("Created file 'personList.xml'");
		} catch (IOException e) {
			System.out.println("An error occoured, while writing to File [writeStringbufferToFile()]");
			e.printStackTrace();
		}
		
	}
}
