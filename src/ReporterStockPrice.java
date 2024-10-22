package duckutil.sign;

import java.util.Map;
import java.util.TreeMap;
import java.text.DecimalFormat;

public class ReporterStockPrice extends LineReporter
{
  private String ticker;
  private DBUtil es_util;

  public ReporterStockPrice(DBUtil es_util, String ticker)
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
    double price = Double.parseDouble(doc.get("price").toString());
    DecimalFormat df = new DecimalFormat("0.00");
    return ticker + ": " + df.format(price);
  }

}
