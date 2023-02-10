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

package de.mpg.mpdl.inge.pubman.web.contextList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO.PredefinedRoles;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubContextVOPresentation;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.pubman.impl.ContextServiceDbImpl;
import de.mpg.mpdl.inge.service.util.SearchUtils;

/**
 * Keeps all attributes that are used for the whole session by the CollectionList.
 * 
 * @author: Thomas Diebäcker, created 12.10.2007
 * @version: $Revision$ $LastChangedDate$
 */
@SuppressWarnings("serial")
@ManagedBean(name = "ContextListSessionBean")
@SessionScoped
public class ContextListSessionBean extends FacesBean {
  private static final Logger logger = Logger.getLogger(ContextListSessionBean.class);

  private List<PubContextVOPresentation> depositorContextList;
  private List<PubContextVOPresentation> moderatorContextList;

  public ContextListSessionBean() {
    this.init();
  }

  public void init() {
    try {
      this.retrieveAllContextsForUser();
    } catch (final Exception e) {
      ContextListSessionBean.logger.error("Could not create context list.", e);
    }
  }

  public List<PubContextVOPresentation> getDepositorContextList() {
    return this.depositorContextList;
  }

  public boolean getOpenContextsAvailable() {
    return this.getDepositorContextList().isEmpty() == false;
  }

  public int getDepositorContextListSize() {
    return this.depositorContextList == null ? 0 : this.depositorContextList.size();
  }

  public void setDepositorContextList(List<PubContextVOPresentation> contextList) {
    this.depositorContextList = contextList;
  }

  public PubContextVOPresentation getSelectedDepositorContext() {
    for (final PubContextVOPresentation coll : this.depositorContextList) {
      if (coll.getSelected()) {
        return coll;
      }
    }

    return null;
  }

  public List<PubContextVOPresentation> getModeratorContextList() {
    return this.moderatorContextList;
  }

  public int getModeratorContextListSize() {
    return this.moderatorContextList == null ? 0 : this.moderatorContextList.size();
  }

  public void setModeratorContextList(List<PubContextVOPresentation> moderatorContextList) {
    this.moderatorContextList = moderatorContextList;
  }

  // TODO NBU: this method needs to be moved elsewhere here only to avoid
  // common logic modification
  // at present
  /**
   * @Retrieves A list of all contexts for which user has granted privileges @see
   *            LoginHelper.getUserGrants
   * @throws SecurityException
   * @throws TechnicalException
   */
  private void retrieveAllContextsForUser() throws SecurityException, TechnicalException {
    this.depositorContextList = new ArrayList<PubContextVOPresentation>();
    this.moderatorContextList = new ArrayList<PubContextVOPresentation>();

    if (this.getLoginHelper().isLoggedIn() && this.getLoginHelper().getAccountUser().getGrantList() != null) {
      try {
        boolean hasGrants = false;
        final ArrayList<String> ctxIdList = new ArrayList<>();
        for (final GrantVO grant : this.getLoginHelper().getAccountUser().getGrantList()) {
          if (grant.getObjectRef() != null) {
            ctxIdList.add(grant.getObjectRef());
            hasGrants = true;
          }
        }

        ContextService contextService = ApplicationBean.INSTANCE.getContextService();

        // ... and transform filter to xml
        if (hasGrants) {
          BoolQueryBuilder bq = QueryBuilders.boolQuery();
          bq.must(SearchUtils.baseElasticSearchQueryBuilder(contextService.getElasticSearchIndexFields(), ContextServiceDbImpl.INDEX_STATE,
              ContextDbVO.State.OPENED.name()));

          for (final String id : ctxIdList) {
            bq.should(QueryBuilders.termQuery(ContextServiceDbImpl.INDEX_OBJECT_ID, id));
          }

          SearchRetrieveResponseVO<ContextDbVO> response = contextService.search(new SearchRetrieveRequestVO(bq, 1000, 0), null);
          List<ContextDbVO> ctxList = response.getRecords().stream().map(rec -> rec.getData()).collect(Collectors.toList());

          // ... and transform to PubCollections.
          List<PubContextVOPresentation> allPrivilegedContextList = CommonUtils.convertToPubCollectionVOPresentationList(ctxList);

          for (final PubContextVOPresentation context : allPrivilegedContextList) {
            // TODO NBU: change this dummy looping once AccountUserVO
            // provides method for
            // isDepositor(ObjectRef)
            // At present it only provides this function for Moderator
            // and Privileged viewer

            for (final GrantVO grant : this.getLoginHelper().getAccountUser().getGrantList()) {
              if ((grant.getObjectRef() != null) && !grant.getObjectRef().equals("")) {
                if (grant.getObjectRef().equals(context.getObjectId())
                    && PredefinedRoles.DEPOSITOR.frameworkValue().contentEquals(grant.getRole())) {
                  this.depositorContextList.add(context);
                } else if (grant.getObjectRef().equals(context.getObjectId())
                    && PredefinedRoles.MODERATOR.frameworkValue().contentEquals(grant.getRole())) {
                  this.moderatorContextList.add(context);
                }
              }
            }
          }
        }
      } catch (final Exception e) {
        // No business exceptions expected.
        throw new TechnicalException(e);
      }
    }
  }
}
