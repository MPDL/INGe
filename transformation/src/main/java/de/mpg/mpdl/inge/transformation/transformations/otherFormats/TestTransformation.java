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

package de.mpg.mpdl.inge.transformation.transformations.otherFormats;

import java.io.StringWriter;

import de.mpg.mpdl.inge.transformation.Transformation;
import de.mpg.mpdl.inge.transformation.Transformation.TransformationModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.mpdl.inge.transformation.valueObjects.Format;

/**
 * TODO Description
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@TransformationModule
public class TestTransformation implements Transformation {
  public static final Format SOURCE_FORMAT = new Format("test-src", "text/plain", "UTF-8");
  public static final Format TARGET_FORMAT = new Format("test-trg", "text/plain", "UTF-8");

  @Override
  public Format[] getSourceFormats() throws RuntimeException {
    return new Format[] {SOURCE_FORMAT};
  }

  @Override
  public Format[] getSourceFormats(Format trg) throws RuntimeException {
    if (TARGET_FORMAT.equals(trg)) {
      return new Format[] {SOURCE_FORMAT};
    } else {
      return new Format[] {};
    }
  }

  @Override
  public Format[] getTargetFormats(Format src) throws RuntimeException {
    if (SOURCE_FORMAT.equals(src)) {
      return new Format[] {TARGET_FORMAT};
    } else {
      return new Format[] {};
    }
  }

  @Override
  public byte[] transform(byte[] src, String srcFormatName, String srcType, String srcEncoding,
      String trgFormatName, String trgType, String trgEncoding, String service)
      throws TransformationNotSupportedException, RuntimeException {
    try {
      String source = new String(src, "UTF-8");
      StringWriter writer = new StringWriter();
      for (int i = src.length - 1; i >= 0; i--) {
        writer.append(source.charAt(i));
      }
      return writer.toString().getBytes("UTF-8");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service)
      throws TransformationNotSupportedException, RuntimeException {
    return transform(src, srcFormat.getName(), srcFormat.getType(), srcFormat.getEncoding(),
        trgFormat.getName(), trgFormat.getType(), trgFormat.getEncoding(), service);
  }
}
