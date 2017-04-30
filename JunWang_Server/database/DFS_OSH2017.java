package database;

//testcase for database

public class DFS_OSH2017 {

	public static void main(String[] args) {
		FileItem fileItem[];
		FileItem f;
		DeviceItem deviceItem,deviceArray[];
		String string;
		int i,j;
		Query query = new Query();
		
		fileItem=query.queryFile("TIM/hello/");		
		if (fileItem==null)
			System.out.println("no result!");
		else{
			i=fileItem.length;
			for (j=0;j<i;j++)
				System.out.println(String.format("%d-%s-%s", fileItem[j].getId(), fileItem[j].getAttribute(), fileItem[j].getName()));
		}
		
		deviceArray = query.queryOnlineDevice();
		if (deviceArray==null)
			System.out.println("no result!");
		else{
			i=deviceArray.length;
			for (j=0;j<i;j++)
				System.out.println(String.format("%d-%d-%s", deviceArray[j].getId(), deviceArray[j].getRs(), deviceArray[j].getIp()));
		}
		
//		
//		string=query.queryfragment(91);
//		if (string==null)
//			System.out.println("no result!");
//		else
//			System.out.println("location:"+string);
//		
//		deviceItem=query.queryDevice(9);
//		if (deviceItem==null)
//			System.out.println("no result!");
//		else
//			System.out.println(String.format("%s-%d", deviceItem.getIp(), deviceItem.getPort())+deviceItem.getIsOnline());
//		
//		f=new FileItem("8.txt", "TIM/hi/", "rw-r--r--", "20170421", 2, false);
//		i=query.addFile(f);
//		System.out.println(String.format("%d", i));
//		
//		i=query.deleteFile(11);
//		System.out.println(String.format("%d", i));
		
//		f=query.queryFile("TIM/hello/", "2.txt");
//		f.setNoa(8);
//		f.setName("2.md");
//		i=query.alterFile(f);
//		System.out.println(String.format("%d", i));
		
//		deviceItem=query.queryDevice(1);
//		deviceItem.setIsOnline(true);
//		deviceItem.setRs(50);
//		i=query.alterDevice(deviceItem);
//		System.out.println(String.format("%d", i));
		
//		System.out.println(String.format("%d", query.addFragment(101, "judy/")));
//		System.out.println(String.format("%d", query.deleteFragment(101)));
//				
		query.closeConnection();
	}

}
