package duckutil.sign;

import com.google.common.collect.ImmutableList;
import duckutil.Config;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

public class ReporterOpenWeather extends LineReporter
{
  private Config config;
  private JSONObject weather_json;

  public ReporterOpenWeather(Config config)
  {
    super("weather");
    this.config = config;
    config.require("openweather_lat");
    config.require("openweather_lon");
    config.require("openweather_api_key");
  }




  @Override
  public List<String> computeLines()
    throws Exception
  {
    String url_str = String
      .format("https://api.openweathermap.org/data/2.5/forecast/daily?lat=%s&lon=%s&cnt=7&appid=%s",
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

      double temp_min_k = Double.parseDouble( "" + temps.get("min"));
      double temp_max_k = Double.parseDouble( "" + temps.get("max"));

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

  @Override
  public BufferedImage getSuccessRender(Font font)
    throws Exception
  {
    List<BufferedImage> sections = new LinkedList<>();

    JSONArray list = (JSONArray) weather_json.get("list");

    for(Object o : list)
    {
      JSONObject jo = (JSONObject) o;
      int dt = (int) jo.get("dt");
      JSONObject temps = (JSONObject) jo.get("temp");

      double temp_min_k = Double.parseDouble( "" + temps.get("min"));
      double temp_max_k = Double.parseDouble( "" + temps.get("max"));

      JSONArray weather_arr = (JSONArray) jo.get("weather");
      JSONObject weather = (JSONObject) weather_arr.get(0);

      String weather_word = (String) weather.get("main");
      String weather_icon = (String) weather.get("icon");


      File icon_file = new File("icons/" + weather_icon + ".png");
      BufferedImage icon_img = null;
      if (icon_file.exists())
      {
        icon_img = ImageIO.read(icon_file);
        GraphicsUtil.dither(icon_img, Color.BLUE, Color.WHITE, Color.BLUE);
        GraphicsUtil.dither(icon_img, Color.BLACK, Color.WHITE, Color.BLACK);

      }
      else
      {
        System.out.println("Missing icon: " + weather_icon + " " + weather_word);

      }

      DecimalFormat df = new DecimalFormat("0");

      String line = getDayForTime(dt) + " " + df.format(getTempKtoF(temp_min_k))
        + " " + df.format(getTempKtoF(temp_max_k));

      if (icon_img == null)
      {
        line = line + " " + weather_word;
      }
      BufferedImage section_img = GraphicsUtil.renderText(Color.WHITE, Color.BLACK, font, line);

      if (icon_img != null)
      {
        section_img = GraphicsUtil.horzStack(ImmutableList.of( section_img, icon_img), 8);
      }

      sections.add(section_img);


    }

    return GraphicsUtil.vertStack( sections, 4);



  }

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
