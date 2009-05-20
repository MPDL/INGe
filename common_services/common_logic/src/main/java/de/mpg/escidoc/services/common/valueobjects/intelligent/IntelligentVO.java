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

public class IntelligentVO implements Serializable
{
    
    public IntelligentVO copy(IntelligentVO orig) {
        
        IntelligentVO obj = null;
        try {
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
            obj = (IntelligentVO)in.readObject();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        catch(ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        return obj;
    }

    public static Object unmarshal(String xml, Class bindingClass) throws Exception
    {
        IBindingFactory bindingFactory = BindingDirectory.getFactory("binding", bindingClass);
        IUnmarshallingContext unmacxt = bindingFactory.createUnmarshallingContext();
        StringReader sr = new StringReader(xml);
        Object o = unmacxt.unmarshalDocument(sr, null);
        return o;
    }

    public static String marshal(Object object, Class bindingClass) throws Exception
    {
        IBindingFactory bindingFactory = BindingDirectory.getFactory("binding", bindingClass);
        IMarshallingContext macxt = bindingFactory.createMarshallingContext();
        StringWriter sw = new StringWriter();
        macxt.marshalDocument(object, "UTF-8", null, sw);
        return sw.toString();
    }
    
}
