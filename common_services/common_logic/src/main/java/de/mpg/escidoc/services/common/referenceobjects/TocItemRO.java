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

package de.mpg.escidoc.services.common.referenceobjects;

import java.util.Date;

import de.mpg.escidoc.services.common.valueobjects.ItemVO;


/**
 * The class for TOC item references.
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class TocItemRO extends ReferenceObject implements Cloneable
{
    
    private static final long serialVersionUID = 1L;

    /**
     * The version number of the referenced item. This attribute is optional.
     */
    private int versionNumber;

    /**
     * The date of the last modification of the referenced item.
     */
    private Date modificationDate;

    /**
     * The message of the last action event of this item.
     */
    private String lastMessage;

    /**
     * The state of the item.
     */
    private ItemVO.State state;

    /**
     * The version PID of the item.
     */
    private String pid;

    /**
     * Creates a new instance.
     */
    public TocItemRO()
    {
        super();
    }

    /**
     * Creates a new instance with the given objectId.
     * @param objectId The id of the object.
     */
    public TocItemRO(String objectId)
    {
        super(objectId);
    }

    /**
     * Copy constructor.
     *
     * @author Thomas Diebaecker
     * @param other The instance to copy.
     */
    public TocItemRO(TocItemRO other)
    {
        super(other);
        this.versionNumber = other.versionNumber;
        this.lastMessage = other.lastMessage;
        this.state = other.state;
        this.modificationDate = other.modificationDate;
        this.pid = other.pid;
    }

    /**
     * {@inheritDoc}
     * @author Thomas Diebaecker
     */
    @Override
    public Object clone()
    {
        return new TocItemRO(this);
    }

    /**
     * Get the full identification of an item version.
     *
     * @return A String in the form objid:versionNumber e.g. "escidoc:345:2"
     */
    public String getObjectIdAndVersion()
    {
        if (versionNumber != 0)
        {
            return getObjectId() + ":" + versionNumber;
        }
        else
        {
            return getObjectId();
        }

    }

    /**
     * Set the full identification of an item version.
     *
     * @param idString A String in the form objid:versionNumber e.g. "escidoc:345:2"
     */
    public void setObjectIdAndVersion(String idString)
    {
        int ix = idString.lastIndexOf(":");
        if (ix == -1)
        {
            setObjectId(idString);
            versionNumber = 0;
        }
        else
        {
            setObjectId(idString.substring(0, ix));
            versionNumber = Integer.parseInt(idString.substring(ix + 1));
        }
    }

    /**
     * The version number of the referenced item. This attribute is optional.
     */
    public int getVersionNumber()
    {
        return versionNumber;
    }

    /**
     * The version number of the referenced item. This attribute is optional.
     *
     * @param newVal
     */
    public void setVersionNumber(int newVal)
    {
        versionNumber = newVal;
    }

    public Date getModificationDate()
    {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate)
    {
        this.modificationDate = modificationDate;
    }

    public String getLastMessage()
    {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage)
    {
        this.lastMessage = lastMessage;
    }

    /**
     * Delivers the state of the item.
     */
    public ItemVO.State getState()
    {
        return state;
    }

    /**
     * Sets the state of the item.
     *
     * @param newVal
     */
    public void setState(ItemVO.State newVal)
    {
        state = newVal;
    }

    public String getPid()
    {
        return pid;
    }

    public void setPid(String pid)
    {
        this.pid = pid;
    }

    @Override
    public boolean equals(Object object)
    {
        if (super.equals(object))
        {
            return (((TocItemRO) object).versionNumber == this.versionNumber);
        }
        else
        {
            return false;
        }
    }

}