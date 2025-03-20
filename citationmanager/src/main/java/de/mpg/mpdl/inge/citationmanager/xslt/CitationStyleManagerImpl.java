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

package de.mpg.mpdl.inge.citationmanager.xslt;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import de.mpg.mpdl.inge.citationmanager.CitationStyleExecuterService;
import de.mpg.mpdl.inge.citationmanager.CitationStyleManagerException;
import de.mpg.mpdl.inge.citationmanager.CitationStyleManagerInterface;
import de.mpg.mpdl.inge.citationmanager.utils.CitationUtil;
import de.mpg.mpdl.inge.citationmanager.utils.Utils;
import de.mpg.mpdl.inge.citationmanager.utils.XmlHelper;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.util.EntityTransformer;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.util.ResourceUtil;
import net.sf.saxon.event.SaxonOutputKeys;

/**
 *
 * Citation Style Manager
 *
 * @author Initial creation: vmakarenko
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class CitationStyleManagerImpl implements CitationStyleManagerInterface {

  public enum TASKS
  {
    validate, compile, pdf, rtf, odt, html_plain, html_styled, txt, snippet, escidoc_snippet
  }


  private static final XmlHelper xh = new XmlHelper();

  @Override
  public void compile(String cs) throws CitationStyleManagerException {
    Utils.checkName(cs, "Citaion Style is not defined");

    try {
      Transformer transformer = XmlHelper
          .tryTemplCache(
              CitationUtil.getPathToClasses() + CitationUtil.TRANSFORMATIONS_DIRECTORY + CitationUtil.CITATION_STYLE_PROCESSING_XSL)
          .newTransformer();

      transformer.setURIResolver(new compilationURIResolver());
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(SaxonOutputKeys.INDENT_SPACES, "4");

      transformer
          .transform(
              new StreamSource(
                  ResourceUtil.getResourceAsStream(CitationUtil.RESOURCES_DIRECTORY_LOCAL + CitationUtil.CITATIONSTYLES_DIRECTORY + cs + "/"
                      + CitationUtil.CITATION_STYLE_XML, CitationStyleManagerImpl.class.getClassLoader())),
              new StreamResult(new FileOutputStream(CitationUtil.RESOURCES_DIRECTORY_LOCAL + CitationUtil.CITATIONSTYLES_DIRECTORY + cs
                  + "/" + CitationUtil.CITATION_STYLE_XSL)));
    } catch (Exception e) {
      throw new RuntimeException("Cannot compile Citation Style " + cs, e);
    }
  }

  @Override
  public void create(String cs) {}

  @Override
  public void delete(String cs) {}

  @Override
  public void update(String cs, String newCs) {}

  @Override
  public String validate(String cs) throws CitationStyleManagerException {
    Utils.checkName(cs, "Citation Style is not defined");

    return xh.validateCitationStyleXML(cs);
  }

  public static class compilationURIResolver implements URIResolver {

    @Override
    public Source resolve(String href, String base) throws TransformerException {
      InputStream is;

      String path =
          ("cs-processing-xslt-includes.xml".equals(href) ? CitationUtil.getPathToClasses() + CitationUtil.TRANSFORMATIONS_DIRECTORY
              : CitationUtil.getPathToCitationStyles()) + href;
      try {
        is = ResourceUtil.getResourceAsStream(path, CitationStyleManagerImpl.class.getClassLoader());
      } catch (FileNotFoundException e) {
        throw new TransformerException(e);
      }
      return new StreamSource(is);
    }
  }

  public static void main(String[] args) throws IOException, CitationStyleManagerException, TechnicalException {
    CitationStyleManagerInterface csm = new CitationStyleManagerImpl();

    String il = null;
    String cs = null;
    String task = null;

    if (0 == args.length) {
      usage();
      return;
    }

    int k = 0;
    while (args.length > k) {
      if (args[k].startsWith("-T"))
        task = args[k].substring(2);
      if (args[k].startsWith("-CS"))
        cs = args[k].substring(3);
      if (args[k].startsWith("-OF")) {
      }
      if (args[k].startsWith("-IL"))
        il = args[k].substring(3);

      k++;
    }

    // if ( task.equals(TASKS.validate.toString()))
    if (TASKS.validate == TASKS.valueOf(task)) {
      String report = csm.validate(cs);
      if (null == report)
        System.out.println(cs + " Citation Style XML for is valid.");
      else
        System.out.println(cs + " Citation Style XML for is not valid:\n" + report);

    } else if (TASKS.compile == TASKS.valueOf(task)) {
      System.out.println(cs + " Citation Style compilation.");
      csm.compile(cs);
      System.out.println("OK");
    }
    // all other tasks
    else if (null != TASKS.valueOf(task)) {
      String outFile = cs + "_output_" + task + "." + XmlHelper.getExtensionByName(task);
      System.out.println(cs + " Citation Style output in " + task + " format. File: " + outFile);
      String escidocXml = ResourceUtil.getResourceAsString(il, CitationStyleManagerImpl.class.getClassLoader());
      List<PubItemVO> oldItemList = XmlTransformingService.transformToPubItemList(escidocXml);
      List<ItemVersionVO> newItemList = EntityTransformer.transformToNew(oldItemList);
      List<String> result = CitationStyleExecuterService.getOutput(newItemList, new ExportFormatVO(task, cs));
      System.out.println(result);
      System.out.println("OK");
    }
  }

  private static void usage() {
    System.out.println("CitationStyleManagerImpl usage:");
    System.out.println("\tjava CitationStyleManagerImpl -Ttask -CScitationstyle -ILitemlist");
    System.out
        .println("\tTasks : validate | compile | pdf | rtf | odt | txt | html_plain | html_styled | snippet | escidoc_snippet");
  }
}
