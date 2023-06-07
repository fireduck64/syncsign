package duckutil.sign;

import duckutil.Config;
import java.net.URL;
import java.util.Map;
import java.util.LinkedList;
import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;


public class MongoUtil implements DBUtil
{
  private final MongoDatabase mongo_db;

  public MongoUtil(Config config)
    throws Exception
  {

    String mongo_uri = config.require("mongo_uri");
    MongoClient mongo = MongoClients.create(mongo_uri);
    mongo_db = mongo.getDatabase("metric");

  }

  public Map<String, Object> getLatest(String index_base)
    throws Exception
  {
    return getLatest(index_base, null);
  }
  public Map<String, Object> getLatest(String collection, Map<String, String> filter_terms)
    throws Exception
  {
    MongoCollection<Document> coll = mongo_db.getCollection(collection);

    Document filter_doc = new Document();
    if (filter_terms != null)
    {
      for(Map.Entry<String, String> me : filter_terms.entrySet())
      {
        filter_doc.put(me.getKey(), me.getValue());
      }
    }
    Document sort = new Document();
    sort.put("createdAt", -1);

    LinkedList<Map<String, Object>> lst = new LinkedList<>();
    for(Document d : coll.find(filter_doc).sort(sort).limit(25))
    {
      return d;
    }
    return null;

    
  }

  public void close()
    throws Exception
  {

  }

}
