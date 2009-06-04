package de.mpg.escidoc.services.common.valueobjects.intelligent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;

/**
 * Super class for value objects that contain additional methods for working with the coreservice.
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class IntelligentVO implements Serializable
{
    private Logger logger = Logger.getLogger(IntelligentVO.class);

    /**
     * Standard constructor.
     */
    public IntelligentVO()
    {
    }

    /**
     * Clone constructor.
     * 
     * @param orig the vo to be copied
     */
    public IntelligentVO(IntelligentVO orig)
    {
        this.copyInFields(orig);
    }

    /**
     * Tries to unmarshal the given xml string to the vo object.
     * 
     * @param xml The xml string.
     * @param bindingClass The class that should be used for the binding.
     * @return The unmarshalled VO object.
     * @throws Exception If an error occurs during unmarshalling.
     */
    protected static Object unmarshal(String xml, Class bindingClass) throws Exception
    {
        IBindingFactory bindingFactory = BindingDirectory.getFactory("binding", bindingClass);
        IUnmarshallingContext unmacxt = bindingFactory.createUnmarshallingContext();
        StringReader sr = new StringReader(xml);
        Object o = unmacxt.unmarshalDocument(sr, null);
        return o;
    }

    /**
     * Tries to marshal the given object to a xml string.
     * 
     * @param object The object to be marhsalled.
     * @param bindingClass he class that should be used for the binding.
     * @return The xml representation of the object.
     * @throws Exception If an error occurs during marshalling.
     */
    protected static String marshal(Object object, Class bindingClass) throws Exception
    {
        IBindingFactory bindingFactory = BindingDirectory.getFactory("binding", bindingClass);
        IMarshallingContext macxt = bindingFactory.createMarshallingContext();
        StringWriter sw = new StringWriter();
        macxt.marshalDocument(object, "UTF-8", null, sw);
        return sw.toString();
    }

    /**
     * Copies fields from the given VO to this VO, using getter and setter methods.
     * 
     * @param copyFrom The VO from which the fields are to be copied.
     */
    protected void copyInFields(IntelligentVO copyFrom)
    {
        Class copyFromClass = copyFrom.getClass();
        Class copyToClass = this.getClass();
        for (Method methodFrom : copyFromClass.getDeclaredMethods())
        {
            String setMethodName = null;
            if (methodFrom.getName().startsWith("get"))
            {
                setMethodName = "set" + methodFrom.getName().substring(3, methodFrom.getName().length());
            }
            else if (methodFrom.getName().startsWith("is"))
            {
                setMethodName = "set" + methodFrom.getName().substring(2, methodFrom.getName().length());
            }
            if (setMethodName != null)
            {
                try
                {
                    Method methodTo = copyToClass.getMethod(setMethodName, methodFrom.getReturnType());
                    try
                    {
                    	methodTo.invoke(this, methodFrom.invoke(copyFrom, null));
                    }
                    catch (Exception e)
                    {
                        logger.error("Could not copy field from method: " + methodFrom.getName(), e);
                    }
                }
                // No setter, do nothing.
                catch (NoSuchMethodException e)
                {
                    
                }
            }
        }
    }
}
