package controllers;

import play.*;
import play.mvc.*;
import play.data.*;
import play.data.Form;
import play.data.DynamicForm;
import play.libs.Json;

import views.html.*;

import models.*;

import util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.*;

public class UserGroups extends Controller {
 
  final static Form<UserGroup> ugForm = Form.form(UserGroup.class);

  public static Result display() {
    String username = session("username");
    if (username == null) {
      return unauthorized();
    }
    List<UserGroup> allGroups = new ArrayList<UserGroup>();
    allGroups.addAll(UserGroup.getOwnedGroups(username, username));
    allGroups.addAll(UserGroup.getMemberGroups(username, username));
    return ok(userGroups.render(allGroups, ugForm)); 
  }

  public static Result displayUsers(String userGroupOwner, String userGroupName) {
    String username = session("username");
    if (username == null) {
      return unauthorized();
    }
    UserGroup ug = UserGroup.get(username, userGroupOwner, userGroupName);
    if (ug == null) {
      return unauthorized();
    }
    return ok(userGroupUsers.render(ug, ugForm));
  }

  public static Result displayDevices(String userGroupOwner, String userGroupName) {
    String username = session("username");
    if (username == null) {
      return unauthorized();
    }
    UserGroup ug = UserGroup.get(username, userGroupOwner, userGroupName);
    if (ug == null) {
      return unauthorized();
    }
    return ok(userGroupDevices.render(ug, UserGroup.getDevices(username, userGroupOwner, userGroupName), ugForm));
  }

  public static Result create() {
    Form<UserGroup> ug = ugForm.bindFromRequest();

    ObjectNode json = Json.newObject();
    try {
      String userGroupName = ug.field("userGroupName").value();
      String memberEdit = ug.field("memberEdit").value();
      String memberInvite = ug.field("memberInvite").value();
      String username = session("username");


      if (userGroupName != null && !userGroupName.isEmpty()
          && memberEdit != null && !memberEdit.isEmpty()
          && memberInvite != null && !memberInvite.isEmpty()) {
        json.put("userGroupOwner", username);
        json.put("userGroupName", userGroupName);
        boolean me = memberEdit.equals("Yes") ? true : false;
        boolean mi = memberInvite.equals("Yes") ? true : false;
        json.put("memberEdit", me);
        json.put("memberInvite", mi);

        JsonNode response = UserGroup.create(username, json);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    return redirect("/userGroups");
  }

  // Only used in controllers.UserGroups.display()
  public static Result delete() {
    DynamicForm df = DynamicForm.form().bindFromRequest();
    String userGroupOwner = session("username");
    String userGroupName = df.field("nameHolder").value();

    UserGroup.delete(userGroupOwner, userGroupOwner, userGroupName);

    return redirect("/userGroups");
  }

  public static Result addMember() {
    DynamicForm df = DynamicForm.form().bindFromRequest();
    String memberUser = df.field("username").value();
    String userGroupOwner = df.field("userGroupOwner").value();
    String userGroupName = df.field("userGroupName").value();
    String requestUser = session("username");

    UserGroup.addMember(requestUser, userGroupOwner, userGroupName, memberUser); 
    return redirect("/userGroup/users/" + userGroupOwner + "/" + userGroupName);
  }

  public static Result deleteMember() {
    DynamicForm df = DynamicForm.form().bindFromRequest();
    String memberUser = df.field("username").value();
    String userGroupOwner = df.field("userGroupOwner").value();
    String userGroupName = df.field("userGroupName").value();
    String requestUser = session("username");

    UserGroup.deleteMember(requestUser, userGroupOwner, userGroupName, memberUser); 
    if (requestUser.equals(userGroupOwner)) {
      return redirect("/userGroup/users/" + userGroupOwner + "/" + userGroupName);
    }
    else {
      return redirect("/userGroups");
    }
  }

  public static Result addDevice() {
    DynamicForm df = DynamicForm.form().bindFromRequest();
    String userGroupOwner = df.field("userGroupOwner").value();
    String userGroupName = df.field("userGroupName").value();
    String deviceUri = df.field("deviceUri").value();
    String requestUser = session("username");

    UserGroup.addDevice(requestUser, userGroupOwner, userGroupName, deviceUri); 
    return redirect("/userGroup/devices/" + userGroupOwner + "/" + userGroupName);
  }

  public static Result deleteDevice() {
    DynamicForm df = DynamicForm.form().bindFromRequest();
    String userGroupOwner = df.field("userGroupOwner").value();
    String userGroupName = df.field("userGroupName").value();
    String deviceUri = df.field("deviceUri").value();
    String requestUser = session("username");

    UserGroup.deleteDevice(requestUser, userGroupOwner, userGroupName, deviceUri); 
    return redirect("/userGroup/devices/" + userGroupOwner + "/" + userGroupName);
  }
}
