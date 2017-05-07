import java.net.Socket;

public class ConnectItem {
	
	private Socket socket;
	private int id;
	
	public ConnectItem(Socket socket, int id){
		this.socket=socket;
		this.id=id;
	}
	
	public int getId() {
		return id;
	}
	
	public Socket getSocket() {	
		return socket;
	}

}
