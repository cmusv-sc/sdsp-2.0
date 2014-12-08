package models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.ArrayList;

// TODO: create index on sensorUri, create index on deviceUri
@Document(collection = "sensors")
public class Sensor {

  @Id
  private String sensorId;
  private String sensorUri;
  private String sensorTypeUri;
  private String deviceUri;
  private String owner;
  private String sensorUserDefinedFields;

  private static ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
  private static MongoOperations mongoOps = (MongoOperations) ctx.getBean("mongoTemplate");

  public Sensor(String sensorUri, String sensorTypeUri, String deviceUri, String owner, String sensorUserDefinedFields) {
    this.sensorUri = sensorUri;
    this.sensorTypeUri = sensorTypeUri;
    this.deviceUri = deviceUri;
    this.owner = owner;
    this.sensorUserDefinedFields = sensorUserDefinedFields;
  }

  public static Query uriQuery(String sensorUri) {
    return new Query(Criteria.where("sensorUri").is(sensorUri));
  }

  public static Sensor getSensor(String sensorUri) {
    Query sensorQuery = Sensor.uriQuery(sensorUri);
    Sensor result = mongoOps.findOne(sensorQuery, Sensor.class);
    return result;
  }

  public static List<Sensor> getSensors(String deviceUri) {
    Query query = new Query(Criteria.where("deviceUri").is(deviceUri));
    List<Sensor> result = mongoOps.find(query, Sensor.class);
    return result;
  }


  // Create and delete are only used internally, as sensors must be defined within a device
  
  public static Sensor create(String requestUser, Sensor newSensor) {
    String sensorUri = newSensor.getSensorUri();
    Sensor sensor = Sensor.getSensor(sensorUri);
    User user = User.getUser(requestUser);
    if (sensor != null || user == null || !requestUser.equals(newSensor.getOwner())) {
      return null;
    }

    String sensorTypeUri = newSensor.getSensorTypeUri();
    SensorType.incrementInstanceCount(sensorTypeUri);
    mongoOps.save(newSensor);
    return Sensor.getSensor(sensorUri);
  }

  public static Sensor delete(String requestUser, String sensorUri) {
    Sensor sensor = Sensor.getSensor(sensorUri);
    User user = User.getUser(requestUser);
    if (sensor == null || user == null || !requestUser.equals(sensor.getOwner())) {
      return null;
    }

    String sensorTypeUri = sensor.getSensorTypeUri();
    SensorType.decrementInstanceCount(sensorTypeUri);
    mongoOps.remove(Sensor.uriQuery(sensorUri), Sensor.class);
    return sensor;
  }

  // CRUD
 
  public static List<Sensor> gets(String requestUser, String deviceUri) {
    List<Sensor> sensorList = new ArrayList<Sensor>();
    User user = User.getUser(requestUser);
    Device device = Device.getDevice(deviceUri);

    if (user == null || device == null) {
      return sensorList;
    }

    if (requestUser.equals(device.getOwner())) {
      return Sensor.getSensors(deviceUri);
    }
    
    List<Device> sharedDevices = DeviceGroup.getSharedDevices(requestUser, requestUser);
    for (Device d : sharedDevices) {
      if (deviceUri.equals(d.getDeviceUri())) {
        return Sensor.getSensors(deviceUri);
      }
    }
    return sensorList;
  }
  
  public static Sensor get(String requestUser, String sensorUri) {
    Sensor sensor = Sensor.getSensor(sensorUri);
    if (sensor == null) {
      return null;
    }

    List<Sensor> sensorList = Sensor.gets(requestUser, sensor.getDeviceUri());
    for (Sensor s : sensorList) {
      if (sensorUri.equals(s.getSensorUri())) {
        return s;
      }
    }
    return null;
  }
  
  public String getSensorId() {
    return this.sensorId;
  }

  public String getSensorUri() {
    return this.sensorUri;
  }

  public String getSensorTypeUri() {
    return this.sensorTypeUri;
  }

  public String getDeviceUri() {
    return this.deviceUri;
  }

  public String getOwner() {
    return this.owner;
  }

  public String getSensorUserDefinedFields() {
    return this.sensorUserDefinedFields;
  }
}
