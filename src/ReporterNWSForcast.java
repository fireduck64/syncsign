package duckutil.sign;


import duckutil.Config;
import java.util.LinkedList;
import java.util.List;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class ReporterNWSForcast extends LineReporter
{
  private Config config;
  private final int count;
  public ReporterNWSForcast(Config config, int count)
  {
    super("nws_forcast");
    this.config = config;
    this.count = count;
  }

  @Override
  public List<String> computeLines() throws Exception
  {
    JSONArray periods = NWSUtil.getForcast(config);

    LinkedList<String> lines = new LinkedList<>();

    int c = 0;
    while((c < count) && (periods.size() > c))
    {
      JSONObject p = (JSONObject) periods.get(c);
      String name = (String) p.get("name");
      int temp = (int) p.get("temperature");
      String shortf = (String) p.get("shortForecast");

      name = name.replace("This ","");
      //name = name.replace("Afternoon","aft");
      name = name.replace("Monday","Mon");
      name = name.replace("Tuesday","Tue");
      name = name.replace("Wednesday","Wed");
      name = name.replace("Thursday","Thr");
      name = name.replace("Friday","Fri");
      name = name.replace("Saturday","Sat");
      name = name.replace("Sunday","Sun");
      //name = name.replace("Night","Ngt");

      shortf = shortf.replace("Chance","Ch");

      if (shortf.length() > 17)
      {
        shortf = shortf.substring(0,17);
      }


      lines.add(name + " " + temp +"F");
      lines.add(": " + shortf);
      if (c == 0)
      {
        String wind = (String) p.get("windSpeed");
        String wind_dir = (String) p.get("windDirection");
        lines.add(": " + wind + " " + wind_dir);

      }


      c++;
    }

    

    return lines;

  }

}
