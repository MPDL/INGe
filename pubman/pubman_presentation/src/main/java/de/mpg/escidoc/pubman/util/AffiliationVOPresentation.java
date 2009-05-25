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

package de.mpg.escidoc.pubman.util;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import de.escidoc.www.services.oum.OrganizationalUnitHandler;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.affiliation.AffiliationBean;
import de.mpg.escidoc.pubman.search.AffiliationDetail;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.pubman.util.AdminHelper;

public class AffiliationVOPresentation extends AffiliationVO implements Comparable<AffiliationVOPresentation>
{
    private List<AffiliationVOPresentation> children = null;
    private AffiliationVOPresentation parent = null;
    private String namePath;
    private String idPath;
    
    private List<AffiliationVO> predecessors = new java.util.ArrayList<AffiliationVO>();
    
    private List<AffiliationVO> successors = new java.util.ArrayList<AffiliationVO>();

    public AffiliationVOPresentation(AffiliationVO affiliation)
    {
        super(affiliation);
        this.namePath = getDetails().getName();
        this.idPath = getReference().getObjectId();
        
        this.successors = getAffiliationVOfromRO(getSuccessorAffiliations());
        this.predecessors = getAffiliationVOfromRO(getPredecessorAffiliations());
        
    }

    public List<AffiliationVOPresentation> getChildren() throws Exception
    {
        if (children == null)
        {
            children = ((ItemControllerSessionBean) FacesContext.getCurrentInstance().getExternalContext()
                    .getSessionMap().get("ItemControllerSessionBean")).searchChildAffiliations(this);
        }
        return children;
    }
    

    public MdsOrganizationalUnitDetailsVO getDetails()
    {
        if (getMetadataSets().size() > 0 && getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO)
        {
            return (MdsOrganizationalUnitDetailsVO) getMetadataSets().get(0);
        }
        else
        {
            return new MdsOrganizationalUnitDetailsVO();
        }
    }

    public boolean getMps()
    {
        return getDetails().getAlternativeNames().contains("MPS");
    }

    public boolean getTopLevel()
    {
        return (parent == null);
    }

    /**
     * This returns a description of the affiliation in a html form.
     * 
     * @return html description
     */
    public String getHtmlDescription()
    {
        InternationalizationHelper i18nHelper = (InternationalizationHelper) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get(InternationalizationHelper.BEAN_NAME);
        ResourceBundle labelBundle = ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle());
        StringBuffer html = new StringBuffer();
        html.append("<html><head></head><body>");
        html.append("<div class=\"affDetails\"><h1>" + labelBundle.getString("AffiliationTree_txtHeadlineDetails")
                + "</h1>");
        html.append("<div class=\"formField\">");
        if (getDetails().getDescriptions().size() > 0 && !"".equals(getDetails().getDescriptions().get(0)))
        {
            html.append("<div>");
            html.append(getDetails().getDescriptions().get(0));
            html.append("</div><br/>");
        }
        for (IdentifierVO identifier : getDetails().getIdentifiers())
        {
            if (!identifier.getId().trim().equals(""))
            {
                html.append("<span>, &nbsp;");
                html.append(identifier.getId());
                html.append("</span>");
            }
        }
        html.append("</div></div>");
        html.append("</body></html>");
        return html.toString();
    }

    public String startSearch()
    {
        ((AffiliationBean) getSessionBean(AffiliationBean.class)).setSelectedAffiliation(this);
        ((AffiliationDetail) getSessionBean(AffiliationDetail.class)).setAffiliationVO(this);
        return ((AffiliationBean) getSessionBean(AffiliationBean.class)).startSearch();
    }

    public AffiliationVOPresentation getParent()
    {
        return parent;
    }

    public void setParent(AffiliationVOPresentation parent)
    {
        this.parent = parent;
    }

    /**
     * Return any bean stored in session scope under the specified name.
     * 
     * @param cls The bean class.
     * @return the actual or new bean instance
     */
    public static synchronized Object getSessionBean(final Class<?> cls)
    {
        String name = null;
        try
        {
            name = (String) cls.getField("BEAN_NAME").get(new String());
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error getting bean name of " + cls, e);
        }
        Object result = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(name);
        if (result == null)
        {
            try
            {
                Object newBean = cls.newInstance();
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(name, newBean);
                return newBean;
            }
            catch (Exception e)
            {
                throw new RuntimeException("Error creating new bean of type " + cls, e);
            }
        }
        else
        {
            return result;
        }
    }

    /** Returns the complete path to this affiliation as a string with the name of the affiliations. */
    public String getNamePath()
    {
        return namePath;
    }

    public void setNamePath(String path)
    {
        this.namePath = path;
    }

    /** Returns the complete path to this affiliation as a string with the ids of the affiliations */
    public String getIdPath()
    {
        return idPath;
    }

    public void setIdPath(String idPath)
    {
        this.idPath = idPath;
    }

    public String getSortOrder()
    {
        if ("closed".equals(this.getPublicStatus()))
        {
            return "3" + getName().toLowerCase();
        }
        else if ("opened".equals(this.getPublicStatus()))
        {
            return "1" + getName().toLowerCase();
        }
        else if ("created".equals(this.getPublicStatus()))
        {
            return "2" + getName().toLowerCase();
        }
        else
        {
            return "9" + getName().toLowerCase();
        }
    }
    
    public String getName()
    {
        if (getMetadataSets().size() > 0 && getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO)
        {
            return ((MdsOrganizationalUnitDetailsVO) getMetadataSets().get(0)).getName();
        }
        else
        {
            return null;
        }
    }

    public List<String> getUris()
    {
        List<IdentifierVO> identifiers = getDefaultMetadata().getIdentifiers();
        List<String> uriList = new ArrayList<String>();
        for (IdentifierVO identifier : identifiers)
        {
            if (identifier.getType() != null && identifier.getType().equals(IdentifierVO.IdType.URI))
            {
                uriList.add(identifier.getId());
            }
        }
        return uriList;
    }

    public boolean getIsClosed()
    {
        return getPublicStatus().equals("closed");
    }

    public int compareTo(AffiliationVOPresentation other)
    {
        return getSortOrder().compareTo(other.getSortOrder());
    }
    
    private List<AffiliationVO> getAffiliationVOfromRO( List<AffiliationRO> affiliations  )
    {
        List<AffiliationVO> transformedAffs = new ArrayList<AffiliationVO>();
        InitialContext initialContext = null;
        XmlTransforming xmlTransforming = null;
        if( affiliations.size() == 0 ) {
            return transformedAffs;
        }
        try
        {
            initialContext = new InitialContext();
            xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
            for( AffiliationRO affiliation : affiliations )
            {
                String userHandle = AdminHelper.getAdminUserHandle();
                OrganizationalUnitHandler ouHandler = ServiceLocator.getOrganizationalUnitHandler(userHandle);
                String ouXml = ouHandler.retrieve(affiliation.getObjectId());
                AffiliationVO affVO = xmlTransforming.transformToAffiliation(ouXml);
                transformedAffs.add(affVO);
            }
            return transformedAffs;
        } 
        catch (Exception e)
        {
            return transformedAffs;
        }
    }

    /**
     * @return the predecessors
     */
    public List<AffiliationVO> getPredecessors()
    {
        return predecessors;
    }

    /**
     * @param predecessors the predecessors to set
     */
    public void setPredecessors(List<AffiliationVO> predecessors)
    {
        this.predecessors = predecessors;
    }

    /**
     * @return the successors
     */
    public List<AffiliationVO> getSuccessors()
    {
        return successors;
    }

    /**
     * @param successors the successors to set
     */
    public void setSuccessors(List<AffiliationVO> successors)
    {
        this.successors = successors;
    }
    
    
}
