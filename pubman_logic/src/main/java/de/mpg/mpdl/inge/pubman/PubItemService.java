/*
 * 
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

package de.mpg.mpdl.inge.pubman;

import static de.mpg.mpdl.inge.pubman.logging.PMLogicMessages.PUBITEM_CREATED;
import static de.mpg.mpdl.inge.pubman.logging.PMLogicMessages.PUBITEM_UPDATED;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import de.escidoc.core.common.exceptions.application.invalid.InvalidContextException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.FileNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyPublishedException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyWithdrawnException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.NotPublishedException;
import de.escidoc.www.services.om.ItemHandler;
import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.model.referenceobjects.ContextRO;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO.FrameworkContextTypeFilter;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO.PubCollectionStatusFilter;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemRelationVO;
import de.mpg.mpdl.inge.model.valueobjects.PidTaskParamVO;
import de.mpg.mpdl.inge.model.valueobjects.ResultVO;
import de.mpg.mpdl.inge.model.valueobjects.TaskParamVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AlternativeTitleVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SubjectVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.model.xmltransforming.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.exceptions.ExceptionHandler;
import de.mpg.mpdl.inge.pubman.exceptions.MissingWithdrawalCommentException;
import de.mpg.mpdl.inge.pubman.exceptions.PubCollectionNotFoundException;
import de.mpg.mpdl.inge.pubman.exceptions.PubFileContentNotFoundException;
import de.mpg.mpdl.inge.pubman.exceptions.PubItemAlreadyReleasedException;
import de.mpg.mpdl.inge.pubman.exceptions.PubItemLockedException;
import de.mpg.mpdl.inge.pubman.exceptions.PubItemMandatoryAttributesMissingException;
import de.mpg.mpdl.inge.pubman.exceptions.PubItemNotFoundException;
import de.mpg.mpdl.inge.pubman.exceptions.PubItemStatusInvalidException;
import de.mpg.mpdl.inge.pubman.logging.ApplicationLog;
import de.mpg.mpdl.inge.pubman.logging.PMLogicMessages;
import de.mpg.mpdl.inge.services.ContextInterfaceConnectorFactory;
import de.mpg.mpdl.inge.services.ItemInterfaceConnectorFactory;
import de.mpg.mpdl.inge.util.PropertyReader;

public class PubItemService {
  private static final Logger logger = Logger.getLogger(PubItemService.class);

  public static final String WORKFLOW_SIMPLE = "simple";
  public static final String WORKFLOW_STANDARD = "standard";

  private static final String PREDICATE_ISREVISIONOF =
      "http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#isRevisionOf";

  public static PubItemVO createPubItem(final ContextRO pubCollectionRef, final AccountUserVO user)
      throws PubCollectionNotFoundException, SecurityException, TechnicalException {

    if (pubCollectionRef == null) {
      throw new IllegalArgumentException(PubItemService.class
          + ".createPubItem: pubCollection reference is null.");
    }

    if (pubCollectionRef.getObjectId() == null) {
      throw new IllegalArgumentException(PubItemService.class
          + ".createPubItem: pubCollection reference does not contain an objectId.");
    }

    if (user == null) {
      throw new IllegalArgumentException(PubItemService.class + ".createPubItem: user is null.");
    }

    ContextVO collection = null;
    try {
      // TODO remove replace
      collection =
          ContextInterfaceConnectorFactory.getInstance()
              .readContext(pubCollectionRef.getObjectId());
    } catch (ContextNotFoundException e) {
      throw new PubCollectionNotFoundException(pubCollectionRef, e);
    } catch (Exception e) {
      ExceptionHandler.handleException(e, PubItemService.class + ".createPubItem");
    }

    // TODO check if user can write to this collection

    PubItemVO result = new PubItemVO();
    result.setContext(pubCollectionRef);
    if (collection.getDefaultMetadata() != null
        && collection.getDefaultMetadata() instanceof MdsPublicationVO) {
      result.setMetadata((MdsPublicationVO) collection.getDefaultMetadata());
    } else {
      result.setMetadata(new MdsPublicationVO());
    }

    return result;
  }

  public static void deletePubItem(final ItemRO pubItemRef, final AccountUserVO user)
      throws PubItemLockedException, PubItemNotFoundException, PubItemStatusInvalidException,
      SecurityException, TechnicalException {

    if (pubItemRef == null) {
      throw new IllegalArgumentException(PubItemService.class
          + ".deletePubItem: pubItem reference is null.");
    }

    if (pubItemRef.getObjectId() == null) {
      throw new IllegalArgumentException(PubItemService.class
          + ".deletePubItem: pubItem reference does not contain an objectId.");
    }

    if (user == null) {
      throw new IllegalArgumentException(PubItemService.class + ".deletePubItem: user is null.");
    }

    try {
      ItemInterfaceConnectorFactory.getInstance().deleteItem(pubItemRef.getObjectId());

      ApplicationLog.info(PMLogicMessages.PUBITEM_DELETED, new Object[] {pubItemRef.getObjectId(),
          user.getUserid()});
    } catch (LockingException e) {
      throw new PubItemLockedException(pubItemRef, e);
    } catch (ItemNotFoundException e) {
      throw new PubItemNotFoundException(pubItemRef, e);
    } catch (AlreadyPublishedException e) {
      throw new PubItemStatusInvalidException(pubItemRef, e);
    } catch (InvalidStatusException e) {
      throw new PubItemStatusInvalidException(pubItemRef, e);
    } catch (Exception e) {
      ExceptionHandler.handleException(e, PubItemService.class + "deletePubItem");
    }
  }

  public static List<ContextVO> getPubCollectionListForDepositing() throws SecurityException,
      TechnicalException {
    try {
      // Create filter
      FilterTaskParamVO filterParam = new FilterTaskParamVO();

      FrameworkContextTypeFilter typeFilter = filterParam.new FrameworkContextTypeFilter("Pubman");
      filterParam.getFilterList().add(typeFilter);

      PubCollectionStatusFilter statusFilter =
          filterParam.new PubCollectionStatusFilter(ContextVO.State.OPENED);
      filterParam.getFilterList().add(statusFilter);

      HashMap<String, String[]> filterMap = filterParam.toMap();

      // Get context list
      String xmlContextList = ServiceLocator.getContextHandler().retrieveContexts(filterMap);
      // ... and transform to PubCollections.
      List<ContextVO> contextList =
          (List<ContextVO>) XmlTransformingService
              .transformSearchRetrieveResponseToContextList(xmlContextList);

      return contextList;
    } catch (Exception e) {
      ExceptionHandler.handleException(e, "PubItemDepositing.getPubCollectionListForDepositing");
    }

    return null;
  }

  public static PubItemVO savePubItem(final PubItemVO pubItem, final AccountUserVO user)
      throws PubItemMandatoryAttributesMissingException, PubCollectionNotFoundException,
      PubItemLockedException, PubItemNotFoundException, PubItemAlreadyReleasedException,
      PubItemStatusInvalidException, TechnicalException, AuthorizationException {

    if (pubItem == null) {
      throw new IllegalArgumentException(PubItemService.class.getSimpleName()
          + ".savePubItem: pubItem is null.");
    }

    if (user == null) {
      throw new IllegalArgumentException(PubItemService.class.getSimpleName()
          + ".savePubItem: user is null.");
    }

    try {
      PMLogicMessages message;
      if (pubItem.getVersion() == null || pubItem.getVersion().getObjectId() == null) {
        ItemRO itemVersion = new ItemRO();
        itemVersion.setVersionNumber(1);
        // TODO remove test objectID
        itemVersion.setObjectId("pure:12345");
        itemVersion.setState(PubItemVO.State.PENDING);
        Date creationDate = Calendar.getInstance().getTime();
        itemVersion.setModificationDate(creationDate);
        pubItem.setVersion(itemVersion);
        pubItem.setPublicStatus(PubItemVO.State.PENDING);
        pubItem.setCreationDate(creationDate);
        pubItem.setOwner(user.getReference());
        ItemInterfaceConnectorFactory.getInstance().createItem(pubItem,
            pubItem.getVersion().getObjectId());
        message = PUBITEM_CREATED;
      } else {
        ItemInterfaceConnectorFactory.getInstance().updateItem(pubItem,
            pubItem.getVersion().getObjectId());
        message = PUBITEM_UPDATED;
      }

      ApplicationLog.info(message,
          new Object[] {pubItem.getVersion().getObjectId(), user.getUserid()});
    } catch (MissingAttributeValueException e) {
      throw new PubItemMandatoryAttributesMissingException(pubItem, e);
    } catch (ContextNotFoundException e) {
      throw new PubCollectionNotFoundException(pubItem.getContext(), e);
    } catch (MissingElementValueException e) {
      throw new PubItemMandatoryAttributesMissingException(pubItem, e);
    } catch (LockingException e) {
      throw new PubItemLockedException(pubItem.getVersion(), e);
    } catch (InvalidContextException e) {
      throw new PubCollectionNotFoundException(pubItem.getContext(), e);
    } catch (ItemNotFoundException e) {
      throw new PubItemNotFoundException(pubItem.getVersion(), e);
    } catch (AlreadyPublishedException e) {
      throw new PubItemAlreadyReleasedException(pubItem.getVersion(), e);
    } catch (InvalidStatusException e) {
      throw new PubItemStatusInvalidException(pubItem.getVersion(), e);
    } catch (FileNotFoundException e) {
      throw new PubFileContentNotFoundException(pubItem.getFiles(), e);
    } catch (NotPublishedException e) {
      throw new TechnicalException(e);
    } catch (AuthorizationException e) {
      throw e;
    } catch (Exception e) {
      ExceptionHandler.handleException(e, PubItemService.class.getSimpleName() + ".savePubItem");
    }

    return pubItem;
  }

  // TODO: submissionComment verwenden! (-> siehe auch QualityAssuranceBean, PubItemPublishingBean)
  public static PubItemVO submitPubItem(final PubItemVO pubItem, String comment,
      final AccountUserVO user) throws PubItemStatusInvalidException, PubItemNotFoundException,
      SecurityException, TechnicalException {

    if (pubItem == null) {
      throw new IllegalArgumentException(PubItemService.class + ".submitPubItem: pubItem is null.");
    }

    if (user == null) {
      throw new IllegalArgumentException(PubItemService.class + ".submitPubItem: user is null.");
    }

    PubItemVO savedPubItem = pubItem;
    try {
      ItemInterfaceConnectorFactory.getInstance().updateItem(pubItem,
          pubItem.getVersion().getObjectId());

      ApplicationLog.info(PMLogicMessages.PUBITEM_SUBMITTED, new Object[] {
          savedPubItem.getVersion().getObjectId(), user.getUserid()});
    } catch (InvalidStatusException e) {
      throw new PubItemStatusInvalidException(pubItem.getVersion(), e);
    } catch (ItemNotFoundException e) {
      throw new PubItemNotFoundException(savedPubItem.getVersion(), e);
    } catch (Exception e) {
      ExceptionHandler.handleException(e, "PubItemDepositing.submitPubItem");
    }

    return pubItem;
  }

  public static PubItemVO createRevisionOfPubItem(final PubItemVO originalPubItem,
      String relationComment, final ContextRO pubCollection, final AccountUserVO owner) {
    PubItemVO copiedPubItem = new PubItemVO();
    copiedPubItem.setOwner(owner.getReference());
    copiedPubItem.setContext(pubCollection);
    copiedPubItem.setMetadata(new MdsPublicationVO());
    copiedPubItem.getMetadata().setGenre(originalPubItem.getMetadata().getGenre());

    for (CreatorVO creator : originalPubItem.getMetadata().getCreators()) {
      copiedPubItem.getMetadata().getCreators().add((CreatorVO) creator.clone());
    }

    if (originalPubItem.getMetadata().getTitle() != null) {
      copiedPubItem.getMetadata().setTitle(originalPubItem.getMetadata().getTitle());
    }

    for (String language : originalPubItem.getMetadata().getLanguages()) {
      copiedPubItem.getMetadata().getLanguages().add(language);
    }

    for (AlternativeTitleVO title : originalPubItem.getMetadata().getAlternativeTitles()) {
      copiedPubItem.getMetadata().getAlternativeTitles().add((AlternativeTitleVO) title.clone());
    }

    if (originalPubItem.getMetadata().getFreeKeywords() != null) {
      copiedPubItem.getMetadata().setFreeKeywords(originalPubItem.getMetadata().getFreeKeywords());
    }

    if (originalPubItem.getMetadata().getSubjects() != null) {
      for (SubjectVO subject : originalPubItem.getMetadata().getSubjects()) {
        copiedPubItem.getMetadata().getSubjects().add(subject);
      }
    }

    ItemRelationVO relation = new ItemRelationVO();
    relation.setType(PREDICATE_ISREVISIONOF);
    relation.setTargetItemRef(originalPubItem.getVersion());
    relation.setDescription(relationComment);
    copiedPubItem.getRelations().add(relation);

    return copiedPubItem;
  }

  // TODO: TaskParamVO ersetzen (siehe PubItemDepositingBean, PubItemPublishingBean)
  public static PubItemVO revisePubItem(final ItemRO pubItemRef, String comment,
      final AccountUserVO user) throws ServiceException, TechnicalException,
      PubItemStatusInvalidException, SecurityException, PubItemNotFoundException {

    if (pubItemRef == null) {
      throw new IllegalArgumentException(PubItemService.class + ".submitPubItem: pubItem is null.");
    }

    if (user == null) {
      throw new IllegalArgumentException(PubItemService.class + ".submitPubItem: user is null.");
    }

    ItemHandler itemHandler;
    try {
      itemHandler = ServiceLocator.getItemHandler(user.getHandle());
    } catch (Exception e) {
      throw new TechnicalException(e);
    }

    PubItemVO pubItemActual = null;
    try {
      TaskParamVO taskParam = new TaskParamVO(pubItemRef.getModificationDate(), comment);
      itemHandler.revise(pubItemRef.getObjectId(),
          XmlTransformingService.transformToTaskParam(taskParam));

      String item = itemHandler.retrieve(pubItemRef.getObjectId());
      pubItemActual = XmlTransformingService.transformToPubItem(item);

      ApplicationLog.info(PMLogicMessages.PUBITEM_REVISED, new Object[] {pubItemRef.getObjectId(),
          user.getUserid()});
    } catch (InvalidStatusException e) {
      throw new PubItemStatusInvalidException(pubItemRef, e);
    } catch (ItemNotFoundException e) {
      throw new PubItemNotFoundException(pubItemRef, e);
    } catch (Exception e) {
      ExceptionHandler.handleException(e, "QualityAssuranceBean.revisePubItem");
    }

    return pubItemActual;
  }

  // TODO: TaskParamVO ersetzen (siehe PubItemDepositingBean, QualityassuranceBean)
  public static PubItemVO releasePubItem(final ItemRO pubItemRef, final Date lastModificationDate,
      String comment, final AccountUserVO user) throws TechnicalException,
      PubItemStatusInvalidException, PubItemNotFoundException, PubItemLockedException,
      SecurityException {
    long gstart = System.currentTimeMillis();

    if (pubItemRef == null) {
      throw new IllegalArgumentException(PubItemService.class
          + ".releasePubItem: pubItem reference is null.");
    }

    if (user == null) {
      throw new IllegalArgumentException(PubItemService.class + ".releasePubItem: user is null.");
    }

    if (pubItemRef.getObjectId() == null) {
      throw new IllegalArgumentException(PubItemService.class
          + ".releasePubItem: pubItem reference does not contain an objectId.");
    }

    ItemHandler itemHandler;
    try {
      itemHandler = ServiceLocator.getItemHandler(user.getHandle());
    } catch (Exception e) {
      throw new TechnicalException(e);
    }

    logger.info("*** start release of <" + pubItemRef.getObjectId() + "> ");

    PubItemVO actualItemVO = null;
    try {
      String actualItem = itemHandler.retrieve(pubItemRef.getObjectId());
      actualItemVO = XmlTransformingService.transformToPubItem(actualItem);

      PidTaskParamVO pidParam;
      String paramXml;
      String url;
      String result = null;

      // Floating PID assignment.
      if (actualItemVO.getPid() == null || actualItemVO.getPid().equals("")) {
        long start = System.currentTimeMillis();
        url =
            PropertyReader.getProperty("escidoc.pubman.instance.url")
                + PropertyReader.getProperty("escidoc.pubman.instance.context.path")
                + PropertyReader.getProperty("escidoc.pubman.item.pattern").replaceAll("\\$1",
                    pubItemRef.getObjectId());

        pidParam = new PidTaskParamVO(lastModificationDate, url);
        paramXml = XmlTransformingService.transformToPidTaskParam(pidParam);

        try {
          result = itemHandler.assignObjectPid(pubItemRef.getObjectId(), paramXml);
        } catch (Exception e) {
          logger.warn("Object PID assignment for " + pubItemRef.getObjectId()
              + " failed. It probably already has one.");
        }
        long end = System.currentTimeMillis();
        logger.info("assign object PID for <" + pubItemRef.getObjectId() + "> needed <"
            + (end - start) + "> msec");

        // Retrieve the item to get last modification date
        actualItem = itemHandler.retrieve(pubItemRef.getObjectId());
        actualItemVO = XmlTransformingService.transformToPubItem(actualItem);
      }

      if (actualItemVO.getVersion().getPid() == null
          || actualItemVO.getVersion().getPid().equals("")) {
        long start = System.currentTimeMillis();
        url =
            PropertyReader.getProperty("escidoc.pubman.instance.url")
                + PropertyReader.getProperty("escidoc.pubman.instance.context.path")
                + PropertyReader.getProperty("escidoc.pubman.item.pattern").replaceAll("\\$1",
                    pubItemRef.getObjectId() + ":" + actualItemVO.getVersion().getVersionNumber());

        pidParam = new PidTaskParamVO(actualItemVO.getModificationDate(), url);
        paramXml = XmlTransformingService.transformToPidTaskParam(pidParam);

        try {
          result =
              itemHandler.assignVersionPid(actualItemVO.getVersion().getObjectId() + ":"
                  + actualItemVO.getVersion().getVersionNumber(), paramXml);
        } catch (Exception e) {
          logger.warn("Version PID assignment for " + pubItemRef.getObjectId()
              + " failed. It probably already has one.", e);
        }

        long end = System.currentTimeMillis();
        logger.info("assign version PID for <" + pubItemRef.getObjectId() + "> needed <"
            + (end - start) + "> msec");
      }

      // Loop over files
      for (FileVO file : actualItemVO.getFiles()) {

        if ((file.getPid() == null || file.getPid().equals(""))
            && file.getStorage().equals(FileVO.Storage.INTERNAL_MANAGED)) {
          long start = System.currentTimeMillis();
          url =
              PropertyReader.getProperty("escidoc.pubman.instance.url")
                  + PropertyReader.getProperty("escidoc.pubman.instance.context.path")
                  + PropertyReader.getProperty("escidoc.pubman.component.pattern")
                      .replaceAll("\\$1", pubItemRef.getObjectIdAndVersion())
                      .replaceAll("\\$2", file.getReference().getObjectId())
                      .replaceAll("\\$3", CommonUtils.urlEncode(file.getName()));

          try {
            ResultVO resultVO = XmlTransformingService.transformToResult(result);
            pidParam = new PidTaskParamVO(resultVO.getLastModificationDate(), url);
            paramXml = XmlTransformingService.transformToPidTaskParam(pidParam);

            result =
                itemHandler.assignContentPid(actualItemVO.getVersion().getObjectId(), file
                    .getReference().getObjectId(), paramXml);

            logger.info("Component PID assigned: " + result);
          } catch (Exception e) {
            logger.warn("Component PID assignment for " + pubItemRef.getObjectId()
                + " failed. It probably already has one.", e);
          }

          long end = System.currentTimeMillis();
          logger.info("assign content PID for " + pubItemRef.getObjectId() + "> needed <"
              + (end - start) + "> msec");
        }

      }

      // Retrieve the item to get last modification date
      actualItem = itemHandler.retrieve(pubItemRef.getObjectId());
      actualItemVO = XmlTransformingService.transformToPubItem(actualItem);

      // Release the item
      long s = System.currentTimeMillis();
      TaskParamVO param = new TaskParamVO(actualItemVO.getModificationDate(), comment);
      itemHandler.release(pubItemRef.getObjectId(),
          XmlTransformingService.transformToTaskParam(param));
      long e = System.currentTimeMillis();
      logger.info("pure itemHandler.release item " + pubItemRef.getObjectId() + "> needed <"
          + (e - s) + "> msec");

      ApplicationLog
          .info(PMLogicMessages.PUBITEM_RELEASED, new Object[] {pubItemRef.getObjectId()});
    } catch (LockingException e) {
      throw new PubItemLockedException(pubItemRef, e);
    } catch (ItemNotFoundException e) {
      throw new PubItemNotFoundException(pubItemRef, e);
    } catch (InvalidStatusException e) {
      throw new PubItemStatusInvalidException(pubItemRef, e);
    } catch (Exception e) {
      ExceptionHandler.handleException(e, PubItemService.class + ".releasePubItem");
    }

    long gend = System.currentTimeMillis();
    logger.info("*** total release of <" + pubItemRef.getObjectId() + "> needed <"
        + (gend - gstart) + "> msec");

    return actualItemVO;
  }

  public static void withdrawPubItem(final PubItemVO pubItem, final Date lastModificationDate,
      String comment, final AccountUserVO user) throws MissingWithdrawalCommentException,
      PubItemNotFoundException, PubItemStatusInvalidException, TechnicalException,
      PubItemLockedException, SecurityException {

    if (pubItem == null) {
      throw new IllegalArgumentException(PubItemService.class
          + ".withdrawPubItem: pubItem reference is null.");
    }

    if (pubItem.getVersion().getObjectId() == null) {
      throw new IllegalArgumentException(PubItemService.class
          + ".withdrawPubItem: pubItem reference does not contain an objectId.");
    }

    if (user == null) {
      throw new IllegalArgumentException(PubItemService.class + ".withdrawPubItem: user is null.");
    }

    if (user.getGrants().contains(
        new GrantVO("escidoc:role-administrator", pubItem.getContext().getObjectId()))) {
      throw new SecurityException();
    }

    // Check the withdrawal comment - must not be null or empty.
    if (comment == null || comment.trim().length() == 0) {
      throw new MissingWithdrawalCommentException(pubItem.getVersion());
    }

    try {
      TaskParamVO param = new TaskParamVO(lastModificationDate, comment);
      ServiceLocator.getItemHandler(user.getHandle()).withdraw(pubItem.getVersion().getObjectId(),
          XmlTransformingService.transformToTaskParam(param));

      ApplicationLog.info(PMLogicMessages.PUBITEM_WITHDRAWN, new Object[] {
          pubItem.getVersion().getObjectId(), user.getUserid()});
    } catch (LockingException e) {
      throw new PubItemLockedException(pubItem.getVersion(), e);
    } catch (AlreadyWithdrawnException e) {
      throw new PubItemStatusInvalidException(pubItem.getVersion(), e);
    } catch (ItemNotFoundException e) {
      throw new PubItemNotFoundException(pubItem.getVersion(), e);
    } catch (NotPublishedException e) {
      throw new PubItemStatusInvalidException(pubItem.getVersion(), e);
    } catch (InvalidStatusException e) {
      throw new PubItemStatusInvalidException(pubItem.getVersion(), e);
    } catch (Exception e) {
      ExceptionHandler.handleException(e, PubItemService.class + ".withdrawPubItem");
    }
  }
}
