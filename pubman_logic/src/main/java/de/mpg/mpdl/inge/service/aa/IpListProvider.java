package de.mpg.mpdl.inge.service.aa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mpg.mpdl.inge.util.NetworkUtils;


public interface IpListProvider {

  public IpRange get(String id);

  public Collection<IpRange> getAll();

  public IpRange getMatch(String adress);

  public class IpRange {
    String name;
    String id;
    private List<String> ipRanges = new ArrayList<String>();


    public IpRange(String id, String name, List<String> ipRanges) {
      super();
      this.name = name;
      this.id = id;
      this.setIpRanges(ipRanges);
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public List<String> getIpRanges() {
      return ipRanges;
    }

    public void setIpRanges(List<String> ipRanges) {
      this.ipRanges = ipRanges;
    }


    public boolean matches(String adress) {
      for (String ipPattern : getIpRanges()) {
        if (NetworkUtils.checkIPMatching(ipPattern, adress)) {
          return true;
        }
      }
      return false;
    }



  }

}
