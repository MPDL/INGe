/*
 *
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */
package de.mpg.mpdl.inge.pubman.web.search.criterions.dates;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.json.JsonData;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;

@SuppressWarnings("serial")
public class DateSearchCriterion extends SearchCriterionBase {

  private String from;

  private String to;

  public DateSearchCriterion(SearchCriterion type) {
    super(type);
  }

  //  @Override
  //  public String toCqlString(Index indexName) {
  //
  //    return this.composeCqlFragments(this.getCQLSearchIndexes(indexName), this.getFrom(), this.getTo());
  //  }

  @Override
  public String getQueryStringContent() {
    return SearchCriterionBase.escapeForQueryString(this.from) + "|" + SearchCriterionBase.escapeForQueryString(this.to);
  }

  @Override
  public void parseQueryStringContent(String content) {
    // Split by '||', which have no backslash before
    final String[] dateParts = content.split("(?<!\\\\)\\|");
    this.from = SearchCriterionBase.unescapeForQueryString(dateParts[0]);
    if (dateParts.length > 1) {
      this.to = SearchCriterionBase.unescapeForQueryString(dateParts[1]);
    }


  }



  @Override
  public boolean isEmpty(QueryType queryType) {
    return (this.from == null || this.from.trim().isEmpty()) && (this.to == null || this.to.trim().isEmpty());
  }

  public String getFrom() {
    return this.from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public String getTo() {
    return this.to;
  }

  public void setTo(String to) {
    this.to = to;
  }



  //  private String[] getCQLSearchIndexes(Index indexName) {
  //    switch (indexName) {
  //      case ESCIDOC_ALL: {
  //        switch (this.getSearchCriterion()) {
  //          case ANYDATE:
  //            return new String[] {"escidoc.publication.published-online", "escidoc.publication.issued", "escidoc.publication.dateAccepted",
  //                "escidoc.publication.dateSubmitted", "escidoc.publication.modified", "escidoc.publication.created"};
  //          case PUBLISHED:
  //            return new String[] {"escidoc.publication.published-online"};
  //          case PUBLISHEDPRINT:
  //            return new String[] {"escidoc.publication.issued"};
  //          case ACCEPTED:
  //            return new String[] {"escidoc.publication.dateAccepted"};
  //          case SUBMITTED:
  //            return new String[] {"escidoc.publication.dateSubmitted"};
  //          case MODIFIED:
  //            return new String[] {"escidoc.publication.modified"};
  //          case CREATED:
  //            return new String[] {"escidoc.publication.created"};
  //          case EVENT_STARTDATE:
  //            return new String[] {"escidoc.publication.event.start-date"};
  //          case EVENT_ENDDATE:
  //            return new String[] {"escidoc.publication.event.end-date"};
  //          case MODIFIED_INTERNAL:
  //            return new String[] {"escidoc.last-modification-date"};
  //          case CREATED_INTERNAL:
  //            return new String[] {"escidoc.property.creation-date"};
  //          case COMPONENT_EMBARGO_DATE:
  //            return new String[] {"escidoc.component.file.available"};
  //
  //          default:
  //            break;
  //        }
  //      }
  //
  //      case ITEM_CONTAINER_ADMIN: {
  //        switch (this.getSearchCriterion()) {
  //          case ANYDATE:
  //            return new String[] {"\"/md-records/md-record/publication/published-online\"", "\"/md-records/md-record/publication/issued\"",
  //                "\"/md-records/md-record/publication/dateAccepted\"", "\"/md-records/md-record/publication/dateSubmitted\"",
  //                "\"/md-records/md-record/publication/modified\"", "\"/md-records/md-record/publication/created\""};
  //          case PUBLISHED:
  //            return new String[] {"\"/md-records/md-record/publication/published-online\""};
  //          case PUBLISHEDPRINT:
  //            return new String[] {"\"/md-records/md-record/publication/issued\""};
  //          case ACCEPTED:
  //            return new String[] {"\"/md-records/md-record/publication/dateAccepted\""};
  //          case SUBMITTED:
  //            return new String[] {"\"/md-records/md-record/publication/dateSubmitted\""};
  //          case MODIFIED:
  //            return new String[] {"\"/md-records/md-record/publication/modified\""};
  //          case CREATED:
  //            return new String[] {"\"/md-records/md-record/publication/created\""};
  //          case EVENT_STARTDATE:
  //            return new String[] {"\"/md-records/md-record/publication/event/start-date\""};
  //          case EVENT_ENDDATE:
  //            return new String[] {"\"/md-records/md-record/publication/event/end-date\""};
  //
  //          case MODIFIED_INTERNAL:
  //            return new String[] {"\"/last-modification-date/date\""};
  //          case CREATED_INTERNAL:
  //            return new String[] {"\"/properties/creation-date/date\""};
  //
  //          case COMPONENT_EMBARGO_DATE:
  //            return new String[] {"\"/components/component/md-records/md-record/file/available\""};
  //
  //          default:
  //            break;
  //        }
  //      }
  //    }
  //
  //    return null;
  //  }
  //
  //  private String composeCqlFragments(String[] searchIndexes, String minor, String major) {
  //    final StringBuffer buffer = new StringBuffer();
  //    buffer.append(" ( ");
  //    try {
  //      for (int i = 0; i < searchIndexes.length; i++) {
  //        if (i == (searchIndexes.length - 1)) {
  //
  //          buffer.append(this.createCqlFragment(searchIndexes[i], minor, major));
  //        } else {
  //          buffer.append(this.createCqlFragment(searchIndexes[i], minor, major));
  //          buffer.append(" or ");
  //        }
  //      }
  //    } catch (final Exception e) {
  //      e.printStackTrace();
  //    }
  //    buffer.append(" ) ");
  //    return buffer.toString();
  //  }

  //  private String createCqlFragment(String index, String minor, String major) throws Exception {
  //    String fromQuery = null;
  //    String toQuery = null;
  //    if (minor != null && !minor.trim().isEmpty()) {
  //      minor = this.normalizeFromQuery(minor);
  //
  //      fromQuery = index + ">=\"" + SearchCriterionBase.escapeForCql(minor) + "\"";
  //
  //      /*
  //       * QueryParser parserFrom = new QueryParser(minor, ">="); parserFrom.addCQLIndex(index);
  //       * fromQuery = parserFrom.parse();
  //       */
  //    }
  //    if (major != null && !major.trim().isEmpty()) {
  //      final String[] majorParts = this.normalizeToQuery(major);
  //      toQuery = index + "<=\"" + SearchCriterionBase.escapeForCql(majorParts[0]) + "\"";
  //      /*
  //       * QueryParser parserTo = new QueryParser(majorParts[0], "<="); parserTo.addCQLIndex(index);
  //       * toQuery = parserTo.parse();
  //       */
  //
  //      for (int i = 1; i < majorParts.length; i++) {
  //        final String toSubQuery = index + "=\"" + SearchCriterionBase.escapeForCql(majorParts[i]) + "\"";
  //        toQuery += " not ( " + toSubQuery + " ) ";
  //
  //        /*
  //         * QueryParser parserNotTo = new QueryParser(majorParts[i], "=");
  //         * parserNotTo.addCQLIndex(index); toQuery += " " + "not" + " ( " + parserNotTo.parse() +
  //         * " ) ";
  //         */
  //      }
  //    }
  //
  //    final StringBuffer buffer = new StringBuffer();
  //
  //    if (fromQuery == null) {
  //      buffer.append(" ( " + toQuery + " ) ");
  //    } else if (toQuery == null)
  //
  //    {
  //      buffer.append(" ( " + fromQuery + " ) ");
  //    } else {
  //      buffer.append(" ( " + fromQuery + " and ( " + toQuery + " ) ) ");
  //    }
  //    return buffer.toString();
  //  }

  //  public String normalizeFromQuery(String fromQuery) {
  //    if (fromQuery == null) {
  //      return null;
  //    } else if (fromQuery.matches("\\d\\d\\d\\d")) {
  //      return fromQuery;
  //    } else if (fromQuery.matches("\\d\\d\\d\\d-\\d\\d")) {
  //      final String[] parts = fromQuery.split("-");
  //      if ("01".equals(parts[1])) {
  //        return parts[0];
  //      } else {
  //        return fromQuery;
  //      }
  //    } else if (fromQuery.matches("\\d\\d\\d\\d-\\d\\d-\\d\\d")) {
  //      final String[] parts = fromQuery.split("-");
  //      if ("01".equals(parts[2])) {
  //        if ("01".equals(parts[1])) {
  //          return parts[0];
  //        } else {
  //          return parts[0] + "-" + parts[1];
  //        }
  //      } else {
  //        return fromQuery;
  //      }
  //    } else {
  //      return fromQuery;
  //    }
  //  }
  //
  //  public String[] normalizeToQuery(String toQuery) {
  //    if (toQuery == null) {
  //      return null;
  //    } else if (toQuery.matches("\\d\\d\\d\\d")) {
  //      return new String[] {toQuery + "-12-31"};
  //    } else if (toQuery.matches("\\d\\d\\d\\d-\\d\\d")) {
  //      final String[] parts = toQuery.split("-");
  //      if ("12".equals(parts[1])) {
  //        return new String[] {toQuery + "-31"};
  //      } else {
  //        return new String[] {toQuery + "-31", parts[0]};
  //      }
  //    } else if (toQuery.matches("\\d\\d\\d\\d-\\d\\d-\\d\\d")) {
  //      final String[] parts = toQuery.split("-");
  //      // Get last day of month
  //      if ("31".equals(parts[2]) && "12".equals(parts[1])) {
  //        return new String[] {toQuery};
  //      } else {
  //        final Calendar calendar = Calendar.getInstance();
  //        calendar.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]));
  //        final int maximumDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
  //        if (Integer.parseInt(parts[2]) == maximumDay) {
  //          return new String[] {toQuery, parts[0]};
  //        } else {
  //          return new String[] {toQuery, parts[0], parts[0] + "-" + parts[1]};
  //        }
  //      }
  //    } else {
  //      return new String[] {toQuery};
  //    }
  //  }

  @Override
  public Query toElasticSearchQuery() {
    return toElasticSearchQuery(this.getSearchCriterion(), this.getFrom(), this.getTo());
  }

  public static Query toElasticSearchQuery(SearchCriterion sc, String from, String to) {
    switch (sc) {
      case ANYDATE: {
        BoolQuery.Builder bq = new BoolQuery.Builder();
        bq.should(buildDateRangeQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_IN_PRINT, from, to));
        bq.should(buildDateRangeQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_ONLINE, from, to));
        bq.should(buildDateRangeQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_ACCEPTED, from, to));
        bq.should(buildDateRangeQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_SUBMITTED, from, to));
        bq.should(buildDateRangeQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_MODIFIED, from, to));
        bq.should(buildDateRangeQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_CREATED, from, to));
        return bq.build()._toQuery();
      }
      case PUBLISHED:
        return buildDateRangeQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_ONLINE, from, to);
      case PUBLISHEDPRINT:
        return buildDateRangeQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_IN_PRINT, from, to);
      case ACCEPTED:
        return buildDateRangeQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_ACCEPTED, from, to);
      case SUBMITTED:
        return buildDateRangeQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_SUBMITTED, from, to);
      case MODIFIED:
        return buildDateRangeQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_MODIFIED, from, to);
      case CREATED:
        return buildDateRangeQuery(PubItemServiceDbImpl.INDEX_METADATA_DATE_CREATED, from, to);
      case EVENT_STARTDATE:
        return buildDateRangeQuery(PubItemServiceDbImpl.INDEX_METADATA_EVENT_STARTDATE, from, to);
      case EVENT_ENDDATE:
        return buildDateRangeQuery(PubItemServiceDbImpl.INDEX_METADATA_EVENT_ENDDATE, from, to);
      case MODIFIED_INTERNAL:
        return buildDateRangeQuery(PubItemServiceDbImpl.INDEX_MODIFICATION_DATE, from, to);
      case CREATED_INTERNAL:
        return buildDateRangeQuery(PubItemServiceDbImpl.INDEX_CREATION_DATE, from, to);
      case COMPONENT_EMBARGO_DATE:
        return buildDateRangeQuery(PubItemServiceDbImpl.INDEX_FILE_METADATA_EMBARGO_UNTIL, from, to);

      default:
        return null;
    }
  }


  private static Query buildDateRangeQuery(String index, String from, String to) {

    RangeQuery.Builder qb = new RangeQuery.Builder();
    qb.field(index);
    if (from != null && !from.trim().isEmpty()) {
      qb.gte(JsonData.of(roundDateString(from)));
    }
    if (to != null && !to.trim().isEmpty()) {
      qb.lte(JsonData.of(roundDateString(to)));
    }
    return qb.build()._toQuery();
  }


  @Override
  public String getElasticSearchNestedPath() {
    if (this.getSearchCriterion() == SearchCriterion.COMPONENT_EMBARGO_DATE) {
      return "files";
    }
    return null;
  }


  public static String roundDateString(String toQuery) {
    if (toQuery == null) {
      return null;
    } else if (toQuery.matches("\\d\\d\\d\\d")) {
      return toQuery + "||/y";
    } else if (toQuery.matches("\\d\\d\\d\\d-\\d\\d")) {
      return toQuery + "||/M";
      /*
       * final String[] parts = toQuery.split("-"); YearMonth yearMonth =
       * YearMonth.of(Integer.parseInt(parts[0]), Month.of(Integer.parseInt(parts[1]))); int
       * daysInMonth = yearMonth.lengthOfMonth(); return toQuery + "-" + daysInMonth;
       */
    } else if (toQuery.matches("\\d\\d\\d\\d-\\d\\d-\\d\\d")) {
      return toQuery + "||/d";

    }

    return toQuery;


  }
}
