package duckutil.sign;

import java.text.DecimalFormat;
import java.util.Map;

public class ReporterPrice extends LineReporter
{
  private String ticker;
  private DBUtil es_util;

  public ReporterPrice(DBUtil es_util, String ticker)
  {
    super("price-" + ticker);
    this.ticker = ticker;
    this.es_util = es_util;
  }

  @Override
  public String computeLine() throws Exception
  {
    Map<String, Object> doc = es_util.getLatest("cryptoprice");
    //System.out.println(doc);
    double price = Double.parseDouble(doc.get(ticker).toString());
    DecimalFormat df = new DecimalFormat("0.00");
    if (price > 1000.0)
    {
      df = new DecimalFormat("0");
    }
    return ticker + ": " + df.format(price);
  }

}
