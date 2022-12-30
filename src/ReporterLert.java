package duckutil.sign;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ReporterLert extends LineReporter
{
  public ReporterLert()
  {
    super("lert");
  }

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
    
    return last_line;
  }

}
