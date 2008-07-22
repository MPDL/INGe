package de.mpg.escidoc.services.cone;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.mpg.escidoc.services.common.util.ResourceUtil;

public class ServiceList
{
    private static ServiceList instance = null;
    private static final Logger logger = Logger.getLogger(ServiceList.class);
    
    private Set<Service> list = new HashSet<Service>();
    
    private ServiceList()
    {
        try
        {
            InputStream in = ResourceUtil.getResourceAsStream("explain/services.xml");
            ServiceListHandler listHandler = new ServiceListHandler();
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(in, listHandler);
            list = listHandler.getList();
            
            logger.debug("Length: " + list.size());
            for (Service service : list)
            {
                logger.debug("Service:" + service.getName());
            }
            
        }
        catch (FileNotFoundException e) {
            logger.error("Error reading services list", e);
        }
        catch (ParserConfigurationException e) {
            logger.error("Error configuring SAX parser", e);
        }
        catch (SAXException e) {
            logger.error("Error parsing services list", e);
        }
        catch (IOException e) {
            logger.error("Error parsing services list", e);
        }
    }
    
    public static ServiceList getInstance()
    {
        if (instance == null)
        {
            instance = new ServiceList();
        }
        return instance;
    }
    
    public Set<Service> getList()
    {
        return list;
    }
    
    private class ServiceListHandler extends ShortContentHandler
    {
        private Set<Service> list = new HashSet<Service>();
        private Service currentService = null;
        
        @Override
        public void content(String uri, String localName, String name, String content)
        {
            if ("services/service/name".equals(stack.toString()))
            {
                currentService.setName(content);
            }
            else if ("services/service/description".equals(stack.toString()))
            {
                currentService.setDescription(content);
            }
            
            
        }

        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
        {
            super.startElement(uri, localName, name, attributes);
            if ("services/service".equals(stack.toString()))
            {
                currentService = new Service();
            }
        }
        
        @Override
        public void endElement(String uri, String localName, String name) throws SAXException
        {
            if ("services/service".equals(stack.toString()))
            {
                list.add(currentService);
            }
            super.endElement(uri, localName, name);
        }

        public Set<Service> getList()
        {
            return list;
        }
        
    }

    private class ShortContentHandler extends DefaultHandler
    {

        private StringBuffer currentContent;
        protected XMLStack stack = new XMLStack();

        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
        {
            stack.push(name);
            currentContent = new StringBuffer();
        }
        
        @Override
        public void endElement(String uri, String localName, String name) throws SAXException
        {
            content(uri, localName, name, currentContent.toString());
            stack.pop();
        }

        @Override
        public final void characters(char[] ch, int start, int length) throws SAXException
        {
            currentContent.append(ch, start, length);
        }
        
        public void content(String uri, String localName, String name, String content)
        {
            // Do nothing by default
        }

        private class XMLStack extends Stack<String>
        {

            @Override
            public synchronized String toString()
            {
                StringWriter writer = new StringWriter();
                for (Iterator<String> iterator = this.iterator(); iterator.hasNext();)
                {
                    String element = (String)iterator.next();
                    writer.append(element);
                    if (iterator.hasNext())
                    {
                        writer.append("/");
                    }
                }
                return writer.toString();
            }
            
        }
    }
    
    public class Service
    {
        private String name;
        private String description;
        
        public Service()
        {
            
        }
        
        public Service(String name)
        {
            this.name = name;
        }
        
        public Service(String name, String description)
        {
            this.name = name;
            this.description = description;
        }
        
        public String getName()
        {
            return name;
        }
        
        public void setName(String name)
        {
            this.name = name;
        }
        
        public String getDescription()
        {
            return description;
        }
        
        public void setDescription(String description)
        {
            this.description = description;
        }
        
        @Override
        public boolean equals(Object object)
        {
            if (object instanceof Service)
            {
                if (object == null)
                {
                    return false;
                }
                else if (((Service)object).name == null)
                {
                    return (this.name == null);
                }
                else
                {
                    return (((Service)object).name.equals(this.name));
                }
            }
            else
            {
                return false;
            }
        }
        
        @Override
        public int hashCode()
        {
            return (this.name == null ? 0 : this.name.hashCode());
        }
    }

    
    
    

}
