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

package de.mpg.escidoc.services.pubman.valueobjects;

import java.util.List;

/**
 * date criterion vo for the advanced search
 * @created 15-Mai-2007 15:46:13
 * @author NiH
 * @version 1.0
 * Revised by NiH: 13.09.2007
 */
public class DateCriterionVO extends CriterionVO
{
	/** serial for the serializable interface*/
	private static final long serialVersionUID = 1L;
	
    public enum DateType
    {
        ACCEPTED, CREATED, MODIFIED, PUBLISHED_ONLINE, PUBLISHED_PRINT, SUBMITTED
    }
    
    //date range for the search criterion
    private String from;
	private String to;
    //type of date
    private List<DateType> dateTypeList;

	/**
	 * constructor.
	 */
	public DateCriterionVO()
    {
        super();
	}

	public String getFrom()
    {
		return from;
	}

	public String getTo()
    {
		return to;
	}

	public void setFrom(String newVal)
    {
		from = newVal;
	}

	public void setTo(String newVal)
    {
		to = newVal;
	}

    public List<DateType> getDateType()
    {
        return dateTypeList;
    }

    public void setDateType(List<DateType> dateType)
    {
        this.dateTypeList = dateType;
    }
    
    private String getSearchIdentifierByDateType( DateType datetype )
    {
        switch( datetype ) 
        {
            case ACCEPTED:
                return "accepted";
            case CREATED:
                return "created";
            case MODIFIED:
                return "modified";
            case PUBLISHED_ONLINE: 
                return "published-online";
            case PUBLISHED_PRINT:
                return "published-in-print";
            case SUBMITTED:
                return "submitted";
            default: 
                    return "";
        }
    }
    
    
    public String getSearchIdentifier( int position )
    {
        if( dateTypeList.size() <= position  )
        {
            return "";
        }
        else
        {
            return this.getSearchIdentifierByDateType( this.dateTypeList.get( position ) );
        }
    }
}