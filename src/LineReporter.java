
package duckutil.sign;

import com.google.common.collect.ImmutableList;
import java.util.List;

public abstract class LineReporter extends Thread
{
  private boolean done = false;
  private List<String> lines = null;
  private String label;

  public LineReporter(String label)
  {
    this.label = label;
    setDaemon(true);
    setName("LineReporter{" + label + "}");
  }


  public void run()
  {
    try
    {
      lines = computeLines();
    }
    catch(Throwable t)
    {
      t.printStackTrace();
      lines = ImmutableList.of(label + " exception");
    }
    done=true;
  }

  /** override this for single line */
  public String computeLine()
    throws Exception
  {
    return "undef";
  }

  /** override this for multiline */
  public List<String> computeLines()
    throws Exception
  {
    return ImmutableList.of(computeLine());
  }


  /**
   * Should immediately return a constant value
   */
  public String getTimeoutLine() {return label + ": timeout";}

  public List<String> returnLines()
  {
    if (isDone())
    {
      return lines;
    }
    return ImmutableList.of(getTimeoutLine());

  }

  public boolean isDone()
  {
    return done;
  }

}
