package jhc.io.cn;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleServer2 {
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(6666);
		//��ȡ�û���Ϣ���û�����Socket��
		Map<String, Socket> map = new HashMap<String, Socket>();
		//�̳߳أ��̴߳�СΪ20
		ExecutorService executorService =Executors.newFixedThreadPool(20);
		System.out.println("�ȴ��ͻ�������...");
			try {
				for(int i = 0;i < 20;i ++) {
				Socket socket = serverSocket.accept();
				System.out.println("���µ��û����ӣ�"+socket.getInetAddress()+socket.getPort());
				
				executorService.execute(new ExcuteClientServer1(socket,map));
				}
				executorService.shutdown();
				serverSocket.close();
			} catch (Exception e) {
		}
	}
}
