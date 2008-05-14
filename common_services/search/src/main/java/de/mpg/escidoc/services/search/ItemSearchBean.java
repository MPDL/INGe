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

package de.mpg.escidoc.services.search;

import gov.loc.www.zing.srw.RecordType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.StringOrXmlFragment;
import gov.loc.www.zing.srw.diagnostic.DiagnosticType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.axis.message.MessageElement;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.RemoteBinding;

import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.logging.LogMethodDurationInterceptor;
import de.mpg.escidoc.services.common.logging.LogStartEndInterceptor;
import de.mpg.escidoc.services.common.valueobjects.PubItemResultVO;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * @author endres
 *
 */
@Remote
@RemoteBinding(jndiBinding = ItemSearch.SERVICE_NAME)
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Interceptors( { LogStartEndInterceptor.class, LogMethodDurationInterceptor.class })
public class ItemSearchBean implements ItemSearch {
	
	/** logging instance */
    private Logger logger = null;
    
    /**
     * A XmlTransforming instance.
     */
    @EJB
    private XmlTransforming xmlTransforming;
    
    public ItemSearchBean() {
    	this.logger = Logger.getLogger(getClass());
    }
    
    public List<PubItemResultVO> search( String cqlQuery, String databaseLang ) {
    	
    	 // call framework Search service
        SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
        searchRetrieveRequest.setVersion("1.1");
        searchRetrieveRequest.setQuery( cqlQuery );

        NonNegativeInteger nni = new NonNegativeInteger("500");
        searchRetrieveRequest.setMaximumRecords(nni);
        ////////////////////////////////////////        
        searchRetrieveRequest.setRecordPacking("xml");
        
        try {
        	return transformToPubItemList( performSearch( searchRetrieveRequest, "all" ) );
        }
        catch (TechnicalException e) {
        	return new ArrayList<PubItemResultVO>();
        }
    }
	
	private SearchRetrieveResponseType performSearch( SearchRetrieveRequestType searchRetrieveRequest, String databaseLang ) 
		throws TechnicalException {
		
		SearchRetrieveResponseType searchResult = null;
		
		try
        {
            searchResult = ServiceLocator.getSearchHandler( databaseLang ).searchRetrieveOperation(searchRetrieveRequest);
            logger.debug("Search result: " + searchResult.getNumberOfRecords());
        }
        catch (Exception e)
        {
            throw new TechnicalException(e);
        }

        // look for errors
        if (searchResult.getDiagnostics() != null)
        {
            // something went wrong
            for (DiagnosticType diagnostic : searchResult.getDiagnostics().getDiagnostic())
            {
                logger.warn(diagnostic.getUri());
                logger.warn(diagnostic.getMessage());
                logger.warn(diagnostic.getDetails());
            }
            throw new TechnicalException("Search request failed for query " + searchRetrieveRequest.getQuery()
                    + ". Diagnostics returned. See log for details.");
        }

        long time = new Date().getTime();
        logger.debug("START TIME: " + time);
        
        return searchResult;
	}
	
	private List<PubItemResultVO> transformToPubItemList( SearchRetrieveResponseType searchResult ) throws TechnicalException {
		
		 ArrayList<PubItemResultVO> pubItemResultList = new ArrayList<PubItemResultVO>();
	        if (searchResult.getRecords() != null)
	        {
	            for (RecordType record : searchResult.getRecords().getRecord())
	            {
	                StringOrXmlFragment data = record.getRecordData();
	                MessageElement[] messages = data.get_any();
	                // Data is in the first record
	                if (messages.length == 1)
	                {
	                    try
	                    {
	                        String searchResultItem = messages[0].getAsString();
	                        logger.debug("Search result: " + searchResultItem);
	                        PubItemResultVO pubItemResult = xmlTransforming.transformToPubItemResultVO(searchResultItem);
	                        pubItemResultList.add(pubItemResult);
	                    }
	                    catch (Exception e)
	                    {
	                        throw new TechnicalException(e);
	                    }
	                } 
	            }
	        }
	        return pubItemResultList;
	}

}
