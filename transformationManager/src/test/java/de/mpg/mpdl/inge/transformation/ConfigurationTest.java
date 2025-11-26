package de.mpg.mpdl.inge.transformation;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;

public class ConfigurationTest {

  @Test
  public void testBibtex() throws TransformationException {
    Set<String> expectedCurlyBracketsForCoNEAuthors = new HashSet<String>(
        Arrays.asList(new String[] {"no", "empty brackets", "identifier and affiliation in brackets", "affiliation id in brackets"}));

    Transformer t = TransformerFactory.newTransformer(FORMAT.BIBTEX_STRING, FORMAT.ESCIDOC_ITEM_V3_XML);

    t.mergeConfiguration(Map.of("CurlyBracketsForCoNEAuthors", "no"));

    assertTrue(t.getConfiguration() != null);

    assertTrue(t.getConfiguration().get("CoNE").equals("false"));
    assertTrue(t.getConfiguration().get("CurlyBracketsForCoNEAuthors").equals("no"));

    assertTrue(t.getAllConfigurationValuesFor("CoNE").contains("true"));
    assertTrue(t.getAllConfigurationValuesFor("CoNE").contains("false"));

    //assertTrue(t.getAllConfigurationValuesFor("CurlyBracketsForCoNEAuthors").containsAll(expectedCurlyBracketsForCoNEAuthors));
  }

  @Test
  public void testEdocXml() throws TransformationException {
    Set<String> expectedImportNames = new HashSet<String>(Arrays.asList(new String[] {"AEI", "BiblHertz", "BPC", "Brain", "CBS", "CEC",
        "CPFS", "ETH", "EVOLBIO", "FHI", "KHI", "MPDL", "MPDLExt", "MPI MoleGen", "MPIA", "MPIBF", "MPIBPH", "MPIBioChem", "MPIC", "MPICBG",
        "MPICC", "MPIDS", "MPIDynamics", "MPIE", "MPIEIS", "MPIEM", "MPIEVA", "MPIGF", "MPIIB", "MPIINF", "MPIIPP", "MPIIS", "MPIK",
        "MPIKG", "MPIKOFO", "MPIKYB", "MPIMET", "MPIMF", "MPIMM", "MPIMMG", "MPINEURO", "MPIP", "MPIPF", "MPISS", "MPIPKS", "MPIPL",
        "MPIPsykl", "MPIS", "MPISF", "MPISOC", "MPIeR", "MPL", "MPQ", "MolePhys", "OTHER"}));

    Transformer t = TransformerFactory.newTransformer(FORMAT.EDOC_XML, FORMAT.ESCIDOC_ITEM_V3_XML);

    assertTrue(t.getConfiguration() != null);

    assertTrue(t.getConfiguration().get("CoNE").equals("true"));
    assertTrue(t.getConfiguration().get("import-name").equals("OTHER"));

    assertTrue(t.getAllConfigurationValuesFor("CoNE").contains("true"));
    assertTrue(t.getAllConfigurationValuesFor("CoNE").contains("false"));

    assertTrue(t.getAllConfigurationValuesFor("import-name").containsAll(expectedImportNames));
  }

  @Test
  public void testEndnoteXml() throws TransformationException {
    Set<String> expectedFlavour =
        new HashSet<String>(Arrays.asList(new String[] {"CAESAR", "ICE", "BGC", "MPFI", "MPIMP", "MPIMPExt", "MPIO", "OTHER"}));
    Transformer t = TransformerFactory.newTransformer(FORMAT.ENDNOTE_XML, FORMAT.ESCIDOC_ITEM_V3_XML);

    assertTrue(t.getConfiguration() != null);
    assertTrue(t.getConfiguration().get("CoNE").equals("false"));
    assertTrue(t.getConfiguration().get("Flavor").equals("OTHER"));

    assertTrue(t.getAllConfigurationValuesFor("CoNE").contains("true"));
    assertTrue(t.getAllConfigurationValuesFor("CoNE").contains("false"));

    assertTrue(t.getAllConfigurationValuesFor("Flavor").containsAll(expectedFlavour));
  }

  @Test
  public void testEndnoteString() throws TransformationException {
    Set<String> expectedFlavour =
        new HashSet<String>(Arrays.asList(new String[] {"CAESAR", "ICE", "BGC", "MPFI", "MPIMP", "MPIMPExt", "MPIO", "OTHER"}));
    Transformer t = TransformerFactory.newTransformer(FORMAT.ENDNOTE_STRING, FORMAT.ESCIDOC_ITEM_V3_XML);

    System.out.println(t.getConfiguration());


    assertTrue(t.getConfiguration() != null);
    assertTrue(t.getConfiguration().get("CoNE").equals("false"));
    assertTrue(t.getConfiguration().get("Flavor").equals("OTHER"));

    assertTrue(t.getAllConfigurationValuesFor("CoNE").contains("true"));
    assertTrue(t.getAllConfigurationValuesFor("CoNE").contains("false"));

    assertTrue(t.getAllConfigurationValuesFor("Flavor").containsAll(expectedFlavour));
  }

  @Test
  public void testRisString() throws TransformationException {
    Set<String> expectedImportNames = new HashSet<String>(Arrays.asList(new String[] {"MPDL", "OTHER"}));
    Transformer t = TransformerFactory.newTransformer(FORMAT.RIS_STRING, FORMAT.ESCIDOC_ITEM_V3_XML);

    assertTrue(t.getConfiguration() != null);
    assertTrue(t.getConfiguration().get("CoNE").equals("false"));
    assertTrue(t.getConfiguration().get("import-name").equals("OTHER"));

    assertTrue(t.getAllConfigurationValuesFor("CoNE").contains("true"));
    assertTrue(t.getAllConfigurationValuesFor("CoNE").contains("false"));

    assertTrue(t.getAllConfigurationValuesFor("import-name").containsAll(expectedImportNames));
  }

  @Test
  public void testRisXml() throws TransformationException {
    Set<String> expectedImportNames = new HashSet<String>(Arrays.asList(new String[] {"MPDL", "OTHER"}));
    Transformer t = TransformerFactory.newTransformer(FORMAT.RIS_XML, FORMAT.ESCIDOC_ITEM_V3_XML);

    assertTrue(t.getConfiguration() != null);
    assertTrue(t.getConfiguration().get("CoNE").equals("false"));
    assertTrue(t.getConfiguration().get("import-name").equals("OTHER"));

    assertTrue(t.getAllConfigurationValuesFor("CoNE").contains("true"));
    assertTrue(t.getAllConfigurationValuesFor("CoNE").contains("false"));

    assertTrue(t.getAllConfigurationValuesFor("import-name").containsAll(expectedImportNames));
  }

  @Test
  public void testWosString() throws TransformationException {
    Set<String> expectedImportNames = new HashSet<String>(Arrays.asList(new String[] {"MPDL", "OTHER"}));
    Transformer t = TransformerFactory.newTransformer(FORMAT.WOS_STRING, FORMAT.ESCIDOC_ITEM_V3_XML);

    assertTrue(t.getConfiguration() != null);
    assertTrue(t.getConfiguration().get("CoNE").equals("false"));
    assertTrue(t.getConfiguration().get("import-name").equals("OTHER"));

    assertTrue(t.getAllConfigurationValuesFor("CoNE").contains("true"));
    assertTrue(t.getAllConfigurationValuesFor("CoNE").contains("false"));

    assertTrue(t.getAllConfigurationValuesFor("import-name").containsAll(expectedImportNames));
  }

  @Test
  public void testWosXml() throws TransformationException {
    Set<String> expectedImportNames = new HashSet<String>(Arrays.asList(new String[] {"MPDL", "MPL", "OTHER"}));
    Transformer t = TransformerFactory.newTransformer(FORMAT.WOS_XML, FORMAT.ESCIDOC_ITEM_V3_XML);

    assertTrue(t.getConfiguration() != null);
    assertTrue(t.getConfiguration().get("CoNE").equals("false"));
    assertTrue(t.getConfiguration().get("import-name").equals("OTHER"));

    assertTrue(t.getAllConfigurationValuesFor("CoNE").contains("true"));
    assertTrue(t.getAllConfigurationValuesFor("CoNE").contains("false"));

    assertTrue(t.getAllConfigurationValuesFor("import-name").containsAll(expectedImportNames));
  }

  @Test
  public void testMabString() throws TransformationException {

    Transformer t = TransformerFactory.newTransformer(FORMAT.MAB_STRING, FORMAT.ESCIDOC_ITEM_V3_XML);
    assertTrue(t.getConfiguration() != null);
    assertTrue(t.getConfiguration().size() == 0);
  }

  @Test
  public void testMarc21String() throws TransformationException {

    Transformer t = TransformerFactory.newTransformer(FORMAT.MARC_21_STRING, FORMAT.ESCIDOC_ITEM_V3_XML);
    assertTrue(t.getConfiguration() != null);
    assertTrue(t.getConfiguration().get("CoNE").equals("false"));

    assertTrue(t.getAllConfigurationValuesFor("CoNE").size() == 2);
    assertTrue(t.getAllConfigurationValuesFor("CoNE").contains("true"));
    assertTrue(t.getAllConfigurationValuesFor("CoNE").contains("false"));
  }

  @Test
  public void testMarcXml() throws TransformationException {

    Transformer t = TransformerFactory.newTransformer(FORMAT.MARC_XML, FORMAT.ESCIDOC_ITEM_V3_XML);
    assertTrue(t.getConfiguration() != null);
    assertTrue(t.getConfiguration().get("CoNE").equals("false"));

    assertTrue(t.getAllConfigurationValuesFor("CoNE").size() == 2);
    assertTrue(t.getAllConfigurationValuesFor("CoNE").contains("true"));
    assertTrue(t.getAllConfigurationValuesFor("CoNE").contains("false"));
  }

  @Test
  public void testZfnTei() throws TransformationException {

    Transformer t = TransformerFactory.newTransformer(FORMAT.ZFN_TEI_XML, FORMAT.ESCIDOC_ITEM_V3_XML);
    assertTrue(t.getConfiguration() != null);
    assertTrue(t.getConfiguration().get("ftpDirectory").equals("folder"));
    assertTrue(t.getConfiguration().get("License").equals("http://creativecommons.org/licenses/by-nd/3.0/de/"));
    assertTrue(t.getConfiguration().get("ftpServer").equals("servername"));
    assertTrue(t.getConfiguration().get("JournalConeID").equals("954927655916_1"));
  }

  @Test
  public void testEscidoc() throws TransformationException {

    Transformer t = TransformerFactory.newTransformer(FORMAT.ESCIDOC_ITEM_V3_XML, FORMAT.ESCIDOC_ITEM_V3_XML);
    assertTrue(t.getConfiguration().isEmpty());

  }

}
