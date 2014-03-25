package de.mpg.escidoc.pubman.search.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.search.bean.criterion.OrganizationCriterion;
import de.mpg.escidoc.pubman.util.AffiliationVOPresentation;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;

/**
 * Bean to handle the OrganizationCriterionCollection on a single jsp.
 * A OrganizationCriterionCollection is represented by a List<OrganizationCriterionVO>.
 * 
 * @author Mario Wagner
 */
public class OrganizationCriterionCollection
{
    public static final String BEAN_NAME = "OrganizationCriterionCollection";
    
    private static final Logger logger = Logger.getLogger(OrganizationCriterionCollection.class);
    
    private List<OrganizationCriterion> parentVO;
    private OrganizationCriterionManager organizationCriterionManager;
    
    /**
     * CTOR to create a new ArrayList<OrganizationCriterionVO> 
     * starting with one empty new OrganizationCriterionVO
     */
    public OrganizationCriterionCollection()
    {
        // ensure the parentVO is never null;
        List<OrganizationCriterion> ctorList = new ArrayList<OrganizationCriterion>();
        ctorList.add(new OrganizationCriterion());
        setParentVO(ctorList);
    }

    /**
     * CTOR to refine or fill a predefined ArrayList<OrganizationCriterionVO>
     * @param parentVO
     */
    public OrganizationCriterionCollection(List<OrganizationCriterion> parentVO)
    {
        setParentVO(parentVO);
    }

    public List<OrganizationCriterion> getParentVO()
    {
        return parentVO;
    }

    public void setParentVO(List<OrganizationCriterion> parentVO)
    {
        this.parentVO = parentVO;
        // ensure proper initialization of our DataModelManager
        organizationCriterionManager = new OrganizationCriterionManager(parentVO);
    }
    
    private List<OrganizationCriterion> resolveIncludes(List<OrganizationCriterion> inVO)
    {
        List<OrganizationCriterion> resolved = new ArrayList<OrganizationCriterion>();
        
        for (OrganizationCriterion criterion : inVO)
        {
            resolved.add(criterion);
            
            AffiliationVO affiliation;
            try
            {
                affiliation = ItemControllerSessionBean.retrieveAffiliation(criterion.getAffiliation().getReference().getObjectId());

                AffiliationVOPresentation affiliationPres = new AffiliationVOPresentation(affiliation);
                
                //AffiliationVOPresentation affiliation = criterion.getAffiliation();           
                logger.debug("Adding " + affiliation.toString());
                
                if (criterion.getIncludePredecessorsAndSuccessors())
                { 
                    List<AffiliationVO> sucessorsVO = affiliationPres.getSuccessors();
                    
                    for (AffiliationVO affiliationVO : sucessorsVO)
                    {
                        OrganizationCriterion organizationCriterion = new OrganizationCriterion();
                        organizationCriterion.setAffiliation(new AffiliationVOPresentation(affiliationVO));
                        resolved.add(organizationCriterion);
                        logger.debug("Adding sucessor " + organizationCriterion.getAffiliation().toString());
                    }
                    
                    List<AffiliationVO> predecessorsVO = affiliationPres.getPredecessors();
                    
                    for (AffiliationVO affiliationVO : predecessorsVO)
                    {
                        OrganizationCriterion organizationCriterion = new OrganizationCriterion();
                        organizationCriterion.setAffiliation(new AffiliationVOPresentation(affiliationVO));
                        resolved.add(organizationCriterion);
                        logger.debug("Adding predecessor " + organizationCriterion.getAffiliation().toString());
                    }
                }
            }
            catch (Exception e)
            {
                logger.error("Error while retrieving affiliation from id", e);
            }
        }
        return resolved;
    }

    /**
     * Specialized DataModelManager to deal with objects of type OrganizationCriterionBean
     * @author Mario Wagner
     */
    public class OrganizationCriterionManager extends DataModelManager<OrganizationCriterionBean>
    {
        List<OrganizationCriterion> parentVO;
        
        public OrganizationCriterionManager(List<OrganizationCriterion> parentVO)
        {
            setParentVO(parentVO);
        }
        
        public OrganizationCriterionBean createNewObject()
        {
            OrganizationCriterion newVO = new OrganizationCriterion();
            // create a new wrapper pojo
            OrganizationCriterionBean organizationCriterionBean = new OrganizationCriterionBean(newVO);
            // we do not have direct access to the original list
            // so we have to add the new VO on our own
            parentVO.add(newVO);
            return organizationCriterionBean;
        }
        
        @Override
        protected void removeObjectAtIndex(int i)
        {
            // due to wrapped data handling
            super.removeObjectAtIndex(i);
            parentVO.remove(i);
        }

        public List<OrganizationCriterionBean> getDataListFromVO()
        {
            if (parentVO == null) return null;
            // we have to wrap all VO's in a nice OrganizationCriterionBean
            List<OrganizationCriterionBean> beanList = new ArrayList<OrganizationCriterionBean>();
            for (OrganizationCriterion organizationCriterionVO : parentVO)
            {
                beanList.add(new OrganizationCriterionBean(organizationCriterionVO));
            }
            return beanList;
        }

        public void setParentVO(List<OrganizationCriterion> parentVO)
        {
            this.parentVO = parentVO;
            // we have to wrap all VO's into a nice OrganizationCriterionBean
            List<OrganizationCriterionBean> beanList = new ArrayList<OrganizationCriterionBean>();
            for (OrganizationCriterion organizationCriterionVO : parentVO)
            {
                beanList.add(new OrganizationCriterionBean(organizationCriterionVO));
            }
            setObjectList(beanList);
        }
        
        public int getSize()
        {
            return getObjectDM().getRowCount();
        }
    }


    public OrganizationCriterionManager getOrganizationCriterionManager()
    {
        return organizationCriterionManager;
    }

    public void setOrganizationCriterionManager(OrganizationCriterionManager organizationCriterionManager)
    {
        this.organizationCriterionManager = organizationCriterionManager;
    }

    public void clearAllForms()
    {        
        for (OrganizationCriterionBean gcb : organizationCriterionManager.getObjectList())
        {
            gcb.clearCriterion();
        }
    }

    public List<OrganizationCriterion> getFilledCriterion()
    {
        List<OrganizationCriterion> returnList = new ArrayList<OrganizationCriterion>();
        for (OrganizationCriterion vo : parentVO)
        {
            if (((vo.getSearchString() != null && vo.getSearchString().length() > 0) || (vo.getAffiliation() != null && vo.getAffiliation().getReference().getObjectId() != null && !"".equals(vo.getAffiliation().getReference().getObjectId()))))
            {
                returnList.add(vo);
            }
        }
        return resolveIncludes(returnList);
    }

}
