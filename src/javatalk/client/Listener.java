package javatalk.client;
/**
 * @author 박수빈, 정영우, 최대진
 */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import javatalk.client.frame.AppFrame;
import javatalk.client.frame.ChatPanel;
import javatalk.client.util.FileSaveLoadUtil;
import javatalk.client.util.UserList;
import javatalk.model.FilePath;
import javatalk.model.Message;
import javatalk.model.User;

/*
 * 최초에 서버에 접속 한 이후 서버에서 전송되는 메세지를 수신
 * login() - 서버 접속
 * islogined() - 접속 됐는지 확인
 * receive() - 서버에서 전송되는 메세지 수신
 */
public class Listener implements Runnable {
	
	private static final String HOST = "14.42.124.87"; // HOST IP
	private static final int PORT = 1550; // 연결 PORT

	// --------------------------------------------- 멤버필드 선언
	Socket socket; 
	ObjectOutputStream objectOutputStream;
	ObjectInputStream objectInputStream;
	Sender sender;

	AppFrame frame;
	ChatPanel chatPanel;
	String name;
	Message message;
	int cnt = 0;
	
	ChatPanel chat = new ChatPanel(); //
	// ---------------------------------------------
	
	public Listener(AppFrame frame) {
		this.frame = frame;
	}

	@Override
	public void run() {
		try {
			socket = new Socket(HOST, PORT); // 소켓 생성
			objectOutputStream = new ObjectOutputStream(socket.getOutputStream()); // output 연결
			objectInputStream = new ObjectInputStream(socket.getInputStream()); // input 연결
			name = frame.getLoginPane().getTxtName(); // Login할때 입력한 이름 가져오기
			chatPanel = frame.getChatPane();
			login(); // 로그인
			
			if (isLogined()) { // 로그인 되었다면
				receive(); // 메시지 출력 메서드 실행
			}
			// --------------------------------------------- 예외 catch
		} catch (UnknownHostException e) {
			System.out.println("서버에 접속할 수 없습니다. : " + e.getMessage());
		} catch (IOException e) {
			System.out.println("소켓 닫힘(추방 or 네트워크 오류) : " + e.getMessage());
//			networkDisconnection();
		} catch (ClassNotFoundException e) {
			System.out.println("Class Not Found Exception : " + e.getMessage());
			// ---------------------------------------------
			
		} finally { // 무조건 실행할 코드
			closeConnection();
		}
	}

	private void login() throws IOException {
		message = new Message(); // Message 클래스 객체 생성
		message.setName(name); // 이름 설정
		message.setMessage("" + frame.getLoginPane().getProfileNum()); // 메시지 설정
		objectOutputStream.writeObject(message); // 메시지 output
		objectOutputStream.reset(); // 리셋
	}

	private boolean isLogined() throws ClassNotFoundException, IOException {
		message = (Message) objectInputStream.readObject(); // 메시지 읽어옴
		switch (message.getType()) { // 메시지를 타입으로 구분하여 switch
		case DUPLICATE: // ID 중복 로그인일 경우
			duplicateName(); // 중복 로그인일 경우의 메서드 실행시킴
			return false; // false 반환.
		case WELCOME: // 문제가 없을경우 
			UserList.setList(message.getUserList()); // Client UserList에 set
		default: // 기본값
			frame.changeToChat(); // 로그인에 문제가 없으므로 GUI 채팅으로 변경
			sender = new Sender(objectOutputStream, name); // Sender 클래스 객체 생성
			printMessage(message.getMessage()); // 메시지 출력
			return true; // true 반환.
		}
	}

	private synchronized void receive() throws IOException, ClassNotFoundException {
		// 서버로부터 수신
		while (socket.isConnected()) { // 소켓 연결이 true 일 경우 반복
			message = (Message) objectInputStream.readObject(); // 서버로부터 수신한 메시지 읽어서 형변환
			switch (message.getType()) { // 메시지 타입에 따른 switch
			case DUPLICATE: // ID 중복
				duplicateName();
				return;
			case IMAGE: // 이미지를 받을 경우
				message.setMessage((saveImage()));
				printMessage();
				break;
			case WELCOME:
			case EXIT: // 유저가 나갔을 경우
				UserList.setList(message.getUserList());
				printMessage(message.getMessage());
				break;
			case KICK: // 강퇴 명령을 받은 경우
				if(UserList.isDuplicated(message.getKickTarget())) {
					printMessage(message.getKickTarget() + " 님이 추방됨"); // 강퇴 대상이 추방되었음을 출력
				}
				
				if (name.equals(message.getKickTarget())) { // 강퇴 대상과 나의 ID가 같다면
					kicked(); // 강퇴 메서드 실행
				}
				// 이하로는 강퇴 대상이 아닐 경우
				// 리스트 새로고침 기능, 강퇴 대상이 방에서 나가졌으므로 리스트 갱신
				ArrayList<User> kicked = new ArrayList<>(); // ArrayList 생성
				kicked.add(UserList.getUser(message.getKickTarget())); // 생성된 ArrayList에 강퇴당한 유저 add
				UserList.getList().remove(kicked.get(0)); // Client UserList에서 해당 유저 지움
				chat.repaint();
				break;

			default: // 기본값
				printMessage();
				break;
			}
		}
	}

	private void kicked() { // 강퇴당한 유저만 실행하는 메서드
		frame.changeToLogin(); // 채팅방에서 로그인창으로 이동
		JOptionPane.showMessageDialog(null, "방장에 의해 추방되었습니다."); // 강퇴당했음을 통보하는 메시지창
		closeConnection(); // 연결 끊기
	}

	private void duplicateName() {
		frame.changeToError("이미 존재하는 이름입니다.\n다른 이름을 선택해 주세요.");
	}

	private void networkDisconnection() {
		JOptionPane.showMessageDialog(null, "연결 중복 감지됨, 한번 더 감지되면 종료될 수 있음");
		if (cnt < 3) {
			cnt++;
			run();
			frame.changeToChat();
		} else {
			JOptionPane.showMessageDialog(null, "지속적인 중복(3회 이상)으로 채팅을 종료합니다.");
			System.exit(0);
		}
	}

	private synchronized void printMessage(String adminMsg) { // 전체 유저가 받는 공지 메시지
		// 입장, 퇴장, 강퇴 등
		chatPanel.addMessage(adminMsg);
	}

	private synchronized void printMessage() { // 채팅창에 본인의 메시지 출력
		chatPanel.addMessage(isMine(), message);
	}

	private boolean isMine() {
		return name.equals(message.getName());
	}

	private String saveImage() {
		return FileSaveLoadUtil.fileSave(message.getImageExtention(), 
				FilePath.DOWNLOADFILEPATH.toString(),
				message.getImage());
	}

	private void closeConnection() { // ois oos socket close
		if (objectInputStream != null) {
			try {
				objectInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (objectOutputStream != null) {
			try {
				objectOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
