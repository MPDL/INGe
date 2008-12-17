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

package de.mpg.escidoc.pubman.editItem;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;

/**
 * Converter for converting Enums to Strings and vice versa.
 * 
 * @author: Thomas Diebäcker, created 10.01.2007
 * @version: $Revision$ $LastChangedDate$
 * Revised by DiT: 14.08.2007
 */
public class EnumConverter implements Converter
{
    private static Logger logger = Logger.getLogger(EnumConverter.class);

    private Object[] valueList = null;
    
    /**
     * Public constructor.
     */
    public EnumConverter()
    {
    }

    /**
     * Public constructor.
     * @param valueList the list with possible values of the enum
     */
    public EnumConverter(Object[] valueList)
    {
        this.valueList = valueList;        
    }

    /**
     * Converts a string value to an object out of the possible objects in the valueList.
     * @param facesContext the current FacesContext
     * @param uiComponent the uiComponent with the possible values
     * @param string the string value which should be converted
     */
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String string)
    {
        Object retVal = null;
        
        //try to guess the valueList if it has not been given in the constructor (the converter is invoked by a JSP, not a JAVA class)
        if (this.valueList == null && string.length() > 0)
        {
            this.valueList = this.guessValueList(string);
        }
        
        if (this.valueList != null)
        {
            for (int i=0; i<this.valueList.length; i++)
            {
                Object valueListObject = this.valueList[i];
                String valueListString = valueListObject.toString();
                if (valueListString.compareTo(string) == 0)
                {
                    retVal = valueListObject;
                    break;
                }
            }
        }
        else if (string.length() == 0)
        {
            retVal = null;
        }
        else
        {
            logger.warn("ValueList is NULL. Cannot convert string '" + string + "' to object!");
        }
        
        
        return retVal;
    }

    /**
     * Converts an object to a string value.
     * @param facesContext the current FacesContext
     * @param uiComponent the uiComponent with the possible values
     * @param string the string value which should be converted
     */
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object object)
    {
        if (object == null)
        {
            return "";
        }
        else
        {
            return object.toString();
        }
    }

    /**
     * Tries to guess the enum that is used by searching all known lists for the String.
     * @param searchString the string to search for in all lists
     * @return Array of objects of which at least one contained the searchString
     */
    private Object[] guessValueList(String searchString)
    {
        // genre
        try
        {
            MdsPublicationVO.Genre.valueOf(searchString);
            return MdsPublicationVO.Genre.values();
        }
        catch (IllegalArgumentException e) {}
        
        // degreeType
        try
        {
            MdsPublicationVO.DegreeType.valueOf(searchString);
            return MdsPublicationVO.DegreeType.values();
        }
        catch (IllegalArgumentException e) {}
        
        // reviewMethod
        try
        {
            MdsPublicationVO.ReviewMethod.valueOf(searchString);
            return MdsPublicationVO.ReviewMethod.values();
        }
        catch (IllegalArgumentException e) {}
        
        // invitationstatus
        try
        {
            EventVO.InvitationStatus.valueOf(searchString);
            return EventVO.InvitationStatus.values();
        }
        catch (IllegalArgumentException e) {}
        
        
        logger.warn("ValueList for searchString '" + searchString + "' is unknown. Did you add a new comboBox in a JSP with a converter and forgot to add the possible values in the guessValueList() method?");
        
        return null;
    }
}
