package de.mpg.mpdl.inge.transformation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.util.SourceTargetPair;

public class TransformerCache {
  // Map holding the transformers
  // key: Pair source format - target format, value: Transformer object
  private static final Map<SourceTargetPair, List<TransformerEdge>> transformerMap = new HashMap<>();

  // Map holding a List of target FORMATS
  // key: source format, value: Array of FORMAT objects containing all reachable target formats
  private static final Map<TransformerFactory.FORMAT, TransformerFactory.FORMAT[]> targetFormatsMap = new HashMap<>();

  // Map holding a List of source FORMATS
  // key: target format, value: Array of FORMAT objects containing all source formats
  private static final Map<TransformerFactory.FORMAT, TransformerFactory.FORMAT[]> sourceFormatsMap = new HashMap<>();

  private TransformerCache() {}

  public static TransformerCache getInstance() {
    return TransformerCacheHolder.instance;
  }

  protected static List<TransformerEdge> getTransformerEdges(TransformerFactory.FORMAT sourceFormat,
      TransformerFactory.FORMAT targetFormat) {

    synchronized (transformerMap) {
      List<TransformerEdge> t = transformerMap.get(new SourceTargetPair(sourceFormat, targetFormat));

      if (t == null) {
        t = TransformerFactory.getShortestPath(sourceFormat, targetFormat);

        if (t != null) {
          transformerMap.put(new SourceTargetPair(sourceFormat, targetFormat), t);
        }
      }
      return t;
    }
  }

  protected static boolean isTransformationExisting(FORMAT sourceFormat, FORMAT targetFormat) {

    if (sourceFormat.equals(targetFormat)) {
      return true;
    }

    synchronized (transformerMap) {

      List<TransformerEdge> t = null;
      if ((t = transformerMap.get(new SourceTargetPair(sourceFormat, targetFormat))) != null)
        return true;

      if (t == null) {
        t = TransformerFactory.getShortestPath(sourceFormat, targetFormat);

        if (t != null) {
          transformerMap.put(new SourceTargetPair(sourceFormat, targetFormat), t);
          return true;
        }
      }
    }

    return false;
  }


  protected static TransformerFactory.FORMAT[] getAllTargetFormatsFor(TransformerFactory.FORMAT sourceFormat) {

    synchronized (targetFormatsMap) {
      TransformerFactory.FORMAT[] targetFormats = targetFormatsMap.get(sourceFormat);

      if (targetFormats == null) {
        targetFormats = TransformerFactory.findAllTargetFormats(sourceFormat);

        if (targetFormats == null)
          return null;
      }

      targetFormatsMap.put(sourceFormat, targetFormats);

      return targetFormats;
    }
  }

  protected static TransformerFactory.FORMAT[] getAllSourceFormatsFor(TransformerFactory.FORMAT targetFormat) {

    synchronized (sourceFormatsMap) {
      TransformerFactory.FORMAT[] sourceFormats = sourceFormatsMap.get(targetFormat);

      if (sourceFormats == null) {
        sourceFormats = TransformerFactory.findAllSourceFormats(targetFormat);

        if (sourceFormats == null)
          return null;
      }

      sourceFormatsMap.put(targetFormat, sourceFormats);

      return sourceFormats;
    }
  }

  // for testing purposes
  static int getTransformerCacheSize() {
    return transformerMap.size();
  }

  public static void clear() {
    transformerMap.clear();

  }

  private static class TransformerCacheHolder {
    private static final TransformerCache instance = new TransformerCache();
  }


}
