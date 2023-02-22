package de.mpg.mpdl.inge.citationmanager.utils;

import java.util.regex.Pattern;

import de.mpg.mpdl.inge.citationmanager.CitationStyleManagerException;

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

/**
 * Utils class.
 * 
 * @author vmakarenko (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class Utils {
  /**
   * Returns true if val is not null && not empty String
   * 
   * @param val
   * @return first not null && not empty String
   */
  public static boolean checkVal(String val) {
    return (val != null && !val.trim().equals(""));
  }

  /**
   * Throws ExportManagerException true if cond is true
   * 
   * @param cond
   * @param message
   * @throws ExportManagerException
   */
  public static void checkCondition(boolean cond, String message) throws CitationStyleManagerException {
    if (cond)
      throw new CitationStyleManagerException(message);
  }

  public static void checkName(String name, String message) throws CitationStyleManagerException {
    Utils.checkCondition(!checkVal(name), message);
  }

  public static String replaceAllTotal(String what, Pattern p, String replacement) {
    return p.matcher(what).replaceAll(replacement);
  }
}
