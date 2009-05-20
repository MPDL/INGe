/**
 * 
 */
package de.mpg.escidoc.services.search.query;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import org.z3950.zing.cql.CQLNode;
import org.z3950.zing.cql.CQLParseException;
import org.z3950.zing.cql.CQLParser;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.search.parser.ParseException;
import de.mpg.escidoc.services.search.parser.QueryParser;

/**
 * The Metadata search criterion defines a query to a specific index with a
 * given value. One metadata search criterion can query multiple indices. This
 * is defined by the criterion type.
 * 
 * @author endres
 * 
 */
public class MetadataSearchCriterion implements Serializable
{
    /** Serializable identifier. */
    private static final long serialVersionUID = 1L;
    
    /** subcriteria for this criterion. */
    private ArrayList<MetadataSearchCriterion> subCriteria = new ArrayList<MetadataSearchCriterion>();

    /** Criteria types for the search criterion. */
    public enum CriterionType
    {
        TITLE, ANY, ANY_INCLUDE, PERSON, PERSON_ROLE, ORGANIZATION, ORGANIZATION_PIDS, GENRE, DATE_ANY,
        DATE_CREATED, DATE_ACCEPTED, DATE_SUBMITTED, DATE_MODIFIED, DATE_PUBLISHED_ONLINE, DATE_ISSUED, TOPIC,
        SOURCE, EVENT, IDENTIFIER, CONTEXT_OBJECTID, CREATED_BY_OBJECTID, LANGUAGE, CONTENT_TYPE, OBJECT_TYPE,
        COMPONENT_ACCESSABILITY, COMPONENT_VISIBILITY, COMPONENT_CONTENT_CATEGORY, LOCAL_TAG, COPYRIGHT_DATE
    };

    /**
     * Logical operator between the criteria. This operator is used to combine
     * this criterion with the criterion before
     */
    public enum LogicalOperator
    {
        AND, OR, NOT, UNSET
    }

    /**
     * Boolean operator between the index and the query.
     * 
     * @author endres
     * 
     */
    public enum BooleanOperator
    {
        EQUALS, GREATER_THAN_EQUALS, LESS_THAN_EQUALS, GREATER
    }

    /** The cql AND constant. */
    protected static final String CQL_AND = "and";
    /** The cql OR constant. */
    protected static final String CQL_OR = "or";
    /** The cql NOT constant. */
    protected static final String CQL_NOT = "not";
    
    private static final String BOOLEAN_EQUALS = "=";
    private static final String BOOLEAN_GREATER_THAN_EQUALS = ">=";
    private static final String BOOLEAN_LESS_THAN_EQUALS = "<=";
    private static final String BOOLEAN_GREATER = ">";

    /** Index for the content-type. */
    private static final String INDEX_CONTENT_TYPE = "escidoc.content-model.objid";
    /** Index for any-title. */
    private static final String INDEX_TITLE = "escidoc.any-title";
    /** Index for metadata. */
    private static final String INDEX_METADATA = "escidoc.metadata";
    /** Index for fulltexts. */
    private static final String INDEX_FULLTEXT = "escidoc.fulltext";
    /** Index for persons. */
    private static final String INDEX_PERSON = "escidoc.any-persons";
    /** Index for creator roles. */
    private static final String INDEX_PERSON_ROLE = "escidoc.creator.role";
    /** Index for organizations. */
    private static final String INDEX_ORGANIZATION = "escidoc.any-organizations";
    /** Index for organization pids. */
    private static final String INDEX_ORGANIZATION_PIDS = "escidoc.any-organization-pids";
    /** Index for genre. */
    private static final String INDEX_GENRE = "escidoc.publication.type";
    /** Index for dates. */
    private static final String INDEX_DATE_ANY = "escidoc.any-dates";
    /** Index for creation date. */
    private static final String INDEX_DATE_CREATED = "escidoc.created";
    /** Index for the accepted date. */
    private static final String INDEX_DATE_ACCEPTED = "escidoc.dateAccepted";
    /** Index for the submitted date. */
    private static final String INDEX_DATE_SUBMITTED = "escidoc.dateSubmitted";
    /** Index for the issued date. */
    private static final String INDEX_DATE_ISSUED = "escidoc.issued";
    /** Index for the modified date. */
    private static final String INDEX_DATE_MODIFIED = "escidoc.modified";
    /** Index for the published online date. */
    private static final String INDEX_DATE_PUBLISHED_ONLINE = "escidoc.published-online";
    /** Index for topics. */
    private static final String INDEX_TOPIC = "escidoc.subject";
    /** Index for sources. */
    private static final String INDEX_SOURCE = "escidoc.any-source";
    /** Index for events. */
    private static final String INDEX_EVENT = "escidoc.any-event";
    /** Index for identifiers. */
    private static final String INDEX_IDENTIFIER = "escidoc.any-identifier";
    /** Index for object ids of contexts. */
    private static final String INDEX_CONTEXT_OBJECTID = "escidoc.context.objid";
    /** Index for the created-by object id. */
    private static final String INDEX_CREATED_BY_OBJECTID = "escidoc.created-by.objid";
    /** Index for languages. */
    private static final String INDEX_LANGUAGE = "escidoc.language";
    /** Index for object types. */
    private static final String INDEX_OBJECT_TYPE = "escidoc.objecttype";
    /** Index for component availability. */
    private static final String INDEX_COMPONENT_ACCESSIBILITY = "escidoc.component.creation-date";
    /** Index for component visibility. */
    private static final String INDEX_COMPONENT_VISIBILITY = "escidoc.component.visibility";
    /** Index for component content category. */
    private static final String INDEX_COMPONENT_CONTENT_CATEGORY = "escidoc.component.content-category";
    /** Index for local tags. */
    private static final String INDEX_LOCAL_TAG = "escidoc.content-model-specific.local-tags.local-tag";
    /** Index for copyright date. */
    private static final String INDEX_COPYRIGHT_DATE = "escidoc.component.file.dateCopyrighted";
    /** String to be used to represent an empty search term. */
    private static final String EMPTY_SEARCH_TERM = "''";

    private ArrayList<String> searchIndexes = null;
    private String searchTerm = null;
    private LogicalOperator logicalOperator = null;
    private BooleanOperator cqlOperator = null;
    private ArrayList<CriterionType> typeList = null;

    
    /**
     * Constructor with criterion type and empty search term. The search term is empty, because
     * this criterion shall be used to verify if a index is present.
     * 
     * @param type
     *            the type of a criteria
     * @throws TechnicalException
     *             if creation of object fails
     */
    public MetadataSearchCriterion(CriterionType type) throws TechnicalException
    {
        typeList = new ArrayList<CriterionType>();

        this.searchTerm = EMPTY_SEARCH_TERM;

        this.searchIndexes = setIndexByEnum(type);
        this.logicalOperator = LogicalOperator.UNSET;
        this.cqlOperator = BooleanOperator.GREATER;
        this.typeList.add(type);
    }
    
    /**
     * Constructor with criterion type, logical operator and empty search term. The search term is empty, because
     * this criterion shall be used to verify if a index is present.
     * 
     * @param type
     *            the type of a criteria
     * @param operator
     *            logical operator
     * @throws TechnicalException
     *             if creation of object fails
     */
    public MetadataSearchCriterion(CriterionType type, LogicalOperator operator) throws TechnicalException
    {
        typeList = new ArrayList<CriterionType>();

        this.searchTerm = EMPTY_SEARCH_TERM;

        this.searchIndexes = setIndexByEnum(type);
        this.logicalOperator = operator;
        this.cqlOperator = BooleanOperator.GREATER;
        this.typeList.add(type);
    }
    
    
    /**
     * Constructor with criterion type and search term.
     * 
     * @param type
     *            the type of a criteria
     * @param searchTerm
     *            the term to search for
     * @throws TechnicalException
     *             if creation of object fails
     */
    public MetadataSearchCriterion(CriterionType type, String searchTerm) throws TechnicalException
    {
        typeList = new ArrayList<CriterionType>();

        this.searchTerm = searchTerm;

        this.searchIndexes = setIndexByEnum(type);
        this.logicalOperator = LogicalOperator.UNSET;
        this.cqlOperator = BooleanOperator.EQUALS;
        this.typeList.add(type);
    }

    /**
     * Constructor with criterion type, search term and boolean operator.
     * 
     * @param type
     *            the type of a criteria
     * @param searchTerm
     *            the term to search for
     * @param booleanOperator
     *            the boolean operator to use between the index and the search
     *            term
     * @throws TechnicalException
     *             if creation of object fails
     */
    public MetadataSearchCriterion(CriterionType type, String searchTerm, BooleanOperator booleanOperator)
        throws TechnicalException
    {
        typeList = new ArrayList<CriterionType>();
        this.searchTerm = searchTerm;
        this.searchIndexes = setIndexByEnum(type);
        this.logicalOperator = LogicalOperator.UNSET;
        this.cqlOperator = BooleanOperator.EQUALS;
        this.typeList.add(type);
    }

    /**
     * Constructor with criterion type, search term, boolean operator and
     * logical operator.
     * 
     * @param type
     *            the type of a criteria
     * @param searchTerm
     *            the term to search for
     * @param booleanOperator
     *            the boolean operator to combine two search criteria
     * @param operator
     *            the boolean operator to use between the index and the search
     * @throws TechnicalException
     *             if creation of object fails
     */
    public MetadataSearchCriterion(CriterionType type, String searchTerm, BooleanOperator booleanOperator,
            LogicalOperator operator) throws TechnicalException
    {
        typeList = new ArrayList<CriterionType>();
        this.searchTerm = searchTerm;
        this.searchIndexes = setIndexByEnum(type);
        this.logicalOperator = operator;
        this.cqlOperator = booleanOperator;
        this.typeList.add(type);
    }

    /**
     * Constructor with criterion type, search term and logical operator.
     * 
     * @param type
     *            the type of a criteria
     * @param searchTerm
     *            the term to search for
     * @param operator
     *            the boolean operator to use between the index and the search
     * @throws TechnicalException
     *             if creation of object fails
     */
    public MetadataSearchCriterion(CriterionType type, String searchTerm, LogicalOperator operator)
        throws TechnicalException
    {
        typeList = new ArrayList<CriterionType>();
        this.searchTerm = searchTerm;
        this.searchIndexes = setIndexByEnum(type);
        this.logicalOperator = operator;
        this.cqlOperator = BooleanOperator.EQUALS;
        this.typeList.add(type);
    }

    /**
     * Constructor with list of criteria and a searchterm.
     * @param types  list of criteria
     * @param searchTerm  search term
     * @throws TechnicalException  if creation of object fails
     */
    public MetadataSearchCriterion(ArrayList<CriterionType> types, String searchTerm) throws TechnicalException
    {
        typeList = new ArrayList<CriterionType>();
        this.searchTerm = searchTerm;
        this.searchIndexes = new ArrayList<String>();
        for (int i = 0; i < types.size(); i++)
        {
            this.searchIndexes.addAll(setIndexByEnum(types.get(i)));
        }
        this.logicalOperator = LogicalOperator.UNSET;
        this.cqlOperator = BooleanOperator.EQUALS;
        this.typeList = types;
    }
    
    /**
     * Constructor with list of criteria, searchterm and logical operator for the criteria before.
     * @param types  list of criteria
     * @param searchTerm  search term
     * @param operator  logical operator to connect with the criteria before
     * @throws TechnicalException  if creation of object fails
     */
    protected MetadataSearchCriterion(ArrayList<CriterionType> types, String searchTerm, LogicalOperator operator) 
        throws TechnicalException
    {
        typeList = new ArrayList<CriterionType>();
        this.searchTerm = searchTerm;
        this.searchIndexes = new ArrayList<String>();
        for (int i = 0; i < types.size(); i++)
        {
            this.searchIndexes.addAll(setIndexByEnum(types.get(i)));
        }
        this.logicalOperator = operator;
        this.cqlOperator = BooleanOperator.EQUALS;
        this.typeList = types;
    }

    /**
     * Return the boolean operator as string.
     * 
     * @param operator
     *            boolean operator
     * @return boolean operator as string
     * @throws TechnicalException
     *             if enum is not supported
     */
    protected String booleanOperatorToString(BooleanOperator operator) throws TechnicalException
    {
        switch (operator)
        {
            case EQUALS:
                return BOOLEAN_EQUALS;
            case GREATER_THAN_EQUALS:
                return BOOLEAN_GREATER_THAN_EQUALS;
            case LESS_THAN_EQUALS:
                return BOOLEAN_LESS_THAN_EQUALS;
            case GREATER:
                return BOOLEAN_GREATER;
            default:
                throw new TechnicalException("Unsupported enum");
        }
    }

    /**
     * Return an array list with indices based on the criterion type.
     * 
     * @param type
     *            criterion type
     * @return array list with indices
     * @throws TechnicalException
     *             if criterion type is unknown
     */
    private ArrayList<String> setIndexByEnum(CriterionType type) throws TechnicalException
    {
        ArrayList<String> indexes = new ArrayList<String>();
        switch (type)
        {
            case TITLE:
                indexes.add(INDEX_TITLE);
                break;
            case ANY:
                indexes.add(INDEX_METADATA);
                break;
            case ANY_INCLUDE:
                indexes.add(INDEX_METADATA);
                indexes.add(INDEX_FULLTEXT);
                break;
            case PERSON:
                indexes.add(INDEX_PERSON);
                break;
            case ORGANIZATION:
                indexes.add(INDEX_ORGANIZATION);
                break;
            case GENRE:
                indexes.add(INDEX_GENRE);
                break;
            case TOPIC:
                indexes.add(INDEX_TOPIC);
                break;
            case SOURCE:
                indexes.add(INDEX_SOURCE);
                break;
            case EVENT:
                indexes.add(INDEX_EVENT);
                break;
            case IDENTIFIER:
                indexes.add(INDEX_IDENTIFIER);
                break;
            case LANGUAGE:
                indexes.add(INDEX_LANGUAGE);
                break;
            case CONTENT_TYPE:
                indexes.add(INDEX_CONTENT_TYPE);
                break;
            case CONTEXT_OBJECTID:
                indexes.add(INDEX_CONTEXT_OBJECTID);
                break;
            case CREATED_BY_OBJECTID:
                indexes.add(INDEX_CREATED_BY_OBJECTID);
                break;
            case PERSON_ROLE:
                indexes.add(INDEX_PERSON_ROLE);
                break;
            case ORGANIZATION_PIDS:
                indexes.add(INDEX_ORGANIZATION_PIDS);
                break;
            case DATE_ANY:
                indexes.add(INDEX_DATE_ANY);
                break;
            case DATE_CREATED:
                indexes.add(INDEX_DATE_CREATED);
                break;
            case DATE_ACCEPTED:
                indexes.add(INDEX_DATE_ACCEPTED);
                break;
            case DATE_SUBMITTED:
                indexes.add(INDEX_DATE_SUBMITTED);
                break;
            case DATE_MODIFIED:
                indexes.add(INDEX_DATE_MODIFIED);
                break;
            case DATE_PUBLISHED_ONLINE:
                indexes.add(INDEX_DATE_PUBLISHED_ONLINE);
                break;
            case DATE_ISSUED:
                indexes.add(INDEX_DATE_ISSUED);
                break;
            case OBJECT_TYPE:
                indexes.add(INDEX_OBJECT_TYPE);
                break;
            case COMPONENT_ACCESSABILITY:
                indexes.add(INDEX_COMPONENT_ACCESSIBILITY);
                break;
            case COMPONENT_VISIBILITY:
                indexes.add(INDEX_COMPONENT_VISIBILITY);
                break;
            case COMPONENT_CONTENT_CATEGORY:
                indexes.add(INDEX_COMPONENT_CONTENT_CATEGORY);
                break;
            case LOCAL_TAG:
                indexes.add(INDEX_LOCAL_TAG);
                break;
            case COPYRIGHT_DATE:
                indexes.add(INDEX_COPYRIGHT_DATE);
                break;
            default:
                throw new TechnicalException("The index is unknown. Cannot map to index name.");
        }
        return indexes;
    }

    /**
     * Generate a Cql query string of the criteria.
     * 
     * @return cql query
     * @throws ParseException
     *             if searchstrings are not parseable
     * @throws TechnicalException
     *             if an internal error occurs
     */
    public String generateCqlQuery() throws ParseException, TechnicalException
    {
        QueryParser parser = new QueryParser(this.searchTerm, booleanOperatorToString(this.cqlOperator));
        for (int i = 0; i < searchIndexes.size(); i++)
        {
            parser.addCQLIndex(searchIndexes.get(i));
        }
        String cqlQuery = " ( " + parser.parse() + " ) ";
        
        cqlQuery = cqlQuery + getCqlQueryFromSubCriteria();
        return cqlQuery;
    }
    
    /**
     * Get the cql query from all the subcriteria.
     * @return  cql query
     * @throws TechnicalException  if a technical problem occurs
     * @throws ParseException  if parsing went wrong
     */
    protected String getCqlQueryFromSubCriteria() throws TechnicalException, ParseException
    {
        String cqlQuery = "";
        if (!subCriteria.isEmpty())
        {
            cqlQuery = cqlQuery + subCriteria.get(0).getLogicalOperatorAsString() + " ( ";
            cqlQuery = cqlQuery + subCriteria.get(0).generateCqlQuery();
            for (int i = 1; i < subCriteria.size(); i++)
            {   
                cqlQuery = cqlQuery + " " + subCriteria.get(i).getLogicalOperatorAsString() + " "
                    + subCriteria.get(i).generateCqlQuery();
            }
            cqlQuery = cqlQuery + " ) ";
        }
        return cqlQuery;
    }

    public ArrayList<String> getSearchIndexes()
    {
        return searchIndexes;
    }

    public String getSearchTerm()
    {
        return searchTerm;
    }

    /**
     * Generate a Cql tree structure.
     * 
     * @return root node of the cql tree
     * @throws CQLParseException
     *             if tree cannot be parsed
     * @throws IOException
     *             if an io error occurs
     * @throws ParseException
     *             if the search terms cannot be parsed
     * @throws TechnicalException
     *             if an internal error occurs
     */
    public CQLNode generateCqlTree() throws CQLParseException, IOException, ParseException, TechnicalException
    {
        CQLParser parser = new CQLParser();
        CQLNode node = parser.parse(generateCqlQuery());
        return node;
    }

    /**
     * Return the last logical operator as string. If there are sub criteria 
     * for this criteria, take the logicoperator from the last subcriteria in the list. 
     * 
     * @return logical operator as string
     * @throws TechnicalException
     *             if type is unknown
     */
    public String getLogicalOperatorAsString() throws TechnicalException
    {       
        switch (this.logicalOperator)
        {
            case AND:
                return CQL_AND;
            case OR:
                return CQL_OR;
            case NOT:
                return CQL_NOT;
            default:
                throw new TechnicalException();
        }
    }

    /**
     * Getter for logical operator.
     * 
     * @return logical operator
     */
    public LogicalOperator getLogicalOperator()
    {
        return this.logicalOperator;
    }

    /**
     * Setter for logical operator.
     * 
     * @param operator
     *            logical operator
     */
    public void setLogicalOperator(LogicalOperator operator)
    {
        this.logicalOperator = operator;
    }
    
    /**
     * Adds a subCriteria to this criteria.
     * @param criterion sub criteria
     */
    public void addSubCriteria(MetadataSearchCriterion criterion)
    {
        subCriteria.add(criterion);
    }
    
    /**
     * Adds a subCriteria to this criteria.
     * @param criterion sub criteria
     */
    public void addSubCriteria(ArrayList<MetadataSearchCriterion> criteria)
    {
        subCriteria.addAll(criteria);
    }
    public ArrayList<MetadataSearchCriterion> getSubCriteriaList() 
    {
        return this.subCriteria;
    }
}
