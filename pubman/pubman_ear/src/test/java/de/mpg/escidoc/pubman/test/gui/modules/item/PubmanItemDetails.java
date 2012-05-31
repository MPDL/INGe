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
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/
package de.mpg.escidoc.pubman.test.gui.modules.item;

import de.mpg.escidoc.pubman.test.gui.modules.item.PubmanItem.IdentifierType;

/**
 * @author endres
 *
 */
public class PubmanItemDetails
{
    public enum ReviewType {
        INTERNAL,
        PEER,
        NO_REVIEW
    }
    
    public String languagePublication = null;
    public String datePublishedInPrint = null;
    public String datePublishedOnline = null;
    public String dateAccepted = null;
    public String dateSubmitted = null;
    public String dateModified = null;
    public String dateCreated = null;
    
    public String numberOfPages = null;
    public String tableOfContent = null;
    
    public ReviewType reviewType = null;
    public IdentifierType identifierType = null;
    public String identifierValue = null;
    
    public PubmanItemDetails( String languagePublication, String datePublishedInPrint, String datePublishedOnline,
            String dateAccepted, String dateSubmitted, String dateModified, String dateCreated, String numberOfPages,
            String tableOfContent, ReviewType reviewType, IdentifierType identifierType, String identifierValue) {
        
        this.languagePublication = languagePublication;
        this.datePublishedInPrint = datePublishedInPrint;
        this.datePublishedOnline = datePublishedOnline;
        this.dateAccepted = dateAccepted;
        this.dateSubmitted = dateSubmitted;
        this.dateModified = dateCreated;
        this.numberOfPages = numberOfPages;
        this.tableOfContent = tableOfContent;
        this.reviewType = reviewType;
        this.identifierType = identifierType;
        this.identifierValue = identifierValue;
    }

    public String getLanguagePublication()
    {
        return languagePublication;
    }

    public String getDatePublishedInPrint()
    {
        return datePublishedInPrint;
    }

    public String getDatePublishedOnline()
    {
        return datePublishedOnline;
    }

    public String getDateAccepted()
    {
        return dateAccepted;
    }

    public String getDateSubmitted()
    {
        return dateSubmitted;
    }

    public String getDateModified()
    {
        return dateModified;
    }

    public String getDateCreated()
    {
        return dateCreated;
    }

    public String getNumberOfPages()
    {
        return numberOfPages;
    }

    public String getTableOfContent()
    {
        return tableOfContent;
    }

    public ReviewType getReviewType()
    {
        return reviewType;
    }

    public IdentifierType getIdentifierType()
    {
        return identifierType;
    }

    public String getIdentifierValue()
    {
        return identifierValue;
    }
}
