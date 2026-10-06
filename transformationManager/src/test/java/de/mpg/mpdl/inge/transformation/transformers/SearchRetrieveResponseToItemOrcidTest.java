package de.mpg.mpdl.inge.transformation.transformers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerVoSource;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class SearchRetrieveResponseToItemOrcidTest {

  @Test
  public void testContributorRolesMapping() throws Exception {
    SearchRetrieveResponseToItemOrcid transformer = new SearchRetrieveResponseToItemOrcid();

    SearchRetrieveResponseVO<ItemVersionVO> responseVO = new SearchRetrieveResponseVO<>();
    List<SearchRetrieveRecordVO<ItemVersionVO>> records = new ArrayList<>();

    ItemVersionVO item = new ItemVersionVO();
    MdsPublicationVO metadata = new MdsPublicationVO();
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
}
