package models;

import util.*;

import com.fasterxml.jackson.databind.*;
import com.google.gson.Gson;

import java.util.*;

public class DeviceType {

  private String deviceTypeId;
  private String deviceTypeUri;
  private int instanceCount;
  private String owner;
  private String[] sensorTypeUris;
  private String manufacturer;
  private String version;

  public String getDeviceTypeId() {
    return this.deviceTypeId;
  }

  public String getDeviceTypeUri() {
    return this.deviceTypeUri;
  }

  public int getInstanceCount() {
    return this.instanceCount;
  }

  public String getOwner() {
    return this.owner;
  }

  public String[] getSensorTypeUris() {
    return this.sensorTypeUris;
  }

  public List<String> getSensorTypeUriList() {
    return Arrays.asList(this.sensorTypeUris);
  }

  public String getManufacturer() {
    return this.manufacturer;
  }

  public String getVersion() {
    return this.version;
  }

  public static final String GET_ALL_DEVICE_TYPES = util.Constants.HOSTNAME + util.Constants.GET_ALL_DEVICE_TYPES_ROUTE;
  public static final String DELETE_DEVICE_TYPE = util.Constants.HOSTNAME + util.Constants.DELETE_DEVICE_TYPE_ROUTE;
  public static final String CREATE_DEVICE_TYPE = util.Constants.HOSTNAME + util.Constants.CREATE_DEVICE_TYPE_ROUTE;

  public static DeviceType get(String requestUser, String deviceTypeUri) {
    List<DeviceType> deviceTypes = DeviceType.getAll(requestUser);
    for (DeviceType dt : deviceTypes) {
      if (dt.getDeviceTypeUri().equals(deviceTypeUri)) {
        return dt;
      }
    }
    return null;
  }

  public static List<String> getDeviceTypeUris(String requestUser) {
    List<String> deviceTypeUris = new ArrayList<String>();
    List<DeviceType> deviceTypes = DeviceType.getAll(requestUser);
    for (DeviceType dt : deviceTypes) {
      deviceTypeUris.add(dt.getDeviceTypeUri()); 
    }
    java.util.Collections.sort(deviceTypeUris);
    return deviceTypeUris; 
  }

  public static List<DeviceType> getAll(String requestUser) {
    final JsonNode json = APICall.callAPI(GET_ALL_DEVICE_TYPES + "/" + requestUser);
    if (json == null || json.has("error") || !json.isArray()) {
      return new ArrayList<DeviceType>();
    }

    DeviceType[] deviceTypeArray = new Gson().fromJson(json.toString(), DeviceType[].class);
    return Arrays.asList(deviceTypeArray);
  }

  public static JsonNode delete(String requestUser, String deviceTypeUri) {
    return APICall.callAPI(DELETE_DEVICE_TYPE + "/" + requestUser + "/" + deviceTypeUri);
  }

  public static JsonNode create(String requestUser, JsonNode jsonData) {
    return APICall.postAPI(CREATE_DEVICE_TYPE + "/" + requestUser, jsonData);
  }
}
