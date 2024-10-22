package duckutil.sign;

import duckutil.Config;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import net.minidev.json.JSONObject;

public class Render
{

  public static int render(Config config, String node, JSONObject view)
    throws Exception
  {
    String host = config.require("host");
    String key = config.require("key");

    String url = String.format("http://%s/key/%s/nodes/%s/renders", host, key, node);
    System.err.println(url);
    URL u = new URL(url);

    HttpURLConnection connection = (HttpURLConnection) u.openConnection();
    connection.setRequestMethod("POST");
    connection.setDoOutput(true);
    connection.setDoInput(true);
    connection.setInstanceFollowRedirects(false);

    connection.setRequestProperty("Content-Type", "application/json");

    JSONObject doc = new JSONObject();

    OutputStream wr = connection.getOutputStream ();
    wr.write(view.toJSONString().getBytes());
    wr.flush();
    wr.close();

    int code = connection.getResponseCode();

    return code;


  }

}
