package de.mpg.mpdl.inge.pubman.web.search.criterions.checkbox;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.ExistsQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
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



  //  @Override
  //  public String[] getCqlIndexes(Index indexName, String value) {
  //    switch (indexName) {
  //      case ESCIDOC_ALL: {
  //        return new String[] {"escidoc.publication-status"};
  //      }
  //      case ITEM_CONTAINER_ADMIN: {
  //        return new String[] {"\"/publication-status\""};
  //      }
  //    }
  //
  //    return null;
  //  }

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
  public Query toElasticSearchQuery() {

    if (!this.isEmpty(QueryType.CQL)) {

      BoolQuery.Builder bq = new BoolQuery.Builder();
      for (final Entry<String, Boolean> entry : this.enumMap.entrySet()) {


        if (entry.getValue()) {
          final String value = this.getValueMap().get(entry.getKey());

          BoolQuery.Builder bqb = new BoolQuery.Builder();

          switch (value) {
            case "not-specified": {

              bqb.mustNot(ExistsQuery.of(e -> e.field(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_IN_PRINT))._toQuery());
              bqb.mustNot(ExistsQuery.of(e -> e.field(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_ONLINE))._toQuery());
              bqb.mustNot(ExistsQuery.of(e -> e.field(PubItemServiceDbImpl.INDEX_METADATA_DATE_ACCEPTED))._toQuery());
              bqb.mustNot(ExistsQuery.of(e -> e.field(PubItemServiceDbImpl.INDEX_METADATA_DATE_SUBMITTED))._toQuery());
              break;

            }
            case "submitted": {
              bqb.mustNot(ExistsQuery.of(e -> e.field(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_IN_PRINT))._toQuery());
              bqb.mustNot(ExistsQuery.of(e -> e.field(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_ONLINE))._toQuery());
              bqb.mustNot(ExistsQuery.of(e -> e.field(PubItemServiceDbImpl.INDEX_METADATA_DATE_ACCEPTED))._toQuery());
              bqb.must(ExistsQuery.of(e -> e.field(PubItemServiceDbImpl.INDEX_METADATA_DATE_SUBMITTED))._toQuery());
              break;
            }
            case "accepted": {
              bqb.mustNot(ExistsQuery.of(e -> e.field(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_IN_PRINT))._toQuery());
              bqb.mustNot(ExistsQuery.of(e -> e.field(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_ONLINE))._toQuery());
              bqb.must(ExistsQuery.of(e -> e.field(PubItemServiceDbImpl.INDEX_METADATA_DATE_ACCEPTED))._toQuery());
              break;
            }
            case "published-online": {
              bqb.mustNot(ExistsQuery.of(e -> e.field(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_IN_PRINT))._toQuery());
              bqb.must(ExistsQuery.of(e -> e.field(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_ONLINE))._toQuery());
              break;

            }
            case "published-in-print": {
              bqb.must(ExistsQuery.of(e -> e.field(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_IN_PRINT))._toQuery());
              break;
            }


          }
          bq.should(bqb.build()._toQuery());
        }

      }


      return bq.build()._toQuery();

    }


    return null;
  }
}
