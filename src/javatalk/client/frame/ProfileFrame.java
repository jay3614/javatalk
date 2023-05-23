package javatalk.client.frame;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

/*
 * 채팅창에서 사용할 프로필 이미지를 선택하는 창의 GUI 정의한 클래스
 * 로그인창에서 기본프로필 이미지를 클릭 시 해당 클래스가 호출되어 다른 프로필 이미지를 선택하면 해당 프로필 이미지로 변경되며 이후 로그인 시 선택한 이미지로 채팅에 참여하게 된다.
 */
public class ProfileFrame extends JFrame {

	public static final String PROFILEPATH = "images/profile";
	private JPanel contentPane;
	JPanel panel;
	
	public ProfileFrame(final AppFrame frame) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 390, 263);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(0, 0, 374, 226);
		
		contentPane.add(scrollPane);
		
		panel = new JPanel();
		panel.setBackground(Color.WHITE);
		scrollPane.setViewportView(panel);
		
		File path = new File(PROFILEPATH);
		int numberOfProfile = path.list().length;
		int rowLength = numberOfProfile%3==0 ? numberOfProfile/3 : numberOfProfile/3+1; 
		panel.setLayout(new GridLayout(rowLength, 3, 0, 12));
		
		JLabel[] lblProfile = new JLabel[numberOfProfile];
		
		for (int i = 0; i < numberOfProfile; i++) {
			lblProfile[i] = new JLabel(getProfileImage(i));
			lblProfile[i].setForeground(Color.WHITE);
			lblProfile[i].setText(""+i);
			lblProfile[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent event) {
					JLabel profile = (JLabel) event.getSource();
					frame.getLoginPane().changeProfileImage(Integer.parseInt(profile.getText()));
					dispose();
				}
			});
			panel.add(lblProfile[i]);
		}		
	}

	private ImageIcon getProfileImage(int index) {
		return new ImageIcon(new ImageIcon(PROFILEPATH+"/profile"+index+".png").getImage().getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH));
	}
}
