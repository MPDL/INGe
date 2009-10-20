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
package de.mpg.escidoc.pubman.test.gui.modules.item;

import de.mpg.escidoc.pubman.test.gui.modules.item.PubmanItem.CreatorRole;
import de.mpg.escidoc.pubman.test.gui.modules.item.PubmanItem.CreatorType;
import de.mpg.escidoc.pubman.test.gui.modules.item.PubmanItem.IdentifierType;

/**
 * @author endres
 *
 */
public class PubmanItemSource
{
    public enum Genre {
        BOOK,
        ISSUE,
        JOURNAL,
        PROCEEDINGS,
        SERIES
    }
    
    public Genre genre = null;
    public String title = null;
    public CreatorRole creatorRole = null;
    public CreatorType creatorType = null;
    public String familyName = null;
    public String givenName = null;
    public String orgaName = null;
    public String orgaAdress = null;
    
    public String volume = null;
    public String totalNumberOfPages = null;
    public String publisher = null;
    public String place = null;
    public IdentifierType identifier = null;
    public String identifierValue = null;
    
    public String edition = null;
    public String issue = null;
    public String startPage = null;
    public String endPage = null;
    public String sequenceNumber = null;
    
    public PubmanItemSource( Genre genre, String title, CreatorRole creatorRole, CreatorType creatorType, String familyName,
            String givenName, String orgaName, String orgaAdress, String volume, String totalNumberOfPages, String publisher,
            String place, IdentifierType identifier, String identifierValue, String edition, String issue, String startPage,
            String endPage, String sequenceNumber) {
        this.genre = genre;
        this.title = title;
        this.creatorRole = creatorRole;
        this.creatorType = creatorType;
        this.familyName = familyName;
        this.givenName = givenName;
        this.orgaName = orgaName;
        this.orgaAdress = orgaAdress;
        this.volume = volume;
        this.totalNumberOfPages = totalNumberOfPages;
        this.publisher = publisher;
        this.place = place;
        this.identifier = identifier;
        this.identifierValue = identifierValue;
        this.edition = edition;
        this.issue = issue;
        this.startPage = startPage;
        this.endPage = endPage;
        this.sequenceNumber = sequenceNumber;
    }
}
