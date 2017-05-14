# 工作内容介绍
last update 2017/5/12

本文介绍到目前为止服务器方面的代码的主要工作，分5个小节，依次为进展、数据库访问、服务器概述与服务器-客户端报文格式与serlvet。

## 进展

数据库访问与控制连接基本完成,数据连接仍未完全实现.

## 一、数据库访问

-----------------------------------------------------------

5/13 new

特殊规则

对file条目,如果noa为负值,则表示文件碎片尚未全部上传;

对fragment条目,如果path中的字符串对应的整数为负值,则表示其对应的文件未完成上传.

函数

public FileItem queryFile(int id)
public int queryFragmentNumbers(int fileId)
public int alterFragment(int id, String path)


-----------------------------------------------------------

5/11 new

### public String queryUserPasswd(String name)

参数：用户名

返回值：如果能在数据库中找到名为name的用户，返回其密码；否则返回null

### public int queryUserID(String name)

参数：用户名

返回值：如果能在数据库中找到名为name的用户，返回其id；否则返回-1

### public int addUser(String name, String passwd)

参数：用户的用户名与密码

作用：在数据库中插入该用户

返回值：成功返回用户id，失败返回-1

### public int alterUser(int id, String name, String passwd)

参数：用户的ID,用户名与密码

作用：修改ID为id的用户的用户名与密码为name,passwd

返回值：成功返回1，失败返回-1

### public int deleteUser(int id)

参数：用户ID

作用：在数据库中删除id指定的用户

返回值：成功返回1，失败返回-1

-----------------------------------------------------------

(可以使用init.sql建立一个测试数据库)

数据库访问目前通过database包进行，database中定义了4个类，其中FileItem、DeviceItem、RequestItem3个类均为为便于执行数据库操作定义的数据结构：

FileItem类是表示文件的数据结构，其包括成员变量

	private int id;
	
	private String name;
	
	private String path;
	
	private String attribute;
	
	private String time;
	
	private int noa;
	
	private boolean isFolder;
	
分别用于表示文件ID、文件名、文件逻辑路径、文件属性、修改时间、碎片数量、文件or文件夹。

FileItem类中还包括了这些成员变量的get函数与set函数，可以通过这些函数修改FileItem类的实例的值。需要注意的是，没有id的setter。

代码中可以使用构造函数

	public FileItem(String name,String path,String attribute,String time,int noa,boolean isFolder)
	
创建FileItem类的实例，使用这个构造函数创建的FileItem类实例的id为0（因为id只有被插入到数据库中后才会被分配）

DeviceItem类是表示设备的数据结构，其包括成员变量

	private int id;
	
	private String ip;
	
	private int port;
	
	private boolean isOnline;
	
	private int rs;
	
分别用于表示设备ID、IP地址、端口号、在线情况、剩余空间。

DeviceItem类中还包括了这些成员变量的get函数与set函数，可以通过这些函数修改DeviceItem类的实例的值。需要注意的是，没有id的setter。

代码中可以使用构造函数

	public DeviceItem(int id,String ip,int port,boolean isOnline,int rs)
	
创建FileItem类的实例。

RequestItem类是表示碎片请求的数据结构，其包括成员变量

	private int id;
	
	private int type;
	
	private int fragmentId;
	
	private int deviceId;
	
分别用于表示请求ID、请求类型（1服务器取分块；2服务器将分块发送给客户端；3删除客户端上的分块）、被请求的分块的id、被请求的分块所在的设备的id。

RequestItem类中还包括了这些成员变量的get函数与set函数，可以通过这些函数修改RequestItem类的实例的值。需要注意的是，没有id的setter。

代码中可以使用构造函数

	public RequestItem(int type, int fid, int did)
	
创建RequestItem类的实例，使用这个构造函数创建的RequestItem类实例的id为0（因为id只有被插入到数据库中后才会被分配）

Query类是database包中的主类，其用于执行数据库操作，Query中的函数均不是静态函数，故在使用Query类前应创建一个Query类的实例。由于Query类的实例
会维持一个到数据库的链接，其在被回收前应调用closeConnection函数关闭链接。Query类的构造函数与closeConnection函数都是无参数的。

目前Query类中定义了以下成员函数：

### public FileItem queryFile(String path,String name)

参数：文件路径、文件名

返回值：如果能在数据库中找到一个文件路径为path文件名为name的文件，则返回包括其所有数据的FileItem类的实例，否则返回null

### public FileItem[] queryFile(String path)

参数：文件路径

返回值：如果能在数据库中找到文件路径为path的文件，则返回所有符合条件的文件组成的FileItem数组，否则返回null。

### public String queryfragment(int id)

参数：碎片ID

返回值：如果能在数据库中找到指定id的碎片，返回一个包含其物理位置（实际上是其所在的主机的ID）的字符串；否则返回null

### public DeviceItem[] queryOnlineDevice()

参数：无输入参数

返回值：返回一个包含所有在线设备的DeviceItem列表，如当前没有在线设备，返回null

### public DeviceItem queryDevice(int id)

参数：设备ID

返回值：如果能在数据库中找到指定id的设备，返回一个包含其信息的DeviceItem类实例；否则返回null

### public RequestItem queryRequestById(int id)

参数：请求ID

返回值：如果能在数据库中找到指定id的请求，返回一个包含其信息的RequestItem类实例；否则返回null

### public RequestItem queryFirstRequest(int id)

参数：设备ID

返回值：如果能在数据库中找到指定id的设备的请求，返回包含一个符合条件的请求的信息的RequestItem类实例；否则返回null

### public RequestItem[] queryRequest(int deviceId)

参数：设备ID

返回值：返回由所有等待该ID指定的设备处理的请求组成的RequestItem数组，如没有等待该设备处理的请求，返回null

### public int queryRequestNumbers(int deviceId)

参数：设备ID

返回值：返回一个整数，为等待该ID指定的设备处理的请求的数量

### public int addFile(FileItem file)

参数：文件对应的FileItem实例，其中ID字段没有意义

作用：在数据库中插入file指定的文件

返回值：成功返回文件id，失败返回-1

### public int deleteFile(int id)

参数：文件的ID

作用：在数据库中删除id指定的文件

返回值：成功返回1，失败返回-1

### public int alterFile(FileItem file)

参数：文件对应的FileItem实例

作用：根据file的id修改数据库中相应表项的所有其他属性（使其与file一致）

返回值：成功返回1，失败返回-1

### public int alterDevice(DeviceItem device)

参数：设备对应的DeviceItem实例

作用：根据device的id修改数据库中相应表项的所有其他属性（使其与device一致）

返回值：成功返回1，失败返回-1

### public int addFragment(int id,String path)

参数：碎片ID，碎片物理位置

作用：在数据库中新增一个由（id，path）指定的碎片

返回值：成功返回1，失败返回-1

### public int deleteFragment(int id)

参数：碎片ID

作用：在数据库中删除id指定的碎片

返回值：成功返回1，失败返回-1

### public int addRequest(RequestItem request)

参数：请求对应的RequestItem实例，其中ID字段没有意义

作用：在数据库中插入request指定的请求

返回值：成功返回请求id，失败返回-1

### public int deleteRequest(int id)

参数：请求ID

作用：在数据库中删除id指定的请求

返回值：成功返回1，失败返回-1

## 二、服务器概述

DFS_Server类为服务器的主类，生成控制链接监听线程与数据链接监听进程；

目前的服务器除DFS_Server类外主要分3个包:

controlConnect包负责控制连接,其是在滕思洁设计的框架下经过了大量的修改后形成的，分为2个类实现:

ServerThread类为控制链接监听线程，为每个连接创建一个链接处理线程；

ClientThread类为控制链接处理线程，对于心跳链接、请求处理报文，其将一直运行并依照下一节描述的报文与客户端保持联系；

在ClientThread类中，主函数将不断接收报文直至收到出错报文或exit为止；对报文的理解与处理由readscentence函数进行。

dataConnect包负责数据连接，其也分为2个类实现:

ServerThread类为数据链接监听线程，为每个连接创建一个链接处理线程；

ClientThread类为数据链接处理线程，与控制连接不同,其不是常开的；

database包负责数据库访问,详见第一节.


## 三、服务器-客户端报文格式

### 控制链接报文

#### 客户端状态报文

客户端发送：

1 {设备ID} {客户端剩余空间}

服务器回复:

received with {等待客户端处理的请求数量} unread request!

#### 处理请求报文：

客户端发送：

3 {设备ID}

服务器回复:

{请求ID} {碎片ID} {请求类型}

接下来进入相应的处理函数

#### 中断连接报文

客户端发送：

exit

----链接中断----

### 数据链接报文

#### 上传服务器请求的碎片到服务器

客户端发送:

1 {请求ID} {碎片ID}

服务器回复:

received!

客户端回复：

{碎片内容}

服务器回复:

received!

----链接中断----

#### 根据服务器请求下载碎片

客户端发送:

2 {请求ID} {碎片ID}

服务器回复:

{碎片内容}

客户端回复:

received!

----链接中断----

#### 客户端删除碎片

客户端发送:

3 {请求ID} {碎片ID}

服务器回复:

received!

----链接中断----

#### 上传文件报文

客户端发送：

4 {设备ID} {文件名} {路径} {属性} {碎片数量} {是不是文件夹}

服务器回复：

FileId: {文件ID}

----链接中断----

#### 上传文件碎片到服务器

(在上传文件碎片前，必须先通过上传文件报文从服务器获取文件的id)
(最后一个碎片一定要最后上传,这时服务器有可能回复UPLOADFAIL表示整个文件上传失败)

客户端发送:

5 {文件ID} {碎片序号} {碎片总数}

服务器回复:

received!

客户端回复:

{碎片内容}

服务器回复:

received!

----链接中断----

## 四、servlet（已过时）
servlet文件夹下利用网页访问数据库并查询目录列表的servlet，使用方法为:

将dfstest.html放到<Tomcat-installation-directory>/webapps/ROOT/

将File_Search.class和database整个目录放到<Tomcat-installation-directory>/webapps/ROOT/WEB-INF/classes/

将mysql-connector-java-5.1.39-bin.jar放到<Tomcat-installation-directory>/webapps/ROOT/WEB-INF/lib/

重启服务器,浏览器打开localhost:8080/dfstest.html,在文件目录访问测试中输入路径名,如数据库访问正常,将显示该路径下的文件列表.

（sourse目录下为源代码）
