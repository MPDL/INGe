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

package de.mpg.escidoc.services.common;



import de.mpg.escidoc.services.common.valueobjects.VersionHistoryEntryVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.comparator.VersionHistoryEntryVOComparator;
import de.mpg.escidoc.services.common.valueobjects.comparator.PubItemVOComparator;

/**
 * Interface for sorting of items that applies the PubItemVOComparator.
 * @created 19-Jul-2007 18:20:35
 * Revised by StG: 24.08.2007
 * @author Galina Stancheva
 * @version 1.0
 * @updated 19-Okt-2007 17:52:23
 */
public interface ItemSorting
{
    /**
     * The name of the service.
     */
    public static String SERVICE_NAME = "ejb/de/mpg/escidoc/services/common/ItemSorting";
   
    /**
     * Method  for sorting  a list of pubItems according the given criterium and order.
     * It sorts the items by comparing them relatively their structure.
     * 
     * @param itemList The list of items to sort.
     * @param criterium The sort criterium.
     * @param order Descending or ascending order.
     * @return The sorted list.
     */
    public java.util.List<PubItemVO> sortItemList(java.util.List<PubItemVO> itemList, PubItemVOComparator.Criteria criterium, String order);

    /**
     * Method for sorting a list of pubItemVersions according the given criterium and order.
     * 
     * @author Johannes Mueller
     * @param itemVersionList The list of item versions to sort.
     * @param criterium The sort criterium.
     * @param order Descending or ascending order.
     * @return The sorted list.
     */
    public java.util.List<VersionHistoryEntryVO> sortItemVersionList(java.util.List<VersionHistoryEntryVO> itemVersionList, VersionHistoryEntryVOComparator.Criteria criterium, VersionHistoryEntryVOComparator.Order order);
}