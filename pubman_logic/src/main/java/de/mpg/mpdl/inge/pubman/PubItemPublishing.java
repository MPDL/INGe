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

import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.pubman.depositing.PubItemLockedException;
import de.mpg.mpdl.inge.pubman.exceptions.PubItemNotFoundException;
import de.mpg.mpdl.inge.pubman.exceptions.PubItemStatusInvalidException;
import de.mpg.mpdl.inge.pubman.publishing.MissingWithdrawalCommentException;

/**
 * Publishing service interface for Publication Items.
 * 
 * @author $Author$
 * @version $Revision$ $LastChangedDate$
 * @created 14-Feb-2007 13:57:03 Revised by StG: 24.08.2007
 */
public interface PubItemPublishing {

  /**
   * The service name.
   */
  public static final String SERVICE_NAME =
      "java:global/de/mpg/escidoc/services/pubman/PubItemPublishing";

  /**
   * Releases the publication item identified by the given pubItemRef.
   * 
   * @param pubItemRef The reference of the publication item.
   * @param lastModificationDate The date of last modification.
   * @param user The user (Necessary for authentication, authorization and logging)
   * @throws TechnicalException
   * @throws PubItemStatusInvalidException
   * @throws PubItemNotFoundException
   * @throws PubItemLockedException
   * @throws SecurityException
   */
  public void releasePubItem(ItemRO pubItemRef, java.util.Date lastModificationDate,
      String releaseComment, AccountUserVO user) throws TechnicalException,
      PubItemStatusInvalidException, PubItemNotFoundException, PubItemLockedException,
      SecurityException;

  /**
   * Withdraws the publication item identified by the given pubItemRef.
   * 
   * @param pubItemRef The reference of the publication item.
   * @param lastModificationDate The date of last modification.
   * @param withdrawalComment The reason for the withdrawal. Must not be null or empty.
   * @param user The user (Necessary for authentication, authorization and logging)
   * @throws TechnicalException
   * @throws PubItemStatusInvalidException
   * @throws PubItemNotFoundException
   * @throws SecurityException
   * @throws PubItemLockedException
   * @throws MissingWithdrawalCommentException if the given withdrawal comment is null or empty.
   */
  public void withdrawPubItem(PubItemVO pubItem, java.util.Date lastModificationDate,
      String withdrawalComment, AccountUserVO user) throws TechnicalException,
      PubItemNotFoundException, PubItemStatusInvalidException, SecurityException,
      PubItemLockedException, MissingWithdrawalCommentException;

}
