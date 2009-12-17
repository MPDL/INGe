package org.fao.oa.ingestion.faodoc;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import noNamespace.EimsresourcesDocument;
import noNamespace.ITEMType;
import noNamespace.ItemType;
import noNamespace.ResourcesDocument;

import org.apache.xmlbeans.XmlException;
import org.fao.oa.ingestion.utils.IngestionProperties;

public class FaodocItem
{
    public static final String FAODOC_BASE_DIR = IngestionProperties.get("faodoc.export.file.location");


    public static void main(String... strings)
    {
        String[] filenames = IngestionProperties.get("faodoc.export.file.names").split(" ");
        // System.out.println(getAll(faodocExportFile).length);
        // ITEMType faodoc = getByARN(faodocExportFile, "XF2006427373");
        // System.out.println(faodoc.getARNArray(0));
        // System.out.println(faodoc.sizeOfJNArray());
        // getAllARNs(faodocExportFile);
        //parseTest(filenames);
        
        String filter = "M";
        ArrayList<ITEMType> items = filteredList(filenames, filter);
        /*
        try
        {
            parseTest(filenames);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        */
        ITEMType item = getByARN(items, "XF2006140233");
//        ITEMType item = getByARN(items, "XF2002400347");

        System.out.println(item);
        //System.out.println(item.getISBNArray(0) + "  " + item.getTITENArray(0));
//        ITEMType item2 = getByARN(items, "XF2009786668");
//        System.out.println(item2);
        //System.out.println(item2.getISBNArray(0) + "  " + item2.getTITENArray(0));
        
        //ITEMType item3 = getByARN(items, "XF2000392509");
        //System.out.println(item3);


    }

    public static ITEMType[] getAll(File file)
    {
        try
        {
            ResourcesDocument resourceDoc = ResourcesDocument.Factory.parse(file);
            ITEMType[] items = resourceDoc.getResources().getITEMArray();
            return items;
        }
        catch (XmlException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static ITEMType getByARN(ArrayList<ITEMType> itemList, String id)
    {
        for (ITEMType item : itemList)
        {
            if (item.sizeOfARNArray() > 0)
            {
                if (item.getARNArray(0).equalsIgnoreCase(id))
                {
                    return item;
                }
            }
        }
        return null;
    }

    public static void getAllARNs(File file)
    {
        int itemNo = 0;
        for (ITEMType item : getAll(file))
        {
            itemNo++;
            if (item.sizeOfARNArray() > 0)
            {
                System.out.println("FAODOC item no. " + itemNo + " has ARN: " + item.getARNArray(0));
            }
        }
    }

    public static void parseTest(String[] names)
    {
        int faodocs = 0;
        for (String name : names)
        {
            File faodocItemFile = new File(FAODOC_BASE_DIR + name);
            System.out.println("Attempt to parse file " + faodocItemFile.getName());
            try
            {
                ResourcesDocument resDoc = ResourcesDocument.Factory.parse(faodocItemFile);
                ITEMType[] items = resDoc.getResources().getITEMArray();
                System.out.println(name + " contains " + items.length + " items");
                faodocs = faodocs + items.length;
                String pattern = name.split("-")[0];
                ArrayList<String> langs = null;

                for (ITEMType item : items)
                {
                    if (item.getBIBLEVELArray(0).equals(pattern))
                    {
                        if (item.sizeOfPARTOFArray() > 0)
                        {
                            System.out.println(item.getARNArray(0));
                            System.out.println(item.getPARTOFArray(0));
                        }
                    }
                    else
                    {
                        System.out.println("OOOPS !!!");
                        System.out.println(name + " contains illegal pattern " + item.getBIBLEVELArray(0) + " in item "
                                + item.getARNArray(0));
                    }
                }
            }
            catch (XmlException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.out.println("total number of faodocs " + faodocs);
    }

    public static ArrayList<ITEMType> filteredList(String[] filenames, String filter)
    {
        ArrayList<ITEMType> filteredFaodocList = new ArrayList<ITEMType>();
        for (String name : filenames)
        {
            if (name.startsWith(filter))
            {
                File faodocFile = new File(FAODOC_BASE_DIR + name);
                try
                {
                    ResourcesDocument resDoc = ResourcesDocument.Factory.parse(faodocFile);
                    ITEMType[] items = resDoc.getResources().getITEMArray();
                    for (ITEMType item : items)
                    {
                        filteredFaodocList.add(item);
                    }
                }
                catch (XmlException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        System.out.println("List contains " + filteredFaodocList.size() + " FAODOC items with BIBLEVEL starting with " + filter);
        return filteredFaodocList;
    }
    
    public static void checkXMLStructure(ArrayList<ITEMType> faodocItemList) throws Exception
    {
        Set moreThanOnce = new TreeSet<String>();
        Set onlyOnce = new TreeSet<String>();
        Set zero = new TreeSet<String>();
        for (ITEMType faodocitem : faodocItemList)
        {
            Class itemTypeClass = faodocitem.getClass();
            for (Method method : itemTypeClass.getDeclaredMethods())
            {
                if (method.getName().contains("sizeOf"))
                {
                    Object size = method.invoke(faodocitem, null);
                    
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
