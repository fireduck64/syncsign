package duckutil.sign;


import duckutil.Config;
import java.util.Set;

public class ReporterNWSAlert extends LineReporter
{
  private Config config;
  public ReporterNWSAlert(Config config)
  {
    super("nws_alert");
    this.config = config;
  }

  @Override
  public String computeLine() throws Exception
  {
    StringBuilder sb = new StringBuilder();
    Set<String> hz = NWSUtil.getAlertHazards(config);
    if (hz.size() == 0)
    {
      return "no nws alert";
    }
    sb.append("HAZ");
    for(String h : NWSUtil.getAlertHazards(config))
    {
      h = h.replace("Hydrologic Outlook","Flood");
      h = h.replace("Flood Watch","Flood");
      sb.append(" ");
      sb.append(h);
    }

    return sb.toString();


  }

}
