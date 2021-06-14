package jhc.io.cn;


import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//��������
/**
 * Map<String,Socket>
 * �鿴�û�see
 * �û�ע�᣺username:yyy
 * �˳�Ⱥ�ģ��ݰ�
 * Ⱥ�ģ�G:hello
 * ˽�ģ�P:yyy-hhh
*/
 
class  ExcuteClientServer1 implements Runnable{
	private Socket client;
	private Map<String,Socket> clientMap;
	private Boolean isLog =false;
	public ExcuteClientServer1(Socket client, Map<String, Socket> clientMap) {
		super();
		this.client = client;
		this.clientMap = clientMap;
	}
 
	public void run() {
		try {
			//�õ��ͻ�������������ȡ�û���Ϣ
			Scanner scanner = new Scanner(client.getInputStream());
			String string = null;
			while(true){
				if(scanner.hasNext()) {
					string = scanner.nextLine();
					Pattern pattern = Pattern.compile("\r\n|\r|\n");
					Matcher matcher = pattern.matcher(string);
					string = matcher.replaceAll("");
					System.out.println("����string:"+string);
					//�鿴�����û�
					if(string.startsWith("see")) {
						getCountUser(client);
						continue;
					}
					//�û�ע��
					if(string.startsWith("user")) {
						//��ȡ�û���
						String useName = string.split("\\:")[1];
						userRegist(useName, client);
						continue;
					}
					//Ⱥ��
					else if(string.startsWith("G")) {
						String message = string.split("\\:")[1];
						gropChat(message);
						continue;
					}
					//˽��
					else if(string.startsWith("P")) {
						String temp = string.split("\\:")[1];
						//ȡ���û���
						String useName = temp.split("\\-")[0];
						//ȡ����Ϣ����
						String message = temp.split("\\-")[1];
						privateChat(useName, message);
						continue;
					}
					//�û��˳�
					else if(string.contains("�ݰ�")) {
						//�ȸ���Socket֪���û���
						String useName = getUseName(client);
						quit(useName);
						clientMap.remove(useName);
						continue;
					}
				} 
			}
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//�鿴�����û�
	public void getCountUser(Socket client) {
		if(!isLogin()) {
			return;
		}
//		System.out.println(getUseName(client));
			//���ݶ�Ӧ��useName�ҵ���Ӧ��Socket
			 try {
				    Socket privateSocket = client;
					PrintStream printStream = new PrintStream(privateSocket.getOutputStream());
					printStream.println("��ǰ����:");
					for (String key : clientMap.keySet()) {
						printStream.println(key);
			        }
				} catch (IOException e) {
				  e.printStackTrace();
				  }	
	}
	//�ж��Ƿ����Ⱥ��
	public Boolean isLogin() {
		System.out.println(client);
//		System.out.println(this.client);
		if(!this.isLog) {
			try {
				Socket privateSocket =client;
					PrintStream printStream = new PrintStream(privateSocket.getOutputStream());
					printStream.println("���ȼ���Ⱥ��");
				} catch (IOException e) {
				  e.printStackTrace();
				  }
			return false;
		}
		return true;
	}
	//��ȡkeyֵ�����ɶ˿ں��ҵ��û�����
	public String getUseName(Socket socket) {
		String useName = null;
		for(String getKey : clientMap.keySet()) {
			if(clientMap.get(getKey).equals(socket)) {
				useName = getKey;
			}
		}
		return useName;
	}
	//ע��ʵ��
	public void userRegist(String useName,Socket client) {
		Socket privateSocket = client;
		if(!UserDao.findId(client.getPort())) {
			if(!UserDao.find(useName)) {
				int i = UserDao.insert(new User(client.getPort(), useName));
				if(i > 0) {
					isLog = true;
					clientMap.put(useName, client);
					sgropChat("���³�Ա"+useName+"����Ⱥ��");
					sgropChat("��ǰ������Ϊ��"+ (clientMap.size()) +"��");
					
				}else {
					try {
						PrintStream printStream = new PrintStream(privateSocket.getOutputStream());
						printStream.println("����ʧ��");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}else {
				
				try {
					PrintStream printStream = new PrintStream(privateSocket.getOutputStream());
					printStream.println("���û���:"+ useName +"�ѱ�ʹ��");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				System.out.println("���û���:"+ useName +"�ѱ�ʹ��");
			}
		}else {
			
		}
		
//		System.out.println("�û�����Ϊ��" + useName);
	}
	//�˳�Ⱥ��
	public void quit(String userName) {
		if(!isLogin()) {
			return;
		}
		if(UserDao.delete(userName) > 0) {
			sgropChat(userName+"�˳�Ⱥ��");
		}
	}
	//Ⱥ��ʵ��
	public void gropChat(String message) {
		if(!isLogin()) {
			return;
		}
		Iterator<Entry<String, Socket>> iterable = clientMap.entrySet().iterator();
		for(Map.Entry<String, Socket> stringSocketEntry:clientMap.entrySet()) {
			try {
				Socket socket = stringSocketEntry.getValue();
				PrintStream printStream = new PrintStream(socket.getOutputStream(),true);
	            printStream.println("Ⱥ�� "+getUseName(client)+":"+message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	//Ⱥ��ʵ��
		public void sgropChat(String message) {
			if(!isLogin()) {
				return;
			}
			Iterator<Entry<String, Socket>> iterable = clientMap.entrySet().iterator();
			for(Map.Entry<String, Socket> stringSocketEntry:clientMap.entrySet()) {
				try {
					Socket socket = stringSocketEntry.getValue();
					PrintStream printStream = new PrintStream(socket.getOutputStream(),true);
		            printStream.println("ϵͳ��Ϣ�� "+":"+message);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	//˽��ʵ��
	public void privateChat(String useName,String message) {
		if(!isLogin()) {
			return;
		}
		//���ݶ�Ӧ��useName�ҵ���Ӧ��Socket
		Socket privateSocket = clientMap.get(useName);
		try {
			PrintStream printStream = new PrintStream(privateSocket.getOutputStream());
			printStream.println("˽�� "+getUseName(client)+":"+message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
 
//public class SingleServer2 {
//	public static void main(String[] args) throws IOException {
//		ServerSocket serverSocket = new ServerSocket(6666);
//		//��ȡ�û���Ϣ���û�����Socket��
//		Map<String, Socket> map = new HashMap<String, Socket>();
//		//�̳߳أ��̴߳�СΪ20
//		ExecutorService executorService =Executors.newFixedThreadPool(20);
//		System.out.println("�ȴ��ͻ�������...");
//			try {
//				for(int i = 0;i < 20;i ++) {
//				Socket socket = serverSocket.accept();
//				System.out.println("���µ��û����ӣ�"+socket.getInetAddress()+socket.getPort());
//				executorService.execute(new ExcuteClientServer1(socket,map));
//				}
//				executorService.shutdown();
//				serverSocket.close();
//			} catch (Exception e) {
//		}
//	}
//}