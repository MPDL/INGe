package de.mpg.mpdl.inge.service.pubman;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;

public interface ReindexListener {

  public void reindexListener(String id) throws IngeTechnicalException;

}
