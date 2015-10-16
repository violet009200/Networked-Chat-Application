package chat.app;

import java.awt.Container;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Server extends JFrame implements Runnable {
	JTextArea textArea = new JTextArea();
	JScrollPane scrollPane;
	Container container;

	@SuppressWarnings("rawtypes")
	Vector vc = new Vector();
	ServerSocket server;

	public Server() {
		super("Server");
		container = getContentPane();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}}
		);
		scrollPane = new JScrollPane(textArea);
		container.add(scrollPane);
		setSize(500, 500);
		setVisible(true);
	}

	// Create server socket, socket.
	public void run() {
		try {
			server = new ServerSocket(25008); 			// port
			textArea.setEditable(false);				// Server text area is not editable.
			textArea.append("Connected to Server.\n");
			textArea.append("100: Login\n"
						  + "200: Send Message to Everyone\n"
						  + "300: Send Secret Message\n"
						  + "900: Disconnected");
			textArea.append("\n\tStart Chat ^( ^_^)^ ('.- )9\n\n");
			textArea.setFont(new Font("Courier New", Font.BOLD, 14));
			textArea.setEditable(false);
		} catch (IOException e) {
			textArea.append("Failed to create ServerSocket\n");
			e.printStackTrace();
		}
		while (true) {
			try {
				Socket socket = server.accept();
				textArea.append("Client login\n");
				Service service = new Service(socket);
				service.start();
			} catch (IOException e) {
				System.err.println("Client failed to login!");
				e.printStackTrace();
			}
		}
	}

	// Inner class Service.
	public class Service extends Thread {
		String name;
		BufferedReader in = null;
		BufferedWriter out = null;
		Socket socket;

		public Service(Socket s) {
			this.socket = s;
			try {
				in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
				out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Send Message to all clients.
		public void sendAll(String msg) {
			int size = vc.size();

			for (int i = 0; i < size; i++) {
				Service sv = (Service) vc.elementAt(i);
				try {
					sv.out.write(msg + "\n");
					sv.out.flush();
				} catch (IOException e) {
					System.err.println("ERROR: writing to service");
					e.printStackTrace();
				}
			}
		}

		public void autoScroll() {
			int len = textArea.getDocument().getLength();
			textArea.setCaretPosition(len);
		}

		@SuppressWarnings("unchecked")
		public void run() {
			while (true) {
				String msg = null;
				try {
					msg = in.readLine();
				} catch (Exception e) {
					System.err.println("read error");
					return;
				}
				textArea.append("Server Receives: " + msg + "\n");
				autoScroll();
				
				StringTokenizer st = new StringTokenizer(msg, "|");
				int num = Integer.parseInt(st.nextToken());
				switch (num) {
					case 100:	// Login.
					{
						name = st.nextToken();
						vc.addElement(this);
						String nickall = "";
						for (int i = 0; i < vc.size(); i++) {
							Service sv = (Service) vc.elementAt(i);
							nickall += sv.name + "%";
						}
						sendAll("100| " + name + " |" + nickall);
					}
						break;
					case 200:	// Message to Everyone.
					{
						msg = st.nextToken();
						sendAll("200| " + name + " >> " + msg);
						break;
					}
					case 300:
					{
						String name = st.nextToken();
						String message = st.nextToken();
						sendToUser(300, name, message);
					}
					break;
					case 900:	// Quit the program.
					{
						for (int i = 0; i < vc.size(); i++) {
							Service sv = (Service) vc.elementAt(i);
							if (name.equals(sv.name)) {
								vc.removeElementAt(i);
							}
						}
						sendAll("900| [" + name + "]\n");
						try {
							this.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}// switch
			}// while
		}// run

		private void sendToUser(int i, String name2, String message) {
			for(int k = 0; k < vc.size(); k++) {
				Service service = (Service)vc.elementAt(k);
				if (name2.equals(service.name)) {
					try {
						service.out.write(i + "| " + name2 + " |" + message + "\n");
						service.out.flush();
					} catch (IOException e) {
						System.err.println("ERROR: sendtoUser");
						e.printStackTrace();
					}
				}
			}
		}
	}// inner class

	public static void main(String[] args) {
		Server ss = new Server();
		new Thread(ss).start();
	}
}
