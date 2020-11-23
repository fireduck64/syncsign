package duckutil.sign;

import java.util.Map;

public class ReporterEth2 extends LineReporter
{
  private ESUtil es_util;

  public ReporterEth2(ESUtil es_util)
  {
    super("eth2");
    this.es_util = es_util;
  }

  @Override
  public String computeLine() throws Exception
  {
    Map<String, Object> doc = es_util.getLatest("lighthouse-b");
    return "valid - " + doc.get("validators").toString();
  }

}
