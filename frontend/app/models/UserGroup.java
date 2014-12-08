package models;

import util.*;

import com.fasterxml.jackson.databind.*;
import com.google.gson.Gson;

import java.util.*;

public class UserGroup {

  private String userGroupId;
  private String userGroupOwner; // username
  private String userGroupName;
  private boolean memberEdit; // applied to members
  private boolean memberInvite;
  private String[] memberUsers;
  private String[] deviceUris;

  public String getUserGroupId() {
    return this.userGroupId;
  }

  public String getUserGroupOwner() {
    return this.userGroupOwner;
  }

  public String getUserGroupName() {
    return this.userGroupName;
  }

  public boolean getMemberEdit() {
    return this.memberEdit;
  }

  public boolean getMemberInvite() {
    return this.memberInvite;
  }

  public String[] getMemberUsers() {
    return this.memberUsers;
  }

  public String[] getDeviceUris() {
    return this.deviceUris;
  }

  public static final String GET_OWNED_USER_GROUPS = util.Constants.HOSTNAME + util.Constants.GET_OWNED_USER_GROUPS_ROUTE;
  public static final String GET_MEMBER_USER_GROUPS = util.Constants.HOSTNAME + util.Constants.GET_MEMBER_USER_GROUPS_ROUTE;
  public static final String GET_USER_GROUP = util.Constants.HOSTNAME + util.Constants.GET_USER_GROUP_ROUTE;
  public static final String GET_USER_GROUP_DEVICES = util.Constants.HOSTNAME + util.Constants.GET_USER_GROUP_DEVICES_ROUTE;
  public static final String ADD_USER_GROUP_MEMBER = util.Constants.HOSTNAME + util.Constants.ADD_USER_GROUP_MEMBER_ROUTE;
  public static final String DELETE_USER_GROUP_MEMBER = util.Constants.HOSTNAME + util.Constants.DELETE_USER_GROUP_MEMBER_ROUTE;
  public static final String ADD_USER_GROUP_DEVICE = util.Constants.HOSTNAME + util.Constants.ADD_USER_GROUP_DEVICE_ROUTE;
  public static final String DELETE_USER_GROUP_DEVICE = util.Constants.HOSTNAME + util.Constants.DELETE_USER_GROUP_DEVICE_ROUTE;
  public static final String DELETE_USER_GROUP = util.Constants.HOSTNAME + util.Constants.DELETE_USER_GROUP_ROUTE;
  public static final String CREATE_USER_GROUP = util.Constants.HOSTNAME + util.Constants.CREATE_USER_GROUP_ROUTE;

  public static List<UserGroup> getOwnedGroups(String requestUser, String userGroupOwner) {
    final JsonNode json = APICall.callAPI(GET_OWNED_USER_GROUPS + "/" + requestUser + "/" + userGroupOwner);
    if (json == null || json.has("error") || !json.isArray()) {
      return new ArrayList<UserGroup>();
    }
    UserGroup[] userGroupArray = new Gson().fromJson(json.toString(), UserGroup[].class);
    return Arrays.asList(userGroupArray);
  }

  public static List<UserGroup> getMemberGroups(String requestUser, String userGroupOwner) {
    final JsonNode json = APICall.callAPI(GET_MEMBER_USER_GROUPS + "/" + requestUser + "/" + userGroupOwner);
    if (json == null || json.has("error") || !json.isArray()) {
      return new ArrayList<UserGroup>();
    }
    UserGroup[] userGroupArray = new Gson().fromJson(json.toString(), UserGroup[].class);
    return Arrays.asList(userGroupArray);
  }

  public static UserGroup get(String requestUser, String userGroupOwner, String userGroupName) {
    final JsonNode json = APICall.callAPI(GET_USER_GROUP + "/" + requestUser + "/" + userGroupOwner + "/" + userGroupName);
    if (json == null || json.has("error")) {
      return null;
    }
    return new Gson().fromJson(json.toString(), UserGroup.class);
  }

  public static List<Device> getDevices(String requestUser, String userGroupOwner, String userGroupName) {
    final JsonNode json = APICall.callAPI(GET_USER_GROUP_DEVICES + "/" + requestUser + "/" + userGroupOwner + "/" + userGroupName);
    if (json == null || json.has("error") || !json.isArray()) {
      return new ArrayList<Device>();
    }

    Device[] deviceArray = new Gson().fromJson(json.toString(), Device[].class);
    return Arrays.asList(deviceArray);
  }

  public static JsonNode addMember(String requestUser, String userGroupOwner, String userGroupName, String memberUser) {
    return APICall.callAPI(ADD_USER_GROUP_MEMBER + "/" + requestUser + "/" + userGroupOwner + "/" + userGroupName + "/" + memberUser);
  }

  public static JsonNode deleteMember(String requestUser, String userGroupOwner, String userGroupName, String memberUser) {
    return APICall.callAPI(DELETE_USER_GROUP_MEMBER + "/" + requestUser + "/" + userGroupOwner + "/" + userGroupName + "/" + memberUser);
  }

  public static JsonNode addDevice(String requestUser, String userGroupOwner, String userGroupName, String deviceUri) {
    return APICall.callAPI(ADD_USER_GROUP_DEVICE + "/" + requestUser + "/" + userGroupOwner + "/" + userGroupName + "/" + deviceUri);
  }

  public static JsonNode deleteDevice(String requestUser, String userGroupOwner, String userGroupName, String deviceUri) {
    return APICall.callAPI(DELETE_USER_GROUP_DEVICE + "/" + requestUser + "/" + userGroupOwner + "/" + userGroupName + "/" + deviceUri);
  }

  public static JsonNode delete(String requestUser, String userGroupOwner, String userGroupName) {
    return APICall.callAPI(DELETE_USER_GROUP + "/" + requestUser + "/" + userGroupOwner + "/" + userGroupName);
  }

  public static JsonNode create(String requestUser, JsonNode jsonData) {
    return APICall.postAPI(CREATE_USER_GROUP + "/" + requestUser, jsonData);
  }
}
