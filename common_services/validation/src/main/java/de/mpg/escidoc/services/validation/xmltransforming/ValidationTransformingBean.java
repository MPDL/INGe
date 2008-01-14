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

package de.mpg.escidoc.services.validation.xmltransforming;

import java.io.StringReader;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.RemoteBinding;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.xmlpull.v1.XmlPullParserException;

import de.mpg.escidoc.services.common.xmltransforming.exceptions.TransformingException;
import de.mpg.escidoc.services.common.xmltransforming.exceptions.UnmarshallingException;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportVO;

/**
 * EJB implementation of interface {@link ValidationTransforming}.
 *
 * @author mfranke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 126 $ $LastChangedDate: 2007-11-15 11:36:15 +0100 (Thu, 15 Nov 2007) $
 */
@Stateless
@Remote
@RemoteBinding(jndiBinding = ValidationTransforming.SERVICE_NAME)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ValidationTransformingBean implements ValidationTransforming
{
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(ValidationTransformingBean.class);

    /**
     * Default constructor.
     */
    public ValidationTransformingBean()
    {
        super();
    }

    private void logErrorDetails(final Throwable e)
    {
        LOGGER.error(e.getMessage());
        if (e instanceof JiBXException)
        {
            JiBXException jibxException = (JiBXException) e;
            if (jibxException.getRootCause() instanceof XmlPullParserException)
            {
                XmlPullParserException rootCause = (XmlPullParserException) jibxException.getRootCause();
                if (rootCause != null)
                {
                    LOGGER.error(rootCause.getMessage());
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public final ValidationReportVO transformToValidationReport(final String report)
        throws TransformingException
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("transformToValidationReport(String) - String report=" + report);
        }
        ValidationReportVO reportVO = null;
        try
        {
            IBindingFactory bfact = BindingDirectory.getFactory(ValidationReportVO.class);
            // unmarshal ValidationReportVO from String
            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
            StringReader sr = new StringReader(report);
            reportVO = (ValidationReportVO) uctx.unmarshalDocument(sr, null);
        }
        catch (JiBXException e)
        {
            logErrorDetails(e);
            throw new UnmarshallingException("ValidationReportVO", e);
        }
        catch (java.lang.ClassCastException e)
        {
            throw new UnmarshallingException("ValidationReportVO", e);
        }

        return reportVO;
    }
}
