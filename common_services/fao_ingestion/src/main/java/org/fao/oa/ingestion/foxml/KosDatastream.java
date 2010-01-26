package org.fao.oa.ingestion.foxml;

import org.fao.oa.ingestion.faodoc.AgrovocSkos;

import noNamespace.AgrovocType;
import noNamespace.ContinentType;
import noNamespace.CountryType;
import noNamespace.ITEMType;
import noNamespace.ItemType;
import noNamespace.KosDocument;
import noNamespace.RegionType;
import noNamespace.KosDocument.Kos;
import noNamespace.KosDocument.Kos.Concept;
import noNamespace.KosDocument.Kos.Continent;
import noNamespace.KosDocument.Kos.Country;
import noNamespace.KosDocument.Kos.Geographic;
import noNamespace.KosDocument.Kos.GeographicFairs;
import noNamespace.KosDocument.Kos.Region;
import noNamespace.KosDocument.Kos.SubjectFairs;
import noNamespace.KosDocument.Kos.Concept.Label;

public class KosDatastream
{
    Kos kos = null;

    /**
     * create SKOS datastream with merged values from FAODOC and EIMS-CDR.
     * @param faodoc {@link ITEMType}
     * @param eims {@link ItemType}
     * @return {@link KosDocument}
     */
    public KosDocument merge(ITEMType faodoc, ItemType eims)
    {
        KosDocument kosDoc = KosDocument.Factory.newInstance();
        kos = kosDoc.addNewKos();
        // S-1 - S-2
        // add skos:concept
        if (faodoc.sizeOfSUBJAGROVOCENArray() > 0)
        {
            AgrovocSkos skos = new AgrovocSkos();
            for (String agrovoc : faodoc.getSUBJAGROVOCENArray())
            {
                String formatted = formatString(agrovoc);
                String uri = skos.getURI(formatted);
                Concept concept = kos.addNewConcept();
                concept.setConceptURI(uri);
                for (String l : skos.getLabels(uri))
                {
                    Label label = concept.addNewLabel();
                    label.setLanguage(l.split("=")[1]);
                    label.setStringValue(l.split("=")[0]);
                }
            }
        }
        else
        {
            if (eims.sizeOfAgrovocArray() > 0)
            {
                AgrovocSkos skos = new AgrovocSkos();
                for (AgrovocType agrovocType : eims.getAgrovocArray())
                {
                    String uri = skos.getURI(agrovocType.getStringValue());
                    Concept concept = kos.addNewConcept();
                    concept.setConceptURI(uri);
                    for (String l : skos.getLabels(uri))
                    {
                        Label label = concept.addNewLabel();
                        label.setLanguage(l.split("=")[1]);
                        label.setStringValue(l.split("=")[0]);
                    }
                }
            }
        }
        // S-3
        // add skos:geographic
        if (faodoc.sizeOfSUBJGEOGRENArray() > 0)
        {
            AgrovocSkos skos = new AgrovocSkos();
            for (String geogr : faodoc.getSUBJGEOGRENArray())
            {
                String formatted = formatString(geogr);
                String uri = skos.getURI(formatted);
                Geographic geo = kos.addNewGeographic();
                geo.setGeographicURI(uri);
                for (String l : skos.getLabels(uri))
                {
                    noNamespace.KosDocument.Kos.Geographic.Label label = geo.addNewLabel();
                    label.setLanguage(l.split("=")[1]);
                    label.setStringValue(l.split("=")[0]);
                }
            }
        }
        // S-4
        // add skos:country
        // TODO: schema: c.set.setISO3 ???
        if (eims.sizeOfCountryArray() > 0)
        {
            for (CountryType country : eims.getCountryArray())
            {
                Country c = kos.addNewCountry();
                c.setISO3("ISO3");
                noNamespace.KosDocument.Kos.Country.Label countryLabel = c.addNewLabel();
                countryLabel.setLanguage(country.getLang());
                countryLabel.setStringValue(country.getStringValue());
            }
        }
        // S-5
        // add skos:region
        if (eims.sizeOfRegionArray() > 0)
        {
            for (RegionType region : eims.getRegionArray())
            {
                Region c = kos.addNewRegion();
                noNamespace.KosDocument.Kos.Region.Label regionLabel = c.addNewLabel();
                regionLabel.setLanguage("en");
                regionLabel.setStringValue(region.getStringValue());
            }
        }
        // S-6
        // add skos:continent
        if (eims.sizeOfContinentArray() > 0)
        {
            for (ContinentType continent : eims.getContinentArray())
            {
                Continent c = kos.addNewContinent();
                noNamespace.KosDocument.Kos.Continent.Label continentLabel = c.addNewLabel();
                continentLabel.setLanguage("en");
                continentLabel.setStringValue(continent.getStringValue());
            }
        }
        // S-7
        // add skos:geographic_fairs
        if (faodoc.sizeOfSUBJGEOGRFAIRSOTArray() > 0)
        {
            for (String geofairs : faodoc.getSUBJGEOGRFAIRSOTArray())
            {
                GeographicFairs geofair = kos.addNewGeographicFairs();
                noNamespace.KosDocument.Kos.GeographicFairs.Label geofairlabel = geofair.addNewLabel();
                geofairlabel.setLanguage("en");
                geofairlabel.setStringValue(geofairs);
            }
        }
        // S-8 - S-9
        // add skos:subject_fairs
        if (faodoc.sizeOfFAIRSSUBJArray() > 0)
        {
            for (String fairssubj : faodoc.getFAIRSSUBJArray())
            {
                SubjectFairs subjfairs = kos.addNewSubjectFairs();
                noNamespace.KosDocument.Kos.SubjectFairs.Label subjfairlabel = subjfairs.addNewLabel();
                subjfairlabel.setLanguage("en");
                subjfairlabel.setStringValue(fairssubj);
            }
        }
        return kosDoc;
    }

    /**
     * create SKOS datastream with values from FAODOC only.
     * @param faodoc {@link ITEMType}
     * @return {@link KosDocument}
     */
    public KosDocument create4Faodoc(ITEMType faodoc)
    {
        KosDocument kosDoc = KosDocument.Factory.newInstance();
        kos = kosDoc.addNewKos();
        // S-1 - S-2
        // add skos:concept
        if (faodoc.sizeOfSUBJAGROVOCENArray() > 0)
        {
            AgrovocSkos skos = new AgrovocSkos();
            for (String agrovoc : faodoc.getSUBJAGROVOCENArray())
            {
                String formatted = formatString(agrovoc);
                String uri = skos.getURI(formatted);
                Concept concept = kos.addNewConcept();
                concept.setConceptURI(uri);
                for (String l : skos.getLabels(uri))
                {
                    Label label = concept.addNewLabel();
                    label.setLanguage(l.split("=")[1]);
                    label.setStringValue(l.split("=")[0]);
                }
            }
        }
        // S-3
        // add skos:geographic
        if (faodoc.sizeOfSUBJGEOGRENArray() > 0)
        {
            AgrovocSkos skos = new AgrovocSkos();
            for (String geogr : faodoc.getSUBJGEOGRENArray())
            {
                String formatted = formatString(geogr);
                String uri = skos.getURI(formatted);
                Geographic geo = kos.addNewGeographic();
                geo.setGeographicURI(uri);
                for (String l : skos.getLabels(uri))
                {
                    noNamespace.KosDocument.Kos.Geographic.Label label = geo.addNewLabel();
                    label.setLanguage(l.split("=")[1]);
                    label.setStringValue(l.split("=")[0]);
                }
            }
        }
        // S-4 - S-6 not available.
        // S-7
        // add skos:geographic_fairs
        if (faodoc.sizeOfSUBJGEOGRFAIRSOTArray() > 0)
        {
            for (String geofairs : faodoc.getSUBJGEOGRFAIRSOTArray())
            {
                GeographicFairs geofair = kos.addNewGeographicFairs();
                noNamespace.KosDocument.Kos.GeographicFairs.Label geofairlabel = geofair.addNewLabel();
                geofairlabel.setLanguage("en");
                geofairlabel.setStringValue(geofairs);
            }
        }
        // S-8 - S-9
        // add skos:subject_fairs
        if (faodoc.sizeOfFAIRSSUBJArray() > 0)
        {
            for (String fairssubj : faodoc.getFAIRSSUBJArray())
            {
                SubjectFairs subjfairs = kos.addNewSubjectFairs();
                noNamespace.KosDocument.Kos.SubjectFairs.Label subjfairlabel = subjfairs.addNewLabel();
                subjfairlabel.setLanguage("en");
                subjfairlabel.setStringValue(fairssubj);
            }
        }
        return kosDoc;
    }

    /**
     * create SKOS datastream with values from EIMS-CDR only.
     * @param eims {@link ItemType}
     * @return {@link KosDocument}
     */
    public KosDocument create4Eims(ItemType eims)
    {
        KosDocument kosDoc = KosDocument.Factory.newInstance();
        kos = kosDoc.addNewKos();
        // S-1 - S-2
        // add skos:concept
        if (eims.sizeOfAgrovocArray() > 0)
        {
            AgrovocSkos skos = new AgrovocSkos();
            for (AgrovocType agrovocType : eims.getAgrovocArray())
            {
                String uri = skos.getURI(agrovocType.getStringValue());
                Concept concept = kos.addNewConcept();
                concept.setConceptURI(uri);
                for (String l : skos.getLabels(uri))
                {
                    Label label = concept.addNewLabel();
                    label.setLanguage(l.split("=")[1]);
                    label.setStringValue(l.split("=")[0]);
                }
            }
        }
        // S-3 not available
        // S-4
        // add skod:country
        if (eims.sizeOfCountryArray() > 0)
        {
            for (CountryType country : eims.getCountryArray())
            {
                Country c = kos.addNewCountry();
                noNamespace.KosDocument.Kos.Country.Label countryLabel = c.addNewLabel();
                countryLabel.setLanguage(country.getLang());
                countryLabel.setStringValue(country.getStringValue());
            }
        }
        // S-5
        // add skod:region
        if (eims.sizeOfRegionArray() > 0)
        {
            for (RegionType region : eims.getRegionArray())
            {
                Region c = kos.addNewRegion();
                noNamespace.KosDocument.Kos.Region.Label regionLabel = c.addNewLabel();
                regionLabel.setLanguage("en");
                regionLabel.setStringValue(region.getStringValue());
            }
        }
        // S-6
        // add skod:continent
        if (eims.sizeOfContinentArray() > 0)
        {
            for (ContinentType continent : eims.getContinentArray())
            {
                Continent c = kos.addNewContinent();
                noNamespace.KosDocument.Kos.Continent.Label continentLabel = c.addNewLabel();
                continentLabel.setLanguage("en");
                continentLabel.setStringValue(continent.getStringValue());
            }
        }
        // S-7 - S-9 not available
        return kosDoc;
    }
    
    public String formatString(String s)
    {
        String formatted = s.toLowerCase();
        String first = formatted.substring(0, 1).toUpperCase();
        formatted = first + formatted.substring(1, formatted.length());
        return formatted;
    }
}
