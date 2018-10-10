package de.mpg.mpdl.inge.service.aa;

import java.net.HttpURLConnection;
import java.net.URL;
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
import de.mpg.mpdl.inge.util.PropertyReader;

@Component
public class MpgIpListProvider implements IpListProvider {

  private static final Logger logger = LogManager.getLogger(MpgIpListProvider.class);
  private Map<String, IpRange> ipRangeMap = new HashMap<>();

  public MpgIpListProvider() {
    init();
  }


  @Scheduled(cron = "0 0 2 * * ?")
  private void init() {
    logger.info("CRON: (re-)initializing IP List");
    HttpURLConnection conn = null;

    try {
      URL url = new URL(PropertyReader.getProperty(PropertyReader.INGE_AUTH_MPG_IP_LIST_URL));

      conn = (HttpURLConnection) url.openConnection();
      conn.setReadTimeout(60 * 1000);


      try (Scanner scanner = new Scanner(conn.getInputStream())) {

        Map<String, IpRange> ipRangeMap = new HashMap<>();
        //Add entry for whole MPG
        ipRangeMap.put("mpg", new IpListProvider.IpRange("mpg", " Max Planck Society (every institute)", new ArrayList<>()));
        scanner.nextLine();
        while (scanner.hasNext()) {
          List<String> line = CSVUtils.parseLine(scanner.nextLine(), ';');
          String id = line.get(2);
          if (id.matches("\\d+")) {
            //Add every range for whole MPG
            ipRangeMap.get("mpg").getIpRanges().add(line.get(1));

            if (ipRangeMap.containsKey(id)) {
              ipRangeMap.get(id).getIpRanges().add(line.get(1));
            } else {
              List<String> ipList = new ArrayList<>();
              ipList.add(line.get(1));
              ipRangeMap.put(id, new IpListProvider.IpRange(id, line.get(4) + ", " + line.get(3), ipList));
            }
          } else {
            logger.warn("Ignoring entry in ip list with id '" + id + "', as it is no valid id");
          }


        }
        this.ipRangeMap = ipRangeMap;
        logger.info("CRON: Successfully set IP List with " + ipRangeMap.size() + " entries");

      }



    } catch (Exception e) {
      logger.error("Problem with parsing ip list file", e);
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
      if (ipRangeMap == null || ipRangeMap.isEmpty()) {
        logger.warn("No IP RANGES found! - List is empty");
      }
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
      if (!"mpg".equals(ipRange.getId())) {
        for (String ipPattern : ipRange.getIpRanges()) {
          if (NetworkUtils.checkIPMatching(ipPattern, adress)) {
            return ipRange;
          }
        }
      }
    }

    return null;
  }


}
