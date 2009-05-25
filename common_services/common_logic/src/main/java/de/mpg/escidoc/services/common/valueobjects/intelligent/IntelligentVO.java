package de.mpg.escidoc.services.common.valueobjects.intelligent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;

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
 *
 */
public class IntelligentVO implements Serializable
{
    
    /**
     * Creates a deep copy of this value pbject using serialization.
     * @param orig the vo to be copied
     * @return a copy of the VO
     */
    public IntelligentVO copy(IntelligentVO orig) {
        
        IntelligentVO obj = null;
        try 
        {
            // Write the object out to a byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(orig);
            out.flush();
            out.close();

            // Make an input stream from the byte array and read
            // a copy of the object back in.
            ObjectInputStream in = new ObjectInputStream(
                new ByteArrayInputStream(bos.toByteArray()));
            obj = (IntelligentVO) in.readObject();
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException cnfe) 
        {
            cnfe.printStackTrace();
        }
        return obj;
    }

    /**
     * Tries to unmarshal the given xml string to the vo object.
     * @param xml The xml string.
     * @param bindingClass The class that should be used for the binding.
     * @return The unmarshalled VO object.
     * @throws Exception If an error occurs during unmarshalling.
     */
    public static Object unmarshal(String xml, Class bindingClass) throws Exception
    {
        IBindingFactory bindingFactory = BindingDirectory.getFactory("binding", bindingClass);
        IUnmarshallingContext unmacxt = bindingFactory.createUnmarshallingContext();
        StringReader sr = new StringReader(xml);
        Object o = unmacxt.unmarshalDocument(sr, null);
        return o;
    }

    /**
     * Tries to marshal the given object to a xml string.
     * @param object The object to be marhsalled.
     * @param bindingClass he class that should be used for the binding.
     * @return The xml representation of the object.
     * @throws Exception If an error occurs during marshalling.
     */
    public static String marshal(Object object, Class bindingClass) throws Exception
    {
        IBindingFactory bindingFactory = BindingDirectory.getFactory("binding", bindingClass);
        IMarshallingContext macxt = bindingFactory.createMarshallingContext();
        StringWriter sw = new StringWriter();
        macxt.marshalDocument(object, "UTF-8", null, sw);
        return sw.toString();
    }
    
}
