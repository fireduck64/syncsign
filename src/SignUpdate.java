package duckutil.sign;

import duckutil.ConfigFile;
import duckutil.Config;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONArray;

import java.util.LinkedList;
import java.util.Collection;

import java.util.List;
import java.util.Random;

public class SignUpdate
{
  public static void main(String args[]) throws Exception
  {
    new SignUpdate(new ConfigFile(args[0]) );

  }
  public static final String font="DDIN_24";

  private ESUtil es_util;
  private Config config;


  public SignUpdate(Config config)
    throws Exception
  {
    this.config = config;
    String node = config.require("node");
    es_util = new ESUtil(config);

    boolean reset_display = false;
    Random rnd = new Random();
    if (rnd.nextDouble() < 0.1) reset_display=true;
    //reset_display=true;

    JSONObject doc = new JSONObject();
    JSONObject layout = new JSONObject();
    doc.put("layout", layout);

    JSONObject options = new JSONObject();
    layout.put("options", options);

    if (reset_display)
    {
      JSONObject background = new JSONObject();
      layout.put("background", background);
      options.put("refreshScreen", true);
    }
    else
    {
      options.put("refreshScreen", false);
    }
    
    JSONArray items = new JSONArray();
    layout.put("items", items);

    LinkedList<String> lines = getLines();

    int lines_per_col = 11;
    if (lines.size() > lines_per_col*2)
    {
      System.out.println("Unable to display that many lines");
    }

    for(int j = 0; j<2; j++)
    {
      if (lines.size() == 0) break;
      StringBuilder sb = new StringBuilder();
      int c = 0;
      while((lines.size() >0) && (c < lines_per_col))
      {
        sb.append(lines.pollFirst());
        sb.append("\n");
        c++;

      }

      JSONObject line = new JSONObject();
      line.put("type", "TEXT");
      JSONObject data = new JSONObject();
      line.put("data", data);



      data.put("text", sb.toString());
      data.put("font", font);
      data.put("text-align", "LEFT");
      data.put("id","nothing");
      data.put("lineSpace", 0);

      JSONObject block = new JSONObject();

      data.put("block", block);

      block.put("x", 2 + 200 * j);
      block.put("y", 2);
      block.put("w", 198);
      block.put("h", 296);

      items.add(line);

    }

    //System.out.println(doc);
    int code = Render.render(config, node, doc);
    //System.err.print(code);

    es_util.close();

    JSONObject process_report = new JSONObject();
    process_report.put("success", 1.0);
    process_report.put("process", "sign-update");

    duckutil.ElasticSearchPost.saveDoc( config.require("elasticsearch_url"), "process-report", process_report);


  }

  public LinkedList<String> getLines()
    throws Exception
  {
    LinkedList<LineReporter> reporters = new LinkedList<LineReporter>();

    int total_wait = 25000;

    reporters.add( new ReporterDate("EEE MM-dd-yy hh:mm") );
    reporters.add( new ReporterDate("yyyy.MM.dd HH:mm") );
    reporters.add( new ReporterPrice(es_util, "BTC"));
    reporters.add( new ReporterPrice(es_util, "BCH"));
    reporters.add( new ReporterPrice(es_util, "ETH"));
    reporters.add( new ReporterPrice(es_util, "SNOW"));
    reporters.add( new ReporterPrice(es_util, "VUG"));
    reporters.add( new ReporterNWSAlert(config));
    reporters.add( new ReporterLert());
    reporters.add( new ReporterBlank());
    reporters.add( new ReporterBlank());
    reporters.add( new ReporterNWSForcast(config,5));

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
