package de.mpg.escidoc.pubman.search.bean;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.services.pubman.valueobjects.AnyFieldCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO;

/**
 * Bean to handle the AnyFieldCriterionCollection on a single jsp.
 * A AnyFieldCriterionCollection is represented by a List<CriterionVO> which 
 * can be AnyFieldCriterionVO, TitleCriterionVO or TopicCriterionVO.
 * 
 * @author Mario Wagner
 */
public class AnyFieldCriterionCollection
{
	public static final String BEAN_NAME = "AnyFieldCriterionCollection";
	
	//private List<CriterionVO> parentVO;
	private AnyFieldCriterionManager anyFieldCriterionManager;
	
	/**
	 * CTOR to create a new ArrayList<CriterionVO> 
	 * starting with one empty new AnyFieldCriterionVO
	 */
	public AnyFieldCriterionCollection()
	{
		// ensure the parentVO is never null;
		List<CriterionVO> ctorList = new ArrayList<CriterionVO>();
		ctorList.add(new AnyFieldCriterionVO());
		anyFieldCriterionManager = new AnyFieldCriterionManager();
		//setParentVO(ctorList);
	}

//	/**
//	 * CTOR to refine or fill a predefined ArrayList<CriterionVO>
//	 * @param parentVO
//	 */
//	public AnyFieldCriterionCollection(List<CriterionVO> parentVO)
//	{
//		setParentVO(parentVO);
//	}

//	public List<CriterionVO> getParentVO()
//	{
//		return parentVO;
//	}
//
//	public void setParentVO(List<CriterionVO> parentVO)
//	{
//		this.parentVO = parentVO;
//		// ensure proper initialization of our DataModelManager
//		anyFieldCriterionManager = new AnyFieldCriterionManager(parentVO);
//	}
//
	public AnyFieldCriterionManager getAnyFieldCriterionManager()
	{
		return anyFieldCriterionManager;
	}

	public void setAnyFieldCriterionManager(AnyFieldCriterionManager anyFieldCriterionManager)
	{
		this.anyFieldCriterionManager = anyFieldCriterionManager;
	}

    public void clearAllForms()
    {        
    	for (AnyFieldCriterionBean gcb : anyFieldCriterionManager.getObjectList())
    	{
    		gcb.clearCriterion();
    	}
    }

    public List<CriterionVO> getFilledCriterionVO()
	{
    	List<CriterionVO> returnList = new ArrayList<CriterionVO>();
    	for (AnyFieldCriterionBean bean : anyFieldCriterionManager.getObjectList())
    	{
    	    CriterionVO vo = bean.getCriterionVO();
    		if ((vo != null && vo.getSearchString() != null && vo.getSearchString().length() > 0))
    		{
    			returnList.add(vo);
    		}
    	}
		return returnList;
	}
    
    /**
     * Specialized DataModelManager to deal with objects of type AnyFieldCriterionBean
     * @author Mario Wagner
     */
    public class AnyFieldCriterionManager extends DataModelManager<AnyFieldCriterionBean>
    {
        //List<CriterionVO> parentVO;
        
//        public AnyFieldCriterionManager(List<CriterionVO> parentVO)
//        {
//            setParentVO(parentVO);
//        }
        
        public AnyFieldCriterionManager()
        {
            if (getSize() == 0)
            {
                List<AnyFieldCriterionBean> beanList = new ArrayList<AnyFieldCriterionBean>();
                beanList.add(createNewObject());
                setObjectList(beanList);
            }
        }
        
        public AnyFieldCriterionBean createNewObject()
        {
            AnyFieldCriterionVO newVO = new AnyFieldCriterionVO();
            // create a new wrapper pojo
            AnyFieldCriterionBean anyFieldCriterionBean = new AnyFieldCriterionBean(newVO);
            // we do not have direct access to the original list
            // so we have to add the new VO on our own
            //parentVO.add(newVO);
            return anyFieldCriterionBean;
        }
        
        @Override
        protected void removeObjectAtIndex(int i)
        {
            // due to wrapped data handling
            super.removeObjectAtIndex(i);
            //parentVO.remove(i);
        }

        public List<AnyFieldCriterionBean> getDataListFromVO()
        {
            return this.objectList;
//            if (parentVO == null) return null;
//            // we have to wrap all VO's in a nice AnyFieldCriterionBean
//            List<AnyFieldCriterionBean> beanList = new ArrayList<AnyFieldCriterionBean>();
//            for (CriterionVO criterionVO : parentVO)
//            {
//                beanList.add(new AnyFieldCriterionBean(criterionVO));
//            }
//            return beanList;
        }

//        public void setParentVO(List<CriterionVO> parentVO)
//        {
//            this.parentVO = parentVO;
//            // we have to wrap all VO's into a nice AnyFieldCriterionBean
//            List<AnyFieldCriterionBean> beanList = new ArrayList<AnyFieldCriterionBean>();
//            for (CriterionVO criterionVO : parentVO)
//            {
//                beanList.add(new AnyFieldCriterionBean(criterionVO));
//            }
//            setObjectList(beanList);
//        }
        
        public int getSize()
        {
            return getObjectDM().getRowCount();
        }
    }

}
