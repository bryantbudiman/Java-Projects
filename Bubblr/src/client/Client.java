package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import GUI.BubbLogin;
import GUI.GamePlayGUI;
import server.Message;

public class Client extends Thread {
	private Socket s;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private boolean gameStarted;
	private User user;
	private BubbLogin bLogin;
	private GamePlayGUI gpGUI;
	
	private boolean alive;

	public Client(String hostname, int port, User user) {
		try {
			// port may be hardcoded at 6789 at some point
			this.user = user;
			s = new Socket(hostname, port);
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			alive = true;
			this.start();
		} catch (IOException ioe) {
			System.out.println("ioe in constructor: " + ioe.getMessage());
		}
	}

	public void run() {
		try {
			// send team name
			sendMessage(new Message(user, Message.USER_LOGIN));
			System.out.println("running client!");
			while (alive) {
				Message m = (Message) ois.readObject();
				if(m.type != Message.HIGHSCORE)
					System.out.println("server said: " + m.type + " " + m.info);
				switch (m.type) {
				case Message.HIGHSCORE: {
					if(gpGUI != null) {
						gpGUI.updateHighscores(m.highScoreList);
						gpGUI.updateTimer(m.score);
					}
					break;
				}
				case Message.END_RESPONSE: {
					user.setHighscore(m.score);
				}
				case Message.LOGIN_RESPONSE: {
					if(m.bool) {
						user.setHighscore(m.user.getIntHighscore());
						bLogin.exitLogin(user);
					} else {
						bLogin.alert(m.info);
					}
					break;
				}
				case Message.SERVER_LOGOUT: {
					
					break;
				}

				default:
					System.out.println("error: message has no type");
					break;
				}
			}
		} catch (IOException ioe) {
			// this exception will happen on logout
			System.out.println("ioe (on purpose to exit loop) in run : " + ioe.getMessage());
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe: " + cnfe.getMessage());
		} finally {
			killThread();
		}
	}
	
	public void killThread() {
		alive = false;
		try {
			if (oos != null)
				oos.close();
			if (ois != null)
				ois.close();
			if (s != null) {
				s.close();
			}
		} catch (IOException ioe) {
			System.out.println("ioe in run's finally: " + ioe.getMessage());
		}
	}

	public void sendMessage(Message m) {
		try {
			System.out.println("telling server: " + m.type + " " + m.info);
			if (gameStarted)
				oos.reset();
			oos.writeObject(m);
			oos.flush();

		} catch (IOException ioe) {
			System.out.println("ioe in sendMessage: " + ioe.getMessage());
		}
	}
	
	public void setBubbLogin(BubbLogin b) {
		bLogin = b;
	}
	
	public void setGamePlayGUI(GamePlayGUI g) {
		gpGUI = g;
	}
}
