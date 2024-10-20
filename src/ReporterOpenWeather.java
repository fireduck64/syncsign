package duckutil.sign;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.awt.image.BufferedImage;
import java.awt.Font;
import java.awt.Color;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONArray;
import net.minidev.json.parser.JSONParser;
import java.util.List;
import java.util.LinkedList;
import java.util.TreeSet;
import duckutil.Config;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DecimalFormat;

public class ReporterOpenWeather extends LineReporter
{
  private Config config;
  private JSONObject weather_json;


  public ReporterOpenWeather(Config config)
  {
      super("weather");
      this.config = config;
  }


  @Override
  public List<String> computeLines()
    throws Exception
  {
    String url_str = String.format("https://api.openweathermap.org/data/2.5/forecast/daily?lat=%s&lon=%s&cnt=7&appid=%s", 
      config.require("openweather_lat"),
      config.require("openweather_lon"),
      config.require("openweather_api_key"));
    URL u = new URL(url_str);
    HttpURLConnection connection = (HttpURLConnection) u.openConnection();
    connection.setRequestMethod("GET");

    weather_json = (JSONObject) new JSONParser( JSONParser.DEFAULT_PERMISSIVE_MODE ).parse(connection.getInputStream());

    JSONArray list = (JSONArray) weather_json.get("list");

    List<String> lines = new LinkedList<>();

    for(Object o : list)
    {
      JSONObject jo = (JSONObject) o;
      int dt = (int) jo.get("dt");
      JSONObject temps = (JSONObject) jo.get("temp");

      double temp_min_k = (double) temps.get("min");
      double temp_max_k = (double) temps.get("max");

      JSONArray weather_arr = (JSONArray) jo.get("weather");
      JSONObject weather = (JSONObject) weather_arr.get(0);

      String weather_word = (String) weather.get("main");
      String weather_icon = (String) weather.get("icon");



      DecimalFormat df = new DecimalFormat("0");

      lines.add( getDayForTime(dt) + " " + df.format(getTempKtoF(temp_min_k)) 
        + " " + df.format(getTempKtoF(temp_max_k)) + " " + weather_word);

    }

    return lines;
  }
  /*@Override
  public BufferedImage getSuccessRender(Font font)
  {

  }*/

  public String getDayForTime(long dt)
  {
    SimpleDateFormat sdf = new SimpleDateFormat("EEE");

    return sdf.format( new Date(dt * 1000L));

  }

  private double getTempKtoF(double kel)
  {
    double c = kel - 273.15;
    double f = c * 9.0 / 5.0 + 32;

    return f;
  }


}
