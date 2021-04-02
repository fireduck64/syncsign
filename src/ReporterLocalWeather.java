package duckutil.sign;

import java.util.Map;
import java.util.TreeMap;
import com.google.common.collect.ImmutableMap;

public class ReporterLocalWeather extends LineReporter
{
  private final ESUtil es_util;

  public ReporterLocalWeather(ESUtil es_util)
  {
    super("local_weather");
    this.es_util = es_util;
  }

  @Override
  public String computeLine() throws Exception
  {
    String out_temp = "";
    {
      Map<String,String> filters = new TreeMap<>();
      filters.put("value_id", "2-49-1-1");
      Map<String, Object> doc = es_util.getLatest("zwave", filters);
      out_temp = doc.get("value_number").toString();
    }
    String out_hum = "";
    {
      Map<String,String> filters = new TreeMap<>();
      filters.put("value_id", "2-49-1-5");
      Map<String, Object> doc = es_util.getLatest("zwave", filters);
      out_hum = doc.get("value_number").toString();
    }

    return "Out: " + out_temp +"F" + " " + out_hum +"%";
  }

}
