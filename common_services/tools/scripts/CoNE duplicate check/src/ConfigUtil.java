/**
 * 
 * Configuration Class containing all properties for the CoNE duplicate Check project
 * 
 * @author walter
 *
 */
public class ConfigUtil {
	
	// Set the CoNE instance to be queried
	public static final String CONE_URL = "http://pubman.mpdl.mpg.de/cone/";
	
	// Set to "true" for logging information
	public final static boolean VERBOSE = false;
	
	// Set to "true" if you want to check only complete-names without alternatives, 
	// or "shortened" complete-names 
	// (e.g. "Mayer, Max" only an not "Mayer, M." in addition)
	public final static boolean SEARCH_ONLY_EXACT_COMPLETE_NAMES = true;
	
	// Set the file path, where you want the duplicates to be written
	public final static String OUTPUT_FILE_PATH = "E:\\tmp\\possibleConeDuplicates.txt";
	
	// set the CoNE-Person model
	public static final String PERSON_MODEL = "persons";
	
	// Set the file path to the cone-rdf-xml
	// (only needed for RDF-Check)
	public final static String RDF_FILE_PATH = "E:\\tmp\\result_cone_mpi_ds.xml";
	
	// XPath-Expression for the title node
	// (only needed for RDF-Check)
	public final static String XPATH_EXPRESSION_TITLE = "/RDF/Description/title/text()";
	
	// XPath-Expression for the alternativ-title node
	// (only needed for RDF-Check)
	public final static String XPATH_EXPRESSION_ALTERNATIVE_TITLE = "/RDF/Description/alternative/text()";

}
