package de.mpg.mpdl.inge.transformation;

import java.util.HashMap;
import java.util.Map;

import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.util.SourceTargetPair;

public class TransformerCache {
  // Map holding the transformers
  // key: Pair source format - target format, value: Transformer object
  private static Map<SourceTargetPair, Transformer> transformerMap =
      new HashMap<SourceTargetPair, Transformer>();

  // Map holding a List of target FORMATS
  // key: source format, value: Array of FORMAT objects containing all reachable target formats
  private static Map<TransformerFactory.FORMAT, TransformerFactory.FORMAT[]> targetFormatsMap =
      new HashMap<TransformerFactory.FORMAT, TransformerFactory.FORMAT[]>();

  // Map holding a List of source FORMATS
  // key: target format, value: Array of FORMAT objects containing all source formats
  private static Map<TransformerFactory.FORMAT, TransformerFactory.FORMAT[]> sourceFormatsMap =
      new HashMap<TransformerFactory.FORMAT, TransformerFactory.FORMAT[]>();

  private TransformerCache() {}

  public static TransformerCache getInstance() {
    return TransformerCacheHolder.instance;
  }

  public static Transformer getTransformer(TransformerFactory.FORMAT sourceFormat,
      TransformerFactory.FORMAT targetFormat) throws TransformationException {

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

  public static TransformerFactory.FORMAT[] getAllTargetFormatsFor(
      TransformerFactory.FORMAT sourceFormat) {

    synchronized (targetFormatsMap) {
      TransformerFactory.FORMAT[] targetFormats = targetFormatsMap.get(sourceFormat);

      if (targetFormats == null) {
        targetFormats = TransformerFactory.getAllTargetFormatsFor(sourceFormat);

        if (targetFormats == null)
          return null;
      }

      targetFormatsMap.put(sourceFormat, targetFormats);

      return targetFormats;
    }
  }

  public static TransformerFactory.FORMAT[] getAllSourceFormatsFor(
      TransformerFactory.FORMAT targetFormat) {

    synchronized (sourceFormatsMap) {
      TransformerFactory.FORMAT[] sourceFormats = sourceFormatsMap.get(targetFormat);

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
