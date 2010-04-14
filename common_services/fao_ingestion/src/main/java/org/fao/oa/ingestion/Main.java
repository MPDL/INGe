package org.fao.oa.ingestion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import noNamespace.ITEMType;
import noNamespace.ItemType;

import org.fao.oa.ingestion.eimscdr.EimsCdrItem;
import org.fao.oa.ingestion.faodoc.FaodocItem;
import org.fao.oa.ingestion.foxml.Foxml;
import org.fao.oa.ingestion.utils.IngestionProperties;
import org.fao.oa.ingestion.utils.XBeanUtils;

import fedora.fedoraSystemDef.foxml.DigitalObjectDocument;

/**
 * @author Wilhelm Frank (MPDL)
 * @version
 * 
 * Main class to either perform the duplicate detection
 * or the creation of FOXML files.
 */
public class Main
{
    /**
     * defines the destination directory to store the created FOXML files. 
     */
    public static final String FOXML_DESTINATION_DIR = IngestionProperties.get("fao.foxml.destination.location");
    
    /**
     * @param args duplicates / foxml [all / start-end].
     */
    public static void main(String[] args)
    {
        
        if (args.length < 1)
        {
            System.out.println("USAGE: duplicates / foxml [all / start-end]");
        }
        else
        {
            if (args.length == 1 && args[0].equalsIgnoreCase("duplicates"))
            {
                DuplicateDetection dd = new DuplicateDetection();
                dd.checkMMS();
            }
            else
            {
                if (args.length == 2 && args[0].equalsIgnoreCase("foxml"))
                {
                   createFoxml(args[1]);
                }
            }
        }
    }

    /**
     * Utility method to read the logfile created by the duplicates detection.
     * @param arg all / range
     * @return {@link String[]}
     * @throws Exception
     */
    public static List<String[]> parseLogFile(String arg) throws Exception
    {
        File logfile = new File("ingestion.log");
        BufferedReader reader = new BufferedReader(new FileReader(logfile));
        ArrayList<String[]> dups = new ArrayList<String[]>();
        List<String[]> subList = null;
        String line;
        while ((line = reader.readLine()) != null)
        {
            String[] values = line.split("\t");
            dups.add(values);
        }
        
        if (arg.equalsIgnoreCase("all"))
        {
            subList = dups;
        }
        else
        {
            String[] indices = arg.split("-");
            subList = dups.subList(Integer.valueOf(indices[0]), Integer.valueOf(indices[1]));
        }
        
        return subList;
    }

    /**
     * create FOXML files.
     * @param arg to be passed 2 parseLogFile() method.
     */
    public static void createFoxml(String arg)
    {
        String[] faodocFiles = IngestionProperties.get("faodoc.export.file.names").split(" ");
        String filter = "M";
        ArrayList<ITEMType> faodocList = FaodocItem.filteredList(faodocFiles, filter);
        String[] eimsFiles = IngestionProperties.get("eims.export.file.names").split(" ");
        ArrayList<ItemType> eimsList = EimsCdrItem.allEIMSItemsAsList(eimsFiles);
        try
        {
            List<String[]> duplicates = parseLogFile(arg);
            ArrayList<String> mergedEimsRecords = new ArrayList<String>();
            for (String[] duplicate : duplicates)
            {
                String arn = null;
                String id = null;
                ITEMType faodoc = null;
                ItemType eims = null;
                int size = duplicate.length;
                arn = duplicate[0].substring(duplicate[0].length() -12, duplicate[0].length());
                faodoc = FaodocItem.getByARN(faodocList, arn);
                if (size > 1)
                {
                    id = duplicate[1];
                    mergedEimsRecords.add(id);
                    eims = EimsCdrItem.getById(eimsList, id);
                }
                if (arn != null && id != null)
                {
                    System.out.println("Merging EIMS " + id + " with FAODOC " + arn);
                    DigitalObjectDocument fox = new Foxml().merge(faodoc, eims);
                    fox.save(new File(FOXML_DESTINATION_DIR + arn + "_" + id), XBeanUtils.getFoxmlOpts());
                }
                else
                {
                    if (arn != null)
                    {
                        System.out.println("Creating FOXML for " + arn);
                        DigitalObjectDocument fox = new Foxml().merge(faodoc, null);
                        fox.save(new File(FOXML_DESTINATION_DIR + arn), XBeanUtils.getFoxmlOpts());
                    }
                }
                // create FOXMLs for the remaining EIMS records
                for (ItemType it : eimsList)
                {
                    String eims_id = it.getIdentifier();
                    if (mergedEimsRecords.contains(eims_id))
                    {
                        
                    }
                    else
                    {
                        System.out.println("Creating FOXML for " + eims_id);
                        DigitalObjectDocument fox = new Foxml().merge(null, it);
                        fox.save(new File(FOXML_DESTINATION_DIR + eims_id), XBeanUtils.getFoxmlOpts());
                    }
                }
            }
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }
}
