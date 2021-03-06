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
package de.mpg.mpdl.inge.pubman.web.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ResourceBundle;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;

public class GenreSpecificItemManager {
  public static final String SUBMISSION_METHOD_EASY = "easy-submission";
  public static final String SUBMISSION_METHOD_FULL = "full-submission";

  private static final String SUBMISSION_METHOD_ALL = "all";

  private ItemVersionVO pubItem;
  private String submissionMethod;

  public GenreSpecificItemManager() {}

  public GenreSpecificItemManager(ItemVersionVO pubItem, String submissionMethod) {
    this.pubItem = pubItem;
    this.submissionMethod = submissionMethod;
  }

  public ItemVersionVO cleanupItem() throws Exception {
    final List<Object> objs = new ArrayList<Object>();
    final LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

    if (this.pubItem != null && this.pubItem.getMetadata() != null && this.pubItem.getMetadata().getGenre() != null) {
      final String genre = this.pubItem.getMetadata().getGenre().name();
      final ResourceBundle genreBundle = ResourceBundle.getBundle("Genre_" + genre);
      final Object javaObject = this.pubItem;

      for (final Enumeration<?> keys = genreBundle.getKeys(); keys.hasMoreElements();) {
        final String key = keys.nextElement().toString();
        map.put(key, genreBundle.getString(key));
      }

      for (final String mapKey : map.keySet()) {
        if (mapKey.endsWith("class_attribute")) {
          final String baseKey = mapKey.replace("class_attribute", "");
          final String fullClassAttribute = map.get(mapKey);
          // check if the property should be available in this genre or not
          if (map.get(baseKey + "display").equals("false") && (map.get(baseKey + "form_id").equals(this.submissionMethod)
              || map.get(baseKey + "form_id").equals(GenreSpecificItemManager.SUBMISSION_METHOD_ALL))) {
            objs.addAll(this.getMappedObject(javaObject, fullClassAttribute));
          }
        }
      }
    }

    return this.pubItem;
  }

  private List<Object> getMappedObject(Object baseObject, String mappingString) throws NoSuchMethodException, Exception {
    final List<Object> result = new ArrayList<Object>();
    // first get all values in the class attribute String and eliminate the "."
    final String[] attributes = mappingString.split("\\.");

    if (baseObject != null) {
      final Object subObject = this.getObject(baseObject, attributes[0]);
      final int index = mappingString.indexOf(".");

      if (index > 0) {
        mappingString = mappingString.substring(index + 1);
        if (subObject instanceof List) {
          for (final Object subObjectElement : (ArrayList<?>) subObject) {
            final List<Object> subResult = this.getMappedObject(subObjectElement, mappingString);
            result.addAll(subResult);
          }
        } else {
          result.addAll(this.getMappedObject(subObject, mappingString));
        }
      } else {
        // prepare the string for a method call
        String renamedAttribute = mappingString;
        // save the first character
        final String firstCharacter = renamedAttribute.substring(0, 1);
        // remove the first character
        renamedAttribute = renamedAttribute.substring(1);
        // add the first character in upper case
        renamedAttribute = firstCharacter.toUpperCase() + renamedAttribute;
        // get the desired object first to examine the type of it
        final Object javaObjectToNullify = this.getObject(baseObject, attributes[0]);
        Method method = null;

        if (javaObjectToNullify != null) {
          if (javaObjectToNullify instanceof List) {
            if (((List<?>) javaObjectToNullify).size() > 0) {
              method = javaObjectToNullify.getClass().getMethod("clear", new Class[] {});
              method.invoke(javaObjectToNullify, new Object[] {});
              result.add(method);
            }
          } else {
            method = baseObject.getClass().getMethod("set" + renamedAttribute, new Class[] {javaObjectToNullify.getClass()});
            method.invoke(baseObject, new Object[] {null});
            result.add(method);
          }
        }
        // result.add(subObject);
      }
    }

    return result;
  }

  private Object getObject(Object object, String mapString) throws Exception, NoSuchMethodException {
    // prepare the string for a method call
    String renamedAttribute = mapString;
    // save the first character
    final String firstCharacter = renamedAttribute.substring(0, 1);
    // remove the first character
    renamedAttribute = renamedAttribute.substring(1);
    // add the first character in upper case
    renamedAttribute = firstCharacter.toUpperCase() + renamedAttribute;
    final Method method = object.getClass().getMethod("get" + renamedAttribute, new Class[] {});
    final Object javaObject = method.invoke(object, new Object[] {});

    return javaObject;
  }

  // private void setObjectValue(Object object, String mapString, Object value)
  // throws Exception, NoSuchMethodException {
  // Method method = null;
  // String renamedAttribute = "";
  // String firstCharacter = "";
  // // prepare the string for a method call
  // renamedAttribute = mapString;
  // // save the first character
  // firstCharacter = renamedAttribute.substring(0, 1);
  // // remove the first character
  // renamedAttribute = renamedAttribute.substring(1);
  // // add the first character in upper case
  // renamedAttribute = firstCharacter.toUpperCase() + renamedAttribute;
  // if (object != null) {
  // method =
  // object.getClass().getMethod("set" + renamedAttribute, new Class[] {object.getClass()});
  // // method.invoke(javaObject, new Object[]{null});
  // }
  // }

  public ResourceBundle getGenreBundle(String genre) {
    return ResourceBundle.getBundle("Genre_" + genre);
  }
}
