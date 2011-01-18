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
* Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
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
 * degree criterion vo for the advanced search.
 * @author Friederike Kleinfercher
 * @version 1.0
 */
public class DegreeCriterion extends Criterion
{
    //the degree for the search criterion
    private List<MdsPublicationVO.DegreeType> degreeList;

    /**
     * constructor.
     */
    public DegreeCriterion()
    {
        super();
    }

    public List<MdsPublicationVO.DegreeType> getDegree()
    {
        return degreeList;
    }

    public void setDegree(List<MdsPublicationVO.DegreeType> degreeList)
    {
        this.degreeList = degreeList;
    }
    
    private String getSearchIdentifierByDegree( MdsPublicationVO.DegreeType g )
    {
        return g.getUri();
    }
    
    public String getSearchIdentifier( int position )
    {
        if( degreeList.size() <= position  )
        {
            return "";
        }
        else
        {
            return this.getSearchIdentifierByDegree( this.degreeList.get( position ) );
        }
    }
    
    private String getDegreesAsStringList() {
        StringBuffer buffer = new StringBuffer();
        for( int i = 0; i < degreeList.size(); i++ ) {
            buffer.append( getSearchIdentifierByDegree( degreeList.get( i ) ) );
            if( i != degreeList.size() -1  ) {
                buffer.append( " OR " );
            }
        }
        return buffer.toString();
    }
    
    /**
     * {@inheritDoc}
     */
    
    public ArrayList<MetadataSearchCriterion> createSearchCriterion() throws TechnicalException {
        ArrayList<MetadataSearchCriterion> criterions = new ArrayList<MetadataSearchCriterion>();
        MetadataSearchCriterion criterion = 
            new MetadataSearchCriterion( CriterionType.DEGREE, getDegreesAsStringList() );
        criterions.add( criterion );
           return criterions;
    }
}