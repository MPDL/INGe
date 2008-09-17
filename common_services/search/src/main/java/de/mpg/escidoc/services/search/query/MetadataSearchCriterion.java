/**
 * 
 */
package de.mpg.escidoc.services.search.query;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import org.z3950.zing.cql.CQLNode;
import org.z3950.zing.cql.CQLParseException;
import org.z3950.zing.cql.CQLParser;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.search.parser.ParseException;
import de.mpg.escidoc.services.search.parser.QueryParser;

/**
 * The Metadata search criterion defines a query to a specific index with a
 * given value. One metadata search criterion can query multiple indices.
 * This is defined by the criterion type.
 * @author endres
 *
 */
public class MetadataSearchCriterion implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/** Criteria types for the search criterion */
	public enum CriterionType {
	        TITLE, ANY, ANY_INCLUDE, PERSON, PERSON_ROLE, ORGANIZATION, ORGANIZATION_PIDS,
	        GENRE, DATE_ANY, DATE_CREATED, DATE_ACCEPTED, DATE_SUBMITTED, DATE_MODIFIED, DATE_PUBLISHED_ONLINE, 
	        DATE_ISSUED, TOPIC, SOURCE, EVENT, IDENTIFIER, CONTEXT_OBJECTID, CREATED_BY_OBJECTID, LANGUAGE, CONTENT_TYPE, 
	        };
	
	/** Logical operator between the criteria. This operator is used to combine this criterion with the 
	 * criterion before */
	public enum LogicalOperator {
		AND,
		OR,
		NOT,
		UNSET
	}
	
	private static final String CQL_AND = "and";
	private static final String CQL_OR = "or";
	private static final String CQL_NOT = "not";
	 
	private static final String INDEX_CONTENT_TYPE = "escidoc.content-model.objid";
	private static final String INDEX_TITLE = "escidoc.any-title";
	private static final String INDEX_METADATA = "escidoc.metadata";
	private static final String INDEX_FULLTEXT = "escidoc.fulltext";
	private static final String INDEX_PERSON = "escidoc.any-persons";
	private static final String INDEX_PERSON_ROLE = "escidoc.creator.role";
	private static final String INDEX_ORGANIZATION = "escidoc.any-organizations";
	private static final String INDEX_ORGANIZATION_PIDS = "escidoc.any-organization-pids";
	private static final String INDEX_GENRE = "escidoc.any-genre";
	private static final String INDEX_DATE_ANY = "escidoc.any-dates";
	private static final String INDEX_DATE_CREATED = "escidoc.created";
	private static final String INDEX_DATE_ACCEPTED = "escidoc.dateAccepted";
	private static final String INDEX_DATE_SUBMITTED = "escidoc.dateSubmitted";
	private static final String INDEX_DATE_ISSUED = "escidoc.issued";
	private static final String INDEX_DATE_MODIFIED = "escidoc.modified";
	private static final String INDEX_DATE_PUBLISHED_ONLINE = "escidoc.published-online";
	private static final String INDEX_TOPIC = "escidoc.any-topic";
	private static final String INDEX_SOURCE = "escidoc.any-source";
	private static final String INDEX_EVENT = "escidoc.any-event";
	private static final String INDEX_IDENTIFIER = "escidoc.any-identifier";
	private static final String INDEX_CONTEXT_OBJECTID = "escidoc.context.objid";
	private static final String INDEX_CREATED_BY_OBJECTID = "escidoc.created-by.objid";
	private static final String INDEX_LANGUAGE = "escidoc.language";
	
	/** this boolean operator is used as a default in a cql query */
	private static final String DEFAULT_CQL_OPERATOR = "=";
	
	private ArrayList<String> searchIndexes = null;
	private String searchTerm = null;
	private LogicalOperator logicalOperator = null;
	private String cqlOperator = null;
	
	
	public MetadataSearchCriterion( CriterionType type, String searchTerm ) throws TechnicalException {
		this.searchIndexes = setIndexByEnum( type );
		this.searchTerm = searchTerm;
		this.logicalOperator = LogicalOperator.UNSET;
		this.cqlOperator = DEFAULT_CQL_OPERATOR;
	}
	
	public MetadataSearchCriterion( CriterionType type, String searchTerm, String booleanOperator ) throws TechnicalException {
		this.searchIndexes = setIndexByEnum( type );
		this.searchTerm = searchTerm;
		this.logicalOperator = LogicalOperator.UNSET;
		this.cqlOperator = booleanOperator;
	}
	
	public MetadataSearchCriterion( CriterionType type, String searchTerm, String booleanOperator, LogicalOperator operator ) throws TechnicalException {
		this.searchIndexes = setIndexByEnum( type );
		this.searchTerm = searchTerm;
		this.logicalOperator = operator;
		this.cqlOperator = booleanOperator;
	}
	
	public MetadataSearchCriterion( CriterionType type, String searchTerm, LogicalOperator operator ) throws TechnicalException {
		this.searchIndexes = setIndexByEnum( type );
		this.searchTerm = searchTerm;
		this.logicalOperator = operator;
		this.cqlOperator = DEFAULT_CQL_OPERATOR;
	}
	
	private ArrayList<String> setIndexByEnum( CriterionType type ) throws TechnicalException {
		ArrayList<String> indexes = new ArrayList<String>();
		switch( type ) {
		case TITLE: 
			indexes.add( INDEX_TITLE );
			break;
		case ANY:
			indexes.add( INDEX_METADATA );
			break;
		case ANY_INCLUDE:
			indexes.add( INDEX_METADATA );
			indexes.add( INDEX_FULLTEXT );
			break;
		case PERSON:
			indexes.add( INDEX_PERSON );
			break;
		case ORGANIZATION:
			indexes.add( INDEX_ORGANIZATION );
			break;	
		case GENRE:
			indexes.add( INDEX_GENRE );
			break;
		case TOPIC:
			indexes.add( INDEX_TOPIC );
			break;
		case SOURCE:
			indexes.add( INDEX_SOURCE );
			break;
		case EVENT:
			indexes.add( INDEX_EVENT );
			break;
		case IDENTIFIER:
			indexes.add( INDEX_IDENTIFIER );
			break;
		case LANGUAGE:
			indexes.add( INDEX_LANGUAGE );
			break;
		case CONTENT_TYPE:
			indexes.add( INDEX_CONTENT_TYPE );
		case CONTEXT_OBJECTID:
			indexes.add( INDEX_CONTEXT_OBJECTID );
			break;
		case CREATED_BY_OBJECTID:
			indexes.add( INDEX_CREATED_BY_OBJECTID );
			break;
		case PERSON_ROLE:
			indexes.add( INDEX_PERSON_ROLE );
			break;
		case ORGANIZATION_PIDS:
			indexes.add( INDEX_ORGANIZATION_PIDS );
			break;
		case DATE_ANY:
			indexes.add( INDEX_DATE_ANY );
			break;
		case DATE_CREATED:
			indexes.add( INDEX_DATE_CREATED );
			break;
		case DATE_ACCEPTED:
			indexes.add( INDEX_DATE_ACCEPTED );
			break;
		case DATE_SUBMITTED:
			indexes.add( INDEX_DATE_SUBMITTED );
			break;
		case DATE_MODIFIED:
			indexes.add( INDEX_DATE_MODIFIED);
			break;
		case DATE_PUBLISHED_ONLINE:
			indexes.add( INDEX_DATE_PUBLISHED_ONLINE );
			break;
		case DATE_ISSUED:
			indexes.add( INDEX_DATE_ISSUED );
			break;
		default:
			throw new TechnicalException("The index is unknown. Cannot map to index name.");
		}	
		return indexes;
	}
	
	public String generateCqlQuery() throws ParseException {
		QueryParser parser = new QueryParser( this.searchTerm, this.cqlOperator );
		for( int i = 0; i < searchIndexes.size(); i++ ) {
			parser.addCQLIndex( searchIndexes.get( i ) );
		}
		String cqlQuery = parser.parse();
		return cqlQuery;
	}
	
	public CQLNode generateCqlTree() throws CQLParseException, IOException, ParseException {
		CQLParser parser = new CQLParser();
		CQLNode node = parser.parse( generateCqlQuery() );
		return node;
	}
	
	public String getLogicalOperatorAsString() throws TechnicalException {
		switch( this.logicalOperator ) {
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
	
	public LogicalOperator getLogicalOperator() {
		return this.logicalOperator;
	}
	
	public void setLogicalOperator( LogicalOperator operator ) {
		this.logicalOperator = operator;
	}
}
