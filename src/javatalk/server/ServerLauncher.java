package javatalk.server;
/**
 * @author 최대진, 박수빈
 */
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JTextArea;

public class ServerLauncher {

	private static final int PORT = 1550;
	private static ServerFrame serverWindow;
	
	private static ServerSocket listener = null;
	private static Socket socket = null;
	private static JTextArea logText;
	
	public static ArrayList <Handler> chatlist = new ArrayList <Handler>(); // 스레드 리스트 생성
	
	public static JTextArea getlogText() { // 다른 클래스에서도 logText에 append할 수 있도록 get
		return logText;
	}
	
	public static void main(String[] args) {
		
		try {
			serverWindow = new ServerFrame(); // 서버 로그 GUI 생성
			logText = serverWindow.getLogText();
			listener = new ServerSocket(PORT); // 서버 소켓 생성
			
			while (true) { // 접속시도를 언제든지 받도록 while 사용
				socket = listener.accept();
				Handler chat = new Handler(socket);
				chatlist.add(chat); // 생성된 연결을 리스트에 추가
				System.out.println(chatlist.toString());
				chat.start();
				logText.append("\n╔═══━━━───── 🌸 Welcome”♡ᵎˀˀ ↷ ⋯ ──────━━━═══╗\n");
				logText.append("\n[SERVER]:\n채팅 접속을 환영합니다\n");
			}			
		} catch (Exception e) {
			logText.append("PORT " + PORT + " failed");
			e.printStackTrace();
		}
	}
}
