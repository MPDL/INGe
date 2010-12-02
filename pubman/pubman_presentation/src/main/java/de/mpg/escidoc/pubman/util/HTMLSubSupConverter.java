package de.mpg.escidoc.pubman.util;

import java.util.EmptyStackException;
import java.util.Stack;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.*;
import de.mpg.escidoc.services.citationmanager.utils.Utils;

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
		if(checkSubSupTag(snippet))
		{
			snippet = Utils.replaceAllTotal(snippet, "\\<(?!|(\\/?su[bp]))", "&lt;");
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



}
 