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

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.citationmanager.CitationStyleExecutorService;
import de.mpg.mpdl.inge.citationmanager.CitationStyleManagerException;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO.FormatType;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.transformation.Transformation;
import de.mpg.mpdl.inge.transformation.TransformationService;
import de.mpg.mpdl.inge.transformation.Util;
import de.mpg.mpdl.inge.transformation.Util.Styles;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.mpdl.inge.transformation.valueObjects.Format;

/**
 * Implements transformations for citation styles.
 * 
 * @author Friederike Kleinfercher (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class CitationTransformation {
  private static final Logger logger = Logger.getLogger(CitationTransformation.class);

  private final String typeHTML = "text/html";
  private final String typeRTF1 = "text/richtext";
  private final String typeRTF2 = "application/rtf";
  private final String typeODT = "application/vnd.oasis.opendocument.text";
  private final String typePDF = "application/pdf";
  private final String typeSnippet = "snippet";

  /**
   * Public constructor.
   */
  public CitationTransformation() {}

  /**
   * Transformation to citation style in snippet format.
   * 
   * @param src
   * @param srcFormat
   * @param trgFormat
   * @param itemListBool - checks if the provided object is an itemList
   * @param service
   * @return transformed item as byte[]
   * @throws TransformationNotSupportedException
   * @throws RuntimeException
   */

  public byte[] transformEscidocItemToCitation(byte[] src, Format srcFormat, Format trgFormat,
      String service, boolean itemListBool) throws TransformationNotSupportedException,
      RuntimeException {
    try {
      String itemList = "";
      if (!itemListBool) {
        PubItemVO itemVO = XmlTransformingService.transformToPubItem(new String(src, "UTF-8"));
        List<PubItemVO> pubitemList = Arrays.asList(itemVO);
        itemList = XmlTransformingService.transformToItemList(pubitemList);
      } else {
        itemList = new String(src, "UTF-8");
      }
      return CitationStyleExecutorService.getOutput(itemList, new ExportFormatVO(FormatType.LAYOUT,
          "snippet", trgFormat.getName().toUpperCase()));
    } catch (CitationStyleManagerException e) {
      throw new TransformationNotSupportedException(e);
    } catch (Exception e) {
      logger.error("An error occurred during a citation transformation.", e);
      throw new RuntimeException(e);
    }
  }

  /**
   * This method calls the transformation service for the transformation from citation snippet to a
   * given output format.
   * 
   * @param src
   * @param srcFormat
   * @param trgFormat
   * @param service
   * @return
   */
  public byte[] transformOutputFormat(byte[] src, Format srcFormat, Format trgFormat, String service)
      throws TransformationNotSupportedException, RuntimeException {
    Transformation transformer = new TransformationService();

    // Create input format
    Styles style = Util.getStyleInfo(trgFormat);
    String formatName = "snippet";
    if (style == Styles.APA || style == Styles.AJP) {
      formatName += "_" + style.toString();
    }
    Format input = new Format(formatName, "application/xml", "UTF-8");
    // Create output format
    Format output =
        new Format(this.getOutputFormat(trgFormat.getType()), trgFormat.getType(),
            trgFormat.getEncoding());
    // Do the transformation
    return transformer.transform(src, input, output, service);
  }

  private String getOutputFormat(String type) {
    if (type.toLowerCase().equals(this.typeHTML)) {
      return "html";
    }
    if (type.toLowerCase().equals(this.typeODT)) {
      return "odt";
    }
    if (type.toLowerCase().equals(this.typePDF)) {
      return "pdf";
    }
    if (type.toLowerCase().equals(this.typeRTF1) || type.toLowerCase().equals(this.typeRTF2)) {
      return "rtf";
    }
    if (type.toLowerCase().equals(this.typeSnippet)) {
      return "snippet";
    }

    return null;
  }
}
