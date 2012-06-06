package de.mpg.escidoc.services.transformation.transformations.otherFormats.mab;

/**
 * A key-value pair.
 * 
 * @author kurt (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 3183 $ $LastChangedDate: 2010-05-27 16:10:51 +0200 (Do, 27 Mai 2010) $
 *
 */
public class Pair{
	
	private String key;
    private String value;
    
    
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
	
	public Pair(){
    	
    }
	
	public String getKey(){
		return key;
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
