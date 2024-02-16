package de.mpg.mpdl.inge.model.xmltransforming.xmltransforming;

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

import de.mpg.mpdl.inge.model.valueobjects.MetadataSetVO;

public class MetadataSetMarshaller implements IMarshaller, IAliasable {
  private static final Logger logger = Logger.getLogger(MetadataSetMarshaller.class);

  private static final String NAME_ATTRIBUTE_NAME = "name";
  private static final String RECORD_ELEMENT_NAME = "md-record";
  // private static final String DEFAULT_NAME = "escidoc";
  // private static final String XSI_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema-instance";
  // private static final String XSD_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema";
  // private static final String[] SCHEMA_NAMESPACE_URIS = {XSI_NAMESPACE_URI, XSD_NAMESPACE_URI};
  // private static final String XSI_NAMESPACE_PREFIX = "xsi";
  // private static final String XSD_NAMESPACE_PREFIX = "xsd";
  // private static final String[] SCHEMA_NAMESPACE_PREFIXES = {XSI_NAMESPACE_PREFIX,
  // XSD_NAMESPACE_PREFIX};
  // private static final String XSD_PREFIX_LEAD = "xsd:";

  private final String m_uri;
  private final int m_index;
  private final String m_name;

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

  @Override
  public void marshal(Object obj, IMarshallingContext ictx) throws JiBXException {

    // make sure the parameters are as expected
    if (!(obj instanceof List)) {
      throw new JiBXException("Invalid object type for marshaller");
    } else if (!(ictx instanceof MarshallingContext ctx)) {
      throw new JiBXException("Invalid object type for marshaller");
    } else {

      // start by generating start tag for container
      List<MetadataSetVO> list = (List<MetadataSetVO>) obj;
      if (!list.isEmpty()) {
        ctx.startTagAttributes(m_index, m_name).closeStartContent();

        // loop through all entries in hashmap
        Iterator<MetadataSetVO> iter = list.iterator();
        boolean first = true;
        while (iter.hasNext()) {
          MetadataSetVO entry = iter.next();
          ctx.startTagAttributes(m_index, RECORD_ELEMENT_NAME);

          logger.debug("m_index: " + m_index);

          if (first) {
            ctx.attribute(0, NAME_ATTRIBUTE_NAME, "escidoc");
          }
          ctx.closeStartContent();
          if (entry instanceof IMarshallable) {
            ((IMarshallable) entry).marshal(ctx);
            ctx.endTag(m_index, RECORD_ELEMENT_NAME);
          } else {
            throw new JiBXException("Mapped value is not marshallable (" + entry.getClass().getSimpleName() + ")");
          }
          first = false;
        }

        // finish with end tag for container element
        ctx.endTag(m_index, m_name);
      }
    }
  }

  public boolean isPresent(IUnmarshallingContext ictx) throws JiBXException {
    return ictx.isAt(m_uri, m_name);
  }

  @Override
  public boolean isExtension(String arg0) {
    return false;
  }
}
