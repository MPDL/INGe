package de.mpg.mpdl.inge.service.aa;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;

import de.mpg.mpdl.inge.util.PropertyReader;

public class MpgJsonIpListProvider implements IpListProvider {

  private static final Logger logger = LogManager.getLogger(MpgIpListProvider.class);
  private static Map<String, IpRange> ipRangeMap = new HashMap<>();
  
  public static void main (String[] args) {
    init();
  }

  @Scheduled(cron = "0 0 2 * * ?")
  private static void init() {
//    if (PropertyReader.INGE_AUTH_MPG_IP_LIST_USE.equalsIgnoreCase("true")) {
      if (true) {
      logger.info("CRON: (re-)initializing IP List");
      HttpURLConnection conn = null;

      try {
        URL url = new URL(PropertyReader.getProperty(PropertyReader.INGE_AUTH_MPG_IP_LIST_URL));

        conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(60 * 1000);
//        JSONParser jsonParser = new JSONParser();
//        JSONObject jsonObjectComplete = (JSONObject) jsonParser
//            .parse(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)); 
        StringBuilder responseStrBuilder = new StringBuilder();
        String inputStr;
        while ((inputStr = streamReader.readLine()) != null) {
            responseStrBuilder.append(inputStr);
        }
        JSONObject jsonObjectComplete = new JSONObject(responseStrBuilder.toString());
        // try (Scanner scanner = new Scanner(conn.getInputStream())) {
        Map<String, IpRange> ipRangeMap = new HashMap<>();
        // Add entry for whole MPG
        ipRangeMap.put("mpg", new IpListProvider.IpRange("mpg",
            " Max Planck Society (every institute)", new ArrayList<>()));
        JSONArray jsonArray = jsonObjectComplete.getJSONArray("details");
        for (int i = 0; i < jsonArray.length(); i++) {
          JSONObject jsonObject = jsonArray.getJSONObject(i);
          String id = jsonObject.keys().next();
          if (id.matches("\\d+")) {
            // Add every range for whole MPG
            // ipRangeMap.get("mpg").getIpRanges().add(jsonObject.getString(id));
            System.out.println("key: " + id + " value: " + jsonObject.getString(id));
          }

          // String id = line.get(2);
          // if (id.matches("\\d+")) {
          // //Add every range for whole MPG
          // ipRangeMap.get("mpg").getIpRanges().add(line.get(1));
          //
          // if (ipRangeMap.containsKey(id)) {
          // ipRangeMap.get(id).getIpRanges().add(line.get(1));
          // } else {
          // List<String> ipList = new ArrayList<>();
          // ipList.add(line.get(1));
          // ipRangeMap.put(id, new IpListProvider.IpRange(id, line.get(4) + ", " + line.get(3),
          // ipList));
          // }
          // } else {
          // logger.warn("Ignoring entry in ip list with id '" + id + "', as it is no valid id");
          // }
          // }
          // this.ipRangeMap = ipRangeMap;
          // logger.info("CRON: Successfully set IP List with " + ipRangeMap.size() + " entries");
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
  }

  @Override
  public IpRange get(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Collection<IpRange> getAll() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IpRange getMatch(String adress) {
    // TODO Auto-generated method stub
    return null;
  }

}
