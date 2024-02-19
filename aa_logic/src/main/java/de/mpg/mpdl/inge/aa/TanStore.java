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

package de.mpg.mpdl.inge.aa;

import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class TanStore {
  private static final Set<String> tanSet = new HashSet<>();

  private TanStore() {}

  public static boolean checkTan(String tan) {
    if (tanSet.contains(tan)) {
      // TAN found, now remove it
      tanSet.remove(tan);
      return true;
    } else {
      // TAN not found
      return false;
    }
  }

  public static String getNewTan() {
    String tan;
    do {
      tan = TanStore.createTan();
    } while (!TanStore.storeTan(tan));

    return tan;
  }

  private static boolean storeTan(String tan) {
    if (tanSet.contains(tan)) {
      // TAN already stored
      return false;
    } else {
      // Add new TAN to store
      tanSet.add(tan);
      return true;
    }
  }

  private static String createTan() {
    Random random = new Random(new Date().getTime());
    byte[] tanBytes = new byte[16];
    random.nextBytes(tanBytes);

    return new String(Base64.encodeBase64(tanBytes));
  }
}
