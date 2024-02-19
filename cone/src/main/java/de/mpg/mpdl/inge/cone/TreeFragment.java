package de.mpg.mpdl.inge.cone;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;

import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * A representation of a tree-like structure built of s-p-o triples.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@SuppressWarnings("serial")
public class TreeFragment extends LinkedHashMap<String, List<LocalizedTripleObject>> implements LocalizedTripleObject {
  private static final String REGEX_PREDICATE_REPLACE = ":/\\-\\.# ";
  private static final Pattern NAMESPACE_PATTERN = Pattern.compile("([\\S]+)(([/#])| )([^/# ]+)");
  private String subject;
  private String language;

  /**
   * Default constructor.
   */
  public TreeFragment() {}

  /**
   * Constructor with given subject.
   *
   * @param subject The subject.
   */
  public TreeFragment(String subject) {
    this.subject = subject;
  }

  /**
   * Constructor with given subject and language.
   *
   * @param subject The subject.
   * @param language The language.
   */
  public TreeFragment(String subject, String language) {
    this.subject = subject;
    this.language = language;
  }

  public String getSubject() {
    return this.subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getLanguage() {
    return this.language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  // Add predicates of other if this predicate does not exist yet, otherwise overwrite it.
  public void merge(TreeFragment other, boolean overwrite) {
    Set<String> removedPredicates = new HashSet<>();

    for (Map.Entry<String, List<LocalizedTripleObject>> entry : other.entrySet()) {
      String predicateName = entry.getKey();
      if (null != get(predicateName)) {
        for (LocalizedTripleObject otherObject : entry.getValue()) {
          if (overwrite && !removedPredicates.contains(predicateName)
              && (!(otherObject instanceof LocalizedString) || !"".equals(((LocalizedString) otherObject).getValue()))) {
            for (int i = 0; i < get(predicateName).size(); i++) {
              LocalizedTripleObject myObject = get(predicateName).get(i);
              if ((null == myObject.getLanguage() && null == otherObject.getLanguage())
                  || myObject.getLanguage().equals(otherObject.getLanguage())) {
                get(predicateName).remove(myObject);
                i--;
                removedPredicates.add(predicateName);
              }
            }
          }
          get(predicateName).add(otherObject);
        }
      } else {
        put(predicateName, entry.getValue());
      }
    }
  }

  public boolean exists() {
    return (null != this.keySet() && !keySet().isEmpty());
  }

  public boolean hasValue() {
    return (null != this.subject && !this.subject.isEmpty());
  }

  /**
   *
   *
   * @throws ConeException
   */
  public String toRdf(ModelList.Model model) throws ConeException {
    if (0 == size()) {

      return StringEscapeUtils.escapeXml10(PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + this.subject);

    } else {
      StringWriter result = new StringWriter();
      Map<String, String> namespaces = new HashMap<>();
      ModelList modelList = ModelList.getInstance();

      int counter = 0;

      result.append("<" + (null != model.getRdfAboutTag().getPrefix() ? model.getRdfAboutTag().getPrefix() + ":" : "")
          + model.getRdfAboutTag().getLocalPart());

      if (!this.subject.startsWith("genid:")) {
        try {
          result.append(" rdf:about=\"");
          result.append(PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + this.subject);
          result.append("\"");
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
      if (null != this.language && !this.language.isEmpty()) {
        result.append(" xml:lang=\"");
        result.append(this.language);
        result.append("\"");
      }
      for (String predicate : keySet()) {
        Matcher matcher = NAMESPACE_PATTERN.matcher(predicate);
        if (matcher.find()) {
          String namespace = matcher.group(1) + (null == matcher.group(3) ? "" : matcher.group(3));
          if (!namespaces.containsKey(namespace)) {
            String prefix;
            if (modelList.getDefaultNamepaces().containsKey(namespace)) {
              prefix = modelList.getDefaultNamepaces().get(namespace);
            } else {
              counter++;
              prefix = "ns" + counter;
            }
            namespaces.put(namespace, prefix);
            result.append(" xmlns:" + prefix + "=\"" + namespace + "\"");
          }
        }
      }
      result.append(">\n");
      for (String predicate : keySet()) {
        Matcher matcher = NAMESPACE_PATTERN.matcher(predicate);
        String namespace = null;
        String tagName = null;
        String prefix = null;
        if (matcher.find()) {
          namespace = matcher.group(1) + (null == matcher.group(3) ? "" : matcher.group(3));
          prefix = namespaces.get(namespace);
          tagName = matcher.group(4);
        } else {
          int lastColon = predicate.lastIndexOf(":");
          tagName = predicate.substring(lastColon + 1);
        }
        List<LocalizedTripleObject> values = get(predicate);
        for (LocalizedTripleObject value : values) {
          result.append("<");
          if (null != namespace) {
            result.append(prefix);
            result.append(":");
          }
          result.append(tagName);
          if (null != value.getLanguage() && !"".equals(value.getLanguage())) {
            result.append(" xml:lang=\"");
            result.append(value.getLanguage());
            result.append("\"");
          }


          ModelList.Predicate p = model.getPredicate(predicate);

          // display links to other resources as rdf:resource attribute, if includeResource is false

          if (null != p && null != p.getResourceModel() && !p.isIncludeResource()) {
            String url = value.toString();
            if (!(url.startsWith("http://") || url.startsWith("https://") || url.startsWith("ftp:"))) {
              try {
                if (value.toString().startsWith("/")) {
                  url = PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + url.substring(0, url.length() - 1);
                } else {
                  url = PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + url;
                }
              } catch (Exception e) {
                throw new RuntimeException(e);
              }

            }

            result.append(" rdf:resource=\"" + url + "\"/>");
          }

          else {

            result.append(">");
            result.append(value.toRdf(model));

            result.append("</");
            if (null != namespace) {
              result.append(prefix);
              result.append(":");
            }
            result.append(tagName);
            result.append(">\n");
          }


        }
      }
      result.append("</" + (null != model.getRdfAboutTag().getPrefix() ? model.getRdfAboutTag().getPrefix() + ":" : "")
          + model.getRdfAboutTag().getLocalPart() + ">\n");
      return result.toString();
    }
  }

  public String toJson() {
    if (0 == size()) {
      try {
        return "\"" + PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + this.subject.replace("\"", "\\\"") + "\"";
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else {
      StringWriter writer = new StringWriter();
      writer.append("{\n");
      if (!this.subject.startsWith("genid:")) {
        writer.append("\"id\" : \"");
        try {
          writer.append(PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + this.subject.replace("\"", "\\\""));
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
        writer.append("\",\n");
      }
      for (Iterator<String> iterator = keySet().iterator(); iterator.hasNext();) {
        String key = iterator.next();
        writer.append("\"");
        writer.append(key.replaceAll("[" + REGEX_PREDICATE_REPLACE + "]+", "_").replace("\"", "\\\""));
        writer.append("\" : ");
        if (1 == get(key).size()) {
          writer.append(get(key).get(0).toJson());
        } else {
          writer.append("[\n");
          for (Iterator<LocalizedTripleObject> iterator2 = get(key).iterator(); iterator2.hasNext();) {
            LocalizedTripleObject object = iterator2.next();
            writer.append(object.toJson());
            if (iterator2.hasNext()) {
              writer.append(",");
            }
            writer.append("\n");
          }
          writer.append("]");
        }
        if (iterator.hasNext()) {
          writer.append(",\n");
        }
      }
      writer.append("\n}\n");
      return writer.toString();
    }
  }

  @Override
  public String toString() {

    if (null == this.subject) {
      return null;
    }
    try {
      return this.subject;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public String toString2() {

    return super.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (null == o) {
      return false;
    } else if (!(o instanceof TreeFragment)) {
      return false;
    } else if (null == this.language && null != ((TreeFragment) o).getLanguage()) {
      return false;
    } else if (null != this.language && !this.language.equals(((TreeFragment) o).getLanguage())) {
      return false;
    } else if (null == this.subject && null != ((TreeFragment) o).getSubject()) {
      return false;
    } else if (null != this.subject && !this.subject.equals(((TreeFragment) o).getSubject())) {
      return false;
    } else {
      return super.equals(o);
    }
  }
}
