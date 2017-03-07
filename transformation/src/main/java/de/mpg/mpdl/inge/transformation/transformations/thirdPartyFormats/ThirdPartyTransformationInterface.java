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

package de.mpg.mpdl.inge.transformation.transformations.thirdPartyFormats;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.mpg.escidoc.metadataprofile.schema.x01.transformation.TransformationType;
import de.mpg.escidoc.metadataprofile.schema.x01.transformation.TransformationsDocument;
import de.mpg.escidoc.metadataprofile.schema.x01.transformation.TransformationsType;
import de.mpg.mpdl.inge.transformation.Transformation;
import de.mpg.mpdl.inge.transformation.Transformation.TransformationModule;
import de.mpg.mpdl.inge.transformation.Util;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.mpdl.inge.transformation.valueObjects.Format;
import de.mpg.mpdl.inge.util.ResourceUtil;

/**
 * Implements transformations for third party metadata formats.
 * 
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@TransformationModule
public class ThirdPartyTransformationInterface implements Transformation {
  private static final Logger logger = Logger.getLogger(ThirdPartyTransformationInterface.class);

  private static final String EXPLAIN_FILE_PATH = "transformations/thirdParty/";
  private static final String EXPLAIN_FILE_NAME = "explain-transformations.xml";

  private ThirdPartyTransformation transformer;

  public ThirdPartyTransformationInterface() {
    this.transformer = new ThirdPartyTransformation();
  }

  @Override
  public Format[] getSourceFormats() throws RuntimeException {
    Vector<Format> sourceFormats = new Vector<Format>();
    TransformationsDocument transDoc = null;
    TransformationsType transType = null;

    ClassLoader cl = this.getClass().getClassLoader();
    java.io.InputStream in = cl.getResourceAsStream(EXPLAIN_FILE_PATH + EXPLAIN_FILE_NAME);
    try {
      transDoc = TransformationsDocument.Factory.parse(in);
    } catch (Exception e) {
      logger.error("An error occurred while reading transformations.xml for micro formats.", e);
      throw new RuntimeException(e);
    }
    transType = transDoc.getTransformations();
    TransformationType[] transformations = transType.getTransformationArray();
    for (int i = 0; i < transformations.length; i++) {
      TransformationType transformation = transformations[i];
      String name = Util.simpleLiteralTostring(transformation.getSource().getName());
      String type = Util.simpleLiteralTostring(transformation.getSource().getType());
      String encoding = Util.simpleLiteralTostring(transformation.getSource().getEncoding());
      Format sourceFormat = new Format(name, type, encoding);

      sourceFormats.add(sourceFormat);
    }
    sourceFormats = Util.getRidOfDuplicatesInVector(sourceFormats);
    Format[] dummy = new Format[sourceFormats.size()];
    return sourceFormats.toArray(dummy);
  }

  @Override
  public Format[] getTargetFormats(Format src) throws RuntimeException {
    Vector<Format> targetFormats = new Vector<Format>();
    TransformationsDocument transDoc = null;
    TransformationsType transType = null;

    java.io.InputStream in;
    try {
      in =
          ResourceUtil.getResourceAsStream(EXPLAIN_FILE_PATH + EXPLAIN_FILE_NAME,
              ThirdPartyTransformationInterface.class.getClassLoader());
      transDoc = TransformationsDocument.Factory.parse(in);
    } catch (Exception e) {
      logger.error("An error occurred while reading transformations.xml for micro formats.", e);
      throw new RuntimeException(e);
    }
    transType = transDoc.getTransformations();
    TransformationType[] transformations = transType.getTransformationArray();
    for (TransformationType transformation : transformations) {
      Format source =
          new Format(Util.simpleLiteralTostring(transformation.getSource().getName()),
              Util.simpleLiteralTostring(transformation.getSource().getType()),
              Util.simpleLiteralTostring(transformation.getSource().getEncoding()));
      // Only get Target if source is given source
      if (Util.isFormatEqual(source, src)) {
        String name = Util.simpleLiteralTostring(transformation.getTarget().getName());
        String type = Util.simpleLiteralTostring(transformation.getTarget().getType());
        String encoding = Util.simpleLiteralTostring(transformation.getTarget().getEncoding());
        Format sourceFormat = new Format(name, type, encoding);

        targetFormats.add(sourceFormat);
      }
    }
    targetFormats = Util.getRidOfDuplicatesInVector(targetFormats);
    Format[] dummy = new Format[targetFormats.size()];
    return targetFormats.toArray(dummy);
  }

  @Override
  public byte[] transform(byte[] src, String srcFormatName, String srcType, String srcEncoding,
      String trgFormatName, String trgType, String trgEncoding, String service)
      throws TransformationNotSupportedException, RuntimeException {
    Format source = new Format(srcFormatName, srcType, srcEncoding);
    Format target = new Format(trgFormatName, trgType, trgEncoding);
    return this.transform(src, source, target, service);
  }

  @Override
  public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service)
      throws TransformationNotSupportedException, RuntimeException {
    byte[] result = null;
    boolean supported = false;

    if (this.transformer.checkXsltTransformation(srcFormat.getName(), trgFormat.getName())) {
      try {
        String transformedXml =
            this.transformer.xsltTransform(srcFormat.getName(), trgFormat.getName(), new String(
                src, "UTF-8"));
        result = transformedXml.getBytes("UTF-8");
      } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
      }
      supported = true;
    }
    if (!supported) {
      logger.warn("Transformation not supported: \n" + srcFormat.getName() + ", "
          + srcFormat.getType() + ", " + srcFormat.getEncoding() + "\n" + trgFormat.getName()
          + ", " + trgFormat.getType() + ", " + trgFormat.getEncoding());
      throw new TransformationNotSupportedException();
    }
    return result;
  }

  @Override
  public Format[] getSourceFormats(Format trg) throws RuntimeException {
    Vector<Format> sourceFormats = new Vector<Format>();
    TransformationsDocument transDoc = null;
    TransformationsType transType = null;

    ClassLoader cl = this.getClass().getClassLoader();
    java.io.InputStream in = cl.getResourceAsStream(EXPLAIN_FILE_PATH + EXPLAIN_FILE_NAME);
    try {
      transDoc = TransformationsDocument.Factory.parse(in);
    } catch (Exception e) {
      logger.error(
          "An error occurred while reading transformations.xml for common publication formats.", e);
      throw new RuntimeException(e);
    }

    transType = transDoc.getTransformations();
    TransformationType[] transformations = transType.getTransformationArray();
    for (TransformationType transformation : transformations) {
      Format target =
          new Format(Util.simpleLiteralTostring(transformation.getTarget().getName()),
              Util.simpleLiteralTostring(transformation.getTarget().getType()),
              Util.simpleLiteralTostring(transformation.getTarget().getEncoding()));
      // Only get Target if source is given source
      if (Util.isFormatEqual(target, trg)) {
        String name = Util.simpleLiteralTostring(transformation.getSource().getName());
        String type = Util.simpleLiteralTostring(transformation.getSource().getType());
        String encoding = Util.simpleLiteralTostring(transformation.getSource().getEncoding());
        Format format = new Format(name, type, encoding);

        sourceFormats.add(format);
      }
    }
    sourceFormats = Util.getRidOfDuplicatesInVector(sourceFormats);
    Format[] dummy = new Format[sourceFormats.size()];

    return sourceFormats.toArray(dummy);
  }
}
