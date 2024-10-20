package duckutil.sign;

import java.util.Map;
import java.text.DecimalFormat;

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
    return ticker + " - " + df.format(price);
  }

}
