package javatalk.client.frame;
/**
 * @author 박수빈
 */
import java.awt.Color;
import java.awt.Font;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
/**
 *  박수빈
 */
import javatalk.login.ModelDAO;

/*
 * 비밀번호 분실 시 찾아주는 로직 정의한 클래스
 * DAO를 통해 DB로부터 입력한 이메일과 비교하여 리턴받은 값에 따라 DB정보와 일치한다면 비밀번호를 전송해주는 GUI 정의
 */
public class ForgotFrame extends JPanel {

	private static final long serialVersionUID = 1L;

	ModelDAO dao;
	private JTextField txtemail;

	public ForgotFrame(final AppFrame frame) {

		setLayout(null);
		setBackground(Color.ORANGE);
		setName("비밀번호찾기");
		setBounds(100, 100, 408, 660);

		setBorder(new EmptyBorder(0, 0, 0, 0));

		JLabel label = new JLabel("Forgot  your  Password?");
		label.setFont(new Font("Baloo", Font.BOLD, 30));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBounds(0, 74, 392, 54);
		add(label);

		JLabel emaillb = new JLabel("> Email ");
		emaillb.setFont(new Font("Baloo", Font.BOLD, 20));
		emaillb.setBounds(12, 351, 97, 15);
		add(emaillb);

		txtemail = new JTextField();
		txtemail.setBounds(22, 376, 346, 38);
		txtemail.setFont(new Font("굴림", Font.BOLD, 18));
		add(txtemail);
		txtemail.setColumns(30);

		JButton Submit = new JButton("Submit");
		Submit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String email = txtemail.getText();
				int result = ModelDAO.getInstance().sendEmail(email);
				if (result == 1) {
					JOptionPane.showMessageDialog(null, "입력한 이메일로 비밀번호를 보냈습니다. \n확인해주세요.");
					frame.changeToLogin();
				} else if (result == 2) {
					JOptionPane.showMessageDialog(null, "해당 이메일에 대한 정보가 없습니다.");
				} else {
					JOptionPane.showMessageDialog(null, "요청 실패.");
				}
			}
		});

		Submit.setForeground(Color.WHITE);
		Submit.setBackground(Color.DARK_GRAY);
		Submit.setFont(new Font("Baloo", Font.PLAIN, 15));
		Submit.setBounds(147, 489, 106, 46);
		add(Submit);

		JButton retn = new JButton("Back");
		retn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				frame.changeToLogin();
			}
		});
		retn.setForeground(Color.WHITE);
		retn.setBackground(Color.DARK_GRAY);
		retn.setFont(new Font("Baloo", Font.PLAIN, 15));
		retn.setBounds(147, 545, 106, 46);
		add(retn);

		JLabel img = new JLabel(new ImageIcon("images/forgot.png"));
		img.setBounds(115, 160, 198, 198);
		add(img);
	}

}
