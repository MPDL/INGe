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
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/
package de.mpg.escidoc.pubman.search.bean.criterion;

import java.util.ArrayList;

import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.LogicalOperator;

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
    /** Shall files with an embargo date be searched */
    private boolean searchForEmbargoFiles;


    public FileCriterion()
    {
        this.excludeCategory = false;
        this.searchForEmbargoFiles = false;
    }

    @Override
    public ArrayList<MetadataSearchCriterion> createSearchCriterion() throws TechnicalException
    {
       ArrayList<MetadataSearchCriterion> criterions = new ArrayList<MetadataSearchCriterion>(); 
       
       String visibility = null;
       String storage = null;
       

       // component visibility
       if(this.componentVisibility != null)
       {
           if(this.componentVisibility.equals(InternationalizationHelper.SelectComponentVisibility.SELECT_COMPONENT_PRIVATE.toString()))
           {
               MetadataSearchCriterion criterion = 
               new MetadataSearchCriterion(MetadataSearchCriterion.CriterionType.COMPONENT_VISIBILITY,
                       "private", MetadataSearchCriterion.LogicalOperator.AND);
               criterions.add(criterion);
               visibility = "private";    

           }
           else if(this.componentVisibility.equals(InternationalizationHelper.SelectComponentVisibility.SELECT_COMPONENT_PUBLIC.toString()))
           {
               MetadataSearchCriterion criterion = 
                   new MetadataSearchCriterion(MetadataSearchCriterion.CriterionType.COMPONENT_VISIBILITY,
                           "public", MetadataSearchCriterion.LogicalOperator.AND);
                   criterions.add(criterion);
                   visibility = "public";    
           }
           else if(this.componentVisibility.equals(InternationalizationHelper.SelectComponentVisibility.SELECT_COMPONENT_RESTRICTED.toString()))
           {
               MetadataSearchCriterion criterion = 
                   new MetadataSearchCriterion(MetadataSearchCriterion.CriterionType.COMPONENT_VISIBILITY,
                           "audience", MetadataSearchCriterion.LogicalOperator.AND);
                   criterions.add(criterion);
                   visibility = "audience";    
           }
       }
            
       
       
       // component availability
      if(this.componentAvailability != null)
      {
          if(this.componentAvailability.equals(InternationalizationHelper.SelectComponentAvailability.SELECT_HAS_NO_COMPONENTS.toString()))
          {
              MetadataSearchCriterion criterion = 
            	  new MetadataSearchCriterion(MetadataSearchCriterion.CriterionType.COMPONENT_ACCESSIBILITY, MetadataSearchCriterion.LogicalOperator.NOT);
              criterions.add(criterion);
          }
          else if(this.componentAvailability.equals(InternationalizationHelper.SelectComponentAvailability.SELECT_HAS_COMPONENTS.toString()))
          {
              MetadataSearchCriterion criterion = 
                  new MetadataSearchCriterion(MetadataSearchCriterion.CriterionType.COMPONENT_ACCESSIBILITY, MetadataSearchCriterion.LogicalOperator.AND);
              criterions.add(criterion);
          }
          else if(this.componentAvailability.equals(InternationalizationHelper.SelectComponentAvailability.SELECT_HAS_FILES.toString()))
          {
              MetadataSearchCriterion criterion = 
                  new MetadataSearchCriterion(MetadataSearchCriterion.CriterionType.COMPONENT_STORAGE, "internal-managed", MetadataSearchCriterion.LogicalOperator.AND);
              criterions.add(criterion);
              storage = "internal-managed";    
                  
          }
          else if(this.componentAvailability.equals(InternationalizationHelper.SelectComponentAvailability.SELECT_HAS_LOCATORS.toString()))
          {
              MetadataSearchCriterion criterion = 
                  new MetadataSearchCriterion(MetadataSearchCriterion.CriterionType.COMPONENT_STORAGE, "external-url", MetadataSearchCriterion.LogicalOperator.AND);
              criterions.add(criterion);
              storage = "external-url ";    
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
       
       // embargo date
       if( getSearchForEmbargoFiles() == true )
       {
           MetadataSearchCriterion criterion = 
               new MetadataSearchCriterion(MetadataSearchCriterion.CriterionType.EMBARGO_DATE,
                        MetadataSearchCriterion.LogicalOperator.AND);
           criterions.add(criterion);
       }

       //add component compound properties search term and index
       
       if (visibility != null || storage != null || contentCategory != null)
       {
    	   // inclusive cases
    	   if (this.excludeCategory == false)
    	   {
    		   if (visibility != null && storage != null && contentCategory == null)
    		   {
    			   criterions.add( new MetadataSearchCriterion(
    					   MetadataSearchCriterion.CriterionType.COMPONENT_COMPOUND_PROPERTIES, 
    					   "\"" + visibility + " " + storage + "\"", 
    					   LogicalOperator.AND ));
    		   }
    		   else if (storage != null && contentCategory != null && visibility == null)
    		   {
    			   criterions.add( new MetadataSearchCriterion(
    					   MetadataSearchCriterion.CriterionType.COMPONENT_COMPOUND_PROPERTIES, 
    					   "\"" + storage + " " + contentCategory + "\"", 
    					   LogicalOperator.AND ));
    		   }
    		   else if (visibility != null && contentCategory != null && storage == null)
    		   {
    			   MetadataSearchCriterion lastMetadataSearchCriterion = criterions.get(criterions.size()-1);
    			   lastMetadataSearchCriterion.addSubCriteria( new MetadataSearchCriterion(
    					   MetadataSearchCriterion.CriterionType.COMPONENT_COMPOUND_PROPERTIES, 
    					   "\"" + visibility + " internal-managed " + contentCategory + "\"",
    					   LogicalOperator.AND ));
    			   lastMetadataSearchCriterion.addSubCriteria( new MetadataSearchCriterion(
    					   MetadataSearchCriterion.CriterionType.COMPONENT_COMPOUND_PROPERTIES, 
    					   "\"" + visibility + " external-url " + contentCategory + "\"",
    					   LogicalOperator.OR ));
    		   }
    		   else if (visibility != null && storage != null && contentCategory != null)
    		   {
    			   criterions.add( new MetadataSearchCriterion(
    					   MetadataSearchCriterion.CriterionType.COMPONENT_COMPOUND_PROPERTIES, 
    					   "\"" + visibility + " " + storage + " " + contentCategory + "\"", 
    					   LogicalOperator.AND ));
    		   }
    		   
    	   }
    	   // exclusive cases
    	   else
    	   {
    		   if (visibility != null && storage != null)
    		   {
    			   criterions.add( new MetadataSearchCriterion(
    					   MetadataSearchCriterion.CriterionType.COMPONENT_COMPOUND_PROPERTIES, 
    					   "\"" + visibility + " " + storage + "\"",
    					   LogicalOperator.AND ));
    			   criterions.add( new MetadataSearchCriterion(
    					   MetadataSearchCriterion.CriterionType.COMPONENT_COMPOUND_PROPERTIES, 
    					   "\"" + visibility + " " + storage + " " + contentCategory + "\"",
    					   LogicalOperator.NOT ));
    		   }    		   
    		   else if (visibility != null && storage == null)
    		   {
        		   MetadataSearchCriterion lastMetadataSearchCriterion = criterions.get(criterions.size()-1);
    			   lastMetadataSearchCriterion.addSubCriteria(new MetadataSearchCriterion(
    					   MetadataSearchCriterion.CriterionType.COMPONENT_COMPOUND_PROPERTIES, 
    					   "\"" + visibility + " internal-managed " + "\"",
    					   LogicalOperator.AND ));
    			   lastMetadataSearchCriterion.addSubCriteria(new MetadataSearchCriterion(
    					   MetadataSearchCriterion.CriterionType.COMPONENT_COMPOUND_PROPERTIES, 
    					   "\"" + visibility + " external-url " + "\"",
    					   LogicalOperator.OR ));
    			   criterions.add( new MetadataSearchCriterion(
    					   MetadataSearchCriterion.CriterionType.COMPONENT_COMPOUND_PROPERTIES, 
    					   "\"" + visibility + " internal-managed " + contentCategory + "\"",
    					   LogicalOperator.NOT ));
    			   criterions.add( new MetadataSearchCriterion(
    					   MetadataSearchCriterion.CriterionType.COMPONENT_COMPOUND_PROPERTIES, 
    					   "\"" + visibility + " external-url " + contentCategory + "\"",
    					   LogicalOperator.NOT ));
    		   }    		   
    		   else if (visibility == null && storage != null)
    		   {
    			   criterions.add( new MetadataSearchCriterion(
    					   MetadataSearchCriterion.CriterionType.COMPONENT_COMPOUND_PROPERTIES, 
    					   "\"" + storage + " " + contentCategory + "\"",
    					   LogicalOperator.NOT ));
    		   }    		   
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

    /**
     * @return the searchForEmbargoFiles
     */
    public boolean getSearchForEmbargoFiles()
    {
        return searchForEmbargoFiles;
    }

    /**
     * @param searchForEmbargoFiles the searchForEmbargoFiles to set
     */
    public void setSearchForEmbargoFiles(boolean searchForEmbargoFiles)
    {
        this.searchForEmbargoFiles = searchForEmbargoFiles;
    }

}
