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

package de.mpg.mpdl.inge.pubman;

import java.net.URISyntaxException;

import javax.ejb.Remote;

import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.model.referenceobjects.ContextRO;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.pubman.exceptions.DepositingException;
import de.mpg.mpdl.inge.pubman.exceptions.PubCollectionNotFoundException;
import de.mpg.mpdl.inge.pubman.exceptions.PubItemAlreadyReleasedException;
import de.mpg.mpdl.inge.pubman.exceptions.PubItemLockedException;
import de.mpg.mpdl.inge.pubman.exceptions.PubItemMandatoryAttributesMissingException;
import de.mpg.mpdl.inge.pubman.exceptions.PubItemNotFoundException;
import de.mpg.mpdl.inge.pubman.exceptions.PubItemStatusInvalidException;
import de.mpg.mpdl.inge.pubman.exceptions.PubManException;

/**
 * Depositing service interface for Publication Items.
 * 
 * @created 31-Jan-2007 17:26:09
 * @author $Author$
 * @version $Revision$ $LastChangedDate$
 * @updated 22-Okt-2007 18:20:48
 */
@Remote
public interface PubItemDepositing {

  public static final String WORKFLOW_SIMPLE = "simple";
  public static final String WORKFLOW_STANDARD = "standard";

  public static final String SERVICE_NAME = "ejb/de/mpg/escidoc/services/pubman/PubItemDepositing";

  // /**
  // * Accepts the given pubItem. A save operation is done before the accept operation: Afterwards
  // it
  // * is released.
  // *
  // * @param pubItem
  // * @param acceptComment
  // * @param user
  // * @exception TechnicalException,
  // * @exception SecurityException,
  // * @exception DepositingException,
  // * @exception PubItemNotFoundException,
  // * @exception PubManException
  // * @throws ValidationException
  // * @throws ItemInvalidException
  // */
  // public PubItemVO acceptPubItem(PubItemVO pubItem, String acceptComment, AccountUserVO user)
  // throws PubItemNotFoundException, SecurityException, TechnicalException;

  /**
   * Creates an new PubItemVO object with the default metadata of the given Collection. The PubItem
   * is not made persistent.
   * 
   * @param collectionRef
   * @param user
   * @throws IllegalArgumentException
   * @exception TechnicalException,
   * @exception SecurityException,
   * @exception PubCollectionNotFoundException
   */
  public PubItemVO createPubItem(ContextRO collectionRef, AccountUserVO user)
      throws PubCollectionNotFoundException, SecurityException, TechnicalException;

  /**
   * Deletes the PubItem identified by the given reference.
   * 
   * @param itemRef
   * @param user
   * @throws SecurityException
   * @throws PubItemStatusInvalidException
   * @throws PubItemNotFoundException
   * @throws PubItemLockedException
   * @exception TechnicalException TechnicalException
   * @exception DepositingException ,
   */
  public void deletePubItem(ItemRO itemRef, AccountUserVO user) throws PubItemLockedException,
      PubItemNotFoundException, PubItemStatusInvalidException, SecurityException,
      TechnicalException;

  /**
   * Returns all open PubCollections for which the given user is in the role "Depositor".
   * 
   * @param user
   * @exception TechnicalException,
   * @exception SecurityException
   */
  public java.util.List<ContextVO> getPubCollectionListForDepositing(AccountUserVO user)
      throws SecurityException, TechnicalException;

  /**
   * Returns all PubContexts.
   * 
   * @param user
   * @exception TechnicalException,
   * @exception SecurityException
   */
  public java.util.List<ContextVO> getPubCollectionListForDepositing() throws SecurityException,
      TechnicalException;

  /**
   * Saves the given pubItem (i.e. creates a new version). If the pubItem already exists an update
   * is executed, otherwise the item is created. If the given item is in state released, the item is
   * submitted at the end.
   * 
   * @param item
   * @param user
   * @throws AuthorizationException
   * @throws URISyntaxException
   * @exception TechnicalException,
   * @exception SecurityException,
   * @exception PubItemMandatoryAttributesMissingException,
   * @exception PubCollectionNotFoundException,
   * @exception PubItemLockedException,
   * @exception PubItemNotFoundException,
   * @exception PubItemStatusInvalidException
   * @throws PubItemAlreadyReleasedException
   * @throws ValidationException
   * @throws ItemInvalidException
   */
  public PubItemVO savePubItem(PubItemVO item, AccountUserVO user)
      throws PubItemMandatoryAttributesMissingException, PubCollectionNotFoundException,
      PubItemLockedException, PubItemNotFoundException, PubItemAlreadyReleasedException,
      PubItemStatusInvalidException, TechnicalException, AuthorizationException;

  /**
   * Submits the given pubItem. As on submit, a new version must be created (which is not done by
   * the framework), a save operation is done before the submit operation: If the pubItem already
   * exists an update is executed, otherwise the item is created. Afterwards it is submitted.
   * 
   * @param item
   * @param submissionComment
   * @param user
   * @throws PubItemStatusInvalidException
   * @exception TechnicalException,
   * @exception SecurityException,
   * @exception DepositingException,
   * @exception PubItemNotFoundException,
   * @exception PubManException
   * @throws ValidationException
   * @throws ItemInvalidException
   */
  public PubItemVO submitPubItem(PubItemVO item, String submissionComment, AccountUserVO user)
      throws PubItemStatusInvalidException, PubItemNotFoundException, SecurityException,
      TechnicalException;

  /**
   * Creates a new PubItem as a revision of the given one. Also a content relation of type
   * isRevisionOf is created.
   * 
   * @param pubItem
   * @param relationComment
   * @param pubCollection
   * @param owner
   * @throws TechnicalException
   * @throws PubItemAlreadyReleasedException
   * @throws PubItemStatusInvalidException
   * @throws PubItemNotFoundException
   * @throws PubCollectionNotFoundException
   * @throws PubItemLockedException
   * @throws PubItemMandatoryAttributesMissingException
   * @throws SecurityException
   */
  public PubItemVO createRevisionOfItem(PubItemVO pubItem, String relationComment,
      ContextRO pubCollection, AccountUserVO user);


  // /**
  // * Submits and releases the given pubItem. As on submit, a new version must be created (which is
  // * not done by the framework), a save operation is done before the submit operation: If the
  // * pubItem already exists an update is executed, otherwise the item is created. Afterwards it is
  // * submitted and released.
  // *
  // * @param item
  // * @param submissionComment
  // * @param user
  // * @throws PubItemStatusInvalidException
  // * @exception TechnicalException,
  // * @exception SecurityException,
  // * @exception DepositingException,
  // * @exception PubItemNotFoundException,
  // * @throws ValidationException
  // * @throws ItemInvalidException
  // * @exception PubManException
  // */
  // public PubItemVO releasePubItem(PubItemVO pubItem, String submissionComment,
  // AccountUserVO user) throws PubItemStatusInvalidException, PubItemNotFoundException,
  // SecurityException, TechnicalException;
}
