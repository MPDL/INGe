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

package de.mpg.escidoc.services.common.util;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Useful HTML functionalities.
 *
 * @author Vlad Makarenko (initial creation)
 * @version $Revision$ $LastChangedDate$ by $Author$
 */
public class HtmlUtils
{
	
	private static final Pattern SUBS_OR_SUPS = Pattern.compile(
			"\\<(\\/?su[bp])\\>",			
			Pattern.DOTALL
	);	
	
	/**
	 * Check of the balanced tags sup/sub
	 * @param snippet
	 * @return <code>true</code> if balanced, <code>false</code> otherwise 
	 */
	public static boolean isBalanced(String snippet)
	{
		if (snippet == null)
			return true; 
		
		Stack<String> s = new Stack<String>();
		Matcher m = SUBS_OR_SUPS.matcher(snippet.toLowerCase());
		while (m.find()) 
		{
			String tag = m.group(1);
			if( tag.startsWith("su") )
			{
				s.push(tag);
			}
			else 
			{
				if ( s.empty() || !tag.equals("/" + s.pop()) )
				{
					return false;
				}
			}
		}
		
		return s.empty();
	}   
	
}
