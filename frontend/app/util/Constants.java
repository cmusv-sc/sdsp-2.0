package util;

public class Constants {

  public static final String HOSTNAME = "http://localhost:9000/";

  // User
  public static final String GET_USER_ROUTE = "user/get";
  public static final String AUTHENTICATE_USER_ROUTE = "authenticate";
  public static final String CREATE_USER_ROUTE = "createUser";
  public static final String CHANGE_PASSWORD_ROUTE = "changePassword";

  // DeviceGroup
  public static final String GET_DEVICE_GROUP_ROUTE = "deviceGroup/get";
  //public static final String GET_DEVICE_GROUP_BY_ID_ROUTE = "deviceGroup/getById";
  public static final String GET_ALL_DEVICE_GROUPS_ROUTE = "deviceGroup/getAll";
  public static final String GET_DEVICE_GROUP_DEVICES_ROUTE = "deviceGroup/getDevices";
  public static final String ADD_DEVICE_GROUP_DEVICE_ROUTE = "deviceGroup/addDevice";
  public static final String DELETE_DEVICE_GROUP_DEVICE_ROUTE = "deviceGroup/deleteDevice";
  public static final String DELETE_DEVICE_GROUP_ROUTE = "deviceGroup/delete";
  public static final String CREATE_DEVICE_GROUP_ROUTE = "createDeviceGroup";

  // UserGroup
  public static final String GET_OWNED_USER_GROUPS_ROUTE = "userGroup/getOwnedGroups";
  public static final String GET_MEMBER_USER_GROUPS_ROUTE = "userGroup/getMemberGroups";
  public static final String GET_USER_GROUP_ROUTE = "userGroup/get";
  public static final String GET_USER_GROUP_DEVICES_ROUTE = "userGroup/getDevices";
  public static final String ADD_USER_GROUP_MEMBER_ROUTE = "userGroup/addMember";
  public static final String DELETE_USER_GROUP_MEMBER_ROUTE = "userGroup/deleteMember";
  public static final String ADD_USER_GROUP_DEVICE_ROUTE = "userGroup/addDevice";
  public static final String DELETE_USER_GROUP_DEVICE_ROUTE = "userGroup/deleteDevice";
  public static final String DELETE_USER_GROUP_ROUTE = "userGroup/delete";
  public static final String CREATE_USER_GROUP_ROUTE = "createUserGroup";

  // SensorType
  public static final String GET_ALL_SENSOR_TYPES_ROUTE = "sensorType/getAll";
  public static final String DELETE_SENSOR_TYPE_ROUTE = "sensorType/delete";
  public static final String CREATE_SENSOR_TYPE_ROUTE = "createSensorType";

  // DeviceType
  public static final String GET_ALL_DEVICE_TYPES_ROUTE = "deviceType/getAll";
  public static final String DELETE_DEVICE_TYPE_ROUTE = "deviceType/delete";
  public static final String CREATE_DEVICE_TYPE_ROUTE = "createDeviceType";

  // Sensor
  public static final String GET_SENSOR_ROUTE = "sensor/get";
  public static final String GET_DEVICE_SENSORS_ROUTE = "sensor/gets";

  // Device
  public static final String GET_DEVICE_ROUTE = "device/get";
  public static final String DELETE_DEVICE_ROUTE = "device/delete";
  public static final String CREATE_DEVICE_ROUTE = "createDevice";
}
