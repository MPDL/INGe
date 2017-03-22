package de.mpg.mpdl.inge.transformation;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;

public class TransformerCacheTest {

  @Test
  public void test() {
    Transformer t1 = null, t2 = null, t3 = null, t4 = null;
    try {
      t1 =
          TransformerCache.getTransformer(FORMAT.ESCIDOC_ITEMLIST_V2_XML,
              FORMAT.ESCIDOC_ITEMLIST_V1_XML);

      assertTrue(TransformerCache.getInstance().getTransformerCacheSize() == 1);
      assertTrue(t1 != null);

      t2 =
          TransformerCache.getTransformer(FORMAT.ESCIDOC_ITEMLIST_V3_XML,
              FORMAT.ESCIDOC_ITEMLIST_V1_XML);
      assertTrue(TransformerCache.getInstance().getTransformerCacheSize() == 2);
      assertTrue(t2 != null);

      t3 =
          TransformerCache.getTransformer(FORMAT.ESCIDOC_ITEMLIST_V2_XML,
              FORMAT.ESCIDOC_ITEMLIST_V1_XML);
      assertTrue(TransformerCache.getInstance().getTransformerCacheSize() == 2);
      assertTrue(t3 != null && t1 == t3);

    } catch (TransformationException e) {
      Assert.fail();
    }

    try {
      t4 = TransformerCache.getTransformer(FORMAT.BIBTEX_STRING, FORMAT.BMC_XML);
    } catch (Exception e) {
      Assert.assertTrue(e instanceof TransformationException);;
    }
    assertTrue(t4 == null);
    assertTrue(TransformerCache.getInstance().getTransformerCacheSize() == 2);
  }

}
