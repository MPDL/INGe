package de.mpg.mpdl.inge.transformation.util;

import static org.junit.Assert.*;

import org.junit.Test;

import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.util.Format;

public class FormatToFORMATTest {

  @Test
  public void test1() {
    assertTrue(new Format("pmc", "application/xml", "UTF-8").toFORMAT().equals(
        TransformerFactory.FORMAT.PMC_OAIPMH_XML));
    assertTrue(new Format("arxiv", "application/xml", "UTF-8").toFORMAT().equals(
        TransformerFactory.FORMAT.ARXIV_OAIPMH_XML));
    assertTrue(new Format("bmc", "application/xml", "UTF-8").toFORMAT().equals(
        TransformerFactory.FORMAT.BMC_XML));
    assertTrue(new Format("spires", "application/xml", "UTF-8").toFORMAT().equals(
        TransformerFactory.FORMAT.SPIRES_XML));
  }

  @Test
  public void test2() {

    assertTrue(new Format("escidoc-publication-item", "application/xml", "UTF-8").toFORMAT()
        .equals(TransformerFactory.FORMAT.ESCIDOC_ITEM_V3_XML));
  }

}
