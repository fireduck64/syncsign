package duckutil.sign;

import java.util.Map;
import java.util.TreeMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList;
import java.util.List;

public class ReporterAQI extends LineReporter
{
  private final DBUtil es_util;

  public ReporterAQI(DBUtil es_util)
  {
    super("aqi");
    this.es_util = es_util;
  }

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

    long out_aqi = 0;
    {
      Map<String,String> filters = new TreeMap<>();
      filters.put("location", "outdoor");
      Map<String, Object> doc = es_util.getLatest("airq", filters);
      out_aqi = getAqi(doc);
    }

    long crab_aqi = 0;
    {
      Map<String,String> filters = new TreeMap<>();
      filters.put("location", "crabshack");
      Map<String, Object> doc = es_util.getLatest("airq", filters);
      crab_aqi = getAqi(doc);
    }
    long studio_aqi = 0;
    {
      Map<String,String> filters = new TreeMap<>();
      filters.put("location", "studio");
      Map<String, Object> doc = es_util.getLatest("airq", filters);
      studio_aqi = getAqi(doc);
    }

   return String.format("A o%d h%d s%d c%d", out_aqi, in_aqi, studio_aqi, crab_aqi);

  }

  public long getAqi(Map<String, Object> doc)
  {
    System.out.println("getApi " + doc);
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
