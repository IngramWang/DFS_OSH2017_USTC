import java.awt.*;
import java.awt.event.*;
import javax.swing.border.TitledBorder;
import javax.swing.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class DFS_server {
	static class ServerThread extends Thread {
		ServerSocket server;
		public ServerThread(int port) throws IOException{
			server = new ServerSocket(port);
			//server.setSoTimeout(10000);
		}
		public void run(){
			while(true){
				try {
					Socket socket = server.accept();
		//			socket.setSoTimeout(10000);
					ClientThread thread =  new ClientThread(socket);
					thread.start();
					    /*{catch(SocketTimeoutException s){
            			System.out.println("Socket timed out!");
                    }*/
                    System.out.println("accepted0!\n");
                    }catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					}
			}
		}
	}	
	
	static class ClientThread extends Thread {
		Socket clientsocket;
		String scentence;
		public ClientThread(Socket scoket){
			this.clientsocket = scoket;
		}
		
		public void run(){
			System.out.println("start!");
			try{
				try{
					clientsocket.setKeepAlive(true);
					clientsocket.setSoTimeout(60000);	
					while(true){
					BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));//从客户端得到的数据流
					String sentence = inFromClient.readLine();//对得到的流进行处理
					readscentence(sentence);
					if(sentence.equals("exit")) 
						break;
					System.out.println("RECV: "+ sentence);
					DataOutputStream outToClient = new DataOutputStream(clientsocket.getOutputStream());
					outToClient.writeBytes("received!");
					}
				} catch(SocketTimeoutException s){
    		    System.out.println("Socket timed out!");
     			} catch(IOException e){
        		e.printStackTrace();
    			}
    			clientsocket.close();
    		}catch(IOException e){
    			e.printStackTrace();
    		}
    	}
		
    	private int readscentence(String sentence){
    		System.out.println(sentence.charAt(0));
    		if(sentence.charAt(0) == '1'){
    			String[] strings = new String[3];
    			int i = 0;
    			int fromIndex = 1;
    			int endIndex = sentence.indexOf(' ',fromIndex + 1);
    			if(endIndex == -1)
    				return 0;
    			strings[0] = sentence.substring(fromIndex + 1,endIndex);
    			strings[2] = sentence.substring(endIndex + 1,sentence.length());
    			
    			strings[1]=clientsocket.getInetAddress().getHostAddress();
    			System.out.println(strings[1]);
    			int id = Integer.parseInt(strings[0]);
    			int port = clientsocket.getPort();
    			int rs = Integer.parseInt(strings[2]);
    			
    			database.Query query=new database.Query();
    			database.DeviceItem deviceitem;
    			deviceitem=query.queryDevice(id);
    			if(deviceitem == null){
    				deviceitem = new database.DeviceItem(id,strings[1],port,true,rs);
    				query.alterDevice(deviceitem);
    			}
    			else{
    				deviceitem.setIp(strings[1]);
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
    			database.DeviceItem deviceitem = query.queryDevice(id);
    			Date date = new Date();
    			@SuppressWarnings("deprecation")
				String time = String.format("%d%d%d", date.getYear()+1900, date.getMonth()+1, date.getDate());
    			database.FileItem fileitem = new database.FileItem(strings[1],strings[2],strings[3],time,noa,isf);
    			query.addFile(fileitem);
                //在这里调用发送碎片的函数

    			query.closeConnection();
    			return 1;
    		}
    		return 0;
    	}    	
    }	
	
	
	public static void main(String args[]) throws Exception{
     	int port = 6668;
      	try{
         Thread t = new ServerThread(port);
         t.start();
      	}catch(IOException e){
         e.printStackTrace();
      	}
      	
      	
	}
}