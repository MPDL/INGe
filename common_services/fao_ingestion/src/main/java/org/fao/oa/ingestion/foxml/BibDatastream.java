package org.fao.oa.ingestion.foxml;

import noNamespace.BibDocument;
import noNamespace.ITEMType;
import noNamespace.BibDocument.Bib;
import noNamespace.BibDocument.Bib.Cataloguer;
import noNamespace.BibDocument.Bib.FileAssignment;

import org.apache.xmlbeans.XmlString;

public class BibDatastream
{
    Bib bib = null;

    /**
     * create BIB datastream with values from FAODOC
     * 
     * @param faodoc {@link ITEMType}
     * @return {@link BibDocument}
     */
    public BibDocument create(ITEMType faodoc)
    {
        BibDocument bibDoc = BibDocument.Factory.newInstance();
        bib = bibDoc.addNewBib();
        // B-1
        // add bib:cataloguer/cataloguerID and bib:cataloguer/cataloguerTeam
        if (faodoc.sizeOfCATBYArray() > 0)
        {
            for (String catBy : faodoc.getCATBYArray())
            {
                if (catBy.contains(";"))
                {
                    for (String cat : catBy.split("; "))
                    {
                        Cataloguer cataloguer = bib.addNewCataloguer();
                        cataloguer.addCataloguerID(cat);
                        if (faodoc.sizeOfTEAMArray() > 0)
                        {
                            for (String team : faodoc.getTEAMArray())
                            {
                                cataloguer.addCataloguerTeam(team);
                            }
                        }
                    }
                }
                else
                {
                    Cataloguer cataloguer = bib.addNewCataloguer();
                    cataloguer.addCataloguerID(catBy);
                    if (faodoc.sizeOfTEAMArray() > 0)
                    {
                        for (String team : faodoc.getTEAMArray())
                        {
                            cataloguer.addCataloguerTeam(team);
                        }
                    }
                }
            }
        }
        // B-3 + B-4
        // add bib:FileAssignment/FileAssignmentCode and FileAssignmentDescr
        if (faodoc.sizeOfASSIGNArray() > 0)
        {
            FileAssignment assign = bib.addNewFileAssignment();
            assign.setLang("en");
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
                }
            }
        }
        // B-5
        // add bib:InternalNotes
        if (faodoc.sizeOfINTERNALNOTESArray() > 0)
        {
            for (String internal : faodoc.getINTERNALNOTESArray())
            {
                bib.addInternalNotes(internal);
            }
        }
        // B-6
        // add bib:REL_ARN_E
        if (faodoc.sizeOfRELARNEArray() > 0)
        {
            for (int rel_arn_e : faodoc.getRELARNEArray())
            {
                bib.addRELARNE(Integer.toString(rel_arn_e));
            }
        }
        // B-7
        // add bib:REL_ARN_F
        if (faodoc.sizeOfRELARNFArray() > 0)
        {
            for (int rel_arn_f : faodoc.getRELARNFArray())
            {
                bib.addRELARNF(Integer.toString(rel_arn_f));
            }
        }
        // B-8
        // add bib:REL_ARN_S
        if (faodoc.sizeOfRELARNSArray() > 0)
        {
            for (int rel_arn_s : faodoc.getRELARNSArray())
            {
                bib.addRELARNS(Integer.toString(rel_arn_s));
            }
        }
        // B-9
        // add bib:REL_ARN_A
        if (faodoc.sizeOfRELARNAArray() > 0)
        {
            for (int rel_arn_a : faodoc.getRELARNAArray())
            {
                bib.addRELARNA(Integer.toString(rel_arn_a));
            }
        }
        // B-10
        // add bib:REL_ARN_C
        if (faodoc.sizeOfRELARNCArray() > 0)
        {
            for (int rel_arn_c : faodoc.getRELARNCArray())
            {
                bib.addRELARNC(Integer.toString(rel_arn_c));
            }
        }
        // B-11
        // add bib:REL_ARN_R
        if (faodoc.sizeOfRELARNRArray() > 0)
        {
            for (int rel_arn_r : faodoc.getRELARNRArray())
            {
                bib.addRELARNR(Integer.toString(rel_arn_r));
            }
        }
        // B-12
        // add bib:PART_OF
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
