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

import de.mpg.escidoc.services.common.referenceobjects.PubItemRO;

/**
 * Represents one content relation to one item. In contrast to RelationVO (which
 * contains subject, predicate and object), this class only contains predicate
 * and object.
 * 
 * This class is a workaround for the relations contained in the PubItemVO
 * (therefore it has no source, in contrast to RelationVO). 
 * 
 * @updated 18-Okt-2007 15:42:32
 */
public class PubItemRelationVO extends ValueObject implements Cloneable {
	
	/**
    * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
    * 'de.mpg.escidoc.services.common.valueobjects.PubItemVO; local class incompatible: stream classdesc
    * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286' that occur after
    * JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to be compiled twice for testing (once
    * for the Application Server, once for the local test).
    * 
    * @author Johannes Mueller
    */
   
	
	/**
	 * description of the content relation, e. g. the reason for the relation.
	 */
	private String description;

	/**
	 * The type of the relation.
	 */
	private String type;

	/**
	 * Describes the reference of the target item.
	 */
	private PubItemRO targetItemRef;

	/**
	 * This no-argument constructor is needed by JiBX!
	 * 
	 * @param type
	 * @param targetItemRef
	 */
	public PubItemRelationVO() {
		super();
	}

	/**
	 * @param type
	 * @param targetItemRef
	 */
	public PubItemRelationVO(String type, PubItemRO targetItemRef) {
		super();
		setType(type);
		setTargetItemRef(targetItemRef);
	}

	/**
	 * Copy constructor.
	 * 
	 * @author Thomas Diebaecker
	 * @param other
	 *            The instance to copy.
	 */
	public PubItemRelationVO(PubItemRelationVO other) {
		this(other.getType(), other.getTargetItemRef());
		this.setDescription(other.getDescription());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @author Thomas Diebaecker
	 */
	@Override
	public Object clone() {
		return new PubItemRelationVO(this);
	}

	/**
	 * description of the content relation, e. g. the reason for the relation.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Describes the reference of the target item.
	 */
	public PubItemRO getTargetItemRef() {
		return targetItemRef;
	}

	/**
	 * Describes the reference of the target item.
	 * 
	 * @param newVal
	 */
	public void setTargetItemRef(PubItemRO newVal) {
		targetItemRef = newVal;
	}

	/**
	 * Description of the content relation, e. g. the reason for the relation.
	 * 
	 * @param newVal
	 */
	public void setDescription(String newVal) {
		description = newVal;
	}

	/**
	 * The type of the relation.
	 */
	public String getType() {
		return type;
	}

	/**
	 * The type of the relation.
	 * 
	 * @param newVal
	 */
	public void setType(String newVal) {
		type = newVal;
	}
}