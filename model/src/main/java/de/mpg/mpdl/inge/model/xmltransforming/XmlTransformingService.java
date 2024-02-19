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

package de.mpg.mpdl.inge.model.xmltransforming;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.PidServiceResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.model.xmltransforming.util.FileVOCreationDateComparator;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.exceptions.MarshallingException;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.exceptions.UnmarshallingException;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.wrappers.ItemVOListWrapper;

/**
 * @author Johannes Mueller (initial creation)
 * @version $Revision$ $LastChangedDate$Author: mfranke $
 * @revised by MuJ: 21.08.2007
 */
public class XmlTransformingService {
  private static final Logger logger = LogManager.getLogger(XmlTransformingService.class);

  private XmlTransformingService() {}

  public static String transformToItem(ItemVO itemVO) throws TechnicalException {
    logger.debug("transformToItem(PubItemVO)");
    if (null == itemVO) {
      throw new IllegalArgumentException(XmlTransformingService.class.getSimpleName() + ":transformToItem:pubItemVO is null");
    }
    String utf8item = null;
    try {
      IBindingFactory bfact = BindingDirectory.getFactory("PubItemVO_PubCollectionVO_output", ItemVO.class);
      // marshal object (with nice indentation, as UTF-8)
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      StringWriter sw = new StringWriter();
      mctx.setOutput(sw);
      mctx.marshalDocument(itemVO, "UTF-8", null, sw);
      // use the following call to omit the leading "<?xml" tag of the generated XML
      // mctx.marshalDocument(pubItemVO);
      utf8item = sw.toString().trim();
    } catch (JiBXException e) {
      throw new MarshallingException(ItemVO.class.getSimpleName(), e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    if (logger.isDebugEnabled()) {
      logger.debug("transformToItem(ItemVO) - result: String utf8item=" + utf8item);
    }
    return utf8item;
  }

  public static ItemVO transformToItem(String item) throws TechnicalException {
    logger.debug("transformToPubItem(String) - String item=" + item);
    if (null == item) {
      throw new IllegalArgumentException(XmlTransformingService.class.getSimpleName() + ":transformToPubItem:item is null");
    }
    ItemVO itemVO = null;
    try {
      IBindingFactory bfact = BindingDirectory.getFactory("PubItemVO_PubCollectionVO_input", ItemVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(item);
      itemVO = (ItemVO) uctx.unmarshalDocument(sr, null);
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error("Error transforming item", e);
      throw new UnmarshallingException(item, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    itemVO.getFiles().sort(new FileVOCreationDateComparator());
    return itemVO;
  }

  private static List<? extends ItemVO> transformToItemList(String itemListXml) throws TechnicalException {
    logger.debug("transformToPubItemList(String) - String itemList=\n" + itemListXml);
    if (null == itemListXml) {
      throw new IllegalArgumentException(XmlTransformingService.class.getSimpleName() + ":transformToPubItemList:itemList is null");
    }
    ItemVOListWrapper itemVOListWrapper = null;
    try {
      // unmarshal ItemVOListWrapper from String
      IBindingFactory bfact = BindingDirectory.getFactory("PubItemVO_PubCollectionVO_input", ItemVOListWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(itemListXml);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      itemVOListWrapper = (ItemVOListWrapper) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(itemListXml, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    // unwrap the List<ItemVO>
    List<? extends ItemVO> itemList = itemVOListWrapper.getItemVOList();

    return itemList;
  }

  public static String transformToItemList(List<? extends ItemVO> itemVOList) throws TechnicalException {
    logger.debug("transformToItemList(List<ItemVO>)");
    if (null == itemVOList) {
      throw new IllegalArgumentException(XmlTransformingService.class.getSimpleName() + ":transformToItemList:pubItemVOList is null");
    }
    // wrap the item list into the according wrapper class
    ItemVOListWrapper listWrapper = new ItemVOListWrapper();
    listWrapper.setItemVOList(itemVOList);

    return transformToItemList(listWrapper);
  }

  public static String transformToItemList(ItemVOListWrapper itemListWrapper) throws TechnicalException {
    // transform the wrapper class into XML
    String utf8itemList = null;
    try {
      IBindingFactory bfact = BindingDirectory.getFactory("PubItemVO_PubCollectionVO_output", ItemVOListWrapper.class);
      // marshal object (with nice indentation, as UTF-8)
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      StringWriter sw = new StringWriter();
      mctx.setOutput(sw);
      mctx.marshalDocument(itemListWrapper, "UTF-8", null, sw);
      utf8itemList = sw.toString().trim();
      // <sub>, <sup>, <br>
      utf8itemList = utf8itemList.replaceAll("&lt;br>", "&lt;br&gt;");
      utf8itemList = utf8itemList.replaceAll("&lt;BR>", "&lt;BR&gt;");
      utf8itemList = utf8itemList.replaceAll("&lt;sub>", "&lt;sub&gt;");
      utf8itemList = utf8itemList.replaceAll("&lt;/sub>", "&lt;/sub&gt;");
      utf8itemList = utf8itemList.replaceAll("&lt;sup>", "&lt;sup&gt;");
      utf8itemList = utf8itemList.replaceAll("&lt;/sup>", "&lt;/sup&gt;");
      utf8itemList = utf8itemList.replaceAll("&lt;SUB>", "&lt;SUB&gt;");
      utf8itemList = utf8itemList.replaceAll("&lt;/SUB>", "&lt;/SUB&gt;");
      utf8itemList = utf8itemList.replaceAll("&lt;SUP>", "&lt;SUP&gt;");
      utf8itemList = utf8itemList.replaceAll("&lt;/SUP>", "&lt;/SUP&gt;");
    } catch (JiBXException e) {
      throw new MarshallingException(ItemVOListWrapper.class.getSimpleName(), e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    if (logger.isDebugEnabled()) {
      logger.debug("transformToItemList(List<ItemVO>) - result: String utf8itemList=\n" + utf8itemList);
    }
    return utf8itemList;
  }

  public static PubItemVO transformToPubItem(String itemXml) throws TechnicalException {
    ItemVO itemVO = transformToItem(itemXml);
    if (!itemVO.getMetadataSets().isEmpty() && itemVO.getMetadataSets().get(0) instanceof MdsPublicationVO) {
      return new PubItemVO(itemVO);
    } else {
      logger.warn("Cannot transform item xml to PubItemVO");
      return null;
    }
  }

  public static List<PubItemVO> transformToPubItemList(String itemList) throws TechnicalException {
    List<? extends ItemVO> list = transformToItemList(itemList);
    List<PubItemVO> newList = new ArrayList<>();
    for (ItemVO itemVO : list) {
      PubItemVO pubItemVO = new PubItemVO(itemVO);
      newList.add(pubItemVO);
    }
    return newList;
  }

  public static FileVO transformToFileVO(String fileXML) throws TechnicalException {
    logger.debug("transformToFileVO(String) - String file=\n" + fileXML);
    if (null == fileXML) {
      throw new IllegalArgumentException(XmlTransformingService.class.getSimpleName() + ":transformTofileVO: fileXML is null");
    }
    FileVO fileVO = null;
    try {
      IBindingFactory bfact = BindingDirectory.getFactory("PubItemVO_PubCollectionVO_input", FileVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(fileXML);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      fileVO = (FileVO) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(fileXML, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }

    return fileVO;
  }

  public static PidServiceResponseVO transformToPidServiceResponse(String pidServiceResponseXml) throws TechnicalException {
    logger.debug("transformToPidServiceResponse(String) - String pidServiceResponse=\n" + pidServiceResponseXml);
    if (null == pidServiceResponseXml) {
      throw new IllegalArgumentException(
          XmlTransformingService.class.getSimpleName() + ":transformToPidServiceResponse: pidServiceResponseXml is null");
    }
    PidServiceResponseVO pidServiceResponseVO = null;

    try {
      // unmarshal pidServiceResponse from String
      IBindingFactory bfact = BindingDirectory.getFactory(PidServiceResponseVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(pidServiceResponseXml);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      pidServiceResponseVO = (PidServiceResponseVO) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(pidServiceResponseXml, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }

    return pidServiceResponseVO;
  }
}
