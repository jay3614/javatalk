package javatalk.client;

import javatalk.client.frame.AppFrame;

/*
 * 클라이언트 런쳐
 */
public class ClientLauncher {
	
	public static void main(String[] args) {
		try {
			AppFrame frame = new AppFrame(); // GUI 생성
			frame.setVisible(true); // 생성된 GUI 보이기
		} catch (Exception e) {
			System.out.println("비정상적인 채팅창 종료");
			e.printStackTrace();
		}
	}
}
