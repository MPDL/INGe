package org.fao.oa.ingestion.eimscdr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import noNamespace.EimsresourcesDocument;
import noNamespace.ItemType;
import noNamespace.SubtitleType;
import noNamespace.TitleType;

import org.apache.xmlbeans.XmlException;
import org.fao.oa.ingestion.utils.IngestionProperties;

/**
 * utility class to get a list of -or a single- EIMS_CDR resource item(s).
 * 
 * @author Wilhelm Frank (MPDL)
 * @version
 */
public class EimsCdrItem
{
    public static final String EIMS_BASE_DIR = IngestionProperties.get("eims.export.file.location");

    /**
     * only used for testing purposes.
     * 
     * @param strings
     */
    public static void main(String... strings)
    {
        String[] filenames = IngestionProperties.get("eims.export.file.names").split(" ");
        parseTest(filenames);
    }

    /**
     * get a single ItenType object from a list.
     * 
     * @param itemList {@link ArrayList} of ItemType objects.
     * @param id {@link String}
     * @return {@link ItemType}
     */
    public static ItemType getById(ArrayList<ItemType> itemList, String id)
    {
        for (ItemType item : itemList)
        {
            if (item.getIdentifier().equalsIgnoreCase(id))
            {
                return item;
            }
        }
        return null;
    }

    /**
     * only used for testing purposes.
     * 
     * @param names {@link String[]}
     */
    public static void parseTest(String[] names)
    {
        int eimsitems = 0;
        BufferedWriter writer = null;
        ;
        try
        {
            writer = new BufferedWriter(new FileWriter("/home/frank/data/AGRIS_FAO/url_variations_EIMS"));
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
        int noDate = 0;
        for (String name : names)
        {
            File eimsItemFile = new File(EIMS_BASE_DIR + name);
            System.out.println("Attempt to parse file " + eimsItemFile.getName());
            try
            {
                EimsresourcesDocument resDoc = EimsresourcesDocument.Factory.parse(eimsItemFile);
                ItemType[] items = resDoc.getEimsresources().getItemArray();
                System.out.println(name + " contains " + items.length + " items");
                eimsitems = eimsitems + items.length;
                for (ItemType i : items)
                {
                    if (i.sizeOfTitleArray() > 0 && i.sizeOfSubtitleArray() > 0)
                    {
                        for (TitleType t : i.getTitleArray())
                        {
                            System.out
                                    .println(i.getIdentifier() + " Title " + t.getStringValue() + "   " + t.getLang());
                            for (SubtitleType s : i.getSubtitleArray())
                            {
                                if (t.getLang().equalsIgnoreCase(s.getLang()))
                                {
                                    System.out.println(i.getIdentifier() + " Sub " + s.getStringValue() + "   "
                                            + s.getLang());
                                }
                                else
                                {
                                    System.out.println("no subtitle in title language");
                                }
                            }
                        }
                    }
                    else
                    {
                        noDate++;
                    }
                }
            }
            catch (XmlException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        System.out.println("total number of eimsitems " + eimsitems);
        System.out.println("items without date: " + noDate);
    }

    /**
     * get a list of all EIMS-CDR resource items as ItemType objects.
     * 
     * @param filenames {@link String[]}
     * @return {@link ArrayList} of ItemType objects.
     */
    public static ArrayList<ItemType> allEIMSItemsAsList(String[] filenames)
    {
        ArrayList<ItemType> allEIMSItemsList = new ArrayList<ItemType>();
        Set<String> maintypeList = new HashSet<String>();
        Set<String> genreList = new HashSet<String>();
        for (String name : filenames)
        {
            File eimsFile = new File(EIMS_BASE_DIR + name);
            try
            {
                EimsresourcesDocument resDoc = EimsresourcesDocument.Factory.parse(eimsFile);
                ItemType[] items = resDoc.getEimsresources().getItemArray();
                for (ItemType item : items)
                {
                    allEIMSItemsList.add(item);
                    String maintype = item.getMaintype().getStringValue();
                    maintypeList.add(maintype);
                    String genre = item.getGenre().getStringValue();
                    genreList.add(genre);
                }
            }
            catch (XmlException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        System.out.println("List contains " + allEIMSItemsList.size() + " EIMS items");
        System.out.println("maintypes are " + maintypeList.toString());
        // System.out.println("genres are " + genreList.toString());
        return allEIMSItemsList;
    }

    /**
     * utility method to examine the xml structure of an EIMS-CDR resource item.
     * 
     * @param eimsItemList {@link ArrayList} of ItemType objects.
     * @throws Exception
     */
    public static void checkXMLStructure(ArrayList<ItemType> eimsItemList) throws Exception
    {
        Set moreThanOnce = new TreeSet<String>();
        Set onlyOnce = new TreeSet<String>();
        Set zero = new TreeSet<String>();
        for (ItemType eimsitem : eimsItemList)
        {
            Class itemTypeClass = eimsitem.getClass();
            for (Method method : itemTypeClass.getDeclaredMethods())
            {
                if (method.getName().contains("sizeOf"))
                {
                    Object size = method.invoke(eimsitem, null);
                    if (Integer.valueOf(size.toString()) > 1)
                    {
                        moreThanOnce.add(method.getName());
                    }
                    if (Integer.valueOf(size.toString()) == 1)
                    {
                        onlyOnce.add(method.getName());
                    }
                    if (Integer.valueOf(size.toString()) == 0)
                    {
                        zero.add(method.getName());
                    }
                }
            }
        }
        System.out.println("zero: " + zero);
        System.out.println("once: " + onlyOnce);
        System.out.println("many: " + moreThanOnce);
    }
}
