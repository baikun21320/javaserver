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


//服务器端
/**
 * Map<String,Socket>
 * 查看用户see
 * 用户注册：username:yyy
 * 退出群聊：拜拜
 * 群聊：G:hello
 * 私聊：P:yyy-hhh
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
			//拿到客户端输入流，读取用户信息
			Scanner scanner = new Scanner(client.getInputStream());
			String string = null;
			while(true){
				if(scanner.hasNext()) {
					string = scanner.nextLine();
					Pattern pattern = Pattern.compile("\r\n|\r|\n");
					Matcher matcher = pattern.matcher(string);
					string = matcher.replaceAll("");
					System.out.println("测试string:"+string);
					//查看在线用户
					if(string.startsWith("see")) {
						getCountUser(client);
						continue;
					}
					//用户注册
					if(string.startsWith("user")) {
						//获取用户名
						String useName = string.split("\\:")[1];
						userRegist(useName, client);
						continue;
					}
					//群聊
					else if(string.startsWith("G")) {
						String message = string.split("\\:")[1];
						gropChat(message);
						continue;
					}
					//私聊
					else if(string.startsWith("P")) {
						String temp = string.split("\\:")[1];
						//取得用户名
						String useName = temp.split("\\-")[0];
						//取得消息内容
						String message = temp.split("\\-")[1];
						privateChat(useName, message);
						continue;
					}
					//用户退出
					else if(string.contains("拜拜")) {
						//先根据Socket知道用户名
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
	//查看在线用户
	public void getCountUser(Socket client) {
		if(!isLogin()) {
			return;
		}
//		System.out.println(getUseName(client));
			//根据对应的useName找到对应的Socket
			 try {
				    Socket privateSocket = client;
					PrintStream printStream = new PrintStream(privateSocket.getOutputStream());
					printStream.println("当前在线:");
					for (String key : clientMap.keySet()) {
						printStream.println(key);
			        }
				} catch (IOException e) {
				  e.printStackTrace();
				  }	
	}
	//判断是否加入群聊
	public Boolean isLogin() {
		System.out.println(client);
//		System.out.println(this.client);
		if(!this.isLog) {
			try {
				Socket privateSocket =client;
					PrintStream printStream = new PrintStream(privateSocket.getOutputStream());
					printStream.println("请先加入群聊");
				} catch (IOException e) {
				  e.printStackTrace();
				  }
			return false;
		}
		return true;
	}
	//获取key值（即由端口号找到用户名）
	public String getUseName(Socket socket) {
		String useName = null;
		for(String getKey : clientMap.keySet()) {
			if(clientMap.get(getKey).equals(socket)) {
				useName = getKey;
			}
		}
		return useName;
	}
	//注册实现
	public void userRegist(String useName,Socket client) {
		Socket privateSocket = client;
		if(!UserDao.findId(client.getPort())) {
			if(!UserDao.find(useName)) {
				int i = UserDao.insert(new User(client.getPort(), useName));
				if(i > 0) {
					isLog = true;
					clientMap.put(useName, client);
					sgropChat("有新成员"+useName+"加入群聊");
					sgropChat("当前在线数为："+ (clientMap.size()) +"人");
					
				}else {
					try {
						PrintStream printStream = new PrintStream(privateSocket.getOutputStream());
						printStream.println("加入失败");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}else {
				
				try {
					PrintStream printStream = new PrintStream(privateSocket.getOutputStream());
					printStream.println("该用户名:"+ useName +"已被使用");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				System.out.println("该用户名:"+ useName +"已被使用");
			}
		}else {
			
		}
		
//		System.out.println("用户姓名为：" + useName);
	}
	//退出群聊
	public void quit(String userName) {
		if(!isLogin()) {
			return;
		}
		if(UserDao.delete(userName) > 0) {
			sgropChat(userName+"退出群聊");
		}
	}
	//群聊实现
	public void gropChat(String message) {
		if(!isLogin()) {
			return;
		}
		Iterator<Entry<String, Socket>> iterable = clientMap.entrySet().iterator();
		for(Map.Entry<String, Socket> stringSocketEntry:clientMap.entrySet()) {
			try {
				Socket socket = stringSocketEntry.getValue();
				PrintStream printStream = new PrintStream(socket.getOutputStream(),true);
	            printStream.println("群聊 "+getUseName(client)+":"+message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	//群聊实现
		public void sgropChat(String message) {
			if(!isLogin()) {
				return;
			}
			Iterator<Entry<String, Socket>> iterable = clientMap.entrySet().iterator();
			for(Map.Entry<String, Socket> stringSocketEntry:clientMap.entrySet()) {
				try {
					Socket socket = stringSocketEntry.getValue();
					PrintStream printStream = new PrintStream(socket.getOutputStream(),true);
		            printStream.println("系统消息： "+":"+message);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	//私聊实现
	public void privateChat(String useName,String message) {
		if(!isLogin()) {
			return;
		}
		//根据对应的useName找到对应的Socket
		Socket privateSocket = clientMap.get(useName);
		try {
			PrintStream printStream = new PrintStream(privateSocket.getOutputStream());
			printStream.println("私聊 "+getUseName(client)+":"+message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
 
//public class SingleServer2 {
//	public static void main(String[] args) throws IOException {
//		ServerSocket serverSocket = new ServerSocket(6666);
//		//存取用户信息（用户名和Socket）
//		Map<String, Socket> map = new HashMap<String, Socket>();
//		//线程池，线程大小为20
//		ExecutorService executorService =Executors.newFixedThreadPool(20);
//		System.out.println("等待客户连接中...");
//			try {
//				for(int i = 0;i < 20;i ++) {
//				Socket socket = serverSocket.accept();
//				System.out.println("有新的用户连接："+socket.getInetAddress()+socket.getPort());
//				executorService.execute(new ExcuteClientServer1(socket,map));
//				}
//				executorService.shutdown();
//				serverSocket.close();
//			} catch (Exception e) {
//		}
//	}
//}