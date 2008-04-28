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
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $
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
}
