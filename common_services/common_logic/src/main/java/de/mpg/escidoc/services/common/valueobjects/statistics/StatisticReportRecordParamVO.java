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
* Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/
package de.mpg.escidoc.services.common.valueobjects.statistics;

import de.mpg.escidoc.services.common.valueobjects.ValueObject;

/**
 * VO class representing a parameter of a statistic report-record or a report-parameters
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class StatisticReportRecordParamVO extends ValueObject
{
    
    
   
    private StatisticReportRecordParamValueVO paramValue;
    protected String name;
    

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }


    public StatisticReportRecordParamValueVO getParamValue()
    {
        return paramValue;
    }

    public void setParamValue(StatisticReportRecordParamValueVO value)
    {
        this.paramValue = value;
    }
    
    public boolean isStringValue()
    {
        return (paramValue instanceof StatisticReportRecordStringParamValueVO);
       
    }
    
    public boolean isDecimalValue()
    {
        return (paramValue  instanceof StatisticReportRecordDecimalParamValueVO);
       
    }
    
    public boolean isDateValue()
    {
        return (paramValue  instanceof StatisticReportRecordDateParamValueVO);
       
    }

    public StatisticReportRecordParamVO(String name, StatisticReportRecordParamValueVO paramValue)
    {
        super();
        this.paramValue = paramValue;
        this.name = name;
    }
    
    public StatisticReportRecordParamVO()
    {
        super();
    }
    
   
    
   
    

   
    
}
