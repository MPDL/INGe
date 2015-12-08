package de.mpg.escidoc.services.tools.scripts.csl_transformation_service;

/**
 * 
 * Utils for HTML
 *
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class HtmlUtils
{
    private static final String[] PROBLEMATIC_CHARACTERS = { "&", ">", "<", "\"", "'", "/" };
    private static final String[] ESCAPED_CHARACTERS = { "&amp;", "&gt;", "&lt;", "&quot;", "&#x27;", "&#x2F;" };

    /**
     * Escape String for use in an HTML document 
     * @param cdata
     * @return
     */
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

