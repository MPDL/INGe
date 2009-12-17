package org.fao.oa.ingestion.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptionCharEscapeMap;
import org.apache.xmlbeans.XmlOptions;

public class XBeanUtils
{
    private static XmlOptions modsOpts = null;
    private static XmlOptions foxmlOpts = null;
    private static XmlOptions agrisOpts = null;
    private static XmlOptions defaultOpts = null;


    private static boolean valid = false;
    
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
