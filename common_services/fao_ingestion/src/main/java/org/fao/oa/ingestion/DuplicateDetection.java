package org.fao.oa.ingestion;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.fao.oa.ingestion.eimscdr.EimsCdrItem;
import org.fao.oa.ingestion.faodoc.FaodocItem;
import org.fao.oa.ingestion.foxml.ModsDatastream;
import org.fao.oa.ingestion.utils.IngestionProperties;

import noNamespace.ITEMType;
import noNamespace.ItemType;
import noNamespace.ResourcesDocument;
import noNamespace.TitleType;

public class DuplicateDetection
{
    /**
     * public constructor.
     */
    public DuplicateDetection()
    {
    }

    ArrayList<ITEMType> faodocItems = null;
    ArrayList<ItemType> eimsItems = null;
    HashMap<String, String> duplicates = null;
    boolean hasDuplicate = false;
    Logger logger = Logger.getLogger("ingestion");

    /**
     * compare all FAODOC items with BIBLEVEL 'M' or 'MS' with all EIMS_CDR items of maintype 'publication' or
     * 'meeting'.
     */
    public void checkMMS()
    {
        duplicates = new HashMap<String, String>();
        String[] faodoc_filenames = IngestionProperties.get("faodoc.export.file.names").split(" ");
        String[] eims_filenames = IngestionProperties.get("eims.export.file.names").split(" ");
        faodocItems = FaodocItem.filteredList(faodoc_filenames, "M");
        eimsItems = EimsCdrItem.allEIMSItemsAsList(eims_filenames);
        int recordCounter = 0;
        for (ITEMType faodoc : faodocItems)
        {
            hasDuplicate = false;
            ArrayList<String> faodocJN = null;
            ArrayList<String> faodocLANG = null;
            if (faodoc.sizeOfJNArray() > 0 && faodoc.sizeOfLANGArray() > 0)
            {
                faodocJN = new ArrayList<String>();
                faodocLANG = new ArrayList<String>();
                for (String jobno : faodoc.getJNArray())
                {
                    faodocJN.add(jobno);
                }
                for (String lang : faodoc.getLANGArray())
                {
                    faodocLANG.add(lang);
                }
            }
            for (ItemType eims : eimsItems)
            {
                String eims_jobno = null;
                String eims_langkey = null;
                if (eims.getDate() != null)
                {
                    if (eims.getJobno() != null && eims.getLangkey() != null)
                    {
                        eims_jobno = eims.getJobno();
                        eims_langkey = eims.getLangkey();
                    }
                }
                if (faodocJN != null && eims_jobno != null && faodocLANG != null && eims_langkey != null)
                {
                    for (String jn : faodocJN)
                    {
                        for (String lang : faodocLANG)
                        {
                            if ((jn.equalsIgnoreCase(eims_jobno) || eims_jobno.startsWith(jn))
                                    && lang.startsWith(eims_langkey))
                            {
                                recordCounter++;
                                StringBuilder message = new StringBuilder(faodoc.getARNArray(0) + "\t" + eims.getIdentifier() + "\t");
                                message.append("jobno and lang\t");
                                message.append("[" + jn + " " + lang + "]\t[" + eims_jobno + " " + eims_langkey + "]");
                                logger.info(message);
                                hasDuplicate = true;
                                duplicates.put(eims.getIdentifier(), faodoc.getARNArray(0));
                            }
                        }
                    }
                }
                else
                {
                    checkURL(faodoc, eims);
                }
            }
            if (hasDuplicate == false)
            {
                logger.info(faodoc.getARNArray(0));
            }
        }
        System.out.println("Successfully parsed " + faodocItems.size() + " FAODOC items");
        System.out.println("we found " + duplicates.size() + " duplicated records:");
        LinkedHashMap<String, String> sorted = sortHashMapByValues(duplicates);
        // System.out.println(sorted.toString());
        // System.out.println(duplicates.toString());
    }

    /**
     * compare a FAODOC item with an EIMS_CDR item. check if any FAODOC URL equals EIMS_CDR html or pdf URL
     */
    public void checkURL(ITEMType faodoc, ItemType eims)
    {
        ArrayList<String> faodocURLs = null;
        if (faodoc.sizeOfURLArray() > 0)
        {
            faodocURLs = new ArrayList<String>();
            for (String url : faodoc.getURLArray())
            {
                faodocURLs.add(url);
            }
        }
        String eims_html = null;
        String eims_pdf = null;
        if (eims.getURL() != null)
        {
            eims_html = eims.getURL().getStringValue();
        }
        if (eims.getPDFURL() != null)
        {
            eims_pdf = eims.getPDFURL().getStringValue();
        }
        if (faodocURLs != null && (eims_html != null || eims_pdf != null))
        {
            for (String url : faodocURLs)
            {
                if (url.equalsIgnoreCase(eims_html) || url.equalsIgnoreCase(eims_pdf))
                {
                    hasDuplicate = true;
                    StringBuilder message = new StringBuilder(faodoc.getARNArray(0) + "\t" + eims.getIdentifier() + "\t");
                    message.append("URL pdf / html\t");
                    message.append("[" + url + "]\t[" + eims_html + "]\t[" + eims_pdf + "]");
                    logger.info(message);
                }
            }
        }
        else
        {
            checkTitles(faodoc, eims);
        }
    }

    /**
     * compare a FAODOC item with an EIMS_CDR item. check if any FAODOC TITLE equals EIMS_CDR title
     */
    public void checkTitles(ITEMType faodoc, ItemType eims)
    {
        ArrayList<String> faodocTitles = null;
        ArrayList<String> faodocDates = null;
        if (faodoc.sizeOfTITENArray() > 0 || faodoc.sizeOfTITESArray() > 0 || faodoc.sizeOfTITFRArray() > 0
                || faodoc.sizeOfTITOTArray() > 0 || faodoc.sizeOfTITTRArray() > 0)
        {
            faodocTitles = new ArrayList<String>();
            if (faodoc.sizeOfTITENArray() > 0)
            {
                for (String title : faodoc.getTITENArray())
                {
                    faodocTitles.add(title);
                }
            }
            if (faodoc.sizeOfTITESArray() > 0)
            {
                for (String title : faodoc.getTITESArray())
                {
                    faodocTitles.add(title);
                }
            }
            if (faodoc.sizeOfTITFRArray() > 0)
            {
                for (String title : faodoc.getTITFRArray())
                {
                    faodocTitles.add(title);
                }
            }
            if (faodoc.sizeOfTITOTArray() > 0)
            {
                for (String title : faodoc.getTITOTArray())
                {
                    faodocTitles.add(title);
                }
            }
            if (faodoc.sizeOfTITTRArray() > 0)
            {
                for (String title : faodoc.getTITTRArray())
                {
                    faodocTitles.add(title);
                }
            }
        }
        if (faodoc.sizeOfDATEISSUEArray() > 0 || faodoc.sizeOfPUBDATEArray() > 0 || faodoc.sizeOfPUBYEARArray() > 0
                || faodoc.sizeOfYEARPUBLArray() > 0)
        {
            faodocDates = new ArrayList<String>();
            if (faodoc.sizeOfDATEISSUEArray() > 0)
            {
                for (String date : faodoc.getDATEISSUEArray())
                {
                    faodocDates.add(date);
                }
            }
            
            if (faodoc.sizeOfPUBDATEArray() > 0)
            {
                for (String date : faodoc.getPUBDATEArray())
                {
                    faodocDates.add(date);
                }
            }

            if (faodoc.sizeOfPUBYEARArray() > 0)
            {
                for (String date : faodoc.getPUBYEARArray())
                {
                    faodocDates.add(date);
                }
            }
            if (faodoc.sizeOfYEARPUBLArray() > 0)
            {
                for (String date : faodoc.getYEARPUBLArray())
                {
                    faodocDates.add(date);
                }
            }
        }
        if (faodocDates != null && eims.getDate() != null)
        {
            if (eims.sizeOfTitleArray() > 0)
            {
                for (TitleType eims_title : eims.getTitleArray())
                {
                    if (faodocTitles != null)
                    {
                        if (faodocTitles.contains(eims_title.getStringValue()))
                        {
                            if (faodocDates.contains(eims.getDate().getStringValue()))
                            {
                                hasDuplicate = true;
                                StringBuilder message = new StringBuilder(faodoc.getARNArray(0) + "\t" + eims.getIdentifier() + "\t");
                                message.append("title and date \t");
                                message.append(faodocTitles + " " + faodocDates + "\t["
                                        + eims_title.getStringValue() + " " + eims.getDate().getStringValue() + " "
                                        + eims_title.getLang() + "]");
                                logger.info(message);
                            }
                        }
                    }
                }
            }
        }
        /*
         * else { System.out.println("NO DATES !!! " + faodoc.getARNArray(0) + faodocDates + "  " +
         * eims.getIdentifier()); }
         */
    }

    /**
     * compare all FAODOC items with BIBLEVEL 'M' or 'MS' with all EIMS_CDR items of maintype 'publication' or
     * 'meeting'.
     */
    public void checkURL()
    {
        duplicates = new HashMap<String, String>();
        String[] faodoc_filenames = IngestionProperties.get("faodoc.export.file.names").split(" ");
        String[] eims_filenames = IngestionProperties.get("eims.export.file.names").split(" ");
        faodocItems = FaodocItem.filteredList(faodoc_filenames, "M");
        eimsItems = EimsCdrItem.allEIMSItemsAsList(eims_filenames);
        int recordCounter = 0;
        for (ITEMType faodoc : faodocItems)
        {
            ArrayList<String> faodocURLs = null;
            if (faodoc.sizeOfURLArray() > 0)
            {
                faodocURLs = new ArrayList<String>();
                for (String url : faodoc.getURLArray())
                {
                    faodocURLs.add(url);
                }
            }
            for (ItemType eims : eimsItems)
            {
                String eims_html = null;
                String eims_pdf = null;
                if (eims.getURL() != null)
                {
                    eims_html = eims.getURL().getStringValue();
                }
                if (eims.getPDFURL() != null)
                {
                    eims_pdf = eims.getPDFURL().getStringValue();
                }
                if (faodocURLs != null && (eims_html != null || eims_pdf != null))
                {
                    for (String url : faodocURLs)
                    {
                        if (url.equalsIgnoreCase(eims_html) || url.equalsIgnoreCase(eims_pdf))
                        {
                            recordCounter++;
                            logger.info("====== duplicated record number: " + recordCounter + " ======");
                            logger.info("  EIMS record:\t" + eims.getIdentifier() + "\t" + eims_html + "\t" + eims_pdf
                                    + "\t" + eims.getMaintype().getStringValue());
                            logger.info("FAODOC record:\t" + faodoc.getARNArray(0) + "\t" + url + "\t" + "\t"
                                    + faodoc.getBIBLEVELArray(0));
                            duplicates.put(eims.getIdentifier(), faodoc.getARNArray(0));
                        }
                    }
                }
            }
        }
        System.out.println("Successfully parsed " + faodocItems.size() + " FAODOC items");
        System.out.println("we found " + duplicates.size() + " duplicated records:");
        LinkedHashMap<String, String> sorted = sortHashMapByValues(duplicates);
        // System.out.println(sorted.toString());
        // System.out.println(duplicates.toString());
    }

    public LinkedHashMap<String, String> sortHashMapByValues(HashMap<String, String> passedMap)
    {
        List<String> mapKeys = new ArrayList<String>(passedMap.keySet());
        List<String> mapValues = new ArrayList<String>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);
        LinkedHashMap<String, String> sortedMap = new LinkedHashMap<String, String>();
        Iterator<String> valueIt = mapValues.iterator();
        while (valueIt.hasNext())
        {
            Object val = valueIt.next();
            Iterator<String> keyIt = mapKeys.iterator();
            while (keyIt.hasNext())
            {
                Object key = keyIt.next();
                String comp1 = passedMap.get(key).toString();
                String comp2 = val.toString();
                if (comp1.equals(comp2))
                {
                    passedMap.remove(key);
                    mapKeys.remove(key);
                    sortedMap.put((String)key, (String)val);
                    break;
                }
            }
        }
        return sortedMap;
    }

    public String comparableURL(String urlString)
    {
        try
        {
            URL url = new URL(urlString);
            if (url.getQuery() != null)
            {
                return url.getQuery();
            }
            else
            {
                return url.getFile();
            }
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
