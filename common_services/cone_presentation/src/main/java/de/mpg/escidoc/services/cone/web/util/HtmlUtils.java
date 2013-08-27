package de.mpg.escidoc.services.cone.web.util;



public class HtmlUtils {
	
	private static final String[] PROBLEMATIC_CHARACTERS =
	    {
	        "&", ">", "<", "\"", "'", "/"
	    };
	    private static final String[] ESCAPED_CHARACTERS =
	    {
	        "&amp;", "&gt;", "&lt;", "&quot;", "&#x27;", "&#x2F;"
	    };
	
	    
	    
	    public static String escapeHtml(String cdata)
	    {
	        if (cdata == null)
	        {
	            return null;
	        }
	        // The escaping has to start with the ampersand (&amp;, '&') !
	        for (int i = 0; i < PROBLEMATIC_CHARACTERS.length; i++)
	        {
	            cdata = cdata.replace(PROBLEMATIC_CHARACTERS[i], ESCAPED_CHARACTERS[i]);

	        }
	        return cdata;
	    }
}
