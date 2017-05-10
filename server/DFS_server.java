import java.io.IOException;

public class DFS_server {
	
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