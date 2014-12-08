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

// TODO: index on userGroupOwner
@Document(collection = "user_groups")
public class UserGroup {

  @Id
  private String userGroupId;
  private String userGroupOwner; // username
  private String userGroupName;
  private boolean memberEdit; // applied to members
  private boolean memberInvite;
  private String[] memberUsers;
  private String[] deviceUris;

  public UserGroup(String userGroupOwner, String userGroupName, boolean memberEdit, boolean memberInvite) {
    this.userGroupOwner = userGroupOwner;
    this.userGroupName = userGroupName;
    this.memberEdit = memberEdit;
    this.memberInvite = memberInvite;
    this.memberUsers = new String[0];
    this.deviceUris = new String[0]; 
  }

  private static ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
  private static MongoOperations mongoOps = (MongoOperations) ctx.getBean("mongoTemplate");

  public static Query idQuery(String userGroupId) {
    return new Query(Criteria.where("userGroupId").is(userGroupId));
  }

  public static UserGroup userGroupById(String userGroupId) {
    Query q = UserGroup.idQuery(userGroupId);
    UserGroup ug = mongoOps.findOne(q, UserGroup.class);
    return ug;
  }

  public static List<UserGroup> getUserGroups(String userGroupOwner, boolean owned) {
    List<UserGroup> ugList = new ArrayList<UserGroup>();
    User user = User.getUser(userGroupOwner);

    if (user == null) {
      return ugList;
    }

    String[] idArr;
    if (owned) {
      idArr = user.getOwnedUserGroupIds();
    }
    else {
      idArr = user.getMemberUserGroupIds();
    }
    for (String groupId : idArr) {
      UserGroup ug = UserGroup.userGroupById(groupId);
      if (ug != null) {
        ugList.add(ug);
      }
    }
    return ugList;
  }   

  public static List<UserGroup> getOwnedUserGroups(String userGroupOwner) {
    return UserGroup.getUserGroups(userGroupOwner, true);
  }

  public static List<UserGroup> getMemberUserGroups(String userGroupOwner) {
    return UserGroup.getUserGroups(userGroupOwner, false);
  }

  public static UserGroup getUserGroup(String userGroupOwner, String userGroupName, boolean owned) {
    List<UserGroup> ugList = UserGroup.getUserGroups(userGroupOwner, owned);
    for (UserGroup ug : ugList) {
      if (ug.getUserGroupName().equals(userGroupName)) {
        return ug;
      }
    }
    return null;
  }

  public static UserGroup getOwnedUserGroup(String userGroupOwner, String userGroupName) {
    return UserGroup.getUserGroup(userGroupOwner, userGroupName, true);
  }

  public static UserGroup getMemberUserGroup(String userGroupOwner, String userGroupName) {
    return UserGroup.getUserGroup(userGroupOwner, userGroupName, false);
  }

  public static boolean withinGroup(String username, UserGroup ug) {
    String userGroupOwner = ug.getUserGroupOwner();
    if (!username.equals(userGroupOwner)) {
      List<String> memberList = Arrays.asList(ug.getMemberUsers());
      if (!memberList.contains(username)) {
        return false;
      }
    }
    return true;
  }

  // CRUD operations

  public static UserGroup create(String requestUser, UserGroup newUG) {
    String userGroupOwner = newUG.getUserGroupOwner();
    String userGroupName = newUG.getUserGroupName();
    
    UserGroup ug = UserGroup.getOwnedUserGroup(userGroupOwner, userGroupName);

    if (ug != null || !requestUser.equals(userGroupOwner)) {
      return null;
    }

    mongoOps.save(newUG);
    Query q = new Query(Criteria.where("userGroupOwner").is(userGroupOwner).and("userGroupName").is(userGroupName));
    ug = mongoOps.findOne(q, UserGroup.class);
    User.addOwnedUserGroup(userGroupOwner, ug.getUserGroupId());
    return ug;
  }

  public static List<UserGroup> getOwnedGroups(String requestUser, String userGroupOwner) {
    if (!requestUser.equals(userGroupOwner)) {
      return new ArrayList<UserGroup>();
    }
    return UserGroup.getOwnedUserGroups(userGroupOwner);
  }

  public static List<UserGroup> getMemberGroups(String requestUser, String userGroupMember) {
    if (!requestUser.equals(userGroupMember)) {
      return new ArrayList<UserGroup>();
    }
    return UserGroup.getMemberUserGroups(userGroupMember);
  }

  // Access the UserGroup, only members can view the UserGroup
  public static UserGroup get(String requestUser, String userGroupOwner, String userGroupName) {
    UserGroup ug = UserGroup.getOwnedUserGroup(userGroupOwner, userGroupName);
    if (ug == null || !UserGroup.withinGroup(requestUser, ug)) {
      return null;
    }
    return ug;
  }

  // Also clears out null refernces in array, that may occur when devices are deleted by users
  public static List<Device> getDevices(String requestUser, String userGroupOwner, String userGroupName) {
    List<Device> devices = new ArrayList<Device>();

    UserGroup ug = UserGroup.getOwnedUserGroup(userGroupOwner, userGroupName);
    if (ug == null || !UserGroup.withinGroup(requestUser, ug)) {
      return devices;
    }
    
    List<String> deviceUriList = new ArrayList<String>(Arrays.asList(ug.getDeviceUris()));
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
    ug.setDeviceUris(deviceUriList.toArray(new String[deviceUriList.size()]));
    mongoOps.save(ug);

    return devices;
  }

  // Members can add only if memberInvite is true 
  public static UserGroup addMember(String requestUser, String userGroupOwner, String userGroupName, String memberUser) {
    UserGroup ug = UserGroup.getOwnedUserGroup(userGroupOwner, userGroupName);
    User requester = User.getUser(requestUser);
    User member = User.getUser(memberUser);

    if (ug == null || requester == null || member == null || memberUser.equals(userGroupOwner)) {
      return null;
    }

    List<String> memberList = new ArrayList<String>(Arrays.asList(ug.getMemberUsers()));
    if (!requestUser.equals(userGroupOwner) && !(ug.getMemberInvite() && memberList.contains(requestUser))) {
      return null;
    }
    if (memberList.contains(memberUser)) {
      return null;
    }

    memberList.add(memberUser);
    ug.setMemberUsers(memberList.toArray(new String[memberList.size()]));
    mongoOps.save(ug);
    User.addMemberUserGroup(memberUser, ug.getUserGroupId());
    return ug;
  }

  // Owner can kick out any member, and member can choose to leave
  public static UserGroup deleteMember(String requestUser, String userGroupOwner, String userGroupName, String memberUser) {
    UserGroup ug = UserGroup.getOwnedUserGroup(userGroupOwner, userGroupName);
    User requester = User.getUser(requestUser);
    User member = User.getUser(memberUser);

    if (ug == null || requester == null || member == null) {
      return null;
    }

    List<String> memberList = new ArrayList<String>(Arrays.asList(ug.getMemberUsers()));
    if (!requestUser.equals(userGroupOwner) && !requestUser.equals(memberUser)) {
      return null;
    }
    if (!memberList.contains(memberUser)) {
      return null;
    }

    // We need to delete all devices owned(shared) by leaving member
    List<String> deviceUriList = new ArrayList<String>(Arrays.asList(ug.getDeviceUris()));
    List<String> toRemove = new ArrayList<String>();
    for (String deviceUri : deviceUriList) {
      Device device = Device.getDevice(deviceUri);
      if (device == null || device.getOwner().equals(memberUser)) {
        toRemove.add(deviceUri);
      }
    }
    for (String deviceUri : toRemove) {
      deviceUriList.remove(deviceUri);
    }
    ug.setDeviceUris(deviceUriList.toArray(new String[deviceUriList.size()]));

    memberList.remove(memberUser);
    ug.setMemberUsers(memberList.toArray(new String[memberList.size()]));

    mongoOps.save(ug);
    User.deleteMemberUserGroup(memberUser, ug.getUserGroupId());
    return ug;
  }

  // Users can only add devices he or she owns
  public static UserGroup addDevice(String requestUser, String userGroupOwner, String userGroupName, String deviceUri) {
    UserGroup ug = UserGroup.getOwnedUserGroup(userGroupOwner, userGroupName);
    User requester = User.getUser(requestUser);
    Device device = Device.getDevice(deviceUri);

    if (ug == null || requester == null || device == null) {
      return null;
    }

    List<String> memberList = Arrays.asList(ug.getMemberUsers());
    if (!requestUser.equals(userGroupOwner) && !(ug.getMemberEdit() && memberList.contains(requestUser) && device.getOwner().equals(requestUser))) {
      return null;
    }

    List<String> deviceUriList = new ArrayList<String>(Arrays.asList(ug.getDeviceUris()));
    if (deviceUriList.contains(deviceUri)) {
      return null;
    }

    deviceUriList.add(deviceUri);
    ug.setDeviceUris(deviceUriList.toArray(new String[deviceUriList.size()]));
    mongoOps.save(ug);
    return ug;
  }
  
  // Owner can delete any device; member can only delete devices he or she owns
  public static UserGroup deleteDevice(String requestUser, String userGroupOwner, String userGroupName, String deviceUri) {
    UserGroup ug = UserGroup.getOwnedUserGroup(userGroupOwner, userGroupName);
    User requester = User.getUser(requestUser);
    Device device = Device.getDevice(deviceUri);

    if (ug == null || requester == null || device == null) {
      return null;
    }

    List<String> memberList = Arrays.asList(ug.getMemberUsers());
    if (!requestUser.equals(userGroupOwner) && !(ug.getMemberEdit() && memberList.contains(requestUser) && device.getOwner().equals(requestUser))) {
      return null;
    }

    List<String> deviceUriList = new ArrayList<String>(Arrays.asList(ug.getDeviceUris()));
    if (!deviceUriList.contains(deviceUri)) {
      return null;
    }

    deviceUriList.remove(deviceUri);
    ug.setDeviceUris(deviceUriList.toArray(new String[deviceUriList.size()]));
    mongoOps.save(ug);
    return ug;
  }

  public static UserGroup delete(String requestUser, String userGroupOwner, String userGroupName) {
    UserGroup ug = UserGroup.getOwnedUserGroup(userGroupOwner, userGroupName);
    if (ug == null || !requestUser.equals(userGroupOwner)) {
      return null;
    }

    String userGroupId = ug.getUserGroupId();
    User.deleteOwnedUserGroup(userGroupOwner, userGroupId);
    for (String userMember : ug.getMemberUsers()) {
      User.deleteMemberUserGroup(userMember, userGroupId);
    }
    mongoOps.remove(UserGroup.idQuery(userGroupId), UserGroup.class);

    return ug;
  }

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

  public void setMemberUsers(String[] memberUsers) {
    this.memberUsers = memberUsers;
  }

  public String[] getDeviceUris() {
    return this.deviceUris;
  }

  public void setDeviceUris(String[] deviceUris) {
    this.deviceUris = deviceUris;
  }
}
