package chat.app;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/*
 * GUI for client. 
 * chatWindow, ID list, 
 * send message to everyone or specific ID (Secret message)
 */
public class Chat extends JFrame implements ItemListener {
	JPanel chatPanel = new JPanel();
	JPanel listPanel = new JPanel();
	
	GridLayout gridLayout1 = new GridLayout();
	
	JLabel idlist = new JLabel("ID list", SwingConstants.CENTER);
	JLabel secretLabel = new JLabel("");
	JLabel whomLabel = new JLabel("Click ID");
	
	JButton closeButton = new JButton();

	JTextField globalsend = new JTextField("Enter Message for Everyone");
	JTextField secretSend = new JTextField("Click ID Then Enter Message");
	
	List list = new List();
	JScrollPane scrollUsersPane = new JScrollPane();
	JTextArea txtArea = new JTextArea();
	JScrollBar scrollBar;

	String user; 		// For secret message.
	
	public Chat() {
		super("Chat :-) ");
		try {
			createChatGUI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Chat GUI that has a chat panel, list of users panel (listPanel),
	 * a close button, and a scroll pane.
	 */
	private void createChatGUI() throws Exception {
		this.getContentPane().setBackground(Color.LIGHT_GRAY);
		this.setResizable(false);
		this.getContentPane().setLayout(null);
		
		chatPanel.setEnabled(false);
		chatPanel.setBorder(BorderFactory.createEtchedBorder());
		chatPanel.setOpaque(false);
		chatPanel.setRequestFocusEnabled(false);
		chatPanel.setBounds(new Rectangle(4, 3, 441, 309));
		chatPanel.setLayout(null);
		
		listPanel.setOpaque(false);
		listPanel.setBounds(new Rectangle(316, 219, 120, 87));
		listPanel.setLayout(gridLayout1);
		
		gridLayout1.setRows(3);
		gridLayout1.setColumns(1);
		gridLayout1.setVgap(5);

		closeButton.setFont(new java.awt.Font("SansSerif", 0, 12));
		closeButton.setBorder(BorderFactory.createRaisedBevelBorder());
		closeButton.setText("QUIT");

		globalsend.setBounds(new Rectangle(7, 249, 306, 27));
		secretSend.setBounds(new Rectangle(70, 278, 243, 27));
		
		whomLabel.setEnabled(false);
		whomLabel.setFont(new java.awt.Font("SansSerif", 0, 12));
		whomLabel.setRequestFocusEnabled(false);
		whomLabel.setBounds(new Rectangle(7, 278, 61, 27));
		
		list.setBounds(new Rectangle(316, 5, 120, 207));
		
		scrollUsersPane.setAutoscrolls(true);
		scrollUsersPane.setBounds(new Rectangle(7, 5, 306, 238));
		
		// Attach everything.
		this.getContentPane().add(chatPanel, null);
		listPanel.add(idlist, null);
		listPanel.add(secretLabel, null);
		listPanel.add(closeButton, null);
		chatPanel.add(globalsend, null);
		chatPanel.add(secretSend, null);
		chatPanel.add(whomLabel, null);
		chatPanel.add(listPanel, null);
		chatPanel.add(list, null);
		chatPanel.add(scrollUsersPane, null);
		scrollUsersPane.getViewport().add(txtArea, null);
		setBounds(250, 200, 455, 360);
		list.addItemListener(this);
	}

	public void autoScroll() {
		int len = txtArea.getDocument().getLength();
		txtArea.setCaretPosition(len);
	}
	
	/*
	 * Secret message.
	 */
	public void itemStateChanged(ItemEvent e) {
		user = list.getSelectedItem();
		whomLabel.setText(user);
	}
}
