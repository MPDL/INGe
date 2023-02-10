/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.model.valueobjects;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * The super class of all value objects.
 * 
 * @revised by MuJ: 28.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 05-Sep-2007 10:30:54
 */
@SuppressWarnings("serial")
public abstract class ValueObject implements Serializable {

  private static Logger logger = Logger.getLogger(ValueObject.class);

  /*
   * @see java.lang.Object#equals(java.lang.Object)
   */
  protected boolean equals(Object obj1, Object obj2) {
    // added by DiT, 19.11.2007: replace windows-line breaks
    if (obj1 instanceof String) {
      obj1 = ((String) obj1).replace("\r", "");
    }
    if (obj2 instanceof String) {
      obj2 = ((String) obj2).replace("\r", "");
    }

    if (obj1 != null) {
      if (!obj1.equals(obj2)) {
        return false;
      }
    } else if (obj2 != null) {
      return false;
    }
    return true;
  }



  public boolean cleanup() throws Exception {
    return this.isEmpty(true);
  }

  public boolean isEmpty(boolean cleanup) throws Exception {
    return isEmpty(this, cleanup, null);
  }



  public static List<Field> getAllFields(Class<?> type) {
    List<Field> fields = new ArrayList<Field>();
    for (Class<?> c = type; c != null; c = c.getSuperclass()) {
      fields.addAll(Arrays.asList(c.getDeclaredFields()));
    }
    return fields;
  }


  public static boolean isEmpty(Object obj, boolean cleanup, Field fromField) throws Exception {
    boolean empty = true;


    if (obj != null) {
      if (String.class.isAssignableFrom(obj.getClass())) {
        empty = ((String) obj).trim().isEmpty();

      } else if (Collection.class.isAssignableFrom(obj.getClass())) {
        Collection<?> coll = (Collection<?>) obj;

        if (coll != null) {

          Collection<Object> toBeRemoved = new ArrayList<Object>();

          for (Object collObj : coll) {

            boolean subObjectIsEmpty = isEmpty(collObj, cleanup, null);
            if (subObjectIsEmpty && cleanup) {
              toBeRemoved.add(collObj);
            }

            empty = empty && subObjectIsEmpty;
          }

          if (cleanup && toBeRemoved.size() > 0) {
            logger.debug("Cleaning up collection " + fromField.getDeclaringClass().getCanonicalName() + " / " + fromField.getName());
            coll.removeAll(toBeRemoved);
          }
        }
      } else if (ValueObject.class.isAssignableFrom(obj.getClass())) {
        for (Field f : getAllFields(obj.getClass())) {
          if ((!Modifier.isStatic(f.getModifiers())) && f.getAnnotation(IgnoreForCleanup.class) == null) {

            f.setAccessible(true);
            Object fieldObject = f.get(obj);
            boolean fieldIsEmpty = isEmpty(fieldObject, cleanup, f);
            if (cleanup && fieldIsEmpty && fieldObject != null && !Collection.class.isAssignableFrom(fieldObject.getClass())) {
              logger.debug("Cleaning up object" + obj.getClass().getCanonicalName() + " / " + f.getName());
              f.set(obj, null);
            }

            empty = empty && fieldIsEmpty;
          }

        }

      } else if (Object.class.isAssignableFrom(obj.getClass())) {
        empty = obj == null;

      }
    }



    /*
     * if(obj!=null) {
     * 
     * Class<?> c = obj.getClass();
     * 
     * for(Field f : getAllFields(c)) { if((!Modifier.isStatic(f.getModifiers())) &&
     * f.getAnnotation(IgnoreForCleanup.class)==null) {
     * 
     * f.setAccessible(true);
     * 
     * boolean fieldIsEmpty = true;
     * 
     * 
     * if(f.getType().equals(String.class)) { fieldIsEmpty = f.get(obj)==null ||
     * ((String)f.get(obj)).isEmpty();
     * 
     * } else if (ValueObject.class.isAssignableFrom(f.getType())) { fieldIsEmpty = f.get(obj)==null
     * || ((ValueObject)f.get(obj)).isEmpty(cleanup); } else if
     * (Collection.class.isAssignableFrom(f.getType())) { Collection<?> coll =
     * (Collection<?>)f.get(obj);
     * 
     * if(coll!=null) {
     * 
     * Collection<Object> toBeRemoved = new ArrayList<Object>();
     * 
     * for(Object collObj : coll) {
     * 
     * boolean subObjectIsEmpty = isEmpty(collObj, cleanup); if(subObjectIsEmpty && cleanup) {
     * toBeRemoved.add(collObj); }
     * 
     * fieldIsEmpty = fieldIsEmpty && subObjectIsEmpty; }
     * 
     * if(cleanup && toBeRemoved.size()>0) { logger.info("Cleaning up collection " +
     * obj.getClass().getCanonicalName() + " / " + f.getName() ); coll.removeAll(toBeRemoved); } } }
     * else if (Object.class.isAssignableFrom(f.getType())) { fieldIsEmpty = f.get(obj)==null; }
     * 
     * if(fieldIsEmpty && cleanup && f.get(obj)!=null) { logger.info("Cleaning up object " +
     * obj.getClass().getCanonicalName() + " / " + f.getName() ); f.set(obj, null); }
     * 
     * empty = empty && fieldIsEmpty; }
     * 
     * } }
     */
    return empty;
  }

}
