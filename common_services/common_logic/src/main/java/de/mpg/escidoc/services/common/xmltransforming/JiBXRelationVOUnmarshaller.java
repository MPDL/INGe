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

package de.mpg.escidoc.services.common.xmltransforming;

import java.util.ArrayList;
import java.util.List;

import org.jibx.runtime.IAliasable;
import org.jibx.runtime.IUnmarshaller;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.UnmarshallingContext;

import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.RelationVO;
import de.mpg.escidoc.services.common.valueobjects.RelationVO.RelationType;

/**
 * This class is a helper class for JiBX binding of TaskParamVO (and subclasses) to XML.
 * 
 * @author Johannes Mueller (initial creation)
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @revised by MuJ: 03.09.2007
 */
public class JiBXRelationVOUnmarshaller implements IUnmarshaller, IAliasable
{
    private static final String RDF_NAMESPACE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static final String CONTENT_RELATIONS_NAMESPACE = "http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#";
    private static final String RDF_DESCRIPTION_ELEMENT_NAME = "Description";
    private static final String RDF_SUBJECT_ATTRIBUTE_NAME = "about";
    private static final String RDF_PREDICATE_ATTRIBUTE_NAME = "resource";
    private static final String RDF_PREDICATE_ELEMENT_NAME_IS_REVISION_OF = "isRevisionOf";

    private String m_uri;
    private int m_index;
    private String m_name;

    /**
     * The standard constructor.
     */
    public JiBXRelationVOUnmarshaller()
    {
        m_uri = null;
        m_index = 0;
        m_name = "hashmap";
    }

    /**
     * Constructor assigning uri, index and name of the start node.
     * 
     * @param uri
     * @param index
     * @param name
     */
    public JiBXRelationVOUnmarshaller(String uri, int index, String name)
    {
        m_uri = uri;
        m_index = index;
        m_name = name;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPresent(IUnmarshallingContext ctx) throws JiBXException
    {
        return ctx.isAt(m_uri, m_name);
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal(Object obj, IUnmarshallingContext ictx) throws JiBXException
    {

        // make sure the parameters are as expected
        if (!(obj instanceof FilterTaskParamVO))
        {
            throw new JiBXException("Invalid object type for unmarshaller");
        }
        else if (!(ictx instanceof UnmarshallingContext))
        {
            throw new JiBXException("Invalid object type for unmarshaller");
        }

        // make sure we're at the appropriate start tag
        UnmarshallingContext ctx = (UnmarshallingContext)ictx;
        if (!ctx.isAt(m_uri, m_name))
        {
            ctx.throwStartTagNameError(m_uri, m_name);
        }

        // create new List<RelationVO>
        List<RelationVO> relations = new ArrayList<RelationVO>();

        // parse past start of element to unmarshall ("rdf:RDF")
        ctx.parsePastStartTag(m_uri, m_name);

        // process all "Description" entries present in the "rdf:RDF" document
        while (!ctx.isEnd())
        {
            if (ctx.isAt(RDF_NAMESPACE, RDF_DESCRIPTION_ELEMENT_NAME))
            {
                // determine the subject (Description.@rdf:about)
                String subject = ctx.attributeText(RDF_NAMESPACE, RDF_SUBJECT_ATTRIBUTE_NAME);
                int lastSlashPosition = subject.lastIndexOf('/');
                String subjectObjectId = subject.substring(lastSlashPosition + 1);
                ItemRO subjectRef = new ItemRO(subjectObjectId);
                ctx.parsePastStartTag(RDF_NAMESPACE, RDF_DESCRIPTION_ELEMENT_NAME);

                // process all predicate-object pairs in the Description.
                // for every relevant pair, a RelationVO is created
                // at the moment, only the "isRevsisionOf" relation is relevant
                while (!ctx.isEnd())
                {
                    if (ctx.isAt(CONTENT_RELATIONS_NAMESPACE, RDF_PREDICATE_ELEMENT_NAME_IS_REVISION_OF))
                    {
                        // determine the predicate (Description.isRevisionOf.@rdf:resource)
                        String predicate = ctx.attributeText(RDF_NAMESPACE, RDF_PREDICATE_ATTRIBUTE_NAME);
                        lastSlashPosition = predicate.lastIndexOf('/');
                        String predicateObjectId = subject.substring(lastSlashPosition + 1);
                        ItemRO predicateRef = new ItemRO(predicateObjectId);
                        // add new RelationVO
                        RelationVO relation = new RelationVO();
                        relation.setSourceItemRef(subjectRef);
                        relation.settype(RelationType.ISREVISIONOF);
                        relation.setTargetItemRef(predicateRef);
                        relations.add(relation);
                    }
                    else
                    {
                        ctx.skipElement();
                    }
                }
            }
            else
            {
                ctx.throwStartTagNameError(RDF_NAMESPACE, RDF_DESCRIPTION_ELEMENT_NAME);
            }
        }

        // parse past end of element to unmarshall ("rdf:RDF")
        ctx.parsePastEndTag(m_uri, m_name);
        return relations;
    }

    /**
     * @return the m_uri
     */
    public String getM_uri()
    {
        return m_uri;
    }

    /**
     * @param m_uri the m_uri to set
     */
    public void setM_uri(String m_uri)
    {
        this.m_uri = m_uri;
    }
}
