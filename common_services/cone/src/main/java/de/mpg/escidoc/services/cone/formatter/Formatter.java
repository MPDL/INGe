/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.cone.formatter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerFactoryConfigurationError;

import de.mpg.escidoc.services.cone.ModelList.Model;
import de.mpg.escidoc.services.cone.util.Pair;
import de.mpg.escidoc.services.cone.util.TreeFragment;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public abstract class Formatter
{
    
    public static Formatter getFormatter(String format)
    {
        if ("html".equals(format.toLowerCase()))
        {
            return new HtmlFormatter();
        }
        else if ("jquery".equals(format.toLowerCase()))
        {
            return new JQueryFormatter();
        }
        else if ("json".equals(format.toLowerCase()))
        {
            return new JsonFormatter();
        }
        else if ("options".equals(format.toLowerCase()))
        {
            return new OptionsFormatter();
        }
        else if ("rdf".equals(format.toLowerCase()))
        {
            return new RdfFormatter();
        }
        else
        {
            throw new RuntimeException("Formatter for '" + format + "' not found");
        }
    }
    
    /**
     * Explain action to be implemented by a format servlet.
     * 
     * @param response The HTTP response piped through.
     * @throws FileNotFoundException From XSLT transformation.
     * @throws TransformerFactoryConfigurationError From XSLT transformation.
     * @throws IOException From XSLT transformation.
     */
    public abstract void explain(HttpServletResponse response)
        throws FileNotFoundException, TransformerFactoryConfigurationError, IOException, URISyntaxException;

    /**
     * Format the results of the query action.
     * 
     * @param pairs The results
     * @return A string that displays the given results in the current format.
     * @throws IOException From XSLT transformation.
     */
    public abstract String formatQuery(List<Pair> pairs) throws IOException;

    /**
     * Format the results of the details action.
     * 
     * @param id The id of the object.
     * @param model The current model.
     * @param triples The structure of the current object.
     * @param lang The selected language.
     * 
     * @return A string that displays the given results in the current format.
     * @throws IOException From XSLT transformation.
     */
    public abstract String formatDetails(String id, Model model, TreeFragment triples, String lang)
        throws IOException;
    
    /**
     * An implementing servlet should return the "Content-Type" header value of its format (e.g. "text/html"). 
     * 
     * @return The content type as string.
     */
    public abstract String getContentType();
    
}
