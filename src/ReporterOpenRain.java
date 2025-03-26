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
    weather_json = OpenWeatherUtil.getOneCall(config);

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


    // top and bottom lines
    for(int i=0; i<total_width; i++)
    {
      bi.setRGB(i, 0, Color.GREEN.getRGB());
      bi.setRGB(i, height-1, Color.BLACK.getRGB());
    }

    // 6 hour hash marks
    for(int i=0; i<4; i++)
    {
      int offset = i * hour_width * 6;

      for(int j=0; j<height; j+=2)
      {
        bi.setRGB(offset, j, Color.BLACK.getRGB());
      }
    }
    // draw night
    {
      JSONObject current = (JSONObject) weather_json.get("current");
      long sunset = Long.parseLong("" + current.get("sunset"));
      long sunrise = Long.parseLong("" + current.get("sunrise"));
      int sunrise_off = getOffset(sunrise);
      int sunset_off = getOffset(sunset);
      Random rnd = new Random();
      double night_fill = 1.0;


      Color fill = Color.BLACK;
      for(int x=0; x<total_width; x++)
      {
        if((x > sunset_off) || (x < sunrise_off))
        {
          fill = Color.BLUE;
        }
        else
        {
          fill = Color.YELLOW;
        }
        if (x == sunset_off) fill = Color.GREEN;
        if (x == sunrise_off) fill = Color.GREEN;

        //for(int j=0; j<height; j++)
        //int j = 0;
        for(int j=0; j<3; j++)
        {
          //if (bi.getRGB(x,j) == Color.WHITE.getRGB())
          {
            if (rnd.nextDouble() < night_fill)
            {
              bi.setRGB(x,j, fill.getRGB());
            }
          }
        }
      
      }



    }

    // Draw in rain prob
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
          if ((x+y) % 2 == 1)
          bi.setRGB(x,y, Color.BLUE.getRGB());
        }
      }
    }

    // Current time
    {
      int cur_offset= getCurrentOffset();
      for(int i=cur_offset; i<=cur_offset+2; i++)
      {
        if (i < bi.getWidth())
        {
        for(int j=1; j<height; j+=1)
        {
          bi.setRGB(i,j, Color.WHITE.getRGB());
          if (j % 3 == 1)
          {
            bi.setRGB(i, j, Color.RED.getRGB());
          }
        }
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
    return getOffset( System.currentTimeMillis()/1000L );

  }

  public int getOffset(long epoch_sec)
  {

    int total_width = 24*hour_width;

    int curr_hour = Instant.ofEpochSecond(epoch_sec).atZone( ZoneId.systemDefault() ).getHour();
    int curr_min = Instant.ofEpochSecond(epoch_sec).atZone( ZoneId.systemDefault() ).getMinute();

    double min = curr_min + 60*curr_hour;
    double min_in_day = 60*24;

    double ratio = min / min_in_day;

    int offset = (int)Math.round( ratio * total_width);


    return Math.min( offset, 60*24-1);




  }


}
