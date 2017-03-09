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

package de.mpg.mpdl.inge.citationmanager.transformation;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import de.mpg.escidoc.metadataprofile.schema.x01.transformation.TransformationType;
import de.mpg.escidoc.metadataprofile.schema.x01.transformation.TransformationsDocument;
import de.mpg.escidoc.metadataprofile.schema.x01.transformation.TransformationsType;
import de.mpg.mpdl.inge.transformation.Util;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.util.Format;
import de.mpg.mpdl.inge.util.ResourceUtil;

/**
 * Implementation of the transformation interface for citation formats.
 * 
 * @author Friederike Kleinfercher (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class CitationTransformationInterface {

  private final Logger logger = Logger.getLogger(CitationTransformationInterface.class);

  private final String EXPLAIN_FILE_PATH = "Transformations/";
  private final String EXPLAIN_FILE_NAME = "explain-transformations.xml";

  /**
   * Public constructor.
   */
  public CitationTransformationInterface() {}

  /**
   * {@inheritDoc}
   */
  public Format[] getSourceFormats() throws RuntimeException {
    Set<Format> sourceFormats = new HashSet<Format>();
    TransformationsDocument transDoc = null;
    TransformationsType transType = null;

    java.io.InputStream in;
    try {
      in =
          ResourceUtil.getResourceAsStream(EXPLAIN_FILE_PATH + EXPLAIN_FILE_NAME,
              CitationTransformationInterface.class.getClassLoader());
      transDoc = TransformationsDocument.Factory.parse(in);
    } catch (Exception e) {
      logger.error("An error occurred while reading transformations.xml for citation formats.", e);
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
    Format[] dummy = new Format[sourceFormats.size()];
    return sourceFormats.toArray(dummy);
  }

  /**
   * {@inheritDoc}
   */
  public Format[] getTargetFormats(Format src) throws RuntimeException {
    Set<Format> targetFormats = new HashSet<Format>();
    TransformationsDocument transDoc = null;
    TransformationsType transType = null;

    java.io.InputStream in;
    try {
      in =
          ResourceUtil.getResourceAsStream(EXPLAIN_FILE_PATH + EXPLAIN_FILE_NAME,
              CitationTransformationInterface.class.getClassLoader());
      transDoc = TransformationsDocument.Factory.parse(in);
    } catch (Exception e) {
      logger.error("An error occurred while reading transformations.xml for citation formats.", e);
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
      if (source.equals(src)) {
        String name = Util.simpleLiteralTostring(transformation.getTarget().getName());
        String type = Util.simpleLiteralTostring(transformation.getTarget().getType());
        String encoding = Util.simpleLiteralTostring(transformation.getTarget().getEncoding());
        Format sourceFormat = new Format(name, type, encoding);

        targetFormats.add(sourceFormat);
      }
    }

    Format[] dummy = new Format[targetFormats.size()];

    return targetFormats.toArray(dummy);
  }

  /**
   * {@inheritDoc}
   */
  public byte[] transform(byte[] src, String srcFormatName, String srcType, String srcEncoding,
      String trgFormatName, String trgType, String trgEncoding, String service)
      throws TransformationException {
    Format source = new Format(srcFormatName, srcType, srcEncoding);
    Format target = new Format(trgFormatName, trgType, trgEncoding);

    return this.transform(src, source, target, service);
  }

  /**
   * {@inheritDoc}
   */
  public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service)
      throws TransformationException, RuntimeException {
    byte[] result = null;
    boolean supported = false;
    boolean list = false;

    try {
      CitationTransformation citeTrans = new CitationTransformation();
      if (srcFormat.getName().toLowerCase().startsWith("escidoc")) {
        // 1. Transform to citation format with output snippet
        if (srcFormat.getName().equalsIgnoreCase("eSciDoc-publication-item-list")) {
          list = true;
        }
        result = citeTrans.transformEscidocItemToCitation(src, srcFormat, trgFormat, service, list);
        if (result != null) {
          supported = true;
        }

        // 2. Transform to given outputformat using the transformation service
        if (!trgFormat.getType().equalsIgnoreCase("application/xml")) {
          result = citeTrans.transformOutputFormat(src, srcFormat, trgFormat, service);
          if (result != null) {
            supported = true;
          }
        }
      }
    } catch (Exception e) {
      logger.error("An error occurred during a citation transformation.", e);
      throw new RuntimeException(e);
    }

    if (!supported) {
      logger.warn("Transformation not supported: \n" + srcFormat.getName() + ", "
          + srcFormat.getType() + ", " + srcFormat.getEncoding() + "\n" + trgFormat.getName()
          + ", " + trgFormat.getType() + ", " + trgFormat.getEncoding());
      throw new TransformationException();
    }

    return result;
  }

  /**
   * {@inheritDoc}
   */
  public Format[] getSourceFormats(Format trg) throws RuntimeException {
    Set<Format> sourceFormats = new HashSet<Format>();
    TransformationsDocument transDoc = null;
    TransformationsType transType = null;

    java.io.InputStream in;
    try {
      in =
          ResourceUtil.getResourceAsStream(EXPLAIN_FILE_PATH + EXPLAIN_FILE_NAME,
              CitationTransformationInterface.class.getClassLoader());
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
      if (target.equals(trg)) {
        String name = Util.simpleLiteralTostring(transformation.getSource().getName());
        String type = Util.simpleLiteralTostring(transformation.getSource().getType());
        String encoding = Util.simpleLiteralTostring(transformation.getSource().getEncoding());
        Format format = new Format(name, type, encoding);

        sourceFormats.add(format);
      }
    }

    Format[] dummy = new Format[sourceFormats.size()];
    return sourceFormats.toArray(dummy);
  }
}
