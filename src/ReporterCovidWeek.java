package duckutil.sign;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import net.minidev.json.JSONArray;
import net.minidev.json.parser.JSONParser;

public class ReporterCovidWeek extends LineReporter
{
  private String location;
  private String label;
  private int days;
  public ReporterCovidWeek(String location, String label, int days)
  {
    super("c19/"+label);
    this.location = location;
    this.label = label;
    this.days = days;
  }

  @Override
  public String computeLine() throws Exception
  {
    JSONArray deaths = get("https://covid19-data.1209k.com/chart_data/deaths/"+location+"?include_delta=true");
    JSONArray cases = get("https://covid19-data.1209k.com/chart_data/cases/"+location+"?include_delta=true");

    double case_count = sumDeltaWeek(cases)/days;
    double death_count = sumDeltaWeek(deaths)/days;

    return label + " " + formatNum(case_count) + " " +formatNum(death_count);
  }

  public String formatNum(double in)
  {
    ArrayList<String> unit_list = new ArrayList<String>();
    unit_list.add("");
    unit_list.add("K");
    unit_list.add("M");
    unit_list.add("G");
    unit_list.add("T");

    int unit = 0;
    double v = in;
    while( v > 1000.0)
    {
      unit++;
      v = v / 1000.0;
    }
    DecimalFormat df = new DecimalFormat("#");
    if (v < 100.0) df = new DecimalFormat("#.0");
    if (v < 10.0) df = new DecimalFormat("#.00");
    return df.format(v) + unit_list.get(unit);

  }

  public double sumDeltaWeek(JSONArray arr)
  {
    int sz = arr.size();
    List<Object> week = arr.subList(sz-days, sz);

    double total = 0.0;

    for(Object o : week)
    {
      JSONArray a = (JSONArray) o;
      double sum = (double) a.get(1);
      double delta = (double) a.get(2);
      total += delta;
    }

    return total;

  }

  public static JSONArray get(String url)
    throws Exception
  {

    URL u = new URL(url);
    HttpURLConnection connection = (HttpURLConnection) u.openConnection();
    connection.setRequestMethod("GET");

    connection.setDoInput(true);

    int code = connection.getResponseCode();

    return (JSONArray) new JSONParser( JSONParser. DEFAULT_PERMISSIVE_MODE).parse(connection.getInputStream());

  }

}
