package javatalk.client;

import java.io.IOException;
import java.io.ObjectOutputStream;

import javatalk.client.util.FileSaveLoadUtil;
import javatalk.login.ModelDAO;
import javatalk.model.Message;
import javatalk.model.TypeOfMessage;

/*
 * 메세지를 송신하는 클래스
 */
public class Sender {

	String name;
	ObjectOutputStream objectOutputStream;
	private static Sender sender;
	
	public Sender(ObjectOutputStream objectOutputStream, String name) {
		this.name = name;
		this.objectOutputStream = objectOutputStream;
		sender = this;
	}
	
	public synchronized void sendMessage(String userMessage) { // 일반 메시지를 sender() 설정
		Message message = getMessage(userMessage);
		message.setType(TypeOfMessage.MESSAGE);
		send(message);
	}
	
	public void sendKickOff(String userMessage, String kickoffTarget) { // 강퇴 메시지를 sender()로 넘김
		Message message = getMessage(userMessage);
		message.setType(TypeOfMessage.KICK);
		message.setKickTarget(kickoffTarget);
		try {
			objectOutputStream.writeObject(message);
			objectOutputStream.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void sendWhisper(String userMessage, String whisperTarget) { // 귓속말을 sender()로 넘김
		Message message = getMessage( "("+ name + "->"+whisperTarget+"):\n"+ userMessage);
		message.setType(TypeOfMessage.WHISPER);
		message.setWhisperTarget(whisperTarget);
		send(message);
	}
	
	public void sendImage(String imagePath) { // 이미지를 sender()로 넘김
		Message message = getMessage(null);
		message.setType(TypeOfMessage.IMAGE);
		message.setImageExtention(imagePath.substring(imagePath.length()-4, imagePath.length()));
		message.setImage(FileSaveLoadUtil.fileLoad(imagePath));
		send(message);
	}
	
	public void sendEmote(String emotePath) { // 이모티콘을 sender()로 넘김
		Message message = getMessage(null);
		message.setType(TypeOfMessage.IMAGE);
		message.setImageExtention(emotePath.substring(emotePath.length()-4, emotePath.length()));
		message.setImage(FileSaveLoadUtil.fileLoad(emotePath));
		send(message);
	}
	
	public void sendSearch(String keyword) { // 검색 결과를 sender()로 넘김
		Message message = getMessage(keyword);
		message.setType(TypeOfMessage.SEARCH);
		send(message);
	}
	
	public Message getMessage(String userMessage) {
		Message message = new Message();
		message.setName(name);
		message.setMessage(userMessage);		
		return message;
	}
	
	public synchronized void send(Message message) {  // 여러 종류의 메시지들을 실질적으로 채팅방에 전송
		try {
			objectOutputStream.writeObject(message);
			objectOutputStream.reset();
			savelog(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void savelog(Message message) { // 저장된 채팅의 로그를 DB에 저장
		String user = message.getName();
		String chat = message.getMessage();
		try {
			ModelDAO.getInstance().saveChatlog(user, chat);
		} catch (Exception e) {
			System.out.println("채팅로그 기록 중 예외 : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static Sender getSender() {
		return sender;
	}
}
