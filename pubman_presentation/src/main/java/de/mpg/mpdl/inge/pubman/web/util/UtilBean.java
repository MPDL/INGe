package de.mpg.mpdl.inge.pubman.web.util;

import java.util.List;

import de.mpg.mpdl.inge.util.PropertyReader;
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.bean.RequestScoped;

@ManagedBean(name = "UtilBean")
@RequestScoped
public class UtilBean {
  public void removeFromList(List<?> list, int index) {
    list.remove(index);
  }

  public String getPidWithoutPrefix(String pidWithPrefix) {
    if (null != pidWithPrefix && pidWithPrefix.startsWith(PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_SHORT))) {
      return pidWithPrefix.substring(PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_SHORT).length());
    } else {
      return pidWithPrefix;
    }
  }

}
