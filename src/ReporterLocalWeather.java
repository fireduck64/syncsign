package duckutil.sign;

import java.util.Map;
import java.util.TreeMap;
import com.google.common.collect.ImmutableMap;
import java.text.DecimalFormat;

public class ReporterLocalWeather extends LineReporter
{
  private final DBUtil es_util;

  public ReporterLocalWeather(DBUtil es_util)
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
      filters.put("location", "outdoor");
      Map<String, Object> doc = es_util.getLatest("airq", filters);
      double t = Double.parseDouble(doc.get("temp_f").toString());
      DecimalFormat df = new DecimalFormat("0.0");
      out_temp = df.format(t);
    }
    String out_hum = "";
    {
      Map<String,String> filters = new TreeMap<>();
      filters.put("location", "outdoor");
      Map<String, Object> doc = es_util.getLatest("airq", filters);
      out_hum = doc.get("hm").toString();
    }

    return "Out: " + out_temp +"F" + " " + out_hum +"%";
  }

}
