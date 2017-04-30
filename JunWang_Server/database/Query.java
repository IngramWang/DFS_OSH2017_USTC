package database;

import java.sql.*;

public class Query {
	
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost:3306/mysql";
    static final String USER = "root";
    static final String PASS = "sin pi=0";
    
    Connection conn = null;
	
	//构造函数
	public Query(){
		try{
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL,USER,PASS);       
        }
		catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	//相当于析构函数，实例被回收前应调用
	public void closeConnection(){
		try{
            if (conn!=null) 
            	conn.close();
        }
		catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	//查询文件 
	//参数：文件路径、文件名
	//成功返回一个包含相应表项的FileItem实例；否则返回null
	public FileItem queryFile(String path,String name){
        Statement stmt = null;
        ResultSet rs = null;
        FileItem fileItem = null;
        try{
            stmt = conn.createStatement();
            String sql;
            sql = String.format("SELECT * FROM DFS.FILE WHERE NAME='%s' AND PATH='%s'",name,path);
            rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                int id  = rs.getInt("ID");
                int noa = rs.getInt("NOA");
                String attr = rs.getString("ATTRIBUTE");
                String time = rs.getString("TIME");
                boolean isFolder = rs.getBoolean("ISFOLDER");
    
                fileItem=new FileItem(id,name,path,attr,time,noa,isFolder);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }        
        finally{
            try{
                if(rs!=null && !rs.isClosed()) 
                	rs.close();
            }
            catch(Exception e){
            }
            try{
                if(stmt!=null && !stmt.isClosed()) 
                	stmt.close();
            }
            catch(Exception e){
            }            
        }
        return fileItem;
	}
	
	//查询文件 
	//参数：文件路径
	//返回一个路径下的所有文件的列表
	public FileItem[] queryFile(String path){
		Statement stmt = null;
        ResultSet rs = null;
        FileItem fileArray[] = null;
        
        int id, noa;
        String name,attr, time;
        boolean isFolder;
        
        int count,i;
        
        try{
            stmt = conn.createStatement();
            String sql;
            sql = String.format("SELECT * FROM DFS.FILE WHERE PATH='%s'",path);
            rs = stmt.executeQuery(sql);
            
            if (!rs.last())
            	return null;           	
            count = rs.getRow();
            fileArray=new FileItem[count];
            i=0;
            rs.first();
            
            while (i<count) {
                id = rs.getInt("ID");                
                noa = rs.getInt("NOA");
                name = rs.getString("NAME");
                attr = rs.getString("ATTRIBUTE");
                time = rs.getString("TIME");
                isFolder = rs.getBoolean("ISFOLDER");
    
                fileArray[i]=new FileItem(id,name,path,attr,time,noa,isFolder);
                rs.next();
                i++;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }        
        finally{
            try{
                if(rs!=null && !rs.isClosed()) 
                	rs.close();
            }
            catch(Exception e){
            }
            try{
                if(stmt!=null && !stmt.isClosed()) 
                	stmt.close();
            }
            catch(Exception e){
            }            
        }
        return fileArray;
	}
	
	//查询文件碎片
	//参数：碎片ID
	//成功返回一个包含其物理位置的字符串；否则返回null
	public String queryfragment(int id){
        Statement stmt = null;
        ResultSet rs = null;
        String path = null;
        try{
            stmt = conn.createStatement();
            String sql;
            sql = String.format("SELECT * FROM DFS.FRAGMENT WHERE ID='%d'",id);
            rs = stmt.executeQuery(sql);
            
            if (rs.next())
                path = rs.getString("PATH");
        }
        catch(Exception e){
            e.printStackTrace();
        }        
        finally{
            try{
                if(rs!=null && !rs.isClosed()) 
                	rs.close();
            }
            catch(Exception e){
            }
            try{
                if(stmt!=null && !stmt.isClosed()) 
                	stmt.close();
            }
            catch(Exception e){
            }            
        }
        return path;
	}
	
	//查询设备
	//无输入参数
	//返回一个包含所有在线设备的DeviceItem列表
	public DeviceItem[] queryOnlineDevice(){
        Statement stmt = null;
        ResultSet rs = null;
        DeviceItem deviceArray[] = null;
        
        String ip;
        int port,rst,id;

        int count,i;

        try{
            stmt = conn.createStatement();
            String sql;
            sql = String.format("SELECT * FROM DFS.DEVICE WHERE ISONLINE=true ORDER BY RS DESC");
            rs = stmt.executeQuery(sql);
            
            if (!rs.last())
                return null;
            count = rs.getRow();
            deviceArray=new DeviceItem[count];
            i=0;
            rs.first();

            while (i<count){

                id = rs.getInt("ID");
                ip  = rs.getString("IP");
                port = rs.getInt("PORT");
                rst = rs.getInt("RS");
  
                deviceArray[i]=new DeviceItem(id,ip,port,true,rst);
                rs.next();
                i++;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }        
        finally{
            try{
                if(rs!=null && !rs.isClosed()) 
                    rs.close();
            }
            catch(Exception e){
            }
            try{
                if(stmt!=null && !stmt.isClosed()) 
                    stmt.close();
            }
            catch(Exception e){
            }            
        }
        return deviceArray;
	}

	//查询在线设备
	//参数：设备ID
	//成功返回一个包含相应表项的DeviceItem实例；否则返回null
	public DeviceItem queryDevice(int id){
		Statement stmt = null;
        ResultSet rs = null;
        DeviceItem deviceItem = null;
        try{
            stmt = conn.createStatement();
            String sql;
            sql = String.format("SELECT * FROM DFS.DEVICE WHERE ID='%d'",id);
            rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                String ip  = rs.getString("IP");
                int port = rs.getInt("PORT");
                boolean isOnline = rs.getBoolean("ISONLINE");
                int rst = rs.getInt("RS");
  
                deviceItem=new DeviceItem(id,ip,port,isOnline,rst);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }        
        finally{
            try{
                if(rs!=null && !rs.isClosed()) 
                	rs.close();
            }
            catch(Exception e){
            }
            try{
                if(stmt!=null && !stmt.isClosed()) 
                	stmt.close();
            }
            catch(Exception e){
            }            
        }
        return deviceItem;
	}
	
	//新增文件 
	//参数：文件对应的FileItem实例，其中ID字段没有意义
	//成功返回文件id，失败返回-1
	public int addFile(FileItem file){
		Statement stmt = null;
		ResultSet rs = null;
        int suc = -1;
        try{
            stmt = conn.createStatement();
            String sql;
            if (file.isFolder())
	            sql = String.format("INSERT INTO DFS.FILE (NAME,PATH,ATTRIBUTE,TIME,NOA,ISFOLDER) "
	            		+ "VALUES ('%s','%s','%s','%s',%d,true);",file.getName(),file.getPath(),
	            		file.getAttribute(),file.getTime(),file.getNoa());
            else
            	sql = String.format("INSERT INTO DFS.FILE (NAME,PATH,ATTRIBUTE,TIME,NOA,ISFOLDER) "
	            		+ "VALUES ('%s','%s','%s','%s',%d,false);",file.getName(),file.getPath(),
	            		file.getAttribute(),file.getTime(),file.getNoa());
            suc = stmt.executeUpdate(sql);
            if (suc>0){
            	rs = stmt.executeQuery("select LAST_INSERT_ID()");
            	rs.next();
            	suc=rs.getInt(1);
            }
            else
            	suc=-1;
        }
        catch(Exception e){
            e.printStackTrace();
        }        
        finally{
            try{
                if(rs!=null && !rs.isClosed()) 
                	rs.close();
            }
            catch(Exception e){
            }
            try{
                if(stmt!=null && !stmt.isClosed()) 
                	stmt.close();
            }
            catch(Exception e){
            }            
        }
        return suc;
	}

	//删除文件
	//参数：文件的ID
	//成功返回1，失败返回-1
	//删除文件并不会在碎片数据库中删除文件对应的碎片
	public int deleteFile(int id){
		Statement stmt = null;
        int suc = -1;
        try{
            stmt = conn.createStatement();
            String sql;
            sql = String.format("DELETE FROM DFS.FILE WHERE ID=%d",id);
            suc = stmt.executeUpdate(sql);
            if (suc>0)
            	return 1;
            else
            	return -1;
        }
        catch(Exception e){
            e.printStackTrace();
        }        
        finally{
            try{
                if(stmt!=null && !stmt.isClosed()) 
                	stmt.close();
            }
            catch(Exception e){
            }            
        }
        return suc;
	}
	
	//修改文件 
	//参数：包含文件相应属性的FileItem实例(其中id必须和数据库中已有的id匹配)
	//成功返回1，失败返回-1
	public int alterFile(FileItem file){
		Statement stmt = null;
        int suc = -1;
        try{
            stmt = conn.createStatement();
            String sql;
            if (file.isFolder())
	            sql = String.format("UPDATE DFS.FILE SET NAME='%s',PATH='%s',ATTRIBUTE='%s',"
	            		+ "TIME='%s',NOA=%d,ISFOLDER=true WHERE id=%d;",file.getName(),file.getPath(),
	            		file.getAttribute(),file.getTime(),file.getNoa(),file.getId());
            else
            	sql = String.format("UPDATE DFS.FILE SET NAME='%s',PATH='%s',ATTRIBUTE='%s',"
	            		+ "TIME='%s',NOA=%d,ISFOLDER=false WHERE id=%d;",file.getName(),file.getPath(),
	            		file.getAttribute(),file.getTime(),file.getNoa(),file.getId());
            suc = stmt.executeUpdate(sql);
            if (suc>0)
            	return 1;
            else
            	return -1;
        }
        catch(Exception e){
            e.printStackTrace();
        }        
        finally{
            try{
                if(stmt!=null && !stmt.isClosed()) 
                	stmt.close();
            }
            catch(Exception e){
            }            
        }
        return suc;
	}
	
	//修改设备
	//参数：设备对应的DeviceItem实例(其中id必须和数据库中已有的id匹配)
	//成功返回1，失败返回-1
	public int alterDevice(DeviceItem device){
		Statement stmt = null;
        int suc = -1;
        try{
            stmt = conn.createStatement();
            String sql;
            if (device.isOnline())
	            sql = String.format("UPDATE DFS.DEVICE SET IP='%s',PORT=%d,ISONLINE=true,"
	            		+ "RS=%d WHERE id=%d;",device.getIp(),device.getPort(),device.getRs(),
	            		device.getId());
            else
            	sql = String.format("UPDATE DFS.DEVICE SET IP='%s',PORT=%d,ISONLINE=false,"
	            		+ "RS=%d WHERE id=%d;",device.getIp(),device.getPort(),device.getRs(),
	            		device.getId());
            suc = stmt.executeUpdate(sql);
            if (suc>0)
            	return 1;
            else
            	return -1;
        }
        catch(Exception e){
            e.printStackTrace();
        }        
        finally{
            try{
                if(stmt!=null && !stmt.isClosed()) 
                	stmt.close();
            }
            catch(Exception e){
            }            
        }
        return suc;
	}
	
	//新增、删除设别需要管理员手动操作服务器执行，故无相关接口函数
	
	//新增碎片
	//参数：碎片ID，碎片物理位置
	//成功返回1，失败返回-1
	public int addFragment(int id,String path){
		Statement stmt = null;
        int suc = -1;
        try{
            stmt = conn.createStatement();
            String sql;
	        sql = String.format("INSERT INTO DFS.FRAGMENT VALUES (%d,'%s');",
	        		id,path);
            suc = stmt.executeUpdate(sql);
            if (suc>0)
            	suc=1;
            else
            	suc=-1;
        }
        catch(Exception e){
            e.printStackTrace();
        }        
        finally{
            try{
                if(stmt!=null && !stmt.isClosed()) 
                	stmt.close();
            }
            catch(Exception e){
            }            
        }
        return suc;
	}

	//删除碎片
	//参数：碎片ID
	//成功返回0，失败返回-1
	public int deleteFragment(int id){
		Statement stmt = null;
        int suc = -1;
        try{
            stmt = conn.createStatement();
            String sql;
	        sql = String.format("DELETE FROM DFS.FRAGMENT WHERE ID=%d",id);
            suc = stmt.executeUpdate(sql);
            if (suc>0)
            	suc=1;
            else
            	suc=-1;
        }
        catch(Exception e){
            e.printStackTrace();
        }        
        finally{
            try{
                if(stmt!=null && !stmt.isClosed()) 
                	stmt.close();
            }
            catch(Exception e){
            }            
        }
        return suc;
	}
}
