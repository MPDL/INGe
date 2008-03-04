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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/
package de.mpg.escidoc.pubman.search.ui;

import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO.CreatorRole;

/**
 * Checkbox and label element for person types.
 * @author endres
 *
 */
public class PersonCheckBoxLabelUI extends CheckBoxLabelUI {
	
	/** creator role */
	private CreatorRole myCreatorRole;
	
	/**
	 * Creates a new instance.
	 * @param role creator role
	 * @param langBundleIdent language bundle identifier
	 * @param bundle language bundle
	 */
	public PersonCheckBoxLabelUI( CreatorRole role, String langBundleIdent)
	{
		super( role.toString(), langBundleIdent );
		this.myCreatorRole = role;
	}
	
	/**
	 * Get creator role.
	 * @return creator role
	 */
	public CreatorRole getCreatorRole() 
	{
		return this.myCreatorRole;
	}
}
