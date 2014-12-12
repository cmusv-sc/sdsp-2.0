<b>Released under a Dual Licensing / GPL 3.</b>
Sensor Service Platform APIs Version 2.0 
========================================
Advisors: Jia Zhang, Bob Iannucci, Martin Griss, Steven Rosenberg, Anthony Rowe<br/>
Current contributors: Chris Lee, Hao Li<br/>
Past Contributors: Yuan Ren, Mark Hennessy, Kaushik Gopal, Sean Xiao, Sumeet Kumar, David Pfeffer, Basmah Aljedia, Bo Liu, Ching Lun Lin, David Lee, Jeff Hsu, Lyman Cao

EXECUTIVE SUMMARY
=================
On top of CMU SensorAndrew, the largest nation-wide campus sensor network, our Sensor Data and Service Platform (SDSP) 
aims to build a software service layer serving sensor data service discovery, reuse, composition, mashup, provisioning, and 
analysis. Another major objective of our project is to support SDSP-empowered innovative application design and 
development. Supported by a cloud computing enrironment with high-performance database, SDSP provides a platform 
to enable and facilitate a variety of research projects at CMUSV in the areas of mobile services, internet of things, 
cloud computing, big data analytics, software as a service, and social services.

Service URL:
------------
[http://einstein.sv.cmu.edu:9012][1]


Overview:
---------
Currently we are providing APIs in 6 categores:

**Category 1: Device Groups**<br/>
   - [Get properties of a device group](#1)
   - [Get properties of all device groups of a user](#2)
   - [Get properties of all devices within a device group](#3)
   - [Add a device to a device group](#4)
   - [Delete a device from a device group](#5)
   - [Delete device group](#6)
   - [Create device group](#7)

**Category 2: User Groups**<br/>
   - [Get properties of user groups from which the user is the owner](#21)
   - [Get properties of user groups from which the user is a member](#22)
   - [Get properties of a user group](#23)
   - [Get properties of all devices within a user group](#24)
   - [Add a member to a user group](#25)
   - [Delete a member from a user group](#26)
   - [Add a device to a user group](#27)
   - [Delete a device from a user group](#28)
   - [Delete a user group](#29)
   - [Create a user group](#30)

**Category 3: Sensor Types**<br/>
   - [Get properties of all sensor types of a user](#41)
   - [Delete a sensor type](#42)
   - [Create a sensor type](#43)

**Category 4: Device Types**<br/>
   - [Get properties all sensor types of a user](#61)
   - [Delete a sensor type](#62)
   - [Create a sensor type](#63)

**Category 5: Sensors**<br/>
   - [Get properties of a sensor](#81)
   - [Get properties of all sensors within a device](#82)

**Category 6: Devices**<br/>
   - [Get properties of a device](#101)
   - [Delete a device](#102)
   - [Create a device](#103)
    

Detailed Usages:
----------------
Note: all TimeStamps are in Unix epoch time format to millisecond. Conversion from readable timestamp format to Unix epoch timestamp can be found in http://www.epochconverter.com
Note: the support more multiple users warrants a means of authentication for API calls. The authentication is currently part of the URL, with requestUser being the username of the requesting user.

**DEVICE GROUPS**<br/>
Note: Only device group does not have requestUser authentication, as the API assumes DeviceGroups to not be shared entities.

<a name="1"></a>**Get properties of a device group**
  - **Purpose**: Query a device group.
  - **Method**: GET
  - **URL**: http://einstein.sv.cmu.edu:9012/deviceGroup/get/<"deviceGroupOwner">/<"deviceGroupName">
  - **Semantics**: 
      - **deviceGroupOwner**: The user who created the device group.
      - **deviceGroupName**: The name of the device group.

<a name="2"></a>**Get properties of all device groups of a user.**
  - **Purpose**: Query all device groups of a user.
  - **Method**: GET
  - **URL**: http://einstein.sv.cmu.edu:9012/deviceGroup/getAll/<"deviceGroupOwner">
  - **Semantics**: 
      - **deviceGroupOwner**: The user who created the device groups.

<a name="3"></a>**Get properties of all devices within a device group**
  - **Purpose**: Query all devices in a device group.
  - **Method**: GET
  - **URL**: http://einstein.sv.cmu.edu:9012/deviceGroup/getDevices/<"deviceGroupOwner">/<"deviceGroupName">
  - **Semantics**: 
      - **deviceGroupOwner**: The user who created the device group.
      - **deviceGroupName**: The name of the device group.

<a name="4"></a>**Add a device to a device group**
  - **Purpose**: Add an existing device to the device group. Does not work on the default Personal and Shared device groups of users.
  - **Method**: GET
  - **URL**: http://einstein.sv.cmu.edu:9012/deviceGroup/addDevice/<"deviceGroupOwner">/<"deviceGroupName">/<"deviceUri">
  - **Semantics**: 
      - **deviceGroupOwner**: The user who created the device group.
      - **deviceGroupName**: The name of the device group.
      - **deviceUri**: The URI of the device to be added.

<a name="5"></a>**Delete a device from a device group**
  - **Purpose**: Delete an existing device from the device group. Does not work on the default Shared device group of users.
  - **Method**: GET
  - **URL**: http://einstein.sv.cmu.edu:9012/deviceGroup/deleteDevice/<"deviceGroupOwner">/<"deviceGroupName">/<"deviceUri">
  - **Semantics**: 
      - **deviceGroupOwner**: The user who created the device group.
      - **deviceGroupName**: The name of the device group.
      - **deviceUri**: The URI of the device to be deleted.

<a name="6"></a>**Delete a device group**
  - **Purpose**: Delete a device group. 
  - **Method**: GET
  - **URL**: http://einstein.sv.cmu.edu:9012/deviceGroup/delete/<"deviceGroupOwner"/<"deviceGroupName">
  - **Semantics**: 
      - **deviceGroupOwner**: The user who created the device group.
      - **deviceGroupName**: The name of the device group.

<a name="7"></a>**Create a device group**
  - **Purpose**: Create a device group.
  - **Method**: POST
  - **URL**: http://einstein.sv.cmu.edu:9012/createDeviceGroup
  - **Semantics**: none
  - **Properties**:
      - **deviceGroupOwner** (string, **not null**): User creating the device group.
      - **deviceGroupName** (int, **not null**): Name of the device group. 
    

**USER GROUPS**<br/>

<a name="21"></a>**Get properties of user groups from which the user is the owner**
  - **Purpose**: Query user groups owned by a user. 
  - **Method**: GET
  - **URL**: http://einstein.sv.cmu.edu:9012/userGroup/getOwnedGroups/<"requestUser">/<"userGroupOwner">
  - **Semantics**: 
      - **requestUser**: Username of requesting user.
      - **userGroupOwner**: The user who created the user group.

<a name="22"></a>**Get properties of user groups from which the user is a member**
  - **Purpose**: Query user groups the user is a member of (not owner). 
  - **Method**: GET
  - **URL**: http://einstein.sv.cmu.edu:9012/userGroup/getMemberGroups/<"requestUser">/<"userGroupOwner">
  - **Semantics**: 
      - **requestUser**: Username of requesting user.
      - **userGroupOwner**: The user who created the user group.

<a name="23"></a>**Get properties of a user group**
  - **Purpose**: Query a user group. Only the owner and members have access. 
  - **Method**: GET
  - **URL**: http://einstein.sv.cmu.edu:9012/userGroup/get/<"requestUser">/<"userGroupOwner">/<"userGroupName">
  - **Semantics**: 
      - **requestUser**: Username of requesting user.
      - **userGroupOwner**: The user who created the user group.
      - **userGroupName**: The name of the user group.

<a name="24"></a>**Get properties of all devices within a user group**
  - **Purpose**: Query devices in a user group. Only the owner and members have access.
  - **Method**: GET
  - **URL**: http://einstein.sv.cmu.edu:9012/userGroup/getDevices/<"requestUser">/<"userGroupOwner">/<"userGroupName">
  - **Semantics**: 
      - **requestUser**: Username of requesting user.
      - **userGroupOwner**: The user who created the user group.
      - **userGroupName**: The name of the user group.

<a name="25"></a>**Add a member to a user group**
  - **Purpose**: Add an existing member to the user group. Members have invite if the memberInvite property is set. 
  - **Method**: GET
  - **URL**: http://einstein.sv.cmu.edu:9012/userGroup/addMember/<"requestUser">/<"userGroupOwner">/<"userGroupName">/<"memberUser">
  - **Semantics**: 
      - **requestUser**: Username of requesting user.
      - **userGroupOwner**: The user who created the user group.
      - **userGroupName**: The name of the user group.
      - **memberUser**: Username of member to be added.

<a name="26"></a>**Delete a member from a user group**
  - **Purpose**: Delete an existing member from the user group. Only the owner can delete members, and the devices shared by the deleted member are removed as well. Members may delete themselves from the group.
  - **Method**: GET
  - **URL**: http://einstein.sv.cmu.edu:9012/userGroup/addMember/<"requestUser">/<"userGroupOwner">/<"userGroupName">/<"memberUser">
  - **Semantics**: 
      - **requestUser**: Username of requesting user.
      - **userGroupOwner**: The user who created the user group.
      - **userGroupName**: The name of the user group.
      - **memberUser**: Username of member to be deleted.

<a name="27"></a>**Add a device to a user group**
  - **Purpose**: Add an existing device to the user group. Users may only add devices they personally own. 
  - **Method**: GET
  - **URL**: http://einstein.sv.cmu.edu:9012/userGroup/addDevice/<"requestUser">/<"userGroupOwner">/<"userGroupName">/<"deviceUri">
  - **Semantics**: 
      - **requestUser**: Username of requesting user.
      - **userGroupOwner**: The user who created the user group.
      - **userGroupName**: The name of the user group.
      - **memberUser**: Universal resource identifier of the device to be added.

<a name="28"></a>**Delete a device from a user group**
  - **Purpose**: Delete an existing device from the user group. Users may only add devices they personally own. 
  - **Method**: GET
  - **URL**: http://einstein.sv.cmu.edu:9012/userGroup/deleteDevice/<"requestUser">/<"userGroupOwner">/<"userGroupName">/<"deviceUri">
  - **Semantics**: 
      - **requestUser**: Username of requesting user.
      - **userGroupOwner**: The user who created the user group.
      - **userGroupName**: The name of the user group.
      - **memberUser**: Universal resource identifier of the device to be deleted.

<a name="29"></a>**Delete a user group**
  - **Purpose**: Delete an existing user group. Only the owner of the group can use this function. 
  - **Method**: GET
  - **URL**: http://einstein.sv.cmu.edu:9012/userGroup/delete/<"requestUser">/<"userGroupOwner">/<"userGroupName">
  - **Semantics**: 
      - **requestUser**: Username of requesting user.
      - **userGroupOwner**: The user who created the user group.
      - **userGroupName**: The name of the user group.

<a name="30"></a>**Create a user group**
  - **Purpose**: Create a user group.
  - **Method**: POST
  - **URL**: http://einstein.sv.cmu.edu:9012/createUserGroup/<"requestUser">
  - **Semantics**: 
      - **requestUser**: Username of requesting user.
  - **Properties**:
      - **userGroupOwner** (string, **not null**): User creating the user group.
      - **userGroupName** (int, **not null**): Name of the user group. 
      - **memberEdit** (boolean, **not null**): Property describing whether members may add devices to the user group. 
      - **memberInvite** (boolean, **not null**): Property describing whether members may invite other members to the user group.
 

**SENSOR TYPES**<br/>

<a name="41"></a>**Get properties of all sensor types of a user**
  - **Purpose**: Query all sensor types owned by a user.
  - **Method**: GET
  - **URL**: http://einstein.sv.cmu.edu:9012/sensorType/getAll/<"requestUser">
  - **Semantics**: 
      - **requestUser**: Username of requesting user.

<a name="42"></a>**Delete a sensor type**
  - **Purpose**: Delete an existing sensor type.
  - **Method**: GET
  - **URL**: http://einstein.sv.cmu.edu:9012/sensorType/delete/<"requestUser">/<"sensorTypeUri">
  - **Semantics**: 
      - **requestUser**: Username of requesting user.
      - **sensorTypeUri**: Universal resource identifier of sensor type to be deleted. 

<a name="43"></a>**Create a sensor type**
  - **Purpose**: Create a sensor type.
  - **Method**: POST
  - **URL**: http://einstein.sv.cmu.edu:9012/createSensorType/<"requestUser">
  - **Semantics**: 
      - **requestUser**: Username of requesting user.
  - **Properties**:
      - **sensorTypeUri** (String, **not null**): The universal resource identifier for the sensor type.
      - **owner** (String, **not null**): The username for the creator of the sensor type.
      - **sensorCategory** (String, **not null**): The sensor category describing the sensor type.
      - **manufacturer** (String, **not null**): The sensor type manufacturer.
      - **version** (String, **not null**): The sensor type version.
      - **maximumValue** (double, **not null**): The maximum value of sensor readings for the sensor type.
      - **minimumValue** (double, **not null**): The minimum value of sensor readings for the sensor type.
      - **unit** (String, **not null**): The unit sensor readings for the sensor type.
      - **interpreter** (String, **not null**): The interpretation of sensor readings for the sensor type.


**DEVICE TYPES**<br/>

<a name="61"></a>**Get properties of all device types of a user**
  - **Purpose**: Query all device types owned by a user.
  - **Method**: GET
  - **URL**: http://einstein.sv.cmu.edu:9012/deviceType/getAll/<"requestUser">
  - **Semantics**: 
      - **requestUser**: Username of requesting user.

<a name="62"></a>**Delete a device type**
  - **Purpose**: Delete an existing device type.
  - **Method**: GET
  - **URL**: http://einstein.sv.cmu.edu:9012/deviceType/delete/<"requestUser">/<"deviceTypeUri">
  - **Semantics**: 
      - **requestUser**: Username of requesting user.
      - **deviceTypeUri**: Universal resource identifier of device type to be deleted. 

<a name="63"></a>**Create a device type**
  - **Purpose**: Create a device type.
  - **Method**: POST
  - **URL**: http://einstein.sv.cmu.edu:9012/createDeviceType/<"requestUser">
  - **Semantics**: 
      - **requestUser**: Username of requesting user.
  - **Properties**:
      - **deviceTypeUri** (String, **not null**): The universal resource identifier for the device type.
      - **owner** (String, **not null**): The username for the creator of the device type.
      - **sensorTypeUris** (String[], **not null**): The universal resource identifiers of sensor types that define the device type.
      - **manufacturer** (String, **not null**): The device type manufacturer.
      - **version** (String, **not null**): The device type version.

**SENSORS**<br/>

<a name="81"></a>**Get properties of a sensor**
  - **Purpose**: Query a sensor. Only users with access to the parent device through personal ownership or shared user groups may access the sensor.
  - **Method**: GET
  - **URL**: http://einstein.sv.cmu.edu:9012/sensor/get/<"requestUser">/<"sensorUri">
  - **Semantics**: 
      - **requestUser**: Username of requesting user.
      - **sensorUri**: Universal resource identifier of a sensor.

<a name="82"></a>**Get properties of all sensors within a device**
  - **Purpose**: Query sensors within a device. Only users with access to the parent device through personal ownership or shared user groups may access the sensors.
  - **Method**: GET
  - **URL**: http://einstein.sv.cmu.edu:9012/sensor/gets/<"requestUser">/<"deviceUri">
  - **Semantics**: 
      - **requestUser**: Username of requesting user.
      - **deviceUri**: Universal resource identifier of a device.


**DEVICES**<br/>

<a name="101"></a>**Get properties of a device**
  - **Purpose**: Query a device. Only users with access through personal ownership or shared user groups may access the device..
  - **Method**: GET
  - **URL**: http://einstein.sv.cmu.edu:9012/device/get/<"requestUser">/<"deviceUri">
  - **Semantics**: 
      - **requestUser**: Username of requesting user.
      - **deviceUri**: Universal resource identifier of a device.

<a name="102"></a>**Delete a device**
  - **Purpose**: Delete an existing device.
  - **Method**: GET
  - **URL**: http://einstein.sv.cmu.edu:9012/device/delete/<"requestUser">/<"deviceUri">
  - **Semantics**: 
      - **requestUser**: Username of requesting user.
      - **deviceUri**: Universal resource identifier of device to be deleted. 

<a name="103"></a>**Create a device**
  - **Purpose**: Create a device. The POST request also includes sensor objects to be included in the device.
  - **Method**: POST
  - **URL**: http://einstein.sv.cmu.edu:9012/createDevice/<"requestUser">
  - **Semantics**: 
      - **requestUser**: Username of requesting user.
  - **Properties**:
      - **deviceUri** (String, **not null**): The universal resource identifier for the device.
      - **deviceTypeUri** (String, **not null**): The universal resource identifier for the device type that characterizes the device.
      - **owner** (String, **not null**): The username for the creator of the device.
      - **deviceUserDefinedFields** (String, **not null**): User defined fields.
      - **sensorCount** (int, **not null**): The number of sensor nodes in the request.
      - **sensor[x]** (Object, x in range(0, sensorCount)): A sensor object that defines sensors to be included in the device. The ordering of the sensors must be the same as defined by the device type.
          - **sensorUri** (String, **not null**): The universal resource identifier for the sensor.
          - **sensorTypeUri** (String, **not null**): The universal resource identifier for the sensor type that characterizes the sensor.
          - **deviceUri** (String, **not null**): The universal resource identifier of the parent device that contains this sensor.
          - **owner** (String, **not null**): The username for the creator of the sensor
          - **sensorUserDefinedFields** (String, **not null**): User defined fields.
