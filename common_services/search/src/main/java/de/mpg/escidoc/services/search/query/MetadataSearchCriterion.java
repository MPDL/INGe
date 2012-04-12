/**
 * 
 */
package de.mpg.escidoc.services.search.query;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ThisExpression;
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
        TITLE, ANY, ANY_INCLUDE, ABSTRACT, PERSON, PERSON_ROLE, ORGANIZATION, CREATOR_ORGANIZATION, ORGANIZATION_PIDS, CREATOR_ORGANIZATION_IDS, GENRE, DATE_ANY,
        DATE_CREATED, DATE_ACCEPTED, DATE_SUBMITTED, DATE_MODIFIED, DATE_PUBLISHED_ONLINE, DATE_ISSUED, DATE_EVENT_START, DATE_EVENT_END, TOPIC,
        SOURCE, EVENT, IDENTIFIER, CONTEXT_OBJECTID, CONTEXT_NAME, CREATED_BY_OBJECTID, LANGUAGE, CONTENT_TYPE, OBJECT_TYPE,
        COMPONENT_ACCESSIBILITY, COMPONENT_STORAGE, COMPONENT_VISIBILITY, COMPONENT_CONTENT_CATEGORY, COMPONENT_COMPOUND_PROPERTIES, LOCAL_TAG, COPYRIGHT_DATE, 
        EMBARGO_DATE, DEGREE, PERSON_IDENTIFIER, LATEST_RELEASE_OBJID, OBJID
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

    /** Index field for the content-type. */
    private static final String INDEX_CONTENT_TYPE = "escidoc.content-model.objid";
    /** Index field for any-title. */
    private static final String INDEX_TITLE = "escidoc.publication.title";
    /** Index field for metadata. */
    private static final String INDEX_METADATA = "escidoc.metadata";
    /** Index field for any-title. */
    private static final String INDEX_ABSTRACT = "escidoc.publication.abstract";
    /** Index field for fulltexts. */
    private static final String INDEX_FULLTEXT = "escidoc.fulltext";
    /** Index field for persons. */
    private static final String INDEX_PERSON = "escidoc.publication.creator.person.compound.person-complete-name";
    /** Index field for creator roles. */
    private static final String INDEX_PERSON_ROLE = "escidoc.publication.creator.role";
    /** Index field for organizations. */
    private static final String INDEX_ORGANIZATION = "escidoc.any-organizations";
    /** Index field for creator_organizations. */
    private static final String INDEX_CREATOR_ORGANIZATION = "escidoc.publication.creator.person.organization.title";
    /** Index field for organization pids. */
    private static final String INDEX_ORGANIZATION_PIDS = "escidoc.any-organization-pids";
    /** Index field for creator organization ids. */
    private static final String INDEX_CREATOR_ORGANIZATION_IDS = "escidoc.publication.creator.person.organization.identifier";
    /** Index field for genre. */
    private static final String INDEX_GENRE = "escidoc.publication.type";
    /** Index field for dates. */
    private static final String INDEX_DATE_ANY = "escidoc.publication.compound.dates";
    /** Index field for creation date. */
    private static final String INDEX_DATE_CREATED = "escidoc.publication.created";
    /** Index field for the accepted date. */
    private static final String INDEX_DATE_ACCEPTED = "escidoc.publication.dateAccepted";
    /** Index field for the submitted date. */
    private static final String INDEX_DATE_SUBMITTED = "escidoc.publication.dateSubmitted";
    /** Index field for the issued date. */
    private static final String INDEX_DATE_ISSUED = "escidoc.publication.issued";
    /** Index field for the modified date. */
    private static final String INDEX_DATE_MODIFIED = "escidoc.publication.modified";
    /** Index field for the published online date. */
    private static final String INDEX_DATE_PUBLISHED_ONLINE = "escidoc.publication.published-online";
    /** Index field for the evebt start date. */
    private static final String INDEX_DATE_EVENT_START = "escidoc.publication.event.start-date";
    /** Index field for the evebt start date. */
    private static final String INDEX_DATE_EVENT_END = "escidoc.publication.event.end-date";
    /** Index field for topics. */
    private static final String INDEX_TOPIC = "escidoc.publication.subject";
    /** Index field for sources. */
    private static final String INDEX_SOURCE = "escidoc.publication.source.any.title";
    /** Index field for events. */
    private static final String INDEX_EVENT = "escidoc.any-event";
    /** Index field for identifiers. */
    private static final String INDEX_IDENTIFIER = "escidoc.any-identifier";
    /** Index field for object ids of contexts. */
    private static final String INDEX_CONTEXT_OBJECTID = "escidoc.context.objid";
    /** Index field for name of contexts. */
    private static final String INDEX_CONTEXT_NAME = "escidoc.context.name";
    /** Index field for languages. */
    private static final String INDEX_LANGUAGE = "escidoc.publication.language";
    /** Index field for object types. */
    private static final String INDEX_OBJECT_TYPE = "escidoc.objecttype";
    
    /** COMPONENTS **/
    /** Index field for the created-by object id. */
    private static final String INDEX_CREATED_BY_OBJECTID = "escidoc.component.created-by.objid";
    /** Index field for component availability. */
    private static final String INDEX_COMPONENT_ACCESSIBILITY = "escidoc.component.creation-date";
    
    private static final String INDEX_COMPONENT_STORAGE = "escidoc.component.content.storage";
    /** Index field for component visibility. */
    private static final String INDEX_COMPONENT_VISIBILITY = "escidoc.component.visibility";
    /** Index field for component content category. */
    private static final String INDEX_COMPONENT_CONTENT_CATEGORY = "escidoc.component.content-category";
    /** Index field for copyright date. */
    private static final String INDEX_COPYRIGHT_DATE = "escidoc.component.file.dateCopyrighted";
    /** Index field for copyright date. */
    private static final String INDEX_EMBARGO_DATE = "escidoc.component.file.available";
    /** Index field for compound properties. */
    private static final String INDEX_COMPOUND_PROPERTIES = "escidoc.component.compound.properties";
    


	/** Index field for local tags. */
    private static final String INDEX_LOCAL_TAG = "escidoc.property.content-model-specific.local-tags.local-tag";
    
    /** Index field for degree. */
    private static final String INDEX_DEGREE = "escidoc.publication.degree";
    
    /** Index field for person identifier. */
    private static final String INDEX_PERSON_IDENTIFIER = "escidoc.publication.creator.person.identifier";
    
    /** Index field for item latest released objectID. */
    private static final String INDEX_LATEST_RELEASE_OBJID = "escidoc.property.latest-release.objid";
    
    /** Index field for item latest released objectID. */
    private static final String INDEX_OBJID = "escidoc.objid";
    
    
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

    MetadataSearchCriterion() {}
    
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
            case ABSTRACT:
            	indexes.add(INDEX_ABSTRACT);
            	break;
            case PERSON:
                indexes.add(INDEX_PERSON);
                break;
            case ORGANIZATION:
                indexes.add(INDEX_ORGANIZATION);
                break;
            case CREATOR_ORGANIZATION:
                indexes.add(INDEX_CREATOR_ORGANIZATION);
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
            case CONTEXT_NAME:
            	indexes.add(INDEX_CONTEXT_NAME);
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
            case CREATOR_ORGANIZATION_IDS:
                indexes.add(INDEX_CREATOR_ORGANIZATION_IDS);
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
            case COMPONENT_ACCESSIBILITY:
                indexes.add(INDEX_COMPONENT_ACCESSIBILITY);
                break;
            case COMPONENT_STORAGE:
                indexes.add(INDEX_COMPONENT_STORAGE);
                break;
            case COMPONENT_VISIBILITY:
                indexes.add(INDEX_COMPONENT_VISIBILITY);
                break;
            case COMPONENT_CONTENT_CATEGORY:
                indexes.add(INDEX_COMPONENT_CONTENT_CATEGORY);
                break;
            case COMPONENT_COMPOUND_PROPERTIES:
            	indexes.add(INDEX_COMPOUND_PROPERTIES);
            	break;
            case LOCAL_TAG:
                indexes.add(INDEX_LOCAL_TAG);
                break;
            case COPYRIGHT_DATE:
                indexes.add(INDEX_COPYRIGHT_DATE);
                break;
            case EMBARGO_DATE:
                indexes.add(INDEX_EMBARGO_DATE);
                break;
            case DEGREE:
                indexes.add(INDEX_DEGREE);
                break;
            case PERSON_IDENTIFIER:
                indexes.add(INDEX_PERSON_IDENTIFIER);
                break;
            case LATEST_RELEASE_OBJID:
                indexes.add(INDEX_LATEST_RELEASE_OBJID);
                break;
            case OBJID:
                indexes.add(INDEX_OBJID);
                break;
            case DATE_EVENT_START:
                indexes.add(INDEX_DATE_EVENT_START);
                break;
            case DATE_EVENT_END:
                indexes.add(INDEX_DATE_EVENT_END);
                break;
            default:
                throw new TechnicalException("The index " + type.name() + " is unknown. Cannot map to index name.");
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
    
    /**
     * Returns all used indices in a string array. 
     * @return string array list of all indices.
     */
    public List<String> getAllSupportedIndicesAsString() throws TechnicalException {
        List<String> indices = new ArrayList<String>();
        
        for( CriterionType type : CriterionType.values() ) {
            indices.addAll( setIndexByEnum(type) );
        }
        return indices;
    }
    
    public static String getINDEX_CONTENT_TYPE()
    {
        return INDEX_CONTENT_TYPE;
    }

    public static String getINDEX_TITLE()
    {
        return INDEX_TITLE;
    }

    public static String getINDEX_METADATA()
    {
        return INDEX_METADATA;
    }

    public static String getINDEX_ABSTRACT()
    {
        return INDEX_ABSTRACT;
    }

    public static String getINDEX_FULLTEXT()
    {
        return INDEX_FULLTEXT;
    }

    public static String getINDEX_PERSON()
    {
        return INDEX_PERSON;
    }

    public static String getINDEX_PERSON_ROLE()
    {
        return INDEX_PERSON_ROLE;
    }

    public static String getINDEX_ORGANIZATION()
    {
        return INDEX_ORGANIZATION;
    }
    
    public static String getINDEX_CREATOR_ORGANIZATION()
    {
        return INDEX_CREATOR_ORGANIZATION;
    }

    public static String getINDEX_ORGANIZATION_PIDS()
    {
        return INDEX_ORGANIZATION_PIDS;
    }
    
    public static String getINDEX_CREATOR_ORGANIZATION_IDS()
    {
        return INDEX_CREATOR_ORGANIZATION_IDS;
    }

    public static String getINDEX_GENRE()
    {
        return INDEX_GENRE;
    }

    public static String getINDEX_DATE_ANY()
    {
        return INDEX_DATE_ANY;
    }

    public static String getINDEX_DATE_CREATED()
    {
        return INDEX_DATE_CREATED;
    }

    public static String getINDEX_DATE_ACCEPTED()
    {
        return INDEX_DATE_ACCEPTED;
    }

    public static String getINDEX_DATE_SUBMITTED()
    {
        return INDEX_DATE_SUBMITTED;
    }

    public static String getINDEX_DATE_ISSUED()
    {
        return INDEX_DATE_ISSUED;
    }

    public static String getINDEX_DATE_MODIFIED()
    {
        return INDEX_DATE_MODIFIED;
    }

    public static String getINDEX_DATE_PUBLISHED_ONLINE()
    {
        return INDEX_DATE_PUBLISHED_ONLINE;
    }

    public static String getINDEX_TOPIC()
    {
        return INDEX_TOPIC;
    }

    public static String getINDEX_SOURCE()
    {
        return INDEX_SOURCE;
    }

    public static String getINDEX_EVENT()
    {
        return INDEX_EVENT;
    }

    public static String getINDEX_IDENTIFIER()
    {
        return INDEX_IDENTIFIER;
    }

    public static String getINDEX_CONTEXT_OBJECTID()
    {
        return INDEX_CONTEXT_OBJECTID;
    }

    public static String getINDEX_CONTEXT_NAME()
    {
        return INDEX_CONTEXT_NAME;
    }

    public static String getINDEX_CREATED_BY_OBJECTID()
    {
        return INDEX_CREATED_BY_OBJECTID;
    }

    public static String getINDEX_LANGUAGE()
    {
        return INDEX_LANGUAGE;
    }

    public static String getINDEX_OBJECT_TYPE()
    {
        return INDEX_OBJECT_TYPE;
    }

    public static String getINDEX_COMPONENT_ACCESSIBILITY()
    {
        return INDEX_COMPONENT_ACCESSIBILITY;
    }

    public static String getINDEX_COMPONENT_STORAGE()
    {
        return INDEX_COMPONENT_STORAGE;
    }

    public static String getINDEX_COMPONENT_VISIBILITY()
    {
        return INDEX_COMPONENT_VISIBILITY;
    }

    public static String getINDEX_COMPONENT_CONTENT_CATEGORY()
    {
        return INDEX_COMPONENT_CONTENT_CATEGORY;
    }

	public static String getINDEX_COMPOUND_PROPERTIES() 
	{
		return INDEX_COMPOUND_PROPERTIES;
	}
    
    public static String getINDEX_LOCAL_TAG()
    {
        return INDEX_LOCAL_TAG;
    }

    public static String getINDEX_COPYRIGHT_DATE()
    {
        return INDEX_COPYRIGHT_DATE;
    }

    public static String getINDEX_EMBARGO_DATE()
    {
        return INDEX_EMBARGO_DATE;
    }

    public static String getINDEX_DEGREE()
    {
        return INDEX_DEGREE;
    }

    public static String getINDEX_PERSON_IDENTIFIER()
    {
        return INDEX_PERSON_IDENTIFIER;
    }
    
    public static String getINDEX_OBJID()
    {
        return INDEX_OBJID;
    }



    
}
