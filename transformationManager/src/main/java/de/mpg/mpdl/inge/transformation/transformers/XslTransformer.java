package de.mpg.mpdl.inge.transformation.transformers;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.SingleTransformer;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerResult;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerSource;
import de.mpg.mpdl.inge.util.LocalUriResolver;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ResourceUtil;

public abstract class XslTransformer extends SingleTransformer implements ChainableTransformer {
  private static final Logger logger = Logger.getLogger(XslTransformer.class);

  public XslTransformer() {
    try {
      setConfiguration(getDefaultConfiguration());
    } catch (TransformationException e) {
      throw new RuntimeException("Could not initialize transformer", e);
    }
  }

  public void transform(Source source, Result result) throws TransformationException {
    try {
      logger.info("Starting XSL transformation " + getSourceFormat() + " --> " + getTargetFormat());

      TransformerFactory xslTransformerFactory = new net.sf.saxon.TransformerFactoryImpl();

      URIResolver uriRes = getURIResolver();
      if (uriRes != null) {
        xslTransformerFactory.setURIResolver(uriRes);
      }

      javax.xml.transform.Transformer xslTransformer = xslTransformerFactory.newTransformer(getXsltSource());

      Map<String, String> outputKeys = getOutputKeys();

      if (outputKeys != null) {
        for (Entry<String, String> entry : outputKeys.entrySet()) {
          xslTransformer.setOutputProperty(entry.getKey(), entry.getValue());
        }
      }

      Map<String, Object> parameters = getParameters();
      if (parameters != null) {
        for (Entry<String, Object> entry : parameters.entrySet()) {
          if (entry.getValue() != null) {
            xslTransformer.setParameter(entry.getKey(), entry.getValue());
          } else {
          }
        }
      }

      Map<String, String> config = getConfiguration();
      if (config != null && config.entrySet() != null) {
        for (Entry<String, String> entry : config.entrySet()) {
          xslTransformer.setParameter(entry.getKey(), entry.getValue());
        }
      }

      logger.info("Using <" + this.getClass().getSimpleName() + ">");

      xslTransformer.transform(source, result);

      //      logger.info("XSL transformation successful");
    } catch (Exception e) {
      throw new TransformationException("Error during XSL Transformation", e);
    }
  }

  @Override
  public void transform(TransformerSource source, TransformerResult result) throws TransformationException {
    this.transform((Source) source, (Result) result);
  }

  public abstract Source getXsltSource() throws TransformationException;

  public abstract Map<String, Object> getParameters();

  public abstract Map<String, String> getDefaultConfiguration() throws TransformationException;

  public TransformerResult createNewInBetweenResult() {
    TransformerStreamResult tr = new TransformerStreamResult(new ByteArrayOutputStream());
    return tr;
  }

  public Map<String, String> getOutputKeys() {
    return null;
  }

  public URIResolver getURIResolver() {
    return new LocalUriResolver("transformations/thirdParty/xslt");
  }

  public Source getXmlSourceFromProperty(String property) throws TransformationException {
    String stylesheetFileName = PropertyReader.getProperty(property);
    try {
      InputStream stylesheetInputStram = ResourceUtil.getResourceAsStream(stylesheetFileName, XslTransformer.class.getClassLoader());
      return new StreamSource(stylesheetInputStram);
    } catch (FileNotFoundException e) {
      throw new TransformationException("Stylesheet file " + stylesheetFileName + " not found", e);
    }
  }

  public void xmlSourceToXmlResult(Source s, Result r) throws TransformerException {
    TransformerFactory xslTransformerFactory = new net.sf.saxon.TransformerFactoryImpl();
    Transformer t = xslTransformerFactory.newTransformer();
    t.setOutputProperty(OutputKeys.INDENT, "yes");
    t.setOutputProperty(OutputKeys.METHOD, "xml");
    t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    t.transform(s, r);
  }
}
