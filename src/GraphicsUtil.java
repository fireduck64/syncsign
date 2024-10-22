package duckutil.sign;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

public class GraphicsUtil
{
  public static BufferedImage trim(BufferedImage in, Color back)
  {
    int min_x = in.getWidth();
    int min_y = in.getHeight();
    int max_x = 0;
    int max_y = 0;

    int back_rgb = back.getRGB();

    for(int x=0; x<in.getWidth(); x++)
    for(int y=0; y<in.getHeight(); y++)
    {
      int v = in.getRGB(x,y);
      if (v != back_rgb)
      {
        min_x = Math.min(x, min_x);
        min_y = Math.min(y, min_y);
        max_x = Math.max(x, max_x);
        max_y = Math.max(y, max_y);
      }
    }

    if (min_x > 0) min_x--;
    if (min_y > 0) min_y--;
    if (max_x + 1 < in.getWidth()) max_x++;
    if (max_y + 1 < in.getHeight()) max_y++;


    if ((max_x - min_x >= 0) && (max_y - min_y >= 0))
    {
      return in.getSubimage(min_x, min_y, max_x - min_x + 1, max_y - min_y + 1);
    }
    else
    {
      return in.getSubimage(0,0,1,1);
    }

  }

  public static BufferedImage renderText(Color back_color, Color fore_color, Font font, String words)
  {
     BufferedImage bi = new BufferedImage(600,400,BufferedImage.TYPE_INT_RGB);

     Graphics2D g = bi.createGraphics();
     g.setColor(back_color);
     g.fillRect(0,0,600,400);
     g.setFont(font);
     g.setColor(fore_color);
     g.drawString( words, 0, 40 );

     return trim(bi, back_color);

  }

  public static BufferedImage horzStack(List<BufferedImage> lst, int space)
  {
    int total_x = 0;
    int total_y = 0;

    for(BufferedImage bi : lst)
    {
      if (total_x != 0) total_x += space;
      total_x += bi.getWidth();
      total_y = Math.max(total_y, bi.getHeight());
    }
    BufferedImage out = new BufferedImage(total_x, total_y, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = out.createGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0,0, total_x, total_y);
    int x = 0;
    for(BufferedImage bi : lst)
    {
      if (x != 0) x += space;

      g.drawImage(bi, null, x, 0);
      x += bi.getWidth();
    }
    return out;

  }
  public static BufferedImage vertStack(List<BufferedImage> lst, int space)
  {
    int total_x = 0;
    int total_y = 0;

    for(BufferedImage bi : lst)
    {
      if (total_y != 0) total_y += space;
      total_y += bi.getHeight();
      total_x = Math.max(total_x, bi.getWidth());
    }
    BufferedImage out = new BufferedImage(total_x, total_y, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = out.createGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0,0, total_x, total_y);
    int y = 0;
    for(BufferedImage bi : lst)
    {
      if (y != 0) y += space;

      g.drawImage(bi, null, 0, y);
      y += bi.getHeight();
    }
    return out;

  }

  public static void dither(BufferedImage bi, Color src, Color dst1, Color dst2)
  {
    for(int i=0; i<bi.getWidth(); i++)
    for(int j=0; j<bi.getHeight(); j++)
    {
      Color n = new Color(bi.getRGB(i,j));
      if (n.equals(src))
      {
        if ((i+j) % 2 == 0)
        {
          //System.out.print("A");
          bi.setRGB(i,j, dst2.getRGB());
        }
        else
        {
          //System.out.print("B");
          bi.setRGB(i,j, dst1.getRGB());
        }
      }
    }

  }


}
