package duckutil.sign;

import duckutil.Config;
import java.util.Map;
import java.util.LinkedList;
import org.bson.Document;


public interface DBUtil
{
  Map<String, Object> getLatest(String index_base)
    throws Exception;

  Map<String, Object> getLatest(String collection, Map<String, String> filter_terms)
    throws Exception;

  public void close() throws Exception;
}
