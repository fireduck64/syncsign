package duckutil.sign;

import java.util.Map;
import java.util.TreeMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList;
import java.util.List;

public class ReporterCountdown extends LineReporter
{
  private final String name;
  private final long target;

  // Get target with a command like
  //  date -d "20220822T15" +%s
  public ReporterCountdown(String name, long target)
  {
    super("countdown_" + name);
    this.name = name;
    this.target = target * 1000;
  }

  @Override
  public String computeLine() throws Exception
  {
    double delta_ms = target - System.currentTimeMillis();
    double delta_day = Math.floor(delta_ms / 1000.0 / 86400.0);
    delta_ms -= delta_day * 1000.0 * 86400.0;
    double delta_hour = Math.floor(delta_ms / 1000.0 / 3600.0);
    int day = (int)Math.round(delta_day);
    int hour = (int)Math.round(delta_hour);

   return String.format("%s: %dd%dh", name, day, hour );

  }

}
