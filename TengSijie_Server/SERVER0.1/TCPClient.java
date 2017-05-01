
import java.io.*;
import java.net.*;

public class TCPClient {
	public static void main(String args[]) throws Exception{
		Socket clientSocket = new Socket("localhost",1234);
		while(true){
		System.out.print("Input\n");
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		String sentence = inFromUser.readLine();
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		outToServer.writeBytes(sentence+'\n');
		}
	}
	
}
