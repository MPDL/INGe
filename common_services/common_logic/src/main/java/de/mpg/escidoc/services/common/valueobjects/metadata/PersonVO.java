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

package de.mpg.escidoc.services.common.valueobjects.metadata;

import de.mpg.escidoc.services.common.valueobjects.ValueObject;

/**
 * @revised by MuJ: 27.08.2007
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $ by $Author: jmueller $
 * @updated 05-Sep-2007 12:48:57
 */
public class PersonVO extends ValueObject implements Cloneable
{
    /**
     * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
     * 'de.mpg.escidoc.services.common.valueobjects.ItemVO; local class incompatible: stream classdesc
     * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286' that occur after
     * JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to be compiled twice for testing (once
     * for the Application Server, once for the local test).
     * 
     * @author Johannes Mueller
     */
    private static final long serialVersionUID = 1L;
    private String completeName;
    private String givenName;
    private String familyName;
    private java.util.List<String> alternativeNames = new java.util.ArrayList<String>();
    private java.util.List<String> titles = new java.util.ArrayList<String>();
    private java.util.List<String> pseudonyms = new java.util.ArrayList<String>();
    private java.util.List<OrganizationVO> organizations = new java.util.ArrayList<OrganizationVO>();
    private IdentifierVO identifier;

    /**
     * Delivers the complete name of the person, usually a concatenation of given names and family name.
     */
    public String getCompleteName()
    {
        return completeName;
    }

    /**
     * Sets the complete name of the person, usually a concatenation of given names and family name.
     * 
     * @param newVal
     */
    public void setCompleteName(String newVal)
    {
        completeName = newVal;
    }

    /**
     * Delivers the given name of the person.
     */
    public String getGivenName()
    {
        return givenName;
    }

    /**
     * Sets the given name of the person.
     * 
     * @param newVal
     */
    public void setGivenName(String newVal)
    {
        givenName = newVal;
    }

    /**
     * Delivers the family name of the person.
     */
    public String getFamilyName()
    {
        return familyName;
    }

    /**
     * Sets the family name of the person.
     * 
     * @param newVal
     */
    public void setFamilyName(String newVal)
    {
        familyName = newVal;
    }

    /**
     * Delivers the list of organizational units the person was affiliated to when creating the item.
     */
    public java.util.List<OrganizationVO> getOrganizations()
    {
        return organizations;
    }

    /**
     * Delivers the identifier in the Personennormdatei, provided by the Deutsche Nationalbibliothek.
     */
    public IdentifierVO getIdentifier()
    {
        return identifier;
    }

    /**
     * Delivers the list of or stage names of the person.
     */
    public java.util.List<String> getPseudonyms()
    {
        return pseudonyms;
    }

    /**
     * Sets the identifier in the Personennormdatei, provided by the Deutsche Nationalbibliothek.
     * 
     * @param newVal
     */
    public void setIdentifier(IdentifierVO newVal)
    {
        identifier = newVal;
    }

    /**
     * Delivers the list of alternative names used for the person.
     */
    public java.util.List<String> getAlternativeNames()
    {
        return alternativeNames;
    }

    /**
     * Delivers the list of titles of the person.
     */
    public java.util.List<String> getTitles()
    {
        return titles;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone()
    {
        PersonVO vo = new PersonVO();
        if (getIdentifier() != null)
        {
            vo.setIdentifier((IdentifierVO)getIdentifier().clone());
        }
        vo.setCompleteName(getCompleteName());
        vo.setFamilyName(getFamilyName());
        vo.setGivenName(getGivenName());
        for (String name : getAlternativeNames())
        {
            vo.getAlternativeNames().add(name);
        }
        for (OrganizationVO organization : getOrganizations())
        {
            vo.getOrganizations().add((OrganizationVO)organization.clone());
        }
        for (String pseudonym : getPseudonyms())
        {
            vo.getPseudonyms().add(pseudonym);
        }
        for (String title : getTitles())
        {
            vo.getTitles().add(title);
        }
        return vo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public boolean equals(Object o)
    {
        if (o == null || !(o instanceof PersonVO))
        {
            return false;
        }
        PersonVO vo = (PersonVO)o;
        return equals(getFamilyName(), vo.getFamilyName()) && equals(getGivenName(), vo.getGivenName())
                && equals(getCompleteName(), vo.getCompleteName()) && equals(getIdentifier(), vo.getIdentifier())
                && equals(getAlternativeNames(), vo.getAlternativeNames())
                && equals(getOrganizations(), vo.getOrganizations()) && equals(getPseudonyms(), vo.getPseudonyms())
                && equals(getTitles(), vo.getTitles());
    }
}