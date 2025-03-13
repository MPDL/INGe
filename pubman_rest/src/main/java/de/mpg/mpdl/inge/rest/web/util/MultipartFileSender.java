package de.mpg.mpdl.inge.rest.web.util;

import de.mpg.mpdl.inge.filestorage.Range;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.pubman.impl.FileVOWrapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by kevin on 10/02/15. See full code here :
 * https://github.com/davinkevin/Podcast-Server/blob/d927d9b8cb9ea1268af74316cd20b7192ca92da7/src/main/java/lan/dk/podcastserver/utils/multipart/MultipartFileSender.java
 */
public class MultipartFileSender {

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());

  // ..bytes = 20KB.
  private static final long DEFAULT_EXPIRE_TIME = 604800000L; // ..ms = 1 week.
  private static final String MULTIPART_BOUNDARY = "MULTIPART_BYTERANGES";

  //Path filepath;
  HttpServletRequest request;
  HttpServletResponse response;
  FileVOWrapper fileVOWrapper;
  String disposition = "inline";

  public MultipartFileSender() {}


  public static MultipartFileSender fromFileVOWrapper(FileVOWrapper fileVOWrapper) {
    return new MultipartFileSender().setFileVOWrapper(fileVOWrapper);
  }

  private MultipartFileSender setFileVOWrapper(FileVOWrapper fileVOWrapper) {
    this.fileVOWrapper = fileVOWrapper;
    return this;
  }

  public MultipartFileSender with(HttpServletRequest httpRequest) {
    request = httpRequest;
    return this;
  }

  public MultipartFileSender with(HttpServletResponse httpResponse) {
    response = httpResponse;
    return this;
  }

  public MultipartFileSender with(String disposition) {
    this.disposition = disposition;
    return this;
  }

  public void serveResource() throws IOException, IngeTechnicalException {
    if (response == null || request == null) {
      return;
    }


    Long length = fileVOWrapper.getFileVO().getSize();
    String fileName = fileVOWrapper.getFileVO().getName();
    Date lastModifiedObj = fileVOWrapper.getFileVO().getLastModificationDate();


    String contentType = fileVOWrapper.getFileVO().getMimeType();

    // Validate request headers for caching ---------------------------------------------------

    // If-None-Match header should contain "*" or ETag. If so, then return 304.
    String ifNoneMatch = request.getHeader("If-None-Match");
    if (ifNoneMatch != null && HttpUtils.matches(ifNoneMatch, fileName)) {
      response.setHeader("ETag", fileName); // Required in 304.
      response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
      return;
    }

    // If-Modified-Since header should be greater than LastModified. If so, then return 304.
    // This header is ignored if any If-None-Match header is specified.
    /*
    long ifModifiedSince = request.getDateHeader("If-Modified-Since");
    if (ifNoneMatch == null && ifModifiedSince != -1 && ifModifiedSince + 1000 > lastModified) {
      response.setHeader("ETag", fileName); // Required in 304.
      response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
      return;
    }
    */

    // Validate request headers for resume ----------------------------------------------------

    // If-Match header should contain "*" or ETag. If not, then return 412.
    String ifMatch = request.getHeader("If-Match");
    if (ifMatch != null && !HttpUtils.matches(ifMatch, fileName)) {
      response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
      return;
    }

    // If-Unmodified-Since header should be greater than LastModified. If not, then return 412.
    /*
    long ifUnmodifiedSince = request.getDateHeader("If-Unmodified-Since");
    if (ifUnmodifiedSince != -1 && ifUnmodifiedSince + 1000 <= lastModified) {
      response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
      return;
    }
    */

    // Validate and process range -------------------------------------------------------------

    // Prepare some variables. The full Range represents the complete file.
    Range full = new Range(0, length - 1, length);
    List<Range> ranges = new ArrayList<>();

    // Validate and process Range and If-Range headers.
    String range = request.getHeader("Range");
    if (range != null) {

      // Range header should match format "bytes=n-n,n-n,n-n...". If not, then return 416.
      if (!range.matches("^bytes=\\d*-\\d*(,\\d*-\\d*)*$")) {
        response.setHeader("Content-Range", "bytes */" + length); // Required in 416.
        response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
        return;
      }

      String ifRange = request.getHeader("If-Range");
      if (ifRange != null && !ifRange.equals(fileName)) {
        try {
          long ifRangeTime = request.getDateHeader("If-Range"); // Throws IAE if invalid.
          if (ifRangeTime != -1) {
            ranges.add(full);
          }
        } catch (IllegalArgumentException ignore) {
          ranges.add(full);
        }
      }

      // If any valid If-Range header, then process each part of byte range.
      if (ranges.isEmpty()) {
        for (String part : range.substring(6).split(",")) {
          // Assuming a file with length of 100, the following examples returns bytes at:
          // 50-80 (50 to 80), 40- (40 to length=100), -20 (length-20=80 to length=100).
          long start = Range.sublong(part, 0, part.indexOf("-"));
          long end = Range.sublong(part, part.indexOf("-") + 1, part.length());

          if (start == -1) {
            start = length - end;
            end = length - 1;
          } else if (end == -1 || end > length - 1) {
            end = length - 1;
          }

          // Check if Range is syntactically valid. If not, then return 416.
          if (start > end) {
            response.setHeader("Content-Range", "bytes */" + length); // Required in 416.
            response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            return;
          }

          // Add range.
          ranges.add(new Range(start, end, length));
        }
      }
    }

    // Prepare and initialize response --------------------------------------------------------

    // Get content type by file name and set content disposition.
    String disposition = this.disposition;

    if (contentType == null) {
      contentType = "application/octet-stream";
    }

    logger.debug("Content-Type : {}", contentType);
    // Initialize response.
    //response.reset();
    response.setBufferSize(Range.DEFAULT_BUFFER_SIZE);
    response.setHeader("Content-Type", contentType);
    response.setHeader("Content-Disposition", disposition + ";filename=\"" + fileName + "\"");


    response.setHeader("Content-Disposition",
        disposition + ";filename*=UTF-8''" + URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20"));

    logger.debug("Content-Disposition : {}", disposition);
    response.setHeader("Accept-Ranges", "bytes");
    response.setHeader("ETag", fileVOWrapper.getFileVO().getChecksum());
    //response.setDateHeader("Last-Modified", lastModified);
    //response.setDateHeader("Expires", System.currentTimeMillis() + DEFAULT_EXPIRE_TIME);

    // Send requested file (part(s)) to client ------------------------------------------------

    // Prepare streams.

    OutputStream output = response.getOutputStream();

    if (ranges == null || ranges.isEmpty()) {
      logger.info("Return full file");
      response.setContentType(contentType);
      response.setHeader("Content-Length", String.valueOf(fileVOWrapper.getFileVO().getSize()));
      response.setStatus(HttpServletResponse.SC_OK); // 200.
      fileVOWrapper.readFile(output);
    }
    if (ranges.size() == 1) {
      // Return single part of file.
      Range r = ranges.get(0);
      logger.info("Return 1 part of file : from ({}) to ({})", r.getStart(), r.getEnd());
      response.setContentType(contentType);
      response.setHeader("Content-Range", "bytes " + r.getStart() + "-" + r.getEnd() + "/" + r.getTotal());
      response.setHeader("Content-Length", String.valueOf(r.getLength()));
      response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.
      fileVOWrapper.readFile(output, r);
      // Copy single part range.
      //Range.copy(input, output, length, r.start, r.length);

    } else {

      // Return multiple parts of file.
      response.setContentType("multipart/byteranges; boundary=" + MULTIPART_BOUNDARY);
      response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

      // Cast back to ServletOutputStream to get the easy println methods.
      ServletOutputStream sos = (ServletOutputStream) output;

      // Copy multi part range.
      for (Range r : ranges) {
        logger.info("Return multi part of file : from ({}) to ({})", r.getStart(), r.getEnd());
        // Add multipart boundary and header fields for every range.
        sos.println();
        sos.println("--" + MULTIPART_BOUNDARY);
        sos.println("Content-Type: " + contentType);
        sos.println("Content-Range: bytes " + r.getStart() + "-" + r.getEnd() + "/" + r.getTotal());
        fileVOWrapper.readFile(output, r);
        // Copy single part range of multi part range.
        //Range.copy(input, output, length, r.start, r.length);
      }

      // End with multipart boundary.
      sos.println();
      sos.println("--" + MULTIPART_BOUNDARY + "--");
    }


  }


  private static class HttpUtils {

    /**
     * Returns true if the given accept header accepts the given value.
     * 
     * @param acceptHeader The accept header.
     * @param toAccept The value to be accepted.
     * @return True if the given accept header accepts the given value.
     */
    public static boolean accepts(String acceptHeader, String toAccept) {
      String[] acceptValues = acceptHeader.split("\\s*(,|;)\\s*");
      Arrays.sort(acceptValues);

      return Arrays.binarySearch(acceptValues, toAccept) > -1 || Arrays.binarySearch(acceptValues, toAccept.replaceAll("/.*$", "/*")) > -1
          || Arrays.binarySearch(acceptValues, "*/*") > -1;
    }

    /**
     * Returns true if the given match header matches the given value.
     * 
     * @param matchHeader The match header.
     * @param toMatch The value to be matched.
     * @return True if the given match header matches the given value.
     */
    public static boolean matches(String matchHeader, String toMatch) {
      String[] matchValues = matchHeader.split("\\s*,\\s*");
      Arrays.sort(matchValues);
      return Arrays.binarySearch(matchValues, toMatch) > -1 || Arrays.binarySearch(matchValues, "*") > -1;
    }
  }
}
