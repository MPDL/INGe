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

import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;


/**
 * genre criterion vo for the advanced search.
 * @created 15-Mai-2007 15:06:37
 * @author NiH
 * @version 1.0
 * @updated 17-Jul-2007 17:46:17
 * Revised by NiH: 13.09.2007
 */
public class GenreCriterionVO extends CriterionVO
{
	/** serial for the serializable interface*/
	private static final long serialVersionUID = 1L;
	
    //the genre for the search criterion
    private List<MdsPublicationVO.Genre> genreList;

	/**
	 * constructor.
	 */
	public GenreCriterionVO()
    {
        super();
	}

    public List<MdsPublicationVO.Genre> getGenre()
    {
        return genreList;
    }

    public void setGenre(List<MdsPublicationVO.Genre> genre)
    {
        this.genreList = genre;
    }
    
    private String getSearchIdentifierByGenre( MdsPublicationVO.Genre g )
    {
        switch( g ) 
        {
            case ARTICLE:
                return "article";
            case BOOK:
                return "book";
            case BOOK_ITEM:
                return "book-item";
            case PROCEEDINGS: 
                return "proceedings";
            case CONFERENCE_PAPER:
                return "conference-paper";
            case TALK_AT_EVENT:
                return "talk-at-event";
            case CONFERENCE_REPORT:
                return "conference-report";
            case POSTER:
                return "poster";
            case COURSEWARE_LECTURE:
                return "courseware-lecture";
            case THESIS:
                return "thesis";
            case PAPER:
                return "paper";
            case REPORT:
                return "report";
            case ISSUE:
                return "issue";
            case JOURNAL:
                return "journal";
            case MANUSCRIPT:
                return "manuscript";
            case SERIES:
                return "series";
            case OTHER:
                return "other";
            default: 
                    return "";
        }
    }
    
    public String getSearchIdentifier( int position )
    {
        if( genreList.size() <= position  )
        {
            return "";
        }
        else
        {
            return this.getSearchIdentifierByGenre( this.genreList.get( position ) );
        }
    }
}