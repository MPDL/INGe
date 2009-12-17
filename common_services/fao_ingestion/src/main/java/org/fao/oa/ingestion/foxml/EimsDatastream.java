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
import noNamespace.EimsDocument.Eims.Category;
import noNamespace.EimsDocument.Eims.Context;
import noNamespace.EimsDocument.Eims.Department;
import noNamespace.EimsDocument.Eims.Division;
import noNamespace.EimsDocument.Eims.Hardcopy;
import noNamespace.EimsDocument.Eims.Keyword;
import noNamespace.EimsDocument.Eims.Maintype;
import noNamespace.EimsDocument.Eims.PriorityAreas;
import noNamespace.EimsDocument.Eims.ProgramName;
import noNamespace.EimsDocument.Eims.PwbEntities;
import noNamespace.EimsDocument.Eims.Service;
import noNamespace.EimsDocument.Eims.StatBody;
import noNamespace.EimsDocument.Eims.Topic;

public class EimsDatastream
{
    Eims eims = null;
    SimpleDateFormat sdf1 = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss");
    SimpleDateFormat sdf2 = new SimpleDateFormat("dd/mm/yyyy");

    
    public EimsDocument create(ItemType eimsItem, ITEMType faodocItem)
    {
        EimsDocument eimsDoc = EimsDocument.Factory.newInstance();
        eims = eimsDoc.addNewEims();
        if (eimsItem.getIdentifier() != null)
        {
            eims.setIdentifier(new BigInteger(eimsItem.getIdentifier()));
        }
        if (eimsItem.getBibReference() != null)
        {
            eims.setBibReference(eimsItem.getBibReference());
        }
        
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
        if (eimsItem.getHardcopy() != null)
        {
            eims.setHardcopy(Hardcopy.Enum.forString(eimsItem.getHardcopy()));
        }
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
        if (eimsItem.getPrice() != null)
        {
            eims.setPrice(new BigInteger(eimsItem.getPrice()));
        }
        // TODO: subpriority_areas
        // neither eims resource items nor eims.xsd contain subpriority_areas
        if (eimsItem.sizeOfPwbEntitiesArray() > 0)
        {
            for (PwbEntitiesType pet : eimsItem.getPwbEntitiesArray())
            {
                PwbEntities pwbEntities = eims.addNewPwbEntities();
                // TODO: changed eims.xsd code type to xs:string
                // because of code="243P2" in eims item 49785
                pwbEntities.setCode(pet.getCode());
                pwbEntities.setLang(pet.getLang());
                pwbEntities.setLabel(pet.getStringValue());
            }
        }
        if (eimsItem.sizeOfProgramNameArray() > 0)
        {
            for (ProgramNameType pnt : eimsItem.getProgramNameArray())
            {
                ProgramName progName = eims.addNewProgramName();
                progName.setCode(new BigInteger(pnt.getCode()));
                progName.setLang(pnt.getLang());
                progName.setLable(pnt.getStringValue());
            }
        }
        // TODO: check if this should be an array
        if (eimsItem.getNotes() != null)
        {
            XmlString note = eims.addNewNotes();
            note.setStringValue(eimsItem.getNotes());
        }
        if (eimsItem.getRemarks() != null)
        {
            eims.setRemarks(eimsItem.getRemarks());
        }
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
        if (eimsItem.getSuggestedPosition() != null)
        {
            eims.setSuggestedPosition(eimsItem.getSuggestedPosition());
        }
        if (eimsItem.getContext() != null)
        {
            // TODO: context is missing in eims.xsd
            // added elemnt context to eims.xsd
            Context ctx = eims.addNewContext();
            ctx.setCode(new BigInteger(eimsItem.getContext().getCode()));
            ctx.setLang(eimsItem.getContext().getLang());
            ctx.setLabel(eimsItem.getContext().getStringValue());
        }
        if (eimsItem.getMaintype() != null)
        {
            Maintype mainType = eims.addNewMaintype();
            mainType.setCode(new BigInteger(eimsItem.getMaintype().getCode()));
            mainType.setLang(eimsItem.getMaintype().getLang());
            mainType.setMaintypeDescription(eimsItem.getMaintype().getStringValue());
        }
        if (eimsItem.getDivision() != null)
        {
            Division division = eims.addNewDivision();
            division.setCode(new BigInteger(eimsItem.getDivision().getCode()));
            division.setLang(eimsItem.getDivision().getLang());
            division.setLabel(eimsItem.getDivision().getStringValue());
        }
        if (faodocItem.sizeOfDIVArray() > 0)
        {
            for (String div : faodocItem.getDIVArray())
            {
                Division division = eims.addNewDivision();
                division.setLabel(div);
            }
        }
        if (eimsItem.getDepartment() != null)
        {
            Department department = eims.addNewDepartment();
            department.setCode(new BigInteger(eimsItem.getDepartment().getCode()));
            department.setLang(eimsItem.getDepartment().getLang());
            department.setLabel(eimsItem.getDepartment().getStringValue());
        }
        if (eimsItem.getService() != null)
        {
            Service service = eims.addNewService();
            service.setCode(new BigInteger(eimsItem.getService().getCode()));
            service.setLang(eimsItem.getService().getLang());
            service.setLabel(eimsItem.getService().getStringValue());
        }
        if (eimsItem.getDepDate() != null)
        {
            eims.setDEPDATE(string2cal(eimsItem.getDepDate()));
        }
        if (eimsItem.getWaicentDate() != null)
        {
            eims.setWAICENTDATE(string2cal(eimsItem.getWaicentDate()));
        }
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
        if (eimsItem.getWaicentPublished() != null)
        {
            eims.setWaicentPublished(new BigInteger(eimsItem.getWaicentPublished()));
        }
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
