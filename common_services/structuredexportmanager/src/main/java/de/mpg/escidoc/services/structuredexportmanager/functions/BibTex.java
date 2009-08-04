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
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/


package de.mpg.escidoc.services.structuredexportmanager.functions;

import java.util.HashMap;
import java.util.Map;

/**
 * Function extensions for the BibTex export functionality.
 * To be used from the XSLT.   
 * Converts PubMan item-list to one of the structured formats.   
 *
 * @author Vlad Makarenko (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */ 

public class BibTex {

	/* 
	 * UNICODE -> BibTex mapping
	 * not really comprehensive
	 *  
	 * */
    public static final Map<String, String>  ENTITIES =     
    	new HashMap<String, String>()   
    	{  
	    	{
	    	      
			}
    	};	
    	
	/**
	 * Escapes UNICODE string with the BibTex entities
	 * @param s 
	 * @return escaped String
	 */
	public static String texString(String str)
	{
		if ( str==null || "".equals(str.trim()) ) return null;
		for( Map.Entry<String, String> entry: ENTITIES.entrySet() )
			str = str.replace(entry.getKey(), entry.getValue());
		return str;
	}
	
}


