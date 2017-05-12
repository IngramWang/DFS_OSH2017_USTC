package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeoutException;

class ClientThread extends Thread {
	Socket clientsocket;
	String scentence;
	BufferedReader inFromClient = null;
	DataOutputStream outToClient = null;

	public ClientThread(Socket socket) {
		this.clientsocket = socket;
	}

	public void run() {

		System.out.println("start!");
		try {
			clientsocket.setKeepAlive(true);
			clientsocket.setSoTimeout(60000);
			inFromClient = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
			outToClient = new DataOutputStream(clientsocket.getOutputStream());

			while (true) {
					String sentence = inFromClient.readLine();// 对得到的流进行处理
					if (readscentence(sentence) == 0)
						break;
					System.out.println("RECV: " + sentence);
			}	
			
		} catch (TimeoutException e) {
			System.out.println("times out");
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
		
		System.out.println("client thread ended");
	}

	private int readscentence(String sentence) throws Exception {
		System.out.println(sentence.charAt(0));

		if (sentence.charAt(0) == '1') {
			String s[], ip;
			s = sentence.split(" ");

			int id = Integer.parseInt(s[1]);
			int port = clientsocket.getPort();
			int rs = Integer.parseInt(s[2]);
			ip = clientsocket.getInetAddress().getHostAddress();

			database.Query query = new database.Query();
			database.DeviceItem deviceitem;
			deviceitem = query.queryDevice(id);
			if (deviceitem == null) {
				// 不允许通过报文新建client
			} else {
				deviceitem.setIp(ip);
				deviceitem.setPort(port);
				deviceitem.setIsOnline(true);
				deviceitem.setRs(rs);
				query.alterDevice(deviceitem);
			}

			outToClient.writeBytes(String.format("received with %d unread request!\n", query.queryRequestNumbers(id)));
			outToClient.flush();

			query.closeConnection();
			return 1;
		} /*else if (sentence.charAt(0) == '2') {
			String[] strings;
			strings = sentence.split(" ");
			int id = Integer.parseInt(strings[1]);
			int noa = Integer.parseInt(strings[5]);
			boolean isf = Boolean.parseBoolean(strings[6]);

			database.Query query = new database.Query();
			//database.DeviceItem deviceItem = query.queryDevice(id);
			Date date = new Date();
			@SuppressWarnings("deprecation")
			String time = String.format("%d%02d%02d", date.getYear() + 1900, date.getMonth() + 1, date.getDate());
			database.FileItem fileitem = new database.FileItem(strings[2], strings[3], strings[4], time, noa, isf);
			int fid = query.addFile(fileitem);

			outToClient.writeBytes(String.format("FileId: %d\n", fid));
			outToClient.flush();
			
			query.closeConnection();
			return 1;
		} */
		else if (sentence.charAt(0) == '2') {
			String s[];
			s = sentence.split(" ");

			int id = Integer.parseInt(s[1]);

			database.Query query = new database.Query();
			database.RequestItem request;
			request = query.queryFirstRequest(id);
			query.closeConnection();

			if (request == null) {
				outToClient.writeBytes("ERROR!\n");
				outToClient.flush();
				return 0;
			} else {
				outToClient.writeBytes(
						String.format("%d %d %d\n", request.getId(), request.getFragmentId(), request.getType()));
				outToClient.flush();
			}
			return 1;
		} /* move to data connect
		else if (sentence.charAt(0) == '4') {
			String s[];
			s = sentence.split(" ");

			int id = Integer.parseInt(s[1]);
			int fid = Integer.parseInt(s[2]);

			database.Query query = new database.Query();
			database.RequestItem request;
			request = query.queryRequestById(id);

			if (request == null) {
				outToClient.writeBytes("ERROR!\n");
				outToClient.flush();
				query.closeConnection();
				return 0;
			} else if (request.getDeviceId()!=fid){
				outToClient.writeBytes("ERROR!\n");
				outToClient.flush();
				query.closeConnection();
				return 0;
			} else {		
				outToClient.writeBytes("received!\n");
				outToClient.flush();
				query.deleteRequest(request.getId());
				query.closeConnection();
				return 1;
			}		
		}*/
		return 0;
	}
/*
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
*/
}