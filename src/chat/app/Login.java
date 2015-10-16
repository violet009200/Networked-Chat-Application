package chat.app;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/*
 * Create GUI for ID / IP address when 
 * user logins to chat program, access to server. 
 */

// TODO Do not allow same nicknames.
// TODO Open new window for private chatting.
// TODO Show messages after a person leaves.

public class Login extends JFrame implements ActionListener, Runnable {
	// Creating GUI.
	JPanel globalPanel = new JPanel();
	JPanel ipPanel 	   = new JPanel();
	JPanel usrPanel    = new JPanel();
	JPanel optionPanel = new JPanel();
	JLabel ipLabel 	   = new JLabel();
	
	JLabel nameLabel = new JLabel();
	
	GridLayout gridLayout1 = new GridLayout();
	
	JTextField ipTextField = new JTextField("127.0.0.1");
	JTextField nameTextField = new JTextField("EnterNickName");
	
	JButton cancelButton = new JButton();
	JButton okButton = new JButton();
	
	Font font = new Font("Courier New", 0, 12);
	// END Login GUI.
	
	private String NAME = null;		// For quitting program.

	// SERVER
	static String myid;
	static BufferedWriter out;
	BufferedReader in;
	Socket socket;
	Chat chat = new Chat();

	public Login() {
		super("LOGIN");
		try {
			createLoginGUI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createLoginGUI() throws Exception {
		this.getContentPane().setBackground(new Color(249, 255, 255));
		this.getContentPane().setLayout(null);
		
		globalPanel.setBorder(BorderFactory.createEtchedBorder());
		globalPanel.setOpaque(false);
		globalPanel.setBounds(new Rectangle(3, 3, 246, 114));
		globalPanel.setLayout(gridLayout1);
		
		gridLayout1.setRows(3);
		gridLayout1.setColumns(1);
		gridLayout1.setVgap(5);
		
		optionPanel.setBorder(BorderFactory.createEtchedBorder());
		optionPanel.setOpaque(false);
		optionPanel.setLayout(null);
		
		usrPanel.setOpaque(false);
		usrPanel.setLayout(null);
		
		ipPanel.setOpaque(false);
		ipPanel.setLayout(null);
		
		ipLabel.setFont(new java.awt.Font("SansSerif", 0, 12));
		ipLabel.setText("IP Address: ");
		ipLabel.setBounds(new Rectangle(4, 3, 100, 27));
		
		nameLabel.setBounds(new Rectangle(4, 0, 100, 27));
		nameLabel.setFont(new java.awt.Font("SansSerif", 0, 12));
		nameLabel.setText("Username: ");
		
		ipTextField.setBounds(new Rectangle(78, 3, 163, 27));
		nameTextField.setBounds(new Rectangle(78, 0, 163, 27));
		
		cancelButton.setFont(new java.awt.Font("SansSerif", 0, 12));
		cancelButton.setBorder(BorderFactory.createRaisedBevelBorder());
		cancelButton.setText("Cancel");
		cancelButton.setBounds(new Rectangle(126, 2, 67, 26));
		
		okButton.setBounds(new Rectangle(48, 2, 67, 26));
		okButton.setFont(new java.awt.Font("SansSerif", 0, 12));
		okButton.setBorder(BorderFactory.createRaisedBevelBorder());
		okButton.setText("Sign In");
		
		this.getContentPane().add(globalPanel, null);
		globalPanel.add(ipPanel, null);
		ipPanel.add(ipLabel, null);
		ipPanel.add(ipTextField, null);
		globalPanel.add(usrPanel, null);
		usrPanel.add(nameLabel, null);
		usrPanel.add(nameTextField, null);
		globalPanel.add(optionPanel, null);
		optionPanel.add(cancelButton, null);
		optionPanel.add(okButton, null);

		setBounds(200, 200, 270, 160);
		setVisible(true);

		// Add Events.
		nameTextField.addActionListener(this);
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		chat.globalsend.addActionListener(this);
		chat.closeButton.addActionListener(this);
		chat.secretSend.addActionListener(this);


		chat.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeProcess();
				System.exit(0);
			}
		});

	}

	/*
	 * Handle events via actionPerformed.
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		// Login window.
		if (e.getSource() == nameTextField || e.getSource() == okButton)
			connectProcess();
		else if (e.getSource() == cancelButton)			// Close login window.
			setVisible(false);
		else if (e.getSource() == chat.globalsend)		// Send message to all.
			sendProcess();
		else if (e.getSource() == chat.closeButton) {	// Quit the chat room.
			closeProcess();
			System.exit(0);
		}
		else if (e.getSource() == chat.secretSend)
			secretMessage();

	}

	private void secretMessage() {
		// Checks if users try to send a message to themselves.
		if (chat.user.equals(myid)) {
			JOptionPane.showMessageDialog(this, "\n\tPlease select other users\n");
			return;
		}
		
		String msg = chat.secretSend.getText().trim();
		try {
			out.write("300| " + chat.user + "| " + msg + "\n");
			out.flush();
			chat.txtArea.append("Secret Message [" + chat.user + "] >> " + msg + "\n");
			chat.autoScroll();
			chat.secretSend.setText("");
			chat.secretSend.requestFocus();
		} catch (IOException e) {
			System.err.println("ERROR: Sending secret message\n");
			e.printStackTrace();
		}
	}

	public void closeProcess() {
		chat.list.removeAll();
		chat.txtArea.setText("");
		chat.globalsend.setText("");
		chat.secretSend.setText("");
		chat.whomLabel.setText("");
		chat.setVisible(false);
		try {
			out.write("900| " + myid + " has disconnected.\n");
			out.flush();
			JOptionPane.showMessageDialog(null, " You are now disconnected ");
		} catch (IOException e) {
			System.err.println("ERROR: Quit the Program!!");
			e.printStackTrace();
		}
	}

	public void sendProcess() {

		String msg = chat.globalsend.getText().trim();
		if (msg.length() < 1) {	return; }
		try {
			out.write("200| " + msg + "\n");
			out.flush();
		} catch (IOException e) {
			System.err.println("ERROR: Sending Message To Everyone!!");
			e.printStackTrace();
		}
		chat.globalsend.setText("");
		chat.globalsend.requestFocus();
	}

	public void connectProcess() {
		String ip = ipTextField.getText().trim();

		if (ip.length() < 1) {
			JOptionPane.showMessageDialog(this, "Enter IP Address");
			ipTextField.requestFocus();
			return;
		}
		myid = nameTextField.getText();
		if (myid.length() < 1) {
			JOptionPane.showMessageDialog(this, "Enter ID");
			nameTextField.requestFocus();
			return;
		}

		try {
			socket = new Socket(ip, 25008); 			// port
			chat.txtArea.setEditable(false);			// Prevents user from typing in chat area.
			chat.txtArea.append("Connection pass\n");
			setVisible(false);
			chat.globalsend.requestFocus();
			chat.setTitle("[" + myid + "'socket ChatRoom]");
			chat.setVisible(true);

			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			out.write("100| " + myid + "\r\n");
			out.flush();
			new Thread(this).start();
		} catch (Exception e) {
			System.err.println("ERROR: CONNECTION!!");
			e.printStackTrace();
		}

	}

	public void run() {
		while (true) {
			try {
				String msg = in.readLine();
				System.out.println(msg);
				StringTokenizer st = new StringTokenizer(msg, "|");
				int num = Integer.parseInt(st.nextToken());
				switch (num) {
				case 100:	// Login
				{
					String nickname = st.nextToken();

					String nickall = st.nextToken();
					StringTokenizer token = new StringTokenizer(nickall, "%");
					chat.list.removeAll();
					while (token.hasMoreTokens()) {
						String temp = token.nextToken();
						chat.list.add(temp);
					}
					chat.txtArea.append("--> [" + nickname + "] Entered ChatRoom\n");
					chat.autoScroll();
					chat.txtArea.setForeground(new Color(255, 0, 255));
				}
					break;
				case 200:	// Send message to everyone.
				{
					String m = st.nextToken();
					chat.txtArea.append(m + "\n");
					chat.txtArea.setForeground(new Color(75, 0, 130));
					chat.autoScroll();
				}
					break;
				case 300:	// Send secret message.
				{
					String name = st.nextToken();
					String message = st.nextToken();
					chat.txtArea.append("[" + name + "] Secret Msg >> " + message + "\n");
					chat.autoScroll();
				}
					break;
				case 900:	// Quit the program.
				{
					String nickname = st.nextToken();
					String tempName;
					tempName = nickname.replace("[", "");
					tempName = tempName.replace("]", "");
					tempName = tempName.replace("  "," ");
					NAME = tempName;
					
					if (nickname.equals(myid)) {
						/*in.close();
						out.close();
						socket.close(); 	// Close socket.
						return;*/

					} else {
						chat.txtArea.append("*** " + NAME + " is Disconnected\n");
						chat.list.remove(NAME);
						chat.autoScroll();
					}
				}
					break;
				}// switch

			} catch (Exception e) {
				System.err.println("ERROR: Client");
			//	return;
			}
		}// while
	}// run

	public static void main(String[] args) {
		new Login();
	}
}
