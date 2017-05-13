package controlConnect;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {
	
		ServerSocket server;
		
		public ServerThread(int port) throws IOException{
			server = new ServerSocket(port);
			System.out.println("control socket setup!\n");
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
                    System.out.println("accepted a control link!\n");
                    }catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					}
			}
		}
	}	