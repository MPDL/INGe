package de.mpg.mpdl.inge.service.listener;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.exception.WriteLimitReachedException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Office;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import de.mpg.mpdl.inge.es.dao.AttachmentDocument;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;

class FulltextAttachmentDocumentExtractor {

  static final int INDEXED_CHARS_LIMIT = 100000;

  AttachmentDocument extract(FileDbVO fileVO, InputStream inputStream) throws IOException, SAXException, TikaException {
    Metadata metadata = new Metadata();
    String resourceName = resolveResourceName(fileVO);
    if (resourceName != null) {
      metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, resourceName);
    }
    if (fileVO.getMimeType() != null && !fileVO.getMimeType().isBlank()) {
      metadata.set(Metadata.CONTENT_TYPE, fileVO.getMimeType());
    }

    BodyContentHandler handler = new BodyContentHandler(INDEXED_CHARS_LIMIT);
    AutoDetectParser parser = new AutoDetectParser();
    ParseContext context = new ParseContext();

    try (TikaInputStream tikaInputStream = TikaInputStream.get(inputStream)) {
      try {
        parser.parse(tikaInputStream, handler, metadata, context);
      } catch (SAXException e) {
        if (!WriteLimitReachedException.isWriteLimitReached(e)) {
          throw e;
        }
      }
    }

    return toAttachmentDocument(fileVO, metadata, handler.toString());
  }

  AttachmentDocument toAttachmentDocument(FileDbVO fileVO, Metadata metadata, String extractedContent) {
    AttachmentDocument attachmentDocument = new AttachmentDocument();
    attachmentDocument.setAuthor(firstNonBlank(metadata.get(TikaCoreProperties.CREATOR), metadata.get("Author")));
    attachmentDocument.setContent(extractedContent);
    attachmentDocument.setContentLength((long) extractedContent.length());
    attachmentDocument.setContentType(firstNonBlank(metadata.get(Metadata.CONTENT_TYPE), fileVO.getMimeType()));
    attachmentDocument
        .setDate(firstNonBlank(metadata.get(TikaCoreProperties.CREATED), metadata.get(TikaCoreProperties.MODIFIED), metadata.get("date")));
    attachmentDocument.setFormat(firstNonBlank(metadata.get(TikaCoreProperties.FORMAT), metadata.get("format")));
    attachmentDocument.setKeywords(firstNonBlank(metadata.get(Office.KEYWORDS), metadata.get("Keywords"), metadata.get("keywords")));
    attachmentDocument.setLanguage(firstNonBlank(metadata.get(TikaCoreProperties.TIKA_DETECTED_LANGUAGE),
        metadata.get(TikaCoreProperties.LANGUAGE), metadata.get("language")));
    attachmentDocument.setTitle(firstNonBlank(metadata.get(TikaCoreProperties.TITLE), metadata.get("title")));
    return attachmentDocument;
  }

  private String resolveResourceName(FileDbVO fileVO) {
    if (fileVO.getName() != null && !fileVO.getName().isBlank()) {
      return fileVO.getName();
    }
    if (fileVO.getObjectId() != null && !fileVO.getObjectId().isBlank()) {
      return fileVO.getObjectId();
    }
    return null;
  }

  private String firstNonBlank(String... values) {
    for (String value : values) {
      if (value != null && !value.isBlank()) {
        return value;
      }
    }
    return null;
  }
}
