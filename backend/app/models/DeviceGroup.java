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
import java.util.Set;
import java.util.HashSet;

// TODO: index on deviceGroupOwner 
@Document(collection = "device_groups")
public class DeviceGroup {

  public enum DefaultGroup {
    Personal, Shared
  }

  @Id
  private String deviceGroupId;
  private String deviceGroupOwner; // username
  private String deviceGroupName;
  private String[] deviceUris;

  public DeviceGroup(String deviceGroupOwner, String deviceGroupName) {
    this.deviceGroupOwner = deviceGroupOwner;
    this.deviceGroupName = deviceGroupName;
    this.deviceUris = new String[0];
  }

  private static ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
  private static MongoOperations mongoOps = (MongoOperations) ctx.getBean("mongoTemplate");

  public static Query idQuery(String deviceGroupId) {
    return new Query(Criteria.where("deviceGroupId").is(deviceGroupId));
  }

  public static DeviceGroup deviceGroupById(String deviceGroupId) {
    Query q = DeviceGroup.idQuery(deviceGroupId);
    DeviceGroup dg = mongoOps.findOne(q, DeviceGroup.class);
    return dg;
  }

  public static List<DeviceGroup> getDeviceGroups(String deviceGroupOwner) {
    List<DeviceGroup> dgList = new ArrayList<DeviceGroup>();
    User user = User.getUser(deviceGroupOwner);

    if (user == null) {
      return dgList;
    }

    String[] idArr = user.getDeviceGroupIds();

    for (String groupId : idArr) {
      DeviceGroup dg = DeviceGroup.deviceGroupById(groupId);
      if (dg != null) {
        dgList.add(dg);
      }
    }
    return dgList;
  }   

  public static DeviceGroup getDeviceGroup(String deviceGroupOwner, String deviceGroupName) {
    List<DeviceGroup> dgList = DeviceGroup.getDeviceGroups(deviceGroupOwner);
    for (DeviceGroup dg : dgList) {
      if (dg.getDeviceGroupName().equals(deviceGroupName)) {
        return dg;
      }
    }
    return null;
  }

  // CRUD operations

  public static DeviceGroup create(String requestUser, DeviceGroup newDG) {
    String deviceGroupOwner = newDG.getDeviceGroupOwner();
    String deviceGroupName = newDG.getDeviceGroupName();

    DeviceGroup dg = DeviceGroup.getDeviceGroup(deviceGroupOwner, deviceGroupName);

    if (dg != null || !requestUser.equals(deviceGroupOwner)) {
      return null;
    }

    mongoOps.save(newDG);
    Query q = new Query(Criteria.where("deviceGroupOwner").is(deviceGroupOwner).and("deviceGroupName").is(deviceGroupName));
    dg = mongoOps.findOne(q, DeviceGroup.class);
    User.addDeviceGroup(deviceGroupOwner, dg.getDeviceGroupId());
    return dg;
  }

  public static DeviceGroup get(String requestUser, String deviceGroupOwner, String deviceGroupName) {
    DeviceGroup dg = DeviceGroup.getDeviceGroup(deviceGroupOwner, deviceGroupName);
    if (dg == null || !requestUser.equals(deviceGroupOwner)) {
      return null;
    }
    return dg;
  }
  /*
  public static DeviceGroup get(String requestUser, String deviceGroupId) {
    DeviceGroup dg = DeviceGroup.deviceGroupById(deviceGroupId);
    // TODO: implement check when authentication mechanism provides requestUser
    //if (dg == null || !requestUser.equals(dg.getDeviceGroupOwner())) {
    if (dg == null) {
      return null;
    }
    return dg;
  }*/

  public static List<DeviceGroup> getAll(String requestUser, String deviceGroupOwner) {
    if (!requestUser.equals(deviceGroupOwner)) {
      return new ArrayList<DeviceGroup>();
    }
    return DeviceGroup.getDeviceGroups(deviceGroupOwner);
  }

  public static List<Device> getSharedDevices(String requestUser, String deviceGroupOwner) {
    List<Device> devices = new ArrayList<Device>();

    User user = User.getUser(deviceGroupOwner);
    if (user == null || !requestUser.equals(deviceGroupOwner)) {
      return devices;
    }

    Set<String> sharedDeviceUris = new HashSet<String>();
    List<String> allUserGroupIds = new ArrayList<String>();
    allUserGroupIds.addAll(Arrays.asList(user.getOwnedUserGroupIds()));
    allUserGroupIds.addAll(Arrays.asList(user.getMemberUserGroupIds()));

    for (String groupId : allUserGroupIds) {
      UserGroup ug = UserGroup.userGroupById(groupId);
      String[] deviceUris = ug.getDeviceUris();

      for (String deviceUri : deviceUris) {
        Device d = Device.getDevice(deviceUri);
        if (d != null && !d.getOwner().equals(deviceGroupOwner) && !sharedDeviceUris.contains(deviceUri)) {
          sharedDeviceUris.add(deviceUri);
          devices.add(d);
        }
      }
    }
    return devices; 
  }

  // Also clears out null refernces in set, that may occur when devices are deleted
  public static List<Device> getDevices(String requestUser, String deviceGroupOwner, String deviceGroupName) {

    if (deviceGroupName.equals(DeviceGroup.DefaultGroup.Shared.toString())) {
      return DeviceGroup.getSharedDevices(requestUser, deviceGroupOwner);
    }

    List<Device> devices = new ArrayList<Device>();

    DeviceGroup dg = DeviceGroup.getDeviceGroup(deviceGroupOwner, deviceGroupName);
    if (dg == null || !requestUser.equals(deviceGroupOwner)) {
      return devices;
    }

    List<String> deviceUriList = new ArrayList<String>(Arrays.asList(dg.getDeviceUris()));
    List<String> toRemove = new ArrayList<String>();

    for (String deviceUri : deviceUriList) {
      Device d = Device.getDevice(deviceUri);
      if (d == null) {
        toRemove.add(deviceUri);
      }
      else {
        devices.add(d);
      }
    }

    for (String deviceUri : toRemove) {
      deviceUriList.remove(deviceUri);
    }

    dg.setDeviceUris(deviceUriList.toArray(new String[deviceUriList.size()]));
    mongoOps.save(dg);
    return devices;
  }

  // Users cannot add to default groups
  // Creating a new device is automatically added to the Personal DeviceGroup
  public static DeviceGroup addDevice(String requestUser, String deviceGroupOwner, String deviceGroupName, String deviceUri) {
    if (deviceGroupName.equals(DefaultGroup.Personal.toString()) || deviceGroupName.equals(DefaultGroup.Shared.toString())) {
      return null;
    }
    return DeviceGroup.addDeviceHelper(requestUser, deviceGroupOwner, deviceGroupName, deviceUri);
  }

  public static DeviceGroup addDeviceHelper(String requestUser, String deviceGroupOwner, String deviceGroupName, String deviceUri) {
    DeviceGroup dg = DeviceGroup.getDeviceGroup(deviceGroupOwner, deviceGroupName);
    User requester = User.getUser(requestUser);
    Device device = Device.getDevice(deviceUri);

    if (dg == null || requester == null || device == null || !requestUser.equals(deviceGroupOwner)) {
      return null;
    }

    List<String> deviceUriList = new ArrayList<String>(Arrays.asList(dg.getDeviceUris()));
    if (deviceUriList.contains(deviceUri)) {
      return null;
    }

    deviceUriList.add(deviceUri);
    dg.setDeviceUris(deviceUriList.toArray(new String[deviceUriList.size()]));
    mongoOps.save(dg);
    return dg;
  }

  public static DeviceGroup deleteDevice(String requestUser, String deviceGroupOwner, String deviceGroupName, String deviceUri) {
    DeviceGroup dg = DeviceGroup.getDeviceGroup(deviceGroupOwner, deviceGroupName);
    User requester = User.getUser(requestUser);
    Device device = Device.getDevice(deviceUri);

    if (dg == null || requester == null || device == null || !requestUser.equals(deviceGroupOwner)) {
      return null;
    }

    List<String> deviceUriList = new ArrayList<String>(Arrays.asList(dg.getDeviceUris()));
    if (!deviceUriList.contains(deviceUri)) {
      return null;
    }

    deviceUriList.remove(deviceUri);
    dg.setDeviceUris(deviceUriList.toArray(new String[deviceUriList.size()]));
    mongoOps.save(dg);
    return dg;
  }

  public static DeviceGroup delete(String requestUser, String deviceGroupOwner, String deviceGroupName) {
    // We do not delete the default groups
    for (DefaultGroup dg : DeviceGroup.DefaultGroup.values()) {
      if (deviceGroupName.equals(dg.toString())) {
        return null;
      }
    }

    DeviceGroup dg = DeviceGroup.getDeviceGroup(deviceGroupOwner, deviceGroupName);
    if (dg == null || !requestUser.equals(deviceGroupOwner)) {
      return null;
    }

    String deviceGroupId = dg.getDeviceGroupId();
    User.deleteDeviceGroup(deviceGroupOwner, deviceGroupId);
    mongoOps.remove(DeviceGroup.idQuery(deviceGroupId), DeviceGroup.class);
    return dg;
  }

  public String getDeviceGroupId() {
    return this.deviceGroupId;
  }

  public String getDeviceGroupOwner() {
    return this.deviceGroupOwner;
  }

  public String getDeviceGroupName() {
    return this.deviceGroupName;
  }

  public String[] getDeviceUris() {
    return this.deviceUris;
  }

  public void setDeviceUris(String[] deviceUris) {
    this.deviceUris = deviceUris;
  }
}
