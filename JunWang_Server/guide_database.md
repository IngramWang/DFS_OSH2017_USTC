# 数据库访问指南

本文介绍如何使用database包.

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

Query类的一个使用例子如

	database.Query query = new Query();

	database.FileItem f = query.queryFile(10);

	System.out.println(f.getName());

	query.closeConnection();

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
