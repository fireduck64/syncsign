package duckutil.sign;


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
