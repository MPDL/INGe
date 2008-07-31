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

package de.mpg.escidoc.services.pubman.logging;

import java.text.MessageFormat;

import de.mpg.escidoc.services.common.logging.Messages;

/**
 * Messages used in pubman logic.
 *
 * @author Miriam Doelle
 * @version $Revision: 422 $ $LastChangedDate: 2007-11-07 12:15:06 +0100 (Wed, 07 Nov 2007) $
 * @Revised by BrP: 20.09.2007
 */
public enum PMLogicMessages implements Messages
{
    //DEBUG AND INFO MESSAGES
    PUBITEM_CREATED("The PubItem with ID {0} has been created by user {1}."),
    PUBITEM_UPDATED("The PubItem with ID {0} has been updated by user {1}."),
    PUBITEM_DELETED("The PubItem with ID {0} has been deleted by user {1}."),
    PUBITEM_SUBMITTED("The PubItem with ID {0} has been submitted by user {1}."),
    PUBITEM_RELEASED("The PubItem with ID {0} has been released."),
    PUBITEM_WITHDRAWN("The PubItem with ID {0} has been withdrawn by user {1}."),
    PUBITEM_REVISED("The PubItem with ID {0} has been revised by user {1}."),

    //ERROR AND WARN MESSAGES  
    SEARCH_TOO_MANY_RESULT_MESSAGES("The search result for query {0} delivered a record with more than one message.");
 
    /**
     * The message pattern. For syntax definition see {@link MessageFormat}
     */
    private String message;

    /**
     * Creates a new instance with the given messageTemplate.
     * @param messageTemplate The message pattern.
     */
    private PMLogicMessages(String messageTemplate)
    {
        this.message = messageTemplate;
    }

    /**
     * Get the messageTemplate.
     * @return the messageTemplate
     */
    public String getMessage()
    {
        return message;
    }
}
