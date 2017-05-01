import java.awt.*;
import java.awt.event.*;
import javax.swing.border.TitledBorder;
import javax.swing.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
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
					if(sentence.equals("exit")) break;
					System.out.println("RECV: "+ sentence);
					DataOutputStream outToClient = new DataOutputStream(clientsocket.getOutputStream());
					outToClient.writeBytes("received!");
					}
				}catch(SocketTimeoutException s){
    		    System.out.println("Socket timed out!");
     			}catch(IOException e){
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
    			String[] strings = new String[4];
    			int i = 0;
    			int fromIndex = 1;
    			int endIndex = sentence.indexOf(32,fromIndex + 1);
    			if(endIndex == -1)
    				return 0;
    			while(i < 4){
    				System.out.println("indexs: " + fromIndex + "and" + endIndex);
    				strings[i] = sentence.substring(fromIndex + 1,endIndex);
    				System.out.println(strings[i]);
    				fromIndex = endIndex;
    				if(sentence.indexOf(32,fromIndex + 1) != -1)
    					endIndex = sentence.indexOf(32,fromIndex + 1);
    				else
    					endIndex = sentence.length();
    				i++;
    			}
    	/*		int id = Integer.parseInt(strings[0]);
    			int port = Integer.parseInt(strings[2]);
    			int rs = Integer.parseInt(strings[3]);
    			DeviceItem deviceitem;
    			if(queryDevice(int id) == null){
    				deviceitem = DeviceItem(id,strings[1],port,1,rs);
    			}
    			else{
    				deviceitem = queryDevice(id);
    				deviceitem.setIp(strings[2]);
    				deviceitem.setPort(port);
    				deviceitem.isOnline(true);
    				deviceitem.setRs(rs);
    			}*/
    			return 1;
    		}
  /*  		if(sentence.charAt(0) == '2'){
    			String[] strings = new String[5];
    			int i = 0;
    			int fromIndex = 1;
    			int endIndex = sentence.indexOf(32,fromIndex + 1);
    			if(endIndex == -1)
    				retrun 0;
    			while(i < 5){
    				strings[i] = sentence.substring(fromIndex + 1,endIndex);
    				fromIndex = endIndex;
    				if(sentence.indexOf(32,fromIndex + 1)!= -1)
    					endIndex = sentence.indexOf(32,fromIndex + 1);
    				else
    					endIndex = sentence.length;
    				i++;
    			}
    			int id = Integer.parseInt(strings[0]);
    			int noa = Integer.parseInt(strings[4]);
    			DeviceItem deviceitem = queryDevice(id);
    			Date date = new Date;
    			String time = date.toString();
    			FileItem fileitem = FileItem(strings[1],strings[2],strings[3],time,noa,false);
    			addFile(fileitem);
                //在这里调用发送碎片的函数




    			//为文件创建目录
    			creat_dir_of_file(strings[2],strings[3],time);
    //			string dir = strings[2];
    			return 1;
    		}*/
    		return 0;
    	}
    	/*
    	private int creat_dir_of_file(String filedir,String attribute,String time){
    		int fromIndex,endIndex,startIndex;
    		startIndex = filedir.indexOf('/');//文件目录的开始
    		fromIndex = filedir.indexOf('/');//当前目录开始
    		endIndex = filedir.indexOf('/',fromIndex + 1);//当前目录结束
    		while(endIndex != -1){
    			String nowdir = filedir.substring(fromIndex + 1,endIndex);//当前目录名
    			String path = filedir.substring(startIndex,fromIndex + 1);//当前path
    			if(queryFile(path,nowdir) == NULL){
    				FileItem diritem = FileItem(nowdir,path,attribute,time,0,true);
    				addFile(diritem);
    			}
    			fromIndex = endIndex;
    			endIndex = filedir.indexOf("/",fromIndex + 1);
    		}
    		return 1;
    	}*/
    }	
	public static void main(String args[]) throws Exception{
     	int port = Integer.parseInt(args[0]);
      	try{
         Thread t = new ServerThread(port);
         t.start();
      	}catch(IOException e){
         e.printStackTrace();
      	}	
	}
}