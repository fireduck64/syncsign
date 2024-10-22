package duckutil.sign;

import duckutil.Config;
import java.net.URL;
import java.util.Map;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

public class ESUtil implements AutoCloseable, DBUtil
{
  private final RestHighLevelClient es_client;

  public ESUtil(Config config)
    throws Exception
  {
    es_client = openElasticSearchClient(config);
  }
  protected RestHighLevelClient openElasticSearchClient(Config config)
    throws Exception
  {
    URL u = new URL(config.require("elasticsearch_url"));

    return new RestHighLevelClient(
      RestClient.builder(new HttpHost(u.getHost(),u.getPort()))
      );
  }

  public Map<String, Object> getLatest(String index_base)
    throws Exception
  {
    return getLatest(index_base, null);
  }
  public Map<String, Object> getLatest(String index_base, Map<String, String> filter_terms)
    throws Exception
  {
    SearchRequest req = new SearchRequest(index_base+"-*");

    QueryBuilder qb = null;
    if (filter_terms !=null)
    {
      BoolQueryBuilder bqb =  QueryBuilders.boolQuery();
      for(Map.Entry<String, String> me : filter_terms.entrySet())
      {
        bqb.must( QueryBuilders.matchQuery(me.getKey() +".keyword", me.getValue() )
          .operator( Operator.AND)
          .analyzer("keyword"));
      }
      qb = bqb;
    }
    req.source(
      new SearchSourceBuilder().size(25).sort("timestamp", SortOrder.DESC).query(qb)
      );

    SearchResponse resp = es_client.search(req,RequestOptions.DEFAULT);

    SearchHits hits = resp.getHits();
    for(SearchHit hit : hits)
    {
      Map<String,Object> source_doc = hit.getSourceAsMap();
      return source_doc;
    }
    return null;

  }

  public void close()
    throws Exception
  {
    es_client.close();
  }
}
