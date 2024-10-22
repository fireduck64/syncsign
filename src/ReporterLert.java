package duckutil.sign;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

public class ReporterLert extends LineReporter
{
  public ReporterLert()
  {
    super("lert");
  }

  private JSONObject lert_json;

  @Override
  public String computeLine() throws Exception
  {
    URL u = new URL("http://skyeye.1209k.com:45781/");
    HttpURLConnection connection = (HttpURLConnection) u.openConnection();
    connection.setRequestMethod("GET");

    connection.setDoInput(true);

    int code = connection.getResponseCode();
    Scanner scan = new Scanner(connection.getInputStream());
    String last_line = null;

    while(scan.hasNextLine())
    {
      last_line = scan.nextLine();
    }

    last_line = last_line.replace("MISSING","MISS");

    String json_line = last_line;
    json_line = json_line.replace("MISS", "\"MISS\"");
    json_line = json_line.replace("OK", "\"OK\"");
    json_line = json_line.replace("BAD", "\"BAD\"");
    json_line = json_line.replace("=", ":");


    lert_json = (JSONObject) new JSONParser( JSONParser.DEFAULT_PERMISSIVE_MODE ).parse(json_line);

    return last_line;
  }

  @Override
  public BufferedImage getSuccessRender(Font font)
  {
    List<BufferedImage> sections = new LinkedList<>();

    TreeSet<String> keys = new TreeSet<>();
    keys.addAll(lert_json.keySet());

    for(String key : keys)
    {
      String s = key + ": " + lert_json.get(key);
      Color bg = Color.WHITE;

      if (!key.equals("OK")) bg=Color.RED;

      BufferedImage bi = GraphicsUtil.renderText(bg, Color.BLACK, font, s);

      if (key.equals("BAD"))
      {
        GraphicsUtil.dither(bi, Color.RED, Color.RED, Color.YELLOW);
      }
      if (key.equals("MISS"))
      {
        GraphicsUtil.dither(bi, Color.RED, Color.RED, Color.WHITE);

      }


      sections.add( bi );

    }

    return GraphicsUtil.vertStack(sections, 8);


  }

}
