package duckutil.sign;

import duckutil.Config;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

public class ReporterOpenRain extends LineReporter
{
  private Config config;
  private JSONObject weather_json;
  private final int hour_width;
  private final int height;

  public ReporterOpenRain(Config config, int hour_width, int height)
  {
    super("rain");
    this.config = config;
    config.require("openweather_lat");
    config.require("openweather_lon");
    config.require("openweather_api_key");

    this.hour_width = hour_width;
    this.height = height;
  }

  @Override
  public List<String> computeLines()
    throws Exception
  {
    String url_str = String
      .format("https://api.openweathermap.org/data/3.0/onecall?lat=%s&lon=%s&appid=%s",
        config.require("openweather_lat"),
        config.require("openweather_lon"),
        config.require("openweather_api_key"));

    URL u = new URL(url_str);
    HttpURLConnection connection = (HttpURLConnection) u.openConnection();
    connection.setRequestMethod("GET");

    weather_json = (JSONObject) new JSONParser( JSONParser.DEFAULT_PERMISSIVE_MODE ).parse(connection.getInputStream());

    LinkedList<String> lines = new LinkedList<>();

    lines.add("rain");
    return lines;
  }

  @Override
  public BufferedImage getSuccessRender(Font font)
    throws Exception
  {
    int total_width = 24*hour_width;

    BufferedImage bi = new BufferedImage(total_width, height,BufferedImage.TYPE_INT_RGB);
    Graphics2D g = bi.createGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0,0,total_width, height);

    for(int i=0; i<total_width; i++)
    {
      bi.setRGB(i, 0, Color.GREEN.getRGB());
      bi.setRGB(i, height-1, Color.BLACK.getRGB());
    }


    for(int i=0; i<4; i++)
    {
      int offset = i * hour_width * 6;

      for(int j=0; j<height; j+=2)
      {
        bi.setRGB(offset, j, Color.BLACK.getRGB());
      }
    }

    {
      int cur_offset= getCurrentOffset();
      for(int j=1; j<height; j+=3)
      {
        bi.setRGB(cur_offset, j, Color.RED.getRGB());
      }
    }

    for(Map.Entry<Integer, Double> h : getRainByHourMap().entrySet())
    {
      int hour = h.getKey();
      double pop = h.getValue();
      if (hour < 0) continue;
      if (hour > 23) continue;

      int draw_h = (int) Math.round(pop * height);

      int offset = hour * hour_width;
      for(int i=0; i<hour_width; i++)
      for(int j=0; j<draw_h; j++)
      {
        int y = height - 1 - j;
        int x = offset + i;

        if (bi.getRGB(x,y) == Color.WHITE.getRGB())
        {
          bi.setRGB(x,y, Color.BLUE.getRGB());
        }

      }

    }

    return bi;

  }

  public TreeMap<Integer, Double> getRainByHourMap()
  {
    TreeMap<Integer, Double> map = new TreeMap<>();


    JSONArray hourly = (JSONArray) weather_json.get("hourly");
    for(Object o : hourly)
    {
      JSONObject jo = (JSONObject) o;

      double pop = Double.parseDouble( "" + jo.get("pop"));
      long dt = Long.parseLong("" + jo.get("dt"));
      int hour = getHourForTime(dt);

      map.put(hour, pop);
      //map.put(hour, new Random().nextDouble());

    }

    System.out.println("Rain map: " + map);

    return map;

  }

  public int getHourForTime(long dt)
  {
    Instant inst = Instant.ofEpochSecond(dt);

    int curr_day = Instant.now().atZone( ZoneId.systemDefault()).getDayOfYear();

    int delta_day = inst.atZone( ZoneId.systemDefault() ).getDayOfYear() - curr_day;

    return inst.atZone( ZoneId.systemDefault() ).getHour() + delta_day * 24;

  }

  public int getCurrentOffset()
  {

    int total_width = 24*hour_width;

    int curr_hour = Instant.now().atZone( ZoneId.systemDefault() ).getHour();
    int curr_min = Instant.now().atZone( ZoneId.systemDefault() ).getMinute();

    double min = curr_min + 60*curr_hour;
    double min_in_day = 60*24;

    double ratio = min / min_in_day;

    int offset = (int)Math.round( ratio * total_width);


    return Math.min( offset, 60*24-1);




  }


}
