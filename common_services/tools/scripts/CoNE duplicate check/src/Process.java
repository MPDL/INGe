import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Process {
	
	public final static boolean VERBOSE = false;
	private final static String OUTPUT_FILE= "E:\\tmp\\possibleConeDuplicates.txt";

	public static void main(String[] args) {
		List<String> nameList = XmlUtils.getUsernameFromConeRdf();
		List<String> possibleDuplicates = new ArrayList<String>();
		int nameListSize = nameList.size();
		List<String> coneResponseList = new ArrayList<String>();
		String name = null;
		for (int i = 0; i < nameListSize; i++) {
			System.out.println("Querying <" + i + "> of <" + (nameListSize - 1) + ">");
			name = nameList.get(i);
			coneResponseList = ConeUtils.printQueryConeCompleteName("persons", name);
			if (coneResponseList != null && coneResponseList.size() > 0) {
				possibleDuplicates.add("\n--------------------\nPossible Duplicates for " + name + " \n--------------------\n");
				possibleDuplicates.addAll(coneResponseList);
			}
			
		}
		System.out.println("\n--------------------\nPossible Duplicates\n--------------------\n");
		File file = new File(OUTPUT_FILE);
		try {
			FileWriter fileWriter = new FileWriter(file);
			for (String possibleDuplicate : possibleDuplicates) {
				fileWriter.write(possibleDuplicate + "\n");
				if (VERBOSE) {
					System.out.println(possibleDuplicate);
				}
			}
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
