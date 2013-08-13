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
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.escidoc.pubman.util;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * SourceVOPresentation defines some presentation specific methods expanding the extended SourceVO class
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SourceVOPresentation extends SourceVO
{
    /**
     * default serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    private static Properties properties;

    /**
     * get the negative list of source genres as Map for this (server-) instance, depending on the
     * source_genres.properties definitions
     * 
     * @return Map filled with all source genres which will be excluded
     */
    public static Map<String, String> getExcludedSourceGenreMap()
    {
        if (properties == null || properties.isEmpty())
        {
            properties = loadExcludedSourceGenreProperties();
        } 
        @SuppressWarnings({ "unchecked", "rawtypes" })
        Map<String, String> propertiesMap = new HashMap<String, String>((Map)properties);
        return propertiesMap;
    }

    /**
     * get the negative list of source genres as properties for this (server-) instance, depending on the
     * source_genres.properties definitions
     * 
     * @return Properties filled with all source genres which will be excluded
     */
    private static Properties loadExcludedSourceGenreProperties()
    {
        properties = new Properties();
        URL contentCategoryURI = null;
        try
        {
            contentCategoryURI = SourceVOPresentation.class.getClassLoader().getResource("source_genres.properties");
            if (contentCategoryURI != null)
            {
                Logger.getLogger(SourceVOPresentation.class).info(
                        "Source genre properties URI is " + contentCategoryURI.toString());
                InputStream in = PropertyReader.getInputStream(contentCategoryURI.getPath().toString(),
                        SourceVOPresentation.class);
                properties.load(in);
                properties.putAll(properties);
                in.close();
                Logger.getLogger(SourceVOPresentation.class).info(
                        "Source genre properties loaded from " + contentCategoryURI.toString());
            }
            else
            {
                Logger.getLogger(SourceVOPresentation.class).debug("Source genre properties file not found.");
            }
        }
        catch (Exception e)
        {
            Logger.getLogger(SourceVOPresentation.class).warn(
                    "WARNING: Source genre properties not found: " + e.getMessage());
        }
        return properties;
    }
}
