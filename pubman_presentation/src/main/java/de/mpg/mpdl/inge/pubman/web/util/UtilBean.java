package de.mpg.mpdl.inge.pubman.web.util;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "UtilBean")
@RequestScoped
@SuppressWarnings("serial")
public class UtilBean {

  
  public String getPidWithoutPrefix(String pidWithPrefix)
  {
    if (pidWithPrefix!=null && pidWithPrefix.startsWith("hdl:")) {
      return pidWithPrefix.substring(4);
    } else {
      return pidWithPrefix;
    }
  }
  
}
