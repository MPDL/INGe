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

package de.mpg.escidoc.services.common.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.services.common.referenceobjects.AccountUserRO;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;

/**
 * Valueobject representing a filter taskParam.
 * 
 * @author Miriam Doelle (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @revised by MuJ: 28.08.2007
 */
public class FilterTaskParamVO extends ValueObject
{
    /**
     * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
     * 'de.mpg.escidoc.services.common.valueobjects.ItemVO; local class incompatible: stream classdesc
     * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286' that occur after
     * JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to be compiled twice for testing (once
     * for the Application Server, once for the local test).
     * 
     * @author Johannes Mueller
     */
  
    private List<Filter> filterList = new ArrayList<Filter>();

    /**
     * @return the filter
     */
    public List<Filter> getFilterList()
    {
        return filterList;
    }

    /**
     * The interface the various specialized filters are implementing.
     */
    public interface Filter extends Serializable
    {
    }

    /**
     * Class to filter by owner.
     */
    public class OwnerFilter implements Filter
    {
        private AccountUserRO userRef;

        /**
         * Creates a new filter instance with the given user reference
         * 
         * @param userRef The user reference of the owner.
         */
        public OwnerFilter(AccountUserRO userRef)
        {
            this.userRef = userRef;
        }

        /**
         * @return the userRef
         */
        public AccountUserRO getUserRef()
        {
            return userRef;
        }

        /**
         * @param userRef the userRef to set
         */
        public void setUserRef(AccountUserRO userRef)
        {
            this.userRef = userRef;
        }
    }

    /**
     * Class to filter by item references. As long as no common content item refs are defined we use the ItemRO.
     */
    public class ItemRefFilter implements Filter
    {
        /**
         * List of ids.
         */
        private List<ItemRO> idList = new ArrayList<ItemRO>();

        /**
         * Creates a new instance.
         */
        public ItemRefFilter()
        {
        }

        /**
         * Creates a new instance with a given list.
         */
        public ItemRefFilter(List<ItemRO> list)
        {
            this.idList = list;
        }

        /**
         * @return the idList
         */
        public List<ItemRO> getIdList()
        {
            return idList;
        }
    }

    /**
     * Class to filter by PubCollection status.
     */
    public class PubCollectionStatusFilter implements Filter
    {
        /**
         * The PubCollection state.
         */
        private ContextVO.State state;

        /**
         * Creates a new instance with the given state. As long as no common content item states are defined we use the
         * PubCollection state.
         * 
         * @param state The state to filter for.
         */
        public PubCollectionStatusFilter(ContextVO.State state)
        {
            this.state = state;
        }

        /**
         * @return the state
         */
        public ContextVO.State getState()
        {
            return state;
        }

        /**
         * @param state the state to set
         */
        public void setState(ContextVO.State state)
        {
            this.state = state;
        }
    }

    /**
     * Class to filter by item status.
     */
    public class ItemStatusFilter implements Filter
    {
        /**
         * The item state.
         */
        private ItemVO.State state;

        /**
         * Creates a new instance with the given state. As long as no common content item states are defined we use the
         * ItemVO state.
         * 
         * @param state The state to filter for.
         */
        public ItemStatusFilter(ItemVO.State state)
        {
            this.state = state;
        }

        /**
         * @return the state
         */
        public ItemVO.State getState()
        {
            return state;
        }

        /**
         * @param state the state to set
         */
        public void setState(ItemVO.State state)
        {
            this.state = state;
        }
    }

    
    /**
     * Class to filter by item public status.
     */
    public class ItemPublicStatusFilter implements Filter
    {
        /**
         * The item state.
         */
        private ItemVO.State state;

        /**
         * Creates a new instance with the given state. As long as no common content item states are defined we use the
         * ItemVO state.
         * 
         * @param state The state to filter for.
         */
        public ItemPublicStatusFilter(ItemVO.State state)
        {
            this.state = state;
        }

        /**
         * @return the state
         */
        public ItemVO.State getState()
        {
            return state;
        }

        /**
         * @param state the state to set
         */
        public void setState(ItemVO.State state)
        {
            this.state = state;
        }
    }
    
    /**
     * Class to filter by Role.
     */
    public class RoleFilter implements Filter
    {
        /**
         * The role to filter for.
         */
        private String role;
        /**
         * The user that has the given role.
         */
        private AccountUserRO userRef;

        /**
         * Creates a new filter instance with the given role and user. To use the role filter the user is mandatory.
         * 
         * @param role The role to filter for.
         * @param user The user that has the given role.
         */
        public RoleFilter(String role, AccountUserRO user)
        {
            this.role = role;
            this.userRef = user;
        }

        /**
         * @return the role
         */
        public String getRole()
        {
            return role;
        }

        /**
         * @param role the role to set
         */
        public void setRole(String role)
        {
            this.role = role;
        }

        /**
         * @return the user
         */
        public AccountUserRO getUserRef()
        {
            return userRef;
        }

        /**
         * @param userRef the user to set
         */
        public void setUserRef(AccountUserRO userRef)
        {
            this.userRef = userRef;
        }
    }

    /**
     * Class to filter by framework item (content) type.
     */
    public class FrameworkItemTypeFilter implements Filter
    {
        /**
         * The framework item (content) type to filter for.
         */
        private String type;

        /**
         * Creates a new instance with the given (content) type.
         * 
         * @param type The framework item (content) type to filter for.
         */
        public FrameworkItemTypeFilter(String type)
        {
            this.type = type;
        }

        /**
         * @return The framework item (content) type to filter for.
         */
        public String getType()
        {
            return type;
        }

        /**
         * Sets the type filter.
         * 
         * @param type The framework item (content) type to set
         */
        public void setType(String type)
        {
            this.type = type;
        }
    }

    /**
     * Class to filter by framework context type.
     */
    public class FrameworkContextTypeFilter implements Filter
    {
        /**
         * The framework context type to filter for.
         */
        private String type;

        /**
         * Creates a new instance with the given type.
         * 
         * @param type The framework context type to filter for.
         */
        public FrameworkContextTypeFilter(String type)
        {
            this.type = type;
        }

        /**
         * @return The framework context type to filter for.
         */
        public String getType()
        {
            return type;
        }

        /**
         * Sets the type filter.
         * 
         * @param type The framework context type to set
         */
        public void setType(String type)
        {
            this.type = type;
        }
    }

    /**
     * Class to filter by affiliation references.
     */
    public class AffiliationRefFilter implements Filter
    {
        /**
         * List of ids.
         */
        private List<AffiliationRO> idList = new ArrayList<AffiliationRO>();

        /**
         * Creates a new instance.
         */
        public AffiliationRefFilter()
        {
        }

        /**
         * @return the idList
         */
        public List<AffiliationRO> getIdList()
        {
            return idList;
        }
    }

    /**
     * Class to filter all top level affiliations.
     */
    public class TopLevelAffiliationFilter implements Filter
    {
        /**
         * Creates a new instance.
         */
        public TopLevelAffiliationFilter()
        {
        }
        // This type of filter does not need further information
    }
    
    /**
     * 
     * Class to filter by object type (e.g. item or container)
     *
     * @author Markus Haarlaender (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     *
     */
    public class ObjectTypeFilter implements Filter
    {
        
        public final static String OBJECT_TYPE_ITEM = "http://escidoc.de/core/01/resources/Item";
        public final static String OBJECT_TYPE_CONTAINER = "http://escidoc.de/core/01/resources/Container";
        public final static String OBJECT_TYPE_ORGANIZATIONAL_UNIT = "http://escidoc.de/core/01/resources/OrganizationalUnit";
        
        
        private String objectType;
        
        public ObjectTypeFilter(String objectType)
        {
            this.objectType = objectType;
        }

        public String getObjectType()
        {
            return objectType;
        }

        public void setObjectType(String objectType)
        {
            this.objectType = objectType;
        }
    }
    
    /**
     * 
     * Class to specify an offset for handlers that return lists. If the OffsetFilter is specified, only the results from the offset are returned.
     *
     * @author Markus Haarlaender (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     *
     */
    public class OffsetFilter implements Filter
    {
        private String offset;

        public OffsetFilter(String offset)
        {
            super();
            this.offset = offset;
        }

        public String getOffset()
        {
            return offset;
        }

        public void setOffset(String offset)
        {
            this.offset = offset;
        }
        
        
    }
    
    /**
     * 
     * Class to specify a list limit for handlers that return lists. Only the number of list entries is returned that is specified with the limit value.
     *
     * @author Markus Haarlaender (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     *
     */
    public class LimitFilter implements Filter
    {
        private String limit;

        public LimitFilter(String limit)
        {
            super();
            this.limit = limit;
        }

        public String getLimit()
        {
            return limit;
        }

        public void setLimit(String limit)
        {
            this.limit = limit;
        }

        
        
        
    }
    
    /**
     * 
     * This filter orders a returned list by the given property and sorting order;
     *
     * @author Markus Haarlaender (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     *
     */
    public class OrderFilter implements Filter
    {
        public final static String ORDER_ASCENDING = "ascending";
        public final static String ORDER_DESCENDING = "descending";
        private String property;
        private String sortOrder;
        
        public OrderFilter(String property, String sortOrder)
        {
            super();
            this.property = property;
            this.sortOrder = sortOrder;
        }

        public String getProperty()
        {
            return property;
        }

        public void setProperty(String property)
        {
            this.property = property;
        }

        public String getSortOrder()
        {
            return sortOrder;
        }

        public void setSortOrder(String sortOrder)
        {
            this.sortOrder = sortOrder;
        }
        
        

        
        
        
        
    }
    
    /**
     * 
     * This filter filters by context id
     *
     * @author Markus Haarlaender (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     *
     */
    public class ContextFilter implements Filter
    {
        
        private String contextId;

        public ContextFilter(String contextId)
        {
            super();
            this.contextId = contextId;
        }

        public String getContextId()
        {
            return contextId;
        }

        public void setContextId(String contextId)
        {
            this.contextId = contextId;
        }
        
        
    }
    
    /**
     * 
     * This filter filters by context id
     *
     * @author Markus Haarlaender (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     *
     */
    public class LocalTagFilter implements Filter
    {
        
        private String localTagId;

        public LocalTagFilter(String localTagId)
        {
            super();
            this.localTagId = localTagId;
        }

        /**
         * @return the localTagId
         */
        public String getLocalTagId()
        {
            return localTagId;
        }

        /**
         * @param localTagId the localTagId to set
         */
        public void setLocalTagId(String localTagId)
        {
            this.localTagId = localTagId;
        }

    }
    
    /**
     * 
     * This filter filters by context id
     *
     * @author Markus Haarlaender (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     *
     */
    public class UserAccountStateFilter implements Filter
    {
       
        
        private boolean active;

        public boolean getActive()
        {
            return active;
        }

        public void setActive(boolean active)
        {
            this.active = active;
        }

        public UserAccountStateFilter(boolean active)
        {
            this.active = active;
        }

        
        
        
    }
    
    /**
     * 
     * This filter filters pub items by the id of the person's organization
     *
     * @author Markus Haarlaender (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     *
     */
    
    public class PersonsOrganizationsFilter implements Filter
    {
        
        private String orgUnitId;

        public PersonsOrganizationsFilter(String  orgUnitId)
        {
            super();
            this.orgUnitId = orgUnitId;
        }

        public String getOrgUnitId()
        {
            return orgUnitId;
        }

        public void setOrgUnitId(String contextId)
        {
            this.orgUnitId = contextId;
        }
        
        
    }
    
    
    
}
