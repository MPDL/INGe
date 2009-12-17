package org.fao.oa.ingestion.foxml;

import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.fao.oa.ingestion.utils.XBeanUtils;
import org.purl.agmes.x11.ResourcesDocument;

import noNamespace.ITEMType;
import noNamespace.ItemType;
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

public class Foxml
{
    DigitalObject foxml = null;

    public DigitalObjectDocument merge(ITEMType faodocItem, ItemType eimscdrItem)
    {
        // ARN and identifier of merged resource items
        String faodocARN = faodocItem.getARNArray(0);
        String eimscdrID = eimscdrItem.getIdentifier();
        // create new FOXML1.2 digital object
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
        label.setVALUE("merged object from " + faodocARN + " and " + eimscdrID);
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
        ModsDocument modsDocument = new ModsDatastream().merge(eimscdrItem, faodocItem);
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
        try
        {
            ResourcesDocument resourcesDocument = new AgrisAPDatastream().agrisValues(faodocItem, eimscdrItem);
            ResourcesDocument resources2add = null;
            resources2add = ResourcesDocument.Factory.parse(resourcesDocument.xmlText(XBeanUtils.getAgrisOpts()));
            agrisapContent.set(resources2add);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (XmlException e)
        {
            e.printStackTrace();
        }
        // add BIB datastrem
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
        // add EIMS datastrem
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
        eimsContent.set(new EimsDatastream().create(eimscdrItem, faodocItem));
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
        skosContent.set(new KosDatastream().create(faodocItem, eimscdrItem));
        return foxmlObject;
    }
    
    public XmlOptions loadOpts()
    {
        XmlOptions load = new XmlOptions();
        return load;
    }
}
