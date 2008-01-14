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

package de.mpg.escidoc.services.common.itemsorting;

import java.util.Collections;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.RemoteBinding;
import de.mpg.escidoc.services.common.ItemSorting;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemVersionVO;
import de.mpg.escidoc.services.common.valueobjects.comparator.PubItemVOComparator;
import de.mpg.escidoc.services.common.valueobjects.comparator.PubItemVersionVOComparator;

/**
* This class provides the ejb implementation of the {@link ItemSorting} interface.
* 
* @author Galina Stancheva
* @author $Author: gstancheva $ (last modification)
* @version $Revision:$ $LastChangedDate:$
* Revised by StG: 24.08.2007
*/
@Stateless
@Remote
@RemoteBinding(jndiBinding = ItemSorting.SERVICE_NAME)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ItemSortingBean implements ItemSorting
{
    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(ItemSortingBean.class);

    /**
     * {@inheritDoc}
     */
     public java.util.List<PubItemVO> sortItemList(java.util.List<PubItemVO> itemList, PubItemVOComparator.Criteria criterium, String sortOrderString)
     {
        if (logger.isDebugEnabled())
        {
            logger.debug("sortItemList with sort order: " + sortOrderString);
        }
                
        if (logger.isDebugEnabled())
        {
            logger.debug("sortItemList with sorting criteria: " + criterium.toString());
        }
    
        // instanciate the comparator with the sorting criteria
        PubItemVOComparator pubItemVOComparator = new PubItemVOComparator(criterium);
        
        // sort ascending or descending
        // TODO STG: Die Sort order MUSS ein enum werden.
        if (sortOrderString.equals("ASCENDING"))
        {
            Collections.sort(itemList, pubItemVOComparator);
        }
        else if (sortOrderString.equals(("DESCENDING")))
        {
            Collections.sort(itemList, Collections.reverseOrder(pubItemVOComparator));
        }
        return itemList;       
    }
     
     /**
      * {@inheritDoc}
      * 
      * @author Johannes Mueller
      */
      public java.util.List<PubItemVersionVO> sortItemVersionList(java.util.List<PubItemVersionVO> itemList, PubItemVersionVOComparator.Criteria criterium, PubItemVersionVOComparator.Order sortOrder)
      {
         if (logger.isDebugEnabled())
         {
             logger.debug("sortItemVersionList with sort order: " + sortOrder.toString());
         }
                 
         if (logger.isDebugEnabled())
         {
             logger.debug("sortItemVersionList with sorting criteria: " + criterium.toString());
         }
     
         // instanciate the comparator with the sorting criteria
         PubItemVersionVOComparator pubItemVersionVOComparator = new PubItemVersionVOComparator(criterium);
         
         // sort ascending or descending
         if (sortOrder.equals(PubItemVersionVOComparator.Order.ASCENDING))
         {
             Collections.sort(itemList, pubItemVersionVOComparator);
         }
         else if (sortOrder.equals((PubItemVersionVOComparator.Order.DESCENDING)))
         {
             Collections.sort(itemList, Collections.reverseOrder(pubItemVersionVOComparator));
         }
         return itemList;       
     }
     
}
