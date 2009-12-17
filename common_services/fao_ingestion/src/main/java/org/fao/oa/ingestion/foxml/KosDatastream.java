package org.fao.oa.ingestion.foxml;

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
import noNamespace.KosDocument.Kos.Concept.Label;

public class KosDatastream
{
    Kos kos = null;
    
    public KosDocument create(ITEMType faodoc, ItemType eims)
    {
        KosDocument kosDoc = KosDocument.Factory.newInstance();
        kos = kosDoc.addNewKos();
        if (faodoc.sizeOfSUBJAGROVOCENArray() > 0)
        {
            for (String agrovoc : faodoc.getSUBJAGROVOCENArray())
            {
                Concept concept = kos.addNewConcept();
                Label label = concept.addNewLabel();
                label.setStringValue(agrovoc);
            }
        }
        else
        {
            if (eims.sizeOfAgrovocArray() > 0)
            {
                for (AgrovocType agrovocType : eims.getAgrovocArray())
                {
                    Concept concept = kos.addNewConcept();
                    Label label = concept.addNewLabel();
                    label.setStringValue(agrovocType.getStringValue());
                    label.setLanguage(agrovocType.getLang());
                }
            }
        }
        if (faodoc.sizeOfSUBJGEOGRENArray() > 0)
        {
            for (String geogr : faodoc.getSUBJGEOGRENArray())
            {
                Geographic geo = kos.addNewGeographic();
                noNamespace.KosDocument.Kos.Geographic.Label geolabel = geo.addNewLabel();
                geolabel.setStringValue(geogr);
            }
        }
        if (eims.sizeOfCountryArray() > 0)
        {
            for (CountryType country : eims.getCountryArray())
            {
                Country c = kos.addNewCountry();
                noNamespace.KosDocument.Kos.Country.Label countryLabel =  c.addNewLabel();
                countryLabel.setLanguage(country.getLang());
                countryLabel.setStringValue(country.getStringValue());
            }
        }
        if (eims.sizeOfRegionArray() > 0)
        {
            for (RegionType region : eims.getRegionArray())
            {
                Region c = kos.addNewRegion();
                noNamespace.KosDocument.Kos.Region.Label regionLabel =  c.addNewLabel();
                regionLabel.setLanguage("en");
                regionLabel.setStringValue(region.getStringValue());
            }
        }
        if (eims.sizeOfContinentArray() > 0)
        {
            for (ContinentType continent : eims.getContinentArray())
            {
                Continent c = kos.addNewContinent();
                noNamespace.KosDocument.Kos.Continent.Label continentLabel =  c.addNewLabel();
                continentLabel.setLanguage("en");
                continentLabel.setStringValue(continent.getStringValue());
            }
        }
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
        // TODO: SUBJ_FAIRS missing in FAODOC resource item.
        return kosDoc;
    }
}
