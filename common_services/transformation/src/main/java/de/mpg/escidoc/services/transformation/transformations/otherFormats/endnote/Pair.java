package de.mpg.escidoc.services.transformation.transformations.otherFormats.endnote;

import java.util.HashMap;
import java.util.Map;

/**
 * A key-value pair.
 * 
 * @author kurt (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class Pair{
	
	private String key;
    private String value;
    public static final Map<String, String> KEY_MAPPING =   
    	new HashMap<String, String>()   
    	{  
			{  
	    		put("%A", "A");  
	    		put("%B", "B");  
	    		put("%C", "C");  
	    		put("%D", "D");  
	    		put("%E", "E");  
	    		put("%F", "F");  
	    		put("%G", "G");  
	    		put("%H", "H");  
	    		put("%I", "I");  
	    		put("%J", "J");  
	    		put("%K", "K");  
	    		put("%L", "L");  
	    		put("%M", "M");  
	    		put("%N", "N");  
	    		put("%P", "P");  
	    		put("%Q", "Q");  
	    		put("%R", "R");  
	    		put("%S", "S");  
	    		put("%T", "T");  
	    		put("%U", "U");  
	    		put("%V", "V");  
	    		put("%W", "W");  
	    		put("%X", "X");  
	    		put("%Y", "Y");  
	    		put("%Z", "Z");  
	    		put("%0", "NUM_0");  
	    		put("%1", "NUM_1");  
	    		put("%2", "NUM_2");  
	    		put("%3", "NUM_3");  
	    		put("%4", "NUM_4");  
	    		put("%6", "NUM_6");  
	    		put("%7", "NUM_7");  
	    		put("%8", "NUM_8");  
	    		put("%9", "NUM_9");  
	    		put("%?", "QUESTION");  
	    		put("%@", "AT");  
	    		put("%!", "EXCLAMATION");  
	    		put("%#", "HASH");  
	    		put("%$", "DOLLAR");  
	    		put("%]", "SQUARE_RIGHT_BRACKET");  
	    		put("%&", "AMPERSAND");  
	    		put("%(", "ROUND_LEFT_BRACKET");  
	    		put("%)", "ROUND_RIGHT_BRACKET");  
	    		put("%*", "STAR");  
	    		put("%+", "PLUS");  
	    		put("%^", "CARET");  
	    		put("%>", "MORE");  
	    		put("%<", "LESS");  
	    		put("%=", "EQUAL");  
	    		put("%~", "TILDE");  
	    	}  
    	};
    	
    /**
     * Constructor with fields.
     * 
     * @param key The key
     * @param value The value
     */
	public Pair(String key, String value){
		this.key = key;
		this.value = value;
	}
	
	
	public String getKey(){
		return key;
	}
	
	public String getXmlTag(){
		return KEY_MAPPING.get(key);
	}
	
	public String getValue(){
		return value;
	}
	
	public void setKey(String key){
		this.key = key;
	}
	
	public void setValue(String value){
		this.value = value;
	}
	
}
