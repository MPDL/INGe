/*
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
* Copyright 2006-20110 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/
package de.mpg.escidoc.pubman.test.gui;

import java.io.IOException;

import org.junit.Test;

import de.mpg.escidoc.pubman.test.gui.modules.PubmanGuiModules;
import de.mpg.escidoc.pubman.test.gui.modules.item.PubmanItem;
import de.mpg.escidoc.pubman.test.gui.modules.login.PubmanUser;

/**
 * @author endres
 *
 */
public abstract class PubmanGuiTestcase extends PubmanGuiModules
{
    public PubmanGuiTestcase() throws IOException
    {
        super();
    }

//    /**
//     *  Checks if the various user logins works. Also checks if the links appear for which the user has rights. 
//     */
//    @Test
//    public void testPMTS3LogonProcedure() {
//        for( PubmanUser.UserType userType : PubmanUser.UserType.values() ) {
//            loginPubmanForType( userType );
//            logoutPubman();
//        }
//    }
//    
    @Test
    public void testPMTS1SaveItem() {
        loginPubmanForType( PubmanUser.UserType.DepositorModeratorSimpleStandardWF);
        PubmanItem item = createPubItem(PubmanItem.ItemType.Item);
        doEasySubmission( item );
        logoutPubman();
    }
}
