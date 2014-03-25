package de.mpg.escidoc.pubman.search.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.InitialContext;

import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.pubman.search.bean.criterion.ContextCriterion;
import de.mpg.escidoc.pubman.search.bean.criterion.Criterion;
import de.mpg.escidoc.pubman.util.SelectItemComparator;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.pubman.PubItemDepositing;

public class ContextCriterionCollection
{
    public static final String BEAN_NAME = "ContextCriterionCollection";
    private PubItemDepositing pubItemDepositing = null;    
    private List<ContextVO> contexts;
    private ContextCriterionBean contextCriterionBean;
    private List<SelectItem> contextList;
    
    private ContextCriterionManager contextCriterionManager;
    
    /**
     * CTOR to create a new ArrayList<CriterionVO> 
     * starting with one empty new ContextCriterionVO
     */
    public ContextCriterionCollection()
    {
    	try{
            InitialContext initialContext = new InitialContext(); 
            // initialize used Beans
            this.pubItemDepositing = (PubItemDepositing) initialContext.lookup(PubItemDepositing.SERVICE_NAME);
    		getContextList();
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
		contextCriterionBean = new ContextCriterionBean(contexts);
		contextCriterionManager = new ContextCriterionManager();
		
		System.out.println();
    }
    
    public void getContexts() throws Exception
    {
    	contexts = new ArrayList<ContextVO>();
    	contexts = this.pubItemDepositing.getPubCollectionListForDepositing();
    }
    
	public List<SelectItem> getContextList() throws Exception
	{
		getContexts();
		contextList = new ArrayList<SelectItem>();
		
        for (ContextVO c : contexts)
        {
        	contextList.add(new SelectItem(c.getReference().getObjectId(), c.getName()));
        }
        
        Collections.sort(contextList, new SelectItemComparator());
        contextList.add(0, new SelectItem("", "--"));
		return contextList;
	}
    
	public void clearAllForms() 
	{
	    for (ContextCriterionBean bean : contextCriterionManager.getObjectList())
        {
	        bean.clearCriterion();
        }
    }

    public List<Criterion> getFilledCriterion()
    {
        List<Criterion> returnList = new ArrayList<Criterion>();
        for (ContextCriterionBean bean : contextCriterionManager.getObjectList())
        {
            Criterion vo = bean.getCriterionVO();
            if ((vo != null && vo.getSearchString() != null && vo.getSearchString().length() > 0))
            {
                returnList.add(vo);
            }
        }
        return returnList;
    }
    
	public ContextCriterionBean getContextCriterionBean() 
	{
		return contextCriterionBean;
	}

	public void setContextCriterionBean(ContextCriterionBean contextCriterionBean) 
	{
		this.contextCriterionBean = contextCriterionBean;
	}

	public void setContexts(List<ContextVO> contexts) 
	{
		this.contexts = contexts;
	}

	public void setContextList(List<SelectItem> contextList) 
	{
		this.contextList = contextList;
	}
    
    public ContextCriterionManager getContextCriterionManager()
    {
        return contextCriterionManager;
    }

    public void setContextCriterionManager(ContextCriterionManager contextCriterionManager)
    {
        this.contextCriterionManager = contextCriterionManager;
    }


    /**
     * Specialized DataModelManager to deal with objects of type ContextCriterionBean
     */
    public class ContextCriterionManager extends DataModelManager<ContextCriterionBean>
    {
        //List<CriterionVO> parentVO;
        
//        public AnyFieldCriterionManager(List<CriterionVO> parentVO)
//        {
//            setParentVO(parentVO);
//        }
        
        public ContextCriterionManager()
        {
            if (getSize() == 0)
            {
                List<ContextCriterionBean> beanList = new ArrayList<ContextCriterionBean>();
                beanList.add(createNewObject());
                setObjectList(beanList);
            }
        }
        
        public ContextCriterionBean createNewObject()
        {
            ContextCriterion newVO = new ContextCriterion();
            // create a new wrapper pojo
            ContextCriterionBean contextCriterionBean = new ContextCriterionBean(newVO);
            // we do not have direct access to the original list
            // so we have to add the new VO on our own
            //parentVO.add(newVO);
            return contextCriterionBean;
        }
        
        @Override
        protected void removeObjectAtIndex(int i)
        {
            // due to wrapped data handling
            super.removeObjectAtIndex(i);
            //parentVO.remove(i);
        }

        public List<ContextCriterionBean> getDataListFromVO()
        {
            return this.objectList;
        }
        
        public int getSize()
        {
            return getObjectDM().getRowCount();
        }
    }
}
