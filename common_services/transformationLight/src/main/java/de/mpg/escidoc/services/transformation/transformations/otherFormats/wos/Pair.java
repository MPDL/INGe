package de.mpg.escidoc.services.transformation.transformations.otherFormats.wos;

/**
 * A key-value pair.
 * 
 * @author kurt (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 1953 $ $LastChangedDate: 2009-05-07 10:40:57 +0200 (Do, 07 Mai 2009) $
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
