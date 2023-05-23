package javatalk.client.frame;
/**
 * @author 정영우
 */
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javatalk.login.DbReader;
import javatalk.login.ModelDAO;

/*
 * 기본 베이스가 되는 GUI 클래스
 * *Panel 클래스들은 전부 해당 클래스의 GUI 창을 베이스로 삼는다.
 */
public class AppFrame extends JFrame {

	private static final long serialVersionUID = 4690763100950422435L;
	private JPanel contentPane;
	private LoginPanel loginPane;
	private ChatPanel chatPane;
	private SignPanel registerPane;
	private ForgotFrame forgotPane;

	public AppFrame() {
		setTitle("javatalk");
		loginPane = new LoginPanel(this);
		chatPane = new ChatPanel();
		registerPane = new SignPanel(this);
		forgotPane = new ForgotFrame(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 408, 660);
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image img = toolkit.getImage("images/icon.png");
		setIconImage(img);
		setLocationRelativeTo(null);
		setResizable(false);
		addWindowListener(new WindowAdapter() { // 창을 끌 때 DB에 채팅로그를 전송
			public void windowClosing(WindowEvent e) {
				try {
					String id = ModelDAO.getInstance().getUserId();
					String[] strList = DbReader.reader(id);
					String strListToString = Arrays.toString(strList);
					if(strList.length != 0) {
						ModelDAO.getInstance().dbChatlog(id, strListToString);
					}
					
					System.exit(0);
				} catch (Exception e2) {
					System.exit(0);
				}
			}
		});
		changeToLogin();
	}

	public void changetoForgot() { // 비번찾기 눌렀을 경우 
		contentPane = forgotPane;
		paintPane();
	}

	public void changeToRegister() { // 회원가입 눌렀을 경우
		contentPane = registerPane;
		paintPane();
	}

	public void changeToLogin() { // 로그인창으로 돌아올 경우
		contentPane = loginPane;
		paintPane();
	}

	public void changeToChat() { // 로그인 이후 채팅창으로 변경
		contentPane = chatPane;
		paintPane();
	}

	public void changeToError(String errorMessage) { // 에러 발생시
		contentPane = new ErrorPanel(this, errorMessage);
		paintPane();
	}

	public void paintPane() {
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		revalidate();
		repaint();
	}

	public LoginPanel getLoginPane() {
		return loginPane;
	}

	public ChatPanel getChatPane() {
		return chatPane;
	}

	public SignPanel getRegisterPane() {
		return registerPane;
	}
}