package de.mpg.escidoc.services.transformation.transformations.otherFormats.endnote;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * provides the import of a EndNote file 
 * 
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class EndNoteImport
{
	
    private String url = null;
    private Logger logger = Logger.getLogger(getClass());
    

    /**
     * reads the import file and transforms the items to XML  
     * @return xml 
     */
    public String transformEndNote2XML(String file){
    	String result = "";
    	
    	List<String> itemList = splitItems(file);    	
    	if(itemList!=null && itemList.size()>1){ //transform items to XML
        	List<List<Pair>> itemPairsList = new ArrayList<List<Pair>>();
    		for(String s: itemList)
    		{
        		List<Pair> itemPairs = getItemPairs(splitItemElements(s));
        		itemPairsList.add(itemPairs);    			
    		}   
    		result = transformItemPairsListToXML(itemPairsList); 
    	}
    	else if(itemList!=null && itemList.size()==1)
    	{
	    		List<Pair> itemPairs = getItemPairs(splitItemElements(itemList.get(0)));
	    		result = transformItemToXML(itemPairs);
    	}
    	return result;
    }
       
    /**
     * reads the file and stores it in a string
     * @return List<String> with file lines
     */
    public String readFile(){
    	
    	String file = "";        	
    	try
    	{    		
    		BufferedReader input = new BufferedReader(new FileReader(this.url));    	
    		// string buffer for file reading  
            String str;               
            // reading line by line from file   
            while ((str = input.readLine()) != null) {            	          	
            		file = file + "\n" +str;            
            }            
    	}
    	catch(Exception e)
    	{    		
    		this.logger.error("An error occurred while reading EndNote file.", e);
            throw new RuntimeException(e);
    	}	
    	return file;
    }
   

    /**
     * Splits EndNote items and puts them into List<String>
     * @param itemsStr item list string  
     * @return
     */
    public List<String> splitItems(String itemsStr)
    {
    	List<String> l = new ArrayList<String>();
    	String pattern = "%0";
    	for (String s: itemsStr.split(pattern) )
    		if ( s.length() > 1 && checkVal(s) )
    			l.add(pattern + s);
    	return l;
    }
    
    /**
     * Splits EndNote fields of an item and puts them into List<String>
     * @param itemStr - item string
     * @return 
     */
    public List<String> splitItemElements(String itemStr){    	
    	   	
    	Pattern p = Pattern.compile("(\\n%\\S.*?)(?=%\\S)", Pattern.DOTALL);
    	Matcher m = p.matcher("\n" + itemStr + "%STOP"); 
    	List<String> l = new ArrayList<String>();
    	while (m.find()) 
    	{
			String s = m.group();
    		if ( checkVal(s) && s.length() > 1  )
    			l.add(s.trim());
		}
    	return l;
    }
    
    /**
     * get item pairs from item string and pack them into the <code>List</code> 
     * @param string - EndNote item as string
     * @return String list with item key-value pairs
     */
    public List<Pair> getItemPairs(List<String> lines){
    	
    	List<Pair> pairList = new ArrayList<Pair>();    	
    	if(lines != null){
    		for(String line: lines)
    		{
    			Pair p = createEndNotePairByString(line);
    			if ( p != null )
    				pairList.add(p);
    		}
    	}    	
    	return pairList;
    }
    
    /**
     * get a EndNote <code>Pair</code> from line string 
     * @param string - EndNote line as string
     * @return Pair - key-value pair created by string line
     */
    public Pair createEndNotePairByString(String line){
    	Pattern p = Pattern.compile("^(%\\S)\\s+(.*)$", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    	Matcher m = p.matcher(line);
    	if (m.find())
    	{
    		return new Pair(m.group(1), m.group(2));
    	}
    	return null;
    }
    
    /**
     * creates a single item in xml  
     * @param item pair list
     * @return xml string of the whole item list
     */
    public String transformItemToXML(List<Pair> item)
    {
    	String xml = "";
    	if(item != null && item.size() > 0)
    	{
    		xml = createXMLElement("item", transformItemSubelementsToXML(item));    			   			
    	}
    	return xml;
    }
    
    /**
     * creates the complete item list in xml  
     * @param item pair list
     * @return xml string of the whole item list
     */
    public String transformItemPairsListToXML(List<List<Pair>> itemList)
    {
    	String xml = "";
    	if(itemList != null && itemList.size() > 0)
    		for (List<Pair> lp: itemList)
    			xml += transformItemToXML(lp);
    	return createXMLElement("item-list", xml);
    }
    
    /**
     * creates an xml string of the item pair list
     * @param item pairs as list
     * @return xml String
     */
    public String transformItemSubelementsToXML(List<Pair> item)
    {
    	String xml = "";
    	if(item != null && item.size() > 0)
    	{    		
    		for(Pair p: item)
    			xml += createXMLElement( p.getXmlTag(), escape(p.getValue()));
    	}
    	return xml;
    }
    
    /**
     * creates a single element in xml 
     * @param tag - tag name of the element
     * @param value - value of the element
     * @return xml element as string
     */
    public String createXMLElement(String tag, String value){
    	String element = "";
    	if(checkVal(tag))
    	{
    		element = "\n<"+tag+">"+value+"</"+tag+">";    		
    	}
    	return element;
    }
    
    /**
     * escapes special characters
     * @param input string
     * @return string with escaped characters
     */
    public String escape(String input)
    {
        if(input != null){
            input = input.replace("&", "&amp;");
            input = input.replace("<", "&lt;");
            input = input.replace("\"", "&quot;");
        }
        return input;
    }
    
    public static boolean checkVal(String val)
    {
    	return ( val != null && !val.trim().equals("") );
    }

    public static boolean checkLen(String val)
    {
    	return ( val != null && val.length()>0 );
    }    
   
}


