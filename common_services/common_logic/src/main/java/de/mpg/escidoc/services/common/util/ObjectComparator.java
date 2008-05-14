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

package de.mpg.escidoc.services.common.util;

import java.lang.reflect.Field;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Compares two objects and creates a list of differences.
 * 
 * @author Miriam Doelle (initial creation)
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $
 * Revised by BrP: 03.09.2007
 */
public class ObjectComparator
{
    
    private static Logger logger = Logger.getLogger(ObjectComparator.class);
    
    private static final MessageFormat DIFFERENT_LIST_SIZE = new MessageFormat("Difference in field {1} ({0}): List size [{2}] [{3}]");
    private static final MessageFormat DIFFERENT_FIELD_VALUE = new MessageFormat("Difference in field {1} ({0}): [{2}] [{3}]");
    private static final MessageFormat DIFFERENT_FIELD_VALUE_IN_LIST = new MessageFormat("Difference in list element of field {1} ({0}) at position {4}: [{2}] [{3}]");
    private static final MessageFormat FIRST_VALUE_NULL = new MessageFormat("First object is null, second object is {0}.");
    private static final MessageFormat SECOND_VALUE_NULL = new MessageFormat("First object is {0}, second object is null.");

    private List<String> diffs = new ArrayList<String>();
    private List<String> fieldnames = new ArrayList<String>();
    
    private Set<Object> compared = new HashSet<Object>();

    /**
     * Creates a new ObjectComparator instance that compares the two given objects. Note: Compare also works with null
     * values.
     * 
     * @param o1 The first object to compare.
     * @param o2 The second object to compare.
     * @throws IllegalAccessException 
     */
    public ObjectComparator(Object o1, Object o2) throws IllegalAccessException
    {
        if (o1 == null)
        {
            if (o2 != null)
            {
                diffs.add(FIRST_VALUE_NULL.format(new Object[] { o2.toString() }));
            }
        }
        else if (o2 == null)
        {
            diffs.add(SECOND_VALUE_NULL.format(new Object[] { o1.toString() }));
        }
        else
        {
            compareObjects(o1, o2, "", "root");
        }
    }

    /**
     * Checks whether the two objects are equal.
     * 
     * @return true if the two objects are equal otherwise false.
     */
    public boolean isEqual()
    {
        if (diffs.size() == 0)
        {
            return true;
        }
        return false;
    }

    /**
     * Gets the list of differences between the two objects.
     * 
     * @return the list of differences. If no differences were detected an empty list is returned.
     */
    public List<String> getDiffs()
    {
        return diffs;
    }

    /**
     * Returns a String with one line for each difference.
     * 
     * @return the string represtation of the differences between the compared objects.
     */
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        for (String string : diffs)
        {
            result.append(string).append("\n");
        }
        return result.toString();
    }

    /**
     * Compares two objects dealing also with null values.
     * 
     * @param o1 The first object to compare.
     * @param o2 The second object to compare.
     * @return true if the two objects are equal otherwise false.
     */
    protected boolean equals(Object obj1, Object obj2)
    {
        if (obj1 != null)
        {
            if (!obj1.equals(obj2))
            {
                return false;
            }
        }
        else if (obj2 != null)
        {
            return false;
        }
        return true;
    }

    private boolean isSimpleComparableType(Object object)
    {
        if (object instanceof String || 
            object instanceof Integer || 
            object instanceof Long || 
            object instanceof Double || 
            object instanceof Byte || 
            object instanceof Boolean || 
            object instanceof Short || 
            object instanceof Float || 
            object instanceof Date || 
            object instanceof Enum || 
            object instanceof URL)
        {
            return true;
        }
        return false;
    }

    private void compareObjects(Object fieldValue1, Object fieldValue2, String fieldname, String enclosingClass) throws IllegalAccessException
    {
        
        logger.debug("Comparing: " + fieldValue1 + " and " + fieldValue2 + " (Field: " + fieldname + ")");
        
        if (fieldValue1 == fieldValue2)
        {
            return;
        }
        else if (compared.contains(fieldValue1))
        {
            return;
        }
        //compared.add(fieldValue1);
        
        fieldnames.add(fieldname);
        try
        {
            // check for null
            if (fieldValue1 == null && fieldValue2 == null)
            {
                return;
            }
            if (fieldValue1 == null || fieldValue2 == null)
            {
                diffs.add(DIFFERENT_FIELD_VALUE.format(new Object[] { enclosingClass, getFieldNames(), fieldValue1, fieldValue2 }));
                return;
            }

            // check if objects are of same type
            if (!fieldValue1.getClass().isAssignableFrom(fieldValue2.getClass()))
            {
                throw new IllegalArgumentException("Object o2 is not of same type or a subclass of type of o1");
            }

            // check if objects can be compared by simple equals call
            if (isSimpleComparableType(fieldValue1))
            {
                if (!equals(fieldValue1, fieldValue2))
                {
                    diffs.add(DIFFERENT_FIELD_VALUE.format(new Object[] { enclosingClass, getFieldNames(), fieldValue1, fieldValue2 }));
                }
            }
            else if (fieldValue1 instanceof List)
            {
                // Check type of list
                List list1 = (List)fieldValue1;
                List list2 = (List)fieldValue2;
                if (list1.size() != list2.size())
                {
                    diffs.add(DIFFERENT_LIST_SIZE.format(new Object[]{enclosingClass,getFieldNames(),list1.size(),list2.size()}));
                    return;
                }
                if (list1.size() > 0)
                {
                    Object listObject = list1.get(0);
                    boolean isSimpleTypeList = isSimpleComparableType(listObject);
                    for (int i = 0; i < list1.size(); i++)
                    {
                        Object listObject1 = list1.get(i);
                        Object listObject2 = list2.get(i);
                        if (isSimpleTypeList)
                        {
                            if (!equals(listObject1, listObject2))
                            {
                                diffs.add(DIFFERENT_FIELD_VALUE_IN_LIST.format(new Object[] { enclosingClass, getFieldNames(), listObject1, listObject2, i }));
                            }
                        }
                        else
                        {
                            compareObjects(listObject1, listObject2, "[" + i + "]", enclosingClass);
                        }
                    }
                }
            }
            else if (fieldValue1.getClass().isArray())
            {
                // Check type of list
                Object[] list1 = (Object[])fieldValue1;
                Object[] list2 = (Object[])fieldValue2;
                if (list1.length != list2.length)
                {
                    diffs.add(DIFFERENT_LIST_SIZE.format(new Object[] { enclosingClass, getFieldNames(), list1.length, list2.length }));
                    return;
                }
                if (list1.length > 0)
                {
                    for (int i = 0; i < list1.length; i++)
                    {
                        Object listObject1 = list1[i];
                        Object listObject2 = list2[i];
                        if (isSimpleComparableType(listObject1))
                        {
                            if (!equals(listObject1, listObject2))
                            {
                                diffs.add(DIFFERENT_FIELD_VALUE_IN_LIST.format(new Object[] { enclosingClass, getFieldNames(), listObject1, listObject2, i }));
                            }
                        }
                        else
                        {
                            compareObjects(listObject1, listObject2, "[" + i + "]", enclosingClass);
                        }
                    }
                }
            }
            else
            {
                checkAllFieldsForClass(fieldValue1.getClass(), fieldValue1, fieldValue2);
            }
        }
        finally
        {
            fieldnames.remove(fieldname);
        }
    }

    private void checkAllFieldsForClass(Class theClass, Object o1, Object o2) throws IllegalAccessException
    {
        for (Field field : theClass.getDeclaredFields())
        {
            
            field.setAccessible(true);
            Object fieldValue1 = field.get(o1);
            Object fieldValue2 = field.get(o2);
            compareObjects(fieldValue1, fieldValue2, field.getName(), field.getDeclaringClass().toString());
        }
        if (theClass.getSuperclass() != null)
        {
            checkAllFieldsForClass(theClass.getSuperclass(), o1, o2);
        }
    }

    private String getFieldNames()
    {
        StringBuffer s = new StringBuffer();
        for (Iterator iter = fieldnames.iterator(); iter.hasNext();)
        {
            String element = (String)iter.next();
            if (element.length() > 0)
            {
                if (s.length() > 0 && (!element.startsWith("[")))
                {
                    s.append(".");
                }
                s.append(element);
            }
        }
        return s.toString();
    }
}
