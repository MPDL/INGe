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
* Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.search.bean;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletRequest;

import de.mpg.escidoc.pubman.search.bean.criterion.ContextCriterion;
import de.mpg.escidoc.pubman.search.bean.criterion.Criterion;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;

/**
 * context criterion vo for the advanced search.
 */
public class ContextCriterionBean extends CriterionBean {

	private List<ContextVO> contexts;

    private ContextCriterion contextCriterionVO;
    private String context;
    
    public String getContext() {
       	HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
    	context = req.getParameter("collection");
    	if(context!=null && context.length()>0)
    	{
			for(ContextVO vo : contexts)
			{
				if(vo.getReference().getObjectId().equals(context))
				{
		    		contextCriterionVO.setSearchString(vo.getReference().getObjectId());
				}
			}
    	}
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	} 

	/**
     * constructor.
     */
    public ContextCriterionBean(List<ContextVO> contexts) 
    {
    	this(new ContextCriterion());
    	this.contexts = contexts;
    	if(contexts!= null && contexts.size()>0)
    	{

    		contextCriterionVO.setSearchString("");
    	}
    }
    
	public ContextCriterionBean(ContextCriterion contextCriterionVO) 
	{
		setContextCriterionVO(contextCriterionVO);
	}

	public String clearCriterion() 
	{
		contextCriterionVO.setSearchString("");
		contextCriterionVO = new ContextCriterion();
		return null;
	}

	public Criterion getContextCriterionVO() 
	{

		return contextCriterionVO;
	}
	
	public void setContextCriterionVO(ContextCriterion contextCriterionVO)
	{
		this.contextCriterionVO = contextCriterionVO;
	}
	
	public List<ContextVO> getContexts() 
	{
		return contexts;
	}

	public void setContexts(List<ContextVO> contexts) 
	{
		this.contexts = contexts;
	} 
 
	public Criterion getCriterionVO() {

		return contextCriterionVO;
	}
}