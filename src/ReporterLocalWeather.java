package duckutil.sign;

import java.util.Map;
import java.util.TreeMap;
import com.google.common.collect.ImmutableMap;
import java.text.DecimalFormat;
import duckutil.Config;

public class ReporterLocalWeather extends LineReporter
{
  private final DBUtil es_util;
  private final String local_weather_location;

  public ReporterLocalWeather(Config config, DBUtil es_util)
  {
    super("local_weather");
    this.es_util = es_util;
    local_weather_location = config.require("local_weather_location");
  }

  @Override
  public String computeLine() throws Exception
  {
    String out_temp = "";
    {
      Map<String,String> filters = new TreeMap<>();
      filters.put("location", local_weather_location);
      Map<String, Object> doc = es_util.getLatest("airq", filters);
      double t = Double.parseDouble(doc.get("temp_f").toString());
      DecimalFormat df = new DecimalFormat("0.0");
      out_temp = df.format(t);
    }
    String out_hum = "";
    {
      Map<String,String> filters = new TreeMap<>();
      filters.put("location", local_weather_location);
      Map<String, Object> doc = es_util.getLatest("airq", filters);
      out_hum = doc.get("hm").toString();
    }

    return "Out: " + out_temp +"F" + " " + out_hum +"%";
  }

}
