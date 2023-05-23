package javatalk.server;
/**
 * @author ALL
 */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

import javatalk.client.frame.ChatPanel;
import javatalk.model.Message;
import javatalk.model.TypeOfMessage;
import javatalk.model.User;
import javatalk.server.exception.DuplicateUsernameException;
import javatalk.server.util.NaverSearching;
import javatalk.server.util.UserList;

/*
 * 클라이언트에서 넘어온 message를 
 * 현재 접속되어 있는 클라이언트 들에게 송신
 */
public class Handler extends Thread {

	public static HashMap<String, Handler> hash = new HashMap<String, Handler>();
	// UserId를 key, Thread를 value로 갖는 HashMap 생성

	// ------------- 필드 정의
	Socket sockt;
	ObjectInputStream objectInputStream;
	ObjectOutputStream objectOutputStream;
	String name, hostName;
	Message message;
	User user;
	NaverSearching searching = new NaverSearching();
	ChatPanel chat = new ChatPanel();
	// -------------

	public Handler(Socket socket) {
		this.sockt = socket;
	}

	@Override
	public void run() {
		try {
			addUser(); // 유저 추가 메서드
			setName(); // 쓰레드에 이름 설정
			receiveMessage(); // 메시지 송수신
			removeUser(); // 유저 제거
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DuplicateUsernameException e) {
			name = null;
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			closeConnection(); // 연결 닫기
		}
	}

	private void addUser() throws IOException, DuplicateUsernameException, ClassNotFoundException {
		objectInputStream = new ObjectInputStream(sockt.getInputStream());
		objectOutputStream = new ObjectOutputStream(sockt.getOutputStream());
		Message userName = (Message) objectInputStream.readObject();
		user = new User(userName.getName(), Integer.parseInt(userName.getMessage()), objectOutputStream);
		if (UserList.addList(user)) {
			name = userName.getName();
			ServerLauncher.getlogText().append("\n IP주소 : " + sockt.getLocalAddress() + "\nUser : " + name + " 님이 접속했습니다.\n");
			sendWelcomeMessage();
		} else {
			sendDuplicateError();
			throw new DuplicateUsernameException("already connected");
		}
	}

	private void setName() {
		this.setName(name);
		hash.put(name, this); // hash에 id, thread put.
		ServerLauncher.getlogText().append("\n"+ this.getName() +"님의 스레드 등록이 완료되었습니다.");
		ServerLauncher.getlogText().append("\n╚═══━━━━────────────────────────────────━━━━══╝\n");

	}

	private void sendDuplicateError() throws IOException {
		setAdminMessage(TypeOfMessage.DUPLICATE, null);
		sendMessageToOne(objectOutputStream);
	}

	private void sendWelcomeMessage() throws IOException {
		String adminMessage = name + " 님이 입장하셨습니다.";
		setAdminMessage(TypeOfMessage.WELCOME, adminMessage);
		sendMessage();
	}

	private void setAdminMessage(TypeOfMessage type, String adminMessage) {
		String adminName = "운영자";
		message = new Message();
		message.setName(adminName);
		message.setType(type);
		message.setMessage(adminMessage);
		message.setUserList(UserList.getList());
	}

	private void setSearchMessage(String userMessage) {
		message.setMessage(userMessage);
	}
	
	/*
	 * 메세지의 타입에 따라서 귓속말이라면 대상에게만 메세지를 전송하고 강퇴 기능은 방장 유무 확인하여 강퇴 기능 작동하도록 정의
	 * 또한 방장이 채팅창에서 나갔을 경우 그 다음 index 번호의 유저에게 방장권한을 위임하는 기능을 정의
	 */

	private void receiveMessage() throws ClassNotFoundException {
		try {

			while (sockt.isConnected()) {
				message = (Message) objectInputStream.readObject();

				switch (message.getType()) {

				case IMAGE:
					sendMessage();
					break;

				case WHISPER:
					sendMessageToOne(objectOutputStream);
					sendMessageToOne(UserList.getUser(message.getWhisperTarget()).getObjectOutputStream());
					break;

				case SEARCH:
					String keyword = message.getMessage().substring(1);
					String searchMessage = getSearchResult(keyword);
					setSearchMessage(searchMessage);
					sendMessage();
					break;

				case KICK:
					if (ServerLauncher.chatlist.indexOf(this) != 0) { 
						// 방장을 arrayList의 0번 index로 배정했기 때문에 index 번호를 확인하여 방장여부 판단
						ServerLauncher.getlogText().append("\n[SERVER] : \n <" + name + "> 님은 강퇴권한이 없습니다.\n");
						break;
					} else {
						String key = message.getKickTarget(); // 추방할 대상 이름
						Handler thread = hash.get(key);
						
						// 방장 인식 기능
						if (key.equals(ServerLauncher.chatlist.get(0).getName())) {
							ServerLauncher.getlogText().append("\n[SERVER] : \n 자기 자신은 강퇴할 수 없습니다.\n");
							break;
						}
						// 유저 목록에 존재하는 대상이라면 강퇴
						if(UserList.isDuplicated(message.getKickTarget())) {
							ServerLauncher.getlogText().append("\n[SERVER] : \n" + key + " 님이 강퇴되었습니다. \n");
							disconnect(thread, key);
						} else {
							ServerLauncher.getlogText().append("\n[SERVER] : \n" + message.getKickTarget() + " 은 접속유저 목록에 없어 강퇴할 수 없습니다. \n");
						}
						
					}

				default:
					sendMessage();
					break;
				}
			}
		} catch (Exception e) {
			// 종료 시 예외를 발생시키게 하여 예외를 감지하여 
			// 방장이 나갔는지 검사하여 방장이 나갔다면 서버로그에 메세지를 전송
			try {
				if (ServerLauncher.chatlist.indexOf(this) == 0) {
					ServerLauncher.getlogText().append("\n╔═══━━━─────────────── • ───────────────━━━═══╗\n");
					ServerLauncher.getlogText().append("[SERVER] : \n방장이 나갔으므로 리스트에서 제거합니다.\n");
					ServerLauncher.chatlist.remove(0);
					ServerLauncher.getlogText().append("[SERVER] : \n방장권한을 <" + ServerLauncher.chatlist.get(0).getName() + "> 님으로 위임합니다.\n");
					ServerLauncher.getlogText().append("╚═══━━━─────────────── • ───────────────━━━═══╝\n");
				}
			} catch (Exception e2) {
				ServerLauncher.getlogText().append("[SERVER] : \n방에 아무도 없어 방장을 위임할 수 없습니다.\n");
				ServerLauncher.getlogText().append("╚═══━━━─────────────── • ───────────────━━━═══╝\n");
			}
			ServerLauncher.getlogText().append("\n[SERVER] : \n IP주소 : " + sockt.getLocalAddress() + "\n Username : " + name + " 님이 종료하였습니다.");
			ServerLauncher.getlogText().append("\n═══━━━─────🌸 Good Bye ”♡ᵎˀˀ ⋯ ──────━━━═══\n");
			
		}
	}

	@SuppressWarnings("deprecation")
	private void disconnect(Handler thread, String id) {
		try {
			thread.stop();
		} catch (Exception e) {
			ServerLauncher.getlogText().append("강퇴 중 예외 발생 : " + e.getMessage() + "\n");
			e.printStackTrace();
		}
		hash.remove(id);
		ServerLauncher.chatlist.remove(thread);
	}

	private void sendMessage() throws IOException {
		for (User user : UserList.getList()) {
			sendMessageToOne(user.getObjectOutputStream());
		}
	}

	private void sendMessageToOne(ObjectOutputStream objectOutputStreamToOne) throws IOException {
		objectOutputStreamToOne.writeObject(message);
		objectOutputStreamToOne.reset();
	}

	public void removeUser() throws IOException {
		// 채팅에 참여한 유저가 종료 시 관리자 메세지로 전체에게 퇴장 메세지 전송
		if (name != null) {
			UserList.getList().remove(user);
			String adminMessage = name + " 님이 퇴장하셨습니다.";
			setAdminMessage(TypeOfMessage.EXIT, adminMessage);
			sendMessage();
		}
	}

	private String getSearchResult(String keyword) {
		searching.search(keyword);
		String link = "<a href=\"" + searching.getLink() + "\">" + keyword + "</a>";
		System.out.println(link);
		String description = searching.getDescription();
		return link + "<hr>" + description;
	}

	private void closeConnection() {
		if (name != null) {
			UserList.getList().remove(user);
		}
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
		if (sockt != null) {
			try {
				sockt.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
