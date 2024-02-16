package de.mpg.mpdl.inge.es.dao;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;

public interface PubItemDaoEs extends GenericDaoEs<ItemVersionVO> {

  String createFulltext(String itemId, String fileId, byte[] file) throws IngeTechnicalException;

}
