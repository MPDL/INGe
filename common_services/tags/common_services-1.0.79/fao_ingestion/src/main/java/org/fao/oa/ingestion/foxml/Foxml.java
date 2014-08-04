package org.fao.oa.ingestion.foxml;

import noNamespace.ITEMType;
import noNamespace.ItemType;

import org.apache.xmlbeans.XmlException;
import org.fao.oa.ingestion.utils.XBeanUtils;
import org.purl.agmes.x11.ResourcesDocument;

import fedora.fedoraSystemDef.foxml.DatastreamType;
import fedora.fedoraSystemDef.foxml.DatastreamVersionType;
import fedora.fedoraSystemDef.foxml.DigitalObjectDocument;
import fedora.fedoraSystemDef.foxml.DigitalObjectType;
import fedora.fedoraSystemDef.foxml.ObjectPropertiesType;
import fedora.fedoraSystemDef.foxml.PropertyType;
import fedora.fedoraSystemDef.foxml.StateType;
import fedora.fedoraSystemDef.foxml.XmlContentType;
import fedora.fedoraSystemDef.foxml.DigitalObjectDocument.DigitalObject;
import gov.loc.mods.v3.ModsDocument;

/**
 * main class to create FOXML files.
 * @author Wilhelm Frank (MPDL)
 *
 */
public class Foxml
{
    DigitalObject foxml = null;

    /**
     * create the DigitalObjectDocument object (= FOXML).
     * @param faodocItem {@link ITEMType} or null
     * @param eimscdrItem {@link ItemType} or null
     * @return {@link DigitalObjectDocument}
     */
    public DigitalObjectDocument merge(ITEMType faodocItem, ItemType eimscdrItem)
    {
        // ARN and identifier of merged resource items
        String faodocARN = null;
        String eimscdrID = null;
        if (faodocItem != null)
        {
            faodocARN = faodocItem.getARNArray(0);
        }
        else
        {
            if (eimscdrItem != null)
            {
                eimscdrID = eimscdrItem.getIdentifier();
            }
        }
        // create new FOXML1.1 digital object
        DigitalObjectDocument foxmlObject = DigitalObjectDocument.Factory.newInstance();
        foxml = foxmlObject.addNewDigitalObject();
        foxml.setVERSION(DigitalObjectType.VERSION.X_1_1);
        // set state and label properties
        ObjectPropertiesType foxmlProps = foxml.addNewObjectProperties();
        PropertyType state = foxmlProps.addNewProperty();
        state.setNAME(PropertyType.NAME.INFO_FEDORA_FEDORA_SYSTEM_DEF_MODEL_STATE);
        state.setVALUE("active");
        PropertyType label = foxmlProps.addNewProperty();
        label.setNAME(PropertyType.NAME.INFO_FEDORA_FEDORA_SYSTEM_DEF_MODEL_LABEL);
        if (faodocARN != null && eimscdrID != null)
        {
            label.setVALUE("merged object from " + faodocARN + " and " + eimscdrID);
        }
        else
        {
            if (faodocARN != null && eimscdrID == null)
            {
                label.setVALUE("object from " + faodocARN);
            }
            else
            {
                if (faodocARN == null && eimscdrID != null)
                {
                    label.setVALUE("object from " + eimscdrID);
                }
            }
        }
        // add MODS datastrem
        DatastreamType faoOAMods = foxml.addNewDatastream();
        faoOAMods.setID("MODS");
        faoOAMods.setSTATE(StateType.A);
        faoOAMods.setCONTROLGROUP(DatastreamType.CONTROLGROUP.X);
        faoOAMods.setVERSIONABLE(true);
        DatastreamVersionType modsVersion = faoOAMods.addNewDatastreamVersion();
        modsVersion.setID("MODS.1");
        modsVersion.setLABEL("MODS datastream");
        modsVersion.setMIMETYPE("text/xml");
        XmlContentType modsContent = modsVersion.addNewXmlContent();
        ModsDocument modsDocument = null;
        if (faodocItem != null && eimscdrItem != null)
        {
            modsDocument = new ModsDatastream().merge(eimscdrItem, faodocItem);
        }
        else
        {
            if (faodocItem != null && eimscdrItem == null)
            {
                modsDocument = new ModsDatastream().faodoc(faodocItem);
            }
            else
            {
                if (faodocItem == null && eimscdrItem != null)
                {
                    modsDocument = new ModsDatastream().eimscdr(eimscdrItem);
                }
            }
        }
        ModsDocument mods2add = null;
        try
        {
            mods2add = ModsDocument.Factory.parse(modsDocument.xmlText(XBeanUtils.getModsOpts()));
        }
        catch (XmlException e1)
        {
            e1.printStackTrace();
        }
        modsContent.set(mods2add);
        // add AGROS_AP datastrem
        DatastreamType faoOAAgrisAP = foxml.addNewDatastream();
        faoOAAgrisAP.setID("AGRIS_AP");
        faoOAAgrisAP.setSTATE(StateType.A);
        faoOAAgrisAP.setCONTROLGROUP(DatastreamType.CONTROLGROUP.X);
        faoOAAgrisAP.setVERSIONABLE(true);
        DatastreamVersionType agrisapVersion = faoOAAgrisAP.addNewDatastreamVersion();
        agrisapVersion.setID("AGRIS_AP.1");
        agrisapVersion.setLABEL("AGRIS_AP datastream");
        agrisapVersion.setMIMETYPE("text/xml");
        XmlContentType agrisapContent = agrisapVersion.addNewXmlContent();
        ResourcesDocument resourcesDocument = null;
        try
        {
            if (faodocItem != null && eimscdrItem != null)
            {
                resourcesDocument = new AgrisAPDatastream().merge(faodocItem, eimscdrItem);
            }
            else
            {
                if (faodocItem != null && eimscdrItem == null)
                {
                    resourcesDocument = new AgrisAPDatastream().create4Faodoc(faodocItem);
                }
                else
                {
                    if (faodocItem == null && eimscdrItem != null)
                    {
                        resourcesDocument = new AgrisAPDatastream().create4Eims(eimscdrItem);
                    }
                }
            }
            ResourcesDocument resources2add = null;
            resources2add = ResourcesDocument.Factory.parse(resourcesDocument.xmlText(XBeanUtils.getAgrisOpts()));
            agrisapContent.set(resources2add);
        }
        catch (XmlException e)
        {
            e.printStackTrace();
        }
        // add BIB datastrem (FAODOC ONLY!)
        if (faodocItem != null)
        {
            DatastreamType faoOABib = foxml.addNewDatastream();
            faoOABib.setID("BIB");
            faoOABib.setSTATE(StateType.A);
            faoOABib.setCONTROLGROUP(DatastreamType.CONTROLGROUP.X);
            faoOABib.setVERSIONABLE(true);
            DatastreamVersionType bibVersion = faoOABib.addNewDatastreamVersion();
            bibVersion.setID("BIB.1");
            bibVersion.setLABEL("BIB datastream");
            bibVersion.setMIMETYPE("text/xml");
            XmlContentType bibContent = bibVersion.addNewXmlContent();
            bibContent.set(new BibDatastream().create(faodocItem));
        }
        // add EIMS datastrem
        if (eimscdrItem != null)
        {
            DatastreamType faoOAEims = foxml.addNewDatastream();
            faoOAEims.setID("EIMS");
            faoOAEims.setSTATE(StateType.A);
            faoOAEims.setCONTROLGROUP(DatastreamType.CONTROLGROUP.X);
            faoOAEims.setVERSIONABLE(true);
            DatastreamVersionType eimsVersion = faoOAEims.addNewDatastreamVersion();
            eimsVersion.setID("EIMS.1");
            eimsVersion.setLABEL("EIMS datastream");
            eimsVersion.setMIMETYPE("text/xml");
            XmlContentType eimsContent = eimsVersion.addNewXmlContent();
            if (faodocItem != null)
            {
                eimsContent.set(new EimsDatastream().merge(eimscdrItem, faodocItem));
            }
            else
            {
                eimsContent.set(new EimsDatastream().merge(eimscdrItem, null));
            }
        }
        // add SKOS datastrem
        DatastreamType faoOAskos = foxml.addNewDatastream();
        faoOAskos.setID("SKOS");
        faoOAskos.setSTATE(StateType.A);
        faoOAskos.setCONTROLGROUP(DatastreamType.CONTROLGROUP.X);
        faoOAskos.setVERSIONABLE(true);
        DatastreamVersionType skosVersion = faoOAskos.addNewDatastreamVersion();
        skosVersion.setID("SKOS.1");
        skosVersion.setLABEL("SKOS datastream");
        skosVersion.setMIMETYPE("text/xml");
        XmlContentType skosContent = skosVersion.addNewXmlContent();
        if (faodocItem != null && eimscdrItem != null)
        {
            skosContent.set(new KosDatastream().merge(faodocItem, eimscdrItem));
        }
        else
        {
            if (faodocItem != null && eimscdrItem == null)
            {
                skosContent.set(new KosDatastream().create4Faodoc(faodocItem));
            }
            else
            {
                if (faodocItem == null && eimscdrItem != null)
                {
                    skosContent.set(new KosDatastream().create4Eims(eimscdrItem));
                }
            }
        }
        return foxmlObject;
    }
}
