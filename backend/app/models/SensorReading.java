package models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.domain.Sort;

import java.util.List;

// TODO: index by sensorUri, timestamp
@Document(collection = "sensor_readings")
public class SensorReading {

  @Id
  private String sensorReadingId;
  private String sensorUri;
  private String value;
  private long timestamp; // unix epoch
  public Location location;

  private static ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
  private static MongoOperations mongoOps = (MongoOperations) ctx.getBean("mongoTemplate");

  public SensorReading(String sensorUri, String value, long timestamp) {
    this(sensorUri, value, timestamp, Location.INVALID);
  }

  public SensorReading(String sensorUri, String value, long timestamp, Location location) {
    this.sensorUri = sensorUri;
    this.value = value;
    this.timestamp = timestamp;
    this.location = location;
  }

  public static Query latestQuery(String sensorUri) {
    return new Query(Criteria.where("sensorUri").is(sensorUri))
      .limit(1)
      .with(new Sort(Sort.Direction.DESC, "timestamp"));
  }

  public static SensorReading latestReading(String sensorUri) {
    Query latestQuery = SensorReading.latestQuery(sensorUri);
    List<SensorReading> result = mongoOps.find(latestQuery, SensorReading.class);

    if (result.isEmpty()) {
      return null;
    }

    return result.get(0); 
  }

  public static List<SensorReading> latestDeviceReadings(String deviceURI) {
    return null;
  }

  public static List<SensorReading> latestDeviceGroupReadings(User owner, String deviceGroupName) {
    return null;
  }

  public String getSensorUri() {
    return sensorUri;
  }

  public String getValue() {
    return value;
  }

  public long getTimestamp() {
    return timestamp;
  }
  
  public Location getLocation() {
    return location;
  }
}
