package duckutil.sign;

import duckutil.Config;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TreeSet;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Collection;
import java.util.LinkedList;


public class NWSUtil
{
  public static TreeSet<String> getAlertHazards(Config config) throws Exception
  {
    JSONObject doc = get(config, config.require("nws_alert_url"));

    if (config.isSet("nws_alert_file"))
    {
      try
      {
        PrintStream out = new PrintStream(new FileOutputStream(config.get("nws_alert_file")));
        out.println(doc);
        out.close();
      }
      catch(java.io.IOException e)
      {
      
      }
        
    }

    TreeSet<String> hazards = new TreeSet<>();

    JSONArray features = (JSONArray) doc.get("features");
    
    for(Object fo : features)
    {
      JSONObject f = (JSONObject) fo;

      for(String event : getHazardsFromFeature(f))
      {

      event = event.replace("Advisory","");
      event = event.replace("Weather","");
      event = event.replace("Watch","");
      event = event.replace("Warning","");
      event = event.replace("Storm","");
      event = event.replace("Avalanche","Slidey");
      event = event.replace("Heavy Snow","Snowy");
      event = event.replace("Flood","Floody");
      event = event.replace("Hydrologic Outlook","Floody");
      event = event.replace("Hydrologic_Outlook","Floody");
      event = event.replace("Flood Watch","Floody");
      event = event.replace("Coastal Floody","Floody");
      event = event.replace("Air Stagnation","Stag");
      event = event.replace("Dense Fog","Fog");
      event = event.trim();
      event = event.replace(" ","_");

        hazards.add(event);

      }

    }
    return hazards;
  }

  private static Collection<String> getHazardsFromFeature(JSONObject f)
  {
    LinkedList<String> lst = new LinkedList<>();

    JSONObject properties = (JSONObject) f.get("properties");
    JSONObject params = (JSONObject) properties.get("parameters");

    String event = (String) properties.get("event");
    event = event.replace("Special Weather Statement","Special");
    if (event.equals("Special"))
    {
      JSONArray headlines = (JSONArray)params.get("NWSheadline");

      String headline = (String) headlines.get(0);

      if (headline.contains("LANDSLIDES")) event="Slidey";

    }
    lst.add(event);

    // Hazard type Isn't always specified
    if (params.containsKey("HazardType"))
    {

      JSONArray ha = (JSONArray) params.get("HazardType");

      for(Object ho : ha)
      {
        String s = (String) ho;
        s = s.trim();
        lst.add(s);
      }
    }

    return lst; 
  }

  public static JSONArray getForcast(Config config)
    throws Exception
  {
    //JSONObject doc = get(config, config.require("nws_forcast_url"));

    JSONObject doc = readFile(config.require("nws_forcast_file"));

    /*if (config.isSet("nws_forcast_file"))
    {
      try
      {
        PrintStream out = new PrintStream(new FileOutputStream(config.get("nws_forcast_file")));
        out.println(doc);
        out.close();
      }
      catch(java.io.IOException e)
      {
      
      }
        
    }*/

    JSONObject properties = (JSONObject) doc.get("properties");
    String gen_time = (String) properties.get("generatedAt");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    Date date = sdf.parse(gen_time);
    if (Math.abs(date. getTime() - System.currentTimeMillis()) > 3600L * 8L * 1000L)
    {
      throw new OldDataException(gen_time);
    }

    JSONArray periods = (JSONArray) properties.get("periods");

    return periods;

  }

  public static JSONObject readFile(String file)
    throws Exception
  {
    File f = new File(file);
    InputStream in = new FileInputStream(f);
    
    return (JSONObject) new JSONParser( JSONParser. DEFAULT_PERMISSIVE_MODE).parse(in);

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

