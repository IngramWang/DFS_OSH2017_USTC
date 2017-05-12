package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.Socket;

public class FragmentManager extends Thread{
	
	private static File fragmentFolder=null;
	private static String serverIP=null;
	private static int serverPort=-1;
	
	Socket toServer;
	DataOutputStream outToServer;
	BufferedReader inFromServer;
	int requestId,fragmentId,type;
	
	public FragmentManager(int rId, int fId, int t) {
		requestId=rId;
		fragmentId=fId;
		type=t;
	}
	
	@Override
	public void run(){
		//暂不进行并发数据操作
	}
	
	public int submit(){
		int status = 1;
		
		if (serverIP==null)
			return -1;
		
		try {
			toServer = new Socket(serverIP, serverPort);
			outToServer = new DataOutputStream(toServer.getOutputStream());
			inFromServer = new BufferedReader(new InputStreamReader(toServer.getInputStream()));
			System.out.println("Connect to server successfully(data)!");
		} catch (Exception e){
			e.printStackTrace();
			try{
				outToServer.close();				
			} catch (Exception ex) {
				// TODO: handle exception
			}
			try{
				inFromServer.close();
			} catch (Exception ex) {
				// TODO: handle exception
			}
			try{
				toServer.close();
			} catch (Exception ex) {
				// TODO: handle exception
			}
			return -1;
		}
		
		if (type==1)
			status = sendFragment();
		else if (type==2)
			status = recvFragment();
		else if (type==3)
			status = deleteFragment();
		
		try{
			outToServer.close();				
		} catch (Exception ex) {
			// TODO: handle exception
		}
		try{
			inFromServer.close();
		} catch (Exception ex) {
			// TODO: handle exception
		}
		try{
			toServer.close();
		} catch (Exception ex) {
			// TODO: handle exception
		}
		
		return status;
	}
	
	public static void init(File f, String ip, int port){
		fragmentFolder=f;
		serverIP=ip;
		serverPort=port;
	}
	
	private int sendFragment() {
		return 1;
	}
	
	private int recvFragment() {
		return 1;
	}
	
	private int deleteFragment() {
		return 1;
	}

}
