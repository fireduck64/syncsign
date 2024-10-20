package duckutil.sign;

import java.util.Map;
import java.util.TreeMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList;
import java.util.List;
import duckutil.Pair;
import java.awt.image.BufferedImage;
import java.awt.Font;
import java.awt.Color;
import java.util.LinkedList;
import duckutil.Config;

public class ReporterAQI extends LineReporter
{
  private final DBUtil es_util;
  private final String local_weather_location;

  public ReporterAQI(Config config, DBUtil es_util)
  {
    super("aqi");
    this.es_util = es_util;
    local_weather_location = config.require("local_weather_location");

  }

  List<Pair<String, Long> > air_val = new LinkedList<>();

  @Override
  public String computeLine() throws Exception
  {
    long in_aqi = 0;
    {
      Map<String,String> filters = new TreeMap<>();
      filters.put("location", "indoor");
      Map<String, Object> doc = es_util.getLatest("airq", filters);
      in_aqi = getAqi(doc);
    }
    air_val.add(new Pair<String, Long>("h", in_aqi));

    long out_aqi = 0;
    {
      Map<String,String> filters = new TreeMap<>();
      filters.put("location", local_weather_location);
      Map<String, Object> doc = es_util.getLatest("airq", filters);
      out_aqi = getAqi(doc);
    }
    air_val.add(new Pair<String, Long>("o", out_aqi));

    long crab_aqi = 0;
    {
      Map<String,String> filters = new TreeMap<>();
      filters.put("location", "crabshack");
      Map<String, Object> doc = es_util.getLatest("airq", filters);
      crab_aqi = getAqi(doc);
    }
    air_val.add(new Pair<String, Long>("c", crab_aqi));

    long studio_aqi = 0;
    {
      Map<String,String> filters = new TreeMap<>();
      filters.put("location", "studio");
      Map<String, Object> doc = es_util.getLatest("airq", filters);
      studio_aqi = getAqi(doc);
    }
    air_val.add(new Pair<String, Long>("s", studio_aqi));

   return String.format("A o%d h%d s%d c%d", out_aqi, in_aqi, studio_aqi, crab_aqi);

  }

  @Override
  public BufferedImage getSuccessRender(Font font)
  {
    List<BufferedImage> sections = new LinkedList<>();
    sections.add( GraphicsUtil.renderText(Color.WHITE, Color.BLACK, font, "AQI"));
    
    for(Pair<String, Long> p : air_val)
    {
      Color fg = Color.RED;
      long aqi = p.getB();
      String label = p.getA();
      if (aqi < 75) fg = Color.YELLOW;
      if (aqi < 45) fg = Color.GREEN;
      String s = label + aqi;
      BufferedImage bi = GraphicsUtil.renderText(Color.RED, Color.BLACK, font, s);

      GraphicsUtil.dither(bi, Color.RED, Color.WHITE, fg);

      sections.add( bi );

    }
    return GraphicsUtil.horzStack(sections, 8);

  }


  public long getAqi(Map<String, Object> doc)
  {
    //System.out.println("getApi " + doc);
    if (doc.containsKey("aqius"))
    {
      long v = (int) doc.get("aqius");
      return v;
    }
    Double p10 = Double.parseDouble(doc.get("p1").toString());
    Double p25 = Double.parseDouble(doc.get("p2").toString());

    return Math.max(
      getAqiFromPol( 
        ImmutableList.of(0.0, 50.0,100.0,150.0, 200.0, 300.0, 400.0, 500.0),
        ImmutableList.of(0.0, 12.0, 35.5, 55.5, 150.5, 250.5, 350.5, 500.5),
        p25),
      getAqiFromPol(
        ImmutableList.of(0.0, 50.0,100.0,150.0, 200.0, 300.0, 400.0, 500.0),
        ImmutableList.of(0.0, 55.0,155.0,255.0, 355.0, 425.0, 505.0, 605.0),
        p10)
    );
  }

	// From https://aqicn.org/calculator/
  public long getAqiFromPol(List<Double> range, List<Double> scale, double p2)
  {
		double c = p2;

		for(int i=0; i<scale.size()-1; i++)
		{
			if ((c >= scale.get(i)) && (c<scale.get(i+1)))
			{
				double aqi = range.get(i) +
					(c - scale.get(i)) * (range.get(i+1) - range.get(i)) / (scale.get(i+1) - scale.get(i));
				return Math.round(aqi);
			}
		}
		return 500;

  }



}
