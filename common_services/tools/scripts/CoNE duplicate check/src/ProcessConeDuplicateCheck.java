import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * 
 * Processing Class for the CoNE duplicate check project,
 * containing a main class and some search-options
 * 
 * @author walter
 *
 */
public class ProcessConeDuplicateCheck {
	
	/**
	 * 
	 * Starting a CoNE duplicate
	 * (either a complete check, or an rdf-file is checked against CoNE)
	 * 
	 * @param args (not used right now)
	 */
	public static void main(String[] args) 
	{
		String option = null;
		
		System.out.println("Select a search option:");
		System.out.println("\"a\" for a complete CoNE check");
		System.out.println("\"b\" for a rdf file to be checked");
		Scanner scanner = new Scanner(System.in);
		option = scanner.nextLine();
		scanner.close();
		if (option.equals("a"))
		{
			System.out.println("\n--------------------\nFSTARTING COMPLETE CHECK\n--------------------\n");
			findAllConeDuplicates();
		}
		else if (option.equals("b"))
		{
			System.out.println("\n--------------------\nFSTARTING RDF FILE CHECK\n--------------------\n");
			findRdfDuplicates();
		}
		System.out.println("\n--------------------\nFINISHED\n--------------------\n");
	}
	
	/**
	 * searching possible duplicates in CoNE for a given rdf-file
	 */
	private static void findRdfDuplicates()
	{
		List<String> nameList = XmlUtils.getUsernamesFromConeRdf();
		List<String> possibleDuplicates = new ArrayList<String>();
		int nameListSize = nameList.size();
		List<String> coneResponseList = new ArrayList<String>();
		String name = null;
		for (int i = 0; i < nameListSize; i++) 
		{
			System.out.println("Querying <" + i + "> of <" + (nameListSize - 1) + ">");
			name = nameList.get(i);
			coneResponseList = ConeUtils.queryConePerson(name);
			if (coneResponseList != null && coneResponseList.size() > 0) 
			{
				possibleDuplicates.add("\n--------------------\nPossible Duplicates for " + name + " \n--------------------\n");
				possibleDuplicates.addAll(coneResponseList);
			}
			
		}
		if (ConfigUtil.VERBOSE) 
		{
			System.out.println("\n--------------------\nPossible Duplicates\n--------------------\n");
		}
		File file = new File(ConfigUtil.OUTPUT_FILE_PATH);
		try 
		{
			FileWriter fileWriter = new FileWriter(file);
			for (String possibleDuplicate : possibleDuplicates) 
			{
				fileWriter.write(possibleDuplicate + "\n");
				if (ConfigUtil.VERBOSE) 
				{
					System.out.println(possibleDuplicate);
				}
			}
			fileWriter.close();
		} 
		catch (IOException e) 
		{
			System.out.println("Error writing to File [" + ProcessConeDuplicateCheck.class.getEnclosingMethod() + "]");
			e.printStackTrace();
		}
		System.out.println("Duplicates written to File [" + ConfigUtil.OUTPUT_FILE_PATH + "]" );
	}
	
	/**
	 * doing a complete CoNE duplicate check on CoNE itself
	 */
	private static void findAllConeDuplicates()
	{
		Map<String, String[]> persons = ConeUtils.getAllCone();
		List<String> queryResults = new ArrayList<String>();
		List<String>  possibleDuplicates = new ArrayList<String>();
		String alternativSearchTerm = null;
		String[] nameAndId = new String[2];
		File file = new File(ConfigUtil.OUTPUT_FILE_PATH);
		try {
			FileWriter fileWriter = new FileWriter(file);
			List<String> keySet = new ArrayList<String> (persons.keySet());
			String key = null;
			String person = null;
			String organization = null;
			int personCount = keySet.size();
			for (int i = 0; i < personCount; i++)
			{
				key = keySet.get(i);
				person = (persons.get(key))[0];
				organization = (persons.get(key))[1];
				
				queryResults = ConeUtils.queryConePerson(person);
				if (!ConfigUtil.SEARCH_ONLY_EXACT_COMPLETE_NAMES) {
					alternativSearchTerm = ConeUtils.getSearchTermForCompleteName(person);
					if (!alternativSearchTerm.equals("")) 
					{
						queryResults.addAll(ConeUtils.queryConePerson(alternativSearchTerm));
					}
				}
				for (String queryResult : queryResults)
				{
					nameAndId = queryResult.split("\\|");
					if (!nameAndId[1].equals(key))
					{
						possibleDuplicates.add(queryResult);
					}
				}
				if (!possibleDuplicates.isEmpty())
				{
					fileWriter.write("\n\n--------------------\nPossible Duplicates for " + person + " (" + organization + ")\n" + key + "\n--------------------\n\n");
					if (ConfigUtil.VERBOSE) 
					{
						System.out.println("\n--------------------\nPossible Duplicates for " + person + " (" + organization + ")\n" + key + "\n--------------------\n");
					}
					for (String possibleDublicate : possibleDuplicates)
					{
						fileWriter.write(possibleDublicate + "\n");
						if (ConfigUtil.VERBOSE) 
						{
							System.out.println(possibleDublicate);
						}
					}
				}
				queryResults.clear();
				possibleDuplicates.clear();
				System.out.println("Querying <" + (i + 1) + ">" + " of " + "<" + personCount + ">");
			}
			fileWriter.close();
		} 
		catch (IOException e) 
		{
			System.out.println("Error writing to File [" + ProcessConeDuplicateCheck.class.getEnclosingMethod() + "]");
			e.printStackTrace();
		} 
	}

}
