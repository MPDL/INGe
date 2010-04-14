package org.fao.oa.ingestion.foxml;

import noNamespace.AGRISType;
import noNamespace.ITEMType;
import noNamespace.ItemType;

import org.apache.xmlbeans.XmlString;
import org.purl.agmes.x11.ResourcesDocument;
import org.purl.agmes.x11.ResourceDocument.Resource;
import org.purl.agmes.x11.ResourceDocument.Resource.Subject;
import org.purl.agmes.x11.ResourceDocument.Resource.Subject.SubjectClassification;
import org.purl.agmes.x11.ResourcesDocument.Resources;

public class AgrisAPDatastream
{
    /**
     * create AGRIS_AP datastrem with merged values from FAODOC and EIMS-CDR.
     * @param faodoc {@link ITEMType}
     * @param eims {@link ItemType}
     * @return {@link ResourcesDocument}
     */
    public ResourcesDocument merge(ITEMType faodoc, ItemType eims)
    {
        ResourcesDocument agris = ResourcesDocument.Factory.newInstance();
        Resources res = agris.addNewResources();
        Resource agmesResource = res.addNewResource();
        // A-1
        // add ARN attribute to resource
        if (faodoc.getARNArray(0) != null)
        {
            agmesResource.setARN(faodoc.getARNArray(0));
        }
        // A-2 + A-3
        // add SubjectClassifications
        if (faodoc.sizeOfSUBJCODEArray() > 0 && faodoc.sizeOfSUBJNAMEArray() > 0)
        {
            Subject subject = agmesResource.addNewSubject();
            if (faodoc.sizeOfSUBJCODEArray() == faodoc.sizeOfSUBJNAMEArray())
            {
                for (int s = 0; s < faodoc.sizeOfSUBJCODEArray(); s++)
                {
                    SubjectClassification subjCode = subject.addNewSubjectClassification();
                    XmlString code = XmlString.Factory.newInstance();
                    code.setStringValue(faodoc.getSUBJCODEArray(s));
                    subjCode.set(code);
                    subjCode.setScheme(SubjectClassification.Scheme.AGS_ASC);
                    SubjectClassification subjName = subject.addNewSubjectClassification();
                    XmlString name = XmlString.Factory.newInstance();
                    name.setStringValue(faodoc.getSUBJNAMEArray(s));
                    subjName.set(name);
                    subjName.setScheme(SubjectClassification.Scheme.AGS_ASC);
                    subjName.setLanguage("en");
                }
            }
        }
        else
        {
            if (eims.sizeOfAGRISArray() > 0)
            {
                Subject subject = agmesResource.addNewSubject();
                for (AGRISType agrisType : eims.getAGRISArray())
                {
                    SubjectClassification subjCode = subject.addNewSubjectClassification();
                    XmlString code = XmlString.Factory.newInstance();
                    code.setStringValue(agrisType.getCode());
                    subjCode.set(code);
                    subjCode.setScheme(SubjectClassification.Scheme.AGS_ASC);
                    SubjectClassification subjName = subject.addNewSubjectClassification();
                    XmlString name = XmlString.Factory.newInstance();
                    name.setStringValue(agrisType.getStringValue());
                    subjName.set(name);
                    subjName.setScheme(SubjectClassification.Scheme.AGS_ASC);
                    subjName.setLanguage(agrisType.getLang());
                }
            }
        }
        return agris;
    }

    /**
     * create AGRIS_AP datastrem with values from FAODOC only.
     * @param faodoc {@link ITEMType}
     * @return {@link ResourcesDocument}
     */
    public ResourcesDocument create4Faodoc(ITEMType faodoc)
    {
        ResourcesDocument agris = ResourcesDocument.Factory.newInstance();
        Resources res = agris.addNewResources();
        Resource agmesResource = res.addNewResource();
        // A-1
        // add ARN attribute to resource
        if (faodoc.getARNArray(0) != null)
        {
            agmesResource.setARN(faodoc.getARNArray(0));
        }
        // A-2 + A-3
        // add SubjectClassifications
        if (faodoc.sizeOfSUBJCODEArray() > 0 && faodoc.sizeOfSUBJNAMEArray() > 0)
        {
            Subject subject = agmesResource.addNewSubject();
            if (faodoc.sizeOfSUBJCODEArray() == faodoc.sizeOfSUBJNAMEArray())
            {
                for (int s = 0; s < faodoc.sizeOfSUBJCODEArray(); s++)
                {
                    SubjectClassification subjCode = subject.addNewSubjectClassification();
                    XmlString code = XmlString.Factory.newInstance();
                    code.setStringValue(faodoc.getSUBJCODEArray(s));
                    subjCode.set(code);
                    subjCode.setScheme(SubjectClassification.Scheme.AGS_ASC);
                    SubjectClassification subjName = subject.addNewSubjectClassification();
                    XmlString name = XmlString.Factory.newInstance();
                    name.setStringValue(faodoc.getSUBJNAMEArray(s));
                    subjName.set(name);
                    subjName.setScheme(SubjectClassification.Scheme.AGS_ASC);
                    subjName.setLanguage("en");
                }
            }
        }
        return agris;
    }

    /**
     * create AGRIS_AP datastrem with values from EIMS-CDR only.
     * @param eims {@link ItemType}
     * @return {@link ResourcesDocument}
     */
    public ResourcesDocument create4Eims(ItemType eims)
    {
        ResourcesDocument agris = ResourcesDocument.Factory.newInstance();
        Resources res = agris.addNewResources();
        Resource agmesResource = res.addNewResource();
        // A-2 + A-3
        // add SubjectClassifications
        if (eims.sizeOfAGRISArray() > 0)
        {
            Subject subject = agmesResource.addNewSubject();
            for (AGRISType agrisType : eims.getAGRISArray())
            {
                SubjectClassification subjCode = subject.addNewSubjectClassification();
                XmlString code = XmlString.Factory.newInstance();
                code.setStringValue(agrisType.getCode());
                subjCode.set(code);
                subjCode.setScheme(SubjectClassification.Scheme.AGS_ASC);
                SubjectClassification subjName = subject.addNewSubjectClassification();
                XmlString name = XmlString.Factory.newInstance();
                name.setStringValue(agrisType.getStringValue());
                subjName.set(name);
                subjName.setScheme(SubjectClassification.Scheme.AGS_ASC);
                subjName.setLanguage(agrisType.getLang());
            }
        }
        return agris;
    }
}
