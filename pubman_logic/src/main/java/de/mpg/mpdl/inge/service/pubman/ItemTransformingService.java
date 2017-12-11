package de.mpg.mpdl.inge.service.pubman;

import java.util.List;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;

public interface ItemTransformingService {

  public byte[] getOutputForExport(ExportFormatVO exportFormat, List<PubItemVO> pubItemVOList) throws TechnicalException;

  public byte[] getOutputForExport(ExportFormatVO exportFormat, String itemList) throws IngeTechnicalException;

  public TransformerFactory.FORMAT[] getAllSourceFormatsFor(TransformerFactory.FORMAT target);

  public TransformerFactory.FORMAT[] getAllTargetFormatsFor(TransformerFactory.FORMAT source);

  public String transformFromTo(TransformerFactory.FORMAT source, TransformerFactory.FORMAT target, String xml)
      throws TransformationException;

  public boolean isTransformationExisting(TransformerFactory.FORMAT sourceFormat, TransformerFactory.FORMAT targetFormat);

  public String transformPubItemTo(TransformerFactory.FORMAT target, PubItemVO item) throws TransformationException;

}
