package duckutil.sign;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ReporterPrice extends LineReporter
{
  private String ticker;
  private ESUtil es_util;

  public ReporterPrice(ESUtil es_util, String ticker)
  {
    super("price-" + ticker);
    this.ticker = ticker;
    this.es_util = es_util;
  }

  @Override
  public String computeLine() throws Exception
  {
    Map<String, Object> doc = es_util.getLatest("cryptoprice");
    return ticker + " - " + doc.get(ticker).toString();
  }

}
