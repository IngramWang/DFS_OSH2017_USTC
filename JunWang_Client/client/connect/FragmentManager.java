package connect;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;

public class FragmentManager extends Thread {

	private static File fragmentFolder = null;
	private static String serverIP = null;
	private static int serverPort = -1;

	Socket toServer;
	DataOutputStream outToServer = null;
	DataInputStream inFromServer = null;
	int requestId, fragmentId, type;

	public FragmentManager(int rId, int fId, int t) {
		requestId = rId;
		fragmentId = fId;
		type = t;
	}

	@Override
	public void run() {
		// 暂不进行并发数据操作
		//submit();		
	}

	public boolean submit() {
		boolean status = true;

		if (serverIP == null)
			return false;

		try {
			toServer = new Socket(serverIP, serverPort);
			toServer.setKeepAlive(true);
			toServer.setSoTimeout(5000);
			outToServer = new DataOutputStream(toServer.getOutputStream());
			inFromServer = new DataInputStream(new BufferedInputStream(toServer.getInputStream()));
			System.out.println("Connect to server successfully(data)!");
		} catch (Exception e) {
			e.printStackTrace();
			try {
				outToServer.close();
			} catch (Exception ex) {
				// TODO: handle exception
			}
			try {
				inFromServer.close();
			} catch (Exception ex) {
				// TODO: handle exception
			}
			try {
				toServer.close();
			} catch (Exception ex) {
				// TODO: handle exception
			}
			return false;
		}

		if (type == 1)
			status = sendFragment();
		else if (type == 2)
			status = recvFragment();
		else if (type == 3)
			status = deleteFragment();

		try {
			outToServer.close();
		} catch (Exception ex) {
			// TODO: handle exception
		}
		try {
			inFromServer.close();
		} catch (Exception ex) {
			// TODO: handle exception
		}
		try {
			toServer.close();
		} catch (Exception ex) {
			// TODO: handle exception
		}

		return status;
	}

	public static void init(File f, String ip, int port) {
		fragmentFolder = f;
		serverIP = ip;
		serverPort = port;
	}

	@SuppressWarnings("deprecation")
	private boolean sendFragment() {
		boolean status;
		String sentense;

		try {
			File f = new File(fragmentFolder + Integer.toString(fragmentId));
			if (!f.exists()) {
				errorHandler(1);
				return false;
			}

			outToServer.writeBytes(String.format("%d %d %d\n", type, requestId, fragmentId));
			outToServer.flush();

			sentense = inFromServer.readLine();
			if (!sentense.equals("received!"))
				return false;

			status = FileTransporter.sendFile(f, inFromServer, outToServer);

			if (status) {
				sentense = inFromServer.readLine();
				if (!sentense.equals("received!"))
					status = false;
			}

			return status;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean recvFragment() {
		try {
			File f = new File(fragmentFolder + Integer.toString(fragmentId));
			if (f.exists()) {
				f.delete();
			}

			outToServer.writeBytes(String.format("%d %d %d\n", type, requestId, fragmentId));
			outToServer.flush();

			if (FileTransporter.recvFile(f, inFromServer, outToServer)) {
				outToServer.writeBytes("received!\n");
				outToServer.flush();
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean deleteFragment() {
		try {
			File f = new File(fragmentFolder + Integer.toString(fragmentId));
			if (f.exists()) {
				f.delete();
			}

			outToServer.writeBytes("received!\n");
			outToServer.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// handle fatal errors, finish it later
	private void errorHandler(int type) {
		// type = 1 can not find fragment
		return;
	}

}
