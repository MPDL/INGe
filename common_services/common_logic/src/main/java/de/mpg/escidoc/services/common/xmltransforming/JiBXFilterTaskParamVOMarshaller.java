/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.common.xmltransforming;

import org.jibx.runtime.IAliasable;
import org.jibx.runtime.IMarshaller;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.MarshallingContext;

import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.AffiliationRefFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.ContextFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.FrameworkContextTypeFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.FrameworkItemTypeFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.ItemPublicStatusFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.ItemRefFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.ItemStatusFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.LimitFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.ObjectTypeFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.OffsetFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.OrderFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.OwnerFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.PubCollectionStatusFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.RoleFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.TopLevelAffiliationFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.UserAccountStateFilter;

/**
 * This class is a helper class for JiBX binding of TaskParamVO (and subclasses) to XML.
 *
 * @author Johannes Mueller (initial creation)
 * @version $Revision: 613 $ $LastChangedDate: 2007-11-07 17:45:28 +0100 (Wed, 07 Nov 2007) $ by $Author: jmueller $
 * @revised by MuJ: 03.09.2007
 */
public class JiBXFilterTaskParamVOMarshaller implements IMarshaller, IAliasable
{
    private static final String FILTER_ELEMENT_NAME = "filter";
    private static final String NAME_ATTRIBUTE_NAME = "name";
    private static final String ID_ELEMENT_NAME = "id";

    private String m_uri;
    private int m_index;
    private String m_name;

    /**
     * The standard constructor.
     */
    public JiBXFilterTaskParamVOMarshaller()
    {
        m_uri = null;
        m_index = 0;
        m_name = "hashmap";
    }

    /**
     * Constructor assigning uri, index and name of the start node.
     * 
     * @param uri
     * @param index
     * @param name
     */
    public JiBXFilterTaskParamVOMarshaller(String uri, int index, String name)
    {
        m_uri = uri;
        m_index = index;
        m_name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jibx.runtime.IMarshaller#isExtension(int)
     */
    public boolean isExtension(int index)
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jibx.runtime.IMarshaller#marshal(Object, IMarshallingContext)
     */
    public void marshal(Object obj, IMarshallingContext ictx) throws JiBXException
    {

        // make sure the parameters are as expected
        if (!(obj instanceof FilterTaskParamVO))
        {
            throw new JiBXException("Invalid object type for marshaller");
        }
        else if (!(ictx instanceof MarshallingContext))
        {
            throw new JiBXException("Invalid object type for marshaller");
        }
        else
        {

            // start by generating start tag for container
            MarshallingContext ctx = (MarshallingContext)ictx;
            FilterTaskParamVO filterTaskParamVO = (FilterTaskParamVO)obj;
            ctx.startTag(m_index, m_name).closeStartContent();

            // loop through all entries in the filter list
            for (Filter filter : filterTaskParamVO.getFilterList())
            {

                // create 'filter' tag with 'name' attribute
                //
                ctx.startTagAttributes(m_index, FILTER_ELEMENT_NAME);
                if (filter instanceof FrameworkItemTypeFilter)
                {
                    ctx.attribute(m_index, NAME_ATTRIBUTE_NAME, "http://escidoc.de/core/01/structural-relations/content-model");
                }
                else if (filter instanceof FrameworkContextTypeFilter)
                {
                    ctx.attribute(m_index, NAME_ATTRIBUTE_NAME, "http://escidoc.de/core/01/properties/type"); //context-type
                }
                else if (filter instanceof OwnerFilter)
                {
                    ctx.attribute(m_index, NAME_ATTRIBUTE_NAME, "http://escidoc.de/core/01/structural-relations/created-by"); //created-by
                }
                else if (filter instanceof ItemRefFilter)
                {
                    ctx.attribute(m_index, NAME_ATTRIBUTE_NAME, "http://purl.org/dc/elements/1.1/identifier"); //items
                }
                else if (filter instanceof AffiliationRefFilter)
                {
                    ctx.attribute(m_index, NAME_ATTRIBUTE_NAME, "http://purl.org/dc/elements/1.1/identifier"); //organizational-units
                }
                else if (filter instanceof RoleFilter)
                {
                    ctx.attribute(m_index, NAME_ATTRIBUTE_NAME, "role"); 
                }
                else if (filter instanceof PubCollectionStatusFilter)
                {
                    ctx.attribute(m_index, NAME_ATTRIBUTE_NAME, "http://escidoc.de/core/01/properties/public-status"); //public-status
                }
                else if (filter instanceof ItemStatusFilter)
                {
                    ctx.attribute(m_index, NAME_ATTRIBUTE_NAME, "http://escidoc.de/core/01/properties/version/status"); //latest-version-status (according to FIZ, only latest versions are filtered)
                }
                else if (filter instanceof TopLevelAffiliationFilter)
                {
                    ctx.attribute(m_index, NAME_ATTRIBUTE_NAME, "top-level-organizational-units"); //see OrgUnitHandler - Method retrieveOrganizationalUnits()
                }
                else if (filter instanceof ObjectTypeFilter)
                {
                    ctx.attribute(m_index, NAME_ATTRIBUTE_NAME, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"); //object-type
                }
                else if (filter instanceof ContextFilter)
                {
                    ctx.attribute(m_index, NAME_ATTRIBUTE_NAME, "http://escidoc.de/core/01/structural-relations/context"); 
                }
                else if (filter instanceof ItemPublicStatusFilter)
                {
                    ctx.attribute(m_index, NAME_ATTRIBUTE_NAME, "http://escidoc.de/core/01/properties/public-status"); //public-status
                }
                else if (filter instanceof UserAccountStateFilter)
                {
                    ctx.attribute(m_index, NAME_ATTRIBUTE_NAME, "http://escidoc.de/core/01/properties/active"); //public-status
                }
                ctx.closeStartContent();

                // create filter content (the "top-level-organizational-units" filter needs no content)
                //
                if (filter instanceof AffiliationRefFilter)
                {
                    AffiliationRefFilter affiliationRefFilter = (AffiliationRefFilter)filter;

                    // loop through all entries in the id list
                    for (AffiliationRO affiliationRO : affiliationRefFilter.getIdList())
                    {
                        ctx.startTag(m_index, ID_ELEMENT_NAME).closeStartContent();
                        ctx.content(affiliationRO.getObjectId());
                        ctx.endTag(m_index, ID_ELEMENT_NAME);
                    }

                }
                else if (filter instanceof ItemRefFilter)
                {
                    ItemRefFilter itemRefFilter = (ItemRefFilter)filter;

                    // loop through all entries in the id list
                    for (ItemRO itemRO : itemRefFilter.getIdList())
                    {
                        ctx.startTag(m_index, ID_ELEMENT_NAME).closeStartContent();
                        ctx.content(itemRO.getObjectId());
                        ctx.endTag(m_index, ID_ELEMENT_NAME);
                    }

                }
                else if (filter instanceof OwnerFilter)
                {
                    OwnerFilter ownerFilter = (OwnerFilter)filter;
                    ctx.content(ownerFilter.getUserRef().getObjectId());
                }
                else if (filter instanceof RoleFilter)
                {
                    RoleFilter roleFilter = (RoleFilter)filter;
                    ctx.content(roleFilter.getRole());
                }
                else if (filter instanceof PubCollectionStatusFilter)
                {
                    PubCollectionStatusFilter pubCollectionStatusFilter = (PubCollectionStatusFilter)filter;
                    ctx.content(pubCollectionStatusFilter.getState().toString().replace('_', '-').toLowerCase());
                }
                else if (filter instanceof ItemStatusFilter)
                {
                    ItemStatusFilter itemStatusFilter = (ItemStatusFilter)filter;
                    ctx.content(itemStatusFilter.getState().toString().replace('_', '-').toLowerCase());
                }
                else if (filter instanceof FrameworkItemTypeFilter)
                {
                    FrameworkItemTypeFilter frameworkItemTypeFilter = (FrameworkItemTypeFilter)filter;
                    ctx.content(frameworkItemTypeFilter.getType());
                }
                else if (filter instanceof FrameworkContextTypeFilter)
                {
                    FrameworkContextTypeFilter frameworkContextTypeFilter = (FrameworkContextTypeFilter)filter;
                    ctx.content(frameworkContextTypeFilter.getType());
                }
                else if (filter instanceof ObjectTypeFilter)
                {
                    ObjectTypeFilter objectTypeFilter = (ObjectTypeFilter)filter;
                    ctx.content(objectTypeFilter.getObjectType());
                }
                else if (filter instanceof ContextFilter)
                {
                    ContextFilter contextFilter = (ContextFilter)filter;
                    ctx.content(contextFilter.getContextId());
                }
                else if (filter instanceof ItemPublicStatusFilter)
                {
                    ItemPublicStatusFilter publicStatusFilter= (ItemPublicStatusFilter)filter;
                    ctx.content(publicStatusFilter.getState().toString().replace('_', '-').toLowerCase());
                }
                else if (filter instanceof UserAccountStateFilter)
                {
                    UserAccountStateFilter stateFilter= (UserAccountStateFilter)filter;
                    ctx.content(String.valueOf(stateFilter.getActive()));
                   
                }

                // finish filter element
                //
                ctx.endTag(m_index, FILTER_ELEMENT_NAME);

                // for RoleFilter, an additional "user" element has to be created...
                if (filter instanceof RoleFilter)
                {
                    RoleFilter roleFilter = (RoleFilter)filter;

                    // create 'filter' tag with 'name' attribute
                    ctx.startTagAttributes(m_index, FILTER_ELEMENT_NAME);
                    ctx.attribute(m_index, NAME_ATTRIBUTE_NAME, "user");
                    ctx.closeStartContent();

                    // create filter content
                    ctx.content(roleFilter.getUserRef().getObjectId());

                    // finish filter element
                    ctx.endTag(m_index, FILTER_ELEMENT_NAME);
                }
                
                if (filter instanceof OffsetFilter)
                {
                    OffsetFilter offsetFilter = (OffsetFilter) filter;
                    ctx.startTag(m_index, "offset");
                    ctx.content(offsetFilter.getOffset());
                    ctx.endTag(m_index, "offset");
                }
                
                else if (filter instanceof LimitFilter)
                {
                    LimitFilter limitFilter = (LimitFilter) filter;
                    ctx.startTag(m_index, "limit");
                    ctx.content(limitFilter.getLimit());
                    ctx.endTag(m_index, "limit");
                }
                
                else if (filter instanceof OrderFilter)
                {
                    OrderFilter orderFilter = (OrderFilter) filter;
                    ctx.startTag(m_index, "order-by");
                    //attribute sorting only in case sort order is descending
                    if (orderFilter.getSortOrder().equals(OrderFilter.ORDER_DESCENDING))
                    {
                        ctx.attribute(m_index, "sorting", orderFilter.getSortOrder());
                    }
                    ctx.content(orderFilter.getProperty());
                    ctx.endTag(m_index, "order-by");
                }

            }
            
            

            // finish with end tag for container element
            ctx.endTag(m_index, m_name);
        }
    }

    /**
     * @return the m_uri
     */
    public String getM_uri()
    {
        return m_uri;
    }

    /**
     * @param m_uri the m_uri to set
     */
    public void setM_uri(String m_uri)
    {
        this.m_uri = m_uri;
    }
}
