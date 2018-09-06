package de.mpg.mpdl.inge.service.aa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.inge.util.CSVUtils;
import de.mpg.mpdl.inge.util.NetworkUtils;
import de.mpg.mpdl.inge.util.ResourceUtil;

@Component
public class MpgIpListProvider implements IpListProvider {

  private static final Logger logger = LogManager.getLogger(MpgIpListProvider.class);
  private Map<String, IpRange> ipRangeMap = new HashMap<>();

  public MpgIpListProvider() {
    init();
  }


  @Scheduled(cron = "0 0 0/1 * * ?")
  private void init() {
    logger.info("(re-)initializing IP List");
    try (Scanner scanner = new Scanner(ResourceUtil.getResourceAsStream("expoipra_all.txt", AuthorizationService.class.getClassLoader()))) {

      Map<String, IpRange> ipRangeMap = new HashMap<>();
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

      this.ipRangeMap = ipRangeMap;
      logger.info("Successfully set IP List with " + ipRangeMap.size() + " entries");
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
