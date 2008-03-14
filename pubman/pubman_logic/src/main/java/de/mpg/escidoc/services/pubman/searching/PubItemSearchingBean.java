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

package de.mpg.escidoc.services.pubman.searching;

import gov.loc.www.zing.srw.RecordType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.StringOrXmlFragment;
import gov.loc.www.zing.srw.diagnostic.DiagnosticType;
import java.io.IOException;
import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.xml.rpc.ServiceException;
import org.apache.axis.message.MessageElement;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.RemoteBinding;
import de.fiz.escidoc.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.fiz.escidoc.common.exceptions.application.security.SecurityException;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.AffiliationNotFoundException;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.logging.LogMethodDurationInterceptor;
import de.mpg.escidoc.services.common.logging.LogStartEndInterceptor;
import de.mpg.escidoc.services.common.logging.MessageCreator;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.exceptions.UnmarshallingException;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.pubman.PubItemSearching;
import de.mpg.escidoc.services.pubman.logging.PMLogicMessages;
import de.mpg.escidoc.services.pubman.valueobjects.AnyFieldCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.DateCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.EventCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.GenreCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.IdentifierCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.OrganizationCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.PersonCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.SourceCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.TitleCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.TopicCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO.LogicOperator;

/**
 * This class provides the ejb implementation of the {@link PubItemSearching} interface.
 * 
 * @author Miriam Doelle (initial creation)
 * @author $Author: tendres $ (last modification)
 * @version $Revision: 449 $ $LastChangedDate: 2007-11-28 14:40:08 +0100 (Wed, 28 Nov 2007) $
 * NiH: code revision done. @TODO: ArrayList<CriterionVO> parameter in method advancedSearch
 * must be changed to List type
 */
@Remote
@RemoteBinding(jndiBinding = PubItemSearching.SERVICE_NAME)
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Interceptors( { LogStartEndInterceptor.class, LogMethodDurationInterceptor.class })
public class PubItemSearchingBean implements PubItemSearching
{
    private static final String INDEX_CONTENT_TYPE = "escidoc.content-model.objid";
    private static final String SEARCH_CLAUSE_WITH_CONTENT_TYPE = "( {0} ) and " + INDEX_CONTENT_TYPE + "=\"{1}\"";

    // type of query
    private enum QueryType
    {
        TITLE, TITLE_LANG, ANY, ANY_INCLUDE, PERSON, PERSON_ROLE, ORGANIZATION, ORGANIZATION_LANG,
        GENRE, DATE_FROM, DATE_TO, DATE_TYPE, TOPIC, TOPIC_LANG, SOURCE, SOURCE_LANG, EVENT, EVENT_LANG, IDENTIFIER
    };

    //the cql query string of the advanced search method
    private String cqlQuery = "";
    
    /**
     * The logger instance.
     */
    private Logger logger = Logger.getLogger(getClass());

    /**
     * A XmlTransforming instance.
     */
    @EJB
    private XmlTransforming xmlTransforming;

    /**
     * compiles the search query from the criterion list and executes the advanced search.
     * @param ArrayList<CriterionVO> list: the search criteria in an ArrayList to search for
     * @return List<PubItemResultVO>: an list with the search result of the query
     */
    public List<PubItemResultVO> advancedSearch(ArrayList<CriterionVO> list, String language) throws ParseException, TechnicalException
    {
        if (list == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName()
                    + ":advancedSearch:list is null");
        }
        //cql query string 
        StringBuffer query = new StringBuffer();

        for (int i = 0; i < list.size(); i++)
        {
            CriterionVO criterion = list.get(i);

            if (criterion.getClass() == AnyFieldCriterionVO.class)
            {
                AnyFieldCriterionVO criterionVO = (AnyFieldCriterionVO)criterion;
                // create cql query with a logic operator (if needed)
                query.append("(");
                if (criterionVO.isIncludeFiles())
                {
                    query.append(createCqlQuery(criterionVO.getSearchString(), QueryType.ANY_INCLUDE ));
                }
                else
                {
                    query.append(createCqlQuery(criterionVO.getSearchString(), QueryType.ANY ));
                }
                query.append(")");
                query.append(createLogicOperator(criterionVO.getLogicOperator(), i, list.size()));
            }
            else if (criterion.getClass() == TitleCriterionVO.class)
            {
                TitleCriterionVO criterionVO = (TitleCriterionVO)criterion;
                // create cql query with a logic operator (if needed)
                query.append("(");
                if (criterionVO.getLanguage() != null && criterionVO.getLanguage().length() > 0)
                {
                    query.append(createCqlQuery(criterionVO.getSearchString(), QueryType.TITLE_LANG ));
                }
                else
                {
                    query.append(createCqlQuery(criterionVO.getSearchString(), QueryType.TITLE ));
                }
                query.append(")");
                query.append(createLogicOperator(criterionVO.getLogicOperator(), i, list.size()));
            }
            else if (criterion.getClass() == PersonCriterionVO.class)
            {
                PersonCriterionVO criterionVO = (PersonCriterionVO)criterion;
                // create cql query with a logic operator (if needed)
                query.append("(");
                query.append(createCqlQuery(criterionVO.getSearchString(), QueryType.PERSON ));
                if (criterionVO.getCreatorRole().size() > 0)
                {
                    query.append(" AND (");
                    for (int j = 0; j < criterionVO.getCreatorRole().size(); j++)
                    {
                        query.append(createCqlQuery(criterionVO.getCreatorRole().get(j).toString(), QueryType.PERSON_ROLE ));
                        if (j < criterionVO.getCreatorRole().size() - 1)
                        {
                            query.append(" OR ");
                        }
                    }
                    query.append(")");
                }
                query.append(")");
                query.append(createLogicOperator(criterionVO.getLogicOperator(), i, list.size()));
            }
            else if (criterion.getClass() == OrganizationCriterionVO.class)
            {
                OrganizationCriterionVO criterionVO = (OrganizationCriterionVO)criterion;
                // create cql query with a logic operator (if needed)
                query.append("(");
                if (criterionVO.getLanguage() != null && criterionVO.getLanguage().length() > 0)
                {
                    query.append(createCqlQuery(criterionVO.getSearchString(), QueryType.ORGANIZATION_LANG ));
                }
                else
                {
                    query.append(createCqlQuery(criterionVO.getSearchString(), QueryType.ORGANIZATION ));                    
                }
                query.append(")");
                query.append(createLogicOperator(criterionVO.getLogicOperator(), i, list.size()));
            }
            else if (criterion.getClass() == GenreCriterionVO.class)
            {
                GenreCriterionVO criterionVO = (GenreCriterionVO)criterion;
                // create cql query with a logic operator (if needed)
                query.append("(");
                for (int j = 0; j < criterionVO.getGenre().size(); j++)
                {
                    query.append(createCqlQuery(criterionVO.getSearchIdentifier( j ), QueryType.GENRE ));
                    if (j < criterionVO.getGenre().size() - 1)
                    {
                        query.append(" OR ");
                    }
                }
                query.append(")");
                query.append(createLogicOperator(criterionVO.getLogicOperator(), i, list.size()));
            }
            else if (criterion.getClass() == DateCriterionVO.class)
            {
                DateCriterionVO criterionVO = (DateCriterionVO)criterion;
                // create cql query with a logic operator (if needed)
                query.append("(");
                query.append(createCqlQuery(criterionVO.getFrom(), QueryType.DATE_FROM ));
                if (criterionVO.getTo() != null && criterionVO.getTo().length() > 0)
                {
                    query.append(" NOT ");
                    query.append(createCqlQuery(criterionVO.getTo(), QueryType.DATE_TO ));
                }
                else
                {
                    Date dt = new Date();   //actual date
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    query.append(" NOT ");
                    query.append(createCqlQuery(df.format(dt), QueryType.DATE_TO ));
                }
                if (criterionVO.getDateType().size() > 0)
                {
                    query.append(" AND (");
                    for (int j = 0; j < criterionVO.getDateType().size(); j++)
                    {
                        query.append(createCqlQuery(criterionVO.getSearchIdentifier( j ), QueryType.DATE_TYPE ));
                        if (j < criterionVO.getDateType().size() - 1)
                        {
                            query.append(" OR ");
                        }
                    }
                    query.append(")");
                }
                query.append(")");
                query.append(createLogicOperator(criterionVO.getLogicOperator(), i, list.size()));
            }
            else if (criterion.getClass() == TopicCriterionVO.class)
            {
                TopicCriterionVO criterionVO = (TopicCriterionVO)criterion;
                // create cql query with a logic operator (if needed)
                query.append("(");
                if (criterionVO.getLanguage() != null && criterionVO.getLanguage().length() > 0)
                {
                    query.append(createCqlQuery(criterionVO.getSearchString(), QueryType.TOPIC_LANG ));
                }
                else
                {
                    query.append(createCqlQuery(criterionVO.getSearchString(), QueryType.TOPIC ));                    
                }
                query.append(")");
                query.append(createLogicOperator(criterionVO.getLogicOperator(), i, list.size()));
            }
            else if (criterion.getClass() == SourceCriterionVO.class)
            {
                SourceCriterionVO criterionVO = (SourceCriterionVO)criterion;
                // create cql query with a logic operator (if needed)
                query.append("(");
                if (criterionVO.getLanguage() != null && criterionVO.getLanguage().length() > 0)
                {
                    query.append(createCqlQuery(criterionVO.getSearchString(), QueryType.SOURCE_LANG ));
                }
                else
                {
                    query.append(createCqlQuery(criterionVO.getSearchString(), QueryType.SOURCE ));                    
                }
                query.append(")");
                query.append(createLogicOperator(criterionVO.getLogicOperator(), i, list.size()));
            }
            else if (criterion.getClass() == EventCriterionVO.class)
            {
                EventCriterionVO criterionVO = (EventCriterionVO)criterion;
                // create cql query with a logic operator (if needed)
                query.append("(");
                if (criterionVO.getLanguage() != null && criterionVO.getLanguage().length() > 0)
                {
                    query.append(createCqlQuery(criterionVO.getSearchString(), QueryType.EVENT_LANG ));
                }
                else
                {
                    query.append(createCqlQuery(criterionVO.getSearchString(), QueryType.EVENT ));                    
                }
                query.append(")");
                query.append(createLogicOperator(criterionVO.getLogicOperator(), i, list.size()));
            }
            else if (criterion.getClass() == IdentifierCriterionVO.class)
            {
                IdentifierCriterionVO criterionVO = (IdentifierCriterionVO)criterion;
                // create cql query with a logic operator (if needed)
                query.append("(");
                query.append(createCqlQuery(criterionVO.getSearchString(), QueryType.IDENTIFIER ));
                query.append(")");
                query.append(createLogicOperator(criterionVO.getLogicOperator(), i, list.size()));
            }
        }

        // execute search for publication items
        this.cqlQuery = query.toString();
        if (query.length() > 0)
        {
            return cqlSearchForPubItems(query.toString(), language);
        }
        else
        {
            return new ArrayList<PubItemResultVO>();
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<PubItemResultVO> search(String searchString, boolean searchInFiles) throws ParseException, TechnicalException
    {
        if (searchString == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName()
                    + ":search:searchString is null");
        }
        if (searchString.length() > 0)
        {
            // create cql query
            QueryParser parser = new QueryParser(searchString);
            parser.addCQLIndex("escidoc.metadata");
            if (searchInFiles)
            {
                parser.addCQLIndex("escidoc.fulltext");
            }
            String cqlQuery = parser.parse();

            // execute search for publication items
            return cqlSearchForPubItems(cqlQuery, null );
        }
        else
        {
            return new ArrayList<PubItemResultVO>();
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<PubItemVO> searchPubItemsByAffiliation(AffiliationVO affiliation) throws TechnicalException, AffiliationNotFoundException
    {
        if (affiliation == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName()
                    + ":searchPubItemsByAffiliation:affiliationRef is null");
        }
        // First read the affiliation to get the PID
        String affiliationXML = null;
        // AffiliationVO affiliation = null;
//        try
//        {   
//            affiliationXML = ServiceLocator.getOrganizationalUnitHandler().retrieve(affiliationRef.getObjectId());
//            affiliation = xmlTransforming.transformToAffiliation(affiliationXML);
//        }
//        catch (OrganizationalUnitNotFoundException e)
//        {
//            throw new AffiliationNotFoundException(affiliationRef, e);
//        }
//        catch (SecurityException e)
//        {
//            // retrieve should not be secured.
//            throw new TechnicalException(e);
//        }
//        catch (RemoteException e)
//        {
//            throw new TechnicalException(e);
//        }
//        catch (UnmarshallingException e)
//        {
//            throw new TechnicalException(e);
//        }
//        catch (ServiceException e)
//        {
//
//        }

        List<PubItemVO> searchResult = searchPubItemsForAffiliationAndChildren(affiliation);

        return searchResult;

    }

    private List<PubItemVO> searchPubItemsForAffiliationAndChildren(AffiliationVO affiliation) throws TechnicalException,
            AffiliationNotFoundException
    {
        if (affiliation == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName()
                    + ":searchPubItemsForAffiliationAndChildren:affiliation is null");
        }
        ArrayList<PubItemVO> searchResult = new ArrayList<PubItemVO>();
        if (affiliation.getPid() != null)
        {
            // affiliation is not open or not correct.
            return searchResult;
        }

        // create cql query
        QueryParser parser = new QueryParser(affiliation.getReference().getObjectId());       
        parser.addCQLIndex("escidoc.any-organization-pids");

        String cqlQuery = null;
        try
        {
            cqlQuery = parser.parse();
        }
        catch (ParseException e)
        {
            // If this exception occurs creation of search query does not work correctly
            throw new TechnicalException(e);
        }

        // execute search for publication items and add to result
        ArrayList<PubItemResultVO> pubItemResultList = cqlSearchForPubItems(cqlQuery, null );
        searchResult.addAll((Arrays.asList(pubItemResultList.toArray(new PubItemVO[pubItemResultList.size()]))));

        return searchResult;
    }

    /**
     * Executes a search with the given cql query supplemented with the clause to search only for publication items.
     * 
     * @param cqlSearchString
     * @return The list of PubItemResultVOs that are in the search result.
     */
    private ArrayList<PubItemResultVO> cqlSearchForPubItems(String cqlSearchString, String language ) throws TechnicalException
    {
        if (cqlSearchString == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName()
                    + ":cqlSearchForPubItems:cqlSearchString is null");
        }
        // append object type clause
        String contentTypeIdPublication = null;
        try
        {
            contentTypeIdPublication = PropertyReader.getProperty("escidoc.framework_access.content-type.id.publication");
        }
        catch (IOException e)
        {
            throw new TechnicalException(e);
        }
        String extendedCqlSearchString = MessageFormat.format(SEARCH_CLAUSE_WITH_CONTENT_TYPE, new Object[] { cqlSearchString,
                contentTypeIdPublication });

        logger.debug("Search for " + extendedCqlSearchString);

        // call framework Search service
        SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
        searchRetrieveRequest.setVersion("1.1");
        searchRetrieveRequest.setQuery(extendedCqlSearchString);
        // TODO NiH: Replace by appropriate implementation
        // ADDED by MuJ to increase record count (without this, only about 20 records are returned)
        NonNegativeInteger nni = new NonNegativeInteger("100");
        searchRetrieveRequest.setMaximumRecords(nni);
        ////////////////////////////////////////        
        searchRetrieveRequest.setRecordPacking("xml");
        SearchRetrieveResponseType searchResult = null;
        try
        {
            searchResult = ServiceLocator.getSearchHandler( language ).searchRetrieveOperation(searchRetrieveRequest);
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
            throw new TechnicalException("Search request failed for query " + extendedCqlSearchString
                    + ". Diagnostics returned. See log for details.");
        }

        // transform to PubItem list
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
                if (messages.length > 1)
                {
                    // what should be in the further message?!
                    logger.warn(MessageCreator.getMessage(PMLogicMessages.SEARCH_TOO_MANY_RESULT_MESSAGES, new Object[] { cqlSearchString }));
                }

            }
        }

        return pubItemResultList;
    }

    /**
     * creates an cql query string by the QueryParser instance.
     * @param searchString (String): the search string
     * @param queryType (QueryType): the type of search criterion
     * @return (String): the cql query string
     * @throws ParseException
     * @throws TechnicalException
     */
    private String createCqlQuery(String searchString, QueryType queryType )
                    throws ParseException, TechnicalException
    {
        if (searchString == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName()
                    + ":createCqlQuery:searchString is null");
        }
        if (queryType == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName()
                    + ":createCqlQuery:queryType is null");
        }
        
        QueryParser parser = new QueryParser(searchString);
        switch( queryType ) {
        	
        	case TITLE: 
        		parser.addCQLIndex("escidoc.any-title");
        		break;
        	case ANY:	
        		parser.addCQLIndex("escidoc.metadata");
        		break;
        	case ANY_INCLUDE:	
        		parser.addCQLIndex("escidoc.metadata");
                parser.addCQLIndex("escidoc.fulltext");
                break;
        	case PERSON:	
        		parser.addCQLIndex("escidoc.any-persons");  
        		break;
        	case PERSON_ROLE:	
        		parser.addCQLIndex("escidoc.creator.role");  
        		break;
        	case ORGANIZATION:
        		parser.addCQLIndex("escidoc.any-organizations");
        		break;
        	case ORGANIZATION_LANG:	
        		parser.addCQLIndex("escidoc.any-organizations");
        		break;
        	case GENRE:	
        		parser.addCQLIndex("escidoc.any-genre");
        		break;
        	case DATE_FROM:
        		return("escidoc.any-dates>=" + searchString);    
        	case DATE_TO:
        		return("escidoc.any-dates>" + searchString); 
        	case DATE_TYPE:
        		parser.addCQLIndex("escidoc.created.type"); 
        		break;
        	case TOPIC:	
        		parser.addCQLIndex("escidoc.any-topic"); 
        		break;
        	case TOPIC_LANG:	
        		parser.addCQLIndex("escidoc.any-topic");
        		break;
        	case SOURCE:
        		parser.addCQLIndex("escidoc.any-source");
        		break;
        	case SOURCE_LANG:	
        		parser.addCQLIndex("escidoc.any-source");
        		break;
        	case EVENT:
        		parser.addCQLIndex("escidoc.any-event"); 
        		break;
        	case EVENT_LANG:	
        		parser.addCQLIndex("escidoc.any-event");
        		break;
        	case IDENTIFIER:
        		parser.addCQLIndex("escidoc.any-identifier");
        		break;
        	default:
        		throw new TechnicalException();
        }
        
        String result = parser.parse();
        return result;
    }
    
    /**
     * creates a logic operator string to be appended on the cql query
     * @param op (LogicOperator): the logic operator
     * @param index (int): the index position in the criterion list
     * @param size (int): the size of the list
     * @return (String): the string with the logic operator
     */
    private String createLogicOperator(LogicOperator op, int index, int size)
    {
        //logic operator only, if it is not the last criterion in the list
        if (op != null && index < size - 1)
        {
            //add logical operator
            return(" " + op.name() + " ");
        }
        else
        {
            return "";
        }
    }

    /* (non-Javadoc)
     * @see de.mpg.escidoc.services.pubman.PubItemSearching#getCqlQuery()
     */
    public String getCqlQuery()
    {
        return cqlQuery;
    }
}
