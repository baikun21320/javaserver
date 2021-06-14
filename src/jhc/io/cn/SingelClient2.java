package jhc.io.cn;

import java.net.Socket;

public class SingelClient2 {
	public static void main(String[] args) {
		String serverName = "127.0.0.1";
		int port = 6666;
		Socket client;
		try {
		    client = new Socket(serverName,port);
			Thread ouThread = new Thread(new OutToServer2(client));
			Thread inThread = new Thread(new ReadFromServer2(client));
			ouThread.start();
			inThread.start();
		} catch (Exception e) {
 
			e.printStackTrace();
		} 
	}
}
