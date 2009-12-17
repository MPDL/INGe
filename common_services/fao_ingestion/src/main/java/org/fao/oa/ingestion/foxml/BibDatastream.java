package org.fao.oa.ingestion.foxml;

import org.apache.xmlbeans.XmlString;

import noNamespace.BibDocument;
import noNamespace.ITEMType;
import noNamespace.BibDocument.Bib;
import noNamespace.BibDocument.Bib.Cataloguer;
import noNamespace.BibDocument.Bib.FileAssignment;
import noNamespace.BibDocument.Bib.FileAssignment.FileAssignmentDescr;

public class BibDatastream
{
    Bib bib = null;
    
    public BibDocument create(ITEMType faodoc)
    {
        BibDocument bibDoc = BibDocument.Factory.newInstance();
        bib = bibDoc.addNewBib();
        if (faodoc.sizeOfCATBYArray() > 0)
        {
            Cataloguer cataloguer = bib.addNewCataloguer();
            for (String catBy : faodoc.getCATBYArray())
            {
                cataloguer.addCataloguerID(catBy);
            }
            if (faodoc.sizeOfTEAMArray() > 0)
            {
                for (String team : faodoc.getTEAMArray())
                {
                    // TODO: get team from FixedControlledLists.xsl
                    cataloguer.addCataloguerTeam(team);
                }
            }
        }
        if (faodoc.sizeOfASSIGNArray() > 0)
        {
            FileAssignment assign = bib.addNewFileAssignment();
            for (String asg : faodoc.getASSIGNArray())
            {
                assign.addFileAssignmentCode(FileAssignment.FileAssignmentCode.Enum.forString(asg));
            }
            if (faodoc.sizeOfASSINGDCArray() > 0)
            {
                for (String asg_dc : faodoc.getASSINGDCArray())
                {
                    // changed bib.xsd. enum did not contain FAO document/publication
                    assign.addFileAssignmentDescr(FileAssignment.FileAssignmentDescr.Enum.forString(asg_dc));
                    // TODO: xml:lang
                }
            }
        }
        if (faodoc.sizeOfINTERNALNOTESArray() > 0)
        {
            for (String internal : faodoc.getINTERNALNOTESArray())
            {
                bib.addInternalNotes(internal);
            }
        }
        if (faodoc.sizeOfRELARNEArray() > 0)
        {
            for (int rel_arn_e : faodoc.getRELARNEArray())
            {
                bib.addRELARNE(Integer.toString(rel_arn_e));
            }
        }
        if (faodoc.sizeOfRELARNFArray() > 0)
        {
            for (int rel_arn_f : faodoc.getRELARNFArray())
            {
                bib.addRELARNF(Integer.toString(rel_arn_f));
            }
        }
        if (faodoc.sizeOfRELARNSArray() > 0)
        {
            for (int rel_arn_s : faodoc.getRELARNSArray())
            {
                bib.addRELARNS(Integer.toString(rel_arn_s));
            }
        }
        if (faodoc.sizeOfRELARNAArray() > 0)
        {
            for (int rel_arn_a : faodoc.getRELARNAArray())
            {
                bib.addRELARNA(Integer.toString(rel_arn_a));
            }
        }
        if (faodoc.sizeOfRELARNCArray() > 0)
        {
            for (int rel_arn_c : faodoc.getRELARNCArray())
            {
                bib.addRELARNC(Integer.toString(rel_arn_c));
            }
        }
        if (faodoc.sizeOfRELARNRArray() > 0)
        {
            for (int rel_arn_r : faodoc.getRELARNRArray())
            {
                bib.addRELARNR(Integer.toString(rel_arn_r));
            }
        }
        if (faodoc.sizeOfPARTOFArray() > 0)
        {
            for (String partOf : faodoc.getPARTOFArray())
            {
                XmlString part = bib.addNewPARTOF();
                part.setStringValue(partOf);
            }
        }
        return bibDoc;
    }
}
