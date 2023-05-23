package javatalk.client.frame;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;

public class ErrorPanel extends JPanel {

	public ErrorPanel(final AppFrame frame, String errorMessage) {
		setBackground(Color.ORANGE);
		setLayout(null);

		JTextPane textPane = new JTextPane();
		textPane.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
		textPane.setBackground(Color.ORANGE);
		textPane.setBounds(62, 161, 306, 94);
		textPane.setEditable(false);
		add(textPane);
		textPane.setText(errorMessage);

		JButton btnNewButton = new JButton("돌아가기");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.changeToLogin();
			}
		});
		btnNewButton.setBackground(Color.DARK_GRAY);
		btnNewButton.setForeground(Color.WHITE);
		btnNewButton.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btnNewButton.setBounds(130, 296, 117, 43);
		add(btnNewButton);
	}
}
