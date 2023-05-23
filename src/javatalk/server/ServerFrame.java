package javatalk.server;
/**
 * @author 최대진
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/*
 * 서버 로그를 보여주는 GUI 정의한 클래스
 */
public class ServerFrame extends JFrame {

	private static final long serialVersionUID = -6439616553826198569L;
	private JLabel topLabel;
	private JTextArea logText;
	private JButton clearBtn;
	private JButton exitBtn;
	private JPanel container;
	private JPanel btnPanel;

	public ServerFrame() {
		setTitle("Server Log Window");
		setSize(400, 550);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

		container = (JPanel) getContentPane();
		topLabel = new JLabel("Server Log :");

		logText = new JTextArea(10, 15);
		logText.setEditable(false);
		logText.setLineWrap(true);

		JScrollPane jsp = new JScrollPane(logText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		clearBtn = new JButton("Clear");
		exitBtn = new JButton("Exit");

		clearBtn.addActionListener(new ClearBtnListener());
		exitBtn.addActionListener(new ExitBtnListener());

		btnPanel = new JPanel();
		btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));

		btnPanel.add(clearBtn);
		btnPanel.add(exitBtn);

		container.add(topLabel);
		container.add(jsp);
		container.add(btnPanel);
		setVisible(true);
	}

	public class ClearBtnListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			logText.setText("");

		}
	}

	public class ExitBtnListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}

	public JTextArea getLogText() {
		return logText;
	}

}
