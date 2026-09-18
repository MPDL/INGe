package de.mpg.mpdl.inge.service.listener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Office;
import org.apache.tika.metadata.TikaCoreProperties;
import org.junit.Test;

import de.mpg.mpdl.inge.es.dao.AttachmentDocument;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;

public class FulltextAttachmentDocumentExtractorTest {

  private final FulltextAttachmentDocumentExtractor extractor = new FulltextAttachmentDocumentExtractor();

  @Test
  public void toAttachmentDocumentShouldMapAttachmentFields() {
    FileDbVO fileVO = new FileDbVO();
    fileVO.setMimeType("application/pdf");

    Metadata metadata = new Metadata();
    metadata.set(TikaCoreProperties.CREATOR, "Author");
    metadata.set(TikaCoreProperties.CREATED, "2024-01-02T03:04:05Z");
    metadata.set(TikaCoreProperties.FORMAT, "application/pdf");
    metadata.set(Office.KEYWORDS, "one,two");
    metadata.set(TikaCoreProperties.TIKA_DETECTED_LANGUAGE, "en");
    metadata.set(TikaCoreProperties.TITLE, "Title");

    AttachmentDocument attachmentDocument = extractor.toAttachmentDocument(fileVO, metadata, "Body");

    assertEquals("Author", attachmentDocument.getAuthor());
    assertEquals("Body", attachmentDocument.getContent());
    assertEquals(Long.valueOf(4L), attachmentDocument.getContentLength());
    assertEquals("application/pdf", attachmentDocument.getContentType());
    assertEquals("2024-01-02T03:04:05Z", attachmentDocument.getDate());
    assertEquals("application/pdf", attachmentDocument.getFormat());
    assertEquals("one,two", attachmentDocument.getKeywords());
    assertEquals("en", attachmentDocument.getLanguage());
    assertEquals("Title", attachmentDocument.getTitle());
  }

  @Test
  public void extractShouldHonorIndexedCharsLimit() throws Exception {
    FileDbVO fileVO = new FileDbVO();
    fileVO.setName("sample.txt");
    fileVO.setMimeType("text/plain");

    String content = "a".repeat(FulltextAttachmentDocumentExtractor.INDEXED_CHARS_LIMIT + 500);

    AttachmentDocument attachmentDocument = extractor.extract(fileVO, new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));

    assertEquals(FulltextAttachmentDocumentExtractor.INDEXED_CHARS_LIMIT, attachmentDocument.getContent().length());
    assertEquals(Long.valueOf(FulltextAttachmentDocumentExtractor.INDEXED_CHARS_LIMIT), attachmentDocument.getContentLength());
    assertTrue(attachmentDocument.getContentType().startsWith("text/plain"));
  }
}
