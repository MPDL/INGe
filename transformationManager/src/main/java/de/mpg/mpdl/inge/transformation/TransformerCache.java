package de.mpg.mpdl.inge.transformation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.util.SourceTargetPair;

public class TransformerCache {
  // Map holding the transformers
  // key: Pair source format - target format, value: Transformer object
  private static Map<SourceTargetPair, List<TransformerEdge>> transformerMap = new HashMap<SourceTargetPair, List<TransformerEdge>>();

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

  protected static List<TransformerEdge> getTransformerEdges(TransformerFactory.FORMAT sourceFormat, TransformerFactory.FORMAT targetFormat)
      throws TransformationException {

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
        try {
          t = TransformerFactory.getShortestPath(sourceFormat, targetFormat);
        } catch (TransformationException e) {
          return false;
        }

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
