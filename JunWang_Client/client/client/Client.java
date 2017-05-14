package client;

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

public class Client {
	String serverIp;
	int controlPort, dataPort;
	int clientId;
	String uploadFolder;
	String fragmentFolder;
	String tmpFragmentFolder;
	SynItem syn;
		
	private static final String setUpFile="setup.ini";
	
	public Client(){
		System.out.println("client start");
	}
	
	public static void main(String[] args) {
		Client client = new Client();
		if (!client.setUp()){
			System.out.println("ERROR: can not read setup.ini");
			return;
		}
		client.begin();
	}
	
	public static int getRS() {
		//返回剩余容量,待实现
		return 250;		
	}
	
	private boolean setUp() {
		boolean status=true;
		try{
			FileInputStream f = new FileInputStream(setUpFile);
			Scanner scanner=new Scanner(f);
			serverIp=scanner.nextLine();
			controlPort=scanner.nextInt();
			dataPort=scanner.nextInt();
			clientId=scanner.nextInt();
				//empty line
				uploadFolder=scanner.nextLine();
			uploadFolder=scanner.nextLine();
			fragmentFolder=scanner.nextLine();
			tmpFragmentFolder=scanner.nextLine();
			scanner.close();
			f.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		File file=new File(uploadFolder);
		if (!file.exists() || !file.isDirectory())
			status=false;
		file=new File(fragmentFolder);
		if (!file.exists() || !file.isDirectory())
			status=false;
		connect.FragmentManager.init(file, serverIp, dataPort);
		file=new File(tmpFragmentFolder);
		if (!file.exists() || !file.isDirectory())
			status=false;
		
		return status;
	}
	
	private void begin(){
		
		syn=new SynItem(0);
		
		connect.ServerConnecter serverConnecter=new connect.ServerConnecter(serverIp, controlPort, 
				dataPort, clientId, syn);
		
		serverConnecter.start();
		
		syn.waitChange(0);
		
		System.out.println("Err: can not connect to server");
	}
}
