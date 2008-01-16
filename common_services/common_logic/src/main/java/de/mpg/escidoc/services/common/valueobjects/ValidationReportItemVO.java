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

package de.mpg.escidoc.services.common.valueobjects;

/**
 * The report of an item validation.
 * 
 * @TODO FrM: Add comments to methods in EA and re-generate class.
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $ by $Author: jmueller $
 * @updated 05-Sep-2007 12:44:20
 */
public class ValidationReportItemVO extends ValueObject
{
    /**
     * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
     * 'de.mpg.escidoc.services.common.valueobjects.PubItemVO; local class incompatible: stream classdesc
     * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286' that occur after
     * JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to be compiled twice for testing (once
     * for the Application Server, once for the local test).
     * 
     * @author Johannes Mueller
     */
   

    /**
     * The possible info levels.
     * 
     * @updated 05-Sep-2007 12:44:20
     */
    public enum InfoLevel
    {
        INFORMATIVE, RESTRICTIVE
    }

    private String content;
    private InfoLevel infoLevel;
    /**
     * The information level of the certain validation report item. Informative means, this rule is a "SHOULD".
     * Restrictive means, this rule is a "MUST".
     */
    public InfoLevel level;

    public String getContent()
    {
        return content;
    }

    public InfoLevel getInfoLevel()
    {
        return infoLevel;
    }

    public boolean isInformative()
    {
        return false;
    }

    public boolean isRestrictive()
    {
        return false;
    }

    /**
     * @param newVal
     */
    public void setContent(String newVal)
    {
        content = newVal;
    }

    /**
     * @param newVal
     */
    public void setInfoLevel(InfoLevel newVal)
    {
        infoLevel = newVal;
    }
}