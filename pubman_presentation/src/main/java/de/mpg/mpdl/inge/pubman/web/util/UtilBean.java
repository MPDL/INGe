package de.mpg.mpdl.inge.pubman.web.util;

import java.util.List;

import jakarta.faces.bean.ManagedBean;
import jakarta.faces.bean.RequestScoped;

import de.mpg.mpdl.inge.util.PropertyReader;

@ManagedBean(name = "UtilBean")
@RequestScoped
public class UtilBean {
  public void removeFromList(List<?> list, int index) {
    list.remove(index);
  }

  public String getPidWithoutPrefix(String pidWithPrefix) {
    if (pidWithPrefix != null && pidWithPrefix.startsWith(PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_SHORT))) {
      return pidWithPrefix.substring(PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_SHORT).length());
    } else {
      return pidWithPrefix;
    }
  }

}
