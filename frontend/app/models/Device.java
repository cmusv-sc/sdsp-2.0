package models;

import util.*;

import com.fasterxml.jackson.databind.*;
import com.google.gson.Gson;

import java.util.*;

public class Device {

  private String deviceId;
  private String deviceUri;
  private String deviceTypeUri;
  private String[] sensorUris;
  private String owner; 
  private String deviceUserDefinedFields;

  public String getDeviceId() {
    return this.deviceId;
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

  public static final String GET_DEVICE = util.Constants.HOSTNAME + util.Constants.GET_DEVICE_ROUTE;
  public static final String DELETE_DEVICE = util.Constants.HOSTNAME + util.Constants.DELETE_DEVICE_ROUTE;
  public static final String CREATE_DEVICE = util.Constants.HOSTNAME + util.Constants.CREATE_DEVICE_ROUTE;

  public static Device get(String requestUser, String deviceUri) {
    final JsonNode json = APICall.callAPI(GET_DEVICE + "/" + requestUser + "/" + deviceUri);
    if (json == null || json.has("error")) {
      return null;
    }

    return new Gson().fromJson(json.toString(), Device.class);
  }

  public static JsonNode delete(String requestUser, String deviceUri) {
    return APICall.callAPI(DELETE_DEVICE + "/" + requestUser + "/" + deviceUri);
  }

  public static JsonNode create(String requestUser, JsonNode jsonData) {
    return APICall.postAPI(CREATE_DEVICE + "/" + requestUser, jsonData);
  }
}
