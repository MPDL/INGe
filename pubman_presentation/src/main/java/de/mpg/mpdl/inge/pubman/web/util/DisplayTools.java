package de.mpg.mpdl.inge.pubman.web.util;

import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;

public class DisplayTools {
  public static final IdType[] ID_TYPES_TO_DISPLAY =
      new IdType[] {IdType.CONE, IdType.URI, IdType.ISBN, IdType.ISSN, IdType.DOI, IdType.URN, IdType.PII, IdType.EDOC, IdType.ESCIDOC,
          IdType.ISI, IdType.PND, IdType.ZDB, IdType.PMID, IdType.ARXIV, IdType.PMC, IdType.BMC, IdType.BIBTEX_CITEKEY, IdType.REPORT_NR,
          IdType.SSRN, IdType.PATENT_NR, IdType.PATENT_APPLICATION_NR, IdType.PATENT_PUBLICATION_NR, IdType.OTHER};
}
