package de.mpg.mpdl.inge.service.pubman;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;

public interface ReindexListener {

  void reindexListener(String id) throws IngeTechnicalException;

}
