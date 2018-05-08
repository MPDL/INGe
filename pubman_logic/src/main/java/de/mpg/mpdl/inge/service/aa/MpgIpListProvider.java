package de.mpg.mpdl.inge.service.aa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.springframework.stereotype.Component;

import de.mpg.mpdl.inge.util.CSVUtils;
import de.mpg.mpdl.inge.util.NetworkUtils;
import de.mpg.mpdl.inge.util.ResourceUtil;

@Component
public class MpgIpListProvider implements IpListProvider {

  private final Map<String, IpRange> ipRangeMap = new HashMap<>();

  public MpgIpListProvider() {
    try (
        Scanner scanner = new Scanner(ResourceUtil.getResourceAsStream("expoipra_all.txt", AuthorizationService.class.getClassLoader()));) {

      scanner.nextLine();
      while (scanner.hasNext()) {
        List<String> line = CSVUtils.parseLine(scanner.nextLine(), ';');
        String id = line.get(2);
        if (ipRangeMap.containsKey(id)) {
          ipRangeMap.get(id).getIpRanges().add(line.get(1));
        } else {
          List<String> ipList = new ArrayList<>();
          ipList.add(line.get(1));
          ipRangeMap.put(id, new IpListProvider.IpRange(id, line.get(4) + ", " + line.get(3), ipList));
        }

      }

    } catch (Exception e) {
      throw new RuntimeException("Problem with parsing ip list file.", e);
    }
  }

  @Override
  public IpRange get(String id) {
    return ipRangeMap.get(id);
  }

  @Override
  public Collection<IpRange> getAll() {
    return ipRangeMap.values();
  }

  @Override
  public IpRange getMatch(String adress) {
    for (IpRange ipRange : ipRangeMap.values()) {
      for (String ipPattern : ipRange.getIpRanges()) {
        if (NetworkUtils.checkIPMatching(ipPattern, adress)) {
          return ipRange;
        }
      }
    }

    return null;
  }


}
