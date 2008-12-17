/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.pubman;

import java.net.URISyntaxException;

import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.pubman.depositing.DepositingException;
import de.mpg.escidoc.services.pubman.depositing.PubItemLockedException;
import de.mpg.escidoc.services.pubman.depositing.PubItemMandatoryAttributesMissingException;
import de.mpg.escidoc.services.pubman.exceptions.PubCollectionNotFoundException;
import de.mpg.escidoc.services.pubman.exceptions.PubItemAlreadyReleasedException;
import de.mpg.escidoc.services.pubman.exceptions.PubItemNotFoundException;
import de.mpg.escidoc.services.pubman.exceptions.PubItemStatusInvalidException;
import de.mpg.escidoc.services.pubman.exceptions.PubManException;
import de.mpg.escidoc.services.validation.ItemInvalidException;

/**
 * Depositing service interface for Publication Items.
 * @created 31-Jan-2007 17:26:09
 * @author $Author$
 * @version $Revision$ $LastChangedDate$
 * @updated 22-Okt-2007 18:20:48
 */
public interface PubItemDepositing
{
    
    public static final String WORKFLOW_SIMPLE = "simple";
    
    public static final String WORKFLOW_STANDARD = "standard";
    
    /**
     * The service name.
     */
    public static final String SERVICE_NAME = "ejb/de/mpg/escidoc/services/pubman/PubItemDepositing";

    /**
	 * Accepts the given pubItem. A save operation is done before the accept operation:
	 * Afterwards it is released.
	 * 
	 * @param pubItem
	 * @param acceptComment
	 * @param user
	 * @exception TechnicalException,
	 * @exception SecurityException,
	 * @exception DepositingException,
	 * @exception PubItemNotFoundException,
	 * @exception PubManException
     * @throws ItemInvalidException 
	 */
    public PubItemVO acceptPubItem(PubItemVO pubItem, String acceptComment, AccountUserVO user)
	  throws TechnicalException, SecurityException, DepositingException, PubItemNotFoundException, PubManException, ItemInvalidException
    ;

    /**
	 * Creates an new PubItemVO object with the default metadata of the given
	 * Collection.
	 * The PubItem is not made persistent.
	 * 
	 * @param collectionRef
	 * @param user
	 * @exception TechnicalException,
	 * @exception SecurityException,
	 * @exception PubCollectionNotFoundException
	 */
    public PubItemVO createPubItem(ContextRO collectionRef, AccountUserVO user)
	  throws TechnicalException, SecurityException, PubCollectionNotFoundException;

    /**
	 * Deletes the PubItem identified by the given reference.
	 * 
	 * @param itemRef
	 * @param user
	 * @exception TechnicalException TechnicalException
	 * @exception DepositingException ,
	 */
    public void deletePubItem(ItemRO itemRef, AccountUserVO user)
	  throws TechnicalException, SecurityException, PubItemNotFoundException, PubItemLockedException, PubItemStatusInvalidException;

    /**
	 * Returns all open PubCollections for which the given user is in the role "Depositor".
	 * 
	 * @param user
	 * @exception TechnicalException,
	 * @exception SecurityException
	 */
    public java.util.List<ContextVO> getPubCollectionListForDepositing(AccountUserVO user)
	  throws TechnicalException, SecurityException;

    /**
	 * Saves the given pubItem (i.e. creates a new version). If the pubItem already
	 * exists an update is executed, otherwise the item is created.
	 * If the given item is in state released, the item is submitted at the end.
	 * 
	 * @param item
	 * @param user
	 * @exception TechnicalException,
	 * @exception SecurityException,
	 * @exception PubItemMandatoryAttributesMissingException,
	 * @exception PubCollectionNotFoundException,
	 * @exception PubItemLockedException,
	 * @exception PubItemNotFoundException,
	 * @exception PubItemStatusInvalidException
     * @throws PubItemAlreadyReleasedException 
	 */
    public PubItemVO savePubItem(PubItemVO item, AccountUserVO user)
	  throws TechnicalException, SecurityException, PubItemMandatoryAttributesMissingException, PubCollectionNotFoundException, PubItemLockedException, PubItemNotFoundException, PubItemStatusInvalidException, PubItemAlreadyReleasedException, URISyntaxException, AuthorizationException;

    /**
	 * Submits the given pubItem. As on submit, a new version must be created (which
	 * is not done by the framework), a save operation is done before the submit
	 * operation: If the pubItem already exists an update is executed, otherwise the
	 * item is created. Afterwards it is submitted.
	 * 
	 * @param item
	 * @param submissionComment
	 * @param user
	 * @exception TechnicalException,
	 * @exception SecurityException,
	 * @exception DepositingException,
	 * @exception PubItemNotFoundException,
	 * @exception PubManException
     * @throws ItemInvalidException 
	 */
    public PubItemVO submitPubItem(PubItemVO item, String submissionComment, AccountUserVO user)
	  throws TechnicalException, SecurityException, DepositingException, PubItemNotFoundException, PubManException, ItemInvalidException, URISyntaxException, AuthorizationException;

	/**
	 * Creates a new PubItem as a revision of the given one.
	 * Also a content relation of type isRevisionOf is created.
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
	public PubItemVO createRevisionOfItem(PubItemVO pubItem, String relationComment, ContextRO pubCollection, AccountUserVO user) throws SecurityException, PubItemMandatoryAttributesMissingException, PubItemLockedException, PubCollectionNotFoundException, PubItemNotFoundException, PubItemStatusInvalidException, PubItemAlreadyReleasedException, TechnicalException;
	
	
	/**
     * Submits and releases the given pubItem. As on submit, a new version must be created (which
     * is not done by the framework), a save operation is done before the submit
     * operation: If the pubItem already exists an update is executed, otherwise the
     * item is created. Afterwards it is submitted and released.
     * 
     * @param item
     * @param submissionComment
     * @param user
     * @exception TechnicalException,
     * @exception SecurityException,
     * @exception DepositingException,
     * @exception PubItemNotFoundException,
     * @exception PubManException
     * @throws ItemInvalidException 
     */
	 public PubItemVO submitAndReleasePubItem(PubItemVO pubItem, String submissionComment, AccountUserVO user) throws DepositingException, TechnicalException, PubItemNotFoundException, SecurityException, PubManException, ItemInvalidException;
}
