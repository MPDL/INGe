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

import java.io.IOException;
import java.rmi.UnmarshalException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import net.sf.jasperreports.engine.JRException;

import org.apache.axis.message.MessageElement;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.RemoteBinding;
import org.z3950.zing.cql.CQLParseException;

import de.mpg.escidoc.services.citationmanager.CitationStyleHandler;
import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.citationmanager.XmlHelper;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.logging.LogMethodDurationInterceptor;
import de.mpg.escidoc.services.common.logging.LogStartEndInterceptor;
import de.mpg.escidoc.services.common.valueobjects.FileFormatVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO.FormatType;
import de.mpg.escidoc.services.common.valueobjects.interfaces.ItemContainerSearchResultVO;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.search.parser.ParseException;
import de.mpg.escidoc.services.search.query.ExportSearchQuery;
import de.mpg.escidoc.services.search.query.SearchQuery;
import de.mpg.escidoc.services.search.query.StandardSearchQuery;
import de.mpg.escidoc.services.structuredexportmanager.StructuredExportHandler;
import de.mpg.escidoc.services.structuredexportmanager.StructuredExportManagerException;
import de.mpg.escidoc.services.structuredexportmanager.StructuredExportXSLTNotFoundException;

/**
 * {@inheritDoc}
 *
 */
@Remote
@RemoteBinding(jndiBinding = ItemContainerSearch.SERVICE_NAME)
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Interceptors( { LogStartEndInterceptor.class, LogMethodDurationInterceptor.class })
public class ItemContainerSearchBean implements ItemContainerSearch {
	
	/** logging instance */
    private Logger logger = null;
    
    /**
     * A XmlTransforming instance.
     */
    @EJB
    private XmlTransforming xmlTransforming;
    
    /**
     * A CitationStyleHandler instance.
     */
    @EJB
    private CitationStyleHandler citationStyleHandler;

    /**
     * A EndnodeExportHandler instance.
     */
    @EJB
    private StructuredExportHandler structuredExportHandler;
    
    public ItemContainerSearchBean() {
    	this.logger = Logger.getLogger(getClass());
    }
    
    /** coreservice identifier for the 'all' index database */
    private static final String INDEXDATABASE_ALL = "all";
    /** coreservice identifier for the 'german' index database */
    private static final String INDEXDATABASE_EN = "en";
    /** coreservice identifier for the 'english' index database */
    private static final String INDEXDATABASE_DE = "de";
    
    
    /** 
     * {@inheritDoc}
     * @throws TechnicalException 
     * @throws ParseException 
     * @throws IOException 
     * @throws CQLParseException 
     */
    public List<ItemContainerSearchResultVO> search( StandardSearchQuery query ) throws TechnicalException {
    	
    	try {
    		// call framework Search service
    		SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
        	searchRetrieveRequest.setVersion("1.1");
        	searchRetrieveRequest.setQuery( query.getCqlQuery() );

        	NonNegativeInteger nni = new NonNegativeInteger("10000");
        	searchRetrieveRequest.setMaximumRecords(nni);
        	searchRetrieveRequest.setRecordPacking("xml");
        
        	SearchRetrieveResponseType searchResult = performSearch( searchRetrieveRequest, query.getIndexSelector() );
        	List<ItemContainerSearchResultVO> resultList = transformToSearchResultList( searchResult );
        	return resultList;
    	}
    	catch( Exception e ) {
    		throw new TechnicalException( e );
    	}
      }
     
    /** 
     * {@inheritDoc}
     */
    public byte[] searchAndExport( ExportSearchQuery query ) throws Exception {
    	
    	 // call framework Search service
        SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
        searchRetrieveRequest.setVersion("1.1");
        searchRetrieveRequest.setQuery( query.getCqlQuery() );

        NonNegativeInteger nni = new NonNegativeInteger("10000");
        searchRetrieveRequest.setMaximumRecords(nni);
        searchRetrieveRequest.setRecordPacking("xml");
        
        SearchRetrieveResponseType searchResult = performSearch( searchRetrieveRequest, query.getIndexSelector() );
        String itemList = transformToItemListAsString( searchResult );
    	
        
    	if ( query.getExportFormat() == null || query.getExportFormat().trim().equals("") )
   	 		throw new TechnicalException("exportFormat is empty");
   	 	
   	 	if ( itemList == null || itemList.trim().equals("") )
   	 		throw new TechnicalException("itemList is empty");
   	 	
   		 byte[] exportData = null;
   		 
   		 //structured export
   		 boolean flag = false;
   		 try
   		 {
       		 FOR: for ( String ef : structuredExportHandler.getFormatsList() )
          		  if ( query.getExportFormat().equals(ef) )
          		  {
          			  flag = true;
          			  break FOR; 
          		  }
   		 }
   		 catch (Exception e) 
   		 {
   			 throw new TechnicalException(e);
   		 }
   		 
   		 if ( flag ){ 
   			 try{
//   				 exportData = structuredExportHandler.getOutput(itemList, exportFormat);
   				 exportData = getOutput( query.getExportFormat(), FormatType.STRUCTURED, null, itemList);
   				 return exportData;
   			 }  catch (Exception e) 
   			 {
   				 throw new TechnicalException(e);
   			 }	 
   		 
   		 }

   		 try
   		 {
   			 FOR: for ( String ef : XmlHelper.getListOfStyles() )
   				 if ( query.getExportFormat().equals(ef) )
   				 {
   					 flag = true;
   					 break FOR; 
   				 }
   		 }
   		 catch (Exception e) 
   		 {
   			 throw new TechnicalException(e);
   		 }
   		 
   		 String outputFormat = query.getOutputFormat();
   		 String exportFormat = query.getExportFormat();
   		 
   		 if ( flag ){
   			 
   	   	 	if ( outputFormat == null || outputFormat.trim().equals("") )
   	   	 		throw new TechnicalException("outputFormat should be not empty for exportFormat:" + exportFormat);

   			 outputFormat = outputFormat.trim();
   		 	 if ( 
   		 			 ! FileFormatVO.isOutputFormatSupported(outputFormat) 
   		 		)
   		 		 throw new TechnicalException("file output format: " + outputFormat + 
   		 				 " for export format: " + exportFormat + " is not supported");
   			 
   			 try{
   				 exportData = getOutput(exportFormat, FormatType.LAYOUT, outputFormat, itemList);
   				 return exportData;
   			 }  
   		 	 catch (Exception e) 
   			 {
   				 throw new TechnicalException(e);
   			 } 
   		 }
   		 else 
   			 // no export format found!!!
   			 throw new TechnicalException("Export format: " + exportFormat + " is not supported");
    	
    }
    
    private byte[] getOutput(
      		 String exportFormat, 
      		 FormatType formatType, 
      		 String outputFormat,
      		 String itemList
      	) throws TechnicalException, StructuredExportXSLTNotFoundException, StructuredExportManagerException, IOException, JRException, CitationStyleManagerException 
       {
      	 
      	 byte[] exportData = null;
      	 
      	 //structured export
      	 if ( formatType == FormatType.LAYOUT )
      	 {	 
        	   if (logger.isDebugEnabled()) logger.debug(">>> start citationStyleHandler " + itemList);
        	   exportData = citationStyleHandler.getOutput( exportFormat, outputFormat, itemList );
      	 }	
      	 else if ( formatType == FormatType.STRUCTURED )
      	 {
      		if (logger.isDebugEnabled()) logger.debug(">>> start structuredExportHandler " + itemList);
      		exportData = structuredExportHandler.getOutput( itemList, exportFormat );
      	 }	
      	 else 
      		// no export format found!!!
      		throw new TechnicalException("format Type: " + formatType + " is not supported");

      	 return exportData;
   	}
    
    
    
    private String getIndexDatabaseSelectorAsString( IndexDatabaseSelector sel ) throws TechnicalException {
    	switch( sel ) {
    		case All:
    			return INDEXDATABASE_ALL;
    		case English:
    			return INDEXDATABASE_EN;
    		case German:
    			return INDEXDATABASE_DE;
    		default:
    			throw new TechnicalException();
    	}
    }
    
    
	
	private SearchRetrieveResponseType performSearch( SearchRetrieveRequestType searchRetrieveRequest, IndexDatabaseSelector sel ) 
		throws TechnicalException {
		
		SearchRetrieveResponseType searchResult = null;
		
		try
        {
			logger.info( "Cql search string: <" + searchRetrieveRequest.getQuery() + ">" );
            searchResult = ServiceLocator.getSearchHandler( getIndexDatabaseSelectorAsString( sel ) ).searchRetrieveOperation(searchRetrieveRequest);
            logger.info("Search result: " + searchResult.getNumberOfRecords() + " item(s) or container(s)");
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
	
	private List<ItemContainerSearchResultVO> transformToSearchResultList( SearchRetrieveResponseType searchResult ) throws TechnicalException {
		
		 ArrayList<ItemContainerSearchResultVO> resultList = new ArrayList<ItemContainerSearchResultVO>();
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
	                    	ItemContainerSearchResultVO itemResult = xmlTransforming.transformToItem( searchResultItem );
	                        resultList.add( itemResult );
	                    }
	                    catch (Exception e)
	                    {
	                        try {
	                        	String searchResultItem = messages[0].getAsString();
			                    logger.debug("Search result: " + searchResultItem);
	                        	ItemContainerSearchResultVO containerResult = xmlTransforming.transformToContainer( searchResultItem );        
		                        resultList.add( containerResult );
	                        }
	                        catch( Exception f ) {
	                        	throw new TechnicalException( f );
	                        }
	                    }
	                } 
	            }
	        }
	        return resultList;
	}
	
	private String transformToItemListAsString( SearchRetrieveResponseType searchResult ) throws Exception {
		ArrayList<ItemVO> resultList = new ArrayList<ItemVO>();
        if (searchResult.getRecords() != null)
        {
            for (RecordType record : searchResult.getRecords().getRecord())
            {
                StringOrXmlFragment data = record.getRecordData();
                MessageElement[] messages = data.get_any();
                // Data is in the first record
                if (messages.length == 1)
                {              	
                   String searchResultItem = messages[0].getAsString();
	               logger.debug("Search result: " + searchResultItem);
	               try { 
	            	   ItemVO itemResult = xmlTransforming.transformToItem( searchResultItem );
	            	   resultList.add( itemResult );
	               }
	               catch( TechnicalException e ) {
	            	   logger.warn( "ItemContainerSearchBean::transformToItemListAsString(): Unmarshalling failed, maybe a container?" + e.getStackTrace() );
	               }
                } 
            }
        }
        String itemStringList = xmlTransforming.transformToItemList( resultList );
        return itemStringList;
	}

}
