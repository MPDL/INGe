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
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 
package de.mpg.escidoc.pubman.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ResourceBundle;

import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;

public class GenreSecificItemManager 
{
	public static final String SUBMISSION_METHOD_ALL = "all";
	public static final String SUBMISSION_METHOD_EASY = "easy-submission";
	public static final String SUBMISSION_METHOD_FULL = "full-submission";
	
	private PubItemVO pubItem;
	private String submissionMethod;
	
	public GenreSecificItemManager()
	{
		
	}
	
	public GenreSecificItemManager(PubItemVO pubItem, String submissionMethod)
	{
		this.pubItem = pubItem;
		this.submissionMethod = submissionMethod;
	}
	
	public PubItemVO cleanupItem() throws Exception
	{
		// First get the Genre of the item
		String genre = "";
		ResourceBundle genreBundle = null;
		ArrayList<String> keyList = new ArrayList<String>();
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		if(this.pubItem != null && this.pubItem.getMetadata() != null && this.pubItem.getMetadata().getGenre() != null)
		{
			genre = this.pubItem.getMetadata().getGenre().name();
			genreBundle = this.getGenreBundle(genre);
			
			String key = "";
			String baseKey = "";
			String fullClassAttribute = "";
			Object javaObject = this.pubItem;
			
			for(Enumeration keys = genreBundle.getKeys(); keys.hasMoreElements();)
			{
				key = keys.nextElement().toString();
				map.put(key, genreBundle.getString(key));
			}
			
			for (String mapKey : map.keySet())
			{
				if(mapKey.endsWith("class_attribute"))
				{
					baseKey = mapKey.replace("class_attribute", "");
					fullClassAttribute = map.get(mapKey);
					
					if(fullClassAttribute.equals("metadata.publishingInfo.place"))
					{
						System.out.println("metadata.publishingInfo.place");
						System.out.println("");
						
					}
					
					// check if the property should be available in this genre or not
					if(map.get(baseKey + "display").equals("false") && (map.get(baseKey + "form_id").equals(this.submissionMethod) || map.get(baseKey + "form_id").equals(GenreSecificItemManager.SUBMISSION_METHOD_ALL)))
					{
						List<Object> obj = this.getMappedObject(javaObject, fullClassAttribute);
						
					}
					
				}
			}
		}
		
		return this.pubItem;
	}
	
	private List<Object> getMappedObject (Object baseObject, String mappingString) throws NoSuchMethodException, Exception
	{
		List<Object> result= new ArrayList<Object>();
		
		// first get all values in the class attribute String and eliminate the "."
		String[] attributes = mappingString.split("\\.");
		if(baseObject != null)
		{
			Object subObject = getObject(baseObject, attributes[0]);
			int index = mappingString.indexOf(".");
			if(index > 0)
			{
				mappingString = mappingString.substring(index +1);
				
				if(subObject instanceof List)
				{
					for(Object subObjectElement : (ArrayList)subObject)
					{
						List subResult = getMappedObject(subObjectElement, mappingString);
						result.addAll(subResult);
					}
				}
				else
				{
					result.addAll(getMappedObject(subObject, mappingString));
				}
			}
			else
			{
				String renamedAttribute = "";
				String firstCharacter = "";
				
				// prepare the string for a method call
				renamedAttribute = mappingString;
				// save the first character
				firstCharacter = renamedAttribute.substring(0, 1);
				// remove the first character
				renamedAttribute = renamedAttribute.substring(1);
				// add the first character in upper case
				renamedAttribute = firstCharacter.toUpperCase() + renamedAttribute;
				// get the desired object  first to examine the type of it
				Object javaObjectToNullify = getObject(baseObject, attributes[0]);
				
				Method method = null;
				
				if(javaObjectToNullify != null)
				{
					if(javaObjectToNullify instanceof List)
					{
						if(((List) javaObjectToNullify).size() > 0)
						{
							method = javaObjectToNullify.getClass().getMethod("clear", new Class[]{});
							method.invoke(javaObjectToNullify, new Object[]{});
						}
					}
					else
					{
						method = baseObject.getClass().getMethod("set" + renamedAttribute, new Class[]{javaObjectToNullify.getClass()});
						method.invoke(baseObject, new Object[]{null});
					}
				}
				//result.add(subObject);
			}
		}
		
		
		
		return result;
	}
	
	private Object getObject(Object object, String mapString) throws Exception, NoSuchMethodException
	{
		Method method = null;
		Object javaObject = null;
		
		String renamedAttribute = "";
		String firstCharacter = "";
		
		// prepare the string for a method call
		renamedAttribute = mapString;
		// save the first character
		firstCharacter = renamedAttribute.substring(0, 1);
		// remove the first character
		renamedAttribute = renamedAttribute.substring(1);
		// add the first character in upper case
		renamedAttribute = firstCharacter.toUpperCase() + renamedAttribute;
		
		method = object.getClass().getMethod("get" + renamedAttribute, new Class[]{});
		javaObject = method.invoke(object, new Object[]{});
		
		return javaObject;
	}
	
	private void setObjectValue(Object object, String mapString, Object value) throws Exception, NoSuchMethodException
	{
		Method method = null;
		
		String renamedAttribute = "";
		String firstCharacter = "";
		
		// prepare the string for a method call
		renamedAttribute = mapString;
		// save the first character
		firstCharacter = renamedAttribute.substring(0, 1);
		// remove the first character
		renamedAttribute = renamedAttribute.substring(1);
		// add the first character in upper case
		renamedAttribute = firstCharacter.toUpperCase() + renamedAttribute;
		
		if(object != null)
		{
			method = object.getClass().getMethod("set" + renamedAttribute, new Class[]{object.getClass()});
			//method.invoke(javaObject, new Object[]{null});
		}
	}
	
	
	
	public ResourceBundle getGenreBundle(String genre)
    {
        return ResourceBundle.getBundle("Genre_" + genre);
    }
}
