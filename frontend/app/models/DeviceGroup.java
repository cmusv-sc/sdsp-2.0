package models;

import util.*;

import com.fasterxml.jackson.databind.*;
import com.google.gson.Gson;

import java.util.*;

public class DeviceGroup {

  private String deviceGroupId;
  private String deviceGroupOwner; // username
  private String deviceGroupName;
  private String[] deviceUris;

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

  public static final String GET_DEVICE_GROUP = util.Constants.HOSTNAME + util.Constants.GET_DEVICE_GROUP_ROUTE;
  //public static final String GET_DEVICE_GROUP_BY_ID = util.Constants.HOSTNAME + util.Constants.GET_DEVICE_GROUP_BY_ID_ROUTE;
  public static final String GET_ALL_DEVICE_GROUPS = util.Constants.HOSTNAME + util.Constants.GET_ALL_DEVICE_GROUPS_ROUTE;
  public static final String GET_DEVICE_GROUP_DEVICES = util.Constants.HOSTNAME + util.Constants.GET_DEVICE_GROUP_DEVICES_ROUTE;
  public static final String ADD_DEVICE_GROUP_DEVICE = util.Constants.HOSTNAME + util.Constants.ADD_DEVICE_GROUP_DEVICE_ROUTE;
  public static final String DELETE_DEVICE_GROUP_DEVICE = util.Constants.HOSTNAME + util.Constants.DELETE_DEVICE_GROUP_DEVICE_ROUTE;
  public static final String DELETE_DEVICE_GROUP = util.Constants.HOSTNAME + util.Constants.DELETE_DEVICE_GROUP_ROUTE;
  public static final String CREATE_DEVICE_GROUP = util.Constants.HOSTNAME + util.Constants.CREATE_DEVICE_GROUP_ROUTE;

  public static DeviceGroup get(String deviceGroupOwner, String deviceGroupName) {
    final JsonNode json = APICall.callAPI(GET_DEVICE_GROUP + "/" + deviceGroupOwner + "/" + deviceGroupName);
    if (json == null || json.has("error")) {
      return null;
    }
    return new Gson().fromJson(json.toString(), DeviceGroup.class);
  }
  /*
  public static DeviceGroup get(String deviceGroupId) {
    final JsonNode json = APICall.callAPI(GET_DEVICE_GROUP_BY_ID + "/" + deviceGroupId);
    if (json == null || json.has("error")) {
      return null;
    }
    return new Gson().fromJson(json.toString(), DeviceGroup.class);
  }*/

  public static List<DeviceGroup> getAll(String deviceGroupOwner) {
    final JsonNode json = APICall.callAPI(GET_ALL_DEVICE_GROUPS + "/" + deviceGroupOwner);
    if (json == null || json.has("error") || !json.isArray()) {
      return new ArrayList<DeviceGroup>();
    }

    DeviceGroup[] deviceGroupArray = new Gson().fromJson(json.toString(), DeviceGroup[].class);
    return Arrays.asList(deviceGroupArray);
  }

  public static List<Device> getDevices(String deviceGroupOwner, String deviceGroupName) {
    final JsonNode json = APICall.callAPI(GET_DEVICE_GROUP_DEVICES + "/" + deviceGroupOwner + "/" + deviceGroupName);
    if (json == null || json.has("error") || !json.isArray()) {
      return new ArrayList<Device>(); 
    }

    Device[] deviceArray = new Gson().fromJson(json.toString(), Device[].class);
    return Arrays.asList(deviceArray);
  }

  public static List<String> getAllDeviceUris(String deviceGroupOwner) {
    List<Device> personalDevices = DeviceGroup.getDevices(deviceGroupOwner, "Personal");
    List<Device> sharedDevices = DeviceGroup.getDevices(deviceGroupOwner, "Shared");
    List<String> names = new ArrayList<String>();
    for (Device d : personalDevices) {
      names.add(d.getDeviceUri());
    }
    for (Device d : sharedDevices) {
      names.add(d.getDeviceUri());
    }
    java.util.Collections.sort(names);
    return names;
  }

  public static List<String> getDeviceUris(String deviceGroupOwner, String deviceGroupName) {
    List<Device> devices = DeviceGroup.getDevices(deviceGroupOwner, deviceGroupName);
    List<String> names = new ArrayList<String>();
    for (Device d : devices) {
      names.add(d.getDeviceUri());
    }
    java.util.Collections.sort(names);
    return names;
  }
 
  public static JsonNode addDevice(String deviceGroupOwner, String deviceGroupName, String deviceUri) {
    return APICall.callAPI(ADD_DEVICE_GROUP_DEVICE + "/" + deviceGroupOwner + "/" + deviceGroupName + "/" + deviceUri);
  }

  public static JsonNode deleteDevice(String deviceGroupOwner, String deviceGroupName, String deviceUri) {
    return APICall.callAPI(DELETE_DEVICE_GROUP_DEVICE + "/" + deviceGroupOwner + "/" + deviceGroupName + "/" + deviceUri);
  }

  public static JsonNode delete(String deviceGroupOwner, String deviceGroupName) {
    return APICall.callAPI(DELETE_DEVICE_GROUP + "/" + deviceGroupOwner + "/" + deviceGroupName);
  }

  public static JsonNode create(JsonNode jsonData) {
    return APICall.postAPI(CREATE_DEVICE_GROUP, jsonData);
  }
}
