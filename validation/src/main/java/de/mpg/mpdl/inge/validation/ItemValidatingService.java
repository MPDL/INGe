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

package de.mpg.mpdl.inge.validation;

import static de.mpg.mpdl.inge.validation.XsltTransforming.transform;

import java.io.StringWriter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Transformer;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.validation.valueobjects.ValidationReportVO;

/**
 * 
 * Validate item data against a given validation schema.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class ItemValidatingService {
  private static final Logger LOGGER = Logger.getLogger(ItemValidatingService.class);

  /**
   * {@inheritDoc}
   */
  public static final String validateItemXml(final String itemXml)
      throws ValidationSchemaNotFoundException, TechnicalException {

    return validateItemXml(itemXml, "default");
  }

  /**
   * {@inheritDoc}
   */
  public static String validateItemXml(final String itemXml, final String validationPoint)
      throws ValidationSchemaNotFoundException, TechnicalException {
    String context = findContext(itemXml);
    String contentModel = findContentModel(itemXml);

    LOGGER.debug("Context found: " + context);
    LOGGER.debug("ContentModel found: " + contentModel);

    return validateItemXml(itemXml, validationPoint, context, contentModel);
  }

  private static String findContentModel(final String itemXml) throws TechnicalException {
    try {
      Pattern pattern =
          Pattern.compile(PropertyReader.getProperty("escidoc.content.model.pattern"));
      Matcher matcher = pattern.matcher(itemXml);
      if (matcher.find()) {
        String contentTypeHref = matcher.group(1);
        return contentTypeHref.substring(contentTypeHref.lastIndexOf('/') + 1);
      } else {
        throw new TechnicalException("No content model in xml");
      }
    } catch (Exception ioe) {
      throw new TechnicalException("Error getting property", ioe);
    }
  }

  private static String findContext(final String itemXml) throws TechnicalException {
    try {
      LOGGER.debug("CONTEXT: " + PropertyReader.getProperty("escidoc.context.pattern"));
      Pattern pattern = Pattern.compile(PropertyReader.getProperty("escidoc.context.pattern"));
      Matcher matcher = pattern.matcher(itemXml);
      if (matcher.find()) {
        String contextHref = matcher.group(1);
        return contextHref.substring(contextHref.lastIndexOf('/') + 1);
      } else {
        throw new TechnicalException("No context in xml");
      }
    } catch (Exception ioe) {
      throw new TechnicalException("Error getting property", ioe);
    }
  }

  /**
   * {@inheritDoc}
   */
  private static String validateItemXml(final String itemXml, final String validationPoint,
      final String context, final String contentType) throws ValidationSchemaNotFoundException,
      TechnicalException {

    try {
      String validationSchema = ValidationSchemaCache.getInstance().getValidationSchemaId(context);

      return validateItemXmlBySchema(itemXml, validationPoint, validationSchema, contentType);
    } catch (Exception e) {
      throw new TechnicalException("Error getting validation schema", e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public static String validateItemXmlBySchema(final String itemXml, final String validationPoint,
      final String validationSchema) throws ValidationSchemaNotFoundException, TechnicalException {

    String contentType = findContentModel(itemXml);
    return validateItemXmlBySchema(itemXml, validationPoint, validationSchema, contentType);
  }

  /**
   * {@inheritDoc}
   */
  private static String validateItemXmlBySchema(final String itemXml, final String validationPoint,
      final String validationSchema, final String contentType)
      throws ValidationSchemaNotFoundException, TechnicalException {

    try {
      Transformer precompiled =
          ValidationSchemaCache.getInstance().getPrecompiledTransformer(validationSchema,
              contentType, validationPoint);
      StringWriter result = transform(itemXml, precompiled, null);

      return result.toString();
    } catch (Exception e) {
      throw new TechnicalException("Error during validation", e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public static ValidationReportVO validateItemObject(final ItemVO itemVO)
      throws ValidationSchemaNotFoundException, TechnicalException {
    if (itemVO instanceof ItemVO) {
      return transformXmlToValidationReport(validateItemXml(
          XmlTransformingService.transformToItem((ItemVO) itemVO), "default"));
    } else {
      // TODO: Implementation for other content models.
      return null;
    }
  }

  /**
   * {@inheritDoc}
   */
  public static ValidationReportVO validateItemObject(final ItemVO itemVO,
      final String validationPoint) throws ValidationSchemaNotFoundException, TechnicalException {

    LOGGER.debug("Validating a " + itemVO.getClass().getSimpleName());

    if (itemVO instanceof ItemVO) {
      return transformXmlToValidationReport(validateItemXml(
          XmlTransformingService.transformToItem((ItemVO) itemVO), validationPoint));
    } else {
      // TODO: Implementation for other content models.
      return null;
    }
  }

  /**
   * {@inheritDoc}
   */
  public static ValidationReportVO validateItemObjectBySchema(final ItemVO itemVO,
      final String validationPoint, final String validationSchema)
      throws ValidationSchemaNotFoundException, TechnicalException {

    LOGGER.debug("Validating a " + itemVO.getClass().getSimpleName());

    return transformXmlToValidationReport(validateItemXmlBySchema(
        XmlTransformingService.transformToItem((ItemVO) itemVO), validationPoint, validationSchema));

  }

  /**
   * Transform the report xml to a validation report value object.
   * 
   * @param reportXml The report xml.
   * @return The validation report as value object.
   */
  private static ValidationReportVO transformXmlToValidationReport(final String reportXml)
      throws TechnicalException {
    ValidationReportVO result;
    try {
      result = ValidationTransformingService.transformToValidationReport(reportXml);
    } catch (Exception e) {
      throw new TechnicalException(e);
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  public static void refreshValidationSchemaCache() throws TechnicalException {
    ValidationSchemaCache.getInstance().refreshCache();
  }

  /**
   * {@inheritDoc}
   */
  public static Date getLastRefreshDate() throws TechnicalException {
    return ValidationSchemaCache.getInstance().getLastRefreshDate();
  }
}
