package org.fao.oa.ingestion;

import java.io.File;
import java.io.IOException;
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
            ArrayList<String> faodocJN = null;
            String faodocLANG = null;
            if (faodoc.sizeOfJNArray() > 0 && faodoc.sizeOfLANGArray() > 0)
            {
                faodocJN = new ArrayList<String>();
                for (String jobno : faodoc.getJNArray())
                {
                    faodocJN.add(jobno);
                }
                faodocLANG = faodoc.getLANGArray(0);
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
                        if ((jn.equalsIgnoreCase(eims_jobno) || eims_jobno.startsWith(jn))
                                && faodocLANG.startsWith(eims_langkey))
                        {
                            recordCounter++;
                            logger.info("====== duplicated record number: " + recordCounter + " ======");
                            logger.info("  EIMS record:\t" + eims.getIdentifier() + "\t" + eims_jobno + "\t"
                                    + eims_langkey + "\t" + eims.getMaintype().getStringValue());
                            logger.info("FAODOC record:\t" + faodoc.getARNArray(0) + "\t" + jn + "\t" + faodocLANG
                                    + "\t" + faodoc.getBIBLEVELArray(0));
                            duplicates.put(eims.getIdentifier(), faodoc.getARNArray(0));
                        }
                    }
                }
            }
        }
        System.out.println("Successfully parsed " + faodocItems.size() + " FAODOC items");
        System.out.println("we found " + duplicates.size() + " duplicated records:");
        LinkedHashMap<String, String> sorted = sortHashMapByValues(duplicates);
        //System.out.println(sorted.toString());
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
}
