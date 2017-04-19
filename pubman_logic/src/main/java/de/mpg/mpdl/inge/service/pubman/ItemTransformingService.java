package de.mpg.mpdl.inge.service.pubman;

import java.util.List;

import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.services.IngeServiceException;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;

public interface ItemTransformingService {

  public byte[] getOutputForExport(ExportFormatVO exportFormat, List<PubItemVO> pubItemVOList)
      throws TechnicalException;

  public byte[] getOutputForExport(ExportFormatVO exportFormat, String itemList)
      throws IngeServiceException;

  public FORMAT[] getAllSourceFormatsFor(FORMAT target);

  public FORMAT[] getAllTargetFormatsFor(FORMAT source);

  // EditItemBean
  public void parseCreatorString(String creatorString, List<OrganizationVO> orgs, boolean overwrite);

  public String transformFromTo(FORMAT source, FORMAT target, String xml)
      throws TransformationException;


}
