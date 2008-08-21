/**
 * 
 */
package de.mpg.escidoc.services.search.query;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.z3950.zing.cql.CQLNode;
import org.z3950.zing.cql.CQLParseException;
import org.z3950.zing.cql.CQLParser;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.search.parser.ParseException;
import de.mpg.escidoc.services.search.parser.QueryParser;

/**
 * @author endres
 *
 */
public class MetadataSearchCriterion implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public enum CriterionType {
	        TITLE, ANY, ANY_INCLUDE, PERSON, ORGANIZATION,
	        GENRE, DATE_FROM, DATE_TO, TOPIC, SOURCE, EVENT, IDENTIFIER,
	        CONTEXT_OBJECTID, CREATED_BY_OBJECTID, LANGUAGE, CONTENT_TYPE
	        };
	        
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
	private static final String INDEX_CREATOR_ROLE = "escidoc.creator.role";
	private static final String INDEX_ORGANIZATION = "escidoc.any-organizations";
	private static final String INDEX_GENRE = "escidoc.any-genre";
	private static final String INDEX_DATE = "escidoc.any-dates";
	private static final String INDEX_TOPIC = "escidoc.any-topic";
	private static final String INDEX_SOURCE = "escidoc.any-source";
	private static final String INDEX_EVENT = "escidoc.any-event";
	private static final String INDEX_IDENTIFIER = "escidoc.any-identifier";
	private static final String INDEX_CONTEXT_OBJECTID = "escidoc.any-source";
	private static final String INDEX_CREATED_BY_OBJECTID = "escidoc.any-source";
	private static final String INDEX_LANGUAGE = "escidoc.language";
	
	private ArrayList<String> searchIndexes = null;
	private String searchTerm = null;
	private LogicalOperator logicalOperator = null;
	
	public MetadataSearchCriterion( CriterionType type, String searchTerm ) throws TechnicalException {
		this.searchIndexes = setIndexByEnum( type );
		this.searchTerm = searchTerm;
		this.logicalOperator = LogicalOperator.UNSET;
	}
	
	public MetadataSearchCriterion( CriterionType type, String searchTerm, LogicalOperator operator ) throws TechnicalException {
		this.searchIndexes = setIndexByEnum( type );
		this.searchTerm = searchTerm;
		this.logicalOperator = operator;
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
		case DATE_FROM:
			indexes.add( INDEX_DATE );
			break;
		case DATE_TO:
			indexes.add( INDEX_DATE );
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
		default:
			throw new TechnicalException("The index is unknown. Cannot map to string.");
		}	
		return indexes;
	}
	
	public String generateCqlQuery() throws ParseException {
		QueryParser parser = new QueryParser( this.searchTerm );
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
	
	public LogicalOperator getLogicalOperator() throws TechnicalException {
		return this.logicalOperator;
	}
}
