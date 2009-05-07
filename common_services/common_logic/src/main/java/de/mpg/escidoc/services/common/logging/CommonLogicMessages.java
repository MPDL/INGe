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

package de.mpg.escidoc.services.common.logging;

import java.text.MessageFormat;

/**
 * Messages used in common logic and other other common messages.
 *
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @revised by MuJ: 03.09.2007
 */
public enum CommonLogicMessages implements Messages
{

    /////////////////////////////////////////////
    // MESSAGE DEFINITIONS
    
    //DEBUG AND INFO MESSAGES
    METHOD_START("Method started: {0}"),
    METHOD_FINISHED("Method finished: {0}"),
    METHOD_DURATION("Method {0} took {1,number} milliseconds."),
    
    //ERROR MESSAGES
    FRAMEWORK_SYSTEM_ERROR("Failed to use service {0} correctly."),
    XML_RELATED_ERROR("\n\n\n####### 1/3 Method ######\n{0}\n"+
                            "####### 2/3 Exception ######\n{1}\n"+
                            "####### 3/3 XML ######\n{2}\n\n\n");
    
    /**
     * The message pattern. For syntax definition see {@link MessageFormat}.
     */
    private String message;

    /**
     * Creates a new instance with the given message template.
     * 
     * @param messageTemplate The message template
     */
    CommonLogicMessages(String messageTemplate)
    {
        this.message = messageTemplate;
    }

    /**
     * Delivers the message.
     * 
     * @return The message
     */
    public String getMessage()
    {
        return message;
    }
}
