/*
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
package de.mpg.escidoc.pubman.search.bean.criterion;

import java.util.ArrayList;

import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.pubman.util.PubFileVOPresentation;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion;

/**
 * @author endres
 *
 */
public class FileCriterion extends Criterion
{
    /** The content category has to be excluded */
    private boolean excludeCategory;
    /** Choose all files which have set this content category */
    private String contentCategory;
    /** Choose all files which have restricted access */
    private String componentVisibility;
    /** Choose all files which have components attached */
    private String componentAvailability;
    


    public FileCriterion()
    {
        this.excludeCategory = false;
    }

    @Override
    public ArrayList<MetadataSearchCriterion> createSearchCriterion() throws TechnicalException
    {
       ArrayList<MetadataSearchCriterion> criterions = new ArrayList<MetadataSearchCriterion>(); 
       
      // component availability
      if(this.componentAvailability != null)
      {
          if(this.componentAvailability.equals(InternationalizationHelper.SelectComponentAvailability.SELECT_HAS_NO_COMPONENTS.toString()))
          {
              MetadataSearchCriterion criterionPre = 
                  new MetadataSearchCriterion(MetadataSearchCriterion.CriterionType.OBJECT_TYPE, "item");
              criterions.add(criterionPre);
              MetadataSearchCriterion criterion = 
              new MetadataSearchCriterion(MetadataSearchCriterion.CriterionType.COMPONENT_ACCESSABILITY, MetadataSearchCriterion.LogicalOperator.NOT);
              criterions.add(criterion);
          }
          else if(this.componentAvailability.equals(InternationalizationHelper.SelectComponentAvailability.SELECT_HAS_COMPONENTS.toString()))
          {
              MetadataSearchCriterion criterionPre = 
                  new MetadataSearchCriterion(MetadataSearchCriterion.CriterionType.OBJECT_TYPE, "item");
              criterions.add(criterionPre);
              MetadataSearchCriterion criterion = 
                  new MetadataSearchCriterion(MetadataSearchCriterion.CriterionType.COMPONENT_ACCESSABILITY, MetadataSearchCriterion.LogicalOperator.AND);
                  criterions.add(criterion);
          }
      }
      
      // component visibility
      if(this.componentVisibility != null)
      {
          if(this.componentVisibility.equals(InternationalizationHelper.SelectComponentVisibility.SELECT_COMPONENT_PRIVATE.toString()))
          {
              MetadataSearchCriterion criterion = 
              new MetadataSearchCriterion(MetadataSearchCriterion.CriterionType.COMPONENT_VISIBILITY,
                      "private", MetadataSearchCriterion.LogicalOperator.AND);
              criterions.add(criterion);
          }
          else if(this.componentVisibility.equals(InternationalizationHelper.SelectComponentVisibility.SELECT_COMPONENT_PUBLIC.toString()))
          {
              MetadataSearchCriterion criterion = 
                  new MetadataSearchCriterion(MetadataSearchCriterion.CriterionType.COMPONENT_VISIBILITY,
                          "public", MetadataSearchCriterion.LogicalOperator.AND);
                  criterions.add(criterion);
          }
      }
           
       // content category
       if(this.contentCategory != null) 
       {
           if(this.excludeCategory == true)
           {
               MetadataSearchCriterion criterion = 
                   new MetadataSearchCriterion(MetadataSearchCriterion.CriterionType.COMPONENT_CONTENT_CATEGORY,
                           this.contentCategory, MetadataSearchCriterion.LogicalOperator.NOT);
               criterions.add(criterion);
           }
           else
           {
               MetadataSearchCriterion criterion = 
                   new MetadataSearchCriterion(MetadataSearchCriterion.CriterionType.COMPONENT_CONTENT_CATEGORY,
                           this.contentCategory, MetadataSearchCriterion.LogicalOperator.AND);
               criterions.add(criterion);
           }
       }
       
       return criterions;
    }

    /**
     * @return the excludeCategory
     */
    public boolean getExcludeCategory()
    {
        return excludeCategory;
    }

    /**
     * @param excludeCategory the excludeCategory to set
     */
    public void setExcludeCategory(boolean excludeCategory)
    {
        this.excludeCategory = excludeCategory;
    }

    /**
     * @return the contentCategory
     */
    public String getContentCategory()
    {
        return contentCategory;
    }

    /**
     * @param contentCategory the contentCategory to set
     */
    public void setContentCategory(String contentCategory)
    {
        this.contentCategory = contentCategory;
    }

    /**
     * @return the componentAccessibility
     */              
    public String getComponentVisibility()
    {
        return componentVisibility;
    }

    /**
     * @param componentAccessibility the componentAccessibility to set
     */
    public void setComponentVisibility(String componentVisibility)
    {
        this.componentVisibility = componentVisibility;
    }

    /**
     * @return the componentAvailability
     */
    public String getComponentAvailability()
    {
        return componentAvailability;
    }

    /**
     * @param componentAvailability the componentAvailability to set
     */
    public void setComponentAvailability(String componentAvailability)
    {
        this.componentAvailability = componentAvailability;
    }

}
