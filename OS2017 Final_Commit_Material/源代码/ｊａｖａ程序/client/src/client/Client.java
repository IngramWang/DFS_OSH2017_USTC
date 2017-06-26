package client;

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

public class Client {
	private int clientId;
	private File uploadFolders[];
	private String uploadAddrs[];
	private SynItem syn;
		
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
		Scanner scanner=null;
		String uploadFolder, fragmentFolder, tmpFragmentFolder;
		int controlPort, dataPort;
		String serverIp;
		
		controlPort=0;
		
		try{
			FileInputStream f = new FileInputStream(setUpFile);
			scanner=new Scanner(f);
			serverIp=scanner.nextLine();
			controlPort=scanner.nextInt();
			dataPort=scanner.nextInt();
			clientId=scanner.nextInt();
				//empty line
				scanner.nextLine();
			fragmentFolder=scanner.nextLine();
			tmpFragmentFolder=scanner.nextLine();
			int i=scanner.nextInt();
			uploadFolders=new File[i];
			uploadAddrs=new String[i];
				//empty line
				scanner.nextLine();
			for (int j=0;j<i;j++){
				uploadFolder=scanner.nextLine();
				uploadFolders[j]=new File(uploadFolder);
				if (!uploadFolders[j].exists() || !uploadFolders[j].isDirectory())
					throw new Exception();
				uploadAddrs[j]=scanner.nextLine();
			}
			
			scanner.close();
			f.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			scanner.close();
			return false;
		}
		
		connect.ServerConnecter.init(serverIp, controlPort);
		File file=new File(fragmentFolder);
		if (!file.exists() || !file.isDirectory())
			return false;
		connect.FragmentManager.init(file, serverIp, dataPort);
		file=new File(tmpFragmentFolder);
		if (!file.exists() || !file.isDirectory())
			return false;
		fileDetector.FolderScanner.init(file);
		fileDetector.FileUploader.init(file, serverIp, dataPort);		
		return true;
	}
	
	private void begin(){
		
		syn=new SynItem(0);
		
		connect.ServerConnecter serverConnecter=new connect.ServerConnecter(clientId, syn);
		
		serverConnecter.start();
		
		fileDetector.FolderScanner folderScanner=new fileDetector.FolderScanner(
				uploadFolders, uploadAddrs, syn);
		
		folderScanner.start();
		
		syn.waitChange(0);
		
		if (syn.getStatus()==1){
			System.out.println("Err: can not connect to server");			
		}
		else if (syn.getStatus()==2) {
			System.out.println("Err: can detect files");
		}
		folderScanner.stopDetecting();
		serverConnecter.stopConnect();
		
	}
}
