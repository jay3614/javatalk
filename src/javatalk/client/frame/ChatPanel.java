package javatalk.client.frame;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.text.html.HTMLDocument;

import javatalk.client.Sender;
import javatalk.client.util.FileChooserUtil;
import javatalk.client.util.HTMLMaker;
import javatalk.client.util.UserList;
import javatalk.login.DbReader;
import javatalk.login.ModelDAO;
import javatalk.model.ChatCommand;
import javatalk.model.Message;

/*
 * 실제로 채팅이 이뤄지는 패널
 */
public class ChatPanel extends JPanel {

	private static final long serialVersionUID = -9145212104616644424L;
	JScrollPane chatScrollPane;
	ImageIcon image1;
	JList userList;
	public JTextPane chatTextPane;
	public JTextArea txtrMessage;
	HTMLDocument doc;
	AppFrame frame;
	DefaultListModel<String> userListModel = new DefaultListModel<String>();
	private StringBuffer messageList = new StringBuffer();
	private boolean isOpenList = false;
	private StringBuffer chatLog = new StringBuffer();
	private HTMLMaker htmlMaker = new HTMLMaker();
	String contents;

	public ChatPanel() {

		Color skyblue = new Color(186, 206, 224);
		setLayout(null);

		JPanel chatBoardPane = new JPanel();
		chatBoardPane.setBackground(skyblue);
		chatBoardPane.setBounds(0, 0, 393, 500);
		add(chatBoardPane);
		chatBoardPane.setLayout(null);

		chatScrollPane = new JScrollPane();
		chatScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		chatScrollPane.setBounds(0, 45, 409, 455);
		chatBoardPane.add(chatScrollPane);

		chatTextPane = new JTextPane();
		chatTextPane.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
		chatTextPane.setBackground(skyblue);
		chatTextPane.setEditable(false);
		chatScrollPane.setViewportView(chatTextPane);
		chatTextPane.setText("");

		image1 = new ImageIcon("images/out.PNG"); // 종료 버튼
		JLabel exitList = new JLabel(image1);
		add(exitList);
		exitList.setFont(new Font("맑은 고딕", Font.BOLD, 22));
		exitList.setHorizontalAlignment(SwingConstants.CENTER);
		exitList.setBounds(320, 5, 85, 35);
		chatBoardPane.add(exitList);
		exitList.addMouseListener(new MouseAdapter() { // 종료 버튼 클릭 시 이벤트 정의
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					String id = ModelDAO.getInstance().getUserId();
					String[] strList = DbReader.reader(id);
					String strListToString = Arrays.toString(strList);
					if (strList.length != 0) {
						ModelDAO.getInstance().dbChatlog(id, strListToString);
					}

				} catch (IOException e) {
					System.out.println("DB에 채팅로그 기록 중 예외 : " + e.getMessage());
					e.printStackTrace();
				}
				System.exit(0);
			}
		});

		userList = new JList(userListModel); // 유저 목록
		userList.addMouseListener(new MouseAdapter() { // 유저 목록에서 유저 클릭 시 발생하는 이벤트 정의
			@Override
			public void mouseClicked(MouseEvent e) {
				if (isDoubleClicked(e)) { // 더블클릭 시 클릭 대상 귓속말 커멘드 호출
					setWhisperCommand(userList.getSelectedValue().toString());
				}
				try {
					if (e.getButton() == MouseEvent.BUTTON3) { // 우클릭 시 클릭 대상 벤 커멘드 호출
						setKickUserChoise(userList.getSelectedValue().toString());
					}

				} catch (NullPointerException e1) {
					System.out.println("유저를 선택 후 우클릭해주세요");
				}
			}
		});

		userList.setBackground(Color.WHITE);
		userList.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
		chatScrollPane.setColumnHeaderView(userList);
		userList.setVisible(false);
		userList.setVisibleRowCount(0);
		userList.setAutoscrolls(true);

		JLabel lblUserList = new JLabel("≡"); // 유저 목록창 open/close 버튼
		lblUserList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				userListControl();
			}
		});

		lblUserList.setFont(new Font("맑은 고딕", Font.BOLD, 36));
		lblUserList.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserList.setBounds(12, 0, 40, 40);
		chatBoardPane.add(lblUserList);
		chatTextPane.setContentType("text/html");
		doc = (HTMLDocument) chatTextPane.getStyledDocument();

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 510, 299, 100);
		add(scrollPane);

		txtrMessage = new JTextArea(); // 채팅 입력하는 입력창
		txtrMessage.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (isEnter(e)) {
					pressEnter(txtrMessage.getText().replaceAll("\n", ""));
				}
			}
		});
		txtrMessage.setLineWrap(true);
		txtrMessage.setWrapStyleWord(true);
		scrollPane.setViewportView(txtrMessage);

		JButton btnNewButton = new JButton("전송");
		btnNewButton.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
		btnNewButton.setBackground(Color.ORANGE);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pressEnter(txtrMessage.getText());
			}
		});
		btnNewButton.setBounds(320, 510, 65, 55);
		add(btnNewButton);

		JLabel lblImage = new JLabel(new ImageIcon("images/image.png")); // 이미지 삽입창 활성화 버튼
		lblImage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				sendImage();
			}
		});
		lblImage.setBounds(320, 570, 30, 30);
		add(lblImage);

		JLabel lblSave = new JLabel(new ImageIcon("images/emoticon.png")); // 이모티콘창 활성화 버튼
		lblSave.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				new EmoteFrame();
			}
		});
		lblSave.setBounds(355, 570, 30, 30);
		add(lblSave);

	}

	private void setWhisperCommand(String whisperTarget) { // 귓속말 커멘드 정의
		txtrMessage.setText(ChatCommand.WHISPER + " " + whisperTarget + " ");
	}

	private void setKickUserChoise(String kickTarget) { // 벤 커멘드 정의
		txtrMessage.setText(ChatCommand.KICK + " " + kickTarget);
	}

	private boolean isDoubleClicked(MouseEvent e) { // 더블클릭 이벤트 정의
		return e.getClickCount() == 2;
	}

	private void userListControl() { // 유저목록 open/close 컨트롤 정의
		if (isOpenList) {
			userListClose();
		} else {
			userListOpen();
		}
	}

	private void userListOpen() { // 유저목록 open
		setUserList();
		userList.setVisible(true);
		userList.setVisibleRowCount(8);
		isOpenList = true;
	}

	private void setUserList() {
		userListModel.clear();
		for (String userName : UserList.getUsernameList()) {
			userListModel.addElement(userName);
		}
	}

	private void userListClose() { // 유저목록 close
		userList.setVisible(false);
		userList.setVisibleRowCount(0);
		isOpenList = false;
	}

	private boolean isEnter(KeyEvent e) {
		return e.getKeyCode() == KeyEvent.VK_ENTER;
	}

	private void pressEnter(String userMessage) { // 보내는 채팅 내용에 따라 맞는 작업 수행
		if (isNullString(userMessage)) {
			return;
		} else if (isWhisper(userMessage)) {
			sendWhisper(userMessage);
		} else if (isSearch(userMessage)) {
			sendSearch(userMessage);
		} else if (isKick(userMessage)) {
			sendKickOff(userMessage);
		} else {
			String contents = txtrMessage.getText();
			int len = contents.length();
			if (len > 30) { // 글자수 30 이상 전송 시도하면 도배금지 작동
				try {
					JOptionPane.showMessageDialog(null, "도배 시도로 인하여 10초간 채팅 금지");
					chatTextPane.setEditable(false);
					TimeUnit.SECONDS.sleep(10);
					chatTextPane.setEditable(true);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				sendMessage(userMessage);
			}
		}
		txtrMessage.setText("");
		txtrMessage.setCaretPosition(0);
	}

	private void sendKickOff(String userMessage) {
		String kickoffTarget = userMessage.split(" ", 2)[1];
		String sendingMessage = userMessage.replaceAll(ChatCommand.KICK + " " + kickoffTarget, "");
		Sender.getSender().sendKickOff(sendingMessage, kickoffTarget);
	}

	private void sendWhisper(String userMessage) {
		String whisperTarget = userMessage.split(" ", 3)[1];
		String sendingMessage = userMessage.replaceAll(ChatCommand.WHISPER + " " + whisperTarget, "");
		Sender.getSender().sendWhisper(sendingMessage, whisperTarget);
	}

	private void sendSearch(String userMessage) {
		Sender.getSender().sendSearch(userMessage);
	}

	private void sendMessage(String userMessage) {
		Sender.getSender().sendMessage(userMessage);
	}

	private boolean isNullString(String userMessage) {
		return userMessage == null || userMessage.equals("");
	}

	private boolean isWhisper(String text) {
		return text.startsWith(ChatCommand.WHISPER.toString());
	}

	private boolean isKick(String text) {
		return text.startsWith(ChatCommand.KICK.toString());
	}

	private boolean isSearch(String userMessage) {
		return userMessage.startsWith(ChatCommand.SEARCH.toString());
	}

	private void sendImage() {
		String imagePath = FileChooserUtil.getFilePath();
		if (imagePath == null) {
			return;
		} else if (imagePath.endsWith("png") || imagePath.endsWith("jpg") || imagePath.endsWith("gif")) {
			Sender.getSender().sendImage(imagePath);
		} else {
			JOptionPane.showMessageDialog(null, ".png, .jpg, .gif 확장자 파일만 전송 가능합니다.");
		}
	}

	public void addMessage(String adminMessage) {
		messageList.append(htmlMaker.getHTML(adminMessage));
		rewriteChatPane();
		addChatLog(adminMessage);
	}

	public void addMessage(boolean isMine, Message message) {
		messageList.append(htmlMaker.getHTML(isMine, message));
		rewriteChatPane();
		addChatLog(message.getName(), message.getMessage());
	}

	private void rewriteChatPane() {
		chatTextPane.setText(messageList.toString());
		chatTextPane.setCaretPosition(doc.getLength());
	}

	private void addChatLog(String adminMessage) {
		chatLog.append(adminMessage + "\r\n");
	}

	private void addChatLog(String userName, String userMsg) {
		chatLog.append(userName + " : " + userMsg + "\r\n");
	}

}
