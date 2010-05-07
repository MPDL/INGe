package org.fao.oa.ingestion.faodoc;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import noNamespace.ITEMType;
import noNamespace.ResourcesDocument;

import org.apache.xmlbeans.XmlException;
import org.fao.oa.ingestion.utils.IngestionProperties;

/**
 * utility class to get a list of -or a single- FAODOC resource item(s).
 * 
 * @author Wilhelm Frank (MPDL)
 * @version
 */
public class FaodocItem
{
    public static final String FAODOC_BASE_DIR = IngestionProperties.get("faodoc.export.file.location");

    /**
     * only used for testing purposes.
     * 
     * @param strings
     */
    public static void main(String... strings)
    {
        String[] filenames = IngestionProperties.get("faodoc.export.file.names").split(" ");
        //parseTest(filenames);
        ArrayList<ITEMType> items = filteredList(filenames, null);
        ITEMType item = getByARN(items, "XF2006236883");
        System.out.println(item.xmlText());
    }

    /**
     * get a single ITEMType object from a list.
     * 
     * @param itemList {@link ArrayList} of ITEMType objects.
     * @param id {@link String}
     * @return {@link ITEMType}
     */
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

    /**
     * only used for testing purposes.
     * 
     * @param names {@link String[]}
     */
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
                for (ITEMType item : items)
                {
                    if (item.getBIBLEVELArray(0).equals(pattern))
                    {
                        if (item.getBIBLEVELArray(0).equalsIgnoreCase("AS"))
                        {
                            for (String url : item.getURLArray())
                            {
                                System.out.println(item.getARNArray(0) + "  has URL " + url);
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
    }

    /**
     * get a list of filtered FAODOC resource items as ITEMType objects.
     * 
     * @param filenames {@link String[]}
     * @param filter {@link String}
     * @return {@link ArrayList} of ITEMType objects.
     */
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
        System.out.println("List contains " + filteredFaodocList.size() + " FAODOC items with BIBLEVEL starting with "
                + filter);
        return filteredFaodocList;
    }

    /**
     * utility method to examine the xml structure of a FAODOC resource item.
     * 
     * @param faodocItemList {@link ArrayList} of ITEMType objects.
     * @throws Exception
     */
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

    /**
     * utility method.
     * 
     * @param item {@link ITEMType}
     * @param condition {@link String}
     * @param args {@link Class[]}
     * @param vals {@link Object[]}
     * @return {@link HashMap}
     */
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
