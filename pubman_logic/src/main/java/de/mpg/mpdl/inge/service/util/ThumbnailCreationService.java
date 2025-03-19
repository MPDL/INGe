package de.mpg.mpdl.inge.service.util;

import de.mpg.mpdl.inge.filestorage.FileStorageInterface;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import net.coobird.thumbnailator.Thumbnailator;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ThumbnailCreationService {

  @Autowired
  @Qualifier("fileSystemServiceBean")
  private FileStorageInterface fsi;

  public void createThumbnail(FileDbVO fileVO) throws IngeTechnicalException {

    if ("application/pdf".equals(fileVO.getMimeType())) {
      try (InputStream is = fsi.readFile(fileVO.getLocalFileIdentifier())) {
        PDDocument pdfDoc = PDDocument.load(is, MemoryUsageSetting.setupTempFileOnly());
        PDFRenderer pdfRenderer = new PDFRenderer(pdfDoc);
        // note that the page number parameter is zero based
        BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 72, ImageType.RGB);
        // suffix in filename will be used as the file format
        Path tmpFile = Files.createTempFile(fileVO.getObjectId(), ".thumbnail.jpg");

        Thumbnails.of(bim).size(400, 400).outputFormat("jpg").toFile(tmpFile.toFile());
        //ImageIO.write(bim, "png", tmpFile.toFile());
        //ImageIOUtil.writeImage(bim, tmpFile.toString(), 72);

        String thumbFilePath = createThumbnailFileIdentifier(fileVO.getLocalFileIdentifier());

        //delete old thumbnail
        fsi.deleteFile(thumbFilePath);

        try (InputStream thumbfile = Files.newInputStream(tmpFile)) {
          fsi.createFile(thumbfile, thumbFilePath);
        }
        Files.delete(tmpFile);


      } catch (Exception e) {
        throw new IngeTechnicalException(
            "Could not generate thumbnail for file " + fileVO.getObjectId() + " Path: " + fileVO.getLocalFileIdentifier(), e);
      } finally {

      }

    }

  }

  public static String createThumbnailFileIdentifier(String fileIdentifier) {
    return fileIdentifier + ".thumbnail.jpg";
  }

  public static void main(String[] args) throws Exception {
    Path f = Path.of("/Users/name/Downloads/test-data/SamplePDF.pdf");
    try (InputStream is = Files.newInputStream(f)) {
      PDDocument pdfDoc = PDDocument.load(is, MemoryUsageSetting.setupTempFileOnly());
      PDFRenderer pdfRenderer = new PDFRenderer(pdfDoc);
      // note that the page number parameter is zero based
      BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 72, ImageType.RGB);
      // suffix in filename will be used as the file format
      Path tmpFile = Files.createTempFile(Path.of("/Users/name/Downloads/test-data"), "samplePdf", ".thumbnail.jpg");
      //Files.cre

      Thumbnails.of(bim).size(400, 400).outputFormat("jpg").toFile(tmpFile.toFile());
      //ImageIO.write(bim, "png", tmpFile.toFile());
      //ImageIOUtil.writeImage(bim, tmpFile.toString(), 72);



    }
  }

}
