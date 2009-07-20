package de.mpg.escidoc.services.common.xmltransforming;

import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jibx.runtime.IAliasable;
import org.jibx.runtime.IMarshallable;
import org.jibx.runtime.IMarshaller;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshaller;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.IXMLReader;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.MarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;

import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.MetadataSetVO;
import de.mpg.escidoc.services.common.valueobjects.RelationVO;
import de.mpg.escidoc.services.common.valueobjects.TocDivVO;
import de.mpg.escidoc.services.common.valueobjects.TocItemVO;
import de.mpg.escidoc.services.common.valueobjects.TocPtrVO;
import de.mpg.escidoc.services.common.valueobjects.RelationVO.RelationType;

public class JibXTocDivMarshaller implements IMarshaller, IUnmarshaller, IAliasable //, IUnmarshaller
{
    private static Logger logger = Logger.getLogger(JibXTocDivMarshaller.class);
    
    
    private static final String DIV_ID_ATTRIBUTE_NAME = "ID";
    private static final String DIV_ORDER_ATTRIBUTE_NAME = "ORDER";
    private static final String DIV_ORDERLABEL_ATTRIBUTE_NAME = "ORDERLABEL";
    private static final String DIV_LABEL_ATTRIBUTE_NAME = "LABEL";
    private static final String DIV_TYPE_ATTRIBUTE_NAME = "TYPE";
    private static final String DIV_VISIBLE_ATTRIBUTE_NAME = "visible";
    
    
    private static final String DIV_ELEMENT_NAME = "div";
    private static final String PTR_ELEMENT_NAME = "ptr";
    
    private static final String PTR_ID_ATTRIBUTE_NAME = "ID";
    private static final String PTR_LOCTYPE_ATTRIBUTE_NAME = "LOCTYPE";
    private static final String PTR_USE_ATTRIBUTE_NAME = "USE";
    private static final String PTR_MIMETYPE_ATTRIBUTE_NAME = "MIMETYPE";
    
    private static final String PTR_REFTYPE_ATTRIBUTE_NAME = "type";
    private static final String PTR_REFTITLE_ATTRIBUTE_NAME = "title";
    private static final String PTR_REFLINK_ATTRIBUTE_NAME = "href";
   
   
    
    private String m_uri;
    private int m_index;
    private String m_name;
    
    private static final String XSI_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String XSD_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema";
    private static final String TABLE_OF_CONTENT_NS = "http://www.escidoc.de/schemas/tableofcontent/0.1";
    private static final String XLINK_NS = "http://www.w3.org/1999/xlink";
    
    
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

    public JibXTocDivMarshaller()
    {
        m_uri = null;
        m_index = 0;
        m_name = "div";
    }
    
    public JibXTocDivMarshaller(String uri, int index, String name)
    {
        m_uri = uri;
        m_index = index;
        m_name = name;
    }
    
    public boolean isExtension(int index) {
        return false;
    }
    
    public void marshal(Object obj, IMarshallingContext ictx)
        throws JiBXException
        {
        
        // make sure the parameters are as expected
        if (!(obj instanceof TocDivVO))
        {
            throw new JiBXException("Invalid object type for marshaller");
        }
        else if (!(ictx instanceof MarshallingContext))
        {
            throw new JiBXException("Invalid object type for marshaller");
        }
        
           
            // start by generating start tag for container
            MarshallingContext ctx = (MarshallingContext) ictx;
            
            
            TocDivVO tocDivVO = (TocDivVO) obj;
            
            ctx.startTagAttributes(m_index, DIV_ELEMENT_NAME);
            if (tocDivVO.getId()!=null) ctx.attribute(0, DIV_ID_ATTRIBUTE_NAME, tocDivVO.getId());
            if (tocDivVO.getLabel()!=null) ctx.attribute(0, DIV_LABEL_ATTRIBUTE_NAME, tocDivVO.getLabel());
            if (tocDivVO.getOrder()!=-1) ctx.attribute(0, DIV_ORDER_ATTRIBUTE_NAME, tocDivVO.getOrder());
            if (tocDivVO.getOrderLabel()!=null) ctx.attribute(0, DIV_ORDERLABEL_ATTRIBUTE_NAME, tocDivVO.getOrderLabel());
            if (tocDivVO.getType()!=null) ctx.attribute(0, DIV_TYPE_ATTRIBUTE_NAME, tocDivVO.getType());
            if (tocDivVO.getVisible()!=null) ctx.attribute(0, DIV_VISIBLE_ATTRIBUTE_NAME, tocDivVO.getVisible());
            ctx.closeStartContent();
            
            if (tocDivVO.getTocPtrList()!=null) {
                //search for xlink namespace index
                int xlinkNsPos = 0;
                for(int i=0; i< ctx.getNamespaces().length; i++)
                {
                    if (ctx.getNamespaces()[i].equals(XLINK_NS))
                    {
                        xlinkNsPos=i;
                        break;
                    }
                }
                
                for(TocPtrVO tocPtrVO : tocDivVO.getTocPtrList())
                {
                    ctx.startTagAttributes(m_index, PTR_ELEMENT_NAME);
                    if (tocPtrVO.getId()!=null) ctx.attribute(0, PTR_ID_ATTRIBUTE_NAME, tocPtrVO.getId());
                    ctx.attribute(0, PTR_LOCTYPE_ATTRIBUTE_NAME, tocPtrVO.getLoctype());
                    if (tocPtrVO.getMimetype()!=null) ctx.attribute(0, PTR_MIMETYPE_ATTRIBUTE_NAME, tocPtrVO.getMimetype());
                    if (tocPtrVO.getUse()!=null) ctx.attribute(0, PTR_USE_ATTRIBUTE_NAME, tocPtrVO.getUse());
                    if (tocPtrVO.getLinkRef()!=null) ctx.attribute(xlinkNsPos, PTR_REFLINK_ATTRIBUTE_NAME, tocPtrVO.getLinkRef());
                    if (tocPtrVO.getLinkTitle()!=null) ctx.attribute(xlinkNsPos, PTR_REFTITLE_ATTRIBUTE_NAME, tocPtrVO.getLinkTitle());
                    if (tocPtrVO.getLinkType()!=null) ctx.attribute(xlinkNsPos, PTR_REFTYPE_ATTRIBUTE_NAME, tocPtrVO.getLinkType());
                    ctx.closeStartEmpty();
                    
                }
            }
            
            if (tocDivVO.getTocDivList()!=null) {
                for(TocDivVO tocDivVONew : tocDivVO.getTocDivList())
                {
                    marshal(tocDivVONew, ctx);
                    
                }
            }
            
            ctx.endTag(m_index, m_name);
            
            
            
            
           
            
            /*
                ctx.startTagAttributes(m_index, m_name).closeStartContent();
                
                // loop through all entries in hashmap
                Iterator<MetadataSetVO> iter = list.iterator();
                boolean first = true;
                while (iter.hasNext())
                {
                    MetadataSetVO entry = (MetadataSetVO) iter.next();
                    ctx.startTagAttributes(m_index, RECORD_ELEMENT_NAME);
                    
                    logger.debug("m_index: " + m_index);
                    
                    if (first)
                    {
                        ctx.attribute(0, NAME_ATTRIBUTE_NAME,
                            "escidoc");
                    }
                    ctx.closeStartContent();
                    if (entry instanceof IMarshallable)
                    {
                        ((IMarshallable) entry).marshal(ctx);
                        ctx.endTag(m_index, RECORD_ELEMENT_NAME);
                    }
                    else
                    {
                        throw new JiBXException("Mapped value is not marshallable (" + entry.getClass().getSimpleName() + ")");
                    }
                    first = false;
                }
                
                // finish with end tag for container element
                ctx.endTag(m_index, m_name);
            */
        
    }

    public boolean isPresent(IUnmarshallingContext ictx)
            throws JiBXException
    {
        return ictx.isAt(m_uri, m_name);
    }

    public Object unmarshal(Object obj, IUnmarshallingContext ictx) throws JiBXException
    {
        // make sure the parameters are as expected
      
        if (!(ictx instanceof UnmarshallingContext))
        {
            throw new JiBXException("Invalid object type for unmarshaller");
        }

        // make sure we're at the appropriate start tag
        UnmarshallingContext ctx = (UnmarshallingContext)ictx;
        if (!ctx.isAt(m_uri, m_name))
        {
            ctx.throwStartTagNameError(m_uri, m_name);
        }

        // create new List<RelationVO>
        TocDivVO tocDivVO = new TocDivVO();
        tocDivVO.setTocDivList(new ArrayList<TocDivVO>());
        tocDivVO.setTocPtrList(new ArrayList<TocPtrVO>());

      //attributes of div-tag
        tocDivVO.setId(ctx.attributeText(null, DIV_ID_ATTRIBUTE_NAME, null));
        tocDivVO.setLabel(ctx.attributeText(null, DIV_LABEL_ATTRIBUTE_NAME, null));
        tocDivVO.setOrder(ctx.attributeInt(null, DIV_ORDER_ATTRIBUTE_NAME, 0));
        tocDivVO.setOrderLabel(ctx.attributeText(null, DIV_ORDERLABEL_ATTRIBUTE_NAME, null));
        tocDivVO.setType(ctx.attributeText(null, DIV_TYPE_ATTRIBUTE_NAME, null));
        tocDivVO.setType(ctx.attributeText(null, DIV_TYPE_ATTRIBUTE_NAME, null));
        tocDivVO.setVisible(ctx.attributeText(null, DIV_VISIBLE_ATTRIBUTE_NAME, null));
            
        ctx.parsePastStartTag(m_uri, m_name);
        
        while(ctx.isStart())
        {

                String currentTagName = ctx.getElementName();
                String currentTagNS = ctx.getElementNamespace();
                
                if (currentTagNS!=null && currentTagNS.equals(TABLE_OF_CONTENT_NS))
                {
                    if (currentTagName.equals(DIV_ELEMENT_NAME))
                    {
                        //recursive call of unmarshaller
                        TocDivVO tocDivNew = (TocDivVO) unmarshal(new TocDivVO(), ictx);
                        tocDivVO.getTocDivList().add(tocDivNew);
                        //ctx.parsePastEndTag(TABLE_OF_CONTENT_NS, DIV_ELEMENT_NAME);
                    }
                    
                    else if (currentTagName.equals(PTR_ELEMENT_NAME))
                    {
                        TocPtrVO tocPtrVO = new TocPtrVO();
                        tocDivVO.getTocPtrList().add(tocPtrVO);
                        
                        //attributes of ptr
                        tocPtrVO.setId(ctx.attributeText(null, PTR_ID_ATTRIBUTE_NAME));
                        tocPtrVO.setLoctype(ctx.attributeText(null, PTR_LOCTYPE_ATTRIBUTE_NAME, "URI"));
                        tocPtrVO.setUse(ctx.attributeText(null, PTR_USE_ATTRIBUTE_NAME, null));
                        tocPtrVO.setMimetype(ctx.attributeText(null, PTR_MIMETYPE_ATTRIBUTE_NAME, null));
                        tocPtrVO.setLinkTitle(ctx.attributeText(XLINK_NS, PTR_REFTITLE_ATTRIBUTE_NAME));
                        tocPtrVO.setLinkType(ctx.attributeText(XLINK_NS, PTR_REFTYPE_ATTRIBUTE_NAME));
                        tocPtrVO.setLinkRef(ctx.attributeText(XLINK_NS, PTR_REFLINK_ATTRIBUTE_NAME));
                        
                        ctx.parsePastEndTag(TABLE_OF_CONTENT_NS, PTR_ELEMENT_NAME);

                    }
                    
                    
                }
                else
                {
                    //ignore additional metadata
                   ctx.parsePastElement(currentTagNS, currentTagName);
                }
                
                        
                    
            
        }
       
       ctx.parsePastEndTag(TABLE_OF_CONTENT_NS, DIV_ELEMENT_NAME);
       return tocDivVO;

    }

    public boolean isExtension(String arg0)
    {
        // TODO Auto-generated method stub
        return false;
    }

}