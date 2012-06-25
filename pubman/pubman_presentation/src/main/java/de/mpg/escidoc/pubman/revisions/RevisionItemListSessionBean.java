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
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.escidoc.pubman.revisions;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.itemList.PubItemListSessionBean;

/**
 * This session bean is a plain copy of the PubItemListSessionBean exclusive for Revisions.
 * So states are hold in PubItemListSessionBean as long as no other "normal" item list is loaded
 * 
 * @author Matthias Walter (initial creation)
 * @author $Author: MWalter $ (last modification)
 * @version $Revision: 4777 $ $LastChangedDate: 2012-06-20 09:49:27 +0200 (Mi, 20 Jun 2012) $
 */
public class RevisionItemListSessionBean extends PubItemListSessionBean
{
    private static Logger logger = Logger.getLogger(RevisionItemListSessionBean.class);
    
    public static String BEAN_NAME = "RevisionItemListSessionBean";
    
    public RevisionItemListSessionBean () 
    {
        super();
    }
}
