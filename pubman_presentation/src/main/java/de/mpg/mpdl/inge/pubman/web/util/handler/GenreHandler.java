package de.mpg.mpdl.inge.pubman.web.util.handler;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class GenreHandler extends ShortContentHandler {

  private FileWriter fileWriter = null;

  private final LinkedHashMap<String, String> defaultMap = new LinkedHashMap<String, String>();
  private LinkedHashMap<String, String> map = null;

  private Map<String, String> authorRoles = new LinkedHashMap<String, String>();
  private Map<String, String> contentCategories = new LinkedHashMap<String, String>();
  private Map<String, String> sourceGenres = new LinkedHashMap<String, String>();

  private final Stack<String> stack = new Stack<String>();

  private String dir = null;
  private String formID = "";
  private String genre = null;
  private String groupID = "";

  public GenreHandler(String dir) {
    this.dir = dir;
    final File dirFile = new File(dir);
    dirFile.mkdirs();
  }

  @Override
  public void content(String uri, String localName, String name, String content) {
    // TODO Auto-generated method stub
    super.content(uri, localName, name, content);
  }

  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    // TODO Auto-generated method stub
    super.endElement(uri, localName, name);

    try {
      if ("genre".equals(name)) {
        this.fileWriter = new FileWriter(this.dir + "/Genre_" + this.genre + ".properties");

        for (final String key : this.map.keySet()) {
          this.fileWriter.append(key.replace("-", "_"));
          this.fileWriter.append("=");
          this.fileWriter.append(this.map.get(key));
          this.fileWriter.append("\n");
        }

        this.fileWriter.flush();
        this.fileWriter.close();
        this.fileWriter = null;

        this.map = null;
      } else if ("group".equals(name) || "field".equals(name)) {
        this.stack.pop();
      } else if ("content-categories".equals(name)) {
        this.fileWriter = new FileWriter(this.dir + "/content_categories.properties");

        for (final String key : this.contentCategories.keySet()) {
          this.fileWriter.append(key.toLowerCase());
          this.fileWriter.append("=");
          this.fileWriter.append(this.contentCategories.get(key));
          this.fileWriter.append("\n");
        }

        this.fileWriter.flush();
        this.fileWriter.close();
        this.fileWriter = null;

        this.contentCategories = null;
      } else if ("author-roles".equals(name)) {
        this.fileWriter = new FileWriter(this.dir + "/author_roles.properties");

        for (final String key : this.authorRoles.keySet()) {
          this.fileWriter.append(key.toLowerCase());
          this.fileWriter.append("=");
          this.fileWriter.append(this.authorRoles.get(key));
          this.fileWriter.append("\n");
        }

        this.fileWriter.flush();
        this.fileWriter.close();
        this.fileWriter = null;

        this.authorRoles = null;
      } else if ("source-genres".equals(name)) {
        this.fileWriter = new FileWriter(this.dir + "/source_genres.properties");

        for (final String key : this.sourceGenres.keySet()) {
          this.fileWriter.append(key.toLowerCase());
          this.fileWriter.append("=");
          this.fileWriter.append(this.sourceGenres.get(key));
          this.fileWriter.append("\n");
        }

        this.fileWriter.flush();
        this.fileWriter.close();
        this.fileWriter = null;

        this.sourceGenres = null;
      } else if ("group".equals(name) || "field".equals(name)) {
        this.stack.pop();
      }
    } catch (final Exception e) {
      throw new SAXException(e);
    }
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes)
      throws SAXException {
    // TODO Auto-generated method stub
    super.startElement(uri, localName, name, attributes);

    try {
      if ("genre-default".equals(name)) {
        this.map = this.defaultMap;
      } else if ("genre".equals(name)) {
        this.genre = attributes.getValue("id");
        if ("DEFAULT".equals(this.genre)) {
          this.map = this.defaultMap;
        } else {
          this.map = (LinkedHashMap<String, String>) this.defaultMap.clone();
        }
      } else if ("group".equals(name)) {
        this.stack.push(attributes.getValue("id"));
        String currentStack = "";
        for (final String element : this.stack) {
          currentStack += element + "_";
        }
        if (!attributes.getValue("id").equals(this.groupID)) {
          for (int i = 0; i < attributes.getLength(); i++) {
            final String key = currentStack + attributes.getQName(i);
            final String value = attributes.getValue(i);

            this.map.put(key, value);

            if ("form-id".equals(attributes.getQName(i))) {
              this.formID = value;
            }
          }
        }
        this.groupID = attributes.getValue("id");
        this.formID = attributes.getValue("form-id");
      } else if ("field".equals(name)) {
        this.stack.push(attributes.getValue("id"));
        String currentStack = "";
        for (final String element : this.stack) {
          currentStack += element + "_";
        }

        for (int i = 0; i < attributes.getLength(); i++) {
          final String key = currentStack + attributes.getQName(i);
          final String value = attributes.getValue(i);
          this.map.put(key, value);
        }
        this.map.put(currentStack + "form-id", this.formID);
      } else if ("content-category".equals(name)) {
        this.contentCategories.put(attributes.getValue("id"), attributes.getValue("url"));
      } else if ("role".equals(name)) {
        this.authorRoles.put(attributes.getValue("id"), attributes.getValue("url"));
      } else if ("source-genre".equals(name)) {
        this.sourceGenres.put(attributes.getValue("id"), attributes.getValue("url"));
      }
    } catch (final Exception e) {
      throw new SAXException(e);
    }
  }
}
