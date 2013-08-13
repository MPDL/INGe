package de.mpg.escidoc.pubman.init;

import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.mpg.escidoc.pubman.util.ShortContentHandler;

public class GenreHandler extends ShortContentHandler 
{

    private String genre = null;
    private FileWriter fileWriter = null;
    private Stack<String> stack = new Stack<String>();
    private String dir = null;
    private Map<String, String> contentCategories = new LinkedHashMap<String, String>();
    private Map<String, String> authorRoles = new LinkedHashMap<String, String>();
    private Map<String, String> sourceGenres = new LinkedHashMap<String, String>();
    
    private LinkedHashMap<String, String> map = null;
    private LinkedHashMap<String, String> defaultMap = new LinkedHashMap<String, String>();
    
    String formID = "";
    String groupID = "";
    
    public GenreHandler(String dir)
    {
        this.dir = dir;
    }
    
    @Override
    public void content(String uri, String localName, String name,
            String content) {
        // TODO Auto-generated method stub
        super.content(uri, localName, name, content);
    }

    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        // TODO Auto-generated method stub
        super.endElement(uri, localName, name);
        
        try
        {
            if ("genre".equals(name))
            {
                fileWriter = new FileWriter(dir + "/Genre_" + genre + ".properties");
                
                for (String key : map.keySet())
                {
                    fileWriter.append(key.replace("-", "_"));
                    fileWriter.append("=");
                    fileWriter.append(map.get(key));
                    fileWriter.append("\n");
                }
                
                fileWriter.flush();
                fileWriter.close();
                fileWriter = null;
                
                map = null;
            }
            else if ("group".equals(name) || "field".equals(name))
            {
                stack.pop();
            }
            else if ("content-categories".equals(name))
            {
                fileWriter = new FileWriter(dir + "/content_categories.properties");
                
                for (String key : contentCategories.keySet())
                {
                    fileWriter.append(key.toLowerCase());
                    fileWriter.append("=");
                    fileWriter.append(contentCategories.get(key));
                    fileWriter.append("\n");
                }
                
                fileWriter.flush();
                fileWriter.close();
                fileWriter = null;
                
                contentCategories = null;
            }
            else if ("author-roles".equals(name))
            {
                fileWriter = new FileWriter(dir + "/author_roles.properties");
                
                for (String key : authorRoles.keySet())
                {
                    fileWriter.append(key.toLowerCase());
                    fileWriter.append("=");
                    fileWriter.append(authorRoles.get(key));
                    fileWriter.append("\n");
                }
                
                fileWriter.flush();
                fileWriter.close();
                fileWriter = null;
                
                authorRoles = null;
            }
            else if ("source-genres".equals(name))
            {
                fileWriter = new FileWriter(dir + "/source_genres.properties");
                
                for (String key : sourceGenres.keySet())
                {
                    fileWriter.append(key.toLowerCase());
                    fileWriter.append("=");
                    fileWriter.append(sourceGenres.get(key));
                    fileWriter.append("\n");
                }
                
                fileWriter.flush();
                fileWriter.close();
                fileWriter = null;
                
                sourceGenres = null;
            }
            else if ("group".equals(name) || "field".equals(name))
            {
                stack.pop();
            }
        }
        catch (Exception e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {
        // TODO Auto-generated method stub
        super.startElement(uri, localName, name, attributes);
        
        try
        {
            if ("genre-default".equals(name))
            {
                map = defaultMap;
            }
            else if ("genre".equals(name))
            {
                genre = attributes.getValue("id");
                if ("DEFAULT".equals(genre))
                {
                    map = defaultMap;
                }
                else
                {
                    map = (LinkedHashMap<String, String>) defaultMap.clone();
                }
            }
            else if ("group".equals(name))
            {
                stack.push(attributes.getValue("id"));
                String currentStack = "";
                for (String element : stack) {
                    currentStack += element + "_";
                }
                if(!attributes.getValue("id").equals(this.groupID))
                {
                    for (int i = 0; i < attributes.getLength(); i++) 
                    {
                        String key = currentStack + attributes.getQName(i);
                        String value = attributes.getValue(i);
                        
                        map.put(key, value);
                        
                        if("form-id".equals(attributes.getQName(i)))
                        {
                            this.formID = value;
                        }
                    }
                }
                this.groupID = attributes.getValue("id");
                this.formID = attributes.getValue("form-id");
            }
            else if ("field".equals(name))
            {
                stack.push(attributes.getValue("id"));
                String currentStack = "";
                for (String element : stack) {
                    currentStack += element + "_";
                }
                
                for (int i = 0; i < attributes.getLength(); i++) {
                    String key = currentStack + attributes.getQName(i);
                    String value = attributes.getValue(i);
                    map.put(key, value);
                }
                map.put(currentStack + "form-id", this.formID);
            }
            else if ("content-category".equals(name))
            {
            	contentCategories.put(attributes.getValue("id"), attributes.getValue("url"));
            }
            else if ("role".equals(name))
            {
                authorRoles.put(attributes.getValue("id"), attributes.getValue("url"));
            }
            else if ("source-genre".equals(name))
            {
                sourceGenres.put(attributes.getValue("id"), attributes.getValue("url"));
            }
        }
        catch (Exception e) {
            throw new SAXException(e);
        }
    }
    

}
