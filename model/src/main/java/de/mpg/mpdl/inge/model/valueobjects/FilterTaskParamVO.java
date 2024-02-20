/*
 *
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.model.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.inge.model.referenceobjects.AccountUserRO;
import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;

/**
 * Valueobject representing a filter taskParam.
 *
 * @author Miriam Doelle (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @revised by MuJ: 28.08.2007
 */
@SuppressWarnings("serial")
public class FilterTaskParamVO extends ValueObject {
  private static final String RIGHT_PARANTHESIS = " ) ";
  private static final String LEFT_PARANTHESIS = " ( ";
  private static final String OR = " or ";
  private static final String AND = " and ";

  private final List<Filter> filterList = new ArrayList<>();

  private static final Logger logger = LogManager.getLogger(FilterTaskParamVO.class);

  /**
   * @return the filter
   */
  public List<Filter> getFilterList() {
    return this.filterList;
  }

  /*
   * @return a Map representation of the FilterTaskParamVO object used for infrastructure SOAP
   * queries
   */
  public HashMap<String, String[]> toMap() {
    Filter previousFilter = null;
    StringBuffer queryBuffer = new StringBuffer(1024);
    String sorting = "";

    HashMap<String, String[]> filterMap = new HashMap<>();
    filterMap.put("operation", new String[] {"searchRetrive"});
    filterMap.put("version", new String[] {"1.1"});

    // the List is sorted corresponding to the filter class names,
    // so filters of the same type occur one after another in the List after sorting.
    // Filter of the same class are composed by "or" in the query, filters of different classes by
    // "and".
    Collections.sort(this.filterList);

    // loop through all entries in the filter list
    for (Filter filter : this.filterList) {
      if (filter instanceof OffsetFilter offsetFilter) {
        String offset = offsetFilter.getOffset();
        int newOffset = Integer.parseInt(offset) + 1;
        filterMap.put("startRecord", new String[] {Integer.toString(newOffset)});
        previousFilter = filter;
      } else if (filter instanceof LimitFilter limitFilter) {
        filterMap.put("maximumRecords", new String[] {limitFilter.getLimit()});
        previousFilter = filter;
      } else if (filter instanceof OrderFilter orderFilter) {

        StringTokenizer tok = new StringTokenizer(orderFilter.getProperty());
        while (tok.hasMoreTokens()) {
          sorting += " sortby" + "\"" + tok.nextToken() + "\"/";
          sorting += orderFilter.getSortOrder();
        }
        previousFilter = filter;
      }

      else {
        // here the queryString for the "query" key is built
        if (filter instanceof FrameworkItemTypeFilter) {
          enhanceQuery(queryBuffer, "\"/properties/content-model/id\"=" + ((FrameworkItemTypeFilter) filter).getType(), previousFilter,
              filter);
        } else if (filter instanceof FrameworkContextTypeFilter) {
          enhanceQuery(queryBuffer,
              "\"/properties/type\"=" + ((FrameworkContextTypeFilter) filter).getType().replace('_', '-').toLowerCase(),
              previousFilter, filter);
        } else if (filter instanceof OwnerFilter) {
          enhanceQuery(queryBuffer, "\"/properties/created-by/id\"=" + ((OwnerFilter) filter).getUserRef().getObjectId(), previousFilter,
              filter);
        } else if (filter instanceof ItemRefFilter) {
          enhanceQuery(queryBuffer, "\"/id\" any", previousFilter, filter);
        } else if (filter instanceof ItemRefVersionFilter) {
          enhanceQuery(queryBuffer, "\"/properties/version/id\" any", previousFilter, filter);
        } else if (filter instanceof AffiliationRefFilter) {
          enhanceQuery(queryBuffer, "\"/id\"=", previousFilter, filter);
        } else if (filter instanceof RoleFilter) {
          enhanceQuery(queryBuffer, "\"/role\"=" + ((RoleFilter) filter).getRole(), previousFilter, filter);
          previousFilter = filter;
          enhanceQuery(queryBuffer, "\"/user\"=" + ((RoleFilter) filter).getUserRef().getObjectId(), previousFilter, filter);
        } else if (filter instanceof PubCollectionStatusFilter) {
          enhanceQuery(queryBuffer,
              "\"/properties/public-status\"=" + ((PubCollectionStatusFilter) filter).getState().toString().replace('_', '-').toLowerCase(),
              previousFilter, filter);
        } else if (filter instanceof ItemStatusFilter) {
          enhanceQuery(queryBuffer,
              "\"/properties/version/status\"=" + ((ItemStatusFilter) filter).getState().toString().replace('_', '-').toLowerCase(),
              previousFilter, filter);
        } else if (filter instanceof TopLevelAffiliationFilter) // todo
        {
          // ctx.attribute(m_index, NAME_ATTRIBUTE_NAME, "top-level-organizational-units"); //see
          // OrgUnitHandler - Method retrieveOrganizationalUnits()
        } else if (filter instanceof ObjectTypeFilter) {
          enhanceQuery(queryBuffer, "\"/type\"=" + ((ObjectTypeFilter) filter).getObjectType(), previousFilter, filter);
        } else if (filter instanceof ContextFilter) {
          enhanceQuery(queryBuffer, "\"/properties/context/id\"=" + ((ContextFilter) filter).getContextId(), previousFilter, filter);
        } else if (filter instanceof LocalTagFilter) {
          enhanceQuery(queryBuffer,
              "\"/properties/content-model-specific/local-tags/local-tag\"=" + "\"" + ((LocalTagFilter) filter).getLocalTagId() + "\"",
              previousFilter, filter);
        } else if (filter instanceof ItemPublicStatusFilter) {
          enhanceQuery(queryBuffer,
              "\"/properties/public-status\"=" + ((ItemPublicStatusFilter) filter).getState().toString().replace('_', '-').toLowerCase(),
              previousFilter, filter);
        } else if (filter instanceof UserAccountStateFilter) {
          enhanceQuery(queryBuffer, "\"/properties/active\"=" + ((UserAccountStateFilter) filter).getActive(), previousFilter, filter);
        } else if (filter instanceof PersonsOrganizationsFilter) {
          enhanceQuery(queryBuffer, "\"/md-records/md-record/publication/creator/person/organization/identifier\"="
              + ((PersonsOrganizationsFilter) filter).getOrgUnitId(), previousFilter, filter);
        } else if (filter instanceof StandardFilter standardFilter) {
          if (null != standardFilter.getOperator()) {
            enhanceQuery(queryBuffer,
                "\"" + standardFilter.getFilterName() + "\"" + standardFilter.getOperator() + standardFilter.getValue(), previousFilter,
                filter);
          } else {
            enhanceQuery(queryBuffer, "\"" + standardFilter.getFilterName() + "\"=" + standardFilter.getValue(), previousFilter, filter);
          }
        } else if (filter instanceof CqlFilter cqlfilterFilter) {
          enhanceQuery(queryBuffer, cqlfilterFilter.getCql(), previousFilter, filter);
        }
        previousFilter = filter;
      }
    }
    queryBuffer.append(RIGHT_PARANTHESIS);

    queryBuffer.append(sorting);

    logger.info("query: " + queryBuffer);
    filterMap.put("query", new String[] {queryBuffer.toString()});

    return filterMap;
  }

  private void enhanceQuery(StringBuffer b, String querySnippet, Filter previousFilter, Filter filter) {
    logger.debug("snippet " + querySnippet);
    if (null == querySnippet)
      return;

    if (null == previousFilter && null != filter) {
      b.append(LEFT_PARANTHESIS);
      doAppend(b, querySnippet, filter);
      return;
    }

    if (null != previousFilter && null == filter) {
      // end ??
      return;
    }


    if (0 != filter.compareTo(previousFilter)) {
      // filter has changed - close the previous one, connect snippets with AND and brackets if
      // query has already been started
      if (!b.isEmpty()) {
        b.append(RIGHT_PARANTHESIS);
        b.append(AND);
      }
      b.append(LEFT_PARANTHESIS);
      doAppend(b, querySnippet, filter);

    } else {
      // filter has not changed - connect snippets with OR without brackets (except for RoleFilters)
      if (filter instanceof RoleFilter)
        b.append(AND);
      else if (filter instanceof StandardFilter && null != ((StandardFilter) filter).getLogicalOperator()
          && "and".equalsIgnoreCase(((StandardFilter) filter).getLogicalOperator())) {
        b.append(AND);
      } else
        b.append(OR);
      doAppend(b, querySnippet, filter);

    }
  }

  private void doAppend(StringBuffer b, String queryPiece, Filter filter) {
    int i = 0;
    if (filter instanceof AffiliationRefFilter affiliationRefFilter) {

      for (AffiliationRO affiliationRO : affiliationRefFilter.getIdList()) {
        b.append(queryPiece);
        b.append(affiliationRO.getObjectId());

        if (++i == affiliationRefFilter.getIdList().size())
          break;
        b.append(OR);
      }

    } else if (filter instanceof ItemRefFilter itemRefFilter) {
      b.append(queryPiece);

      for (ItemRO itemRO : itemRefFilter.getIdList()) {
        b.append(" ");
        b.append(itemRO.getObjectId());
        if (++i == itemRefFilter.getIdList().size())
          break;
      }

    } else if (filter instanceof ItemRefVersionFilter itemRefFilter) {
      b.append(queryPiece);

      for (ItemRO itemRO : itemRefFilter.getIdList()) {
        b.append(" ");
        b.append(itemRO.getObjectIdAndVersion());
        if (++i == itemRefFilter.getIdList().size())
          break;
      }

    } else {
      b.append(queryPiece);
    }
  }

  public String getOperator(Filter filter, Filter previousFilter) {
    if (null == filter || null == previousFilter)
      return "";
    if (0 == filter.compareTo(previousFilter))
      return OR;

    return AND;
  }


  /**
   * The interface the various specialized filters are implementing.
   */
  public interface Filter extends Serializable, Comparable<Object> {
  }



  public static abstract class AbstractFilter implements Comparable<Object> {
    public int compareTo(Object o) {
      return o.getClass().getName().compareTo(this.getClass().getName());
    }

    @Override
    public boolean equals(Object o) {
      return o instanceof AbstractFilter && compareTo((AbstractFilter) o) == 0;
    }
  }

  public static class CqlFilter extends AbstractFilter implements Filter {
    private String cql;

    public CqlFilter(String cql) {
      this.cql = cql;
    }

    public String getCql() {
      return this.cql;
    }

    public void setCql(String cql) {
      this.cql = cql;
    }
  }


  /**
   * Class to filter by owner.
   */
  public static class OwnerFilter extends AbstractFilter implements Filter {
    private AccountUserRO userRef;

    /**
     * Creates a new filter instance with the given user reference
     *
     * @param userRef The user reference of the owner.
     */
    public OwnerFilter(AccountUserRO userRef) {
      this.userRef = userRef;
    }

    /**
     * @return the userRef
     */
    public AccountUserRO getUserRef() {
      return this.userRef;
    }

    /**
     * @param userRef the userRef to set
     */
    public void setUserRef(AccountUserRO userRef) {
      this.userRef = userRef;
    }
  }

  /**
   * Class to filter by item references. As long as no common content item refs are defined we use
   * the ItemRO.
   */
  public static class ItemRefFilter extends AbstractFilter implements Filter {
    /**
     * List of ids.
     */
    private List<ItemRO> idList = new ArrayList<>();

    /**
     * Creates a new instance.
     */
    public ItemRefFilter() {}

    /**
     * Creates a new instance with a given list.
     */
    public ItemRefFilter(List<ItemRO> list) {
      this.idList = list;
    }

    /**
     * @return the idList
     */
    public List<ItemRO> getIdList() {
      return this.idList;
    }
  }

  /**
   * Class to filter by item references with version. As long as no common content item refs are
   * defined we use the ItemRO.
   */
  public static class ItemRefVersionFilter extends AbstractFilter implements Filter {
    /**
     * List of ids.
     */
    private List<ItemRO> idList = new ArrayList<>();

    /**
     * Creates a new instance.
     */
    public ItemRefVersionFilter() {}

    /**
     * Creates a new instance with a given list.
     */
    public ItemRefVersionFilter(List<ItemRO> list) {
      this.idList = list;
    }

    /**
     * @return the idList
     */
    public List<ItemRO> getIdList() {
      return this.idList;
    }
  }

  /**
   * Class to filter by PubCollection status.
   */
  public static class PubCollectionStatusFilter extends AbstractFilter implements Filter {

    /**
     * The PubCollection state.
     */
    private ContextVO.State state;

    /**
     * Creates a new instance with the given state. As long as no common content item states are
     * defined we use the PubCollection state.
     *
     * @param state The state to filter for.
     */
    public PubCollectionStatusFilter(ContextVO.State state) {
      this.state = state;
    }

    /**
     * @return the state
     */
    public ContextVO.State getState() {
      return this.state;
    }

    /**
     * @param state the state to set
     */
    public void setState(ContextVO.State state) {
      this.state = state;
    }
  }

  /**
   * Class to filter by item status.
   */
  public static class ItemStatusFilter extends AbstractFilter implements Filter {
    /**
     * The item state.
     */
    private ItemVO.State state;

    /**
     * Creates a new instance with the given state. As long as no common content item states are
     * defined we use the ItemVO state.
     *
     * @param state The state to filter for.
     */
    public ItemStatusFilter(ItemVO.State state) {
      this.state = state;
    }

    /**
     * @return the state
     */
    public ItemVO.State getState() {
      return this.state;
    }

    /**
     * @param state the state to set
     */
    public void setState(ItemVO.State state) {
      this.state = state;
    }

  }


  /**
   * Class to filter by item public status.
   */
  public static class ItemPublicStatusFilter extends AbstractFilter implements Filter {
    /**
     * The item state.
     */
    private ItemVO.State state;

    /**
     * Creates a new instance with the given state. As long as no common content item states are
     * defined we use the ItemVO state.
     *
     * @param state The state to filter for.
     */
    public ItemPublicStatusFilter(ItemVO.State state) {
      this.state = state;
    }

    /**
     * @return the state
     */
    public ItemVO.State getState() {
      return this.state;
    }

    /**
     * @param state the state to set
     */
    public void setState(ItemVO.State state) {
      this.state = state;
    }
  }

  /**
   * Class to filter by Role.
   */
  public static class RoleFilter extends AbstractFilter implements Filter {
    /**
     * The role to filter for.
     */
    private String role;
    /**
     * The user that has the given role.
     */
    private AccountUserRO userRef;

    /**
     * Creates a new filter instance with the given role and user. To use the role filter the user
     * is mandatory.
     *
     * @param role The role to filter for.
     * @param user The user that has the given role.
     */
    public RoleFilter(String role, AccountUserRO user) {
      this.role = role;
      this.userRef = user;
    }

    /**
     * @return the role
     */
    public String getRole() {
      return this.role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(String role) {
      this.role = role;
    }

    /**
     * @return the user
     */
    public AccountUserRO getUserRef() {
      return this.userRef;
    }

    /**
     * @param userRef the user to set
     */
    public void setUserRef(AccountUserRO userRef) {
      this.userRef = userRef;
    }

  }

  /**
   * Class to filter by framework item (content) type.
   */
  public static class FrameworkItemTypeFilter extends AbstractFilter implements Filter {
    /**
     * The framework item (content) type to filter for.
     */
    private String type;

    /**
     * Creates a new instance with the given (content) type.
     *
     * @param type The framework item (content) type to filter for.
     */
    public FrameworkItemTypeFilter(String type) {
      this.type = type;
    }

    /**
     * @return The framework item (content) type to filter for.
     */
    public String getType() {
      return this.type;
    }

    /**
     * Sets the type filter.
     *
     * @param type The framework item (content) type to set
     */
    public void setType(String type) {
      this.type = type;
    }
  }

  /**
   * Class to filter by framework context type.
   */
  public static class FrameworkContextTypeFilter extends AbstractFilter implements Filter {

    /**
     * The framework context type to filter for.
     */
    private String type;

    /**
     * Creates a new instance with the given type.
     *
     * @param type The framework context type to filter for.
     */
    public FrameworkContextTypeFilter(String type) {
      this.type = type;
    }

    /**
     * @return The framework context type to filter for.
     */
    public String getType() {
      return this.type;
    }

    /**
     * Sets the type filter.
     *
     * @param type The framework context type to set
     */
    public void setType(String type) {
      this.type = type;
    }
  }

  /**
   * Class to filter by affiliation references.
   */
  public static class AffiliationRefFilter extends AbstractFilter implements Filter {
    /**
     * List of ids.
     */
    private final List<AffiliationRO> idList = new ArrayList<>();

    /**
     * Creates a new instance.
     */
    public AffiliationRefFilter() {}

    /**
     * @return the idList
     */
    public List<AffiliationRO> getIdList() {
      return this.idList;
    }
  }

  /**
   * Class to filter all top level affiliations.
   */
  public static class TopLevelAffiliationFilter extends AbstractFilter implements Filter {
    /**
     * Creates a new instance.
     */
    public TopLevelAffiliationFilter() {}
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
  public static class ObjectTypeFilter extends AbstractFilter implements Filter {

    public static final String OBJECT_TYPE_ITEM = "http://escidoc.de/core/01/resources/Item";
    public static final String OBJECT_TYPE_CONTAINER = "http://escidoc.de/core/01/resources/Container";
    public static final String OBJECT_TYPE_ORGANIZATIONAL_UNIT = "http://escidoc.de/core/01/resources/OrganizationalUnit";


    private String objectType;

    public ObjectTypeFilter(String objectType) {
      this.objectType = objectType;
    }

    public String getObjectType() {
      return this.objectType;
    }

    public void setObjectType(String objectType) {
      this.objectType = objectType;
    }
  }

  /**
   *
   * Class to specify an offset for handlers that return lists. If the OffsetFilter is specified,
   * only the results from the offset are returned.
   *
   * @author Markus Haarlaender (initial creation)
   * @author $Author$ (last modification)
   * @version $Revision$ $LastChangedDate$
   *
   */
  public static class OffsetFilter extends AbstractFilter implements Filter {
    private String offset;

    public OffsetFilter(String offset) {
      this.offset = offset;
    }

    public String getOffset() {
      return this.offset;
    }

    public void setOffset(String offset) {
      this.offset = offset;
    }

  }

  /**
   *
   * Class to specify a list limit for handlers that return lists. Only the number of list entries
   * is returned that is specified with the limit value.
   *
   * @author Markus Haarlaender (initial creation)
   * @author $Author$ (last modification)
   * @version $Revision$ $LastChangedDate$
   *
   */
  public static class LimitFilter extends AbstractFilter implements Filter {
    private String limit;

    public LimitFilter(String limit) {
      this.limit = limit;
    }

    public String getLimit() {
      return this.limit;
    }

    public void setLimit(String limit) {
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
  public static class OrderFilter extends AbstractFilter implements Filter {
    public static final String ORDER_ASCENDING = "sort.ascending";
    public static final String ORDER_DESCENDING = "sort.descending";
    public static final String SORTBY = "sortby";
    private String property;
    private String sortOrder;

    public OrderFilter(String property, String sortOrder) {
      this.property = property;
      this.sortOrder = sortOrder;
    }

    public String getProperty() {
      return this.property;
    }

    public void setProperty(String property) {
      this.property = property;
    }

    public String getSortOrder() {
      return this.sortOrder;
    }

    public void setSortOrder(String sortOrder) {
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
  public static class ContextFilter extends AbstractFilter implements Filter {

    private String contextId;

    public ContextFilter(String contextId) {
      this.contextId = contextId;
    }

    public String getContextId() {
      return this.contextId;
    }

    public void setContextId(String contextId) {
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
  public static class LocalTagFilter extends AbstractFilter implements Filter {

    private String localTagId;

    public LocalTagFilter(String localTagId) {
      this.localTagId = localTagId;
    }

    /**
     * @return the localTagId
     */
    public String getLocalTagId() {
      return this.localTagId;
    }

    /**
     * @param localTagId the localTagId to set
     */
    public void setLocalTagId(String localTagId) {
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
  public static class UserAccountStateFilter extends AbstractFilter implements Filter {


    private boolean active;

    public boolean getActive() {
      return this.active;
    }

    public void setActive(boolean active) {
      this.active = active;
    }

    public UserAccountStateFilter(boolean active) {
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

  public static class PersonsOrganizationsFilter extends AbstractFilter implements Filter {

    private String orgUnitId;

    public PersonsOrganizationsFilter(String orgUnitId) {
      this.orgUnitId = orgUnitId;
    }

    public String getOrgUnitId() {
      return this.orgUnitId;
    }

    public void setOrgUnitId(String contextId) {
      this.orgUnitId = contextId;
    }

  }

  public static class StandardFilter extends AbstractFilter implements Filter {

    private String filterName;
    private String value;
    private String operator;
    private String logicalOperator;

    public StandardFilter(String filterName, String value) {
      this.filterName = filterName;
      this.value = value;
    }

    /**
     *
     * @param filterName (eg. "http://escidoc.de/core/01/properties/user")
     * @param value (eg. "escidoc:12345")
     * @param operator (eg. "=" or "<>")
     */
    public StandardFilter(String filterName, String value, String operator) {
      this.filterName = filterName;
      this.value = value;
      this.operator = operator;
    }

    /**
     *
     * @param filterName (eg. "http://escidoc.de/core/01/properties/user")
     * @param value (eg. "escidoc:12345")
     * @param operator (eg. "=" or "<>")
     * @param logicalOperator for possible link with the next StandardFilter (eg. "AND" or "OR")
     */
    public StandardFilter(String filterName, String value, String operator, String logicalOperator) {
      this.filterName = filterName;
      this.value = value;
      this.operator = operator;
      this.logicalOperator = logicalOperator;
    }

    public String getFilterName() {
      return this.filterName;
    }

    public void setFilterName(String filterName) {
      this.filterName = filterName;
    }

    public String getValue() {
      return this.value;
    }

    public void setValue(String value) {
      this.value = value;
    }

    public String getOperator() {
      return this.operator;
    }

    public void setOperator(String operator) {
      this.operator = operator;
    }

    public String getLogicalOperator() {
      return this.logicalOperator;
    }

    public void setLogicalOperator(String logicalOperator) {
      this.logicalOperator = logicalOperator;
    }

  }

}
