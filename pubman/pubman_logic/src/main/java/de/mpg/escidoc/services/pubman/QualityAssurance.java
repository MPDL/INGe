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
* Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 
package de.mpg.escidoc.services.pubman;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.xml.rpc.ServiceException;


import de.fiz.escidoc.common.exceptions.application.invalid.InvalidXmlException;
import de.fiz.escidoc.common.exceptions.application.missing.MissingMethodParameterException;
import de.fiz.escidoc.common.exceptions.application.notfound.ContextNotFoundException;
import de.fiz.escidoc.common.exceptions.application.security.AuthenticationException;
import de.fiz.escidoc.common.exceptions.application.security.AuthorizationException;
import de.fiz.escidoc.common.exceptions.application.security.SecurityException;
import de.fiz.escidoc.common.exceptions.system.SystemException;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.pubman.exceptions.PubItemNotFoundException;
import de.mpg.escidoc.services.pubman.exceptions.PubItemStatusInvalidException;
import de.mpg.escidoc.services.pubman.searching.ParseException;


/**
 * Interface for Quality Assurance related functionalities
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */

public interface QualityAssurance
{
    
    /**
     * The name of the EJB service.
     */
    public static String SERVICE_NAME = "ejb/de/mpg/escidoc/services/common/QualityAssurance";

    
    /**
     * Searches for publication items that matches the given context object id, the given organizational unit id and the given item state
     * 
     * @param searchString The search query.
     * @param searchInFiles If true, search is also executed in files.
     * @param greaterDate items which are greater than this date matches 
     * @return The list of PubItemResultVOs that matched the query.
     * @throws ParseException 
     * @throws TechnicalException 
     */
    public List<PubItemVO> searchForQAWorkspace(String contextobjId, String state, AccountUserVO user) throws ParseException, TechnicalException, ServiceException, MissingMethodParameterException, ContextNotFoundException, InvalidXmlException, AuthenticationException, AuthorizationException, SystemException, RemoteException;
    
    
    /**
     * retrieves all Contexts for which the given user is Moderator
     * @param user
     * @return
     * @throws SecurityException
     * @throws TechnicalException
     */
    public List<ContextVO> retrievePubContextsForModerator(AccountUserVO user) throws SecurityException, TechnicalException;
    
    /**
     * Revises a PubItem in the state submitted to state "in revision". 
     * @param user
     * @return
     * @throws SecurityException
     * @throws TechnicalException
     */
    public PubItemVO revisePubItem(ItemRO pubItemRef, String reviseComment, AccountUserVO user) throws ServiceException, TechnicalException, PubItemStatusInvalidException, SecurityException, PubItemNotFoundException;
}
