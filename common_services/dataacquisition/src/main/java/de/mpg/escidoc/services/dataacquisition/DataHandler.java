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
 * Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.escidoc.services.dataacquisition;

import java.rmi.AccessException;

import de.mpg.escidoc.services.dataacquisition.exceptions.FormatNotAvailableException;
import de.mpg.escidoc.services.dataacquisition.exceptions.FormatNotRecognisedException;
import de.mpg.escidoc.services.dataacquisition.exceptions.IdentifierNotRecognisedException;
import de.mpg.escidoc.services.dataacquisition.exceptions.SourceNotAvailableException;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

/**
 * Interface for fetching data from external systems.
 * 
 * @author Friederike Kleinfercher (initial creation)
 */
public interface DataHandler
{
    String SERVICE_NAME = "ejb/de/mpg/escidoc/services/dataacquisition/DataHandler";

    /**
     * This method provides XML formated output of the supported import sources.
     * 
     * @return xml presentation of all available import sources
     * @throws RuntimeException
     */
    String explainSources() throws RuntimeException;

    /**
     * This operation fetches data from the specified source. 
     * The format of the requested data will be the default metadata format defined in sources.xml.
     * 
     * @param sourceName 
     * @param identifier
     * @return itemXML as byte[]
     * @throws SourceNotAvailableException
     * @throws IdentifierNotRecognisedException
     * @throws FormatNotRecognisedException
     * @throws RuntimeException
     * @throws FormatNotAvailableException
     */
    byte[] doFetch(String sourceName, String identifier) throws SourceNotAvailableException,
            IdentifierNotRecognisedException, FormatNotRecognisedException, 
            RuntimeException, AccessException, FormatNotAvailableException;

    /**
     * This operation fetches data from the specified source and returns it in the requested format. 
     * The format properties are default
     * This format can either be the format the external source provides, or a format we can transform 
     * from a format the external source provides
     * 
     * @param sourceName
     * @param identifier
     * @param formatName
     * @return fetched data as byte[]
     * @throws SourceNotAvailableException
     * @throws IdentifierNotRecognisedException
     * @throws FormatNotRecognisedException
     * @throws RuntimeException
     * @throws FormatNotAvailableException
     */
    byte[] doFetch(String sourceName, String identifier, String formatName) throws SourceNotAvailableException,
            IdentifierNotRecognisedException, FormatNotRecognisedException, 
            RuntimeException, AccessException, FormatNotAvailableException;

    /**
     * This operation fetches data from the specified source and returns it in the requested format. 
     * This format can either be the format the external source provides, or a format we can transform 
     * from a format the external source provides
     * 
     * @param sourceName
     * @param identifier
     * @param trgFormatName
     * @param trgFormatType
     * @param trgFormatEndcoding
     * @return fetched data as byte[]
     * @throws SourceNotAvailableException
     * @throws IdentifierNotRecognisedException
     * @throws FormatNotRecognisedException
     * @throws RuntimeException
     * @throws FormatNotAvailableException
     */
    byte[] doFetch(String sourceName, String identifier, String trgFormatName, String trgFormatType, String trgFormatEncoding) throws SourceNotAvailableException,
            IdentifierNotRecognisedException, FormatNotRecognisedException, 
            RuntimeException, AccessException, FormatNotAvailableException;

    
    /**
     * This operation fetches data from the specified source and returns it in the requested format. 
     * The fetched data will return in zip format, currently only file fetching is possible for multiple formats
     * 
     * @param sourceName
     * @param identifier
     * @param formats[]
     * @return fetched data as byte[]
     * @throws SourceNotAvailableException
     * @throws IdentifierNotRecognisedException
     * @throws FormatNotRecognisedException
     * @throws RuntimeException
     * @throws AccessException
     * @throws FormatNotAvailableException
     */
    byte[] doFetch(String sourceName, String identifier, Format[] formats) throws SourceNotAvailableException,
            IdentifierNotRecognisedException, FormatNotRecognisedException, 
            RuntimeException, AccessException, FormatNotAvailableException;
    
    /**
     * This operation fetches data from the specified source and returns it in the requested format. 
     * The fetched data will return in zip format, currently only file fetching is possible for multiple formats
     * The formats properties are default
     * 
     * @param sourceName
     * @param identifier
     * @param formats[]
     * @return fetched data as byte[]
     * @throws SourceNotAvailableException
     * @throws IdentifierNotRecognisedException
     * @throws FormatNotRecognisedException
     * @throws RuntimeException
     * @throws AccessException
     * @throws FormatNotAvailableException
     */
    byte[] doFetch(String sourceName, String identifier, String[] formats) throws SourceNotAvailableException,
            IdentifierNotRecognisedException, FormatNotRecognisedException, 
            RuntimeException, AccessException, FormatNotAvailableException;
}
