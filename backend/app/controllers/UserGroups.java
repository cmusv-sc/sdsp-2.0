package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

import models.UserGroup;
import models.Device;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

import java.util.*;

public class UserGroups extends Controller {

  public static Result getOwnedGroups(String requestUser, String userGroupOwner) {
    if (requestUser == null || userGroupOwner == null) {
      return badRequest();
    }

    response().setHeader("Access-Control-Allow-Origin", "*");
    List<UserGroup> ugList = UserGroup.getOwnedGroups(requestUser, userGroupOwner);

    String ret = new Gson().toJson(ugList);
    return ok(ret);
  }

  public static Result getMemberGroups(String requestUser, String userGroupMember) {
    if (requestUser == null || userGroupMember == null) {
      return badRequest();
    }

    response().setHeader("Access-Control-Allow-Origin", "*");
    List<UserGroup> ugList = UserGroup.getMemberGroups(requestUser, userGroupMember);

    String ret = new Gson().toJson(ugList);
    return ok(ret);
  }

  public static Result get(String requestUser, String userGroupOwner, String userGroupName) {
    if (requestUser == null || userGroupOwner == null || userGroupName == null) {
      return badRequest();
    }

    response().setHeader("Access-Control-Allow-Origin", "*");
    UserGroup ug = UserGroup.get(requestUser, userGroupOwner, userGroupName);
    if (ug == null) {
      return notFound();
    }

    String ret = new Gson().toJson(ug);
    return ok(ret);
  }

  public static Result getDevices(String requestUser, String userGroupOwner, String userGroupName) {
    if (requestUser == null || userGroupOwner == null || userGroupName == null) {
      return badRequest();
    }

    response().setHeader("Access-Control-Allow-Origin", "*");
    List<Device> devices = UserGroup.getDevices(requestUser, userGroupOwner, userGroupName);

    String ret = new Gson().toJson(devices);
    return ok(ret);
  }

  public static Result addMember(String requestUser, String userGroupOwner, String userGroupName, String memberUser) {
    if (requestUser == null || userGroupOwner == null || userGroupName == null || memberUser == null) {
      return badRequest();
    }

    response().setHeader("Access-Control-Allow-Origin", "*");
    UserGroup ug = UserGroup.addMember(requestUser, userGroupOwner, userGroupName, memberUser);
    if (ug == null) {
      return badRequest();
    }

    return ok();
  }

  public static Result deleteMember(String requestUser, String userGroupOwner, String userGroupName, String memberUser) {
    if (requestUser == null || userGroupOwner == null || userGroupName == null || memberUser == null) {
      return badRequest();
    }

    response().setHeader("Access-Control-Allow-Origin", "*");
    UserGroup ug = UserGroup.deleteMember(requestUser, userGroupOwner, userGroupName, memberUser);
    if (ug == null) {
      return badRequest();
    }

    return ok();
  }

  public static Result addDevice(String requestUser, String userGroupOwner, String userGroupName, String deviceUri) {
    if (requestUser == null || userGroupOwner == null || userGroupName == null || deviceUri == null) {
      return badRequest();
    }

    response().setHeader("Access-Control-Allow-Origin", "*");
    UserGroup ug = UserGroup.addDevice(requestUser, userGroupOwner, userGroupName, deviceUri);
    if (ug == null) {
      return badRequest();
    }

    return ok();
  }

  public static Result deleteDevice(String requestUser, String userGroupOwner, String userGroupName, String deviceUri) {
    if (requestUser == null || userGroupOwner == null || userGroupName == null || deviceUri == null) {
      return badRequest();
    }

    response().setHeader("Access-Control-Allow-Origin", "*");
    UserGroup ug = UserGroup.deleteDevice(requestUser, userGroupOwner, userGroupName, deviceUri);
    if (ug == null) {
      return badRequest();
    }

    return ok();
  }

  public static Result create(String requestUser) {
    JsonNode jsonRequest = request().body().asJson();
    if (jsonRequest == null) {
      return badRequest();
    }

    if (!jsonRequest.has("userGroupOwner") || !jsonRequest.has("userGroupName") || !jsonRequest.has("memberEdit") || !jsonRequest.has("memberInvite")) {
      return badRequest();
    }
    String userGroupOwner = jsonRequest.findPath("userGroupOwner").asText();
    String userGroupName = jsonRequest.findPath("userGroupName").asText();
    boolean memberEdit = jsonRequest.findPath("memberEdit").asBoolean();
    boolean memberInvite = jsonRequest.findPath("memberInvite").asBoolean();

    UserGroup ugRequest = new UserGroup(userGroupOwner, userGroupName, memberEdit, memberInvite);
    UserGroup ug = UserGroup.create(requestUser, ugRequest);

    if (ug == null) {
      return badRequest();
    }
    
    return ok();
  }

  public static Result delete(String requestUser, String userGroupOwner, String userGroupName) {
    if (requestUser == null || userGroupOwner == null || userGroupName == null) {
      return badRequest();
    }

    response().setHeader("Access-Control-Allow-Origin", "*");
    UserGroup ug = UserGroup.delete(requestUser, userGroupOwner, userGroupName);
    if (ug == null) {
      return badRequest();
    }
    
    return ok();
  }
}
