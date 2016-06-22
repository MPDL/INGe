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

package de.mpg.mpdl.inge.transformation.transformations.standardFormats;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.mpg.escidoc.metadataprofile.schema.x01.transformation.TransformationType;
import de.mpg.escidoc.metadataprofile.schema.x01.transformation.TransformationsDocument;
import de.mpg.escidoc.metadataprofile.schema.x01.transformation.TransformationsType;
import de.mpg.mpdl.inge.transformation.Configurable;
import de.mpg.mpdl.inge.transformation.Transformation;
import de.mpg.mpdl.inge.transformation.Transformation.TransformationModule;
import de.mpg.mpdl.inge.transformation.Util;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.mpdl.inge.transformation.valueObjects.Format;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ResourceUtil;

/**
 * Implementation of the transformation interface for standard formats.
 * 
 * @author Friederike Kleinfercher (initial creation)
 * 
 */
@TransformationModule
public class StandardTransformationInterface implements Transformation, Configurable {

  private final Logger logger = Logger.getLogger(StandardTransformationInterface.class);

  private final String EXPLAIN_FILE_PATH = "transformations/standardFormats/";
  private final String EXPLAIN_FILE_NAME = "explain-transformations.xml";

  private Util util;
  private StandardTransformation transformer;

  /**
   * Public constructor.
   */
  public StandardTransformationInterface() {
    this.transformer = new StandardTransformation();
  }

  /**
   * {@inheritDoc}
   */
  public Format[] getSourceFormats() throws RuntimeException {
    Vector<Format> sourceFormats = new Vector<Format>();
    TransformationsDocument transDoc = null;
    TransformationsType transType = null;

    java.io.InputStream in;
    try {
      in =
          ResourceUtil.getResourceAsStream(this.EXPLAIN_FILE_PATH + this.EXPLAIN_FILE_NAME,
              StandardTransformationInterface.class.getClassLoader());
      transDoc = TransformationsDocument.Factory.parse(in);
    } catch (Exception e) {
      this.logger.error(
          "An error occurred while reading transformations.xml for common publication formats.", e);
      throw new RuntimeException(e);
    }
    transType = transDoc.getTransformations();
    TransformationType[] transformations = transType.getTransformationArray();
    for (int i = 0; i < transformations.length; i++) {
      TransformationType transformation = transformations[i];
      String name = this.util.simpleLiteralTostring(transformation.getSource().getName());
      String type = this.util.simpleLiteralTostring(transformation.getSource().getType());
      String encoding = this.util.simpleLiteralTostring(transformation.getSource().getEncoding());
      Format sourceFormat = new Format(name, type, encoding);

      sourceFormats.add(sourceFormat);
    }
    sourceFormats = this.util.getRidOfDuplicatesInVector(sourceFormats);
    Format[] dummy = new Format[sourceFormats.size()];
    return sourceFormats.toArray(dummy);
  }

  /**
   * {@inheritDoc}
   */
  public String getSourceFormatsAsXml() throws RuntimeException {
    Format[] formats = this.getSourceFormats();
    return this.util.createFormatsXml(formats);
  }

  /**
   * {@inheritDoc}
   */
  public String getTargetFormatsAsXml(String srcFormatName, String srcType, String srcEncoding)
      throws RuntimeException {
    Format[] formats = this.getTargetFormats(new Format(srcFormatName, srcType, srcEncoding));
    return this.util.createFormatsXml(formats);
  }

  /**
   * {@inheritDoc}
   */
  public Format[] getTargetFormats(Format src) throws RuntimeException {
    Vector<Format> targetFormats = new Vector<Format>();
    TransformationsDocument transDoc = null;
    TransformationsType transType = null;

    java.io.InputStream in;
    try {
      in =
          ResourceUtil.getResourceAsStream(this.EXPLAIN_FILE_PATH + this.EXPLAIN_FILE_NAME,
              StandardTransformationInterface.class.getClassLoader());
      transDoc = TransformationsDocument.Factory.parse(in);
    } catch (Exception e) {
      this.logger.error(
          "An error occurred while reading transformations.xml for standard publication formats.",
          e);
      throw new RuntimeException(e);
    }

    transType = transDoc.getTransformations();
    TransformationType[] transformations = transType.getTransformationArray();
    for (TransformationType transformation : transformations) {
      Format source =
          new Format(this.util.simpleLiteralTostring(transformation.getSource().getName()),
              this.util.simpleLiteralTostring(transformation.getSource().getType()),
              this.util.simpleLiteralTostring(transformation.getSource().getEncoding()));
      // Only get Target if source is given source
      if (this.util.isFormatEqual(source, src)) {
        String name = this.util.simpleLiteralTostring(transformation.getTarget().getName());
        String type = this.util.simpleLiteralTostring(transformation.getTarget().getType());
        String encoding = this.util.simpleLiteralTostring(transformation.getTarget().getEncoding());
        Format sourceFormat = new Format(name, type, encoding);

        targetFormats.add(sourceFormat);
      }
    }
    targetFormats = this.util.getRidOfDuplicatesInVector(targetFormats);
    Format[] dummy = new Format[targetFormats.size()];
    return targetFormats.toArray(dummy);
  }

  /**
   * {@inheritDoc}
   */
  public byte[] transform(byte[] src, String srcFormatName, String srcType, String srcEncoding,
      String trgFormatName, String trgType, String trgEncoding, String service)
      throws TransformationNotSupportedException {
    Format source = new Format(srcFormatName, srcType, srcEncoding);
    Format target = new Format(trgFormatName, trgType, trgEncoding);
    return this.transform(src, source, target, service);
  }

  /**
   * {@inheritDoc}
   */
  public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service)
      throws TransformationNotSupportedException, RuntimeException {
    byte[] result = null;
    boolean supported = false;

    // One xslt for both transformations (ignore case for multiple import format name)
    if (trgFormat.getName().equalsIgnoreCase("escidoc-publication-item-list")
        || trgFormat.getName().equalsIgnoreCase("escidoc-publication-item")) {
      trgFormat.setName("escidoc-publication-item");
    }

    if (this.transformer.checkXsltTransformation(srcFormat.getName(), trgFormat.getName())) {
      try {
        String transformedXml =
            this.transformer.xsltTransform(srcFormat.getName(), trgFormat.getName(), new String(
                src, "UTF-8"), null);
        result = transformedXml.getBytes("UTF-8");
      } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
      }
      supported = true;
    }
    if (!supported) {
      this.logger.warn("Transformation not supported: \n" + srcFormat.getName() + ", "
          + srcFormat.getType() + ", " + srcFormat.getEncoding() + "\n" + trgFormat.getName()
          + ", " + trgFormat.getType() + ", " + trgFormat.getEncoding());
      throw new TransformationNotSupportedException();
    }
    return result;
  }


  /**
   * {@inheritDoc}
   */
  public Format[] getSourceFormats(Format trg) throws RuntimeException {
    Vector<Format> sourceFormats = new Vector<Format>();
    TransformationsDocument transDoc = null;
    TransformationsType transType = null;

    java.io.InputStream in;
    try {
      in =
          ResourceUtil.getResourceAsStream(this.EXPLAIN_FILE_PATH + this.EXPLAIN_FILE_NAME,
              StandardTransformationInterface.class.getClassLoader());
      transDoc = TransformationsDocument.Factory.parse(in);
    } catch (Exception e) {
      this.logger.error(
          "An error occurred while reading transformations.xml for standard publication formats.",
          e);
      throw new RuntimeException(e);
    }

    transType = transDoc.getTransformations();
    TransformationType[] transformations = transType.getTransformationArray();
    for (TransformationType transformation : transformations) {
      Format target =
          new Format(this.util.simpleLiteralTostring(transformation.getTarget().getName()),
              this.util.simpleLiteralTostring(transformation.getTarget().getType()),
              this.util.simpleLiteralTostring(transformation.getTarget().getEncoding()));
      // Only get Target if source is given source
      if (this.util.isFormatEqual(target, trg)) {
        String name = this.util.simpleLiteralTostring(transformation.getSource().getName());
        String type = this.util.simpleLiteralTostring(transformation.getSource().getType());
        String encoding = this.util.simpleLiteralTostring(transformation.getSource().getEncoding());
        Format format = new Format(name, type, encoding);

        sourceFormats.add(format);
      }
    }
    sourceFormats = this.util.getRidOfDuplicatesInVector(sourceFormats);
    Format[] dummy = new Format[sourceFormats.size()];
    return sourceFormats.toArray(dummy);
  }


  private Map<String, List<String>> propertiesTrans = null;
  private Map<String, String> configuration = null;


  private void init() throws IOException, FileNotFoundException, URISyntaxException {

    configuration = new LinkedHashMap<String, String>();
    propertiesTrans = new HashMap<String, List<String>>();
    Properties props = new Properties();
    props.load(ResourceUtil.getResourceAsStream(
        PropertyReader.getProperty("escidoc.transformation.zfn.configuration.filename"),
        StandardTransformationInterface.class.getClassLoader()));
    for (Object key : props.keySet()) {
      if (!"configuration".equals(key.toString())) {
        String[] values = props.getProperty(key.toString()).split(",");
        propertiesTrans.put(key.toString(), Arrays.asList(values));
      } else {
        String[] confValues = props.getProperty("configuration").split(",");
        for (String field : confValues) {
          String[] fieldArr = field.split("=", 2);
          configuration.put(fieldArr[0], fieldArr[1] == null ? "" : fieldArr[1]);
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service,
      Map<String, String> configuration) throws TransformationNotSupportedException {

    try {
      String itemString = new String(src, "UTF-8");
      return this.transformer.xsltTransform(srcFormat.getName(), trgFormat.getName(), itemString,
          configuration).getBytes("UTF-8");
    } catch (Exception e) {
      this.logger.error("Error during string conversion", e);
      e.printStackTrace();
    }
    return null;
  }

  public List<String> getConfigurationValues(Format srcFormat, Format trgFormat, String key)
      throws Exception {
    if (propertiesTrans == null) {
      init();
    }

    return propertiesTrans.get(key);
  }

  public Map<String, String> getConfiguration(Format srcFormat, Format trgFormat) throws Exception {
    if (configuration == null) {
      init();
    }

    return configuration;
  }

}