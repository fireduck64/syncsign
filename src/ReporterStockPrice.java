package duckutil.sign;

import java.util.Map;
import java.util.TreeMap;

public class ReporterStockPrice extends LineReporter
{
  private String ticker;
  private ESUtil es_util;

  public ReporterStockPrice(ESUtil es_util, String ticker)
  {
    super("price-" + ticker);
    this.ticker = ticker;
    this.es_util = es_util;
  }

  @Override
  public String computeLine() throws Exception
  {
    Map<String, String> filter = new TreeMap<>();
    filter.put("ticker", ticker);

    Map<String, Object> doc = es_util.getLatest("stockprice", filter);
    return ticker + " - " + doc.get("price").toString();
  }

}
