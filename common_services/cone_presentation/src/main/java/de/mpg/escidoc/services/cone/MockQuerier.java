/*
 * CDDL HEADER START The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License. You can
 * obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for the
 * specific language governing permissions and limitations under the License. When distributing Covered Code, include
 * this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the
 * following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner] CDDL HEADER END
 */
/*
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH
 * and Max-Planck- Gesellschaft zur Förderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */
package de.mpg.escidoc.services.cone;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.mpg.escidoc.services.cone.util.LocalizedString;
import de.mpg.escidoc.services.cone.util.Pair;
import de.mpg.escidoc.services.cone.util.TreeFragment;
import de.mpg.escidoc.services.cone.util.LocalizedTripleObject;

/**
 * Mock implementation of the {@link Querier} interface.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class MockQuerier implements Querier
{
    private static final LocalizedString DIES_IST_DIE_BESCHREIBUNG = new LocalizedString("Dies ist die Beschreibung. ");
    private static final LocalizedString DIES_IST_DER_TITEL = new LocalizedString("Dies ist der Titel");
    private static final String DC_DESCRIPTION = "http://purl.org/dc/elements/1.1/description";
    private static final String DC_TITLE = "http://purl.org/dc/elements/1.1/title";
    private static final LocalizedString THIS_IS_THE_DESCRIPTION = new LocalizedString("This is the description. ");
    private static final LocalizedString THIS_IS_THE_TITLE = new LocalizedString("This is the title");

    /**
     * {@inheritDoc}
     */
    public List<Pair> query(String model, String query, String lang) throws Exception
    {
        return query(model, query);
    }

    /**
     * {@inheritDoc}
     */
    public List<Pair> query(String model, String query, String lang, int limit) throws Exception
    {
        return query(model, query);
    }
    
    /**
     * {@inheritDoc}
     */
    public List<Pair> query(String model, String query)
    {
        Map<String, String> resultMap = new LinkedHashMap<String, String>();
        resultMap.put("id:0",
                "Acta Crystallographica Section F: Structural Biology and Crystallization Communications  v.62(Pt 7)");
        resultMap.put("id:1", "Acta Histochemica et Cytochemica v.41(2)");
        resultMap.put("id:2", "Acta Veterinaria Scandinavica    v.50(1)");
        resultMap.put("id:3", "Advances in Urology  v.2008");
        resultMap.put("id:4",
                "The Aesculapian — now published as Journal of the Medical Library Association : JMLA v.1(3-4)");
        resultMap.put("id:5", "African Health Sciences  v.8(1)");
        resultMap.put("id:6", "Age  v.29(1)");
        resultMap.put("id:7", "AIDS Research and Therapy    v.5");
        resultMap.put("id:8", "Algorithms for Molecular Biology : AMB   v.3");
        resultMap.put("id:9", "American Journal of Human Genetics   v.82(1)");
        resultMap.put("id:10", "The American Journal of Pathology   v.172(1)");
        resultMap.put("id:11", "American Journal of Pharmaceutical Education    v.72(2)");
        resultMap.put("id:12", "American Journal of Public Health   v.96(7)");
        resultMap.put("id:13", "American Journal of Public Health (New York, N.Y. : 1912) — "
                + "now published as American Journal of Public Health  v.17(12)");
        resultMap.put("id:14", "American Journal of Public Health and the Nation's Health — "
                + "now published as American Journal of Public Health  v.60(12)");
        resultMap.put("id:15", "AMIA Annual Symposium Proceedings   v.2006");
        resultMap.put("id:16", "Amphibian & Reptile Conservation    v.3(1)");
        resultMap.put("id:17", "Anesthesia Progress v.54(4)");
        resultMap.put("id:18", "Annals of Clinical Microbiology and Antimicrobials  v.7");
        resultMap.put("id:19", "Annals of Family Medicine   v.6(Suppl 1)");
        resultMap.put("id:20",
                "Annals of General Hospital Psychiatry — now published as Annals of General Psychiatry   v.3");
        resultMap.put("id:21", "Annals of General Psychiatry    v.7");
        resultMap.put("id:22", "Annals of Surgery   v.246(3)");
        resultMap.put("id:23", "Annals of Surgical Innovation and Rev.2");
        resultMap.put("id:24", "Annals of the Rheumatic Diseases    v.64(7)");
        resultMap.put("id:25", "Annals of The Royal College of Surgeons of England  v.89(5)");
        resultMap.put("id:26", "Antimicrobial Agents and Chemotherapy   v.52(3)");
        resultMap.put("id:27", "Applied and Environmental Microbiology  v.74(6)");
        resultMap.put("id:28",
                "Applied Microbiology — now published as Applied and Environmental Microbiology  v.30(6)");
        resultMap.put("id:29", "Archives of Disease in Childhood — now published as Archives of "
                + "Disease in Childhood. Fetal and Neonatal Edition    v.90(7)");
        resultMap.put("id:30", "Archives of Disease in Childhood. Fetal and Neonatal Edition    v.90(4)");
        resultMap.put("id:31",
                "Archives of Emergency Medicine — now published as Emergency Medicine Journal : EMJ  v.10(4)");
        resultMap.put("id:32", "Arthritis Research — now published as Arthritis Research & Therapy  v.4(6)");
        resultMap.put("id:33", "Arthritis Research & Therapy    v.10(2)");
        resultMap.put("id:34",
                "Association Medical Journal — now published as BMJ : British Medical Journal    v.4(208)");
        resultMap.put("id:35",
                "Australasian Chiropractic & Osteopathy — now published as Chiropractic & Osteopathy v.12(2)");
        resultMap.put("id:36", "Australia and New Zealand Health Policy v.5");
        resultMap.put("id:37", "Bacteriological Reviews — now published as Microbiology and Molecular "
                + "Biology Reviews : MMBR    v.41(4)");
        resultMap.put("id:38", "The Behavior Analyst    v.29(1)");
        resultMap.put("id:39", "Behavioral and Brain Functions : BBF    v.4");
        resultMap.put("id:40", "Beilstein Journal of Organic Chemistry  v.4");
        resultMap.put("id:41", "The Biochemical Journal v.408(Pt 3)");
        resultMap.put("id:42", "Bioinformation  v.2(7)");
        resultMap.put("id:43", "Bioinorganic Chemistry and Applications v.2008");
        resultMap.put("id:44", "Biological Procedures Online    v.10");
        resultMap.put("id:45", "Biology Direct  v.3");
        resultMap.put("id:46", "Biology Letters v.3(3)");
        resultMap.put("id:47", "Biomagnetic Research and Technology v.6");
        resultMap.put("id:48", "Biomedical Digital Libraries    v.4");
        resultMap.put("id:49", "BioMedical Engineering OnLine   v.7");
        resultMap.put("id:50", "Biophysical Journal v.94(11)");
        resultMap.put("id:51", "Biopsychosocial Medicine    v.2");
        resultMap.put("id:52", "Biotechnology for Biofuels  v.1");
        resultMap.put("id:53", "BMC Anesthesiology  v.8");
        resultMap.put("id:54", "BMC Biochemistry    v.9");
        resultMap.put("id:55", "BMC Bioinformatics  v.9(Suppl 6)");
        resultMap.put("id:56", "BMC Biology v.6");
        resultMap.put("id:57", "BMC Biotechnology   v.8");
        resultMap.put("id:58", "BMC Blood Disorders v.8");
        resultMap.put("id:59", "BMC Cancer  v.8");
        resultMap.put("id:60", "BMC Cardiovascular Disorders    v.8");
        resultMap.put("id:61", "BMC Cell Biology    v.9");
        resultMap.put("id:62", "BMC Chemical Biology    v.8");
        resultMap.put("id:63", "BMC Clinical Pathology  v.8");
        resultMap.put("id:64", "BMC Clinical Pharmacology   v.8");
        resultMap.put("id:65", "BMC Complementary and Alternative Medicine  v.8");
        resultMap.put("id:66", "BMC Dermatology v.8");
        resultMap.put("id:67", "BMC Developmental Biology   v.8");
        resultMap.put("id:68", "BMC Ear, Nose, and Throat Disorders v.8");
        resultMap.put("id:69", "BMC Ecology v.8");
        resultMap.put("id:70", "BMC Emergency Medicine  v.8");
        resultMap.put("id:71", "BMC Endocrine Disorders v.8");
        resultMap.put("id:72", "BMC Evolutionary Biology    v.8");
        resultMap.put("id:73", "BMC Family Practice v.9");
        resultMap.put("id:74", "BMC Gastroenterology    v.8");
        resultMap.put("id:75", "BMC Genetics    v.9");
        resultMap.put("id:76", "BMC Genomics    v.9(Suppl 1)");
        resultMap.put("id:77", "BMC Geriatrics  v.8");
        resultMap.put("id:78", "BMC Health Services Rev.8");
        resultMap.put("id:79", "BMC Immunology  v.9");
        resultMap.put("id:80", "BMC Infectious Diseases v.8");
        resultMap.put("id:81", "BMC International Health and Human Rights   v.8");
        resultMap.put("id:82", "BMC Medical Education   v.8");
        resultMap.put("id:83", "BMC Medical Ethics  v.9");
        resultMap.put("id:84", "BMC Medical Genetics    v.9");
        resultMap.put("id:85", "BMC Medical Genomics    v.1");
        resultMap.put("id:86", "BMC Medical Imaging v.8");
        resultMap.put("id:87", "BMC Medical Informatics and Decision Making v.8");
        resultMap.put("id:88", "BMC Medical Physics v.8");
        resultMap.put("id:89", "BMC Medical Research Methodology    v.8");
        resultMap.put("id:90", "BMC Medicine    v.6");
        resultMap.put("id:91", "BMC Microbiology    v.8");
        resultMap.put("id:92", "BMC Molecular Biology   v.9");
        resultMap.put("id:93", "BMC Musculoskeletal Disorders   v.9");
        resultMap.put("id:94", "BMC Nephrology  v.9");
        resultMap.put("id:95", "BMC Neurology   v.8");
        resultMap.put("id:96", "BMC Neuroscience    v.9");
        resultMap.put("id:97", "BMC Nuclear Medicine — now published as BMC Medical Physics v.7");
        resultMap.put("id:98", "BMC Nursing v.7");
        resultMap.put("id:99", "BMC Ophthalmology   v.8");
        resultMap.put("id:100", "BMC Oral Health    v.8");
        resultMap.put("id:101", "BMC Palliative Care    v.7");
        resultMap.put("id:102", "BMC Pediatrics v.8");
        resultMap.put("id:103", "BMC Pharmacology   v.8");
        resultMap.put("id:104", "BMC Physiology v.8");
        resultMap.put("id:105", "BMC Plant Biology  v.8");
        resultMap.put("id:106", "BMC Pregnancy and Childbirth   v.8");
        resultMap.put("id:107", "BMC Proceedings    v.1(Suppl 1)");
        resultMap.put("id:108", "BMC Psychiatry v.8(Suppl 1)");
        resultMap.put("id:109", "BMC Public Health  v.8");
        resultMap.put("id:110", "BMC Pulmonary Medicine v.8");
        resultMap.put("id:111", "BMC Research Notes v.1");
        resultMap.put("id:112", "BMC Structural Biology v.8");
        resultMap.put("id:113", "BMC Surgery    v.8");
        resultMap.put("id:114", "BMC Systems Biology    v.2");
        resultMap.put("id:115", "BMC Urology    v.8");
        resultMap.put("id:116", "BMC Veterinary Rev.4");
        resultMap.put("id:117", "BMC Women's Health v.8");
        resultMap.put("id:118", "BMJ : British Medical Journal  v.337(7661)");
        resultMap.put("id:119", "Breast Cancer Research : BCR   v.10(2)");
        resultMap.put("id:120", "British Heart Journal — now published as Heart v.74(6)");
        resultMap.put("id:121", "British Journal of Clinical Pharmacology   v.64(1)");
        resultMap.put("id:122", "The British Journal of General Practice    v.57(540)");
        resultMap.put("id:123", "British Journal of Industrial Medicine — now published as "
                + "Occupational and Environmental Medicine  v.50(12)");
        resultMap.put("id:124", "The British Journal of Ophthalmology   v.89(7)");
        resultMap.put("id:125", "British Journal of Pharmacology    v.151(6)");
        resultMap.put("id:126", "British Journal of Pharmacology and Chemotherapy — "
                + "now published as British Journal of Pharmacology    v.33(3)");
        resultMap.put("id:127", "British Journal of Preventive & Social Medicine — "
                + "now published as Journal of Epidemiology and Community Health    v.31(4)");
        resultMap.put("id:128", "British Journal of Social Medicine — "
                + "now published as Journal of Epidemiology and Community Health v.6(4)");
        resultMap.put("id:129", "British Journal of Sports Medicine v.39(7)");
        resultMap.put("id:130", "The British Journal of Venereal Diseases — "
                + "now published as Sexually Transmitted Infections    v.60(6)");
        resultMap.put("id:131",
                "British Medical Journal — now published as BMJ : British Medical Journal   v.281(Suppl)");
        resultMap.put("id:132", "British Medical Journal (Clinical research ed.) — "
                + "now published as BMJ : British Medical Journal   v.295(Suppl)");
        resultMap.put("id:133", "Bulletin - British Association of Sport and Medicine — "
                + "now published as British Journal of Sports Medicine v.3(4)");
        resultMap.put("id:134", "Bulletin of the Association of Medical Librarians — "
                + "now published as Journal of the Medical Library Association : JMLA v.1(3-4)");
        resultMap.put("id:135", "Bulletin of the Medical Library Association — "
                + "now published as Journal of the Medical Library Association : JMLA   v.89(4)");
        resultMap.put("id:136", "Bulletin of the New York Academy of Medicine — "
                + "now published as Journal of Urban Health : Bulletin of the New York Academy of Medicine v.74(2)");
        resultMap.put("id:137", "Bulletins of the Public Health — now published as Public Health Reports    v.1");
        Set<String> keySet = new HashSet<String>();
        keySet.addAll(resultMap.keySet());
        for (String id : keySet)
        {
            if (!resultMap.get(id).toLowerCase().contains(query.toLowerCase()))
            {
                resultMap.remove(id);
            }
        }
        List<Pair> resultSet = new ArrayList<Pair>();
        for (String subject : resultMap.keySet())
        {
            Pair pair = new Pair(subject, resultMap.get(subject));
            resultSet.add(pair);
        }
        return resultSet;
    }

    /**
     * {@inheritDoc}
     */
    public TreeFragment details(String model, String id) throws Exception
    {
        TreeFragment resultSet = new TreeFragment(id);
        List<LocalizedTripleObject> triple1 = new ArrayList<LocalizedTripleObject>();
        triple1.add(THIS_IS_THE_TITLE);
        resultSet.put(DC_TITLE, triple1);
        List<LocalizedTripleObject> triple2 = new ArrayList<LocalizedTripleObject>();
        triple2.add(THIS_IS_THE_DESCRIPTION
                .concat(THIS_IS_THE_DESCRIPTION)
                .concat(THIS_IS_THE_DESCRIPTION)
                .concat(THIS_IS_THE_DESCRIPTION));
        resultSet.put(DC_DESCRIPTION, triple1);
        return resultSet;
    }

    /**
     * {@inheritDoc}
     */
    public TreeFragment details(String model, String id, String lang) throws Exception
    {
        TreeFragment resultSet = new TreeFragment(id);
        if ("de".equals(lang))
        {
            List<LocalizedTripleObject> triple1 = new ArrayList<LocalizedTripleObject>();
            triple1.add(DIES_IST_DER_TITEL);
            resultSet.put(DC_TITLE, triple1);
            List<LocalizedString> triple2 = new ArrayList<LocalizedString>();
            triple2.add(DIES_IST_DIE_BESCHREIBUNG
                    .concat(DIES_IST_DIE_BESCHREIBUNG)
                    .concat(DIES_IST_DIE_BESCHREIBUNG)
                    .concat(DIES_IST_DIE_BESCHREIBUNG));
            resultSet.put(DC_DESCRIPTION, triple1);
        }
        else
        {
            List<LocalizedTripleObject> triple1 = new ArrayList<LocalizedTripleObject>();
            triple1.add(THIS_IS_THE_TITLE);
            resultSet.put(DC_TITLE, triple1);
            List<LocalizedString> triple2 = new ArrayList<LocalizedString>();
            triple2
                    .add(THIS_IS_THE_DESCRIPTION
                            .concat(THIS_IS_THE_DESCRIPTION)
                            .concat(THIS_IS_THE_DESCRIPTION)
                            .concat(THIS_IS_THE_DESCRIPTION));
            resultSet.put(DC_DESCRIPTION, triple1);
        }
        return resultSet;
    }

    /**
     * Empty implementation.
     */
    public void create(String model, String id, TreeFragment values) throws Exception
    {
        // Do nothing here
    }

    /**
     * Empty implementation.
     */
    public void delete(String model, String id) throws Exception
    {
     // Do nothing here
    }

    /**
     * Empty implementation.
     */
    public String createUniqueIdentifier(String model) throws Exception
    {
        return "mock" + new Date().getTime();
    }

    /**
     * Empty implementation.
     */
    public List<String> getAllIds(String modelName) throws Exception
    {
        return null;
    }
    
}
