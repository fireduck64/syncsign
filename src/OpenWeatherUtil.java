
package duckutil.sign;

import duckutil.Config;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.TreeSet;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;


public class OpenWeatherUtil
{
  private static JSONObject one_call_cached;

  public static synchronized JSONObject getOneCall(Config config)
    throws Exception
  {
    if (one_call_cached != null) return one_call_cached;

    String url_str = String
      .format("https://api.openweathermap.org/data/3.0/onecall?lat=%s&lon=%s&appid=%s",
        config.require("openweather_lat"),
        config.require("openweather_lon"),
        config.require("openweather_api_key"));

    URL u = new URL(url_str);
    HttpURLConnection connection = (HttpURLConnection) u.openConnection();
    connection.setRequestMethod("GET");

    JSONObject weather_json = (JSONObject) new JSONParser( JSONParser.DEFAULT_PERMISSIVE_MODE ).parse(connection.getInputStream());

    one_call_cached = weather_json;

    if (config.isSet("openweather_onecall_savefile"))
    {
      PrintStream out = new PrintStream(new FileOutputStream(config.get("openweather_onecall_savefile"))); 
      out.println(weather_json);
      out.close();
    }
    

    return one_call_cached;





  }

}
