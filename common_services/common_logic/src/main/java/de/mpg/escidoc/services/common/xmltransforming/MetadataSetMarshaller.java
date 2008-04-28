package de.mpg.escidoc.services.common.xmltransforming;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jibx.runtime.IAliasable;
import org.jibx.runtime.IMarshallable;
import org.jibx.runtime.IMarshaller;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.MarshallingContext;

import de.mpg.escidoc.services.common.valueobjects.MetadataSetVO;

public class MetadataSetMarshaller implements IMarshaller, IAliasable //, IUnmarshaller
{
    private static Logger logger = Logger.getLogger(MetadataSetMarshaller.class);
    
    private static final String NAME_ATTRIBUTE_NAME = "name";
    private static final String RECORD_ELEMENT_NAME = "md-record";
    private static final String DEFAULT_NAME = "escidoc";
    
    private String m_uri;
    private int m_index;
    private String m_name;
    
    private static final String XSI_NAMESPACE_URI =
        "http://www.w3.org/2001/XMLSchema-instance";
    private static final String XSD_NAMESPACE_URI =
        "http://www.w3.org/2001/XMLSchema";
    private static final String[] SCHEMA_NAMESPACE_URIS =
    {
        XSI_NAMESPACE_URI, XSD_NAMESPACE_URI
    };
    private static final String XSI_NAMESPACE_PREFIX = "xsi";
    private static final String XSD_NAMESPACE_PREFIX = "xsd";
    private static final String[] SCHEMA_NAMESPACE_PREFIXES =
    {
        XSI_NAMESPACE_PREFIX, XSD_NAMESPACE_PREFIX
    };
    private static final String XSD_PREFIX_LEAD = "xsd:";

    public MetadataSetMarshaller() {
        m_uri = null;
        m_index = 0;
        m_name = "md-records";
    }
    
    public MetadataSetMarshaller(String uri, int index, String name) {
        m_uri = uri;
        m_index = index;
        m_name = name;
    }
    
    public boolean isExtension(int index) {
        return false;
    }
    
    public void marshal(Object obj, IMarshallingContext ictx)
        throws JiBXException {
        
        // make sure the parameters are as expected
        if (!(obj instanceof List)) {
            throw new JiBXException("Invalid object type for marshaller");
        } else if (!(ictx instanceof MarshallingContext)) {
            throw new JiBXException("Invalid object type for marshaller");
        } else {
            
            // start by generating start tag for container
            MarshallingContext ctx = (MarshallingContext)ictx;
            List<MetadataSetVO> list = (List<MetadataSetVO>)obj;
            ctx.startTagAttributes(m_index, m_name).closeStartContent();
            
            // loop through all entries in hashmap
            Iterator<MetadataSetVO> iter = list.iterator();
            boolean first = true;
            while (iter.hasNext()) {
                MetadataSetVO entry = (MetadataSetVO)iter.next();
                ctx.startTagAttributes(m_index, RECORD_ELEMENT_NAME);
                
                logger.debug("m_index: " + m_index);
                
                if (first) {
                    ctx.attribute(0, NAME_ATTRIBUTE_NAME,
                        "escidoc");
                }
                ctx.closeStartContent();
                if (entry instanceof IMarshallable) {
                    ((IMarshallable)entry).marshal(ctx);
                    ctx.endTag(m_index, RECORD_ELEMENT_NAME);
                } else {
                    throw new JiBXException("Mapped value is not marshallable");
                }
                first = false;
            }
            
            // finish with end tag for container element
            ctx.endTag(m_index, m_name);
        }
    }

    public boolean isPresent(IUnmarshallingContext ictx)
            throws JiBXException {
        return ictx.isAt(m_uri, m_name);
    }

    /* (non-Javadoc)
     * @see org.jibx.runtime.IUnmarshaller#unmarshal(java.lang.Object,
     *  org.jibx.runtime.IUnmarshallingContext)
     */
//    public Object unmarshal(Object obj, IUnmarshallingContext ictx)
//        throws JiBXException {
//        
//        // make sure we're at the appropriate start tag
//        UnmarshallingContext ctx = (UnmarshallingContext)ictx;
//        if (!ctx.isAt(m_uri, m_name)) {
//            ctx.throwStartTagNameError(m_uri, m_name);
//        }
//        
//        // lookup the prefixes assigned to required namespaces
//        int nscnt = ctx.getActiveNamespaceCount();
//        String xsdlead = null;
//        for (int i = nscnt-1; i >= 0; i--) {
//            String uri = ctx.getActiveNamespaceUri(i);
//            if (XSD_NAMESPACE_URI.equals(uri)) {
//                String prefix = ctx.getActiveNamespacePrefix(i);
//                if (!"".equals(prefix)) {
//                    xsdlead = prefix + ':';
//                    break;
//                }
//            }
//        }
//        if (xsdlead == null) {
//            throw new JiBXException
//                ("Missing required schema namespace declaration");
//        }
//        
//        // create new hashmap if needed
//        List<MetadataSetVO> list = (List<MetadataSetVO>) obj;
//        if (list == null) {
//            list = new ArrayList<MetadataSetVO>();
//        }
//        
//        // process all entries present in document
//        ctx.parsePastStartTag(m_uri, m_name);
//        String tdflt = xsdlead + "string";
//        while (ctx.isAt(m_uri, RECORD_ELEMENT_NAME))
//        {
//            
//            // unmarshal name from start tag attributes
//            Object name = ctx.attributeText(m_uri, NAME_ATTRIBUTE_NAME, "escidoc");
//            
//            // deserialize content as specified type
//            String text = ctx.parseElementText(m_uri, RECORD_ELEMENT_NAME);
//            ctx.
//            
//            // add key-value pair to map
//            list.add(value);
//        }
//        
//        // finish by skipping past wrapper end tag
//        ctx.parsePastEndTag(m_uri, m_name);
//        return list;
//    }
}