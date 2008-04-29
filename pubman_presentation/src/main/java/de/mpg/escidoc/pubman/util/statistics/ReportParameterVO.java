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

package de.mpg.escidoc.pubman.util.statistics;

import de.mpg.escidoc.services.common.valueobjects.ValueObject;



/**
 * TODO Description
 *
 * @author haarlae1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ReportParameterVO extends ValueObject
{
    public enum ParamType
    {
        STRINGVALUE, DATEVALUE, DECIMALVALUE
    }
    
    private ParamType paramType;
    private String name;
    private String value;
    
    
    
    public ReportParameterVO(ParamType type, String name, String value){
        super();
        this.paramType = type;
        this.name=name;
        this.value=value;
    }
    
    public ReportParameterVO() {
        super();
    }

    public ParamType getParamType() 
    {
        return paramType;
    }
    
    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        
        return value;
    }

    public void setParamType(ParamType paramType)
    {
        this.paramType = paramType;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}
