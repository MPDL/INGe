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

import de.fiz.escidoc.common.exceptions.application.security.SecurityException;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.pubman.depositing.PubItemLockedException;
import de.mpg.escidoc.services.pubman.exceptions.PubItemNotFoundException;
import de.mpg.escidoc.services.pubman.exceptions.PubItemStatusInvalidException;
import de.mpg.escidoc.services.pubman.publishing.MissingWithdrawalCommentException;

/**
 * Publishing service interface for Publication Items.
 * 
 * @author $Author: jmueller $
 * @version $Revision: 422 $ $LastChangedDate: 2007-11-07 12:15:06 +0100 (Wed, 07 Nov 2007) $
 * @created 14-Feb-2007 13:57:03
 * Revised by StG: 24.08.2007
 */
public interface PubItemPublishing {

    /**
     * The service name.
     */
	public static final String SERVICE_NAME = "ejb/de/mpg/escidoc/services/pubman/PubItemPublishing";

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
	public void releasePubItem(ItemRO pubItemRef, java.util.Date lastModificationDate, String releaseComment, AccountUserVO user) throws TechnicalException, PubItemStatusInvalidException, PubItemNotFoundException, PubItemLockedException, SecurityException;

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
	public void withdrawPubItem(PubItemVO pubItem, java.util.Date lastModificationDate, String withdrawalComment, AccountUserVO user) throws TechnicalException, PubItemNotFoundException, PubItemStatusInvalidException, SecurityException, PubItemLockedException, MissingWithdrawalCommentException;

}