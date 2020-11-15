package duckutil.sign;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReporterBlank extends LineReporter
{
  public ReporterBlank()
  {
    super("blank");
  }

  @Override
  public String computeLine() throws Exception
  {
    return "";
  }

}
