package models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

// TODO: index on sensorTypeUri, maybe owner too?
@Document(collection = "sensor_types")
public class SensorType {

  @Id
  private String sensorTypeId;
  private String sensorTypeUri;
  private int instanceCount;
  private String owner; 
  private String sensorCategory;
  private String manufacturer;
  private String version;
  private double maximumValue;
  private double minimumValue;
  private String unit;
  private String interpreter;

  public SensorType(String sensorTypeUri, String owner, String sensorCategory, String manufacturer, String version,
                    double maximumValue, double minimumValue, String unit, String interpreter) {
    this.sensorTypeUri = sensorTypeUri;
    this.instanceCount = 0;
    this.owner = owner;
    this.sensorCategory = sensorCategory;
    this.manufacturer = manufacturer;
    this.version = version;
    this.maximumValue = maximumValue;
    this.minimumValue = minimumValue;
    this.unit = unit;
    this.interpreter = interpreter;
  }

  private static ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
  private static MongoOperations mongoOps = (MongoOperations) ctx.getBean("mongoTemplate");

  public static Query uriQuery(String sensorTypeUri) {
    return new Query(Criteria.where("sensorTypeUri").is(sensorTypeUri));
  }

  public static SensorType getSensorType(String sensorTypeUri) {
    Query q = SensorType.uriQuery(sensorTypeUri);
    SensorType st = mongoOps.findOne(q, SensorType.class);
    return st;
  }

  public static Query ownerQuery(String owner) {
    return new Query(Criteria.where("owner").is(owner));
  }

  public static SensorType changeInstanceCount(String sensorTypeUri, boolean increment) {
    SensorType st = SensorType.getSensorType(sensorTypeUri);
    if (st == null) {
      return null;
    }

    if (increment) {
      st.incrementInstanceCount();
    }
    else{
      st.decrementInstanceCount();
    }
    mongoOps.save(st);
    return st;
  }

  public static SensorType incrementInstanceCount(String sensorTypeUri) {
    return SensorType.changeInstanceCount(sensorTypeUri, true);
  }

  public static SensorType decrementInstanceCount(String sensorTypeUri) {
    return SensorType.changeInstanceCount(sensorTypeUri, false);
  }

  // CRUD operations
  public static SensorType create(String requestUser, SensorType newST) {
    User user = User.getUser(requestUser);
    String sensorTypeOwner = newST.getOwner();
    String sensorTypeUri = newST.getSensorTypeUri();

    SensorType st = SensorType.getSensorType(sensorTypeUri);

    if (user == null || st != null || !requestUser.equals(sensorTypeOwner)) {
      return null;
    }

    mongoOps.save(newST);
    st = SensorType.getSensorType(sensorTypeUri);
    return st;
  }

  public static List<SensorType> getAll(String requestUser) {
    User user = User.getUser(requestUser);

    if (user == null) {
      return new ArrayList<SensorType>();
    }

    List<SensorType> sensorTypeList = mongoOps.find(SensorType.ownerQuery(requestUser), SensorType.class);
    return sensorTypeList;
  }

  public static SensorType delete(String requestUser, String sensorTypeUri) {
    User user = User.getUser(requestUser);
    SensorType st = SensorType.getSensorType(sensorTypeUri);

    if (user == null || st == null || !requestUser.equals(st.getOwner())) {
      return null;
    }
  
    if (st.getInstanceCount() != 0) { // We cannot delete if there are existing sensors of this type
      return null;
    }

    // We cannot delete if other device types are using this sensor type
    List<DeviceType> deviceTypeList = DeviceType.getAll(requestUser);
    for (DeviceType dt : deviceTypeList) {
      List<String> sensorTypeUriList = Arrays.asList(dt.getSensorTypeUris());
      if (sensorTypeUriList.contains(sensorTypeUri)) {
        return null;
      }
    }

    mongoOps.remove(SensorType.uriQuery(sensorTypeUri), SensorType.class);
    return st;
  }

  public String getSensorTypeId() {
    return this.sensorTypeId;
  }

  public String getSensorTypeUri() {
    return this.sensorTypeUri;
  }

  public int getInstanceCount() {
    return this.instanceCount;
  }

  public void incrementInstanceCount() {
    this.instanceCount += 1;
  }

  public void decrementInstanceCount() {
    this.instanceCount -= 1;
  }

  public String getOwner() {
    return this.owner;
  }

  public String getSensorCategory() {
    return this.sensorCategory;
  }

  public String getManufacturer() {
    return this.manufacturer;
  }

  public String getVersion() {
    return this.version;
  }

  public double getMaximumValue() {
    return this.maximumValue;
  }

  public double getMinimumValue() {
    return this.minimumValue;
  }

  public String getUnit() {
    return this.unit;
  }

  public String getInterpreter() {
    return this.interpreter;
  }
}

