package duckutil.sign;

import duckutil.ConfigFile;
import duckutil.Config;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONArray;

import java.util.LinkedList;
import java.util.Collection;


public class FontSelect
{
  public static void main(String args[]) throws Exception
  {
    new FontSelect(new ConfigFile(args[0]) );

  }


  public Collection<String> getFontList(int size)
  {
    LinkedList<String> lst = new LinkedList<>();

    lst.add("DDIN_16");
    lst.add("DDIN_24");
    lst.add("DDIN_CONDENSED_16");
    lst.add("DDIN_CONDENSED_24");
    //lst.add("CHARRIOT_10");
    //lst.add("APRILSANS_10");
    //lst.add("APRILSANS_16");
    lst.add("APRILSANS_24");
    //lst.add("ROBOTO_CONDENSED_24");
    lst.add("ROBOTO_SLAB_24");
    lst.add("YANONE_KAFFEESATZ_24_B");
    lst.add("KAUSHAN_SCRIPT_20");
    lst.add("SRIRACHA_24");

    /*lst.add("DDIN_" + size);
    lst.add("DDIN_CONDENSED_" + size);
    lst.add("CHARRIOT_" + size);
    lst.add("APRILSANS_" + size);
    lst.add("ROBOTO_CONDENSED_" + size);
    lst.add("ROBOTO_SLAB_" + size);
    
    lst.add("YANONE_KAFFEESATZ_"+size+"_B");
    lst.add("KAUSHAN_SCRIPT_" + size);
    lst.add("SRIRACHA_" + size);
    lst.add("DORSA_" + size);
    lst.add("LONDRINA_OUTLINE_" + size);
    lst.add("BUNGEE_SHADE_" + size);*/

    return lst;

  }

  public FontSelect(Config config)
    throws Exception
  {
    String node = config.require("node");


    JSONObject doc = new JSONObject();
    JSONObject layout = new JSONObject();
    doc.put("layout", layout);

    JSONObject background = new JSONObject();
    layout.put("background", background);
    
    JSONArray items = new JSONArray();
    layout.put("items", items);


    int font_size = 24;
    int box_size=font_size+6;
    int y = 2;
    for(String f : getFontList(font_size))
    {
      JSONObject line = new JSONObject();
      line.put("type", "TEXT");
      JSONObject data = new JSONObject();
      line.put("data", data);

      data.put("text", "ABCDEF abced 01234.8 " + f);
      data.put("font", f);
      data.put("text-align", "LEFT");
      data.put("id",f);

      JSONObject block = new JSONObject();

      data.put("block", block);

      block.put("x", 10);
      block.put("y", y);
      block.put("w", 380);
      block.put("h", box_size);

      items.add(line);
      y+=box_size;

    }

    System.out.println(doc);
    int code = Render.render(config, node, doc);
    System.err.print(code);


  }
}
