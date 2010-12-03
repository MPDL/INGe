package de.mpg.escidoc.pubman.util;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.*;
import de.mpg.escidoc.services.citationmanager.utils.Utils;
import de.mpg.escidoc.services.common.util.HtmlUtils;

public class HTMLSubSupConverter implements Converter{
    public static final String CONVERTER_ID = "HTMLSubSupConverter";
    
	public HTMLSubSupConverter()
	{
		
	}

	public Object getAsObject(FacesContext arg0, UIComponent arg1, String text) 
	{
		return null;
	}
	
	public String getAsString(FacesContext arg0, UIComponent arg1, Object object) 
	{
        String snippet = (String) object;
		snippet = Utils.replaceAllTotal(snippet, "\\&(?!amp;)", "&amp;");
		if(HtmlUtils.isBalanced(snippet))
		{
			snippet = Utils.replaceAllTotal(snippet, "\\<(?!(\\/?su[bp]))", "&lt;");
		}
		else
		{
			snippet = Utils.replaceAllTotal(snippet, "\\<", "&lt;");
		}
//		snippet = Utils.replaceAllTotal(snippet, "\\<((\\/?su[bp]))\\>", "");
		return snippet;
	}
	
	public static boolean checkSubSupTag(String snippet) {
		String startSub = "<sub>";
		String endSub = "</sub>";
		String startSup = "<sup>";
		String endSup = "</sup>";
		int sLength = 5;
		int eLength = 6;
		Stack s = new Stack(); // Create an empty stack.
		boolean balanced = true;
		try {
			if(balanced){
				for (int index=0; index < snippet.length(); index++) 
				{
					if (index+sLength<=snippet.length() && (startSub.equals(snippet.substring(index,index+sLength)) || startSup.equals(snippet.substring(index,index+sLength))) )
						{
							s.push(snippet.substring(index, index+sLength));
							index += sLength-1;
						} 
					
					if (index +eLength<=snippet.length() && (endSub.equals(snippet.substring(index, index+eLength)) || endSup.equals(snippet.substring(index, index+eLength))) )
						{
							if(s.lastElement().toString().substring(1, 4).equals(snippet.substring(index+2, index+eLength-1)))
							{
								s.pop();
								index += eLength-1;
							}
							else
								balanced = false;
						}
				}
			}
		} 
		catch (EmptyStackException ex) {
			balanced = false;
		}
		return (balanced && s.empty());
	}
	
	public boolean checkTag(String tag)
	{
		
	
		String snippet="Test Bo&ok about H<sub>2</sub>O with <b>Kap1</b> <i>italic</i>111";
		
		if(snippet.matches("\\<sub\\>|\\<sup\\>"))
		{
			int charnumber = 0;
			List<Integer> openings = new ArrayList<Integer>();
			List<Integer> closing = new ArrayList<Integer>();
			
			int openingmatchPos = 0;
			while  (openingmatchPos!=-1)
			{
				openingmatchPos = snippet.indexOf("<"+ tag +">");
				if(openingmatchPos!=-1)
				{
					openings.add(openingmatchPos);
				}
			}
			
			int closingmatchPos = 0;
			while  (closingmatchPos!=-1)
			{
				closingmatchPos = snippet.indexOf("</" + tag + ">");
				if(closingmatchPos!=-1)
				{
					closing.add(closingmatchPos);
				}
			}
	
			if(openings.size()==closing.size())
			{
				
				for (int i=0; i<openings.size(); i++) {
					if(openings.get(i)>closing.get(i))
					{
						return false;
					}
				}
			}
			else
			{
				return false;
			}
			
		}
		return true;
	}
	
	public static void main(String[] args)
	{
		
		String snippet="Test Bo&ok about H<sub>2</sub>O with <b>Kap1</b> <i>italic</i>111";
		
		if(snippet.matches("\\<sub\\>|\\<sup\\>"))
		{
			int charnumber = 0;
			List<Integer> openings = new ArrayList<Integer>();
			List<Integer> closing = new ArrayList<Integer>();
			
			int openingmatchPos = 0;
			while  (openingmatchPos!=-1)
			{
				openingmatchPos = snippet.indexOf("<sub>");
				if(openingmatchPos!=-1)
				{
					openings.add(openingmatchPos);
				}
			}
			
			int closingmatchPos = 0;
			while  (closingmatchPos!=-1)
			{
				closingmatchPos = snippet.indexOf("</sub>");
				if(closingmatchPos!=-1)
				{
					closing.add(closingmatchPos);
				}
			}

			if(openings.size()==closing.size())
			{
				
				for (int i=0; i<openings.size(); i++) {
					if(openings.get(i)<closing.get(i))
					{
						
					}
				}
			}
		}
		
		
		/*
		snippet = snippet.replaceAll("<", "&lt;");
		StringReader reader = new StringReader(snippet);
		String substring;
		int c = 0;
        int opened=0;
        int closed=0;
        String tag ="";
        
		while ((c = reader.read()) != -1)
        {
            substring += (char)c;
            if (substring.matches("\\&lt;" + tag + ">"))
    		{
            	opened++;
            	substring="";
            	
    		}
            if (substring.matches("\\&lt;\\/" + tag + ">"))
    		{
            	closed++;
            	substring="";
    		}
    	
        }
		*/
		//snippet = Utils.replaceAllTotal(snippet, "\\<", "&lt;");
		//snippet = Utils.replaceAllTotal(snippet, "((?<=\\&lt;sub\\>)[^\\&lt;sub\\>]*?(?=\\&lt;\\/sub\\>))", "<sub>$1</sub>");
		
		//Pattern p = Pattern.compile("(\\&lt;sub\\>)^(.*\\&lt;sub\\>.*)(\\&lt;\\/sub\\>)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		
		/*
		Pattern p = Pattern.compile("\\&lt;((?=sub\\>.*(?![&lt;|\\<]sub>).*?\\&lt;\\/sub\\>){1})", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		 Matcher m = p.matcher(snippet);
		 snippet =  m.replaceAll("<");
		 */
	     //snippet=snippet.replaceAll("\\&lt;\\/sub>", "</sub>")
		 
		 //snippet =  m.replaceAll("<");
		 
		
		 //System.out.println(snippet);

		//snippet = Utils.replaceAllTotal(snippet, "(?=\\<sub\\>.*?)\\<(?=\\/sub\\>)", "XX");
		
		//System.out.println(snippet);
	}



}
 