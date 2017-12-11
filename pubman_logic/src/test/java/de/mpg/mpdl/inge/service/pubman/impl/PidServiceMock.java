package de.mpg.mpdl.inge.service.pubman.impl;

import java.net.URI;

import de.mpg.mpdl.inge.model.valueobjects.PidServiceResponseVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.PidService;

public class PidServiceMock implements PidService {

  @Override
  public PidServiceResponseVO createPid(URI url) throws IngeApplicationException, TechnicalException {
    PidServiceResponseVO pidServiceResponseVO = new PidServiceResponseVO();
    int idx = url.getPath().indexOf("item_");
    pidServiceResponseVO.setUrl(url.toString());
    pidServiceResponseVO.setIdentifier(url.getPath().substring(idx));

    return pidServiceResponseVO;
  }
}
