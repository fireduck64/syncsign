package duckutil.sign;

import duckutil.Config;
import duckutil.ConfigFile;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class Display
{
  public static void main(String args[]) throws Exception
  {
    new Display(new ConfigFile(args[0]) );

  }
  public static final String font="DDIN_24";

  private DBUtil es_util;
  private Config config;


  public Display(Config config)
    throws Exception
  {
    this.config = config;
    try
    {
      es_util = new MongoUtil(config);

      LinkedList<String> lines = getLines();
    }
    finally
    {
      es_util.close();
    }


  }

  public LinkedList<String> getLines()
    throws Exception
  {
    LinkedList<LineReporter> reporters = new LinkedList<LineReporter>();

    int total_wait = 25000;

    reporters.add( new ReporterDate("EEE MM-dd hh:mm") );
    reporters.add( new ReporterDate("yyyy.MM.dd HH:mm") );
    reporters.add( new ReporterPrice(es_util, "BTC"));
    reporters.add( new ReporterPrice(es_util, "BCH"));
    reporters.add( new ReporterPrice(es_util, "ETH"));
    reporters.add( new ReporterPrice(es_util, "SNOW"));
    reporters.add( new ReporterStockPrice(es_util, "VUG"));
    reporters.add( new ReporterLert());
    //reporters.add( new ReporterCovidWeek("US","US",1));
    //reporters.add( new ReporterCovidWeek("Washington","WA",1));
    //reporters.add( new ReporterCovidWeek("Washington,King","King",1));
    //reporters.add( new ReporterCountdown("fdt", 1661205600L));
    reporters.add( new ReporterLocalWeather(config, es_util) );
    reporters.add( new ReporterAQI(config, es_util) );
    reporters.add( new ReporterNWSAlert(config));
    //reporters.add( new ReporterNWSForcast(config,4));
    reporters.add(new ReporterOpenWeather(config));

    for(LineReporter r : reporters)
    {
      r.start();
    }

    for(int i=0; i<total_wait; i+=100)
    {
      Thread.sleep(100);
      int not_done = 0;
      for(LineReporter r : reporters)
      {
        if (!r.isDone())
        {
          not_done++;
        }
      }
      if (not_done == 0) break;
    }

    LinkedList<String> lines = new LinkedList<>();
    for(LineReporter r : reporters)
    {
      List<String> l = r.returnLines();
      for(String ll : l)
      {
        System.out.println(ll);
      }
      lines.addAll(l);
    }
    return lines;
  }
}
