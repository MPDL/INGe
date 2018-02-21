package de.mpg.mpdl.inge.pubman.web.util;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean(name = "UtilBean")
@RequestScoped
@SuppressWarnings("serial")
public class UtilBean {



  public void removeFromList(List<?> list, int index) {
    list.remove(index);
  }

  public String getPidWithoutPrefix(String pidWithPrefix) {
    if (pidWithPrefix != null && pidWithPrefix.startsWith("hdl:")) {
      return pidWithPrefix.substring(4);
    } else {
      return pidWithPrefix;
    }
  }

}
