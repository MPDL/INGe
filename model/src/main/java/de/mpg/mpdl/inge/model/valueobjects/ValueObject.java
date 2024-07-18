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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.interfaces.IgnoreForCleanup;

/**
 * The super class of all value objects.
 *
 * @revised by MuJ: 28.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 05-Sep-2007 10:30:54
 */
@SuppressWarnings("serial")
public abstract class ValueObject implements Serializable {

  private static final Logger logger = LogManager.getLogger(ValueObject.class);

  protected boolean equals(Object obj1, Object obj2) {
    // added by DiT, 19.11.2007: replace windows-line breaks
    if (obj1 instanceof String) {
      obj1 = ((String) obj1).replace("\r", "");
    }
    if (obj2 instanceof String) {
      obj2 = ((String) obj2).replace("\r", "");
    }

    if (null != obj1) {
      if (!obj1.equals(obj2)) {
        return false;
      }
    } else if (null != obj2) {
      return false;
    }
    return true;
  }

  public void cleanup() throws Exception {
    this.isEmpty(true);
  }

  public boolean isEmpty(boolean cleanup) throws Exception {
    return isEmpty(this, cleanup, null);
  }

  public static List<Field> getAllFields(Class<?> type) {
    List<Field> fields = new ArrayList<>();
    for (Class<?> c = type; null != c; c = c.getSuperclass()) {
      fields.addAll(Arrays.asList(c.getDeclaredFields()));
    }
    return fields;
  }

//  Diese ist eine zentrale Methode, die überprüft, ob ein gegebenes Objekt "leer" ist. Die Definition von "leer" variiert je nach Typ des Objekts:
//  Für String-Objekte bedeutet "leer", dass der String nach dem Trimmen keine Zeichen enthält.
//  Für Collection-Objekte bedeutet "leer", dass alle Elemente der Sammlung "leer" sind. Wenn cleanup wahr ist, werden "leere" Elemente aus der Sammlung entfernt.
//  Für Objekte, die als Wertobjekte betrachtet werden (durch eine nicht dargestellte ValueObject-Klasse), überprüft die Methode jedes Feld des Objekts auf "Leere". Wenn cleanup wahr ist, werden Felder, die "leer" sind und keine Sammlungen sind, auf null gesetzt.
//  Für alle anderen Objekttypen gilt ein Objekt als "leer", wenn es null ist.
  public static boolean isEmpty(Object obj, boolean cleanup, Field fromField) throws Exception {
    boolean empty = true;

    if (null != obj) {
      if (String.class.isAssignableFrom(obj.getClass())) {
        empty = ((String) obj).trim().isEmpty();

      } else if (Collection.class.isAssignableFrom(obj.getClass())) {
        Collection<?> coll = (Collection<?>) obj;

        if (null != coll) {

          Collection<Object> toBeRemoved = new ArrayList<>();

          for (Object collObj : coll) {

            boolean subObjectIsEmpty = isEmpty(collObj, cleanup, null);
            if (subObjectIsEmpty && cleanup) {
              toBeRemoved.add(collObj);
            }

            empty = empty && subObjectIsEmpty;
          }

          if (cleanup && !toBeRemoved.isEmpty()) {
            logger.debug("Cleaning up collection " + fromField.getDeclaringClass().getCanonicalName() + " / " + fromField.getName());
            coll.removeAll(toBeRemoved);
          }
        }
      } else if (ValueObject.class.isAssignableFrom(obj.getClass())) {
        for (Field f : getAllFields(obj.getClass())) {
          if ((!Modifier.isStatic(f.getModifiers())) && null == f.getAnnotation(IgnoreForCleanup.class)) {

            f.setAccessible(true);
            Object fieldObject = f.get(obj);
            boolean fieldIsEmpty = isEmpty(fieldObject, cleanup, f);
            if (cleanup && fieldIsEmpty && null != fieldObject && !Collection.class.isAssignableFrom(fieldObject.getClass())) {
              logger.debug("Cleaning up object" + obj.getClass().getCanonicalName() + " / " + f.getName());
              f.set(obj, null);
            }

            empty = empty && fieldIsEmpty;
          }

        }

      } else if (Object.class.isAssignableFrom(obj.getClass())) {
        empty = null == obj;

      }
    }
    return empty;
  }
}
