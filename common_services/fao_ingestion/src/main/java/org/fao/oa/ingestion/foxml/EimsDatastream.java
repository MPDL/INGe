package org.fao.oa.ingestion.foxml;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.xmlbeans.XmlString;

import noNamespace.CategoryType;
import noNamespace.EimsDocument;
import noNamespace.ITEMType;
import noNamespace.ItemType;
import noNamespace.KeywordType;
import noNamespace.PriorityAreasType;
import noNamespace.ProgramNameType;
import noNamespace.PwbEntitiesType;
import noNamespace.StatBodyType;
import noNamespace.TopicType;
import noNamespace.EimsDocument.Eims;
import noNamespace.EimsDocument.Eims.Publication;
import noNamespace.EimsDocument.Eims.Publication.Category;
import noNamespace.EimsDocument.Eims.Publication.Context;
import noNamespace.EimsDocument.Eims.Publication.Department;
import noNamespace.EimsDocument.Eims.Publication.Division;
import noNamespace.EimsDocument.Eims.Publication.Hardcopy;
import noNamespace.EimsDocument.Eims.Publication.Keyword;
import noNamespace.EimsDocument.Eims.Publication.Maintype;
import noNamespace.EimsDocument.Eims.Publication.PriorityAreas;
import noNamespace.EimsDocument.Eims.Publication.ProgramName;
import noNamespace.EimsDocument.Eims.Publication.PwbEntities;
import noNamespace.EimsDocument.Eims.Publication.Service;
import noNamespace.EimsDocument.Eims.Publication.StatBody;
import noNamespace.EimsDocument.Eims.Publication.Topic;

public class EimsDatastream
{
    Publication eims = null;
    SimpleDateFormat sdf1 = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss");
    SimpleDateFormat sdf2 = new SimpleDateFormat("dd/mm/yyyy");

    /**
     * create EIMS datastream with merged values from FAODOC and EIMS-CDR
     * @param eimsItem {@link ItemType}
     * @param faodocItem {@link ITEMType}
     * @return {@link EimsDocument}
     */
    public EimsDocument merge(ItemType eimsItem, ITEMType faodocItem)
    {
        EimsDocument eimsDoc = EimsDocument.Factory.newInstance();
        Eims eimsType = eimsDoc.addNewEims();
        eims = eimsType.addNewPublication();
        // E-1
        // add eims:identifier
        if (eimsItem.getIdentifier() != null)
        {
            eims.setIdentifier(new BigInteger(eimsItem.getIdentifier()));
        }
        // E-2
        // add eims:bib_reference
        if (eimsItem.getBibReference() != null)
        {
            eims.setBibReference(eimsItem.getBibReference());
        }
        // E-3
        // add eims:category
        if (eimsItem.sizeOfCategoryArray() > 0)
        {
            for (CategoryType catType : eimsItem.getCategoryArray())
            {
                Category cat = eims.addNewCategory();
                cat.setCode(new BigInteger(catType.getCode()));
                cat.setLang(catType.getLang());
                cat.setLabel(catType.getStringValue());
            }
        }
        // E-4
        // add eims:hardcopy
        if (eimsItem.getHardcopy() != null)
        {
            eims.addHardcopy(Hardcopy.Enum.forString(eimsItem.getHardcopy()));
        }
        // E-5
        // add eims:keyword
        if (eimsItem.sizeOfKeywordArray() > 0)
        {
            for (KeywordType kwt : eimsItem.getKeywordArray())
            {
                Keyword keyWord = eims.addNewKeyword();
                keyWord.setCode(new BigInteger(kwt.getCode()));
                keyWord.setLang(kwt.getLang());
                keyWord.setLabel(kwt.getStringValue());
            }
        }
        // E-6
        // add eims:price
        if (eimsItem.getPrice() != null)
        {
            eims.setPrice(new BigInteger(eimsItem.getPrice()));
        }
        // E-7
        // neither eims resource items nor eims.xsd contain subpriority_areas
        // E-8
        // add eims:pwb_entities
        if (eimsItem.sizeOfPwbEntitiesArray() > 0)
        {
            for (PwbEntitiesType pet : eimsItem.getPwbEntitiesArray())
            {
                PwbEntities pwbEntities = eims.addNewPwbEntities();
                // changed eims.xsd code type to xs:string
                // because of code="243P2" in eims item 49785
                pwbEntities.setCode(pet.getCode());
                pwbEntities.setLang(pet.getLang());
                pwbEntities.setLabel(pet.getStringValue());
            }
        }
        // E-9
        // add eims:program_name
        if (eimsItem.sizeOfProgramNameArray() > 0)
        {
            for (ProgramNameType pnt : eimsItem.getProgramNameArray())
            {
                ProgramName progName = eims.addNewProgramName();
                progName.setCode(new BigInteger(pnt.getCode()));
                progName.setLang(pnt.getLang());
                progName.setLabel(pnt.getStringValue());
            }
        }
        // E-10
        // add eims:notes
        if (eimsItem.getNotes() != null)
        {
            XmlString note = eims.addNewNotes();
            note.setStringValue(eimsItem.getNotes());
        }
        // E-11
        // add eims:remarks
        if (eimsItem.getRemarks() != null)
        {
            eims.setRemarks(eimsItem.getRemarks());
        }
        // E-12
        // add eims:stat_body
        if (eimsItem.sizeOfStatBodyArray() > 0)
        {
            for (StatBodyType sbt : eimsItem.getStatBodyArray())
            {
                StatBody statBody = eims.addNewStatBody();
                statBody.setCode(new BigInteger(sbt.getCode()));
                statBody.setLang(sbt.getLang());
                statBody.setLabel(sbt.getStringValue());
            }
        }
        // E-13
        // add eims:topic
        if (eimsItem.sizeOfTopicArray() > 0)
        {
            for (TopicType tt : eimsItem.getTopicArray())
            {
                Topic topic = eims.addNewTopic();
                topic.setCode(new BigInteger(tt.getCode()));
                topic.setLang(tt.getLang());
                topic.setLabel(tt.getStringValue());
            }
        }
        // E-14
        // add eims:suggested_position
        if (eimsItem.getSuggestedPosition() != null)
        {
            eims.setSuggestedPosition(eimsItem.getSuggestedPosition());
        }
        // E-15
        // add eims:context
        if (eimsItem.getContext() != null)
        {
            // context is missing in eims.xsd
            // added elemnt context to eims.xsd
            Context ctx = eims.addNewContext();
            ctx.setCode(new BigInteger(eimsItem.getContext().getCode()));
            ctx.setLang(eimsItem.getContext().getLang());
            ctx.setLabel(eimsItem.getContext().getStringValue());
        }
        // E-16
        // add eims:maintype
        if (eimsItem.getMaintype() != null)
        {
            Maintype mainType = eims.addNewMaintype();
            mainType.setCode(new BigInteger(eimsItem.getMaintype().getCode()));
            mainType.setLang(eimsItem.getMaintype().getLang());
            mainType.setMaintypeDescription(eimsItem.getMaintype().getStringValue());
        }
        // E-17
        // add eims:division
        if (eimsItem.getDivision() != null)
        {
            Division division = eims.addNewDivision();
            division.setCode(new BigInteger(eimsItem.getDivision().getCode()));
            division.setLang(eimsItem.getDivision().getLang());
            division.setLabel(eimsItem.getDivision().getStringValue());
        }
        if (faodocItem != null)
        {
            if (faodocItem.sizeOfDIVArray() > 0)
            {
                for (String div : faodocItem.getDIVArray())
                {
                    Division division = eims.addNewDivision();
                    division.setLabel(div);
                }
            }
        }
        // E-18
        // add eims:department
        if (eimsItem.getDepartment() != null)
        {
            Department department = eims.addNewDepartment();
            department.setCode(new BigInteger(eimsItem.getDepartment().getCode()));
            department.setLang(eimsItem.getDepartment().getLang());
            department.setLabel(eimsItem.getDepartment().getStringValue());
        }
        // E-19
        // add eims:service
        if (eimsItem.getService() != null)
        {
            Service service = eims.addNewService();
            service.setCode(new BigInteger(eimsItem.getService().getCode()));
            service.setLang(eimsItem.getService().getLang());
            service.setLabel(eimsItem.getService().getStringValue());
        }
        // E-20
        // add eims:DEP_DATE
        if (eimsItem.getDepDate() != null)
        {
            eims.addDEPDATE(string2cal(eimsItem.getDepDate()));
        }
        // E-21
        // add eims:WAICENT_DATE
        if (eimsItem.getWaicentDate() != null)
        {
            eims.addWAICENTDATE(string2cal(eimsItem.getWaicentDate()));
        }
        // E-22
        // add eims:priority_areas
        if (eimsItem.sizeOfPriorityAreasArray() > 0)
        {
            for (PriorityAreasType pat : eimsItem.getPriorityAreasArray())
            {
                PriorityAreas prioAreas = eims.addNewPriorityAreas();
                prioAreas.setCode(new BigInteger(pat.getCode()));
                prioAreas.setLang(pat.getLang());
                prioAreas.setLabel(pat.getStringValue());
            }
        }
        // E-23
        // add eims:waicent_published
        if (eimsItem.getWaicentPublished() != null)
        {
            eims.setWaicentPublished(new BigInteger(eimsItem.getWaicentPublished()));
        }
        // E-24
        // TODO: add eims:paia
        
        // E-25
        // TODO: add eims:creator
        
        // E-26
        // TODO: add eims:google_url
        
        return eimsDoc;
    }

    public Calendar string2cal(String dateString)
    {
        Calendar cal = Calendar.getInstance();
        Date date;
        try
        {
            if (dateString.length() > 10)
            {
                date = sdf1.parse(dateString);
            }
            else
            {
                date = sdf2.parse(dateString);
            }
            cal.setTime(date);
            return cal;
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
