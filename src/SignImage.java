package duckutil.sign;

import duckutil.Config;
import duckutil.ConfigFile;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.font.FontRenderContext;
import java.awt.Font;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;



public class SignImage
{
  public static void main(String args[]) throws Exception
  {
    new SignImage(new ConfigFile(args[0]) );

  }
  private DBUtil es_util;
  private Config config;

  private int body_font_size=38;
  //private Font body_font = new Font("Lucida Console", Font.PLAIN, body_font_size);
  private Font base_font = Font.createFont(Font.TRUETYPE_FONT, new File("font/CONSOLA.TTF"));
  private Font body_font = base_font. deriveFont( Font.PLAIN, body_font_size);

  public static final String MULTICAST_IPV6_ADDRESS="FFFF::E030:3E91";
  public static final int mcast_port=28917;


  public SignImage(Config config)
    throws Exception
  {
    this.config = config;
    config.require("node");
    List<String> node = config.getList("node");
    try
    {
      es_util = new MongoUtil(config);

      LinkedList<BufferedImage> lines = getLines();

      BufferedImage bi = new BufferedImage(600,400,BufferedImage.TYPE_INT_RGB);

      Graphics2D g = bi.createGraphics();
      //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

      Color background = new Color(255,255,255);

      g.setColor(background);

      g.fillRect(0,0,600,400);

      int bi_x=10;
      int bi_y=10;

      for(BufferedImage ci : lines)
      {
        //System.out.println("Image: " + ci.getWidth() + " " + ci.getHeight());
        if (bi_y + ci.getHeight() > 400)
        {
          bi_y = 10;
          bi_x += 280;
        }
        g.drawImage(ci, null, bi_x, bi_y);
        bi_y+=ci.getHeight()+10;

      }

      ImageIO.write(bi, "PNG", new File("image/signimage.png"));

      multicastAnnounce();
    }
    finally
    {
      es_util.close();
    }
  }

  public void multicastAnnounce() throws Exception
  {
    MulticastSocket ds = new MulticastSocket(mcast_port);

    ds.setLoopbackMode(false);
    InetAddress group = InetAddress.getByName(MULTICAST_IPV6_ADDRESS);
    ds.joinGroup(group);

    byte[] buff = new byte[10];

    ds.send(new DatagramPacket(buff, buff.length, group, mcast_port));

  }

  public Color pickBackColor(int brightness)
  {
    Random rnd = new Random();
    while(true)
    {
      int r = rnd.nextInt(256);
      int g = rnd.nextInt(256);
      int b = rnd.nextInt(256);
      if (r+g+b <= brightness)
      return new Color(r,g,b);
    }

  }

  public LinkedList<BufferedImage> getLines()
    throws Exception
  {
    LinkedList<LineReporter> reporters = new LinkedList<LineReporter>();

    int total_wait = 25000;

    //reporters.add( new ReporterDate("EEE MM-dd hh:mm") );
    reporters.add( new ReporterDate("EEE MM.dd") );
    reporters.add( new ReporterDate("HH:mm") );
    reporters.add( new ReporterPrice(es_util, "BTC"));
    reporters.add( new ReporterPrice(es_util, "BCH"));
    reporters.add( new ReporterPrice(es_util, "ETH"));
    reporters.add( new ReporterPrice(es_util, "SNOW"));
    reporters.add( new ReporterStockPrice(es_util, "VUG"));
    reporters.add( new ReporterLert());
    reporters.add( new ReporterLocalWeather(config, es_util) );
    reporters.add( new ReporterAQI(config, es_util) );
    reporters.add( new ReporterCountdown("DL", 1732395600L));
    reporters.add( new ReporterNWSAlert(config));
    //reporters.add( new ReporterNWSForcast(config,4));
    reporters.add(new ReporterOpenWeather(config));

    for(LineReporter r : reporters)
    {
      r.start();
    }

    for(int i=0; i<total_wait; i+=100)
    {
      Thread.sleep(100);
      int not_done = 0;
      for(LineReporter r : reporters)
      {
        if (!r.isDone())
        {
          not_done++;
        }
      }
      if (not_done == 0) break;
    }

    LinkedList<BufferedImage> lines = new LinkedList<>();
    for(LineReporter r : reporters)
    {
      lines.add(r.getRender(body_font));
    }
    return lines;
  }
}
