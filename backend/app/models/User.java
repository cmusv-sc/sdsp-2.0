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

// TODO: index on email
@Document(collection = "users")
public class User {

  @Id
  private String userId;
  private String email;
  private String username;
  private String password;
  private String firstName;
  private String lastName;
  private String[] ownedUserGroupIds;
  private String[] memberUserGroupIds;
  private String[] deviceGroupIds;

  private static ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
  private static MongoOperations mongoOps = (MongoOperations) ctx.getBean("mongoTemplate");

  public User(String email, String username, String password, String firstName, String lastName) {
    this.email = email;
    this.username = username;
    this.password = password;
    this.firstName = firstName;
    this.lastName = lastName;
    this.ownedUserGroupIds = new String[0];
    this.memberUserGroupIds = new String[0];
    this.deviceGroupIds = new String[0];
  }

  public static Query emailQuery(String email) {
    return new Query(Criteria.where("email").is(email));
  }

  public static Query usernameQuery(String username) {
    return new Query(Criteria.where("username").is(username));
  }

  public static void initializeDeviceGroups(String username) {
    for (DeviceGroup.DefaultGroup dg : DeviceGroup.DefaultGroup.values()) {
      DeviceGroup group = DeviceGroup.create(username, new DeviceGroup(username, dg.toString()));
    } 
  }

  // @param: add - true is add, false is remove
  // @param: device - true is deviceGroupIds, false is userGroupIds
  // @param: owned - true is ownedUserGroupIds, false is memberUserGroupIds (only used when @param user is true)
  public static User modifyGroup(String username, String groupId, boolean add, boolean userGroup, boolean owned) {
    User user = User.getUser(username);

    if (user == null) {
      return null;
    }

    String[] arr;
    if (userGroup) {
      arr = owned ? user.getOwnedUserGroupIds() : user.getMemberUserGroupIds();
    }
    else {
      arr = user.getDeviceGroupIds();
    }

    List<String> list = new ArrayList<String>(Arrays.asList(arr));

    if (add) {
      if (list.contains(groupId)) {
        return null;
      }
      list.add(groupId);
    }
    else {
      if (!list.contains(groupId)) {
        return null;
      }
      list.remove(groupId);
    }

    arr = list.toArray(new String[list.size()]);

    if (userGroup) {
      if (owned) {
        user.setOwnedUserGroupIds(arr);
      }
      else{
        user.setMemberUserGroupIds(arr);
      }
    }
    else {
      user.setDeviceGroupIds(arr);
    }

    mongoOps.save(user);
    return user;
  }

  public static User addOwnedUserGroup(String username, String ugId) {
    return User.modifyGroup(username, ugId, true, true, true);
  }

  public static User deleteOwnedUserGroup(String username, String ugId) {
    return User.modifyGroup(username, ugId, false, true, true);
  }

  public static User addMemberUserGroup(String username, String ugId) {
    return User.modifyGroup(username, ugId, true, true, false);
  }

  public static User deleteMemberUserGroup(String username, String ugId) {
    return User.modifyGroup(username, ugId, false, true, false);
  }

  public static User addDeviceGroup(String username, String dgId) {
    return User.modifyGroup(username, dgId, true, false, true);
  }

  public static User deleteDeviceGroup(String username, String dgId) {
    return User.modifyGroup(username, dgId, false, false, true);
  }

  public static User getUser(String username) {
    Query getUserQuery = User.usernameQuery(username);
    User result = mongoOps.findOne(getUserQuery, User.class);
    return result;
  }

  // CRUD operations

  // Unique emails and usernames
  
  public static User get(String requestUser, String username) {
    User user = User.getUser(username);

    if (user == null || !requestUser.equals(username)) {
      return null;
    }

    return user;
  }

  public static User create(User newUser) {
    User emailResult = mongoOps.findOne(User.emailQuery(newUser.getEmail()), User.class);
    if (emailResult != null) {
      return null; // email already exists
    }

    String username = newUser.getUsername();
    User user = User.getUser(username);
    if (user != null) {
      return null; // username already exists
    }

    mongoOps.save(newUser);
    User.initializeDeviceGroups(username);
    return User.getUser(username);
  }

  /*
  public static User delete(String requestUser, String username) {
    User user = User.getUser(username);

    if (user == null || !requestUser.equals(username)) {
      return null; // user does not exist
    }

    mongoOps.remove(User.usernameQuery(username), User.class);
    return user;
  }*/

  public static User changePassword(String requestUser, String username, String oldPassword, String newPassword) {
    User user = User.getUser(username);

    if (user == null || !requestUser.equals(username) || !user.getPassword().equals(oldPassword)) {
      return null;
    }
 
    user.setPassword(newPassword);
    mongoOps.save(user);
    return user; 
  } 

  public String getEmail() {
    return this.email;
  }

  public String getUsername() {
    return this.username;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String newPassword) {
    this.password = newPassword;
  }

  public String getFirstName() {
    return this.firstName;
  }

  public String getLastName() {
    return this.lastName;
  }

  public String[] getOwnedUserGroupIds() {
    return this.ownedUserGroupIds;
  }

  public void setOwnedUserGroupIds(String[] ownedUserGroupIds) {
    this.ownedUserGroupIds = ownedUserGroupIds;
  }

  public String[] getMemberUserGroupIds() {
    return this.memberUserGroupIds;
  }

  public void setMemberUserGroupIds(String[] memberUserGroupIds) {
    this.memberUserGroupIds = memberUserGroupIds;
  }

  public String[] getDeviceGroupIds() {
    return this.deviceGroupIds;
  }

  public void setDeviceGroupIds(String[] deviceGroupIds) {
    this.deviceGroupIds = deviceGroupIds;
  }
}
