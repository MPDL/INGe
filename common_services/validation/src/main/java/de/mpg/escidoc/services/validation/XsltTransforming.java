/*
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

package de.mpg.escidoc.services.validation;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;

/**
 * Utility class for XSLT transformations.
 *
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 123 $ $LastChangedDate: 2007-11-14 10:58:02 +0100 (Wed, 14 Nov 2007) $
 *
 */
public final class XsltTransforming
{

     /**
     * XSLT transformer factory.
     */
    private static TransformerFactory factory = TransformerFactory.newInstance();

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(XsltTransforming.class);

    /**
     * Hide default constructor.
     */
    private XsltTransforming()
    {

    }

    /**
     * Transforms a given xml and xsl without parameters.
     *
     * @param xml The xml to be transformed.
     * @param xsl The xslt stylesheet.
     * @throws TechnicalException Mostly parse exceptions.
     * @return The transformation result as StringWriter.
     */
    public static StringWriter transform(final String xml, final String xsl) throws TechnicalException
    {

        return transform(xml, xsl, null);
    }

    /**
     * Transforms a given xml and xsl with parameters.
     *
     * @param xml The xml to be transformed.
     * @param xsl The xslt stylesheet.
     * @param params xslt parameter.
     * @throws TechnicalException Mostly parse exceptions.
     * @return The transformation result as StringWriter.
     */
    public static StringWriter transform(
            final String xml,
            final String xsl,
            final Map <String, String> params) throws
            TechnicalException
    {

        try
        {
            Transformer t = factory.newTransformer(new StreamSource(new StringReader(xsl)));
            return transform(xml, t, params);
        }
        catch (TransformerConfigurationException tce)
        {
            throw new TechnicalException(tce);
        }
    }

    /**
     * Transforms a given xml and xsl with parameters.
     *
     * @param xml The xml to be transformed.
     * @param t The xslt stylesheet transformer.
     * @param params xslt parameter.
     * @throws TechnicalException Mostly parse exceptions.
     * @return The transformation result as StringWriter.
     */
    public static synchronized StringWriter transform(
            final String xml,
            final Transformer t,
            final Map <String, String> params) throws
            TechnicalException
    {

        StringWriter result = new StringWriter();
        try
        {
            if (params != null)
            {
                for (Iterator iter = params.keySet().iterator(); iter.hasNext();)
                {
                    String element = (String) iter.next();
                    t.setParameter(element, params.get(element));
                }
            }

            //LOGGER.debug("Now transforming: " + xml);
            LOGGER.debug("Transformer: " + t);

            result = new StringWriter();

            t.transform(new StreamSource(new StringReader(xml)), new StreamResult(result));

            return result;
        }
        catch (TransformerConfigurationException tce)
        {
            tce.printStackTrace();
            throw new TechnicalException(tce);
        }
        catch (TransformerException te)
        {
            te.printStackTrace();
            throw new TechnicalException(te);
        }
        catch (Exception e)
        {
            throw new TechnicalException("Unexpected exception", e);
        }
    }

}
