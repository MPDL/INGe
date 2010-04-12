package org.fao.oa.ingestion.faodoc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
        parseTest(filenames);
        
        //String filter = "M";
        //ArrayList<ITEMType> items = filteredList(filenames, filter);
        /*
        try
        {
            parseTest(filenames);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        */
        //ITEMType item = getByARN(items, "XF2006364349");
//        ITEMType item = getByARN(items, "XF2002400347");

        //System.out.println(item);
        //Object o = matchCondition(item, "sizeOfJNArray").get(int.class);
        /*
        Map map = matchCondition(item, "sizeOfISSNArray", null, null);
        System.out.println(map);
        int jns = Integer.valueOf(map.get(int.class).toString());
        System.out.println(jns);
        Class[] args = new Class[]{int.class};
        Object[] vals = new Object[]{Integer.valueOf("0")};
        Map issnMap = matchCondition(item, "getISSNArray", args, vals);
        System.out.println(issnMap);
        String issn = (String)matchCondition(item, "getISSNArray", args, vals).get(String.class);
        System.out.println(issn);
        */
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
        int multiconf = 0;
        int multiseries = 0;
        BufferedWriter writer = null;
        /*
        try
        {
            writer = new BufferedWriter(new FileWriter("/home/frank/data/AGRIS_FAO/url_variations_FAODOC"));
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
        */

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

                int withoutJN = 0;
                int withoutURL = 0;
                int withoutTIT = 0;
                for (ITEMType item : items)
                {
                    if (item.getBIBLEVELArray(0).equals(pattern))
                    {
                        /*
                        if (item.sizeOfURLArray() > 0)
                        {
                            for (int l = 0; l < item.sizeOfURLArray(); l++)
                            {
                                writer.write(item.getARNArray(0) + "   " + item.getURLArray(l));
                                writer.newLine();
                                System.out.println(item.getARNArray(0) + "   " + item.getURLArray(l));
                            }
                            
                        }
                        */
                        /*
                        if (item.sizeOfJNArray() > 0)
                        {
                            for (String jn : item.getJNArray())
                            {
                               // System.out.println(item.getARNArray(0) + " has jn " + jn);
                            }
                        }
                        else
                        {
                            withoutJN++;
                            if (item.sizeOfURLArray() > 0)
                            {
                                //System.out.println(item.getARNArray(0) + " has no job number! BUT an URL: " + item.getURLArray(0));
                            }
                            else
                            {
                                withoutURL++;
                                //System.out.println(item.getARNArray(0) + " has NO jn AND NO URL !!!");
                                if (item.sizeOfTITENArray() > 0 
                                        || item.sizeOfTITESArray() > 0 
                                        || item.sizeOfTITFRArray() > 0 
                                        || item.sizeOfTITOTArray() > 0 
                                        || item.sizeOfTITTRArray() > 0)
                                {
                                    
                                }
                                else
                                {
                                    withoutTIT++;
                                }
                            }
                        }
                        */
                        
                        
                        if (item.sizeOfCOLLINFOArray() > 0)
                        {
                            for (String tit : item.getCOLLINFOArray())
                            {
                                System.out.println(item.getARNArray(0) + "  has COLL_INFO " + tit);
                            }
                        }
                    }
                    else
                    {
                        System.out.println("OOOPS !!!");
                        System.out.println(name + " contains illegal pattern " + item.getBIBLEVELArray(0) + " in item "
                                + item.getARNArray(0));
                    }
                }
                //System.out.println("items without job number: " + withoutJN);
                //System.out.println("items without jn and url: " + withoutURL);
                //System.out.println("items without anything: " + withoutTIT);

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
        System.out.println("total number of faodocs " + faodocs);
        System.out.println("multiple conferences " + multiconf);
        System.out.println("multiple series " + multiseries);
    }

    public static ArrayList<ITEMType> filteredList(String[] filenames, String filter)
    {
        ArrayList<ITEMType> filteredFaodocList = new ArrayList<ITEMType>();
        for (String name : filenames)
        {
            if (filter != null)
            {
            if (name.startsWith(filter) || name.startsWith("updated_100120/" + filter))
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
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            }
            else
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
                    e.printStackTrace();
                }
                catch (IOException e)
                {
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
    
    public static HashMap<Class, Object> matchCondition(ITEMType item, String condition, Class[] args, Object[] vals)
    {
        Class itemTypeClass = item.getClass();
        HashMap<Class, Object> map = new HashMap<Class, Object>();
        Method m = null;
        try
        {
            if (args != null)
            {
                m = itemTypeClass.getDeclaredMethod(condition, args);
            }
            else
            {
                m = itemTypeClass.getDeclaredMethod(condition, null);
            }
        }
        catch (SecurityException e1)
        {
            e1.printStackTrace();
        }
        catch (NoSuchMethodException e1)
        {
            e1.printStackTrace();
        }
            if (m != null)
            {
                Class returnType = m.getReturnType();
                try
                {
                    if (vals != null)
                    {
                        Object returnValue = m.invoke(item, vals);
                        map.put(returnType, returnValue);
                        return map;
                    }
                    else
                    {
                        Object returnValue = m.invoke(item, null);
                        map.put(returnType, returnValue);
                        return map;
                    }
                }
                catch (IllegalArgumentException e)
                {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
                catch (InvocationTargetException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                return null;
            }
        
        return null;
    }
}
