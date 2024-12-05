package de.mpg.mpdl.inge.service.aa;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.inge.util.NetworkUtils;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * @inheritDoc
 *
 *             Implementation of IpListProvider for JSON EXAMPLE: { "details": [ { "100": {
 *             "ip_ranges": [ "123.123.123.123/31", "234.123.123.123/32", ], "inst_name_de":
 *             "Generalverwaltung der Max-Planck-Gesellschaft", "domains": [ "gv.mpg.de" ],
 *             "inst_code": "MMGV" } }, { "200": { "domains": [ "mpdl.mpg.de" ], "inst_code":
 *             "MMDL", "ip_ranges": [ "123.123.123.123/29", "234.123.123.123/29", ], "inst_name_de":
 *             "Max Planck Digital Library" } } ] }
 *
 * @author walter
 *
 */
@Component
public class MpgJsonIpListProvider implements IpListProvider {

  private static final Logger logger = LogManager.getLogger(MpgJsonIpListProvider.class);
  private static final String DETAILS = "details";
  private static final String IP_RANGE = "ip_ranges";
  private static final String MPG = "mpg";

  private Map<String, IpRange> ipRangeMap = new HashMap<>();

  public MpgJsonIpListProvider() {
    this.init();
  }

  /**
   * initializing IpListProvider with new IP list (done continuously as CRON job
   */
  @Scheduled(cron = "0 0 2 * * ?")
  private void init() {
    if ("true".equalsIgnoreCase(PropertyReader.getProperty(PropertyReader.INGE_AUTH_MPG_IP_LIST_USE))) {
      logger.info("*** CRON (0 0 2 * * ?): (re-)initializing IP List from <"
          + PropertyReader.getProperty(PropertyReader.INGE_AUTH_MPG_JSON_IP_LIST_URL) + ">");
      HttpURLConnection conn = null;

      try {
        // Setup Connection
        URL url = new URL(PropertyReader.getProperty(PropertyReader.INGE_AUTH_MPG_JSON_IP_LIST_URL));
        // URL url = new URL("https://rena.mpdl.mpg.de/iplists/keeperx_en.json");
        conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(60 * 1000);
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder responseStrBuilder = new StringBuilder();
        String inputStr;
        while (null != (inputStr = streamReader.readLine())) {
          responseStrBuilder.append(inputStr);
        }

        // Read response
        JSONObject jsonObjectComplete = new JSONObject(responseStrBuilder.toString());
        Map<String, IpRange> ipRangeMap = new HashMap<>();
        // Add entry for whole MPG
        ipRangeMap.put(MpgJsonIpListProvider.MPG,
            new IpListProvider.IpRange(MpgJsonIpListProvider.MPG, " Max Planck Society (every institute)", new ArrayList<>()));
        JSONArray jsonArray = jsonObjectComplete.getJSONArray(MpgJsonIpListProvider.DETAILS);
        // Go through JSON for every Entry/Institute
        for (int i = 0; i < jsonArray.length(); i++) {
          JSONObject jsonObject = jsonArray.getJSONObject(i);
          String id = jsonObject.keys().next();
          if (id.matches("\\d+")) {
            JSONObject singleOrganization = jsonObject.getJSONObject(id);
            List<String> ipList = new ArrayList<>();
            try {
              JSONArray ipRangeArray = singleOrganization.getJSONArray(MpgJsonIpListProvider.IP_RANGE);
              // Add all ip_ranges Entries for each institute
              for (int j = 0; j < ipRangeArray.length(); j++) {
                ipList.add(ipRangeArray.getString(j));
              }
              // Add every range for whole MPG
              ipRangeMap.get(MpgJsonIpListProvider.MPG).getIpRanges().addAll(ipList);
              // Add every institute as single IP list entry
              // Case: institute already exists
              if (ipRangeMap.containsKey(id)) {
                for (int j = 0; j < ipRangeArray.length(); j++) {
                  ipRangeMap.get(id).getIpRanges().add(ipRangeArray.getString(j));
                }
              }
              // Case: new institute entry
              else {
                for (int j = 0; j < ipRangeArray.length(); j++) {
                  ipList.add(ipRangeArray.getString(j));
                }
                ipRangeMap.put(id, new IpListProvider.IpRange(id,
                    singleOrganization.getString("inst_name_en") + ", " + singleOrganization.getString("inst_code"), ipList));
              }
            } catch (JSONException e) {
              logger.warn("*** CRON: Could not get '" + MpgJsonIpListProvider.IP_RANGE + "' for id '" + id + "', as they are not defined");
            }
          } else {
            logger.warn("*** CRON: Ignoring entry in ip list with id '" + id + "', as it is no valid id");
          }
        }
        // write local ipRangeMap back to class ipRangeMap
        this.ipRangeMap = ipRangeMap;
        logger.info("*** CRON: Successfully set JSON IP List with " + ipRangeMap.size() + " entries");
      } catch (Exception e) {
        logger.error("*** CRON: Problem with parsing ip list file", e);
      } finally {
        if (null != conn) {
          conn.disconnect();
        }
        if (null == this.ipRangeMap || this.ipRangeMap.isEmpty()) {
          logger.warn("*** CRON: No IP RANGES found! - List is empty");
        }

      }
    }
  }

  @Override
  public IpRange get(String id) {
    return this.ipRangeMap.get(id);
  }

  @Override
  public Collection<IpRange> getAll() {
    return this.ipRangeMap.values();
  }

  @Override
  public IpRange getMatch(String adress) {
    for (IpRange ipRange : this.ipRangeMap.values()) {
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
