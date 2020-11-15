package duckutil.sign;

import duckutil.Config;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TreeSet;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

public class NWSUtil
{
  public static TreeSet<String> getAlertHazards(Config config) throws Exception
  {
    JSONObject doc = get(config, config.require("nws_alert_url"));
    TreeSet<String> hazards = new TreeSet<>();

    JSONArray features = (JSONArray) doc.get("features");
    
    for(Object fo : features)
    {
      JSONObject f = (JSONObject) fo;
      JSONObject properties = (JSONObject) f.get("properties");
      JSONObject params = (JSONObject) properties.get("parameters");
      JSONArray ha = (JSONArray) params.get("HazardType");

      for(Object ho : ha)
      {
        String s = (String) ho;
        hazards.add(s);
      }

    }
    return hazards;
  }

  public static JSONArray getForcast(Config config)
    throws Exception
  {
    JSONObject doc = get(config, config.require("nws_forcast_url"));

    JSONObject properties = (JSONObject) doc.get("properties");
    JSONArray periods = (JSONArray) properties.get("periods");

    return periods;

  }

  public static JSONObject get(Config config, String url)
    throws Exception
  {
    String user_agent = config.require("nws_user_agent");

    URL u = new URL(url);
    HttpURLConnection connection = (HttpURLConnection) u.openConnection();
    connection.setRequestMethod("GET");

    connection.setDoInput(true);

    connection.setRequestProperty("User-Agent", user_agent);

    int code = connection.getResponseCode();


    return (JSONObject) new JSONParser( JSONParser. DEFAULT_PERMISSIVE_MODE).parse(connection.getInputStream());

  }

}

