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
package de.mpg.mpdl.inge.pubman.web.search.criterions.checkbox;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO.InvitationStatus;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;

@SuppressWarnings("serial")
public class EventInvitationSearchCriterion extends SearchCriterionBase {

  private boolean invited = false;

  //  @Override
  //  public String toCqlString(Index indexName) {
  //    if (this.isInvited()) {
  //      switch (indexName) {
  //        case ESCIDOC_ALL:
  //          return "escidoc.publication.event.invitation-status=\"invited\"";
  //        case ITEM_CONTAINER_ADMIN:
  //          return "\"/md-records/md-record/publication/event/invitation-status\">\"''\"";
  //      }
  //    }
  //
  //    return null;
  //  }

  @Override
  public String getQueryStringContent() {
    return String.valueOf(this.invited);
  }

  @Override
  public void parseQueryStringContent(String content) {
    this.invited = Boolean.parseBoolean(content);
  }

  @Override
  public boolean isEmpty(QueryType queryType) {
    return !this.isInvited();
  }

  public boolean isInvited() {
    return this.invited;
  }

  public void setInvited(boolean invited) {
    this.invited = invited;
  }

  @Override
  public Query toElasticSearchQuery() {
    if (this.isInvited()) {
      return SearchCriterionBase.baseElasticSearchQueryBuilder(new String[] {PubItemServiceDbImpl.INDEX_METADATA_EVENT_INVITATION_STATUS},
          InvitationStatus.INVITED.name());
    }

    return null;
  }

  @Override
  public String getElasticSearchNestedPath() {
    return null;
  }
}
