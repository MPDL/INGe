package org.fao.oa.ingestion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import noNamespace.ITEMType;
import noNamespace.ItemType;

import org.apache.xmlbeans.impl.values.XmlValueOutOfRangeException;
import org.fao.oa.ingestion.eimscdr.EimsCdrItem;
import org.fao.oa.ingestion.faodoc.ConferenceName;
import org.fao.oa.ingestion.faodoc.FaodocItem;
import org.fao.oa.ingestion.faodoc.JournalName;
import org.fao.oa.ingestion.faodoc.SeriesName;
import org.fao.oa.ingestion.foxml.Foxml;
import org.fao.oa.ingestion.utils.IngestionProperties;
import org.fao.oa.ingestion.utils.XBeanUtils;

import fedora.fedoraSystemDef.foxml.DigitalObjectDocument;

/**
 * @author Wilhelm Frank (MPDL)
 * @version Main class to either perform the duplicate detection or the creation of FOXML files.
 */
public class Main
{
    /**
     * defines the destination directory to store the created FOXML files.
     */
    public static final String FOXML_DESTINATION_DIR = IngestionProperties.get("fao.foxml.destination.location");
    public static final String PUBLICATIONS = "publications";
    public static final String ARTICLES = "articles";

    /**
     * @param args duplicates / foxml [all / start-end].
     */
    public static void main(String[] args)
    {
        switch (args.length)
        {
            case 0:
                printUsage("no args specified!");
                break;
            case 1:
                if (args[0].equalsIgnoreCase("test"))
                {
                    // performTest();
                    // recover();
                    testControlledVocab();
                }
                else
                {
                    printUsage("invalid arguments: " + args[0]);
                }
                break;
            case 2:
                if (args[0].equalsIgnoreCase("duplicates"))
                {
                    DuplicateDetection dd = new DuplicateDetection();
                    if (args[1].equalsIgnoreCase(PUBLICATIONS))
                    {
                        dd.checkMMS();
                    }
                    else
                    {
                        if (args[1].equalsIgnoreCase(ARTICLES))
                        {
                            dd.checkAS();
                        }
                    }
                }
                else
                {
                    printUsage("invalid arguments: " + args[0] + " " + args[1]);
                }
                break;
            case 3:
                if (args[0].equalsIgnoreCase("foxml"))
                {
                    if (args[2].equalsIgnoreCase("rest"))
                    {
                        createFoxml(args[1], null);
                    }
                    else
                    {
                        createFoxml(args[1], args[2]);
                    }
                }
                else
                {
                    printUsage("invalid arguments: " + args[0] + "   " + args[1] + "   " + args[2]);
                }
                break;
            default:
                printUsage("too many arguments!");
                break;
        }
    }

    /**
     * Utility method to read the logfile created by the duplicates detection.
     * 
     * @param arg all / range
     * @return {@link String[]}
     * @throws Exception
     */
    public static List<String[]> parseLogFile(String arg, String genreType) throws Exception
    {
        File logfile = null;
        if (genreType.equalsIgnoreCase(PUBLICATIONS))
        {
            logfile = new File("ingestion_" + genreType + ".log");
        }
        if (genreType.equalsIgnoreCase(ARTICLES))
        {
            logfile = new File("ingestion_" + genreType + ".log");
        }
        if (logfile != null)
        {
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
        else
        {
            System.out.println("no logfile available for " + genreType);
            return null;
        }
    }

    /**
     * create FOXML files.
     * 
     * @param arg {@link String} to be passed 2 parseLogFile() method.
     * @param genreType {@link String} [publications / articles / null].
     */
    public static void createFoxml(String arg, String genreType)
    {
        ArrayList<ITEMType> faodocList = null;
        ArrayList<ItemType> eimsList = null;
        if (genreType != null)
        {
            if (genreType.equalsIgnoreCase(PUBLICATIONS))
            {
                String[] faodocFiles = IngestionProperties.get("faodoc.export.file.names").split(" ");
                String filter = "M";
                faodocList = FaodocItem.filteredList(faodocFiles, filter);
                String[] eimsFiles = IngestionProperties.get("eims.export.file.names").split(" ");
                eimsList = EimsCdrItem.allEIMSItemsAsList(eimsFiles, genreType);
            }
            else
            {
                if (genreType.equalsIgnoreCase(ARTICLES))
                {
                    String[] faodocFiles = IngestionProperties.get("faodoc.export.file.names").split(" ");
                    String filter = "AS";
                    faodocList = FaodocItem.filteredList(faodocFiles, filter);
                    String[] eimsFiles = IngestionProperties.get("eims.export.file.names.articles").split(" ");
                    eimsList = EimsCdrItem.allEIMSItemsAsList(eimsFiles, genreType);
                }
            }
            try
            {
                List<String[]> duplicates = parseLogFile(arg, genreType);
                if (duplicates != null)
                {
                    ArrayList<String> mergedEimsRecords = new ArrayList<String>();
                    for (String[] duplicate : duplicates)
                    {
                        String arn = null;
                        String id = null;
                        ITEMType faodoc = null;
                        ItemType eims = null;
                        int size = duplicate.length;
                        arn = duplicate[0].substring(duplicate[0].length() - 12, duplicate[0].length());
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
                else
                {
                    System.out.println("invalid argument: " + genreType);
                    System.out.println("must be 'publications' or 'articles'");
                }
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }
        }
        else
        {
            String[] faodocFiles = IngestionProperties.get("faodoc.export.file.names").split(" ");
            String filter = "AM";
            faodocList = FaodocItem.filteredList(faodocFiles, filter);
            for (ITEMType it : faodocList)
            {
                String id = it.getARNArray(0);
                System.out.println("Creating FOXML for " + id);
                DigitalObjectDocument fox = new Foxml().merge(it, null);
                try
                {
                    fox.save(new File(FOXML_DESTINATION_DIR + id), XBeanUtils.getFoxmlOpts());
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void printUsage(String message)
    {
        System.out.println(message);
        System.out.println();
        System.out.println("USAGE:");
        System.out.println("      1. Duplicates detection");
        System.out.println("         duplicates publications (M + MS - publication)");
        System.out.println("         duplicates articles (AS - article)");
        System.out.println("      2. FOXML creation");
        System.out.println("         foxml [all / range] publications (M + MS)");
        System.out.println("         foxml [all / range] articles (AS)");
        System.out.println("         foxml [all / range] rest (AM + AMS)");
    }

    private static void performTest()
    {
        int good = 0;
        int bad = 0;
        String[] faodocFiles = IngestionProperties.get("faodoc.export.file.names").split(" ");
        String filter = "AM";
        ArrayList<ITEMType> faodocList = FaodocItem.filteredList(faodocFiles, filter);
        // ITEMType item = FaodocItem.getByARN(faodocList, "XF2004759611");
        for (ITEMType item : faodocList)
        {
            try
            {
                if (item.sizeOfCDArray() > 0)
                {
                    int invalid = item.getCDArray(0);
                }
                good++;
            }
            catch (XmlValueOutOfRangeException xvoore)
            {
                System.out.println(xvoore.getMessage());
                bad++;
            }
        }
        System.out.println(good + " good ones");
        System.out.println(bad + " invalid Integers");
    }

    private static void recover()
    {
        int numnew = 0;
        ArrayList<ITEMType> faodocList = null;
        ArrayList<String> alreadyCreated = new ArrayList<String>();
        File dir = new File(FOXML_DESTINATION_DIR);
        for (File f : dir.listFiles())
        {
            alreadyCreated.add(f.getName());
        }
        String[] faodocFiles = IngestionProperties.get("faodoc.export.file.names").split(" ");
        String filter = "AM";
        faodocList = FaodocItem.filteredList(faodocFiles, filter);
        for (ITEMType it : faodocList)
        {
            String id = it.getARNArray(0);
            try
            {
                if (alreadyCreated.contains(id))
                {
                    System.out.println("FOXML for " + id + " was already created");
                }
                else
                {
                    System.out.println("Creating FOXML for " + id);
                    numnew++;
                    DigitalObjectDocument fox = new Foxml().merge(it, null);
                    fox.save(new File(FOXML_DESTINATION_DIR + id), XBeanUtils.getFoxmlOpts());
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        System.out.println("all FAODOCS with AM or AMS: " + faodocList.size());
        System.out.println("already created: " + alreadyCreated.size());
        System.out.println("still to create: " + numnew);
    }
    
    public static void testControlledVocab()
    {
        SeriesName sname = new SeriesName();
        String[] vals = sname.getSpanish("Serie Tecnica. Manual Tecnico - Centro Agronomico Tropical de Investigacion y Ensenanza (Costa Rica)");
        System.out.println(vals[0] + " " + vals[1] + " " + vals[2]);
        JournalName jname = new JournalName();
        String[] jvals = jname.get("Boletim de Informacao (OAM)");
        System.out.println(jvals[0] + " " + jvals[1] + " " + jvals[2]);
        String[] filenames = IngestionProperties.get("faodoc.export.file.names").split(" ");
        String filter = "M";
        ArrayList<ITEMType> itemList = FaodocItem.filteredList(filenames, filter);
        String id = "XF2006236883";
        ITEMType faodoc = FaodocItem.getByARN(itemList, id);
        System.out.println(faodoc.getCONFERENCEArray(0).getCONFEN());
        ConferenceName cname = new ConferenceName();
        String[] cvals = cname.getEnglish(faodoc.getCONFERENCEArray(0), faodoc.getCONFERENCEArray(0).getCONFEN());
        System.out.println(cvals[0] + " " + cvals[1]);
      //LanguageCodes lc = new LanguageCodes();
        //String[] codes = lc.getIso639Codes2("ar");
        //System.out.println(codes[0] + "  " + codes[1] + "  " + codes[2]);
    }
}
