package duckutil.sign;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.OutputStream;
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
		URL u = new URL("http://localhost:45781/");
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
    
    return last_line;
  }

}
