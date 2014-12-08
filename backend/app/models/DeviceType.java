package models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.ArrayList;

// TODO: index on deviceTypeUri, maybe owner too?
@Document(collection = "device_types")
public class DeviceType {

  @Id
  private String deviceTypeId;
  private String deviceTypeUri;
  private int instanceCount;
  private String owner;
  private String[] sensorTypeUris;
  private String manufacturer;
  private String version;

  public DeviceType(String deviceTypeUri, String owner, String[] sensorTypeUris, String manufacturer, String version) {
    this.deviceTypeUri = deviceTypeUri;
    this.owner = owner;
    this.instanceCount = 0;
    this.sensorTypeUris = sensorTypeUris;
    this.manufacturer = manufacturer;
    this.version = version;
  }

  private static ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
  private static MongoOperations mongoOps = (MongoOperations) ctx.getBean("mongoTemplate");

  public static Query uriQuery(String deviceTypeUri) {
    return new Query(Criteria.where("deviceTypeUri").is(deviceTypeUri));
  }

  public static DeviceType getDeviceType(String deviceTypeUri) {
    Query q = DeviceType.uriQuery(deviceTypeUri);
    DeviceType dt = mongoOps.findOne(q, DeviceType.class);
    return dt;
  }

  public static Query ownerQuery(String owner) {
    return new Query(Criteria.where("owner").is(owner));
  }

  public static DeviceType changeInstanceCount(String deviceTypeUri, boolean increment) {
    DeviceType dt = DeviceType.getDeviceType(deviceTypeUri);
    if (dt == null) {
      return null;
    }

    if (increment) {
      dt.incrementInstanceCount();
    }
    else {
      dt.decrementInstanceCount();
    }
    mongoOps.save(dt);
    return dt;
  }

  public static DeviceType incrementInstanceCount(String deviceTypeUri) {
    return DeviceType.changeInstanceCount(deviceTypeUri, true);
  }

  public static DeviceType decrementInstanceCount(String deviceTypeUri) {
    return DeviceType.changeInstanceCount(deviceTypeUri, false);
  }

  // CRUD operations
  public static DeviceType create(String requestUser, DeviceType newDT) {
    User user = User.getUser(requestUser);
    String deviceTypeOwner = newDT.getOwner();
    String deviceTypeUri = newDT.getDeviceTypeUri();

    DeviceType dt = DeviceType.getDeviceType(deviceTypeUri);

    if (user == null || dt != null || !requestUser.equals(deviceTypeOwner)) {
      return null;
    }

    // Make sure all sensor types are already created, with the same owner
    for (String sensorTypeUri : newDT.getSensorTypeUris()) {
      SensorType st = SensorType.getSensorType(sensorTypeUri);
      if (st == null || !st.getOwner().equals(requestUser)) {
        return null;
      }
    }

    mongoOps.save(newDT);
    dt = DeviceType.getDeviceType(deviceTypeUri);
    return dt;
  }

  public static List<DeviceType> getAll(String requestUser) {
    User user = User.getUser(requestUser);

    if (user == null) {
      return new ArrayList<DeviceType>();
    }

    List<DeviceType> deviceTypeList = mongoOps.find(DeviceType.ownerQuery(requestUser), DeviceType.class);
    return deviceTypeList;
  }

  public static DeviceType delete(String requestUser, String deviceTypeUri) {
    User user = User.getUser(requestUser);
    DeviceType dt = DeviceType.getDeviceType(deviceTypeUri);

    if (user == null || dt == null || !requestUser.equals(dt.getOwner())) {
      return null;
    }

    if (dt.getInstanceCount() != 0) { // We cannot delete if there are existing devices of this type
      return null;
    }

    mongoOps.remove(DeviceType.uriQuery(deviceTypeUri), DeviceType.class);
    return dt;
  }

  public String getDeviceTypeUri() {
    return this.deviceTypeUri;
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

  public String[] getSensorTypeUris() {
    return this.sensorTypeUris;
  }

  public String getManufacturer() {
    return this.manufacturer;
  }

  public String getVersion() {
    return this.version;
  }
}
