package javatalk.client.frame;
/**
 * @author ALL
 * 
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import javatalk.client.Listener;
import javatalk.login.ModelDAO;

/*
 * 로그인창 GUI를 정의한 클래스
 * DAO를 통해 DB로부터 입력한 값을 비교하여 맞는 값이라면 로그인에 성공하여 ChatPanel로 보내준다.
 * 그 외에 회원가입창, 비밀번호분실창으로 넘어가는 버튼 구현
 */
public class LoginPanel extends JPanel {

	private JTextField txtName;
	private JPasswordField txtPassword;
	private int profileNum = 0;
	ImageIcon profileImage = getProfileImage();
	JLabel lblProfile;
	String id, password;

	public LoginPanel(final AppFrame frame) {

		setBackground(Color.ORANGE);
		setLayout(null);

		JLabel lblImage = new JLabel(new ImageIcon("images/javatalk.png"));
		lblImage.setBounds(104, 7, 200, 200);
		add(lblImage);

		txtName = new JTextField();
		TextHint hint = new TextHint(txtName, "아이디 입력!");
		txtName.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		txtName.setHorizontalAlignment(SwingConstants.CENTER);
		txtName.setBounds(112, 380, 166, 27);
		add(txtName);
		txtName.setColumns(10);

		txtPassword = new JPasswordField();
		TextHint hint2 = new TextHint(txtPassword, "비밀번호 입력!");
		txtPassword.setFont(new Font("맑은 고딕", Font.PLAIN, 14));// 아이디 입력하는 글씨크기
		txtPassword.setHorizontalAlignment(SwingConstants.CENTER);
		txtPassword.setBounds(112, 420, 166, 27);
		add(txtPassword);
		txtPassword.setColumns(10);

		JButton btnLogin = new JButton("로그인");
		btnLogin.setForeground(Color.WHITE);
		btnLogin.setBackground(Color.DARK_GRAY);
		btnLogin.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				id = getTxtName();
				password = getTxtPassword();
				if (ModelDAO.getInstance().isMember(id) == 0) { // 0 = 일치하는 id의 갯수가 0이라는 뜻
					JOptionPane.showMessageDialog(null, "존재하지 않는 ID 입니다.");
				} else {

					if (ModelDAO.getInstance().checkPass(id, password)) { // 입력한 값과 DB에 있는 비밀번호 비교
						JOptionPane.showMessageDialog(null, id + "님 로그인을 환영합니다.");
						// 로그인 이후 채팅방으로 넘어가도록 메서드 호출
						ModelDAO.getInstance().makeFile(id); // id.dat 파일 생성하는 메서드 호출
						ModelDAO.getInstance().setUserId(id);
						new Thread(new Listener(frame)).start();

					} else {
						JOptionPane.showMessageDialog(null, "Password를 틀렸습니다.");
					}
				}

			}
		});
		btnLogin.setBounds(112, 470, 166, 27);
		btnLogin.setBorderPainted(false);
		
		add(btnLogin);

		JLabel lblNewLabel = new JLabel("━━━━━━━━━━━━━━━━━━━━━━━━━━");
		lblNewLabel.setFont(new Font("굴림", Font.PLAIN, 12));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(12, 507, 368, 15);
		add(lblNewLabel);
		
		JButton btnSign = new JButton("회원가입");
		btnSign.setForeground(Color.DARK_GRAY);
		btnSign.setBackground(Color.DARK_GRAY);
		btnSign.setOpaque(false);
		btnSign.setBorderPainted(false);
		btnSign.setBorderPainted(false);
		btnSign.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		//

		btnSign.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				frame.changeToRegister();
			}
		});
		btnSign.setBounds(205, 529, 133, 27);
		add(btnSign);

		JButton btnfind = new JButton("비밀번호 찾기");
		btnfind.setForeground(Color.DARK_GRAY);
		btnfind.setBackground(Color.DARK_GRAY);
		btnfind.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		btnfind.setBounds(58, 529, 133, 27);
		btnfind.setBorderPainted(false);
		btnfind.setOpaque(false);
		btnfind.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				frame.changetoForgot();
			}
		});
		add(btnfind);

		lblProfile = new JLabel(profileImage);
		lblProfile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				ProfileFrame profileFrame = new ProfileFrame(frame);
				profileFrame.setVisible(true);
			}
		});
		lblProfile.setBounds(120, 200, 160, 160);
		add(lblProfile);
	}

	public void changeProfileImage(int index) {
		profileNum = index;
		profileImage = getProfileImage();
		lblProfile.setIcon(profileImage);
	}

	private ImageIcon getProfileImage() {
		return new ImageIcon(new ImageIcon(ProfileFrame.PROFILEPATH + "/profile" + profileNum + ".png").getImage()
				.getScaledInstance(160, 160, java.awt.Image.SCALE_SMOOTH));
	}

	public String getTxtName() {
		return txtName.getText();
	}

	public String getTxtPassword() {
		return txtPassword.getText();
	}

	public void setTxtPassword(JPasswordField txtPassword) {
		this.txtPassword = txtPassword;
	}

	public void setTxtName(JTextField txtName) {
		this.txtName = txtName;
	}

	public int getProfileNum() {
		return profileNum;
	}

	public class TextHint implements FocusListener, DocumentListener, PropertyChangeListener {
		private final JTextField textfield;
		private boolean isEmpty;
		private Color hintColor;
		private Color foregroundColor;
		private final String hintText;

		public TextHint(final JTextField textfield, String hintText) {
			super();
			this.textfield = textfield;
			this.hintText = hintText;
			this.hintColor = Color.ORANGE;
			textfield.addFocusListener(this);
			registerListeners();
			updateState();
			if (!this.textfield.hasFocus()) {
				focusLost(null);
			}
		}

		public void delete() {
			unregisterListeners();
			textfield.removeFocusListener(this);
		}

		private void registerListeners() {
			textfield.getDocument().addDocumentListener(this);
			textfield.addPropertyChangeListener("foreground", this);
		}

		private void unregisterListeners() {
			textfield.getDocument().removeDocumentListener(this);
			textfield.removePropertyChangeListener("foreground", this);
		}

		public Color getGhostColor() {
			return hintColor;
		}

		public void setGhostColor(Color hintColor) {
			this.hintColor = hintColor;
		}

		private void updateState() {
			isEmpty = textfield.getText().length() == 0;
			foregroundColor = textfield.getForeground();
		}

		@Override
		public void focusGained(FocusEvent e) {
			if (isEmpty) {
				unregisterListeners();
				try {
					textfield.setText("");
					textfield.setForeground(foregroundColor);
				} finally {
					registerListeners();
				}
			}
		}

		@Override
		public void focusLost(FocusEvent e) {
			if (isEmpty) {
				unregisterListeners();
				try {
					textfield.setText(hintText);
					textfield.setForeground(hintColor);
				} finally {
					registerListeners();
				}
			}
		}

		@Override

		public void propertyChange(PropertyChangeEvent evt) {
			updateState();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			updateState();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			updateState();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			updateState();
		}
	}

}
