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

package de.mpg.mpdl.inge.pubman.publishing;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.log4j.Logger;

import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyWithdrawnException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.NotPublishedException;
import de.escidoc.www.services.om.ItemHandler;
import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.PidTaskParamVO;
import de.mpg.mpdl.inge.model.valueobjects.ResultVO;
import de.mpg.mpdl.inge.model.valueobjects.TaskParamVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransforming;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.model.xmltransforming.logging.LogMethodDurationInterceptor;
import de.mpg.mpdl.inge.model.xmltransforming.logging.LogStartEndInterceptor;
import de.mpg.mpdl.inge.model.xmltransforming.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.PubItemPublishing;
import de.mpg.mpdl.inge.pubman.exceptions.ExceptionHandler;
import de.mpg.mpdl.inge.pubman.exceptions.MissingWithdrawalCommentException;
import de.mpg.mpdl.inge.pubman.exceptions.PubItemLockedException;
import de.mpg.mpdl.inge.pubman.exceptions.PubItemNotFoundException;
import de.mpg.mpdl.inge.pubman.exceptions.PubItemStatusInvalidException;
import de.mpg.mpdl.inge.pubman.logging.ApplicationLog;
import de.mpg.mpdl.inge.pubman.logging.PMLogicMessages;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * This class provides the ejb implementation of the {@link PubItemPublishing} interface.
 * 
 * @author Miriam Doelle (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$ Revised by StG: 24.08.2007
 */
@Remote(PubItemPublishing.class)
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Interceptors({LogStartEndInterceptor.class, LogMethodDurationInterceptor.class})
public class PubItemPublishingBean implements PubItemPublishing {
  private static final Logger LOGGER = Logger.getLogger(PubItemPublishingBean.class);

  @EJB
  private XmlTransforming xmlTransforming;

  // TODO: TaskParamVO ersetzen (siehe PubItemDepositingBean, QualityassuranceBean)
  public PubItemVO releasePubItem(final ItemRO pubItemRef, final Date lastModificationDate,
      String comment, final AccountUserVO user) throws TechnicalException,
      PubItemStatusInvalidException, PubItemNotFoundException, PubItemLockedException,
      SecurityException {
    long gstart = System.currentTimeMillis();

    if (pubItemRef == null) {
      throw new IllegalArgumentException(getClass() + ".releasePubItem: pubItem reference is null.");
    }

    if (user == null) {
      throw new IllegalArgumentException(getClass() + ".releasePubItem: user is null.");
    }

    if (pubItemRef.getObjectId() == null) {
      throw new IllegalArgumentException(getClass()
          + ".releasePubItem: pubItem reference does not contain an objectId.");
    }

    ItemHandler itemHandler;
    try {
      itemHandler = ServiceLocator.getItemHandler(user.getHandle());
    } catch (Exception e) {
      throw new TechnicalException(e);
    }

    LOGGER.info("*** start release of <" + pubItemRef.getObjectId() + "> ");

    PubItemVO actualItemVO = null;
    try {
      String actualItem = itemHandler.retrieve(pubItemRef.getObjectId());
      actualItemVO = xmlTransforming.transformToPubItem(actualItem);

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
        paramXml = xmlTransforming.transformToPidTaskParam(pidParam);

        try {
          result = itemHandler.assignObjectPid(pubItemRef.getObjectId(), paramXml);
        } catch (Exception e) {
          LOGGER.warn("Object PID assignment for " + pubItemRef.getObjectId()
              + " failed. It probably already has one.");
        }
        long end = System.currentTimeMillis();
        LOGGER.info("assign object PID for <" + pubItemRef.getObjectId() + "> needed <"
            + (end - start) + "> msec");

        // Retrieve the item to get last modification date
        actualItem = itemHandler.retrieve(pubItemRef.getObjectId());
        actualItemVO = xmlTransforming.transformToPubItem(actualItem);
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
        paramXml = xmlTransforming.transformToPidTaskParam(pidParam);

        try {
          result =
              itemHandler.assignVersionPid(actualItemVO.getVersion().getObjectId() + ":"
                  + actualItemVO.getVersion().getVersionNumber(), paramXml);
        } catch (Exception e) {
          LOGGER.warn("Version PID assignment for " + pubItemRef.getObjectId()
              + " failed. It probably already has one.", e);
        }

        long end = System.currentTimeMillis();
        LOGGER.info("assign version PID for <" + pubItemRef.getObjectId() + "> needed <"
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
            ResultVO resultVO = xmlTransforming.transformToResult(result);
            pidParam = new PidTaskParamVO(resultVO.getLastModificationDate(), url);
            paramXml = xmlTransforming.transformToPidTaskParam(pidParam);

            result =
                itemHandler.assignContentPid(actualItemVO.getVersion().getObjectId(), file
                    .getReference().getObjectId(), paramXml);

            LOGGER.info("Component PID assigned: " + result);
          } catch (Exception e) {
            LOGGER.warn("Component PID assignment for " + pubItemRef.getObjectId()
                + " failed. It probably already has one.", e);
          }

          long end = System.currentTimeMillis();
          LOGGER.info("assign content PID for " + pubItemRef.getObjectId() + "> needed <"
              + (end - start) + "> msec");
        }

      }

      // Retrieve the item to get last modification date
      actualItem = itemHandler.retrieve(pubItemRef.getObjectId());
      actualItemVO = xmlTransforming.transformToPubItem(actualItem);

      // Release the item
      long s = System.currentTimeMillis();
      TaskParamVO param = new TaskParamVO(actualItemVO.getModificationDate(), comment);
      itemHandler.release(pubItemRef.getObjectId(), xmlTransforming.transformToTaskParam(param));
      long e = System.currentTimeMillis();
      LOGGER.info("pure itemHandler.release item " + pubItemRef.getObjectId() + "> needed <"
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
      ExceptionHandler.handleException(e, getClass() + ".releasePubItem");
    }

    long gend = System.currentTimeMillis();
    LOGGER.info("*** total release of <" + pubItemRef.getObjectId() + "> needed <"
        + (gend - gstart) + "> msec");

    return actualItemVO;
  }

  public final void withdrawPubItem(final PubItemVO pubItem, final Date lastModificationDate,
      String comment, final AccountUserVO user) throws MissingWithdrawalCommentException,
      PubItemNotFoundException, PubItemStatusInvalidException, TechnicalException,
      PubItemLockedException, SecurityException {

    if (pubItem == null) {
      throw new IllegalArgumentException(getClass()
          + ".withdrawPubItem: pubItem reference is null.");
    }

    if (pubItem.getVersion().getObjectId() == null) {
      throw new IllegalArgumentException(getClass()
          + ".withdrawPubItem: pubItem reference does not contain an objectId.");
    }

    if (user == null) {
      throw new IllegalArgumentException(getClass() + ".withdrawPubItem: user is null.");
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
          xmlTransforming.transformToTaskParam(param));

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
      ExceptionHandler.handleException(e, getClass() + ".withdrawPubItem");
    }
  }

}
