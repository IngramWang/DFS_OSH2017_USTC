package dataConnect;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {
	
		ServerSocket server;
		
		public ServerThread(int port) throws IOException{
			server = new ServerSocket(port);
			System.out.println("control socket setup!\n");
		}
		
		public void run(){
			while(true){
				try {
					Socket socket = server.accept();
					ClientThread thread =  new ClientThread(socket);
					thread.start();
					System.out.println("accepted a data link!\n");
                }catch (IOException e) {
					// TODO Auto-generated catch block
				}
			}
		}
	}	