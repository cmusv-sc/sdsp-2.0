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

// TODO: index on deviceUri
@Document(collection = "devices")
public class Device {

  @Id
  private String deviceId; 
  private String deviceUri;
  private String deviceTypeUri;
  private String[] sensorUris;
  private String owner; // username as the identifier
  private String deviceUserDefinedFields;

  private static ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
  private static MongoOperations mongoOps = (MongoOperations) ctx.getBean("mongoTemplate");

  public Device(String deviceUri, String deviceTypeUri, String[] sensorUris, 
                String owner, String deviceUserDefinedFields) {
    this.deviceUri = deviceUri;
    this.deviceTypeUri = deviceTypeUri;
    this.sensorUris = sensorUris;
    this.owner = owner;
    this.deviceUserDefinedFields = deviceUserDefinedFields;
  }

  public static Query uriQuery(String deviceUri) {
    return new Query(Criteria.where("deviceUri").is(deviceUri));
  }

  public static Device getDevice(String deviceUri) {
    Query getDeviceQuery = Device.uriQuery(deviceUri);
    Device result = mongoOps.findOne(getDeviceQuery, Device.class);
    return result;
  }
  
  // CRUD
  
  public static Device get(String requestUser, String deviceUri) {
    Device device = Device.getDevice(deviceUri);
    User user = User.getUser(requestUser);

    if (user == null || device == null) {
      return null;
    }

    if (requestUser.equals(device.getOwner())) {
      return device;
    }

    List<Device> sharedDevices = DeviceGroup.getSharedDevices(requestUser, requestUser);
    for (Device d : sharedDevices) {
      if (deviceUri.equals(d.getDeviceUri())) {
        return d;
      }
    }
    return null;
  }

  // To register a device, we first register all sensors and then the device itself
  public static Device create(String requestUser, Device newDevice, List<Sensor> sensors) {
    String deviceUri = newDevice.getDeviceUri();
    String deviceOwner = newDevice.getOwner();
    Device device = Device.getDevice(deviceUri);
    User user = User.getUser(requestUser);
    if (device != null || user == null || !requestUser.equals(deviceOwner)) {
      return null;
    }

    // Object consistency


    // Sensor Uris must all be unique
    List<String> sensorUriList = new ArrayList<String>();
    for (Sensor s : sensors) {
      String sensorUri = s.getSensorUri();
      if (sensorUriList.contains(sensorUri)) {
        return null;
      }
      sensorUriList.add(sensorUri);
    }

    // Assumes ordering of sensors is same as device type
    String[] sensorTypeUris = DeviceType.getDeviceType(newDevice.getDeviceTypeUri()).getSensorTypeUris();
    if (sensorTypeUris.length != sensors.size()) {
      return null;
    }
    int arrIndex = 0;
    for (Sensor s : sensors) {
      String sensorOwner = s.getOwner();
      String sensorUri = s.getSensorUri();
      String sensorDeviceUri = s.getDeviceUri();
      Sensor dbSensor = Sensor.getSensor(sensorUri);

      if (!sensorOwner.equals(requestUser) || !sensorDeviceUri.equals(deviceUri) || dbSensor != null){
        return null;
      }
     
      String sensorTypeUri = s.getSensorTypeUri();
      if (!sensorTypeUris[arrIndex].equals(sensorTypeUri)) {
        return null;
      }
      arrIndex += 1;
    }

    // Register all sensors first
    for (Sensor s : sensors) {
      Sensor.create(requestUser, s);
    }

    String deviceTypeUri = newDevice.getDeviceTypeUri();
    DeviceType.incrementInstanceCount(deviceTypeUri);
    mongoOps.save(newDevice);
    // Every newly created devices is in the Personal DeviceGroup
    DeviceGroup.addDeviceHelper(deviceOwner, deviceOwner, DeviceGroup.DefaultGroup.Personal.toString(), newDevice.getDeviceUri());
    return Device.getDevice(deviceUri);
  }

  public static Device delete(String requestUser, String deviceUri) {
    Device device = Device.getDevice(deviceUri);
    User user = User.getUser(requestUser);
    if (device == null || user == null || !requestUser.equals(device.getOwner())) {
      return null;
    }

    for (String sensorUri : device.getSensorUris()) {
      Sensor.delete(requestUser, sensorUri);
    }
    String deviceTypeUri = device.getDeviceTypeUri();
    DeviceType.decrementInstanceCount(deviceTypeUri);
    mongoOps.remove(Device.uriQuery(deviceUri), Device.class);
    return device;
  }

  public String getDeviceUri() {
    return this.deviceUri;
  }

  public String getDeviceTypeUri() {
    return this.deviceTypeUri;
  }

  public String[] getSensorUris() {
    return this.sensorUris;
  }

  public String getOwner() {
    return this.owner;
  }

  public String getDeviceUserDefinedFields() {
    return this.deviceUserDefinedFields;
  }
}
