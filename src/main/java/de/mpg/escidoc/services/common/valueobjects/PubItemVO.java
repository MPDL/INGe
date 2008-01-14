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
package de.mpg.escidoc.services.common.valueobjects;

import java.util.List;
import de.mpg.escidoc.services.common.referenceobjects.AccountUserRO;
import de.mpg.escidoc.services.common.referenceobjects.PubCollectionRO;
import de.mpg.escidoc.services.common.referenceobjects.PubItemRO;

/**
 * Publication object which consists of descriptive metadata and may have one or more files associated.
 * 
 * @revised by MuJ: 28.08.2007
 * @version $Revision: 641 $ $LastChangedDate: 2007-11-22 14:49:16 +0100 (Thu, 22 Nov 2007) $ by $Author: jmueller $
 * @updated 21-Nov-2007 11:52:58
 */
public class PubItemVO extends ValueObject implements Cloneable
{
    /**
     * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
     * 'de.mpg.escidoc.services.common.valueobjects.PubItemVO; local class incompatible: stream classdesc
     * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286' that occur after
     * JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to be compiled twice for testing (once
     * for the Application Server, once for the local test).
     * 
     * @author Johannes Mueller
     */
    private static final long serialVersionUID = 1L;

    /**
     * The possible states of an item.
     * 
     * @updated 21-Nov-2007 11:52:58
     */
    public enum State
    {
        PENDING, SUBMITTED, RELEASED, WITHDRAWN, IN_REVISION
    }

    /**
     * The possible lock status of an item.
     * 
     * @updated 21-Nov-2007 11:52:58
     */
    public enum LockStatus
    {
        LOCKED, UNLOCKED
    }

    private java.util.List<PubFileVO> files = new java.util.ArrayList<PubFileVO>();
    private MdsPublicationVO metadata;
    /**
     * This date is updated whenever the item is stored.
     */
    private java.util.Date modificationDate;
    private AccountUserRO owner;
    /**
     * The persistent identifier of the released item.
     */
    private String pid;
    private PubCollectionRO pubCollection;
    private PubItemRO reference;
    /**
     * This is the number of the latest item version that exists. Comparing this number with the number of the current
     * item's versionNumber (which is contained in its reference) helps to determine whether the current item's version
     * is the latest version that exists or not.
     */
    private int latestVersionNumber = -1;
    /**
     * This list of relations is a quickfix and cannot be found in the model yet. The reason for this is that the
     * relations are delivered with every item retrieval from the framework, and they get deleted when they are note
     * provided on updates. TODO MuJ or BrP: model and implement correctly, transforming too. Remove quickfix-VO
     * ("PubItemRelationVO").
     */
    private List<PubItemRelationVO> relations = new java.util.ArrayList<PubItemRelationVO>();
    /**
     * The state of the item.
     */
    private PubItemVO.State state;
    private java.util.Date creationDate;
    private PubItemVO.LockStatus lockStatus;
    /**
     * A comment which has to be given when an item is withdrawn.
     */
    private String withdrawalComment;

    /**
     * Public contructor.
     * 
     * @author Thomas Diebaecker
     */
    public PubItemVO()
    {
        super();
    }

    /**
     * Copy constructor.
     * 
     * @author Thomas Diebaecker
     * @param other The instance to copy.
     */
    public PubItemVO(PubItemVO other)
    {
        this.setCreationDate(other.getCreationDate());
        for (PubFileVO file : other.getFiles())
        {
            this.getFiles().add((PubFileVO)file.clone());
        }
        this.setLockStatus(other.getLockStatus());
        if (other.getMetadata() != null)
        {
            this.setMetadata((MdsPublicationVO)other.getMetadata().clone());
        }
        this.setModificationDate(other.getModificationDate());
        if (other.getOwner() != null)
        {
            this.setOwner((AccountUserRO)other.getOwner().clone());
        }
        this.setPid(other.getPid());
        if (other.getPubCollection() != null)
        {
            this.setPubCollection((PubCollectionRO)other.getPubCollection().clone());
        }
        if (other.getReference() != null)
        {
            this.setReference((PubItemRO)other.getReference().clone());
        }
        for (PubItemRelationVO relation : other.getRelations())
        {
            this.getRelations().add((PubItemRelationVO)relation.clone());
        }
        this.setState(other.getState());
        this.setWithdrawalComment(other.getWithdrawalComment());
    }

    /**
     * {@inheritDoc}
     * 
     * @author Thomas Diebaecker
     */
    @Override
    public Object clone()
    {
        return new PubItemVO(this);
    }

    /**
     * Helper method for JiBX transformations. This method helps JiBX to determine if this is a 'create' or an 'update'
     * transformation.
     */
    boolean alreadyExistsInFramework()
    {
        return (this.reference != null);
    }

    /**
     * Helper method for JiBX transformations. This method helps JiBX to determine if a "components" XML structure has
     * to be created during marshalling.
     */
    boolean hasFiles()
    {
        return (this.files.size() >= 1);
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
     * Delivers the list of files in this item.
     */
    public java.util.List<PubFileVO> getFiles()
    {
        return files;
    }

    /**
     * Delivers the metadata of the item.
     */
    public MdsPublicationVO getMetadata()
    {
        return metadata;
    }

    /**
     * Delivers the date of the last modification of the item.
     */
    public java.util.Date getModificationDate()
    {
        return modificationDate;
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
    public PubCollectionRO getPubCollection()
    {
        return pubCollection;
    }

    /**
     * Delivers the reference of the item.
     */
    public PubItemRO getReference()
    {
        return reference;
    }

    /**
     * Delivers the list of relations in this item.
     */
    public java.util.List<PubItemRelationVO> getRelations()
    {
        return relations;
    }

    /**
     * Delivers the state of the item.
     */
    public PubItemVO.State getState()
    {
        return state;
    }

    /**
     * Sets the metadata of the item.
     * 
     * @param newVal
     */
    public void setMetadata(MdsPublicationVO newVal)
    {
        metadata = newVal;
    }

    /**
     * Sets the date of the last modification of the item.
     * 
     * @param newVal
     */
    public void setModificationDate(java.util.Date newVal)
    {
        modificationDate = newVal;
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
    public void setPubCollection(PubCollectionRO newVal)
    {
        pubCollection = newVal;
    }

    /**
     * Sets the reference of the item.
     * 
     * @param newVal
     */
    public void setReference(PubItemRO newVal)
    {
        reference = newVal;
    }

    /**
     * Sets the state of the item.
     * 
     * @param newVal
     */
    public void setState(PubItemVO.State newVal)
    {
        state = newVal;
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
    public LockStatus getLockStatus()
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
    public void setLockStatus(LockStatus newVal)
    {
        this.lockStatus = newVal;
    }

    /**
     * Delivers the comment which has to be given when an item is withdrawn.
     */
    public String getWithdrawalComment()
    {
        return withdrawalComment;
    }

    /**
     * Sets the comment which has to be given when an item is withdrawn.
     * 
     * @param newVal
     */
    public void setWithdrawalComment(String newVal)
    {
        withdrawalComment = newVal;
    }

    /**
     * Delivers the number of the latest item version that exists. Comparing this number with the number of the current
     * item's versionNumber (which is contained in its reference) helps to determine whether the current item's version
     * is the latest version that exists or not.
     */
    public int getLatestVersionNumber()
    {
        return latestVersionNumber;
    }

    /**
     * Sets the number of the latest item version that exists. Comparing this number with the number of the current
     * item's versionNumber (which is contained in its reference) helps to determine whether the current item's version
     * is the latest version that exists or not.
     */
    public void setLatestVersionNumber(int latestVersionNumber)
    {
        this.latestVersionNumber = latestVersionNumber;
    }
}