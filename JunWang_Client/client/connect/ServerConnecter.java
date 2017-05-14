package connect;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerConnecter extends Thread {
	Socket toServer;
	String serverIP;
	int controlPort, dataPort;
	int clientId;
	FragmentManager fragmentManager;
	client.SynItem syn;

	public ServerConnecter(String sIp, int cPort, int dPort, int cId, client.SynItem s) {
		serverIP = sIp;
		controlPort = cPort;
		dataPort = dPort;
		clientId = cId;
		syn=s;
	}

	@Override
	public void run() {
		DataOutputStream outToServer = null;
		BufferedReader inFromServer = null;
		boolean status=true;
		String input,str[];
		
		while (true) {
			try {
				toServer = new Socket(serverIP, controlPort);
				outToServer = new DataOutputStream(toServer.getOutputStream());
				inFromServer = new BufferedReader(new InputStreamReader(toServer.getInputStream()));
				System.out.println("Connect to server successfully(control)!");
			} catch (Exception e){
				e.printStackTrace();
				status=false;
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
			}
			
			if (!status){
				break;
			}
			
			while (true){
				try {	
					outToServer.writeBytes(String.format("1 %d %d\n", clientId, client.Client.getRS()));
					outToServer.flush();
					input=inFromServer.readLine();
					str=input.split(" ");
					
					//debug
					System.out.println(input);
					
					int unreadRequest=Integer.parseInt(str[2]);
					while (unreadRequest>0){
						outToServer.writeBytes(String.format("2 %d\n", clientId));
						outToServer.flush();
						input=inFromServer.readLine();
						str=input.split(" ");
						int requestId=Integer.parseInt(str[0]);
						int fragmentId=Integer.parseInt(str[1]);
						int type=Integer.parseInt(str[2]);
						FragmentManager fManager=new FragmentManager(requestId, fragmentId, type);
						fManager.submit();
						unreadRequest--;
					}
					
					Thread.sleep(5000);				
				} catch (IOException e) {
					e.printStackTrace();
					break;
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}
			
			try{
				outToServer.writeBytes("exit\n");
				outToServer.flush();
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			try{
				outToServer.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			try{
				inFromServer.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			try{
				toServer.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		syn.setStatus(1);
		System.out.println("ERR: connect to server has been interrupted!");
	}

}
