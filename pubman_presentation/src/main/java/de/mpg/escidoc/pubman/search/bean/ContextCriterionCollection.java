package de.mpg.escidoc.pubman.search.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.InitialContext;

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
        	contextList.add(new SelectItem(c.getReference().getObjectId(),c.getName()));
        }
        
        Collections.sort(contextList, new SelectItemComparator());
        contextList.add(0,new SelectItem("", "--"));
		return contextList;
	}
    
	public void clearAllForms() 
	{
		contextCriterionBean.clearCriterion();
    }

	public Criterion getFilledCriterion()
    {
        if(contextCriterionBean.getContextCriterionVO()!=null && contextCriterionBean.getContextCriterionVO().getSearchString()!=null && contextCriterionBean.getContextCriterionVO().getSearchString().length()>0)
        	return contextCriterionBean.getCriterionVO();
        else
        	return null;
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


}
