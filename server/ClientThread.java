import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

class ClientThread extends Thread {
		Socket clientsocket;		
		String scentence;
		
		public ClientThread(Socket scoket){
			this.clientsocket = scoket;
		}
		
		public void run(){
			BufferedReader inFromClient=null;
			DataOutputStream outToClient=null;
			
			System.out.println("start!");
			try{
				clientsocket.setKeepAlive(true);
				clientsocket.setSoTimeout(60000);	
				while(true){
					inFromClient = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
					String sentence = inFromClient.readLine();//对得到的流进行处理
					readscentence(sentence);
					if(sentence.equals("exit")) 
						break;
					System.out.println("RECV: "+ sentence);
					outToClient = new DataOutputStream(clientsocket.getOutputStream());
					outToClient.writeBytes("received!");				
					outToClient.flush();;
				}
			} catch(SocketTimeoutException s){
				System.out.println("Socket timed out!");
			} catch(IOException e){
				e.printStackTrace();
			}
			
			try {
				inFromClient.close();
				clientsocket.close();
				outToClient.close();
			}
			catch (Exception e) {
				// TODO: handle exception
			}
    	}
		
    	private int readscentence(String sentence){
    		System.out.println(sentence.charAt(0));
    		if(sentence.charAt(0) == '1'){
    			String string;
    			int fromIndex = sentence.indexOf(' ',2);
    			if(fromIndex == -1)
    				return 0;
    			int endIndex = sentence.indexOf(' ',fromIndex + 1);
    			if(endIndex == -1)
    				return 0;
    			string = sentence.substring(2,fromIndex);
    			int id = Integer.parseInt(string);
    			
    			string = sentence.substring(fromIndex + 1,endIndex);
    			int port = Integer.parseInt(string);
    			
    			string = sentence.substring(endIndex + 1,sentence.length());
    			int rs = Integer.parseInt(string);
    			
    			string=clientsocket.getInetAddress().getHostAddress();
    			System.out.println(string);
    			
    			database.Query query=new database.Query();
    			database.DeviceItem deviceitem;
    			deviceitem=query.queryDevice(id);
    			if(deviceitem == null){
    				// 不允许通过报文新建client
    			}
    			else{
    				deviceitem.setIp(string);
    				deviceitem.setPort(port);
    				deviceitem.setIsOnline(true);
    				deviceitem.setRs(rs);
    				query.alterDevice(deviceitem);
    			}
    			query.closeConnection();
    			return 1;
    		}
		if(sentence.charAt(0) == '2'){
    			String[] strings = new String[6];
    			int i = 0;
    			int fromIndex = 1;
    			int endIndex = sentence.indexOf(32,fromIndex + 1);
    			if (endIndex == -1)
    				return 0;
    			while(i < 6){
    				strings[i] = sentence.substring(fromIndex + 1,endIndex);
    				fromIndex = endIndex;
    				if(sentence.indexOf(32,fromIndex + 1)!= -1)
    					endIndex = sentence.indexOf(32,fromIndex + 1);
    				else
    					endIndex = sentence.length();
    				i++;
    			}
    			int id = Integer.parseInt(strings[0]);
    			int noa = Integer.parseInt(strings[4]);
    			boolean isf=Boolean.parseBoolean(strings[5]);
    			
    			database.Query query=new database.Query();
    			database.DeviceItem deviceItem = query.queryDevice(id);
    			Date date = new Date();
    			@SuppressWarnings("deprecation")
				String time = String.format("%d%02d%02d", date.getYear()+1900, date.getMonth()+1, date.getDate());
    			database.FileItem fileitem = new database.FileItem(strings[1],strings[2],strings[3],time,noa,isf);
    			int fid = query.addFile(fileitem);
                //在这里调用接收/发送碎片的函数
    			try{
    				DataOutputStream outToClient = new DataOutputStream(clientsocket.getOutputStream());
    				outToClient.writeBytes("received!");
    				String s=getFragment(noa);
    				confirm(fid, noa, s);
    			}catch (IOException e) {
					// TODO: handle exception
				}

    			query.closeConnection();
    			return 1;
    		}
    		return 0;
    	}   
    	
    	public int confirm(int id, int num, String path) {
    		
    		database.Query query=new database.Query();
    		
    		// 根据上面的方法,获得所有的在线主机
    		database.DeviceItem[] di = query.queryOnlineDevice();

    		ArrayList<ConnectItem> listAll = new ArrayList<ConnectItem>();

    		// 将获得所有在线主机建立socket，并保存在Arraylist中
    		for (int i = 0; i < di.length; i++) {
    			try {
    				// for循环创建socket
    				Socket socket = new Socket(di[i].getIp(), di[i].getPort());   				
    				listAll.add(new ConnectItem(socket, di[i].getId()));// 添加到集合中
    			} catch (IOException e) {
    				//test
    				listAll.add(new ConnectItem(null, di[i].getId()));// 添加到集合中
    				//e.printStackTrace();
    			}
    		}
    		
    		if (listAll.isEmpty()){
    			return 1;
    		}

    		Random rd = new Random();
    		// 根据碎片数量确定要发送碎片数量的主机
    		
    		int size=listAll.size();
    		if (num  <= size) {
    			// 如果碎片数量小于在线主机数
    			int t = rd.nextInt(listAll.size());
    			for (int i = 0; i < num; i++) {
    				try {
    					//文件发送函数
    					sentFragment(listAll.get((i+t)%size).getSocket(),path+i);
    					query.addFragment(id*100+t, Integer.toString(listAll.get((i+t)%size).getId()));
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    			}
    		} 
    		
    		else {
    			//如果碎片数量大于在线主机数
    			int[] n = new int[size];//新建一个数组存放每个主机要发送的碎片数量
    			//首先所有要发送的碎片数量为num/di.length
    			for (int i = 0; i < size; i++) {
    				n[i] = num/size;
    			}
    			int m = num % size;//剩余的碎片数量
    			
    			int t = rd.nextInt(listAll.size());
    			for (int i = 0; i < m; i++) {
    				n[t%size]++;
    				t++;
    			}

    			t=0;
    			for (int i = 0; i < size; i++) {
    				try {
    					for (int j = 0; j < n[i]; j++) {
    						//碎片发送函数
    						sentFragment(listAll.get(i).getSocket(),path+t);   						
    						query.addFragment(id*100+t, Integer.toString(listAll.get(i).getId()));
    						t++;
    					}
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    			}   			
    		}
    		
    		size=listAll.size();
    		for (int i=0;i<size;i++){
    			try{
    				listAll.get(i).getSocket().close();
    			}
    			catch (Exception e) {
					// TODO: handle exception
				}
    		}
    		
    		return 0;
    	}
    	
    	private String getFragment(int noa) {
    		//接收碎片的函数,待实现
			return "tmp";
		}
    	
    	private void sentFragment(Socket soc, String path) {
    		//发送碎片的函数,待实现			
		}

    }