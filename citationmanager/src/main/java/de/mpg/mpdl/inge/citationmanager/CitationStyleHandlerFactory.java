package de.mpg.mpdl.inge.citationmanager;

import de.mpg.mpdl.inge.citationmanager.impl.CitationStyleExecutor;

public class CitationStyleHandlerFactory {

  private static CitationStyleHandler instance = new CitationStyleExecutor();

  public static CitationStyleHandler getCitationStyleHandler() {
    if (instance == null)
      instance = new CitationStyleExecutor();
    return instance;
  }

}
