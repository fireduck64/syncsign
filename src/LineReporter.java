package duckutil.sign;

import com.google.common.collect.ImmutableList;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public abstract class LineReporter extends Thread
{
  private boolean done = false;
  private List<String> lines = null;
  private String label;
  private boolean err=false;

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
      err=true;
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

  public BufferedImage getRender(Font font)
    throws Exception
  {

    if (isDone())
    {
      if (err)
      {
        LinkedList<BufferedImage> lines = new LinkedList<>();

        for(String line  : returnLines())
        {
          lines.add(GraphicsUtil.renderText(Color.WHITE, Color.RED, font, line));
        }

        return GraphicsUtil.vertStack(lines, 10);
      }
      else
      {
        return getSuccessRender(font);
      }
    }
    else
    {
      return GraphicsUtil.renderText(Color.WHITE, Color.RED, font, getTimeoutLine());
    }

  }
  public BufferedImage getSuccessRender(Font font)
    throws Exception
  {

    LinkedList<BufferedImage> lines = new LinkedList<>();

    for(String line  : returnLines())
    {
      lines.add(GraphicsUtil.renderText(Color.WHITE, Color.BLACK, font, line));
    }
    return GraphicsUtil.vertStack(lines, 8);

  }

  public boolean isDone()
  {
    return done;
  }

}
