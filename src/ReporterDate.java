package duckutil.sign;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReporterDate extends LineReporter
{
  private String form;
  public ReporterDate(String form)
  {
    super("date");
    this.form=form;
  }

  @Override
  public String computeLine() throws Exception
  {
    SimpleDateFormat sdf = new SimpleDateFormat(form);

    return sdf.format(new Date());
  }

}
