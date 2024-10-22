package duckutil.sign;

import java.util.Map;

public interface DBUtil
{
  Map<String, Object> getLatest(String index_base)
    throws Exception;

  Map<String, Object> getLatest(String collection, Map<String, String> filter_terms)
    throws Exception;

  public void close() throws Exception;
}
