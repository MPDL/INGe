package de.mpg.escidoc.services.transformation.transformations.otherFormats.mab;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * provides the import of a MAB file 
 * 
 * @author kurt (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 3471 $ $LastChangedDate: 2010-08-10 14:15:21 +0200 (Di, 10 Aug 2010) $
 *
 */
public class MABImport{
	
    private String url = null;
    private Logger logger = Logger.getLogger(getClass());
    

    /**
     * Public Constructor MABImport.
     */
    public MABImport(){   	 
    	
    }
    
    /**
     * reads the import file and transforms the items to XML  
     * @return xml 
     */
    public String transformMAB2XML(String file){
    	String result = "";
    	
    	String[] itemList = getItemListFromString(file, "(\\n|\\r|\\r\\n)(\\s{4})([0-9]*.)?([0-9]*,[0-9]*)(\\n|\\r|\\r\\n)");    	// extract items to array
    	   for(String item: itemList){
    		//   System.out.print(item+"\n*******************************************");
    	   }
    	List<List<Pair>> items = new ArrayList<List<Pair>>();
    	if(itemList!=null && itemList.length>1){ //transform items to XML
    		
    		for (String item : itemList) {
    			List<Pair> itemPairs = getItemPairs(getItemFromString(item, "(\\s{6})[0-9]\\s*(.*(\\n|\\r|\\r\\n)(\\s{14}\\s*.*(\\n|\\r|\\r\\n))*)"));
    			for(Pair p : itemPairs){
    			//	System.out.print(p.getKey()+" : "+p.getValue()+"\n");
    			}
    			items.add(itemPairs);  
			}
    		result = transformItemListToXML(items); 
    		
    	}else if(itemList!=null && itemList.length==1){
    		List<Pair> item = getItemPairs(getItemFromString(itemList[0], "(\\s{6})[0-9]\\s*(.*(\\n|\\r|\\r\\n)(\\s{14}\\s*.*(\\n|\\r|\\r\\n))*)"));
    		result = transformItemToXML(item);
    	}
    	return result;
    }
       
    /**
     * reads the file and stores it in a string
     * @return List<String> with file lines
     */
    public String readFile()
    {
    	
    	String file = "";        	
    	try{    		
    		BufferedReader input = new BufferedReader(new FileReader(this.url));    	
    		// string buffer for file reading  
            String str;               
            // reading line by line from file   
            while ((str = input.readLine()) != null) {            	          	
            		file = file + "\n" +str;            
            }            
    	}catch(Exception e){    		
    		this.logger.error("An error occurred while reading MAB file.", e);
            throw new RuntimeException(e);
    	}	
    	return file;
    }
   
    
    /**
     * identifies item lines from input string and stores it in a List<String>
     * @param string
     * @return
     */
    public List<String> getItemFromString(String string, String patternString){    	
    	List<String> strArr = new ArrayList();
    	Pattern patternLine1 = Pattern.compile("(\\s{6})[###]\\s*(.*(\\n|\\r|\\r\\n))");
    	
    	Matcher matcherLine1 = patternLine1.matcher(string);
    	while(matcherLine1.find()){
    		strArr.add(matcherLine1.group());
    	}
    	
    	Pattern pattern = Pattern.compile("(\\s{6})[0-9]{3}\\s*(.*((\\n|\\r|\\r\\n)\\s{14}\\s*.*)*)");
    	Matcher matcher = pattern.matcher(string);   	
    	
    	while(matcher.find()){
    		strArr.add(matcher.group());
    	}
    	
    	return strArr;
    }
    
    /**
     * identifies MAB items from input string and stores it in an String Array
     * @param string 
     * @return
     */
    public String[] getItemListFromString(String string, String pattern){
    	
    	//replace first empty lines and BOM 
    	string = Pattern.compile("^.*?(\\w)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(string).replaceFirst("$1");
    	String itemList[] = string.split(pattern);
    	return itemList;
    }
    
    /**
     * get item pairs from item string (by regex string)
     * @param string - MAB item as string
     * @return String list with item key-value pairs
     */
    public List<Pair> getItemPairs(List<String> lines){
    	
    	List<Pair> pairList = new ArrayList();    	
    	if(lines !=null && lines.size()>0){
    		String line1 = lines.get(0);
    		Pair pair1 = createMABPairByString(line1,"(\\s{6})###\\s*");
    		pairList.add(pair1);
    		for (String line : lines) {
    			Pair pair = createMABPairByString(line,"([0-9]{3}\\s{5}|[0-9]\\s[a-z]\\s{3})");
    			pairList.add(pair);
			}
    	}    	
    	return pairList;
    }
    
    /**
     * get a pair from line string (by regex string)
     * @param string - MAB line as string
     * @return Pair - key-value pair created by string line
     */
    public Pair createMABPairByString(String line, String regex){
    	//String[] line1 = line.split("(\\s{6})[###]\\s*");
    	//String[] lineArr = line.split("([0-9]{3}\\s{5}|[0-9]\\s[a-z]\\s{3})");    	
    	String[] lineArr = line.split(regex);
    	Pair pair = null;
    	if(lineArr.length>1){
    		if(lineArr[0]!=null && lineArr[1]!=null){
    			String key = line.substring(0, 13).trim().replaceAll("\\s", "_").replaceAll("###", "raute");
    			
    			pair = new Pair("mab".concat(key), lineArr[1].trim());
    		//	System.out.print(pair.getKey()+" ::: "+pair.getValue()+"\n");
    		}
    	}
    	return pair;
    }
    
    /**
     * creates a single item in xml  
     * @param item pair list
     * @return xml string of the whole item list
     */
    public String transformItemToXML(List<Pair> item){
    	String xml = "";
    	if(item != null && item.size() > 0){
    		xml = createXMLElement("item",transformItemSubelementsToXML(item));    			   			
    	}
    	return xml;
    }
    
    /**
     * creates the complete item list in xml  
     * @param item pair list
     * @return xml string of the whole item list
     */
    public String transformItemListToXML(List<List<Pair>> itemList){
    	String xml = "<item-list>";
    	
    	if(itemList != null && itemList.size() > 0){
    		
    		for (List<Pair> item : itemList) {
    			xml = xml + "\n" + transformItemToXML(item);
			}
    	}
    	xml = xml + "</item-list>";
    	return xml;
    }
    
    /**
     * creates an xml string of the item pair list
     * @param item pairs as list
     * @return xml String
     */
    public String transformItemSubelementsToXML(List<Pair> item){
    	String xml = "";
    	if(item != null && item.size() > 0){ 	
    		  
    		for (Pair pair : item) {
    			String key = "";
    			String value = "";
    			if(pair!=null){    			
    			if(pair.getKey()!=null){
    				key = pair.getKey();
    			}	
    			if(pair.getValue()!=null){
    				value = pair.getValue();
    			}
    			}
    			xml = xml + createXMLElement(key,escape(value));
    			
			}
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
    	if(tag!=null && tag!=""){
    		
    		element = "<"+tag+">"+value+"</"+tag+">";    		
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
    
   
}


