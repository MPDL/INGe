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

package de.mpg.escidoc.services.common.valueobjects.face;

import de.mpg.escidoc.services.common.valueobjects.MetadataSetVO;

/**
 * Metadata content of a Faces' face.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class MdsFaceVO extends MetadataSetVO
{
    
    private Emotion emotion;
    private PictureGroup pictureGroup;
    private String identifier;
    private int age;
    private AgeGroup ageGroup;
    private Gender gender;
    private AccessLevel accessLevel;
    
    public enum Emotion
    {
        neutrality, sadness, disgust, fear, anger, happiness
    }
    
    public enum PictureGroup
    {
        a, b
    }
    
    public enum AgeGroup
    {
        young , middle_age, old
    }
    
    public enum Gender
    {
        female, male
    }
    
    public enum AccessLevel
    {
        PUBLIC, PRIVATE
    }

    /**
     * Creates a new instance.
     */
    public MdsFaceVO()
    {
        super();
    }

    /**
     * Copy constructor.
     * 
     * @param other The instance to copy.
     */
    public MdsFaceVO(MdsFaceVO mdsFaceVO)
    {
        this.accessLevel = mdsFaceVO.accessLevel;
        this.age = mdsFaceVO.age;
        this.ageGroup = mdsFaceVO.ageGroup;
        this.emotion = mdsFaceVO.emotion;
        this.gender = mdsFaceVO.gender;
        this.identifier = mdsFaceVO.identifier;
        this.pictureGroup = mdsFaceVO.pictureGroup;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MdsFaceVO clone()
    {
        return new MdsFaceVO(this);
    }

    public Emotion getEmotion()
    {
        return emotion;
    }

    public void setEmotion(Emotion emotion)
    {
        this.emotion = emotion;
    }

    public PictureGroup getPictureGroup()
    {
        return pictureGroup;
    }

    public void setPictureGroup(PictureGroup pictureGroup)
    {
        this.pictureGroup = pictureGroup;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public void setIdentifier(String identifier)
    {
        this.identifier = identifier;
    }

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public AgeGroup getAgeGroup()
    {
        return ageGroup;
    }

    public void setAgeGroup(AgeGroup ageGroup)
    {
        this.ageGroup = ageGroup;
    }

    public Gender getGender()
    {
        return gender;
    }

    public void setGender(Gender gender)
    {
        this.gender = gender;
    }

    public AccessLevel getAccessLevel()
    {
        return accessLevel;
    }

    public void setAccessLevel(AccessLevel accessLevel)
    {
        this.accessLevel = accessLevel;
    }
    
}
