package de.mpg.mpdl.inge.service.pubman;

import java.util.List;
import java.util.Map;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.service.util.SearchUtils;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;

public interface ItemTransformingService {

  public byte[] getOutputForExport(ExportFormatVO exportFormat, SearchRetrieveResponseVO<ItemVersionVO> srr) throws IngeTechnicalException;

  public byte[] getOutputForExport(ExportFormatVO exportFormat, List<ItemVersionVO> pubItemVOList) throws IngeTechnicalException;

  public TransformerFactory.FORMAT[] getAllSourceFormatsFor(TransformerFactory.FORMAT target);

  public TransformerFactory.FORMAT[] getAllTargetFormatsFor(TransformerFactory.FORMAT source);

  public String transformFromTo(TransformerFactory.FORMAT source, TransformerFactory.FORMAT target, String xml,
      Map<String, String> configuration) throws TransformationException;

  public boolean isTransformationExisting(TransformerFactory.FORMAT sourceFormat, TransformerFactory.FORMAT targetFormat);

  public String transformPubItemTo(TransformerFactory.FORMAT target, PubItemVO item) throws TransformationException;

}
