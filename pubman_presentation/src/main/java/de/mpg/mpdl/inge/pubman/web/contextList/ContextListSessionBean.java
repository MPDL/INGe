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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import de.mpg.mpdl.inge.es.handler.ContextServiceHandler;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO.State;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO.PredefinedRoles;
import de.mpg.mpdl.inge.model.valueobjects.SearchQueryVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubContextVOPresentation;
import de.mpg.mpdl.inge.services.SearchInterface;
import de.mpg.mpdl.inge.services.SearchInterfaceConnectorFactory;

/**
 * Keeps all attributes that are used for the whole session by the CollectionList.
 * 
 * @author: Thomas Diebäcker, created 12.10.2007
 * @version: $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "ContextListSessionBean")
@SessionScoped
@SuppressWarnings("serial")
public class ContextListSessionBean extends FacesBean {
  private static final Logger logger = Logger.getLogger(ContextListSessionBean.class);

  private List<PubContextVOPresentation> depositorContextList =
      new ArrayList<PubContextVOPresentation>();

  private List<PubContextVOPresentation> moderatorContextList =
      new ArrayList<PubContextVOPresentation>();

  private List<PubContextVOPresentation> yearbookContextList =
      new ArrayList<PubContextVOPresentation>();

  private List<PubContextVOPresentation> yearbookModeratorContextList =
      new ArrayList<PubContextVOPresentation>();

  private List<PubContextVOPresentation> allPrivilegedContextList =
      new ArrayList<PubContextVOPresentation>();

  private ContextServiceHandler contextServiceHandler;;

  // private UIXIterator contextIterator = new UIXIterator();

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
    final List<PubContextVOPresentation> newDepositorContextList =
        new ArrayList<PubContextVOPresentation>();

    if (this.getOpenContextsAvailable()) {
      for (final PubContextVOPresentation context : this.depositorContextList) {
        if (context.getState() == State.OPENED) {
          newDepositorContextList.add(context);
        }
      }
      Collections.sort(newDepositorContextList);
      this.setDepositorContextList(newDepositorContextList);
    }

    return this.depositorContextList;
  }

  public boolean getOpenContextsAvailable() {
    State state = State.CLOSED;

    for (final PubContextVOPresentation context : this.depositorContextList) {
      if (context.getState() == State.OPENED) {
        state = State.OPENED;
      }
    }

    if (state == State.OPENED) {
      return true;
    }

    return false;
  }

  public int getDepositorContextListSize() {
    if (this.depositorContextList == null) {
      return 0;
    }

    return this.depositorContextList.size();
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
    if (this.moderatorContextList == null) {
      return 0;
    }

    return this.moderatorContextList.size();
  }

  public void setModeratorContextList(List<PubContextVOPresentation> moderatorContextList) {
    this.moderatorContextList = moderatorContextList;
  }

  /*
   * public UIXIterator getContextIterator() { return contextIterator; }
   * 
   * public void setContextIterator(UIXIterator contextIterator) { this.contextIterator =
   * contextIterator; }
   */

  public void setYearbookContextList(List<PubContextVOPresentation> yearbookContextList) {
    this.yearbookContextList = yearbookContextList;
  }

  public List<PubContextVOPresentation> getYearbookContextList() {
    return this.yearbookContextList;
  }

  public int getYearbookContextListSize() {
    if (this.yearbookContextList == null) {
      return 0;
    }

    return this.yearbookContextList.size();
  }

  public int getYearbookModeratorContextListSize() {
    if (this.yearbookModeratorContextList == null) {
      return 0;
    }

    return this.yearbookModeratorContextList.size();
  }

  public void setYearbookModeratorContextList(
      List<PubContextVOPresentation> yearbookModeratorContextList) {
    this.yearbookModeratorContextList = yearbookModeratorContextList;
  }

  public List<PubContextVOPresentation> getYearbookModeratorContextList() {
    return this.yearbookModeratorContextList;
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
    if (this.getLoginHelper().isLoggedIn()
        && this.getLoginHelper().getAccountUser().getGrantsWithoutAudienceGrants() != null) {
      try {

        boolean hasGrants = false;

        final ArrayList<String> ctxIdList = new ArrayList<>();
        for (final GrantVO grant : this.getLoginHelper().getAccountUser()
            .getGrantsWithoutAudienceGrants()) {
          if (grant.getObjectRef() != null) {
            final String id = grant.getObjectRef();
            ctxIdList.add(id);
            hasGrants = true;
          }
        }

        // ... and transform filter to xml
        if (hasGrants) {

          SearchInterface<QueryBuilder> searchService= SearchInterfaceConnectorFactory.getInstance();
          BoolQueryBuilder bq = QueryBuilders.boolQuery();

          for (final String id : ctxIdList) {
            bq.should(QueryBuilders.termQuery("reference.objectId", id));
          }
          
          SearchRetrieveResponseVO response = searchService.searchForContexts(new SearchQueryVO<QueryBuilder>(bq, 0, 0, null));
          List<ContextVO> ctxList = response.getRecords().stream().map(rec -> (ContextVO)rec.getData()).collect(Collectors.toList());
          
          // ... and transform to PubCollections.
          this.allPrivilegedContextList =
              CommonUtils.convertToPubCollectionVOPresentationList(ctxList);
        }

        this.depositorContextList = new ArrayList<PubContextVOPresentation>();
        this.moderatorContextList = new ArrayList<PubContextVOPresentation>();
        this.yearbookContextList = new ArrayList<PubContextVOPresentation>();
        this.yearbookModeratorContextList = new ArrayList<PubContextVOPresentation>();

        for (final PubContextVOPresentation context : this.allPrivilegedContextList) {
          // TODO NBU: change this dummy looping once AccountUserVO
          // provides method for
          // isDepositor(ObjectRef)
          // At present it only provides this function for Moderator
          // and Privileged viewer

          for (final GrantVO grant : this.getLoginHelper().getAccountUser()
              .getGrantsWithoutAudienceGrants()) {
            if ((grant.getObjectRef() != null) && !grant.getObjectRef().equals("")) {

              if (!grant.getObjectRef().equals("")
                  && grant.getObjectRef().equals(context.getReference().getObjectId())
                  && grant.getRole().equals(PredefinedRoles.DEPOSITOR.frameworkValue())
                  && context.getType().toLowerCase().equals(("PubMan".toLowerCase()))) {

                this.depositorContextList.add(context);
              }
              if (!grant.getObjectRef().equals("")
                  && grant.getObjectRef().equals(context.getReference().getObjectId())
                  && grant.getRole().equals(PredefinedRoles.MODERATOR.frameworkValue())
                  && context.getType().toLowerCase().equals(("PubMan".toLowerCase()))) {

                this.moderatorContextList.add(context);
              }

              if (!grant.getObjectRef().equals("")
                  && grant.getObjectRef().equals(context.getReference().getObjectId())
                  && grant.getRole().equals(PredefinedRoles.DEPOSITOR.frameworkValue())
                  && context.getType().toLowerCase().equals(("Yearbook".toLowerCase()))) {
                this.yearbookContextList.add(context);
              }

              if (!grant.getObjectRef().equals("")
                  && grant.getObjectRef().equals(context.getReference().getObjectId())
                  && grant.getRole().equals(PredefinedRoles.MODERATOR.frameworkValue())
                  && context.getType().toLowerCase().equals(("Yearbook".toLowerCase()))) {
                this.yearbookModeratorContextList.add(context);
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

  public List<PubContextVOPresentation> getAllPrivilegedContextList() {
    return this.allPrivilegedContextList;
  }

  public void setAllPrivilegedContextList(List<PubContextVOPresentation> allPrivilegedContextList) {
    this.allPrivilegedContextList = allPrivilegedContextList;
  }
}
