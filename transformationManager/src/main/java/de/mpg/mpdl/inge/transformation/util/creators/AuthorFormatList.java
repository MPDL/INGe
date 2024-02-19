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

import java.util.Arrays;

/**
 * Class to define all valid AuthorFormats.
 *
 * @author franke
 * @version $Revision: 4134 $ $LastChangedDate: 2011-09-22 18:21:00 +0200 (Do, 22 Sep 2011) $ by
 *          $Author: mfranke $
 *
 */
public final class AuthorFormatList {

  private static AuthorFormat[] formats = {new WesternFormat1(), new WesternFormat2(), new WesternFormat3(), new WesternFormat4(),
      new WesternFormat5(), new WesternFormat6(), new WesternFormat7(), new WesternFormat8(), new WesternFormat9(), new WesternFormat10(),
      new WesternFormat11(), new WesternFormat12(), new WesternFormat13(),
      // new WesternFormat14(),
      new OxfordJournalFormat(), new ScienceDirectFormat(), new BibTeXSpecialFormat1(),
      // new LooseFormat(),
      // new LooseFormatSurnameFirst(),
      new LooseFormatWithInfoInBraces(), new ResidualFormat(), new EndnoteFormat(), new MpisBibtexFormat(), new MpiKybFormat(),
      new BibTeXMixedSurnameAndGivennameFormat()};

  static {
    Arrays.sort(formats);
  }

  private AuthorFormatList() {}

  public static AuthorFormat[] getFormats() {
    AuthorFormat[] result = new AuthorFormat[formats.length];
    System.arraycopy(formats, 0, result, 0, formats.length);
    return result;
  }

  public static void setFormats(AuthorFormat[] formats) {
    AuthorFormatList.formats = formats;
  }

}
