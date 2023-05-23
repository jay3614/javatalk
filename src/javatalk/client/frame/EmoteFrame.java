package javatalk.client.frame;
/**
 * @author 정영우
 */
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

import javatalk.client.Sender;

public class EmoteFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9163356082619332871L;
	public static final String EMOTEPATH = "images/emote";
	private JPanel contentPane;
	JPanel panel;
	JLabel[] lblEmote;

	public EmoteFrame() {
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 390, 263);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setLocationRelativeTo(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(0, 0, 374, 226);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		contentPane.add(scrollPane);

		panel = new JPanel();
		panel.setBackground(Color.WHITE);
		scrollPane.setViewportView(panel);

		File path = new File(EMOTEPATH); // 폴더 경로 설정
		path.list(); ////
		int numberOfEmote = path.list().length; // 폴더 내 이모티콘 파일들의 갯수
		int rowLength = numberOfEmote % 3 == 0 ? numberOfEmote / 3 : numberOfEmote / 3 + 1; // 파일 갯수에 따른 열 정렬 방법 정의
		panel.setLayout(new GridLayout(rowLength, 3, 0, 12));

		lblEmote = new JLabel[numberOfEmote]; // 이모티콘 목록창

		for (int i = 0; i < numberOfEmote; i++) { // 이모티콘 번호에 따라 이미지 설정
			lblEmote[i] = new JLabel(getEmoteImage(i));
			lblEmote[i].setForeground(Color.WHITE);
			lblEmote[i].setText("" + i);
			lblEmote[i].addMouseListener(new MouseAdapter() { // 이모티콘 이미지 클릭 시 이벤트 정의
				@Override
				public void mouseClicked(MouseEvent event) {

					JLabel emote = (JLabel) event.getSource(); // 클릭한 이미지에 대한 소스 가져옴
					int emoteNum = Integer.parseInt(emote.getText()); // 소스를 숫자로 변환 0~n
					String emopath = EMOTEPATH + "/emote" + emoteNum + ".jpg";
					Sender.getSender().sendEmote(emopath);

					dispose();
				}
			});
			panel.add(lblEmote[i]);
		}
	}

	private ImageIcon getEmoteImage(int index) {
		return new ImageIcon(new ImageIcon(EMOTEPATH + "/emote" + index + ".jpg").getImage().getScaledInstance(100, 100,
				java.awt.Image.SCALE_SMOOTH));
	}
}
