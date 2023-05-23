package javatalk.client.frame;
/**
 * @author 박수빈
 */
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javatalk.login.MemberDTO;
import javatalk.login.ModelDAO;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

/*
 * 회원가입 창 GUI를 정의한 클래스
 * 이름과 email, ID, Password를 입력하고 ID 중복검사를 통하여 DB에 이미 존재하는지 검증하는 로직이 구성되어 있다.
 */
public class SignPanel extends JPanel { // 회원가입 GUI

	private JTextField txtname;
	private JTextField txtemail;
	private JTextField txtid;
	private JTextField txtpw;

	public SignPanel(final AppFrame frame) {
		setLayout(null);
		
		setBackground(Color.ORANGE);
		setName("회원가입창");
		
		setBounds(100, 100, 305, 580);
		setBorder(new EmptyBorder(0, 0, 0, 0));
		
		JLabel label = new JLabel("Sign Up");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("Abril Fatface", Font.BOLD, 45));
		label.setBounds(87+20+5, 39, 184, 60);
		add(label);
		
		JLabel namelb = new JLabel("User name : ");
		namelb.setFont(new Font("굴림", Font.BOLD, 18));
		namelb.setBounds(52+20+5, 156, 133, 18);
		add(namelb);
		
		JLabel maillb = new JLabel("Email : ");
		maillb.setFont(new Font("굴림", Font.BOLD, 18));
		maillb.setBounds(52+20+5, 222, 90, 18);
		add(maillb);
		
		JLabel idlb = new JLabel("ID : ");
		idlb.setFont(new Font("굴림", Font.BOLD, 18));
		idlb.setBounds(54+20+5, 290, 45, 18);
		add(idlb);
		
		JLabel pwlb = new JLabel("Password : ");
		pwlb.setFont(new Font("굴림", Font.BOLD, 18));
		pwlb.setBounds(52+20+5, 359, 120, 18);
		add(pwlb);
		
		txtname = new JTextField();
		txtname.setBounds(52+20+5, 181, 240, 24);
		add(txtname);
		txtname.setColumns(10);
		
		txtemail = new JTextField();
		txtemail.setBounds(52+20+5, 247, 240, 24);
		add(txtemail);
		txtemail.setColumns(25);
		
		txtid = new JTextField();
		txtid.setColumns(12);
		txtid.setBounds(52+20+5, 315, 240, 24);
		add(txtid);
		
		txtpw = new JTextField();
		txtpw.setColumns(12);
		txtpw.setBounds(52+20+5, 385, 240, 24);
		add(txtpw);

		JButton check = new JButton("중복검사");
		check.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(txtid.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "ID를 입력하세요.");
				}else{
					int result = ModelDAO.getInstance().isMember(txtid.getText());	// 입력한 ID가 이미 존재하는지 DB로부터 검증
					if(result == 0) {
						JOptionPane.showMessageDialog(null, txtid.getText() + "은(는) 사용 가능한 ID 입니다.");
					} else if(result == 1) {
						JOptionPane.showMessageDialog(null, txtid.getText() + "은(는) 이미 사용중입니다.");
					}
				}
			}
		});
		check.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		check.setFont(new Font("굴림", Font.PLAIN, 13));
		check.setBounds(94+20+5, 287, 110, 22);
		add(check);
		
		JButton join = new JButton("가입하기");
		join.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				MemberDTO dto = new MemberDTO();
				String id = txtid.getText();
				String password = txtpw.getText();
				String email = txtemail.getText();
				String name = txtname.getText();
				
				dto.setUserId(id);
				dto.setUserPassword(password);
				dto.setUserEmail(email);
				dto.setUserName(name);
				int result = ModelDAO.getInstance().regChat(dto);
				if(result == 1) {
					JOptionPane.showMessageDialog(null, id + "님, 회원가입이 완료되었습니다.");
					frame.changeToLogin();
				} else {
					JOptionPane.showMessageDialog(null, "회원가입 실패.");
				}
			}
		});
		
		join.setBackground(Color.DARK_GRAY);
		join.setForeground(Color.WHITE);
		join.setFont(new Font("굴림", Font.BOLD, 17));
		join.setBounds(100+20+5, 435, 140, 40);
		add(join);
		
		JButton back = new JButton("뒤로가기");
		back.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				frame.changeToLogin();
			}
		});
		back.setBackground(Color.DARK_GRAY);
		back.setForeground(Color.WHITE);
		back.setFont(new Font("굴림", Font.BOLD, 17));
		back.setBounds(100+20+5, 485, 140, 40);
		add(back);
	}
}
