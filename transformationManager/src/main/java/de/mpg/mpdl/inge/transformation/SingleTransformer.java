package de.mpg.mpdl.inge.transformation;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerResult;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerSource;
import de.mpg.mpdl.inge.transformation.sources.TransformerStreamSource;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ResourceUtil;

public abstract class SingleTransformer implements Transformer {

  private static final Logger logger = Logger.getLogger(SingleTransformer.class);

  private TransformerFactory.FORMAT sourceFormat;

  private TransformerFactory.FORMAT targetFormat;

  private Map<String, String> configuration = new HashMap<>();


  public Map<String, String> getConfiguration() {
    logger.debug("Singletransformer");
    if (this.configuration != null && this.configuration.entrySet() != null) {
      for (Entry<String, String> entry : this.configuration.entrySet()) {
        logger.debug("Transformation parameter from configuration " + entry.getKey() + " -- " + entry.getValue());
      }
    }

    return this.configuration;
  }

  public void setConfiguration(Map<String, String> configuration) {
    this.configuration = configuration;
  }

  public void mergeConfiguration(Map<String, String> givenConfiguration) {
    if (givenConfiguration != null) {
      if (this.configuration != null) {
        this.configuration.putAll(givenConfiguration);
      } else {
        this.configuration = new HashMap<>();
        this.configuration.putAll(givenConfiguration);
      }
    }
  }

  public TransformerFactory.FORMAT getTargetFormat() {
    return this.targetFormat;
  }

  public void setTargetFormat(TransformerFactory.FORMAT targetFormat) {
    this.targetFormat = targetFormat;
  }

  public TransformerFactory.FORMAT getSourceFormat() {
    return this.sourceFormat;
  }

  public void setSourceFormat(TransformerFactory.FORMAT sourceFormat) {
    this.sourceFormat = sourceFormat;
  }

  protected static Map<String, String> getDefaultConfigurationFromProperty(String property) throws TransformationException {
    String propertyFileName = PropertyReader.getProperty(property);

    if (propertyFileName == null) {
      logger.warn("No property configuration file found for transformer. Property " + property + " not set.");
      return null;
    } else {
      try {
        Map<String, String> config = new HashMap<String, String>();
        InputStream propertyInputStram = ResourceUtil.getResourceAsStream(propertyFileName, SingleTransformer.class.getClassLoader());
        Properties props = new Properties();
        props.load(propertyInputStram);
        propertyInputStram.close();
        String[] defaultConfValues = props.getProperty("configuration").split(",");
        for (String field : defaultConfValues) {
          String[] fieldArr = field.split("=", 2);
          config.put(fieldArr[0], fieldArr[1] == null ? "" : fieldArr[1]);
        }

        return config;
      } catch (Exception e) {
        throw new TransformationException("Error while XML transformation configuration file " + propertyFileName, e);
      }
    }
  }

  protected static Map<String, List<String>> getAllConfigurationValuesFromProperty(String property) throws TransformationException {
    String propertyFileName = PropertyReader.getProperty(property);

    if (propertyFileName == null) {
      logger.warn("No property configuration file found for transformer. Property " + property + " not set.");
      return null;
    } else {
      try {
        Map<String, List<String>> properties = new HashMap<String, List<String>>();
        InputStream propertyInputStram = ResourceUtil.getResourceAsStream(propertyFileName, SingleTransformer.class.getClassLoader());
        Properties props = new Properties();
        props.load(propertyInputStram);
        propertyInputStram.close();

        for (Object key : props.keySet()) {

          if (key.equals("configuration"))
            continue;

          String[] values = props.getProperty(key.toString()).split(",");
          properties.put(key.toString(), Arrays.asList(values));
        }

        return properties;
      } catch (Exception e) {
        throw new TransformationException("Error while XML transformation configuration file " + propertyFileName, e);
      }
    }
  }

  public static String getStringFromSource(TransformerSource transformerSource) throws TransformationException {
    TransformerStreamSource s;
    try {
      s = (TransformerStreamSource) transformerSource;
    } catch (Exception e) {
      throw new TransformationException("Wrong source type, expected a TransformerStreamSource", e);
    }

    Scanner scanner;

    if (s.getInputStream() != null) {
      scanner = new Scanner(s.getInputStream(), StandardCharsets.UTF_8);

    } else if (s.getReader() != null) {
      scanner = new Scanner(s.getReader());
    } else {
      throw new TransformationException("The source does not contain a input stream or a reader");
    }

    String ret = scanner.useDelimiter("\\Z").next();
    scanner.close();

    return ret;
  }

  public static void writeStringToStreamResult(String s, TransformerResult transformerRes) throws TransformationException {
    TransformerStreamResult res;
    try {
      res = (TransformerStreamResult) transformerRes;
    } catch (Exception e1) {
      throw new TransformationException("Wrong result type, expected a TransformerStreamResult", e1);
    }

    if (res.getOutputStream() != null) {
      try {
        res.getOutputStream().write(s.getBytes(StandardCharsets.UTF_8));
        res.getOutputStream().close();
      } catch (IOException e) {
        throw new TransformationException("Could not write to output stream", e);
      }
    } else if (res.getWriter() != null) {
      try {
        res.getWriter().write(s);
        res.getWriter().close();
      } catch (IOException e) {
        throw new TransformationException("Could not write to writer", e);
      }
    } else {
      throw new TransformationException("The result does not contain an output stream or a writer");
    }
  }

  public static void writeByteArrayToStreamResult(byte[] content, TransformerResult transformerRes) throws TransformationException {
    TransformerStreamResult res;
    try {
      res = (TransformerStreamResult) transformerRes;
    } catch (Exception e1) {
      throw new TransformationException("Wrong result type, expected a TransformerStreamResult", e1);
    }

    if (res.getOutputStream() != null) {
      try {
        res.getOutputStream().write(content);
        res.getOutputStream().close();
      } catch (IOException e) {
        throw new TransformationException("Could not write to output stream", e);
      }

    } else if (res.getWriter() != null) {
      try {
        res.getWriter().write(new String(content));
        res.getWriter().close();
      } catch (IOException e) {
        throw new TransformationException("Could not write to writer", e);
      }
    } else {
      throw new TransformationException("The result does not contain an output stream or a writer");
    }
  }

  public List<String> getAllConfigurationValuesFor(String key) throws TransformationException {
    return new ArrayList<String>();
  }


  public String toString() {
    return super.toString() + " (<" + getSourceFormat() + "> to <" + getTargetFormat() + ">)";
  }

}
