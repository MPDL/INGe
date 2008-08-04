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
package de.mpg.escidoc.services.common.valueobjects;

import java.util.Date;
import java.util.List;

import de.mpg.escidoc.services.common.referenceobjects.AccountUserRO;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.referenceobjects.TocItemRO;


/**
 * Object representing a Toc Item (http://www.escidoc.de/schemas/toc/0.6)
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class TocItemVO extends ValueObject implements Cloneable
{
 


    private List<MetadataSetVO> metadataSets = new java.util.ArrayList<MetadataSetVO>();

    private AccountUserRO owner;
    /**
     * The persistent identifier of the released toc.
     */
    private String pid;
    private ContextRO contextRO;
    
    private String contentModel;
    
    /**
     * Version information of this toc version.
     */
    private TocItemRO version = new TocItemRO();
    
    /**
     * Version information of the latest version of this toc.
     */
    private TocItemRO latestVersion = new TocItemRO();
    
    /**
     * Version information of the latest release of this toc.
     */
    private TocItemRO latestRelease = new TocItemRO();
    

    private List<ItemRelationVO> relations = new java.util.ArrayList<ItemRelationVO>();

    private java.util.Date creationDate;
    private ItemVO.LockStatus lockStatus;
    private ItemVO.State publicStatus;
    private String publicStatusComment;
    
    private TocVO tocVO;

    public TocVO getTocVO()
    {
        return tocVO;
    }

    public void setTocVO(TocVO tocVO)
    {
        this.tocVO = tocVO;
    }

    public String getPublicStatusComment()
    {
        return publicStatusComment;
    }

    public void setPublicStatusComment(String publicStatusComment)
    {
        this.publicStatusComment = publicStatusComment;
    }

    /**
     * Public constructor.
     **/
  
    public TocItemVO()
    {
        super();
    }

    /**
     * Copy constructor.
     * 
     * @author Thomas Diebaecker
     * @param other The instance to copy.
     */
    public TocItemVO(TocItemVO other)
    {
        this.setCreationDate(other.getCreationDate());
       
        this.setLockStatus(other.getLockStatus());
        this.setPublicStatus(other.getPublicStatus());
        this.setPublicStatusComment(other.getPublicStatusComment());
        for (MetadataSetVO mds : other.getMetadataSets())
        {
            this.getMetadataSets().add(mds.clone());
        }
        if (other.getOwner() != null)
        {
            this.setOwner((AccountUserRO) other.getOwner().clone());
        }
        this.setPid(other.getPid());
        if (other.getContext() != null)
        {
            this.setContext((ContextRO) other.getContext().clone());
        }
        if (other.getContentModel() != null)
        {
            this.setContentModel(other.getContentModel());
        }
        if (other.getVersion() != null)
        {
            this.setVersion((TocItemRO) other.getVersion().clone());
        }
        if (other.getLatestVersion() != null)
        {
            this.setLatestVersion((TocItemRO) other.getLatestVersion().clone());
        }
        if (other.getLatestRelease() != null)
        {
            this.setLatestRelease((TocItemRO) other.getLatestRelease().clone());
        }
        for (ItemRelationVO relation : other.getRelations())
        {
            this.getRelations().add((ItemRelationVO) relation.clone());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @author Thomas Diebaecker
     */
    @Override
    public Object clone()
    {
        return new TocItemVO(this);
    }

    /**
     * Helper method for JiBX transformations. This method helps JiBX to determine if this is a 'create' or an 'update'
     * transformation.
     * 
     * @return true, if this item already has a version object.
     */
    boolean alreadyExistsInFramework()
    {
        return (this.version != null);
    }

   

    /**
     * Helper method for JiBX transformations.
     */
    boolean hasPID()
    {
        return (this.pid != null);
    }

    /**
     * Helper method for JiBX transformations. This method helps JiBX to determine if a "relations" XML structure has to
     * be created during marshalling.
     */
    boolean hasRelations()
    {
        return (this.relations.size() >= 1);
    }

   

    /**
     * Delivers the metadata sets of the item.
     */
    public List<MetadataSetVO> getMetadataSets()
    {
        return metadataSets;
    }

    /**
     * Delivers the owner of the item.
     */
    public AccountUserRO getOwner()
    {
        return owner;
    }

    /**
     * Delivers the persistent identifier of the item.
     */
    public String getPid()
    {
        return pid;
    }

    /**
     * Delivers the reference of the collection the item is contained in.
     */
    public ContextRO getContext()
    {
        return contextRO;
    }

    /**
     * Delivers the reference of the item.
     */
    public TocItemRO getVersion()
    {
        return version;
    }

    /**
     * Delivers the list of relations in this item.
     */
    public java.util.List<ItemRelationVO> getRelations()
    {
        return relations;
    }

    /**
     * Sets the owner of the item.
     * 
     * @param newVal
     */
    public void setOwner(AccountUserRO newVal)
    {
        owner = newVal;
    }

    /**
     * Sets the persistent identifier of the item.
     * 
     * @param newVal
     */
    public void setPid(String newVal)
    {
        pid = newVal;
    }

    /**
     * Sets the reference of the collection the item is contained in.
     * 
     * @param newVal
     */
    public void setContext(ContextRO newVal)
    {
        contextRO = newVal;
    }

    /**
     * Sets the reference of the item.
     * 
     * @param newVal
     */
    public void setVersion(TocItemRO newVal)
    {
        version = newVal;
    }

    /**
     * Delivers the date when the item was created.
     */
    public java.util.Date getCreationDate()
    {
        return creationDate;
    }

    /**
     * Delivers the lock status of the item.
     */
    public ItemVO.LockStatus getLockStatus()
    {
        return lockStatus;
    }

    /**
     * Sets the date when the item was created.
     * 
     * @param newVal
     */
    public void setCreationDate(java.util.Date newVal)
    {
        creationDate = newVal;
    }

    /**
     * Sets the lock status of the item.
     * 
     * @param newVal
     */
    public void setLockStatus(ItemVO.LockStatus newVal)
    {
        this.lockStatus = newVal;
    }

    /**
     * Delivers the comment which has to be given when an item is withdrawn.
     */
    public String getWithdrawalComment()
    {
        if (getPublicStatus() == ItemVO.State.WITHDRAWN)
        {
            return getPublicStatusComment();
        }
        else
        {
            return null;
        }
    }

    /**
     * Delivers the comment which has to be given when an item is withdrawn.
     */
    public Date getModificationDate()
    {
        if (getVersion() != null)
        {
            return getVersion().getModificationDate();
        }
        else
        {
            return null;
        }
    }
    
    public TocItemRO getLatestVersion()
    {
        return latestVersion;
    }

    public void setLatestVersion(TocItemRO latestVersion)
    {
        this.latestVersion = latestVersion;
    }

    public TocItemRO getLatestRelease()
    {
        return latestRelease;
    }

    public void setLatestRelease(TocItemRO latestRelease)
    {
        this.latestRelease = latestRelease;
    }

    public String getContentModel()
    {
        return contentModel;
    }

    public void setContentModel(String contentModel)
    {
        this.contentModel = contentModel;
    }

    public ItemVO.State getPublicStatus()
    {
        return publicStatus;
    }
    
    public void setPublicStatus(ItemVO.State publicStatus)
    {
        this.publicStatus = publicStatus;
    }


}