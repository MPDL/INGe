package de.mpg.mpdl.inge.pubman.web.util.handler;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class GenreHandler extends ShortContentHandler {

  private final LinkedHashMap<String, String> defaultMap = new LinkedHashMap<>();
  private Map<String, String> map = null;

  private Map<String, String> authorRoles = new LinkedHashMap<>();
  private Map<String, String> contentCategories = new LinkedHashMap<>();
  private Map<String, String> sourceGenres = new LinkedHashMap<>();

  private final Stack<String> stack = new Stack<>();

  private String dir = null;
  private String formID = "";
  private String genre = null;
  private String groupID = "";

  public GenreHandler(String dir) {
    this.dir = dir;
    File dirFile = new File(dir);
    dirFile.mkdirs();
  }

  @Override
  public void content(String uri, String localName, String name, String content) {
    super.content(uri, localName, name, content);
  }

  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    super.endElement(uri, localName, name);

    try {
      FileWriter fileWriter = null;
      if ("genre".equals(name)) {
        fileWriter = new FileWriter(this.dir + "/Genre_" + this.genre + ".properties");

        for (Map.Entry<String, String> entry : this.map.entrySet()) {
          fileWriter.append(entry.getKey().replace("-", "_"));
          fileWriter.append("=");
          fileWriter.append(entry.getValue());
          fileWriter.append("\n");
        }

        fileWriter.flush();
        fileWriter.close();
        fileWriter = null;

        this.map = null;
      } else if ("group".equals(name) || "field".equals(name)) {
        this.stack.pop();
      } else if ("content-categories".equals(name)) {
        fileWriter = new FileWriter(this.dir + "/content_categories.properties");

        for (Map.Entry<String, String> entry : this.contentCategories.entrySet()) {
          fileWriter.append(entry.getKey().toLowerCase());
          fileWriter.append("=");
          fileWriter.append(entry.getValue());
          fileWriter.append("\n");
        }

        fileWriter.flush();
        fileWriter.close();
        fileWriter = null;

        this.contentCategories = null;
      } else if ("author-roles".equals(name)) {
        fileWriter = new FileWriter(this.dir + "/author_roles.properties");

        for (Map.Entry<String, String> entry : this.authorRoles.entrySet()) {
          fileWriter.append(entry.getKey().toLowerCase());
          fileWriter.append("=");
          fileWriter.append(entry.getValue());
          fileWriter.append("\n");
        }

        fileWriter.flush();
        fileWriter.close();
        fileWriter = null;

        this.authorRoles = null;
      } else if ("source-genres".equals(name)) {
        fileWriter = new FileWriter(this.dir + "/source_genres.properties");

        for (Map.Entry<String, String> entry : this.sourceGenres.entrySet()) {
          fileWriter.append(entry.getKey().toLowerCase());
          fileWriter.append("=");
          fileWriter.append(entry.getValue());
          fileWriter.append("\n");
        }

        fileWriter.flush();
        fileWriter.close();
        fileWriter = null;

        this.sourceGenres = null;
      }
    } catch (Exception e) {
      throw new SAXException(e);
    }
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
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
        for (String element : this.stack) {
          currentStack += element + "_";
        }
        if (!attributes.getValue("id").equals(this.groupID)) {
          for (int i = 0; i < attributes.getLength(); i++) {
            String key = currentStack + attributes.getQName(i);
            String value = attributes.getValue(i);

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
        for (String element : this.stack) {
          currentStack += element + "_";
        }

        for (int i = 0; i < attributes.getLength(); i++) {
          String key = currentStack + attributes.getQName(i);
          String value = attributes.getValue(i);
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
    } catch (Exception e) {
      throw new SAXException(e);
    }
  }
}
