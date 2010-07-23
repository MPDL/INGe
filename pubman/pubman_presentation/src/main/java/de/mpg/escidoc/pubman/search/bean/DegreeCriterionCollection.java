package de.mpg.escidoc.pubman.search.bean;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.pubman.search.bean.criterion.DegreeCriterion;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;

/**
 * Bean to handle the DegreeCriterionCollection on a single jsp.
 * A DegreeCriterionCollection is represented by a List<DegreeCriterionVO>.
 * 
 * @author Friederike Kleinfercher
 */
public class DegreeCriterionCollection
{
    public static final String BEAN_NAME = "DegreeCriterionCollection";
    
    private List<DegreeCriterion> parentVO;
    private DegreeCriterionManager degreeCriterionManager;
    
    /**
     * CTOR to create a new ArrayList<DegreeCriterionVO> 
     * starting with one empty new DegreeCriterionVO
     */
    public DegreeCriterionCollection()
    {
        // ensure the parentVO is never null;
        List<DegreeCriterion> ctorList = new ArrayList<DegreeCriterion>();
        ctorList.add(new DegreeCriterion());
        setParentVO(ctorList);
    }

    /**
     * CTOR to refine or fill a predefined ArrayList<DegreeCriterionVO>
     * @param parentVO
     */
    public DegreeCriterionCollection(List<DegreeCriterion> parentVO)
    {
        setParentVO(parentVO);
    }

    public List<DegreeCriterion> getParentVO()
    {
        return parentVO;
    }

    public void setParentVO(List<DegreeCriterion> parentVO)
    {
        this.parentVO = parentVO;
        // ensure proper initialization of our DataModelManager
        degreeCriterionManager = new DegreeCriterionManager(parentVO);
    }
    
    public class DegreeCriterionManager extends DataModelManager<DegreeCriterionBean>
    {
        List<DegreeCriterion> parentVO;
        
        public DegreeCriterionManager(List<DegreeCriterion> parentVO)
        {
            setParentVO(parentVO);
        }
        
        public DegreeCriterionBean createNewObject()
        {
            DegreeCriterion newVO = new DegreeCriterion();
            newVO.setDegree(new ArrayList<MdsPublicationVO.DegreeType>());
            // create a new wrapper pojo
            DegreeCriterionBean degreeCriterionBean = new DegreeCriterionBean(newVO);
            // we do not have direct access to the original list
            // so we have to add the new VO on our own
            parentVO.add(newVO);
            return degreeCriterionBean;
        }
        
        @Override
        protected void removeObjectAtIndex(int i)
        {
            // due to wrapped data handling
            super.removeObjectAtIndex(i);
            parentVO.remove(i);
        }

        public List<DegreeCriterionBean> getDataListFromVO()
        {
            if (parentVO == null) return null;
            // we have to wrap all VO's in a nice DegreeCriterionBean
            List<DegreeCriterionBean> beanList = new ArrayList<DegreeCriterionBean>();
            for (DegreeCriterion degreeCriterionVO : parentVO)
            {
                beanList.add(new DegreeCriterionBean(degreeCriterionVO));
            }
            return beanList;
        }

        public void setParentVO(List<DegreeCriterion> parentVO)
        {
            this.parentVO = parentVO;
            // we have to wrap all VO's into a nice DegreeCriterionBean
            List<DegreeCriterionBean> beanList = new ArrayList<DegreeCriterionBean>();
            for (DegreeCriterion degreeCriterionVO : parentVO)
            {
                beanList.add(new DegreeCriterionBean(degreeCriterionVO));
            }
            setObjectList(beanList);
        }
        
        public int getSize()
        {
            return getObjectDM().getRowCount();
        }
    }


    public DegreeCriterionManager getDegreeCriterionManager()
    {
        return degreeCriterionManager;
    }

    public void setDegreeCriterionManager(DegreeCriterionManager degreeCriterionManager)
    {
        this.degreeCriterionManager = degreeCriterionManager;
    }

    public void clearAllForms()
    {        
        for (DegreeCriterionBean gcb : degreeCriterionManager.getObjectList())
        {
            gcb.clearCriterion();
        }
    }

    public List<DegreeCriterion> getFilledCriterion()
    {
        List<DegreeCriterion> returnList = new ArrayList<DegreeCriterion>();
        for (DegreeCriterion vo : parentVO)
        {
            if (vo.getDegree().size() > 0 || (vo.getSearchString() != null && vo.getSearchString().length() > 0))
            {
                returnList.add(vo);
            }
        }
        return returnList;
    }
    
}
