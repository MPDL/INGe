package de.mpg.mpdl.inge.transformation.transformers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemRootVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerVoSource;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class SearchRetrieveResponseToItemOrcidTest {

  @Test
  public void testContributorRolesMapping() throws Exception {
    SearchRetrieveResponseToItemOrcid transformer = new SearchRetrieveResponseToItemOrcid();

    SearchRetrieveResponseVO<ItemVersionVO> responseVO = new SearchRetrieveResponseVO<>();
    List<SearchRetrieveRecordVO<ItemVersionVO>> records = new ArrayList<>();

    ItemVersionVO item = new ItemVersionVO();
    MdsPublicationVO metadata = new MdsPublicationVO();
    metadata.setGenre(MdsPublicationVO.Genre.ARTICLE);
    metadata.setTitle("Test Publication");

    for (CreatorVO.CreatorRole role : CreatorVO.CreatorRole.values()) {
      PersonVO person = new PersonVO();
      person.setGivenName("Given");
      person.setFamilyName(role.name());
      CreatorVO creator = new CreatorVO(person, role);
      metadata.getCreators().add(creator);
    }
    item.setMetadata(metadata);

    SearchRetrieveRecordVO<ItemVersionVO> record = new SearchRetrieveRecordVO<>();
    record.setData(item);
    records.add(record);
    responseVO.setRecords(records);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    TransformerVoSource source = new TransformerVoSource(responseVO);
    TransformerStreamResult result = new TransformerStreamResult(baos);

    transformer.transform(source, result);

    String jsonOutput = baos.toString("UTF-8");
    assertNotNull(jsonOutput);

    ObjectMapper mapper = MapperFactory.getObjectMapper();
    JsonNode root = mapper.readTree(jsonOutput);
    JsonNode bulk = root.path("bulk");
    assertEquals(1, bulk.size());

    JsonNode work = bulk.get(0).path("work");
    JsonNode contributors = work.path("contributors").path("contributor");
    assertEquals(CreatorVO.CreatorRole.values().length, contributors.size());

    for (int i = 0; i < CreatorVO.CreatorRole.values().length; i++) {
      CreatorVO.CreatorRole role = CreatorVO.CreatorRole.values()[i];
      JsonNode contribNode = contributors.get(i);
      String contributorRole = contribNode.path("contributor-attributes").path("contributor-role").asText();

      switch (role) {
        case AUTHOR:
        case ARTIST:
        case COMMENTATOR:
        case INTERVIEWEE:
        case INTERVIEWER:
        case PAINTER:
        case ACTOR:
          assertEquals("Expected author for " + role, "author", contributorRole);
          break;
        case EDITOR:
        case DIRECTOR:
          assertEquals("Expected editor for " + role, "editor", contributorRole);
          break;
        case TRANSLATOR:
          assertEquals("Expected chair-or-translator for " + role, "chair-or-translator", contributorRole);
          break;
        case INVENTOR:
          assertEquals("Expected co-inventor for " + role, "co-inventor", contributorRole);
          break;
        case APPLICANT:
          assertEquals("Expected assignee for " + role, "assignee", contributorRole);
          break;
        case ADVISOR:
          assertEquals("Expected co-investigator for " + role, "co-investigator", contributorRole);
          break;
        case DEVELOPER:
        case CONTRIBUTOR:
        case HONOREE:
        case TRANSCRIBER:
        case ILLUSTRATOR:
        case PHOTOGRAPHER:
        case CINEMATOGRAPHER:
        case SOUND_DESIGNER:
        case PRODUCER:
        case REFEREE:
          assertEquals("Expected support-staff for " + role, "support-staff", contributorRole);
          break;
        default:
          assertEquals("Expected fallback author for " + role, "author", contributorRole);
      }
    }
  }

  @Test
  public void testExternalIdsMappingAndUrls() throws Exception {
    SearchRetrieveResponseToItemOrcid transformer = new SearchRetrieveResponseToItemOrcid();

    SearchRetrieveResponseVO<ItemVersionVO> responseVO = new SearchRetrieveResponseVO<>();
    List<SearchRetrieveRecordVO<ItemVersionVO>> records = new ArrayList<>();

    ItemVersionVO item = new ItemVersionVO();
    item.setObjectId("item_12345");
    ItemRootVO obj = new ItemRootVO();
    obj.setObjectPid("11858/00-001M-0000-000E-B8E5-7");
    item.setObject(obj);

    MdsPublicationVO metadata = new MdsPublicationVO();
    metadata.setGenre(MdsPublicationVO.Genre.ARTICLE);
    metadata.setTitle("Test External IDs");

    Map<IdentifierVO.IdType, String> sampleIds = new HashMap<>();
    sampleIds.put(IdentifierVO.IdType.ADS, "2020ApJ...890....1A");
    sampleIds.put(IdentifierVO.IdType.ARXIV, "2101.00001");
    sampleIds.put(IdentifierVO.IdType.BIORXIV, "10.1101/2020.01.01.123456");
    sampleIds.put(IdentifierVO.IdType.CHEMRXIV, "10.26434/chemrxiv.123456");
    sampleIds.put(IdentifierVO.IdType.DOI, "10.1000/182");
    sampleIds.put(IdentifierVO.IdType.EARTHARXIV, "10.31223/osf.io/12345");
    sampleIds.put(IdentifierVO.IdType.EDARXIV, "10.35542/osf.io/12345");
    sampleIds.put(IdentifierVO.IdType.ESS_OPEN_ARCHIVE, "10.1002/essoar.12345");
    sampleIds.put(IdentifierVO.IdType.ISI, "000123456700001");
    sampleIds.put(IdentifierVO.IdType.MEDRXIV, "10.1101/2020.01.01.20000000");
    sampleIds.put(IdentifierVO.IdType.PMC, "PMC1234567");
    sampleIds.put(IdentifierVO.IdType.PMID, "12345678");
    sampleIds.put(IdentifierVO.IdType.PSYARXIV, "10.31234/osf.io/12345");
    sampleIds.put(IdentifierVO.IdType.RESEARCH_SQUARE, "10.21203/rs.3.rs-12345/v1");
    sampleIds.put(IdentifierVO.IdType.SOCARXIV, "10.31235/osf.io/12345");
    sampleIds.put(IdentifierVO.IdType.SSRN, "1234567");
    sampleIds.put(IdentifierVO.IdType.ZDB, "123456-7");

    for (Map.Entry<IdentifierVO.IdType, String> entry : sampleIds.entrySet()) {
      metadata.getIdentifiers().add(new IdentifierVO(entry.getKey(), entry.getValue()));
    }

    SourceVO src = new SourceVO();
    src.setTitle("Source Journal");
    src.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.DOI, "10.1000/source-doi"));
    metadata.getSources().add(src);

    item.setMetadata(metadata);

    SearchRetrieveRecordVO<ItemVersionVO> record = new SearchRetrieveRecordVO<>();
    record.setData(item);
    records.add(record);
    responseVO.setRecords(records);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    TransformerVoSource source = new TransformerVoSource(responseVO);
    TransformerStreamResult result = new TransformerStreamResult(baos);

    transformer.transform(source, result);

    String jsonOutput = baos.toString("UTF-8");
    assertNotNull(jsonOutput);

    ObjectMapper mapper = MapperFactory.getObjectMapper();
    JsonNode root = mapper.readTree(jsonOutput);
    JsonNode extIdArray = root.path("bulk").get(0).path("work").path("external-ids").path("external-id");

    Map<String, JsonNode> extIdMap = new HashMap<>();
    for (JsonNode n : extIdArray) {
      String key = n.path("external-id-type").asText() + ":" + n.path("external-id-value").asText();
      extIdMap.put(key, n);
    }

    // 1. ADS
    assertEquals("https://ui.adsabs.harvard.edu/abs/2020ApJ...890....1A",
        extIdMap.get("bibcode:2020ApJ...890....1A").path("external-id-url").path("value").asText());
    assertEquals("self", extIdMap.get("bibcode:2020ApJ...890....1A").path("external-id-relationship").asText());

    // 2. ARXIV
    assertEquals("https://arxiv.org/abs/2101.00001", extIdMap.get("arxiv:2101.00001").path("external-id-url").path("value").asText());

    // 3. BIORXIV
    assertEquals("https://doi.org/10.1101/2020.01.01.123456",
        extIdMap.get("other-id:10.1101/2020.01.01.123456").path("external-id-url").path("value").asText());

    // 4. CHEMRXIV
    assertEquals("https://doi.org/10.26434/chemrxiv.123456",
        extIdMap.get("other-id:10.26434/chemrxiv.123456").path("external-id-url").path("value").asText());

    // 5. DOI
    assertEquals("https://doi.org/10.1000/182", extIdMap.get("doi:10.1000/182").path("external-id-url").path("value").asText());

    // 6. EARTHARXIV
    assertEquals("https://doi.org/10.31223/osf.io/12345",
        extIdMap.get("other-id:10.31223/osf.io/12345").path("external-id-url").path("value").asText());

    // 7. EDARXIV
    assertEquals("https://doi.org/10.35542/osf.io/12345",
        extIdMap.get("other-id:10.35542/osf.io/12345").path("external-id-url").path("value").asText());

    // 8. ESS_OPEN_ARCHIVE
    assertEquals("https://doi.org/10.1002/essoar.12345",
        extIdMap.get("other-id:10.1002/essoar.12345").path("external-id-url").path("value").asText());

    // 9. ISI
    assertEquals("https://www.webofscience.com/wos/woscc/full-record/WOS:000123456700001",
        extIdMap.get("wosuid:000123456700001").path("external-id-url").path("value").asText());

    // 10. MEDRXIV
    assertEquals("https://doi.org/10.1101/2020.01.01.20000000",
        extIdMap.get("other-id:10.1101/2020.01.01.20000000").path("external-id-url").path("value").asText());

    // 11. PMC
    assertEquals("https://www.ncbi.nlm.nih.gov/pmc/articles/PMC1234567",
        extIdMap.get("pmc:PMC1234567").path("external-id-url").path("value").asText());

    // 12. PMID
    assertEquals("https://pubmed.ncbi.nlm.nih.gov/12345678", extIdMap.get("pmid:12345678").path("external-id-url").path("value").asText());

    // 13. PSYARXIV
    assertEquals("https://doi.org/10.31234/osf.io/12345",
        extIdMap.get("other-id:10.31234/osf.io/12345").path("external-id-url").path("value").asText());

    // 14. RESEARCH_SQUARE
    assertEquals("https://doi.org/10.21203/rs.3.rs-12345/v1",
        extIdMap.get("other-id:10.21203/rs.3.rs-12345/v1").path("external-id-url").path("value").asText());

    // 15. SOCARXIV
    assertEquals("https://doi.org/10.31235/osf.io/12345",
        extIdMap.get("other-id:10.31235/osf.io/12345").path("external-id-url").path("value").asText());

    // 16. SSRN
    assertEquals("https://ssrn.com/abstract=1234567", extIdMap.get("ssrn:1234567").path("external-id-url").path("value").asText());

    // 17. ZDB
    assertEquals("https://ld.zdb-services.de/resource/123456-7",
        extIdMap.get("other-id:123456-7").path("external-id-url").path("value").asText());

    // 18. Item-ID (ohne Version!) -> source-work-id
    assertEquals("https://pure.mpg.de/view/item_12345",
        extIdMap.get("source-work-id:item_12345").path("external-id-url").path("value").asText());
    assertEquals("self", extIdMap.get("source-work-id:item_12345").path("external-id-relationship").asText());

    // 19. Object handle -> handle
    assertEquals("https://hdl.handle.net/11858/00-001M-0000-000E-B8E5-7",
        extIdMap.get("handle:11858/00-001M-0000-000E-B8E5-7").path("external-id-url").path("value").asText());
    assertEquals("self", extIdMap.get("handle:11858/00-001M-0000-000E-B8E5-7").path("external-id-relationship").asText());

    // Source identifier -> relationship = part-of
    assertEquals("https://doi.org/10.1000/source-doi",
        extIdMap.get("doi:10.1000/source-doi").path("external-id-url").path("value").asText());
    assertEquals("part-of", extIdMap.get("doi:10.1000/source-doi").path("external-id-relationship").asText());
  }
}
