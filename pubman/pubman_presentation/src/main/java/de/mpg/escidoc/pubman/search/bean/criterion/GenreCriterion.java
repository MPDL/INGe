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

package de.mpg.escidoc.pubman.search.bean.criterion;
import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.CriterionType;


/**
 * genre criterion vo for the advanced search.
 * @created 15-Mai-2007 15:06:37
 * @author NiH
 * @version 1.0
 * @updated 17-Jul-2007 17:46:17
 * Revised by NiH: 13.09.2007
 */
public class GenreCriterion extends Criterion
{
    //the genre for the search criterion
    private List<MdsPublicationVO.Genre> genreList;

	/**
	 * constructor.
	 */
	public GenreCriterion()
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
    
    private String getGenresAsStringList() {
    	StringBuffer buffer = new StringBuffer();
    	for( int i = 0; i < genreList.size(); i++ ) {
    		buffer.append( getSearchIdentifierByGenre( genreList.get( i ) ) );
    		buffer.append( " " );
    	}
    	return buffer.toString();
    }
    
    /**
     * {@inheritDoc}
     */
    
    public ArrayList<MetadataSearchCriterion> createSearchCriterion() throws TechnicalException {
    	ArrayList<MetadataSearchCriterion> criterions = new ArrayList<MetadataSearchCriterion>();
    	MetadataSearchCriterion criterion = 
			new MetadataSearchCriterion( CriterionType.GENRE, getGenresAsStringList() );
    	criterions.add( criterion );
	   	return criterions;
	}
}