package de.mpg.mpdl.inge.transformation;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.util.SourceTargetPair;

public class TransformerCache {

  private static Logger logger = Logger.getLogger(TransformerCache.class);

  // Map holding the transformers
  // key: Pair source format - target format, value: Transformer object
  private static Map<SourceTargetPair, Transformer> transformerMap =
      new HashMap<SourceTargetPair, Transformer>();

  // Map holding a List of target FORMATS
  // key: source format, value: Array of FORMAT objects containing all reachable target formats
  private static Map<FORMAT, FORMAT[]> targetFormatsMap = new HashMap<FORMAT, FORMAT[]>();

  // Map holding a List of source FORMATS
  // key: target format, value: Array of FORMAT objects containing all source formats
  private static Map<FORMAT, FORMAT[]> sourceFormatsMap = new HashMap<FORMAT, FORMAT[]>();

  private TransformerCache() {}

  public static TransformerCache getInstance() {
    return TransformerCacheHolder.instance;
  }

  public static Transformer getTransformer(FORMAT sourceFormat, FORMAT targetFormat)
      throws TransformationException {

    synchronized (transformerMap) {
      Transformer t = transformerMap.get(new SourceTargetPair(sourceFormat, targetFormat));

      if (t == null) {
        t = TransformerFactory.newInstance(sourceFormat, targetFormat);

        if (t != null) {
          transformerMap.put(new SourceTargetPair(sourceFormat, targetFormat), t);
        }
      }
      return t;
    }
  }

  public static FORMAT[] getAllTargetFormatsFor(FORMAT sourceFormat) {

    synchronized (targetFormatsMap) {
      FORMAT[] targetFormats = targetFormatsMap.get(sourceFormat);

      if (targetFormats == null) {
        targetFormats = TransformerFactory.getAllTargetFormatsFor(sourceFormat);

        if (targetFormats == null)
          return null;
      }

      targetFormatsMap.put(sourceFormat, targetFormats);

      return targetFormats;
    }
  }

  public static FORMAT[] getAllSourceFormatsFor(FORMAT targetFormat) {

    synchronized (sourceFormatsMap) {
      FORMAT[] sourceFormats = sourceFormatsMap.get(targetFormat);

      if (sourceFormats == null) {
        sourceFormats = TransformerFactory.getAllSourceFormatsFor(targetFormat);

        if (sourceFormats == null)
          return null;
      }

      sourceFormatsMap.put(targetFormat, sourceFormats);

      return sourceFormats;
    }
  }

  // for testing purposes
  int getTransformerCacheSize() {
    return transformerMap.size();
  }

  private static class TransformerCacheHolder {
    private static final TransformerCache instance = new TransformerCache();
  }
}
