/*
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

package de.mpg.mpdl.inge.transformation.util.creators;

import java.util.HashMap;
import java.util.Map;

/**
 * Bean object to hold an author's data.
 *
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 4134 $ $LastChangedDate: 2011-09-22 18:21:00 +0200 (Do, 22 Sep 2011) $
 */
public class Author {

  private String surname = null;
  private String givenName = null;
  private String initial = null;
  private String title = null;
  private String prefix = null;
  private AuthorFormat format = null;

  private final Map<String, String> tags = new HashMap<>();

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public String getGivenName() {
    return givenName;
  }

  /**
   * Setter for givenName. Also sets the initials.
   *
   * @param givenName The given name string.
   */
  public void setGivenName(String givenName) {
    this.givenName = givenName;
    if (!givenName.isEmpty()) {
      if (givenName.contains("-")) {
        String[] names = givenName.split("-");
        StringBuilder init = new StringBuilder();
        for (String name : names) {
          init.append(name.charAt(0)).append(".-");
        }
        this.initial = init.substring(0, init.length() - 1);
      } else if (givenName.contains(" ")) {
        String[] names = givenName.split(" |\\.");
        StringBuilder init = new StringBuilder();
        for (String name : names) {
          if (!"".equals(name)) {
            init.append(name.charAt(0)).append(". ");
          }
        }
        this.initial = init.toString().trim();
      } else {
        this.initial = givenName.charAt(0) + ".";
      }

    }

  }

  public String getInitial() {
    return initial;
  }

  public void setInitial(String initial) {
    this.initial = initial;
  }

  public String toString() {
    return "[Author: givenName=" + givenName + ", initial=" + initial + ", sn=" + surname + "(" + format.getName() + ")]";
  }

  public boolean equals(Object obj) {
    if (obj instanceof Author other) {

      // givenName
      if (this.givenName == null) {
        if (other.givenName != null) {
          return false;
        }
      } else {
        if (!this.givenName.equals(other.givenName)) {
          return false;
        }
      }

      // surname
      if (this.surname == null) {
        if (other.surname != null) {
          return false;
        }
      } else {
        if (!this.surname.equals(other.surname)) {
          return false;
        }
      }

      // initial
      if (this.initial == null) {
        if (other.initial != null) {
          return false;
        }
      } else {
        if (!this.initial.equals(other.initial)) {
          return false;
        }
      }

      return true;

    } else {
      return false;
    }
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix.trim();
  }

  public AuthorFormat getFormat() {
    return format;
  }

  public void setFormat(AuthorFormat format) {
    this.format = format;
  }

  public void addTag(final String key, final String value) {
    tags.put(key, value);
  }

  public Map<String, String> getTags() {
    return tags;
  }

}
