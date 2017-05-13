package dataConnect;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeoutException;

class ClientThread extends Thread {
	Socket clientsocket;	
	BufferedReader inFromClient = null;
	DataOutputStream outToClient = null;
	String[] command;

	public ClientThread(Socket socket) {
		this.clientsocket = socket;
	}

	public void run() {
		
		boolean status=false;

		System.out.println("start!");
		try {
			clientsocket.setKeepAlive(true);
			clientsocket.setSoTimeout(5000);
			inFromClient = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
			outToClient = new DataOutputStream(clientsocket.getOutputStream());

			String sentence = inFromClient.readLine();// 对得到的流进行处理
			System.out.println("D-RECV: " + sentence);
			command=sentence.split(" ");
				if (command[0].equals("1"))
					status=recvRequiredFragment();
				else if (command[0].equals("2"))
					status=sendFragment();
				else if (command[0].equals("3"))
					status=deleteFragment();
				else if (command[0].equals("4"))
					status=registerFile();
				else if (command[0].equals("5"))
					status=recvFileFragment();
				else {
					outToClient.writeBytes("ERROR!\n");
					outToClient.flush();
				}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			inFromClient.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			clientsocket.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			outToClient.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if (status)
			System.out.println("D-client thread ended (finished)");
		else
			System.out.println("D-client thread ended (aborted)");
	}
	
	private boolean recvRequiredFragment() throws Exception{
		return false;
	}
	
	private boolean sendFragment() throws Exception{
		return false;
	}
	
	private boolean deleteFragment() throws Exception{

		int id = Integer.parseInt(command[1]);
		int fid = Integer.parseInt(command[2]);

		database.Query query = new database.Query();
		database.RequestItem request;
		
		request = query.queryRequestById(id);

		if (request == null) {
			outToClient.writeBytes("ERROR!\n");
			outToClient.flush();
			query.closeConnection();
			return false;
		} else if (request.getDeviceId()!=fid){
			outToClient.writeBytes("ERROR!\n");
			outToClient.flush();
			query.closeConnection();
			return false;
		} else {		
			outToClient.writeBytes("received!\n");
			outToClient.flush();
			query.deleteRequest(request.getId());
			query.closeConnection();
			return true;
		}		
		
	}
	
	private boolean registerFile() throws Exception{
		
		int id = Integer.parseInt(command[1]);
		int noa = Integer.parseInt(command[5]);
		boolean isf = Boolean.parseBoolean(command[6]);

		database.Query query = new database.Query();
		//database.DeviceItem deviceItem = query.queryDevice(id);
		
		Date date = new Date();
		@SuppressWarnings("deprecation")
		String time = String.format("%d%02d%02d", date.getYear() + 1900, date.getMonth() + 1, date.getDate());
		database.FileItem fileitem = new database.FileItem(command[2], command[3], command[4], time, noa, isf);
		
		int fid = query.addFile(fileitem);

		outToClient.writeBytes(String.format("FileId: %d\n", fid));
		outToClient.flush();
		
		query.closeConnection();
		return true;
		
	}
	
	private boolean recvFileFragment() throws Exception{
		return false;
	}

	public static int confirm(int id, int num) {

		database.Query query = new database.Query();

		// 根据上面的方法,获得所有的在线主机
		database.DeviceItem[] di = query.queryOnlineDevice();

		if (di == null) {
			return 1;
		}

		Random rd = new Random();

		// 根据碎片数量确定要发送碎片数量的主机
		int size = di.length;
		if (num <= size) {
			// 如果碎片数量小于在线主机数e
			int t = rd.nextInt(size);
			for (int i = 0; i < num; i++) {
				try {
					// 将碎片分配到该主机
					query.addRequest(new database.RequestItem(2, id * 100 + i, di[(i + t) % size].getId()));
					query.addFragment(id * 100 + i, Integer.toString(di[(i + t) % size].getId()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			// 如果碎片数量大于在线主机数
			int[] n = new int[size];// 新建一个数组存放每个主机要发送的碎片数量
			// 首先所有要发送的碎片数量为num/di.length
			for (int i = 0; i < size; i++) {
				n[i] = num / size;
			}
			int m = num % size;// 剩余的碎片数量

			int t = rd.nextInt(size);
			for (int i = 0; i < m; i++) {
				n[t % size]++;
				t++;
			}

			t = 0;
			for (int i = 0; i < size; i++) {
				try {
					for (int j = 0; j < n[i]; j++) {
						// 碎片发送函数
						query.addRequest(new database.RequestItem(2, id * 100 + t, di[i].getId()));
						query.addFragment(id * 100 + t, Integer.toString(di[i].getId()));
						t++;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return 0;
	}

}