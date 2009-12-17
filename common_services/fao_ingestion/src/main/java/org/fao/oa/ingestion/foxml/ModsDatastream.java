package org.fao.oa.ingestion.foxml;

import gov.loc.mods.v3.AbstractType;
import gov.loc.mods.v3.AccessConditionType;
import gov.loc.mods.v3.CodeOrText;
import gov.loc.mods.v3.CopyInformationType;
import gov.loc.mods.v3.DateOtherType;
import gov.loc.mods.v3.DateType;
import gov.loc.mods.v3.DetailType;
import gov.loc.mods.v3.GenreType;
import gov.loc.mods.v3.HoldingSimpleType;
import gov.loc.mods.v3.IdentifierType;
import gov.loc.mods.v3.LanguageType;
import gov.loc.mods.v3.LocationType;
import gov.loc.mods.v3.ModsDocument;
import gov.loc.mods.v3.ModsType;
import gov.loc.mods.v3.NameType;
import gov.loc.mods.v3.NameTypeAttribute;
import gov.loc.mods.v3.NoteType;
import gov.loc.mods.v3.OriginInfoType;
import gov.loc.mods.v3.PartType;
import gov.loc.mods.v3.PhysicalDescriptionType;
import gov.loc.mods.v3.PhysicalLocationType;
import gov.loc.mods.v3.PlaceTermType;
import gov.loc.mods.v3.PlaceType;
import gov.loc.mods.v3.RecordInfoType;
import gov.loc.mods.v3.RelatedItemType;
import gov.loc.mods.v3.StringPlusAuthorityPlusDisplayLabel;
import gov.loc.mods.v3.StringPlusAuthorityPlusType;
import gov.loc.mods.v3.TitleInfoType;
import gov.loc.mods.v3.UrlType;
import gov.loc.mods.v3.VersionType;
import gov.loc.mods.v3.LanguageType.LanguageTerm;
import gov.loc.mods.v3.LanguageType.LanguageTerm.Authority;
import gov.loc.mods.v3.RoleType.RoleTerm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import noNamespace.AUPERType;
import noNamespace.ITEMType;
import noNamespace.ItemType;
import noNamespace.LOCType;
import noNamespace.SubtitleType;
import noNamespace.TitleType;

import org.apache.xmlbeans.XmlString;
import org.fao.oa.ingestion.faodoc.ConferenceName;
import org.fao.oa.ingestion.faodoc.CorporateBody;
import org.fao.oa.ingestion.faodoc.JournalName;
import org.fao.oa.ingestion.faodoc.LanguageCodes;
import org.fao.oa.ingestion.faodoc.ProjectName;
import org.fao.oa.ingestion.faodoc.SeriesName;
import org.fao.oa.ingestion.utils.XBeanUtils;

public class ModsDatastream
{
    private ModsType modsType;
    private static final String FAO = "Food and Agriculture Organization of the United Nations";

    /**
     * creata a MODS datastream with merged values from FAODOC and EIMS_CDR.
     * 
     * @param eims {@link ItemType}
     * @param faodoc {@link ITEMType}
     * @return {@link ModsDocument}
     */
    public ModsDocument merge(ItemType eims, ITEMType faodoc)
    {
        ModsDocument modsDoc = ModsDocument.Factory.newInstance();
        modsType = modsDoc.addNewMods();
        modsType.setVersion(VersionType.X_3_3);
        // add mods:genre@type='class'
        if (faodoc.sizeOfBIBLEVELArray() > 0)
        {
            addNewGenreType(faodoc.getBIBLEVELArray(0), "class", null);
        }
        // add mods:name@type='personal'/namePart
        if (faodoc.sizeOfAUPERArray() > 0)
        {
            HashMap authorMap = new HashMap<String, String>();
            for (AUPERType auper : faodoc.getAUPERArray())
            {
                String name = "", aff = "";
                if (auper.getAUNAME() != null)
                {
                    name = auper.getAUNAME();
                }
                if (auper.getAUAFF() != null)
                {
                    aff = auper.getAUAFF();
                }
                authorMap.put(name, aff);
            }
            Iterator it = authorMap.entrySet().iterator();
            while (it.hasNext())
            {
                Entry e = (Entry)it.next();
                NameType nameType = modsType.addNewName();
                nameType.setType(NameTypeAttribute.PERSONAL);
                nameType.addNewNamePart().setStringValue(e.getKey().toString());
                RoleTerm roleTerm = nameType.addNewRole().addNewRoleTerm();
                roleTerm.setAuthority("marcrelator");
                roleTerm.setType(CodeOrText.TEXT);
                roleTerm.setStringValue(e.getValue().toString());
            }
        }
        else
        {
            if (eims.getAuthor() != null)
            {
                String authors = eims.getAuthor();
                String[] authorArray = authors.split(";");
                for (String author : authorArray)
                {
                    NameType nameType = modsType.addNewName();
                    nameType.setType(NameTypeAttribute.PERSONAL);
                    nameType.addNewNamePart().setStringValue(author);
                }
            }
        }
        // add mods:name@type='corporate'/namePart
        if (faodoc.sizeOfAUCORENArray() > 0)
        {
            for (int a = 0; a < faodoc.sizeOfAUCORENArray(); a++)
            {
                String au_cor_en = faodoc.getAUCORENArray(a);
                String[] labelAndHref = new CorporateBody().getEnglish(au_cor_en);
                if (labelAndHref != null)
                {
                    addNewCorporateNameType(labelAndHref[0], "en", labelAndHref[1]);
                }
            }
        }
        if (faodoc.sizeOfAUCORFRArray() > 0)
        {
            for (int a = 0; a < faodoc.sizeOfAUCORFRArray(); a++)
            {
                String au_cor_fr = faodoc.getAUCORFRArray(a);
                String[] labelAndHref = new CorporateBody().getFrench(au_cor_fr);
                if (labelAndHref != null)
                {
                    addNewCorporateNameType(labelAndHref[0], "fr", labelAndHref[1]);
                }
            }
        }
        if (faodoc.sizeOfAUCORESArray() > 0)
        {
            for (int a = 0; a < faodoc.sizeOfAUCORESArray(); a++)
            {
                String au_cor_es = faodoc.getAUCORESArray(a);
                String[] labelAndHref = new CorporateBody().getEnglish(au_cor_es);
                if (labelAndHref != null)
                {
                    addNewCorporateNameType(labelAndHref[0], "en", labelAndHref[1]);
                }
            }
        }
        if (faodoc.sizeOfAUCOROTArray() > 0)
        {
            for (int a = 0; a < faodoc.sizeOfAUCOROTArray(); a++)
            {
                String au_cor_ot = faodoc.getAUCOROTArray(a);
                String[] labelAndHref = new CorporateBody().getEnglish(au_cor_ot);
                if (labelAndHref != null)
                {
                    addNewCorporateNameType(labelAndHref[0], "en", labelAndHref[1]);
                }
            }
        }
        // add mods:name@type='conference'/namePart
        if (faodoc.sizeOfCONFENArray() > 0 || faodoc.sizeOfCONFFRArray() > 0 || faodoc.sizeOfCONFESArray() > 0
                || faodoc.sizeOfCONFOTArray() > 0)
        {
            if (faodoc.sizeOfCONFENArray() > 0)
            {
                for (int c = 0; c < faodoc.sizeOfCONFENArray(); c++)
                {
                    String name = faodoc.getCONFENArray(c);
                    String[] nameAndHref = new ConferenceName().getEnglish(faodoc, name);
                    if (nameAndHref != null)
                    {
                        addNewConferenceNameType(nameAndHref[0], "en", nameAndHref[1]);
                    }
                }
            }
            if (faodoc.sizeOfCONFFRArray() > 0)
            {
                for (int c = 0; c < faodoc.sizeOfCONFFRArray(); c++)
                {
                    String name = faodoc.getCONFFRArray(c);
                    String[] nameAndHref = new ConferenceName().getEnglish(faodoc, name);
                    if (nameAndHref != null)
                    {
                        addNewConferenceNameType(nameAndHref[0], "en", nameAndHref[1]);
                    }
                }
            }
            if (faodoc.sizeOfCONFESArray() > 0)
            {
                for (int c = 0; c < faodoc.sizeOfCONFESArray(); c++)
                {
                    String name = faodoc.getCONFESArray(c);
                    String[] nameAndHref = new ConferenceName().getEnglish(faodoc, name);
                    if (nameAndHref != null)
                    {
                        addNewConferenceNameType(nameAndHref[0], "en", nameAndHref[1]);
                    }
                }
            }
            if (faodoc.sizeOfCONFOTArray() > 0)
            {
                for (int c = 0; c < faodoc.sizeOfCONFOTArray(); c++)
                {
                    String name = faodoc.getCONFOTArray(c);
                    String[] nameAndHref = new ConferenceName().getEnglish(faodoc, name);
                    if (nameAndHref != null)
                    {
                        addNewConferenceNameType(nameAndHref[0], "en", nameAndHref[1]);
                    }
                }
            }
        }
        else
        {
            if (eims.getConference() != null)
            {
                NameType nameType = modsType.addNewName();
                nameType.setType(NameTypeAttribute.CONFERENCE);
                nameType.setAuthority("fao-aos-conference");
                String confname = eims.getConference().getConferenceName().getStringValue();
                nameType.addNewNamePart().setStringValue(confname);
            }
        }
        // add mods:titleInfo/title
        if (eims.sizeOfTitleArray() > 0)
        {
            for (int t = 0; t < eims.sizeOfTitleArray(); t++)
            {
                TitleType eimsTitle = eims.getTitleArray(t);
                String title = eimsTitle.getStringValue();
                String lang = eimsTitle.getLang();
                addNewTitleInfoType(title, lang);
            }
            if (faodoc.sizeOfTITENArray() > 0)
            {
                String tit_en = faodoc.getTITENArray(0);
                addNewTranslatedTitleInfoType(tit_en, "en");
            }
        }
        else
        {
            if (faodoc.sizeOfTITENArray() > 0)
            {
                String tit_en = faodoc.getTITENArray(0);
                addNewTitleInfoType(tit_en, "en");
            }
            if (faodoc.sizeOfTITFRArray() > 0)
            {
                String tit_fr = faodoc.getTITFRArray(0);
                addNewTitleInfoType(tit_fr, "fr");
            }
            if (faodoc.sizeOfTITESArray() > 0)
            {
                String tit_es = faodoc.getTITESArray(0);
                addNewTitleInfoType(tit_es, "es");
            }
        }
        // add mods:titleInfo@type='translated'/title
        if (faodoc.sizeOfTITTRArray() > 0)
        {
            for (int t = 0; t < faodoc.sizeOfTITTRArray(); t++)
            {
                String translated = faodoc.getTITTRArray(t);
                addNewTranslatedTitleInfoType(translated, "en");
            }
        }
        // add mods:titleInfo/subTitle
        if (eims.sizeOfSubtitleArray() > 0)
        {
            for (int s = 0; s < eims.sizeOfSubtitleArray(); s++)
            {
                SubtitleType subTitle = eims.getSubtitleArray(s);
                String sub = subTitle.getStringValue();
                String lang = subTitle.getLang();
                addNewSubTitleInfoType(sub, lang);
            }
        }
        // add mods:note@type='title'
        if (faodoc.sizeOfSUBTITENArray() > 0)
        {
            for (String s : faodoc.getSUBTITENArray())
            {
                addNewNoteType(s, "title", "en");
            }
        }
        if (faodoc.sizeOfSUBTITFRArray() > 0)
        {
            for (String s : faodoc.getSUBTITFRArray())
            {
                addNewNoteType(s, "title", "fr");
            }
        }
        if (faodoc.sizeOfSUBTITESArray() > 0)
        {
            for (String s : faodoc.getSUBTITESArray())
            {
                addNewNoteType(s, "title", "es");
            }
        }
        if (faodoc.sizeOfSUBTITOTArray() > 0)
        {
            for (String s : faodoc.getSUBTITOTArray())
            {
                addNewNoteType(s, "title", "ot");
            }
        }
        if (faodoc.sizeOfSUBTITTRArray() > 0)
        {
            for (String s : faodoc.getSUBTITTRArray())
            {
                addNewNoteType(s, "title", "tr");
            }
        }
        // add mods:originInfo/edition
        if (faodoc.sizeOfEDITIONArray() > 0)
        {
            OriginInfoType origin = modsType.addNewOriginInfo();
            XmlString edition = origin.addNewEdition();
            edition.setStringValue(faodoc.getEDITIONArray(0));
        }
        // add mods:originInfo/publisher
        if (faodoc.sizeOfPUBNAMEArray() > 0)
        {
            for (String p : faodoc.getPUBNAMEArray())
            {
                OriginInfoType origin = modsType.addNewOriginInfo();
                XmlString publisher = origin.addNewPublisher();
                publisher.setStringValue(p);
            }
        }
        else
        {
            if (eims.getPublisher() != null)
            {
                OriginInfoType origin = modsType.addNewOriginInfo();
                XmlString publisher = origin.addNewPublisher();
                publisher.setStringValue(eims.getPublisher().getPublisherName());
            }
        }
        // add mods:originInfo/placce/placeterm@type='text'
        if (faodoc.sizeOfPUBPLACEArray() > 0)
        {
            for (String pp : faodoc.getPUBPLACEArray())
            {
                OriginInfoType origin = modsType.addNewOriginInfo();
                PlaceType place = origin.addNewPlace();
                PlaceTermType placeTerm = place.addNewPlaceTerm();
                placeTerm.setType(CodeOrText.TEXT);
                placeTerm.setStringValue(pp);
            }
        }
        // add mods:originInfo/dateIssued
        {
            if (faodoc.sizeOfDATEISSUEArray() > 0 || faodoc.sizeOfPUBDATEArray() > 0 || faodoc.sizeOfPUBYEARArray() > 0)
            {
                if (faodoc.sizeOfDATEISSUEArray() > 0)
                {
                    String date = faodoc.getDATEISSUEArray(0);
                    addNewDateIssued(date);
                }
                else
                {
                    if (faodoc.sizeOfPUBDATEArray() > 0)
                    {
                        String date = faodoc.getPUBDATEArray(0);
                        addNewDateIssued(date);
                    }
                    else
                    {
                        if (faodoc.sizeOfPUBYEARArray() > 0)
                        {
                            String date = faodoc.getPUBYEARArray(0);
                            addNewDateIssued(date);
                        }
                    }
                }
            }
            else
            {
                if (eims.getDate() != null)
                {
                    String date = eims.getDate().getStringValue();
                    addNewDateIssued(date);
                }
            }
        }
        // add mods:originInfo/dateOther@type='year'
        if (faodoc.sizeOfYEARPUBLArray() > 0)
        {
            OriginInfoType origin = modsType.addNewOriginInfo();
            DateOtherType other = origin.addNewDateOther();
            other.setType("year");
            other.setStringValue(Short.valueOf(faodoc.getYEARPUBLArray(0)).toString());
        }
        // add mods:identifier@type='type'
        if (faodoc.sizeOfISBNArray() > 0)
        {
            addNewIdentifier(faodoc.getISBNArray(0), "isbn");
        }
        else
        {
            if (eims.getIsbn() != null)
            {
                addNewIdentifier(eims.getIsbn(), "isbn");
            }
        }
        // add mods:language/languageTerm@type='code'@authority
        if (faodoc.sizeOfLANGArray() > 0)
        {
            for (String lang : faodoc.getLANGArray())
            {
                String[] codes = new LanguageCodes().getIso639Codes(lang);
                if (codes != null)
                {
                    addNewLanguage(codes);
                }
                else
                {
                    if (eims.getLanguage() != null)
                    {
                        String[] codes2 = new LanguageCodes().getIso639Codes2(eims.getLanguage().toLowerCase());
                        if (codes2 != null)
                        {
                            addNewLanguage(codes2);
                        }
                    }
                }
            }
        }
        else
        {
            if (eims.getLanguage() != null)
            {
                String[] codes = new LanguageCodes().getIso639Codes2(eims.getLanguage().toLowerCase());
                if (codes != null)
                {
                    addNewLanguage(codes);
                }
            }
        }
        // add mods:identifier@type='type'
        if (faodoc.sizeOfRNArray() > 0)
        {
            addNewIdentifier(faodoc.getRNArray(0), "rn");
        }
        // add mods:identifier@type='type'
        if (eims.getJobno() != null)
        {
            addNewIdentifier(eims.getJobno(), "jn");
        }
        else
        {
            if (faodoc.sizeOfJNArray() > 0)
            {
                addNewIdentifier(faodoc.getJNArray(0), "jn");
            }
        }
        // add mods:relatedItem@type='originel'/titleInfo/title
        if (faodoc.sizeOfPNAMEArray() > 0)
        {
            for (String pname : faodoc.getPNAMEArray())
            {
                String[] values = new ProjectName().checkLabel(pname);
                if (values != null)
                {
                    addNewRelatedItem(values);
                }
            }
        }
        else
        {
            if (eims.getProject() != null)
            {
                String[] values = new ProjectName().checkLabel(eims.getProject().getProjectName());
                if (values != null)
                {
                    addNewRelatedItem(values);
                }
            }
        }
        // add mods:relatedItem@type='project'/identifier@type='faopn'
        if (faodoc.sizeOfPNUMBERArray() > 0)
        {
            for (String pnumber : faodoc.getPNUMBERArray())
            {
                addNewRelatedItemIdentifier(pnumber, "project", "faopn");
            }
        }
        else
        {
            if (eims.getProject() != null)
            {
                addNewRelatedItemIdentifier(eims.getProject().getProjectCode(), "project", "faopn");
            }
        }
        // add mods:relatedItem@type='project'/note@type='project'
        if (faodoc.sizeOfPDOCArray() > 0)
        {
            for (String pdoc : faodoc.getPDOCArray())
            {
                addNewRelatedItemNote(pdoc);
            }
        }
        // add mods:relatedItem@type='series'/titleInfo/title
        if (faodoc.sizeOfSERTITENArray() > 0 || faodoc.sizeOfSERTITFRArray() > 0 || faodoc.sizeOfSERTITESArray() > 0
                || faodoc.sizeOfSERTITOTArray() > 0 || faodoc.sizeOfSERTITArray() > 0)
        {
            if (faodoc.sizeOfSERTITENArray() > 0)
            {
                for (String s : faodoc.getSERTITENArray())
                {
                    String[] ser_vals = new SeriesName().getEnglish(s);
                    String[] jour_vals = new JournalName().get(s);
                    if (ser_vals != null)
                    {
                        addNewRelatedItemSeries(ser_vals);
                    }
                    if (jour_vals != null)
                    {
                        addNewRelatedItemHost(jour_vals);
                    }
                }
            }
            if (faodoc.sizeOfSERTITFRArray() > 0)
            {
                for (String s : faodoc.getSERTITFRArray())
                {
                    String[] ser_vals = new SeriesName().getFrench(s);
                    String[] jour_vals = new JournalName().get(s);
                    if (ser_vals != null)
                    {
                        addNewRelatedItemSeries(ser_vals);
                    }
                    if (jour_vals != null)
                    {
                        addNewRelatedItemHost(jour_vals);
                    }
                }
            }
            if (faodoc.sizeOfSERTITESArray() > 0)
            {
                for (String s : faodoc.getSERTITESArray())
                {
                    String[] ser_vals = new SeriesName().getSpanish(s);
                    String[] jour_vals = new JournalName().get(s);
                    if (ser_vals != null)
                    {
                        addNewRelatedItemSeries(ser_vals);
                    }
                    if (jour_vals != null)
                    {
                        addNewRelatedItemHost(jour_vals);
                    }
                }
            }
            if (faodoc.sizeOfSERTITOTArray() > 0)
            {
                for (String s : faodoc.getSERTITOTArray())
                {
                    String[] ser_vals = new SeriesName().getOther(s);
                    String[] jour_vals = new JournalName().get(s);
                    if (ser_vals != null)
                    {
                        addNewRelatedItemSeries(ser_vals);
                    }
                    if (jour_vals != null)
                    {
                        addNewRelatedItemHost(jour_vals);
                    }
                }
            }
            // TODO: add check for SER_TIT
            // is lang always en?
        }
        else
        {
            // assumption: lang is always en
            if (eims.getIspartofseries() != null)
            {
                String[] ser_vals = new SeriesName().getEnglish(eims.getIspartofseries());
                if (ser_vals != null)
                {
                    addNewRelatedItemSeries(ser_vals);
                }
            }
        }
        // add mods:relatedItem@type='series'/identifier@type='issn'
        if (faodoc.getBIBLEVELArray(0).equals("MS") || faodoc.getBIBLEVELArray(0).equals("AMS"))
        {
            if (faodoc.sizeOfISSNArray() > 0)
            {
                for (String issn : faodoc.getISSNArray())
                {
                    addNewRelatedItemIdentifier(issn, "series", "issn");
                }
            }
            else
            {
                if (eims.getIssn() != null)
                {
                    addNewRelatedItemIdentifier(eims.getIssn(), "series", "issn");
                }
            }
        }
        else
        {
            if (faodoc.getBIBLEVELArray(0).equals("AS"))
            {
                if (faodoc.sizeOfISSNArray() > 0)
                {
                    for (String issn : faodoc.getISSNArray())
                    {
                        addNewRelatedItemIdentifier(issn, "host", "issn");
                    }
                }
                else
                {
                    if (eims.getIssn() != null)
                    {
                        addNewRelatedItemIdentifier(eims.getIssn(), "host", "issn");
                    }
                }
            }
        }
        // add mods:relatedItem@type='host'/part/detail
        if (faodoc.sizeOfSERPAGESArray() > 0)
        {
            addNewRelatedItemPartDetail(faodoc.getSERPAGESArray(0));
        }
        // add mods:note@type='library_subject_code'
        if (faodoc.sizeOfSUBJLIBArray() > 0)
        {
            for (String subjLib : faodoc.getSUBJLIBArray())
            {
                addNewNoteType(subjLib, "library_subject_code", "en");
            }
        }
        // add mods:physicalDescription/extent
        if (faodoc.sizeOfPAGESArray() > 0 || faodoc.sizeOfSERHOLDArray() > 0 || eims.getPages() != null)
        {
            PhysicalDescriptionType desc = modsType.addNewPhysicalDescription();
            if (faodoc.sizeOfPAGESArray() > 0)
            {
                desc.addExtent(faodoc.getPAGESArray(0));
            }
            if (faodoc.sizeOfSERHOLDArray() > 0)
            {
                desc.addExtent(faodoc.getSERHOLDArray(0));
            }
            if (eims.getPages() != null)
            {
                desc.addExtent(eims.getPages());
            }
        }
        // add mods:physicalDescription/note
        if (faodoc.sizeOfCOLLINFOArray() > 0)
        {
            PhysicalDescriptionType desc = modsType.addNewPhysicalDescription();
            for (String cInfo : faodoc.getCOLLINFOArray())
            {
                NoteType note = desc.addNewNote();
                note.setStringValue(cInfo);
            }
        }
        // add mods:location/url@type='external url'
        if (faodoc.sizeOfURLArray() > 0)
        {
            LocationType loc = modsType.addNewLocation();
            for (String url : faodoc.getURLArray())
            {
                UrlType urlType = loc.addNewUrl();
                urlType.setNote("external url");
                urlType.setStringValue(url);
            }
        }
        // add mods:note@type='source note'
        if (faodoc.sizeOfSOURCEArray() > 0)
        {
            NoteType note = modsType.addNewNote();
            note.setType("source note");
            note.setStringValue(faodoc.getSOURCEArray(0));
        }
        // add mods:note
        if (faodoc.sizeOfNOTESArray() > 0)
        {
            for (String note : faodoc.getNOTESArray())
            {
                NoteType noteType = modsType.addNewNote();
                noteType.setStringValue(note);
            }
        }
        if (faodoc.sizeOfINSTArray() > 0)
        {
            for (String note : faodoc.getINSTArray())
            {
                NoteType noteType = modsType.addNewNote();
                noteType.setStringValue(note);
            }
        }
        if (eims.getNotes() != null)
        {
            NoteType noteType = modsType.addNewNote();
            noteType.setStringValue(eims.getNotes());
        }
        // add mods:abstract
        if (eims.sizeOfAbstractArray() > 0)
        {
            for (noNamespace.AbstractType abstType : eims.getAbstractArray())
            {
                AbstractType modsAbst = modsType.addNewAbstract();
                modsAbst.setLang2(abstType.getLang());
                modsAbst.setStringValue(abstType.getStringValue());
            }
        }
        else
        {
            if (faodoc.sizeOfABSTRArray() > 0)
            {
                for (String abstr : faodoc.getABSTRArray())
                {
                    AbstractType modsAbst = modsType.addNewAbstract();
                    // which language to set here?
                    modsAbst.setStringValue(abstr);
                }
            }
        }
        // add tons of mods:genre@type='type'
        if (faodoc.sizeOfLITINDICATORArray() > 0)
        {
            for (String litInd : faodoc.getLITINDICATORArray())
            {
                addNewGenreType(litInd, "type", "en");
            }
        }
        if (faodoc.sizeOfTYPEArray() > 0)
        {
            for (String type : faodoc.getTYPEArray())
            {
                addNewGenreType(type, "type", "en");
            }
        }
        if (faodoc.sizeOfCLASSCODEArray() > 0)
        {
            for (String cc : faodoc.getCLASSCODEArray())
            {
                if (cc.equalsIgnoreCase("Y") || cc.equalsIgnoreCase("Z"))
                {
                    AccessConditionType access = modsType.addNewAccessCondition();
                    access.setType("copyright");
                    access.setTitle(cc);
                }
                else
                {
                    if (cc.equalsIgnoreCase("W"))
                    {
                        addNewGenreType("U", "type", "en");
                    }
                    else
                    {
                        if (cc.equalsIgnoreCase("C"))
                        {
                            addNewGenreType("G", "type", "en");
                        }
                        else
                        {
                            addNewGenreType(cc, "type", "en");
                        }
                    }
                }
            }
        }
        if (faodoc.sizeOfCLASSCODEDCArray() > 0)
        {
            for (String ccdc : faodoc.getCLASSCODEDCArray())
            {
                if (ccdc.contains("FAO"))
                {
                    AccessConditionType access = modsType.addNewAccessCondition();
                    access.setType("copyright");
                    access.setTitle(ccdc);
                }
                else
                {
                    addNewGenreType(ccdc, "type", "en");
                }
            }
        }
        if (faodoc.sizeOfRECORDTYPEArray() > 0)
        {
            for (String rec : faodoc.getRECORDTYPEArray())
            {
                if (rec.equals("F"))
                {
                    addNewGenreType("L", "type", "en");
                }
                if (rec.equals("G"))
                {
                    addNewGenreType("Y", "type", "en");
                }
                if (rec.equals("J"))
                {
                    addNewGenreType("AS", "class", "en");
                }
            }
        }
        if (faodoc.sizeOfRECORDTYPEDCArray() > 0)
        {
            for (String recdc : faodoc.getRECORDTYPEDCArray())
            {
                if (recdc.equals("Monograph"))
                {
                    PhysicalDescriptionType desc = modsType.addNewPhysicalDescription();
                    StringPlusAuthorityPlusType form = desc.addNewForm();
                    form.setStringValue(recdc);
                }
                if (recdc.equals("Film"))
                {
                    addNewGenreType(recdc, "type", "en");
                }
                if (recdc.contains("Map"))
                {
                    addNewGenreType("Map(s)/Atlas", "type", "en");
                }
                if (recdc.contains("Article"))
                {
                    addNewGenreType("Analytic from a serial", "type", "en");
                }
            }
        }
        if (faodoc.getBIBLEVELArray(0).equals("AS"))
        {
            addNewGenreType("Journal article", "type", "en");
        }
        if (faodoc.getBIBLEVELArray(0).equals("AM") || faodoc.getBIBLEVELArray(0).equals("AMS"))
        {
            addNewGenreType("Analytic", "type", "en");
        }
        // TODO: check what to do with eims.fao
        
        // add new mods:location/physicalLocation
        LocationType fao_location = modsType.addNewLocation();
        PhysicalLocationType phys = fao_location.addNewPhysicalLocation();
        phys.setStringValue(FAO);
        if (faodoc.sizeOfLOCArray() > 0)
        {
            for (LOCType locType : faodoc.getLOCArray())
            {
                LocationType location = modsType.addNewLocation();
                HoldingSimpleType holding = location.addNewHoldingSimple();
                CopyInformationType copy = holding.addNewCopyInformation();
                copy.addSubLocation(locType.getLOCATION());
                copy.addShelfLocator(locType.getAVNUMBER());
            }
        }
        if (faodoc.getLOCALNUMBERArray(0) != null)
        {
            LocationType local = modsType.addNewLocation();
            HoldingSimpleType holding = local.addNewHoldingSimple();
            CopyInformationType copy = holding.addNewCopyInformation();
            copy.addShelfLocator(faodoc.getLOCALNUMBERArray(0));
        }
        if (faodoc.sizeOfMICROFICHEArray() > 0)
        {
            for (String mf : faodoc.getMICROFICHEArray())
            {
                LocationType microFiche = modsType.addNewLocation();
                HoldingSimpleType holding = microFiche.addNewHoldingSimple();
                CopyInformationType copy = holding.addNewCopyInformation();
                copy.addShelfLocator(mf);
            }
        }
        // add mods:location/url@note
        if (eims.getURL() != null)
        {
            LocationType location = modsType.addNewLocation();
            UrlType url = location.addNewUrl();
            url.setNote(eims.getURL().getNote());
            url.setStringValue(eims.getURL().getStringValue());
        }
        if (eims.getPDFURL() != null)
        {
            LocationType location = modsType.addNewLocation();
            UrlType url = location.addNewUrl();
            url.setNote(eims.getPDFURL().getNote());
            url.setStringValue(eims.getPDFURL().getStringValue());
        }
        if (eims.getZIPURL() != null)
        {
            LocationType location = modsType.addNewLocation();
            UrlType url = location.addNewUrl();
            url.setNote(eims.getZIPURL().getNote());
            url.setStringValue(eims.getZIPURL().getStringValue());
        }
        //  add mods:physicalDescription/form or genre@type='type'
        if (faodoc.sizeOfFORMDOCArray() > 0)
        {
            if (faodoc.getFORMDOCArray(0).equals("Audiocassette"))
            {
                PhysicalDescriptionType desc = modsType.addNewPhysicalDescription();
                desc.setLang2("en");
                StringPlusAuthorityPlusType form = desc.addNewForm();
                form.setStringValue(faodoc.getFORMDOCArray(0) + "/tape");
            }
            if (faodoc.getFORMDOCArray(0).equals("Videocassette"))
            {
                PhysicalDescriptionType desc = modsType.addNewPhysicalDescription();
                desc.setLang2("en");
                StringPlusAuthorityPlusType form = desc.addNewForm();
                form.setStringValue(faodoc.getFORMDOCArray(0) + "/tape");
            }
            if (faodoc.getFORMDOCArray(0).equals("Filmstrip"))
            {
                PhysicalDescriptionType desc = modsType.addNewPhysicalDescription();
                desc.setLang2("en");
                StringPlusAuthorityPlusType form = desc.addNewForm();
                form.setStringValue(faodoc.getFORMDOCArray(0) + "/reel");
            }
            if (faodoc.getFORMDOCArray(0).equals("Slides"))
            {
                PhysicalDescriptionType desc = modsType.addNewPhysicalDescription();
                desc.setLang2("en");
                StringPlusAuthorityPlusType form = desc.addNewForm();
                form.setStringValue(faodoc.getFORMDOCArray(0));
            }
            if (faodoc.getFORMDOCArray(0).equals("Bibliography"))
            {
                addNewGenreType(faodoc.getFORMDOCArray(0), "literary indicator", "en");
                addNewGenreType("Z", "type", "en");
            }
            if (faodoc.getFORMDOCArray(0).equals("Dictionary"))
            {
                addNewGenreType(faodoc.getFORMDOCArray(0), "literary indicator", "en");
                addNewGenreType("O", "type", "en");
            }
            if (faodoc.getFORMDOCArray(0).equals("Directory"))
            {
                addNewGenreType(faodoc.getFORMDOCArray(0), "literary indicator", "en");
                addNewGenreType("B", "type", "en");
            }
            if (faodoc.getFORMDOCArray(0).equals("Field Document"))
            {
                addNewGenreType(faodoc.getFORMDOCArray(0), "literary indicator", "en");
                addNewGenreType("X", "type", "en");
            }
            if (faodoc.getFORMDOCArray(0).equals("Handbook/Manual"))
            {
                addNewGenreType(faodoc.getFORMDOCArray(0), "literary indicator", "en");
                addNewGenreType("H", "type", "en");
            }
            if (faodoc.getFORMDOCArray(0).equals("Map(s)/Atlas"))
            {
                addNewGenreType(faodoc.getFORMDOCArray(0), "literary indicator", "en");
                addNewGenreType("Y", "type", "en");
            }
            if (faodoc.getFORMDOCArray(0).equals("Terminal Report"))
            {
                addNewGenreType(faodoc.getFORMDOCArray(0), "literary indicator", "en");
                addNewGenreType("R", "type", "en");
            }
            if (faodoc.getFORMDOCArray(0).equals("Thesaurus"))
            {
                addNewGenreType(faodoc.getFORMDOCArray(0), "literary indicator", "en");
                addNewGenreType("T", "type", "en");
            }
            if (eims.getGenre() != null)
            {
                if (eims.getGenre().getStringValue().equals("Annotated bibliography"))
                {
                    addNewGenreType("Bibliography", "literary indicator", "en");
                    addNewGenreType("Z", "type", "en");
                }
                if (eims.getGenre().getStringValue().equals("Book"))
                {
                    addNewGenreType("Publication", "literary indicator", "en");
                    addNewGenreType("P", "type", "en");
                }
                if (eims.getGenre().getStringValue().equals("Journal"))
                {
                    addNewGenreType(eims.getGenre().getStringValue(), "literary indicator", "en");
                    addNewGenreType("J", "type", "en");
                }
                if (eims.getGenre().getStringValue().equals("Meeting"))
                {
                    addNewGenreType(eims.getGenre().getStringValue(), "literary indicator", "en");
                    addNewGenreType("K", "type", "en");
                }
                if (eims.getGenre().getStringValue().equals("Other"))
                {
                    addNewGenreType("Information", "literary indicator", "en");
                    addNewGenreType("I", "type", "en");
                }
                if (eims.getGenre().getStringValue().equals("Project"))
                {
                    addNewGenreType("Field document", "literary indicator", "en");
                    addNewGenreType("X", "type", "en");
                }
                if (eims.getGenre().getStringValue().equals("Report"))
                {
                    addNewGenreType("Meeting", "literary indicator", "en");
                    addNewGenreType("K", "type", "en");
                }
            }
        }
        // add mods:recordInfo/record/creationDate
        if (faodoc.sizeOfCDArray() > 0)
        {
            RecordInfoType info = modsType.addNewRecordInfo();
            DateType date = info.addNewRecordCreationDate();
            date.setStringValue(Integer.valueOf(faodoc.getCDArray(0)).toString());
        }
        else
        {
            if (eims.getDateCreated() != null)
            {
                RecordInfoType info = modsType.addNewRecordInfo();
                DateType date = info.addNewRecordCreationDate();
                date.setStringValue(eims.getDateCreated());
            }
        }
        // TODO: check which date from EIMS
        // add mods:recordInfo/record/changedDate
        if (faodoc.sizeOfDUArray() > 0)
        {
            RecordInfoType info = modsType.addNewRecordInfo();
            DateType date = info.addNewRecordChangeDate();
            date.setStringValue(Integer.valueOf(faodoc.getDUArray(0)).toString());
        }
        // TODO: check which date from EIMS
        
        //System.out.println(modsDoc.xmlText(XBeanUtils.getModsOpts()));
        return modsDoc;
    }

    /**
     * add mods:genre@type='type'.
     * 
     * @param value {@link String}
     */
    public void addNewGenreType(String value, String type, String lang)
    {
        GenreType genre = modsType.addNewGenre();
        genre.setType(type);
        if (lang != null)
        {
            genre.setLang2(lang);
        }
        genre.setStringValue(value);
    }

    /**
     * add mods:name@type='corporate'/namePart.
     * 
     * @param value {@link String}
     * @param lang {@link String}
     * @param href {@link String}
     */
    public void addNewCorporateNameType(String value, String lang, String href)
    {
        NameType nameType = modsType.addNewName();
        nameType.setType(NameTypeAttribute.CORPORATE);
        nameType.setAuthority("fao-aos-corporatebody");
        nameType.setType2("simple");
        nameType.setHref(href);
        nameType.setLang2(lang);
        nameType.addNewNamePart().setStringValue(value);
    }

    /**
     * add mods:name@type='conference'/namePart.
     * 
     * @param value {@link String}
     * @param lang {@link String}
     * @param href {@link String}
     */
    public void addNewConferenceNameType(String value, String lang, String href)
    {
        NameType nameType = modsType.addNewName();
        nameType.setType(NameTypeAttribute.CONFERENCE);
        nameType.setAuthority("fao-aos-conference");
        nameType.setHref(href);
        nameType.setLang2(lang);
        nameType.addNewNamePart().setStringValue(value);
    }

    /**
     * add mods:titleInfo/title.
     * 
     * @param value {@link String}
     * @param lang {@link String}
     */
    public void addNewTitleInfoType(String value, String lang)
    {
        TitleInfoType titleInfo = modsType.addNewTitleInfo();
        titleInfo.setLang2(lang);
        XmlString title = titleInfo.addNewTitle();
        title.setStringValue(value);
    }

    /**
     * add mods:titleInfo@type='translated'/title.
     * 
     * @param value {@link String}
     * @param lang {@link String}
     */
    public void addNewTranslatedTitleInfoType(String value, String lang)
    {
        TitleInfoType titleInfo = modsType.addNewTitleInfo();
        titleInfo.setType2(TitleInfoType.Type.TRANSLATED);
        titleInfo.setLang2(lang);
        XmlString title = titleInfo.addNewTitle();
        title.setStringValue(value);
    }

    /**
     * add mods:titleInfo/subTitle.
     * 
     * @param value {@link String}
     * @param lang {@link String}
     */
    public void addNewSubTitleInfoType(String value, String lang)
    {
        TitleInfoType titleInfo = modsType.addNewTitleInfo();
        titleInfo.setLang2(lang);
        XmlString subTitle = titleInfo.addNewSubTitle();
        subTitle.setStringValue(value);
    }

    /**
     * add mods:note@type='title'.
     * 
     * @param value {@link String}
     * @param lang {@link String}
     */
    public void addNewNoteType(String value, String type, String lang)
    {
        NoteType note = modsType.addNewNote();
        note.setType(type);
        note.setLang2(lang);
        note.setStringValue(value);
    }

    /**
     * add mods:originInfo/dateIssued.
     * 
     * @param value {@link String}
     */
    public void addNewDateIssued(String value)
    {
        OriginInfoType origin = modsType.addNewOriginInfo();
        DateType issued = origin.addNewDateIssued();
        issued.setStringValue(value);
    }

    /**
     * add mods:identifier@type='type'.
     * 
     * @param value {@link String}
     * @param type {@link String}
     */
    public void addNewIdentifier(String value, String type)
    {
        IdentifierType id = modsType.addNewIdentifier();
        id.setType(type);
        id.setStringValue(value);
    }

    /**
     * add mods:language/languageTerm@type='code'@authority='auth'.
     * 
     * @param values {@link String[]}
     */
    public void addNewLanguage(String[] values)
    {
        LanguageType langType = modsType.addNewLanguage();
        LanguageTerm langTermCode2b = langType.addNewLanguageTerm();
        langTermCode2b.setType(CodeOrText.CODE);
        langTermCode2b.setAuthority(LanguageTerm.Authority.ISO_639_2_B);
        langTermCode2b.setStringValue(values[0]);
        LanguageTerm langTermCode3 = langType.addNewLanguageTerm();
        langTermCode3.setType(CodeOrText.CODE);
        langTermCode3.setAuthority(LanguageTerm.Authority.ISO_639_3);
        langTermCode3.setStringValue(values[1]);
        LanguageTerm langTermText = langType.addNewLanguageTerm();
        langTermText.setType(CodeOrText.TEXT);
        langTermText.setStringValue(values[2]);
    }

    /**
     * add mods:relatedItem@type='original'@lang='values[2]'@authority='fao-aos-project'/titleInfo/title.
     * 
     * @param values {@link String[]}
     */
    public void addNewRelatedItem(String[] values)
    {
        RelatedItemType related = modsType.addNewRelatedItem();
        related.setType(RelatedItemType.Type.ORIGINAL);
        TitleInfoType titleInfo = related.addNewTitleInfo();
        titleInfo.setLang2(values[2]);
        titleInfo.setAuthority("fao-aos-project");
        titleInfo.setHref(values[1]);
        XmlString title = titleInfo.addNewTitle();
        title.setStringValue(values[0]);
    }

    /**
     * add mods:relatedItem@type='project'/identifier@type='faopn'.
     * 
     * @param value {@link String}
     */
    public void addNewRelatedItemIdentifier(String value, String itemType, String idType)
    {
        RelatedItemType related = modsType.addNewRelatedItem();
        if (itemType.equalsIgnoreCase("series"))
        {
            related.setType(RelatedItemType.Type.SERIES);
        }
        else
        {
            if (itemType.equalsIgnoreCase("host"))
            {
                related.setType(RelatedItemType.Type.HOST);
            }
            else
            {
                related.setType2(itemType);
            }
        }
        IdentifierType id = related.addNewIdentifier();
        id.setType(idType);
        id.setStringValue(value);
    }

    /**
     * add mods:relatedItem@type='project'/note@type='project'.
     * 
     * @param value {@link String}
     */
    public void addNewRelatedItemNote(String value)
    {
        RelatedItemType related = modsType.addNewRelatedItem();
        related.setType2("project");
        NoteType note = related.addNewNote();
        note.setType("project");
        note.setStringValue(value);
    }

    /**
     * add mods:relatedItem@type='series'@lang='values[2]'@authority='fao-aos-series'/titleInfo/title.
     * 
     * @param values {@link String[]}
     */
    public void addNewRelatedItemSeries(String[] values)
    {
        RelatedItemType related = modsType.addNewRelatedItem();
        related.setType(RelatedItemType.Type.SERIES);
        TitleInfoType titleInfo = related.addNewTitleInfo();
        titleInfo.setLang2(values[2]);
        titleInfo.setAuthority("fao-aos-series");
        titleInfo.setHref(values[1]);
        XmlString title = titleInfo.addNewTitle();
        title.setStringValue(values[0]);
    }

    /**
     * add mods:relatedItem@type='host'@lang='values[2]'@authority='fao-aos-journal'/titleInfo/title.
     * 
     * @param values {@link String[]}
     */
    public void addNewRelatedItemHost(String[] values)
    {
        RelatedItemType related = modsType.addNewRelatedItem();
        related.setType(RelatedItemType.Type.HOST);
        TitleInfoType titleInfo = related.addNewTitleInfo();
        titleInfo.setLang2(values[2]);
        titleInfo.setAuthority("fao-aos-journal");
        titleInfo.setHref(values[1]);
        XmlString title = titleInfo.addNewTitle();
        title.setStringValue(values[0]);
    }

    /**
     * add mods:relatedItem@type='host'/part/detail.
     * 
     * @param value {@link String}
     */
    public void addNewRelatedItemPartDetail(String value)
    {
        RelatedItemType related = modsType.addNewRelatedItem();
        related.setType(RelatedItemType.Type.HOST);
        PartType part = related.addNewPart();
        DetailType detail = part.addNewDetail();
        detail.addNumber(value);
    }
}
