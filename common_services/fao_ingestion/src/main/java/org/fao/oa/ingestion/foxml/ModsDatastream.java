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
import gov.loc.mods.v3.StringPlusAuthority;
import gov.loc.mods.v3.StringPlusAuthorityPlusDisplayLabel;
import gov.loc.mods.v3.StringPlusAuthorityPlusType;
import gov.loc.mods.v3.TitleInfoType;
import gov.loc.mods.v3.UrlType;
import gov.loc.mods.v3.VersionType;
import gov.loc.mods.v3.LanguageType.LanguageTerm;
import gov.loc.mods.v3.LanguageType.LanguageTerm.Authority;
import gov.loc.mods.v3.RoleType.RoleTerm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import noNamespace.AUPERType;
import noNamespace.CONFERENCEType;
import noNamespace.ITEMType;
import noNamespace.ItemType;
import noNamespace.LOCType;
import noNamespace.SERIESType;
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
    private static final String FAO_MC = "FAO Master Collection";

    /**
     * creata MODS datastream with merged values from FAODOC and EIMS_CDR.
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
        // M-1
        // add mods:genre@type='class'
        if (faodoc.sizeOfBIBLEVELArray() > 0)
        {
            addNewGenreType(faodoc.getBIBLEVELArray(0), "class", null);
        }
        // M-2 + M-3
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
                if (e.getValue().toString().equalsIgnoreCase(""))
                {
                    roleTerm.setStringValue("author");
                }
                else
                {
                    roleTerm.setStringValue(e.getValue().toString());
                }
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
        // M-4 - M-7
        // add mods:name@type='corporate'/namePart
        if (faodoc.sizeOfAUCORENArray() > 0)
        {
            for (int a = 0; a < faodoc.sizeOfAUCORENArray(); a++)
            {
                String au_cor_en = faodoc.getAUCORENArray(a);
                String[] labelHrefDescription = new CorporateBody().getEnglish(au_cor_en);
                if (labelHrefDescription != null)
                {
                    addNewCorporateNameType(labelHrefDescription[0], "en", labelHrefDescription[1],
                            labelHrefDescription[2]);
                }
            }
        }
        if (faodoc.sizeOfAUCORFRArray() > 0)
        {
            for (int a = 0; a < faodoc.sizeOfAUCORFRArray(); a++)
            {
                String au_cor_fr = faodoc.getAUCORFRArray(a);
                String[] labelHrefDescription = new CorporateBody().getFrench(au_cor_fr);
                if (labelHrefDescription != null)
                {
                    addNewCorporateNameType(labelHrefDescription[0], "fr", labelHrefDescription[1],
                            labelHrefDescription[2]);
                }
            }
        }
        if (faodoc.sizeOfAUCORESArray() > 0)
        {
            for (int a = 0; a < faodoc.sizeOfAUCORESArray(); a++)
            {
                String au_cor_es = faodoc.getAUCORESArray(a);
                String[] labelHrefDescription = new CorporateBody().getSpanish(au_cor_es);
                if (labelHrefDescription != null)
                {
                    addNewCorporateNameType(labelHrefDescription[0], "es", labelHrefDescription[1],
                            labelHrefDescription[2]);
                }
            }
        }
        if (faodoc.sizeOfAUCOROTArray() > 0)
        {
            for (int a = 0; a < faodoc.sizeOfAUCOROTArray(); a++)
            {
                String au_cor_ot = faodoc.getAUCOROTArray(a);
                String[] labelHrefDescription = new CorporateBody().getOther(au_cor_ot);
                if (labelHrefDescription != null)
                {
                    addNewCorporateNameType(labelHrefDescription[0], "en", labelHrefDescription[1],
                            labelHrefDescription[2]);
                }
            }
        }
        // M-8 - M-15
        // add mods:name@type='conference'/namePart
        if (faodoc.sizeOfCONFERENCEArray() > 0)
        {
            for (CONFERENCEType conference : faodoc.getCONFERENCEArray())
            {
                if (conference.isSetCONFEN())
                {
                    String name = conference.getCONFEN();
                    String[] nameAndHref = new ConferenceName().getEnglish(conference, name);
                    if (nameAndHref != null)
                    {
                        addNewConferenceNameType(nameAndHref[0], "en", nameAndHref[1]);
                    }
                }
                if (conference.isSetCONFFR())
                {
                    String name = conference.getCONFFR();
                    String[] nameAndHref = new ConferenceName().getFrench(conference, name);
                    if (nameAndHref != null)
                    {
                        addNewConferenceNameType(nameAndHref[0], "fr", nameAndHref[1]);
                    }
                }
                if (conference.isSetCONFES())
                {
                    String name = conference.getCONFES();
                    String[] nameAndHref = new ConferenceName().getSpanish(conference, name);
                    if (nameAndHref != null)
                    {
                        addNewConferenceNameType(nameAndHref[0], "es", nameAndHref[1]);
                    }
                }
                if (conference.isSetCONFOT())
                {
                    String name = conference.getCONFOT();
                    String[] nameAndHref = new ConferenceName().getOther(conference, name);
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
        // M-16 - M-22
        // add mods:titleInfo/title
        // TODO: check rules for TIT_OT and title lang=ot
        if (eims.sizeOfTitleArray() > 0)
        {
            for (int t = 0; t < eims.sizeOfTitleArray(); t++)
            {
                TitleType eimsTitle = eims.getTitleArray(t);
                String title = eimsTitle.getStringValue();
                String lang = eimsTitle.getLang();
                addNewTitleInfoType(title, lang);
                if (faodoc.sizeOfTITENArray() > 0 && !lang.equalsIgnoreCase("en"))
                {
                    if (checkLanguages(faodoc.getLANGArray()))
                    {
                        String tit_en = faodoc.getTITENArray(0);
                        addNewTranslatedTitleInfoType(tit_en, "en");
                    }
                }
                if (faodoc.sizeOfTITOTArray() > 0 && lang.equalsIgnoreCase("ot"))
                {
                    if (checkLangIsRussian(faodoc.getLANGArray()))
                    {
                        String tit_ot = faodoc.getTITOTArray(0);
                        addNewTransliterationTitleInfoType(tit_ot, "ru");
                    }
                }
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
            if (faodoc.sizeOfTITOTArray() > 0)
            {
                String tit_ot = faodoc.getTITOTArray(0);
                addNewTitleInfoType(tit_ot, "ot");
            }
        }
        // M-23
        // add mods:titleInfo@type='translated'/title
        if (faodoc.sizeOfTITTRArray() > 0)
        {
            for (int t = 0; t < faodoc.sizeOfTITTRArray(); t++)
            {
                String translated = faodoc.getTITTRArray(t);
                addNewTranslatedTitleInfoType(translated, "en");
            }
        }
        // M-24 - M-29
        // add mods:titleInfo/subTitle
        if (eims.sizeOfSubtitleArray() > 0)
        {
            for (int s = 0; s < eims.sizeOfSubtitleArray(); s++)
            {
                SubtitleType subTitle = eims.getSubtitleArray(s);
                String sub = subTitle.getStringValue();
                String lang = subTitle.getLang();
                // addNewSubTitleInfoType(sub, lang);
                addSubTitle2ExistingTitleInfoType(sub, lang);
            }
        }
        // M-30 - M-34
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
        // M-35
        // add mods:originInfo/edition
        if (faodoc.sizeOfEDITIONArray() > 0)
        {
            OriginInfoType origin = null;
            if (modsType.sizeOfOriginInfoArray() == 1)
            {
                origin = modsType.getOriginInfoArray(0);
            }
            else
            {
                origin = modsType.addNewOriginInfo();
            }
            XmlString edition = origin.addNewEdition();
            edition.setStringValue(faodoc.getEDITIONArray(0));
        }
        // M-36
        // add mods:originInfo/publisher
        if (faodoc.sizeOfPUBNAMEArray() > 0)
        {
            OriginInfoType origin = null;
            if (modsType.sizeOfOriginInfoArray() == 1)
            {
                origin = modsType.getOriginInfoArray(0);
            }
            else
            {
                origin = modsType.addNewOriginInfo();
            }
            for (String p : faodoc.getPUBNAMEArray())
            {
                XmlString publisher = origin.addNewPublisher();
                publisher.setStringValue(p);
            }
        }
        else
        {
            if (eims.getPublisher() != null)
            {
                OriginInfoType origin = null;
                if (modsType.sizeOfOriginInfoArray() == 1)
                {
                    origin = modsType.getOriginInfoArray(0);
                }
                else
                {
                    origin = modsType.addNewOriginInfo();
                }
                XmlString publisher = origin.addNewPublisher();
                publisher.setStringValue(eims.getPublisher().getPublisherName());
            }
        }
        // M-37
        // add mods:originInfo/placce/placeterm@type='text'
        if (faodoc.sizeOfPUBPLACEArray() > 0)
        {
            OriginInfoType origin = null;
            if (modsType.sizeOfOriginInfoArray() == 1)
            {
                origin = modsType.getOriginInfoArray(0);
            }
            else
            {
                origin = modsType.addNewOriginInfo();
            }
            for (String pp : faodoc.getPUBPLACEArray())
            {
                PlaceType place = origin.addNewPlace();
                PlaceTermType placeTerm = place.addNewPlaceTerm();
                placeTerm.setType(CodeOrText.TEXT);
                placeTerm.setStringValue(pp);
            }
        }
        // M-38 - M-40
        // add mods:originInfo/dateIssued
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
        // M-41
        // add mods:originInfo/dateOther@type='year'
        if (faodoc.sizeOfYEARPUBLArray() > 0)
        {
            OriginInfoType origin = null;
            if (modsType.sizeOfOriginInfoArray() == 1)
            {
                origin = modsType.getOriginInfoArray(0);
            }
            else
            {
                origin = modsType.addNewOriginInfo();
            }
            DateOtherType other = origin.addNewDateOther();
            other.setType("year");
            other.setStringValue(faodoc.getYEARPUBLArray(0));
        }
        // M-42
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
        // M-43
        // add mods:language/languageTerm@type='code'@authority
        if (faodoc.sizeOfLANGArray() > 0)
        {
            for (String lang : faodoc.getLANGArray())
            {
                if (lang.equalsIgnoreCase("spanish"))
                {
                    String[] codes = new LanguageCodes().getIso639Codes("Spanish; Castilian");
                    if (codes != null)
                    {
                        addNewLanguage(codes);
                    }
                }
                else
                {
                    String[] codes = new LanguageCodes().getIso639Codes(lang);
                    if (codes != null)
                    {
                        addNewLanguage(codes);
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
        // M-44
        // add mods:identifier@type='type'
        if (faodoc.sizeOfRNArray() > 0)
        {
            addNewIdentifier(faodoc.getRNArray(0), "rn");
        }
        // M-45
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
        // M-46
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
        // M-47
        // add mods:relatedItem@type='project'/identifier@type='faopn'
        if (faodoc.sizeOfPNUMBERArray() > 0)
        {
            for (String pnumber : faodoc.getPNUMBERArray())
            {
                // addNewRelatedItemIdentifier(pnumber, "project", "faopn");
                addIdentifier2existingRelatedItem(pnumber, "original", "faopn");
            }
        }
        else
        {
            if (eims.getProject() != null)
            {
                // addNewRelatedItemIdentifier(eims.getProject().getProjectCode(), "project", "faopn");
                addIdentifier2existingRelatedItem(eims.getProject().getProjectCode(), "original", "faopn");
            }
        }
        // M-48
        // add mods:relatedItem@type='project'/note@type='project'
        if (faodoc.sizeOfPDOCArray() > 0)
        {
            for (String pdoc : faodoc.getPDOCArray())
            {
                addNote2existingRelatedItem(pdoc, "original", "project");
            }
        }
        // M-49 - M-52 + M-54 - M-58
        // add mods:relatedItem@type='series'/titleInfo/title
        if (faodoc.getBIBLEVELArray(0).equalsIgnoreCase("MS") || faodoc.getBIBLEVELArray(0).equalsIgnoreCase("AMS"))
        {
            ArrayList<String[]> seriesTitles = null;
            if (faodoc.sizeOfSERIESArray() > 0)
            {
                for (SERIESType series : faodoc.getSERIESArray())
                {
                    seriesTitles = new ArrayList<String[]>();
                    String issn = null;
                    if (series.isSetSERTITEN())
                    {
                        String[] ser_vals = new SeriesName().getEnglish(series.getSERTITEN());
                        if (ser_vals != null)
                        {
                            seriesTitles.add(ser_vals);
                        }
                    } // end if
                    if (series.isSetSERTITFR())
                    {
                        String[] ser_vals = new SeriesName().getFrench(series.getSERTITFR());
                        if (ser_vals != null)
                        {
                            seriesTitles.add(ser_vals);
                        }
                    } // end if
                    if (series.isSetSERTITES())
                    {
                        String[] ser_vals = new SeriesName().getSpanish(series.getSERTITES());
                        if (ser_vals != null)
                        {
                            seriesTitles.add(ser_vals);
                        }
                    } // end if
                    if (series.isSetSERTITOT())
                    {
                        String[] ser_vals = new SeriesName().getOther(series.getSERTITOT());
                        if (ser_vals != null)
                        {
                            seriesTitles.add(ser_vals);
                        }
                    } // end if
                    // M-53 + M-59
                    // add mods:relatedItem@type='series'/identifier@type='issn'
                    if (series.isSetISSN())
                    {
                        issn = series.getISSN();
                    }
                    else
                    {
                        if (eims.getIssn() != null)
                        {
                            issn = eims.getIssn();
                        }
                    }
                    addNewRelatedItemSeries(seriesTitles, issn);
                } // end for
                // TODO: add check for SER_TIT
                // is lang always en?
            } // end if
            else
            {
                // assumption: lang is always en
                String issn = null;
                if (eims.getIspartofseries() != null)
                {
                    String[] ser_vals = new SeriesName().getEnglish(eims.getIspartofseries());
                    if (ser_vals != null)
                    {
                        seriesTitles = new ArrayList<String[]>();
                        seriesTitles.add(ser_vals);
                    }
                }
                if (eims.getIssn() != null)
                {
                    issn = eims.getIssn();
                }
                addNewRelatedItemSeries(seriesTitles, issn);
            }
        } // end if biblevel = MS or AS
        else
        {
            if (faodoc.getBIBLEVELArray(0).equalsIgnoreCase("AS"))
            {
                if (faodoc.sizeOfSERIESArray() > 0)
                {
                    for (SERIESType series : faodoc.getSERIESArray())
                    {
                        ArrayList<String[]> journalTitles = new ArrayList<String[]>();
                        String issn = null;
                        String pages = null;
                        if (series.isSetSERTITEN())
                        {
                            String[] jour_vals = new JournalName().get(series.getSERTITEN());
                            if (jour_vals != null)
                            {
                                journalTitles.add(jour_vals);
                            }
                        }
                        if (series.isSetSERTITFR())
                        {
                            String[] jour_vals = new JournalName().get(series.getSERTITFR());
                            if (jour_vals != null)
                            {
                                journalTitles.add(jour_vals);
                            }
                        }
                        if (series.isSetSERTITES())
                        {
                            String[] jour_vals = new JournalName().get(series.getSERTITES());
                            if (jour_vals != null)
                            {
                                journalTitles.add(jour_vals);
                            }
                        }
                        if (series.isSetSERTITOT())
                        {
                            String[] jour_vals = new JournalName().get(series.getSERTITOT());
                            if (jour_vals != null)
                            {
                                journalTitles.add(jour_vals);
                            }
                        }
                        // M-53 + M-59
                        // add mods:relatedItem@type='series'/identifier@type='issn'
                        if (series.isSetISSN())
                        {
                            issn = series.getISSN();
                        }
                        else
                        {
                            if (eims.getIssn() != null)
                            {
                                issn = eims.getIssn();
                            }
                        }
                        // M-60
                        // add mods:relatedItem@type='host'/part/detail
                        if (series.isSetSERPAGES())
                        {
                            pages = series.getSERPAGES();
                        }
                        addNewRelatedItemHost(journalTitles, issn, pages);
                    } // end for
                }
            }
        } // end else
        // M-61
        // add mods:note@type='library_subject_code'
        if (faodoc.sizeOfSUBJLIBArray() > 0)
        {
            for (String subjLib : faodoc.getSUBJLIBArray())
            {
                addNewNoteType(subjLib, "library_subject_code", "en");
            }
        }
        // M-62 - M-63
        // add mods:physicalDescription/extent
        if (faodoc.sizeOfPAGESArray() > 0 || faodoc.sizeOfSERIESArray() > 0 || eims.getPages() != null)
        {
            PhysicalDescriptionType desc = null;
            if (modsType.sizeOfPhysicalDescriptionArray() == 1)
            {
                desc = modsType.getPhysicalDescriptionArray(0);
            }
            else
            {
                desc = modsType.addNewPhysicalDescription();
            }
            if (eims.getPages() != null)
            {
                desc.addExtent(eims.getPages());
            }
            else
            {
                if (faodoc.sizeOfPAGESArray() > 0)
                {
                    desc.addExtent(faodoc.getPAGESArray(0));
                }
                for (SERIESType series : faodoc.getSERIESArray())
                {
                    if (series.isSetSERHOLD())
                    {
                        desc.addExtent(series.getSERHOLD());
                    }
                }
            }
        }
        // M-64
        // add mods:physicalDescription/note
        if (faodoc.sizeOfCOLLINFOArray() > 0)
        {
            PhysicalDescriptionType desc = null;
            if (modsType.sizeOfPhysicalDescriptionArray() == 1)
            {
                desc = modsType.getPhysicalDescriptionArray(0);
            }
            else
            {
                desc = modsType.addNewPhysicalDescription();
            }
            for (String cInfo : faodoc.getCOLLINFOArray())
            {
                NoteType note = desc.addNewNote();
                note.setStringValue(cInfo);
            }
        }
        // M-65
        // add mods:location/url@type='external url'
        if (faodoc.sizeOfURLArray() > 0)
        {
            LocationType loc = null;
            if (modsType.sizeOfLocationArray() == 1)
            {
                loc = modsType.getLocationArray(0);
            }
            else
            {
                loc = modsType.addNewLocation();
            }
            for (String url : faodoc.getURLArray())
            {
                UrlType urlType = loc.addNewUrl();
                urlType.setNote("external url");
                urlType.setStringValue(url);
            }
        }
        // M-66
        // add mods:note@type='source note'
        if (faodoc.sizeOfSOURCEArray() > 0)
        {
            if (!faodoc.getSOURCEArray(0).equalsIgnoreCase(""))
            {
                NoteType note = modsType.addNewNote();
                note.setType("source note");
                note.setStringValue(faodoc.getSOURCEArray(0));
            }
        }
        // M-67 -M-68
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
        // according to IS only taken from FAODOC
        /*
         * if (eims.getNotes() != null) { NoteType noteType = modsType.addNewNote();
         * noteType.setStringValue(eims.getNotes()); }
         */
        // M-69 - M-75
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
        // M-76 - M-82
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
                    PhysicalDescriptionType desc = null;
                    if (modsType.sizeOfPhysicalDescriptionArray() == 1)
                    {
                        desc = modsType.getPhysicalDescriptionArray(0);
                    }
                    else
                    {
                        desc = modsType.addNewPhysicalDescription();
                    }
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
        // M-83 - M-87
        // add new mods:location/physicalLocation
        LocationType fao_location = null;
        if (modsType.sizeOfLocationArray() == 1)
        {
            fao_location = modsType.getLocationArray(0);
        }
        else
        {
            fao_location = modsType.addNewLocation();
        }
        PhysicalLocationType phys = fao_location.addNewPhysicalLocation();
        phys.setStringValue(FAO);
        HoldingSimpleType holding = fao_location.addNewHoldingSimple();
        if (faodoc.sizeOfLOCArray() > 0)
        {
            for (LOCType locType : faodoc.getLOCArray())
            {
                if (!locType.getLOCATION().equalsIgnoreCase("FAO"))
                {
                    // LocationType location = modsType.addNewLocation();
                    CopyInformationType copy = holding.addNewCopyInformation();
                    StringPlusAuthority form = copy.addNewForm();
                    form.setAuthority("marcform");
                    form.setStringValue("print");
                    copy.addSubLocation(locType.getLOCATION());
                    copy.addShelfLocator(locType.getAVNUMBER());
                }
            }
        }
        if (faodoc.getLOCALNUMBERArray(0) != null)
        {
            // LocationType location = modsType.addNewLocation();
            // HoldingSimpleType holding = fao_location.addNewHoldingSimple();
            CopyInformationType copy = holding.addNewCopyInformation();
            StringPlusAuthority form = copy.addNewForm();
            form.setAuthority("marcform");
            form.setStringValue("print");
            copy.addSubLocation(FAO_MC);
            copy.addShelfLocator(faodoc.getLOCALNUMBERArray(0));
            addNewIdentifier(faodoc.getLOCALNUMBERArray(0), "AccessionNumber");
        }
        if (faodoc.sizeOfMICROFICHEArray() > 0)
        {
            for (String mf : faodoc.getMICROFICHEArray())
            {
                // LocationType microFiche = modsType.addNewLocation();
                // HoldingSimpleType holding = fao_location.addNewHoldingSimple();
                CopyInformationType copy = holding.addNewCopyInformation();
                StringPlusAuthority form = copy.addNewForm();
                form.setAuthority("marcform");
                form.setStringValue("microfiche");
                copy.addShelfLocator(mf);
            }
        }
        // M-88 - M-90
        // add mods:location/url@note
        if (eims.getURL() != null || eims.getPDFURL() != null || eims.getZIPURL() != null)
        {
            LocationType location = null;
            if (modsType.sizeOfLocationArray() == 1)
            {
                location = modsType.getLocationArray(0);
            }
            else
            {
                location = modsType.addNewLocation();
            }
            if (eims.getURL() != null)
            {
                UrlType url = location.addNewUrl();
                url.setNote(eims.getURL().getNote());
                url.setStringValue(eims.getURL().getStringValue());
            }
            if (eims.getPDFURL() != null)
            {
                UrlType url = location.addNewUrl();
                url.setNote(eims.getPDFURL().getNote());
                url.setStringValue(eims.getPDFURL().getStringValue());
            }
            if (eims.getZIPURL() != null)
            {
                UrlType url = location.addNewUrl();
                url.setNote(eims.getZIPURL().getNote());
                url.setStringValue(eims.getZIPURL().getStringValue());
            }
        }
        // M-91
        // add mods:physicalDescription/form or genre@type='type'
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
                addNewGenreType(faodoc.getFORMDOCArray(0), "type", "en");
                addNewGenreType("Z", "type", "en");
            }
            if (faodoc.getFORMDOCArray(0).equals("Dictionary"))
            {
                addNewGenreType(faodoc.getFORMDOCArray(0), "type", "en");
                addNewGenreType("O", "type", "en");
            }
            if (faodoc.getFORMDOCArray(0).equals("Directory"))
            {
                addNewGenreType(faodoc.getFORMDOCArray(0), "type", "en");
                addNewGenreType("B", "type", "en");
            }
            if (faodoc.getFORMDOCArray(0).equals("Field Document"))
            {
                addNewGenreType(faodoc.getFORMDOCArray(0), "type", "en");
                addNewGenreType("X", "type", "en");
            }
            if (faodoc.getFORMDOCArray(0).equals("Handbook/Manual"))
            {
                addNewGenreType(faodoc.getFORMDOCArray(0), "type", "en");
                addNewGenreType("H", "type", "en");
            }
            if (faodoc.getFORMDOCArray(0).equals("Map(s)/Atlas"))
            {
                addNewGenreType(faodoc.getFORMDOCArray(0), "type", "en");
                addNewGenreType("Y", "type", "en");
            }
            if (faodoc.getFORMDOCArray(0).equals("Terminal Report"))
            {
                addNewGenreType(faodoc.getFORMDOCArray(0), "type", "en");
                addNewGenreType("R", "type", "en");
            }
            if (faodoc.getFORMDOCArray(0).equals("Thesaurus"))
            {
                addNewGenreType(faodoc.getFORMDOCArray(0), "type", "en");
                addNewGenreType("T", "type", "en");
            }
            if (eims.getGenre() != null)
            {
                if (eims.getGenre().getStringValue().equals("Annotated bibliography"))
                {
                    addNewGenreType("Bibliography", "type", "en");
                    addNewGenreType("Z", "type", "en");
                }
                if (eims.getGenre().getStringValue().equals("Book"))
                {
                    addNewGenreType("Publication", "type", "en");
                    addNewGenreType("P", "type", "en");
                }
                if (eims.getGenre().getStringValue().equals("Journal"))
                {
                    addNewGenreType(eims.getGenre().getStringValue(), "type", "en");
                    addNewGenreType("J", "type", "en");
                }
                if (eims.getGenre().getStringValue().equals("Meeting"))
                {
                    addNewGenreType(eims.getGenre().getStringValue(), "type", "en");
                    addNewGenreType("K", "type", "en");
                }
                if (eims.getGenre().getStringValue().equals("Other"))
                {
                    addNewGenreType("Information", "type", "en");
                    addNewGenreType("I", "type", "en");
                }
                if (eims.getGenre().getStringValue().equals("Project"))
                {
                    addNewGenreType("Field document", "type", "en");
                    addNewGenreType("X", "type", "en");
                }
                if (eims.getGenre().getStringValue().equals("Report"))
                {
                    addNewGenreType("Meeting", "type", "en");
                    addNewGenreType("K", "type", "en");
                }
            }
        }
        // M-92
        // add mods:recordInfo/record/creationDate
        if (faodoc.sizeOfCDArray() > 0)
        {
            RecordInfoType rit = null;
            if (modsType.sizeOfRecordInfoArray() == 1)
            {
                rit = modsType.getRecordInfoArray(0);
            }
            else
            {
                rit = modsType.addNewRecordInfo();
            }
            DateType dateCD = rit.addNewRecordCreationDate();
            dateCD.setStringValue(Integer.valueOf(faodoc.getCDArray(0)).toString());
            // M-93
            // add mods:recordInfo/record/changedDate
            if (faodoc.sizeOfDUArray() > 0)
            {
                DateType dateDU = rit.addNewRecordChangeDate();
                dateDU.setStringValue(Integer.valueOf(faodoc.getDUArray(0)).toString());
            }
        }
        else
        {
            if (eims.getDateCreated() != null)
            {
                RecordInfoType rit = null;
                if (modsType.sizeOfRecordInfoArray() == 1)
                {
                    rit = modsType.getRecordInfoArray(0);
                }
                else
                {
                    rit = modsType.addNewRecordInfo();
                }
                DateType date = rit.addNewRecordCreationDate();
                date.setStringValue(eims.getDateCreated());
            }
        }
        // E-17 no longer in EIMS datastream
        // create mods:note@type='division' instead
        if (eims.getDivision() != null)
        {
            addNewNoteType(eims.getDivision().getStringValue(), "division", eims.getDivision().getLang());
            if (faodoc.sizeOfDIVArray() > 0)
            {
                for (String div : faodoc.getDIVArray())
                {
                    if (!div.equalsIgnoreCase(eims.getDivision().getStringValue()))
                    {
                        NoteType note = modsType.addNewNote();
                        note.setType("division");
                        note.setStringValue(div);
                    }
                }
            }
        }
        return modsDoc;
    }

    /**
     * add mods:genre@type='type'.
     * 
     * @param value {@link String}
     * @param type {@link String}
     * @param lang {@link String}
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
     * @param desc {@link String}
     */
    public void addNewCorporateNameType(String value, String lang, String href, String desc)
    {
        NameType nameType = modsType.addNewName();
        nameType.setType(NameTypeAttribute.CORPORATE);
        nameType.setAuthority("fao-aos-corporatebody");
        nameType.setType2("simple");
        nameType.setHref(href);
        nameType.setLang2(lang);
        nameType.addNewNamePart().setStringValue(value);
        if (desc != null)
        {
            nameType.addNewDescription().setStringValue(desc);
        }
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
        // titleInfo.setTransliteration("Code to be determined");
        titleInfo.setType2(TitleInfoType.Type.TRANSLATED);
        titleInfo.setLang2(lang);
        XmlString title = titleInfo.addNewTitle();
        title.setStringValue(value);
    }

    /**
     * add mods:titleInfo@transliteration='Code to be detremined'/title.
     * 
     * @param value {@link String}
     * @param lang {@link String}
     */
    public void addNewTransliterationTitleInfoType(String value, String lang)
    {
        TitleInfoType titleInfo = modsType.addNewTitleInfo();
        titleInfo.setTransliteration("Code to be determined");
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
    public void addSubTitle2ExistingTitleInfoType(String value, String lang)
    {
        TitleInfoType[] titleInfos = modsType.getTitleInfoArray();
        for (TitleInfoType titleInfo : titleInfos)
        {
            if (titleInfo.getLang2().equalsIgnoreCase(lang))
            {
                XmlString subTitle = titleInfo.addNewSubTitle();
                subTitle.setStringValue(value);
            }
        }
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
     * @param type {@link String}
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
        OriginInfoType origin = null;
        if (modsType.sizeOfOriginInfoArray() == 1)
        {
            origin = modsType.getOriginInfoArray(0);
        }
        else
        {
            origin = modsType.addNewOriginInfo();
        }
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
        LanguageType langType = null;
        if (modsType.sizeOfLanguageArray() == 1)
        {
            langType = modsType.getLanguageArray(0);
        }
        else
        {
            langType = modsType.addNewLanguage();
        }
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
     * add mods:relatedItem@type='itemType'/identifier@type='idType'.
     * 
     * @param value {@link String}
     * @param itemType {@link String}
     * @param idType {@link String}
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
                if (itemType.equalsIgnoreCase("original"))
                {
                    related.setType(RelatedItemType.Type.ORIGINAL);
                }
            }
        }
        IdentifierType id = related.addNewIdentifier();
        id.setType(idType);
        id.setStringValue(value);
    }

    /**
     * add /identifier@type='idType' to existing RelatedItem.
     * 
     * @param value {@link String}
     * @param itemType {@link String}
     * @param idType {@link String}
     */
    public void addIdentifier2existingRelatedItem(String value, String itemType, String idType)
    {
        if (modsType.sizeOfRelatedItemArray() > 0)
        {
            RelatedItemType[] relItems = modsType.getRelatedItemArray();
            for (RelatedItemType relItem : relItems)
            {
                if (relItem.getType().equals(RelatedItemType.Type.SERIES))
                {
                    if (itemType.equalsIgnoreCase("series"))
                    {
                        IdentifierType id = relItem.addNewIdentifier();
                        id.setType(idType);
                        id.setStringValue(value);
                    }
                }
                else
                {
                    if (relItem.getType().equals(RelatedItemType.Type.HOST))
                    {
                        if (itemType.equalsIgnoreCase("host"))
                        {
                            IdentifierType id = relItem.addNewIdentifier();
                            id.setType(idType);
                            id.setStringValue(value);
                        }
                    }
                    else
                    {
                        if (relItem.getType().equals(RelatedItemType.Type.ORIGINAL))
                        {
                            if (itemType.equalsIgnoreCase("original"))
                            {
                                IdentifierType id = relItem.addNewIdentifier();
                                id.setType(idType);
                                id.setStringValue(value);
                            }
                        }
                    }
                }
            }
        }
        else
        {
            addNewRelatedItemIdentifier(value, itemType, idType);
        }
    }

    /**
     * add /note@type='noteType' to existing RelatedItem.
     * 
     * @param value {@link String}
     * @param itemType {@link String}
     * @param noteType {@link String}
     */
    public void addNote2existingRelatedItem(String value, String itemType, String noteType)
    {
        if (modsType.sizeOfRelatedItemArray() > 0)
        {
            RelatedItemType[] relItems = modsType.getRelatedItemArray();
            for (RelatedItemType relItem : relItems)
            {
                if (relItem.getType().equals(RelatedItemType.Type.SERIES))
                {
                    if (itemType.equalsIgnoreCase("series"))
                    {
                        NoteType note = relItem.addNewNote();
                        note.setType(noteType);
                        note.setStringValue(value);
                    }
                }
                else
                {
                    if (relItem.getType().equals(RelatedItemType.Type.HOST))
                    {
                        if (itemType.equalsIgnoreCase("host"))
                        {
                            NoteType note = relItem.addNewNote();
                            note.setType(noteType);
                            note.setStringValue(value);
                        }
                    }
                    else
                    {
                        if (relItem.getType().equals(RelatedItemType.Type.ORIGINAL))
                        {
                            if (itemType.equalsIgnoreCase("original"))
                            {
                                NoteType note = relItem.addNewNote();
                                note.setType(noteType);
                                note.setStringValue(value);
                            }
                        }
                    }
                }
            }
        }
        else
        {
            addNewRelatedItemNote(value, itemType, noteType);
        }
    }

    /**
     * add /part/detail to existing RelatedItem.
     * 
     * @param value {@link String}
     * @param itemType {@link String}
     */
    public void addPartDetail2existingRelatedItem(String value, String itemType)
    {
        if (modsType.sizeOfRelatedItemArray() > 0)
        {
            RelatedItemType[] relItems = modsType.getRelatedItemArray();
            for (RelatedItemType relItem : relItems)
            {
                if (relItem.getType().equals(RelatedItemType.Type.SERIES))
                {
                    if (itemType.equalsIgnoreCase("series"))
                    {
                        PartType part = relItem.addNewPart();
                        DetailType detail = part.addNewDetail();
                        detail.addNumber(value);
                    }
                }
                else
                {
                    if (relItem.getType().equals(RelatedItemType.Type.HOST))
                    {
                        if (itemType.equalsIgnoreCase("host"))
                        {
                            PartType part = relItem.addNewPart();
                            DetailType detail = part.addNewDetail();
                            detail.addNumber(value);
                        }
                    }
                }
            }
        }
        else
        {
            addNewRelatedItemPartDetail(value);
        }
    }

    /**
     * add mods:relatedItem@type='itemType'/note@type='noteType'.
     * 
     * @param value {@link String}
     * @param itemType {@link String}
     * @param noteType {@link String}
     */
    public void addNewRelatedItemNote(String value, String itemType, String noteType)
    {
        RelatedItemType related = modsType.addNewRelatedItem();
        if (itemType.equalsIgnoreCase("original"))
        {
            related.setType(RelatedItemType.Type.ORIGINAL);
        }
        else
        {
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
            }
        }
        NoteType note = related.addNewNote();
        note.setType(noteType);
        note.setStringValue(value);
    }

    /**
     * add mods:relatedItem@type='series'@lang='values[2]'@authority='fao-aos-series'/titleInfo/title.
     * 
     * @param values {@link String[]}
     */
    public void addNewRelatedItemSeries(ArrayList<String[]> titles, String issn)
    {
        if (titles != null || issn != null)
        {
            RelatedItemType related = modsType.addNewRelatedItem();
            related.setType(RelatedItemType.Type.SERIES);
            if (titles != null)
            {
                for (String[] ser_title : titles)
                {
                    TitleInfoType titleInfo = related.addNewTitleInfo();
                    titleInfo.setLang2(ser_title[2]);
                    titleInfo.setAuthority("fao-aos-series");
                    titleInfo.setHref(ser_title[1]);
                    XmlString title = titleInfo.addNewTitle();
                    title.setStringValue(ser_title[0]);
                }
            }
            if (issn != null)
            {
                IdentifierType idType = related.addNewIdentifier();
                idType.setType("issn");
                idType.setStringValue(issn);
            }
        }
    }

    /**
     * add mods:relatedItem@type='host'@lang='values[2]'@authority='fao-aos-journal'/titleInfo/title.
     * 
     * @param values {@link String[]}
     */
    public void addNewRelatedItemHost(ArrayList<String[]> journal_titles, String issn, String pages)
    {
        RelatedItemType related = modsType.addNewRelatedItem();
        related.setType(RelatedItemType.Type.HOST);
        for (String[] journal : journal_titles)
        {
            TitleInfoType titleInfo = related.addNewTitleInfo();
            titleInfo.setLang2(journal[2]);
            titleInfo.setAuthority("fao-aos-journal");
            titleInfo.setHref(journal[1]);
            XmlString title = titleInfo.addNewTitle();
            title.setStringValue(journal[0]);
        }
        if (issn != null)
        {
            IdentifierType idType = related.addNewIdentifier();
            idType.setType("issn");
            idType.setStringValue(issn);
        }
        if (pages != null)
        {
            PartType part = related.addNewPart();
            DetailType detail = part.addNewDetail();
            detail.addNumber(pages);
        }
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

    /**
     * @param langArray {@link String[]}
     * @return {@link boolean}
     */
    public boolean checkLanguages(String[] langArray)
    {
        boolean checked = false;
        List<String> languages = Arrays.asList(langArray);
        if (languages.contains("English") && languages.contains("Arabic"))
        {
            checked = true;
        }
        if (languages.contains("English") && languages.contains("Chinese"))
        {
            checked = true;
        }
        if (languages.contains("English") && languages.contains("Arabic") && languages.contains("Chinese"))
        {
            checked = true;
        }
        if ((languages.contains("Arabic") || languages.contains("Chinese")) && !languages.contains("English"))
        {
            checked = true;
        }
        return checked;
    }

    /**
     * @param langArray {@link String[]}
     * @return {@link boolean}
     */
    public boolean checkLangIsRussian(String[] langArray)
    {
        boolean russian = false;
        List<String> languages = Arrays.asList(langArray);
        if (languages.contains("Russian"))
        {
            russian = true;
        }
        return russian;
    }

    /**
     * creata MODS datastream with values from EIMS_CDR only.
     * 
     * @param eims {@link ItemType}
     * @return {@link ModsDocument}
     */
    public ModsDocument eimscdr(ItemType eims)
    {
        ModsDocument modsDoc = ModsDocument.Factory.newInstance();
        modsType = modsDoc.addNewMods();
        modsType.setVersion(VersionType.X_3_3);
        // M-1
        // add mods:genre@type='class'
        // M-2 + M-3
        // add mods:name@type='personal'/namePart
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
        // M-4 - M-7
        // add mods:name@type='corporate'/namePart
        // M-8 - M-15
        // add mods:name@type='conference'/namePart
        if (eims.getConference() != null)
        {
            NameType nameType = modsType.addNewName();
            nameType.setType(NameTypeAttribute.CONFERENCE);
            nameType.setAuthority("fao-aos-conference");
            String confname = eims.getConference().getConferenceName().getStringValue();
            nameType.addNewNamePart().setStringValue(confname);
        }
        // M-16 - M-22
        // add mods:titleInfo/title
        // TODO: check rules for TIT_OT and title lang=ot
        if (eims.sizeOfTitleArray() > 0)
        {
            for (int t = 0; t < eims.sizeOfTitleArray(); t++)
            {
                TitleType eimsTitle = eims.getTitleArray(t);
                String title = eimsTitle.getStringValue();
                String lang = eimsTitle.getLang();
                addNewTitleInfoType(title, lang);
            }
        }
        // M-23
        // add mods:titleInfo@type='translated'/title
        // M-24 - M-29
        // add mods:titleInfo/subTitle
        if (eims.sizeOfSubtitleArray() > 0)
        {
            for (int s = 0; s < eims.sizeOfSubtitleArray(); s++)
            {
                SubtitleType subTitle = eims.getSubtitleArray(s);
                String sub = subTitle.getStringValue();
                String lang = subTitle.getLang();
                // addNewSubTitleInfoType(sub, lang);
                addSubTitle2ExistingTitleInfoType(sub, lang);
            }
        }
        // M-30 - M-34
        // add mods:note@type='title'
        // M-35
        // add mods:originInfo/edition
        // M-36
        // add mods:originInfo/publisher
        if (eims.getPublisher() != null)
        {
            OriginInfoType origin = null;
            if (modsType.sizeOfOriginInfoArray() == 1)
            {
                origin = modsType.getOriginInfoArray(0);
            }
            else
            {
                origin = modsType.addNewOriginInfo();
            }
            XmlString publisher = origin.addNewPublisher();
            publisher.setStringValue(eims.getPublisher().getPublisherName());
        }
        // M-37
        // add mods:originInfo/placce/placeterm@type='text'
        // M-38 - M-40
        // add mods:originInfo/dateIssued
        if (eims.getDate() != null)
        {
            String date = eims.getDate().getStringValue();
            addNewDateIssued(date);
        }
        // M-41
        // add mods:originInfo/dateOther@type='year'
        // M-42
        // add mods:identifier@type='type'
        if (eims.getIsbn() != null)
        {
            addNewIdentifier(eims.getIsbn(), "isbn");
        }
        // M-43
        // add mods:language/languageTerm@type='code'@authority
        if (eims.getLanguage() != null)
        {
            String[] codes = new LanguageCodes().getIso639Codes2(eims.getLanguage().toLowerCase());
            if (codes != null)
            {
                addNewLanguage(codes);
            }
        }
        // M-44
        // add mods:identifier@type='type'
        // M-45
        // add mods:identifier@type='type'
        if (eims.getJobno() != null)
        {
            addNewIdentifier(eims.getJobno(), "jn");
        }
        // M-46
        // add mods:relatedItem@type='originel'/titleInfo/title
        if (eims.getProject() != null)
        {
            String[] values = new ProjectName().checkLabel(eims.getProject().getProjectName());
            if (values != null)
            {
                addNewRelatedItem(values);
            }
        }
        // M-47
        // add mods:relatedItem@type='project'/identifier@type='faopn'
        if (eims.getProject() != null)
        {
            addNewRelatedItemIdentifier(eims.getProject().getProjectCode(), "project", "faopn");
        }
        // M-48
        // add mods:relatedItem@type='project'/note@type='project'
        // M-49 - M-52 + M-54 - M-58
        // add mods:relatedItem@type='series'/titleInfo/title
        // assumption: lang is always en
        String issn = null;
        ArrayList<String[]> seriesTitles = null;
        if (eims.getIspartofseries() != null)
        {
            String[] ser_vals = new SeriesName().getEnglish(eims.getIspartofseries());
            if (ser_vals != null)
            {
                seriesTitles = new ArrayList<String[]>();
                seriesTitles.add(ser_vals);
            }
        }
        if (eims.getIssn() != null)
        {
            issn = eims.getIssn();
        }
        addNewRelatedItemSeries(seriesTitles, issn);
        // M-61
        // add mods:note@type='library_subject_code'
        // M-62 - M-63
        // add mods:physicalDescription/extent
        // M-64
        // add mods:physicalDescription/note
        // M-65
        // add mods:location/url@type='external url'
        // M-66
        // add mods:note@type='source note'
        // M-67 -M-68
        // add mods:note
        // according to IS only taken from FAODOC
        /*
         * if (eims.getNotes() != null) { NoteType noteType = modsType.addNewNote();
         * noteType.setStringValue(eims.getNotes()); }
         */
        // M-69 - M-75
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
        // M-76 - M-82
        // add tons of mods:genre@type='type'
        // M-83 - M-87
        // add new mods:location/physicalLocation
        LocationType fao_location = null;
        if (modsType.sizeOfLocationArray() == 1)
        {
            fao_location = modsType.getLocationArray(0);
        }
        else
        {
            fao_location = modsType.addNewLocation();
        }
        PhysicalLocationType phys = fao_location.addNewPhysicalLocation();
        phys.setStringValue(FAO);
        HoldingSimpleType holding = fao_location.addNewHoldingSimple();
        // M-88 - M-90
        // add mods:location/url@note
        if (eims.getURL() != null || eims.getPDFURL() != null || eims.getZIPURL() != null)
        {
            LocationType location = null;
            if (modsType.sizeOfLocationArray() == 1)
            {
                location = modsType.getLocationArray(0);
            }
            else
            {
                location = modsType.addNewLocation();
            }
            if (eims.getURL() != null)
            {
                UrlType url = location.addNewUrl();
                url.setNote(eims.getURL().getNote());
                url.setStringValue(eims.getURL().getStringValue());
            }
            if (eims.getPDFURL() != null)
            {
                UrlType url = location.addNewUrl();
                url.setNote(eims.getPDFURL().getNote());
                url.setStringValue(eims.getPDFURL().getStringValue());
            }
            if (eims.getZIPURL() != null)
            {
                UrlType url = location.addNewUrl();
                url.setNote(eims.getZIPURL().getNote());
                url.setStringValue(eims.getZIPURL().getStringValue());
            }
        }
        // M-91
        // add mods:physicalDescription/form or genre@type='type'
        if (eims.getGenre() != null)
        {
            if (eims.getGenre().getStringValue().equals("Annotated bibliography"))
            {
                addNewGenreType("Bibliography", "type", "en");
                addNewGenreType("Z", "type", "en");
            }
            if (eims.getGenre().getStringValue().equals("Book"))
            {
                addNewGenreType("Publication", "type", "en");
                addNewGenreType("P", "type", "en");
            }
            if (eims.getGenre().getStringValue().equals("Journal"))
            {
                addNewGenreType(eims.getGenre().getStringValue(), "type", "en");
                addNewGenreType("J", "type", "en");
            }
            if (eims.getGenre().getStringValue().equals("Meeting"))
            {
                addNewGenreType(eims.getGenre().getStringValue(), "type", "en");
                addNewGenreType("K", "type", "en");
            }
            if (eims.getGenre().getStringValue().equals("Other"))
            {
                addNewGenreType("Information", "type", "en");
                addNewGenreType("I", "type", "en");
            }
            if (eims.getGenre().getStringValue().equals("Project"))
            {
                addNewGenreType("Field document", "type", "en");
                addNewGenreType("X", "type", "en");
            }
            if (eims.getGenre().getStringValue().equals("Report"))
            {
                addNewGenreType("Meeting", "type", "en");
                addNewGenreType("K", "type", "en");
            }
        }
        // M-92
        // add mods:recordInfo/record/creationDate
        if (eims.getDateCreated() != null)
        {
            RecordInfoType rit = null;
            if (modsType.sizeOfRecordInfoArray() == 1)
            {
                rit = modsType.getRecordInfoArray(0);
            }
            else
            {
                rit = modsType.addNewRecordInfo();
            }
            DateType date = rit.addNewRecordCreationDate();
            date.setStringValue(eims.getDateCreated());
        }
        // E-17 no longer in EIMS datastream
        // create mods:note@type='division' instead
        if (eims.getDivision() != null)
        {
            addNewNoteType(eims.getDivision().getStringValue(), "division", eims.getDivision().getLang());
        }
        return modsDoc;
    }

    /**
     * creata MODS datastream with values from FAODOC only.
     * 
     * @param faodoc {@link ITEMType}
     * @return {@link ModsDocument}
     */
    public ModsDocument faodoc(ITEMType faodoc)
    {
        ModsDocument modsDoc = ModsDocument.Factory.newInstance();
        modsType = modsDoc.addNewMods();
        modsType.setVersion(VersionType.X_3_3);
        // M-1
        // add mods:genre@type='class'
        if (faodoc.sizeOfBIBLEVELArray() > 0)
        {
            addNewGenreType(faodoc.getBIBLEVELArray(0), "class", null);
        }
        // M-2 + M-3
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
                if (e.getValue().toString().equalsIgnoreCase(""))
                {
                    roleTerm.setStringValue("author");
                }
                else
                {
                    roleTerm.setStringValue(e.getValue().toString());
                }
            }
        }
        // M-4 - M-7
        // add mods:name@type='corporate'/namePart
        if (faodoc.sizeOfAUCORENArray() > 0)
        {
            for (int a = 0; a < faodoc.sizeOfAUCORENArray(); a++)
            {
                String au_cor_en = faodoc.getAUCORENArray(a);
                String[] labelHrefDescription = new CorporateBody().getEnglish(au_cor_en);
                if (labelHrefDescription != null)
                {
                    addNewCorporateNameType(labelHrefDescription[0], "en", labelHrefDescription[1],
                            labelHrefDescription[2]);
                }
            }
        }
        if (faodoc.sizeOfAUCORFRArray() > 0)
        {
            for (int a = 0; a < faodoc.sizeOfAUCORFRArray(); a++)
            {
                String au_cor_fr = faodoc.getAUCORFRArray(a);
                String[] labelHrefDescription = new CorporateBody().getFrench(au_cor_fr);
                if (labelHrefDescription != null)
                {
                    addNewCorporateNameType(labelHrefDescription[0], "fr", labelHrefDescription[1],
                            labelHrefDescription[2]);
                }
            }
        }
        if (faodoc.sizeOfAUCORESArray() > 0)
        {
            for (int a = 0; a < faodoc.sizeOfAUCORESArray(); a++)
            {
                String au_cor_es = faodoc.getAUCORESArray(a);
                String[] labelHrefDescription = new CorporateBody().getSpanish(au_cor_es);
                if (labelHrefDescription != null)
                {
                    addNewCorporateNameType(labelHrefDescription[0], "es", labelHrefDescription[1],
                            labelHrefDescription[2]);
                }
            }
        }
        if (faodoc.sizeOfAUCOROTArray() > 0)
        {
            for (int a = 0; a < faodoc.sizeOfAUCOROTArray(); a++)
            {
                String au_cor_ot = faodoc.getAUCOROTArray(a);
                String[] labelHrefDescription = new CorporateBody().getOther(au_cor_ot);
                if (labelHrefDescription != null)
                {
                    addNewCorporateNameType(labelHrefDescription[0], "en", labelHrefDescription[1],
                            labelHrefDescription[2]);
                }
            }
        }
        // M-8 - M-15
        // add mods:name@type='conference'/namePart
        if (faodoc.sizeOfCONFERENCEArray() > 0)
        {
            for (CONFERENCEType conference : faodoc.getCONFERENCEArray())
            {
                if (conference.isSetCONFEN())
                {
                    String name = conference.getCONFEN();
                    String[] nameAndHref = new ConferenceName().getEnglish(conference, name);
                    if (nameAndHref != null)
                    {
                        addNewConferenceNameType(nameAndHref[0], "en", nameAndHref[1]);
                    }
                }
                if (conference.isSetCONFFR())
                {
                    String name = conference.getCONFFR();
                    String[] nameAndHref = new ConferenceName().getFrench(conference, name);
                    if (nameAndHref != null)
                    {
                        addNewConferenceNameType(nameAndHref[0], "fr", nameAndHref[1]);
                    }
                }
                if (conference.isSetCONFES())
                {
                    String name = conference.getCONFES();
                    String[] nameAndHref = new ConferenceName().getSpanish(conference, name);
                    if (nameAndHref != null)
                    {
                        addNewConferenceNameType(nameAndHref[0], "es", nameAndHref[1]);
                    }
                }
                if (conference.isSetCONFOT())
                {
                    String name = conference.getCONFOT();
                    String[] nameAndHref = new ConferenceName().getOther(conference, name);
                    if (nameAndHref != null)
                    {
                        addNewConferenceNameType(nameAndHref[0], "en", nameAndHref[1]);
                    }
                }
            }
        }
        // M-16 - M-22
        // add mods:titleInfo/title
        // TODO: check rules for TIT_OT and title lang=ot
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
            if (faodoc.sizeOfTITOTArray() > 0)
            {
                String tit_ot = faodoc.getTITOTArray(0);
                addNewTitleInfoType(tit_ot, "ot");
            }
        // M-23
        // add mods:titleInfo@type='translated'/title
        if (faodoc.sizeOfTITTRArray() > 0)
        {
            for (int t = 0; t < faodoc.sizeOfTITTRArray(); t++)
            {
                String translated = faodoc.getTITTRArray(t);
                addNewTranslatedTitleInfoType(translated, "en");
            }
        }
        // M-24 - M-29
        // add mods:titleInfo/subTitle
        // M-30 - M-34
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
        // M-35
        // add mods:originInfo/edition
        if (faodoc.sizeOfEDITIONArray() > 0)
        {
            OriginInfoType origin = null;
            if (modsType.sizeOfOriginInfoArray() == 1)
            {
                origin = modsType.getOriginInfoArray(0);
            }
            else
            {
                origin = modsType.addNewOriginInfo();
            }
            XmlString edition = origin.addNewEdition();
            edition.setStringValue(faodoc.getEDITIONArray(0));
        }
        // M-36
        // add mods:originInfo/publisher
        if (faodoc.sizeOfPUBNAMEArray() > 0)
        {
            OriginInfoType origin = null;
            if (modsType.sizeOfOriginInfoArray() == 1)
            {
                origin = modsType.getOriginInfoArray(0);
            }
            else
            {
                origin = modsType.addNewOriginInfo();
            }
            for (String p : faodoc.getPUBNAMEArray())
            {
                XmlString publisher = origin.addNewPublisher();
                publisher.setStringValue(p);
            }
        }
        // M-37
        // add mods:originInfo/placce/placeterm@type='text'
        if (faodoc.sizeOfPUBPLACEArray() > 0)
        {
            OriginInfoType origin = null;
            if (modsType.sizeOfOriginInfoArray() == 1)
            {
                origin = modsType.getOriginInfoArray(0);
            }
            else
            {
                origin = modsType.addNewOriginInfo();
            }
            for (String pp : faodoc.getPUBPLACEArray())
            {
                PlaceType place = origin.addNewPlace();
                PlaceTermType placeTerm = place.addNewPlaceTerm();
                placeTerm.setType(CodeOrText.TEXT);
                placeTerm.setStringValue(pp);
            }
        }
        // M-38 - M-40
        // add mods:originInfo/dateIssued
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
        // M-41
        // add mods:originInfo/dateOther@type='year'
        if (faodoc.sizeOfYEARPUBLArray() > 0)
        {
            OriginInfoType origin = null;
            if (modsType.sizeOfOriginInfoArray() == 1)
            {
                origin = modsType.getOriginInfoArray(0);
            }
            else
            {
                origin = modsType.addNewOriginInfo();
            }
            DateOtherType other = origin.addNewDateOther();
            other.setType("year");
            other.setStringValue(faodoc.getYEARPUBLArray(0));
        }
        // M-42
        // add mods:identifier@type='type'
        if (faodoc.sizeOfISBNArray() > 0)
        {
            addNewIdentifier(faodoc.getISBNArray(0), "isbn");
        }
        // M-43
        // add mods:language/languageTerm@type='code'@authority
        if (faodoc.sizeOfLANGArray() > 0)
        {
            for (String lang : faodoc.getLANGArray())
            {
                if (lang.equalsIgnoreCase("spanish"))
                {
                    String[] codes = new LanguageCodes().getIso639Codes("Spanish; Castilian");
                    if (codes != null)
                    {
                        addNewLanguage(codes);
                    }
                }
                else
                {
                    String[] codes = new LanguageCodes().getIso639Codes(lang);
                    if (codes != null)
                    {
                        addNewLanguage(codes);
                    }
                }
            }
        }
        // M-44
        // add mods:identifier@type='type'
        if (faodoc.sizeOfRNArray() > 0)
        {
            addNewIdentifier(faodoc.getRNArray(0), "rn");
        }
        // M-45
        // add mods:identifier@type='type'
            if (faodoc.sizeOfJNArray() > 0)
            {
                addNewIdentifier(faodoc.getJNArray(0), "jn");
            }
        // M-46
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
        // M-47
        // add mods:relatedItem@type='project'/identifier@type='faopn'
        if (faodoc.sizeOfPNUMBERArray() > 0)
        {
            for (String pnumber : faodoc.getPNUMBERArray())
            {
                // addNewRelatedItemIdentifier(pnumber, "project", "faopn");
                addIdentifier2existingRelatedItem(pnumber, "original", "faopn");
            }
        }
        // M-48
        // add mods:relatedItem@type='project'/note@type='project'
        if (faodoc.sizeOfPDOCArray() > 0)
        {
            for (String pdoc : faodoc.getPDOCArray())
            {
                addNote2existingRelatedItem(pdoc, "original", "project");
            }
        }
        // M-49 - M-52 + M-54 - M-58
        // add mods:relatedItem@type='series'/titleInfo/title
        if (faodoc.getBIBLEVELArray(0).equalsIgnoreCase("MS") || faodoc.getBIBLEVELArray(0).equalsIgnoreCase("AMS"))
        {
            ArrayList<String[]> seriesTitles = null;
            if (faodoc.sizeOfSERIESArray() > 0)
            {
                for (SERIESType series : faodoc.getSERIESArray())
                {
                    seriesTitles = new ArrayList<String[]>();
                    String issn = null;
                    if (series.isSetSERTITEN())
                    {
                        String[] ser_vals = new SeriesName().getEnglish(series.getSERTITEN());
                        if (ser_vals != null)
                        {
                            seriesTitles.add(ser_vals);
                        }
                    } // end if
                    if (series.isSetSERTITFR())
                    {
                        String[] ser_vals = new SeriesName().getFrench(series.getSERTITFR());
                        if (ser_vals != null)
                        {
                            seriesTitles.add(ser_vals);
                        }
                    } // end if
                    if (series.isSetSERTITES())
                    {
                        String[] ser_vals = new SeriesName().getSpanish(series.getSERTITES());
                        if (ser_vals != null)
                        {
                            seriesTitles.add(ser_vals);
                        }
                    } // end if
                    if (series.isSetSERTITOT())
                    {
                        String[] ser_vals = new SeriesName().getOther(series.getSERTITOT());
                        if (ser_vals != null)
                        {
                            seriesTitles.add(ser_vals);
                        }
                    } // end if
                    // M-53 + M-59
                    // add mods:relatedItem@type='series'/identifier@type='issn'
                    if (series.isSetISSN())
                    {
                        issn = series.getISSN();
                    }
                    addNewRelatedItemSeries(seriesTitles, issn);
                } // end for
                // TODO: add check for SER_TIT
                // is lang always en?
            } // end if
        } // end if biblevel = MS or AS
        else
        {
            if (faodoc.getBIBLEVELArray(0).equalsIgnoreCase("AS"))
            {
                if (faodoc.sizeOfSERIESArray() > 0)
                {
                    for (SERIESType series : faodoc.getSERIESArray())
                    {
                        ArrayList<String[]> journalTitles = new ArrayList<String[]>();
                        String issn = null;
                        String pages = null;
                        if (series.isSetSERTITEN())
                        {
                            String[] jour_vals = new JournalName().get(series.getSERTITEN());
                            if (jour_vals != null)
                            {
                                journalTitles.add(jour_vals);
                            }
                        }
                        if (series.isSetSERTITFR())
                        {
                            String[] jour_vals = new JournalName().get(series.getSERTITFR());
                            if (jour_vals != null)
                            {
                                journalTitles.add(jour_vals);
                            }
                        }
                        if (series.isSetSERTITES())
                        {
                            String[] jour_vals = new JournalName().get(series.getSERTITES());
                            if (jour_vals != null)
                            {
                                journalTitles.add(jour_vals);
                            }
                        }
                        if (series.isSetSERTITOT())
                        {
                            String[] jour_vals = new JournalName().get(series.getSERTITOT());
                            if (jour_vals != null)
                            {
                                journalTitles.add(jour_vals);
                            }
                        }
                        // M-53 + M-59
                        // add mods:relatedItem@type='series'/identifier@type='issn'
                        if (series.isSetISSN())
                        {
                            issn = series.getISSN();
                        }
                        // M-60
                        // add mods:relatedItem@type='host'/part/detail
                        if (series.isSetSERPAGES())
                        {
                            pages = series.getSERPAGES();
                        }
                        addNewRelatedItemHost(journalTitles, issn, pages);
                    } // end for
                }
            }
        } // end else
        // M-61
        // add mods:note@type='library_subject_code'
        if (faodoc.sizeOfSUBJLIBArray() > 0)
        {
            for (String subjLib : faodoc.getSUBJLIBArray())
            {
                addNewNoteType(subjLib, "library_subject_code", "en");
            }
        }
        // M-62 - M-63
        // add mods:physicalDescription/extent
        if (faodoc.sizeOfPAGESArray() > 0 || faodoc.sizeOfSERIESArray() > 0)
        {
            PhysicalDescriptionType desc = null;
            if (modsType.sizeOfPhysicalDescriptionArray() == 1)
            {
                desc = modsType.getPhysicalDescriptionArray(0);
            }
            else
            {
                desc = modsType.addNewPhysicalDescription();
            }
                if (faodoc.sizeOfPAGESArray() > 0)
                {
                    desc.addExtent(faodoc.getPAGESArray(0));
                }
                for (SERIESType series : faodoc.getSERIESArray())
                {
                    if (series.isSetSERHOLD())
                    {
                        desc.addExtent(series.getSERHOLD());
                    }
                }
        }
        // M-64
        // add mods:physicalDescription/note
        if (faodoc.sizeOfCOLLINFOArray() > 0)
        {
            PhysicalDescriptionType desc = null;
            if (modsType.sizeOfPhysicalDescriptionArray() == 1)
            {
                desc = modsType.getPhysicalDescriptionArray(0);
            }
            else
            {
                desc = modsType.addNewPhysicalDescription();
            }
            for (String cInfo : faodoc.getCOLLINFOArray())
            {
                NoteType note = desc.addNewNote();
                note.setStringValue(cInfo);
            }
        }
        // M-65
        // add mods:location/url@type='external url'
        if (faodoc.sizeOfURLArray() > 0)
        {
            LocationType loc = null;
            if (modsType.sizeOfLocationArray() == 1)
            {
                loc = modsType.getLocationArray(0);
            }
            else
            {
                loc = modsType.addNewLocation();
            }
            for (String url : faodoc.getURLArray())
            {
                UrlType urlType = loc.addNewUrl();
                urlType.setNote("external url");
                urlType.setStringValue(url);
            }
        }
        // M-66
        // add mods:note@type='source note'
        if (faodoc.sizeOfSOURCEArray() > 0)
        {
            if (!faodoc.getSOURCEArray(0).equalsIgnoreCase(""))
            {
                NoteType note = modsType.addNewNote();
                note.setType("source note");
                note.setStringValue(faodoc.getSOURCEArray(0));
            }
        }
        // M-67 -M-68
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
        // according to IS only taken from FAODOC
        /*
         * if (eims.getNotes() != null) { NoteType noteType = modsType.addNewNote();
         * noteType.setStringValue(eims.getNotes()); }
         */
        // M-69 - M-75
        // add mods:abstract
            if (faodoc.sizeOfABSTRArray() > 0)
            {
                for (String abstr : faodoc.getABSTRArray())
                {
                    AbstractType modsAbst = modsType.addNewAbstract();
                    // which language to set here?
                    modsAbst.setStringValue(abstr);
                }
            }
        // M-76 - M-82
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
                    PhysicalDescriptionType desc = null;
                    if (modsType.sizeOfPhysicalDescriptionArray() == 1)
                    {
                        desc = modsType.getPhysicalDescriptionArray(0);
                    }
                    else
                    {
                        desc = modsType.addNewPhysicalDescription();
                    }
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
        // M-83 - M-87
        // add new mods:location/physicalLocation
        LocationType fao_location = null;
        if (modsType.sizeOfLocationArray() == 1)
        {
            fao_location = modsType.getLocationArray(0);
        }
        else
        {
            fao_location = modsType.addNewLocation();
        }
        PhysicalLocationType phys = fao_location.addNewPhysicalLocation();
        phys.setStringValue(FAO);
        HoldingSimpleType holding = fao_location.addNewHoldingSimple();
        if (faodoc.sizeOfLOCArray() > 0)
        {
            for (LOCType locType : faodoc.getLOCArray())
            {
                if (!locType.getLOCATION().equalsIgnoreCase("FAO"))
                {
                    // LocationType location = modsType.addNewLocation();
                    CopyInformationType copy = holding.addNewCopyInformation();
                    StringPlusAuthority form = copy.addNewForm();
                    form.setAuthority("marcform");
                    form.setStringValue("print");
                    copy.addSubLocation(locType.getLOCATION());
                    copy.addShelfLocator(locType.getAVNUMBER());
                }
            }
        }
        if (faodoc.getLOCALNUMBERArray(0) != null)
        {
            // LocationType location = modsType.addNewLocation();
            // HoldingSimpleType holding = fao_location.addNewHoldingSimple();
            CopyInformationType copy = holding.addNewCopyInformation();
            StringPlusAuthority form = copy.addNewForm();
            form.setAuthority("marcform");
            form.setStringValue("print");
            copy.addSubLocation(FAO_MC);
            copy.addShelfLocator(faodoc.getLOCALNUMBERArray(0));
            addNewIdentifier(faodoc.getLOCALNUMBERArray(0), "AccessionNumber");
        }
        if (faodoc.sizeOfMICROFICHEArray() > 0)
        {
            for (String mf : faodoc.getMICROFICHEArray())
            {
                // LocationType microFiche = modsType.addNewLocation();
                // HoldingSimpleType holding = fao_location.addNewHoldingSimple();
                CopyInformationType copy = holding.addNewCopyInformation();
                StringPlusAuthority form = copy.addNewForm();
                form.setAuthority("marcform");
                form.setStringValue("microfiche");
                copy.addShelfLocator(mf);
            }
        }
        // M-88 - M-90
        // add mods:location/url@note
        // M-91
        // add mods:physicalDescription/form or genre@type='type'
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
                addNewGenreType(faodoc.getFORMDOCArray(0), "type", "en");
                addNewGenreType("Z", "type", "en");
            }
            if (faodoc.getFORMDOCArray(0).equals("Dictionary"))
            {
                addNewGenreType(faodoc.getFORMDOCArray(0), "type", "en");
                addNewGenreType("O", "type", "en");
            }
            if (faodoc.getFORMDOCArray(0).equals("Directory"))
            {
                addNewGenreType(faodoc.getFORMDOCArray(0), "type", "en");
                addNewGenreType("B", "type", "en");
            }
            if (faodoc.getFORMDOCArray(0).equals("Field Document"))
            {
                addNewGenreType(faodoc.getFORMDOCArray(0), "type", "en");
                addNewGenreType("X", "type", "en");
            }
            if (faodoc.getFORMDOCArray(0).equals("Handbook/Manual"))
            {
                addNewGenreType(faodoc.getFORMDOCArray(0), "type", "en");
                addNewGenreType("H", "type", "en");
            }
            if (faodoc.getFORMDOCArray(0).equals("Map(s)/Atlas"))
            {
                addNewGenreType(faodoc.getFORMDOCArray(0), "type", "en");
                addNewGenreType("Y", "type", "en");
            }
            if (faodoc.getFORMDOCArray(0).equals("Terminal Report"))
            {
                addNewGenreType(faodoc.getFORMDOCArray(0), "type", "en");
                addNewGenreType("R", "type", "en");
            }
            if (faodoc.getFORMDOCArray(0).equals("Thesaurus"))
            {
                addNewGenreType(faodoc.getFORMDOCArray(0), "type", "en");
                addNewGenreType("T", "type", "en");
            }
        }
        // M-92
        // add mods:recordInfo/record/creationDate
        if (faodoc.sizeOfCDArray() > 0)
        {
            RecordInfoType rit = null;
            if (modsType.sizeOfRecordInfoArray() == 1)
            {
                rit = modsType.getRecordInfoArray(0);
            }
            else
            {
                rit = modsType.addNewRecordInfo();
            }
            DateType dateCD = rit.addNewRecordCreationDate();
            dateCD.setStringValue(Integer.valueOf(faodoc.getCDArray(0)).toString());
            // M-93
            // add mods:recordInfo/record/changedDate
            if (faodoc.sizeOfDUArray() > 0)
            {
                DateType dateDU = rit.addNewRecordChangeDate();
                dateDU.setStringValue(Integer.valueOf(faodoc.getDUArray(0)).toString());
            }
        }
        return modsDoc;
    }

}
