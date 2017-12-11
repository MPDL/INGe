package de.mpg.mpdl.inge.pubman.web.search.criterions.checkbox;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import de.mpg.mpdl.inge.pubman.web.search.criterions.component.MapListSearchCriterion;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;

@SuppressWarnings("serial")
public class PublicationStatusListSearchCriterion extends MapListSearchCriterion<String> {



  public PublicationStatusListSearchCriterion() {
    super(PublicationStatusListSearchCriterion.getPublicationStatusMap());
    // TODO Auto-generated constructor stub
  }

  private static Map<String, String> getPublicationStatusMap() {


    final Map<String, String> publicationStatusMap = new LinkedHashMap<String, String>();

    publicationStatusMap.put("not-specified", "not-specified");
    publicationStatusMap.put("submitted", "submitted");
    publicationStatusMap.put("accepted", "accepted");
    publicationStatusMap.put("published-online", "published-online");
    publicationStatusMap.put("published-in-print", "published-in-print");

    return publicationStatusMap;
  }



  @Override
  public String[] getCqlIndexes(Index indexName, String value) {
    switch (indexName) {
      case ESCIDOC_ALL: {
        return new String[] {"escidoc.publication-status"};
      }
      case ITEM_CONTAINER_ADMIN: {
        return new String[] {"\"/publication-status\""};
      }
    }

    return null;
  }

  @Override
  public String getCqlValue(Index indexName, String value) {
    return value;
  }

  // TODO Does not exist yet
  @Override
  public String[] getElasticIndexes(String value) {
    return new String[] {"publicationStatus"};
  }

  @Override
  public String getElasticSearchNestedPath() {
    return null;
  }



  @Override
  public QueryBuilder toElasticSearchQuery() {

    if (!this.isEmpty(QueryType.CQL)) {

      BoolQueryBuilder bq = QueryBuilders.boolQuery();
      for (final Entry<String, Boolean> entry : this.enumMap.entrySet()) {


        if (entry.getValue()) {
          final String value = this.getValueMap().get(entry.getKey());

          BoolQueryBuilder bqb = QueryBuilders.boolQuery();

          switch (value) {
            case "not-specified": {

              bqb.mustNot(QueryBuilders.existsQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_IN_PRINT));
              bqb.mustNot(QueryBuilders.existsQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_ONLINE));
              bqb.mustNot(QueryBuilders.existsQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_ACCEPTED));
              bqb.mustNot(QueryBuilders.existsQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_SUBMITTED));
              break;

            }
            case "submitted": {
              bqb.mustNot(QueryBuilders.existsQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_IN_PRINT));
              bqb.mustNot(QueryBuilders.existsQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_ONLINE));
              bqb.mustNot(QueryBuilders.existsQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_ACCEPTED));
              bqb.must(QueryBuilders.existsQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_SUBMITTED));
              break;
            }
            case "accepted": {
              bqb.mustNot(QueryBuilders.existsQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_IN_PRINT));
              bqb.mustNot(QueryBuilders.existsQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_ONLINE));
              bqb.must(QueryBuilders.existsQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_ACCEPTED));
              break;
            }
            case "published-online": {
              bqb.mustNot(QueryBuilders.existsQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_IN_PRINT));
              bqb.must(QueryBuilders.existsQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_ONLINE));
              break;

            }
            case "published-in-print": {
              bqb.must(QueryBuilders.existsQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_IN_PRINT));
              break;
            }


          }
          bq.should(bqb);
        }

      }


      return bq;

    }


    return null;
  }
}
