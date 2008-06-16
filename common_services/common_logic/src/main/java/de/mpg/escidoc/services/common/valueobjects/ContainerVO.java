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
import de.mpg.escidoc.services.common.referenceobjects.ContainerRO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;

/**
 * Container object which consists of descriptive metadata and may have zero or more members.
 * 
 * @revised by FrW: 10.06.2008
 * @version 1.0
 */
public class ContainerVO extends ValueObject implements Cloneable
{
    /**
     * The possible states of a container.
     */
    public enum State
    {
        PENDING, SUBMITTED, RELEASED, WITHDRAWN, IN_REVISION
    }

    /**
     * The possible lock status of a container.
     */
    public enum LockStatus
    {
        LOCKED, UNLOCKED
    }

    private java.util.List<Object> members = new java.util.ArrayList<Object>();
    private List<MetadataSetVO> metadataSets = new java.util.ArrayList<MetadataSetVO>();

    private AccountUserRO owner;
    /**
     * The persistent identifier of the released container.
     */
    private String pid;
    private ContextRO contextRO;
    private String statusComment;
    private String contentModel;
    
    /**
     * Version information of this container version.
     */
    private ContainerRO version = new ContainerRO();
    
    /**
     * Version information of the latest version of this container.
     */
    private ContainerRO latestVersion = new ContainerRO();
    
    /**
     * Version information of the latest release of this container.
     */
    private ContainerRO latestRelease = new ContainerRO();

    /**
     * This list of relations is a quickfix and cannot be found in the model yet. The reason for this is that the
     * relations are delivered with every item retrieval from the framework, and they get deleted when they are note
     * provided on updates. TODO MuJ or BrP: model and implement correctly, transforming too. Remove quickfix-VO
     * ("ItemRelationVO").
     */
    private List<ItemRelationVO> relations = new java.util.ArrayList<ItemRelationVO>();

    private java.util.Date creationDate;
    private ContainerVO.LockStatus lockStatus;
    private ContainerVO.State state;

    /**
     * Public constructor.
     */
    public ContainerVO()
    {
        super();
    }

    /**
     * Copy constructor.
     * 
     * @param other The instance to copy.
     */
    public ContainerVO(ContainerVO other)
    {
        this.setCreationDate(other.getCreationDate());
        for (Object member : other.getMembers())
        {
            this.getMembers().add((Object) member);
        }
        this.setLockStatus(other.getLockStatus());
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
            this.setVersion((ContainerRO) other.getVersion().clone());
        }
        if (other.getLatestVersion() != null)
        {
            this.setLatestVersion((ContainerRO) other.getLatestVersion().clone());
        }
        if (other.getLatestRelease() != null)
        {
            this.setLatestRelease((ContainerRO) other.getLatestRelease().clone());
        }
        for (ItemRelationVO relation : other.getRelations())
        {
            this.getRelations().add((ItemRelationVO) relation.clone());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone()
    {
        return new ContainerVO(this);
    }

    /**
     * Helper method for JiBX transformations. This method helps JiBX to determine if this is a 'create' or an 'update'
     * transformation.
     * 
     * @return true, if this container already has a version object.
     */
    boolean alreadyExistsInFramework()
    {
        return (this.version != null);
    }

    /**
     * Helper method for JiBX transformations. This method helps JiBX to determine if a "struct-map" XML structure has
     * to be created during marshalling.
     * 
     * @return true, if the container has one or more members.
     */
    boolean hasMembers()
    {
        return (this.members.size() >= 1);
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
     * Delivers the list of members in this container.
     */
    public java.util.List<Object> getMembers()
    {
        return members;
    }

    /**
     * Delivers the metadata sets of the container.
     */
    public List<MetadataSetVO> getMetadataSets()
    {
        return metadataSets;
    }

    /**
     * Delivers the owner of the container.
     */
    public AccountUserRO getOwner()
    {
        return owner;
    }

    /**
     * Delivers the persistent identifier of the container.
     */
    public String getPid()
    {
        return pid;
    }

    /**
     * Delivers the reference of the collection the container is contained in.
     */
    public ContextRO getContext()
    {
        return contextRO;
    }

    /**
     * Delivers the reference of the container.
     */
    public ContainerRO getVersion()
    {
        return version;
    }

    /**
     * Delivers the list of relations in this container.
     */
    public java.util.List<ItemRelationVO> getRelations()
    {
        return relations;
    }

    /**
     * Sets the owner of the container.
     * 
     * @param newVal
     */
    public void setOwner(AccountUserRO newVal)
    {
        owner = newVal;
    }

    /**
     * Sets the persistent identifier of the container.
     * 
     * @param newVal
     */
    public void setPid(String newVal)
    {
        pid = newVal;
    }

    /**
     * Sets the reference of the collection the container is contained in.
     * 
     * @param newVal
     */
    public void setContext(ContextRO newVal)
    {
        contextRO = newVal;
    }

    /**
     * Sets the reference of the container.
     * 
     * @param newVal
     */
    public void setVersion(ContainerRO newVal)
    {
        version = newVal;
    }

    /**
     * Delivers the date when the container was created.
     */
    public java.util.Date getCreationDate()
    {
        return creationDate;
    }

    /**
     * Delivers the lock status of the container.
     */
    public LockStatus getLockStatus()
    {
        return lockStatus;
    }

    public ContainerVO.State getState()
    {
        return state;
    }

    public void setState(ContainerVO.State state)
    {
        this.state = state;
    }

    /**
     * Sets the date when the container was created.
     * 
     * @param newVal
     */
    public void setCreationDate(java.util.Date newVal)
    {
        creationDate = newVal;
    }

    /**
     * Sets the lock status of the container.
     * 
     * @param newVal
     */
    public void setLockStatus(LockStatus newVal)
    {
        this.lockStatus = newVal;
    }

    /**
     * Delivers the comment which has to be given when a container is withdrawn.
     */
    public String getWithdrawalComment()
    {
        if (getVersion().getState() == ContainerVO.State.WITHDRAWN)
        {
            return getVersion().getLastMessage();
        }
        else
        {
            return null;
        }
    }

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
    
    public ContainerRO getLatestVersion()
    {
        return latestVersion;
    }

    public void setLatestVersion(ContainerRO latestVersion)
    {
        this.latestVersion = latestVersion;
    }

    public ContainerRO getLatestRelease()
    {
        return latestRelease;
    }

    public void setLatestRelease(ContainerRO latestRelease)
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

    public String getStatusComment()
    {
        return statusComment;
    }

    public void setStatusComment(String statusComment)
    {
        this.statusComment = statusComment;
    }
    
    /**
     * MetaData
     * @return
     */
    public MdsPublicationVO getMetadata()
    {
        if (getMetadataSets() != null && getMetadataSets().size() > 0 && getMetadataSets().get(0) instanceof MdsPublicationVO)
        {
            return (MdsPublicationVO) getMetadataSets().get(0);
        }
        else
        {
            return null;
        }
    }

    public void setMetadata(MdsPublicationVO mdsPublicationVO)
    {
        if (getMetadataSets().size() > 0 && getMetadataSets().get(0) instanceof MdsPublicationVO)
        {
            getMetadataSets().set(0, mdsPublicationVO);
        }
        else if (getMetadataSets() != null)
        {
            getMetadataSets().add(mdsPublicationVO);
        }
    }
    
}