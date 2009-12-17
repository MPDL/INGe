package org.fao.oa.ingestion.foxml;

import java.io.File;
import java.io.IOException;

import noNamespace.ITEMType;
import noNamespace.ItemType;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlString;
import org.fao.oa.ingestion.utils.XBeanUtils;
import org.purl.agmes.x11.ResourcesDocument;
import org.purl.agmes.x11.ResourceDocument.Resource;
import org.purl.agmes.x11.ResourceDocument.Resource.Subject;
import org.purl.agmes.x11.ResourceDocument.Resource.Subject.SubjectClassification;
import org.purl.agmes.x11.ResourcesDocument.Resources;

public class AgrisAPDatastream
{
    public ResourcesDocument agrisValues(ITEMType faodoc, ItemType eims) throws IOException
    {
        ResourcesDocument agris = ResourcesDocument.Factory.newInstance();
        Resources res = agris.addNewResources();
        Resource agmesResource = res.addNewResource();
        if (faodoc.getARNArray(0) != null)
        {
            agmesResource.setARN(faodoc.getARNArray(0));
        }
        if (faodoc.sizeOfSUBJCODEArray() > 0)
        {
            Subject subject = agmesResource.addNewSubject();
            for (String subj : faodoc.getSUBJCODEArray())
            {
                SubjectClassification subjCode = subject.addNewSubjectClassification();
                XmlString code = XmlString.Factory.newInstance();
                code.setStringValue(subj);
                subjCode.set(code);
                subjCode.setScheme(SubjectClassification.Scheme.AGS_ASC);
            }
        }
        return agris;
    }
}
