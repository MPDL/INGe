package de.mpg.escidoc.services.util;

import java.io.FileNotFoundException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import de.mpg.escidoc.services.common.util.ResourceUtil;
/**
 * This class handle URIs in XSLT stylesheets such as xsl:import.
 * In a jar the stylesheet can only be loaded as InputStream.
 * Without this URIResolver it is not possible to work with import statements.
 *
 * @author mfranke
 * @author $Author: mfranke $
 * @version $Revision: 131 $$LastChangedDate: 2007-11-21 18:53:43 +0100 (Wed, 21 Nov 2007) $
 */
public class LocalURIResolver implements URIResolver
{

    /**
     * {@inheritDoc}
     */
    public final Source resolve(final String href, final String base) throws TransformerException
    {
        try
        {
            Source source = new StreamSource(ResourceUtil.getResourceAsStream(base + "/" + href));

            return source;
        }
        catch (FileNotFoundException e)
        {
            throw new TransformerException("Cannot resolve URI: " + base + "/" + href);
        }
    }
}
