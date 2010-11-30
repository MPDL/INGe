package de.mpg.escidoc.pubman.util;

import java.util.EmptyStackException;
import java.util.Stack;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.*;
import de.mpg.escidoc.services.citationmanager.utils.Utils;

public class HTMLSubSupConverter implements Converter{
    public static final String CONVERTER_ID = "HTMLSubSupConverter";
    
	private static final String startSub = "<sub>";
	private static final String endSub = "</sub>";
	private static final String startSup = "<sup>";
	private static final String endSup = "</sup>";
	
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
		if(checkTag(snippet, startSub, endSub, startSup, endSup))
		{
			snippet = Utils.replaceAllTotal(snippet, "\\<(?!(\\/?style)|(\\/?su[bp]))", "&lt;");
		}
		else
		{
			snippet = Utils.replaceAllTotal(snippet, "\\<(?!(\\/?style))", "&lt;");
		}
//		snippet = Utils.replaceAllTotal(snippet, "\\<((\\/?su[bp]))\\>", "");
		return snippet;
	}
	
	public static boolean checkTag(String snippet, String start, String end, String start2, String end2) {
		Stack s = new Stack(); // Create an empty stack.
		boolean balanced = true;
		try {
			if(balanced){
				for (int index=0; index < snippet.length(); index++) 
				{
					if (index+start.length()<=snippet.length() && (start.equals(snippet.substring(index,index+start.length())) || start2.equals(snippet.substring(index,index+start.length()))) )
						{
							s.push(snippet.substring(index, index+start.length()));
							index += start.length()-1;
						} 
					
					if (index +end.length()<=snippet.length() && (end.equals(snippet.substring(index, index+end.length())) || end2.equals(snippet.substring(index, index+end.length()))) )
						{
							if(s.lastElement().toString().substring(1, 4).equals(snippet.substring(index+2, index+end.length()-1)))
							{
								s.pop();
								index += end.length()-1;
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



}
 