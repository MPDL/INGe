package org.fao.oa.ingestion.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptionCharEscapeMap;
import org.apache.xmlbeans.XmlOptions;

import de.escidoc.schemas.container.x07.ContainerDocument;

public class XBeanUtils
{
    private static XmlOptions containerOpts = null;
    private static XmlOptions itemOpts = null;
    private static XmlOptions tocOpts = null;
    private static XmlOptions modsOpts = null;
    private static XmlOptions mabOpts = null;
    private static XmlOptions pubmanOpts = null;
    private static XmlOptions foxmlOpts = null;
    private static XmlOptions fileOpts = null;
    private static XmlOptions agrisOpts = null;
    private static XmlOptions defaultOpts = null;


    private static boolean valid = false;
    
    public static XmlOptions getContainerOpts()
    {
        containerOpts = new XmlOptions();
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("http://purl.org/escidoc/metadata/profiles/0.1/virrelement", "virrelement");
        namespaces.put("http://www.escidoc.de/schemas/container/0.7", "container");
        namespaces.put("http://escidoc.de/core/01/properties/", "prop");
        namespaces.put("http://escidoc.de/core/01/structural-relations/", "srel");
        namespaces.put("http://www.escidoc.de/schemas/metadatarecords/0.4", "md-records");
        namespaces.put("http://purl.org/dc/elements/1.1/", "dc");
        namespaces.put("http://www.w3.org/1999/xlink", "xlink");
        namespaces.put("http://www.escidoc.de/schemas/structmap/0.4", "structmap");
        namespaces.put("http://www.loc.gov/mods/v3", "mods");
        containerOpts.setSavePrettyPrint();
        containerOpts.setSavePrettyPrintIndent(4);
        containerOpts.setSaveAggressiveNamespaces();
        containerOpts.setSaveSuggestedPrefixes(namespaces);
        containerOpts.setUseDefaultNamespace();
        return containerOpts;
    }
    
    public static XmlOptions getItemOpts()
    {
        itemOpts = new XmlOptions();
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("http://purl.org/escidoc/metadata/profiles/0.1/virrelement", "virrelement");
        namespaces.put("http://www.escidoc.de/schemas/item/0.8", "item");
        namespaces.put("http://escidoc.de/core/01/properties/", "prop");
        namespaces.put("http://escidoc.de/core/01/structural-relations/", "srel");
        namespaces.put("http://www.escidoc.de/schemas/metadatarecords/0.4", "md-records");
        namespaces.put("http://purl.org/dc/elements/1.1/", "dc");
        namespaces.put("http://www.w3.org/1999/xlink", "xlink");
        namespaces.put("http://www.escidoc.de/schemas/components/0.8", "components");
        namespaces.put("http://purl.org/escidoc/metadata/profiles/0.1/file", "file");
        namespaces.put("http://www.loc.gov/mods/v3", "mods");
        itemOpts.setSavePrettyPrint();
        itemOpts.setSavePrettyPrintIndent(4);
        itemOpts.setSaveAggressiveNamespaces();
        itemOpts.setSaveSuggestedPrefixes(namespaces);
        itemOpts.setUseDefaultNamespace();
        return itemOpts;
    }
    
    public static XmlOptions getTOCOpts()
    {
        tocOpts = new XmlOptions();
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("http://www.w3.org/1999/xlink", "xlink");
        tocOpts.setSavePrettyPrint();
        tocOpts.setSavePrettyPrintIndent(4);
        tocOpts.setSaveAggressiveNamespaces();
        tocOpts.setSaveSuggestedPrefixes(namespaces);
        tocOpts.setUseDefaultNamespace();
        return tocOpts;
    }
    
    public static XmlOptions getModsOpts()
    {
        modsOpts = new XmlOptions();
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("http://www.w3.org/1999/xlink", "xlink");
        modsOpts.setSavePrettyPrint();
        modsOpts.setSavePrettyPrintIndent(4);
        modsOpts.setSaveAggressiveNamespaces();
        modsOpts.setSaveSuggestedPrefixes(namespaces);
        modsOpts.setUseDefaultNamespace();
        return modsOpts;
    }
    
    public static XmlOptions getAgrisOpts()
    {
        agrisOpts = new XmlOptions();
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("http://purl.org/agmes/1.1/", "ags");
        namespaces.put("http://www.w3.org/1999/xlink", "xlink");

        agrisOpts.setSavePrettyPrint();
        agrisOpts.setSavePrettyPrintIndent(4);
        agrisOpts.setSaveAggressiveNamespaces();
        agrisOpts.setSaveSuggestedPrefixes(namespaces);
        agrisOpts.setUseDefaultNamespace();
        return agrisOpts;
    }
    
    public static XmlOptions getDefaultOpts()
    {
        defaultOpts = new XmlOptions();
        defaultOpts.setSavePrettyPrint();
        defaultOpts.setSavePrettyPrintIndent(4);
        return defaultOpts;
    }
    
    public static XmlOptions getMABOpts()
    {
        mabOpts = new XmlOptions();
        mabOpts.setSavePrettyPrint();
        mabOpts.setSavePrettyPrintIndent(4);
        mabOpts.setUseDefaultNamespace();
        mabOpts.setUseCDataBookmarks();
        return mabOpts;
    }
    
    public static XmlOptions getPubmanOpts()
    {
        pubmanOpts = new XmlOptions();
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("http://escidoc.mpg.de/metadataprofile/schema/0.1/publication", "publication");
        namespaces.put("http://escidoc.mpg.de/metadataprofile/schema/0.1/types", "escidoc");
        namespaces.put("http://purl.org/dc/elements/1.1/", "dc");
        namespaces.put("http://purl.org/dc/terms/", "dcterms");
        pubmanOpts.setSavePrettyPrint();
        pubmanOpts.setSavePrettyPrintIndent(4);
        pubmanOpts.setSaveAggressiveNamespaces();
        pubmanOpts.setSaveSuggestedPrefixes(namespaces);
        pubmanOpts.setUseDefaultNamespace();
        pubmanOpts.setUseCDataBookmarks();
        return pubmanOpts;
    }
    
    public static XmlOptions getFoxmlOpts()
    {
        foxmlOpts = new XmlOptions();
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("info:fedora/fedora-system:def/foxml#", "foxml");
        namespaces.put("http://www.w3.org/2001/XMLSchema-instance", "foxmlxsi");
        foxmlOpts.setSavePrettyPrint();
        foxmlOpts.setSavePrettyPrintIndent(4);
        foxmlOpts.setSaveSuggestedPrefixes(namespaces);
        return foxmlOpts;
    }
    
    public static XmlOptions getGenericMDOpts()
    {
        foxmlOpts = new XmlOptions();
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("http://purl.org/escidoc/schemas/generic-metadata/metadata/0.1", "generic-metadata");
        namespaces.put("http://purl.org/dc/elements/1.1/", "dc");
        foxmlOpts.setSavePrettyPrint();
        foxmlOpts.setSavePrettyPrintIndent(4);
        // foxmlOpts.setSaveAggressiveNamespaces();
        foxmlOpts.setSaveSuggestedPrefixes(namespaces);
        //foxmlOpts.setUseDefaultNamespace();
        return foxmlOpts;
    }
    
    public static XmlOptions getFileOpts()
    {
        fileOpts = new XmlOptions();
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("http://purl.org/dc/elements/1.1/", "dc");
        namespaces.put("http://www.w3.org/1999/xlink", "xlink");
        namespaces.put("http://purl.org/escidoc/metadata/profiles/0.1/file", "file");
        fileOpts.setSavePrettyPrint();
        fileOpts.setSavePrettyPrintIndent(4);
        fileOpts.setSaveAggressiveNamespaces();
        fileOpts.setSaveSuggestedPrefixes(namespaces);
        fileOpts.setUseDefaultNamespace();
        return fileOpts;
    }
    
    public static boolean validation(XmlObject o)
    {
        ArrayList valErrors = new ArrayList();
        XmlOptions valOpts = new XmlOptions();
        valOpts.setErrorListener(valErrors);
        valOpts.setValidateTreatLaxAsSkip();
        
        if (o.validate(valOpts))
        {
            valid = true;
        }
        else
        {
            Logger.getLogger(XBeanUtils.class).error("Validation failed! "+o.getClass().getName());
            Iterator iter = valErrors.iterator();
            while (iter.hasNext())
            {
                Logger.getLogger(XBeanUtils.class).error(">> " + iter.next() + "\n");
            }
            valid = false;
        }
        
        return valid;
    }
}
