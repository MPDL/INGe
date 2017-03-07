package de.mpg.mpdl.inge.transformation.transformations.otherFormats.mab;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import de.mpg.mpdl.inge.transformation.Transformation;
import de.mpg.mpdl.inge.transformation.Transformation.TransformationModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.mpdl.inge.transformation.transformations.LocalUriResolver;
import de.mpg.mpdl.inge.transformation.valueObjects.Format;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ResourceUtil;

@TransformationModule
public class MABTransformation implements Transformation {
  private static final Format ESCIDOC_ITEM_LIST_FORMAT = new Format(
      "eSciDoc-publication-item-list", "application/xml", "*");
  private static final Format ESCIDOC_ITEM_FORMAT = new Format("eSciDoc-publication-item",
      "application/xml", "*");
  private static final Format MAB_FORMAT = new Format("MAB", "text/plain", "UTF-8");

  public MABTransformation() {}

  @Override
  public Format[] getSourceFormats(Format targetFormat) throws RuntimeException {
    if (targetFormat != null
        && (targetFormat.matches(ESCIDOC_ITEM_FORMAT) || targetFormat
            .matches(ESCIDOC_ITEM_LIST_FORMAT))) {
      return new Format[] {MAB_FORMAT};
    } else {
      return new Format[] {};
    }
  }

  @Override
  public Format[] getTargetFormats(Format sourceFormat) throws RuntimeException {
    if (MAB_FORMAT.equals(sourceFormat)) {
      return new Format[] {ESCIDOC_ITEM_LIST_FORMAT, ESCIDOC_ITEM_FORMAT};
    } else {
      return new Format[] {};
    }
  }

  @Override
  public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service)
      throws TransformationNotSupportedException, RuntimeException {
    String output = "";
    try {

      StringWriter result = new StringWriter();

      if (srcFormat.matches(MAB_FORMAT)) {

        String mabSource = new String(src, "UTF-8");
        MABImport mab = new MABImport();
        output = mab.transformMAB2XML(mabSource);
        TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl();

        String xslPath =
            PropertyReader.getProperty("escidoc.transformation.mab.stylesheet.filename");
        String xslDir;
        if (xslPath != null) {
          xslPath = xslPath.replace('\\', '/');
          if (xslPath.contains("/")) {
            xslDir = xslPath.substring(0, xslPath.lastIndexOf("/"));
          } else {
            xslDir = ".";
          }
        } else {
          xslDir = ".";
          xslPath = "transformations/otherFormats/xslt/mabxml2escidoc.xsl";
        }

        factory.setURIResolver(new LocalUriResolver(xslDir));
        InputStream stylesheet =
            ResourceUtil.getResourceAsStream(xslPath, MABTransformation.class.getClassLoader());
        Transformer transformer = factory.newTransformer(new StreamSource(stylesheet));

        if (trgFormat.matches(ESCIDOC_ITEM_LIST_FORMAT)) {
          transformer.setParameter("is-item-list", Boolean.TRUE);
        } else if (trgFormat.matches(ESCIDOC_ITEM_FORMAT)) {
          transformer.setParameter("is-item-list", Boolean.FALSE);
        } else {
          throw new TransformationNotSupportedException("The requested target format ("
              + trgFormat.toString() + ") is not supported");
        }

        transformer.setParameter("localPrefix",
            PropertyReader.getProperty("escidoc.transformation.mab.content.url.prefix"));
        transformer.setParameter("content-model",
            PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication"));
        transformer.setParameter("external-organization",
            PropertyReader.getProperty("escidoc.pubman.external.organisation.id"));

        transformer.setOutputProperty(OutputKeys.ENCODING, trgFormat.getEncoding());

        transformer.transform(new StreamSource(new StringReader(output)), new StreamResult(result));

      }

      return result.toString().getBytes("UTF-8");
    } catch (Exception e) {
      throw new RuntimeException("Error getting file content", e);
    }
  }

  @Override
  public byte[] transform(byte[] arg0, String arg1, String arg2, String arg3, String arg4,
      String arg5, String arg6, String arg7) throws TransformationNotSupportedException,
      RuntimeException {
    return transform(arg0, new Format(arg1, arg2, arg3), new Format(arg4, arg5, arg6), arg7);
  }

  @Override
  public Format[] getSourceFormats() throws RuntimeException {
    return null;
  }
}
