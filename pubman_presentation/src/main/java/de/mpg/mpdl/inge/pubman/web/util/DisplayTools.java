package de.mpg.mpdl.inge.pubman.web.util;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.mpdl.inge.util.PropertyReader;

public class DisplayTools {
  //  public static final IdType[] ID_TYPES_TO_DISPLAY =
  //      new IdType[] {IdType.CONE, IdType.URI, IdType.ISBN, IdType.ISSN, IdType.DOI, IdType.URN, IdType.PII, IdType.EDOC, IdType.ISI,
  //          IdType.PND, IdType.ZDB, IdType.PMID, IdType.ARXIV, IdType.PMC, IdType.BMC, IdType.BIBTEX_CITEKEY, IdType.REPORT_NR, IdType.SSRN,
  //          IdType.PATENT_NR, IdType.PATENT_APPLICATION_NR, IdType.PATENT_PUBLICATION_NR, IdType.OTHER};

  public static IdType[] getIdTypesToDisplay() {
    List<IdType> idTypes = new ArrayList<>();
    String[] sIdTypes = PropertyReader.getProperty(PropertyReader.INGE_ID_TYPES_TO_DISPLAY).split(",");

    for (String sIdType : sIdTypes) {
      idTypes.add(IdentifierVO.IdType.valueOf(sIdType));
    }

    return idTypes.toArray(new IdType[0]);
  }

}
