package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import client.User;

public class Server extends Thread {
	private ServerSocket ss;
	private Vector<LoggedInPlayer> players;
	private ScoreList scoreList;
	private MySQLDriver db;

	public static void main(String[] args) {
		new Server(6789);

	}

	public Server(int port) {
		players = new Vector<LoggedInPlayer>();
		scoreList = new ScoreList(players);
		db = new MySQLDriver();
		db.connect();
		try {
			ss = new ServerSocket(port);
			this.start();
		} catch (IOException ioe) {
			System.out.println("ioe in Server: " + ioe.getMessage());
		}
	}

	public void run() {
		try {
			while (true) {
				if (players.size() < 10) {
					System.out.println("waiting for connection...");
					Socket s = ss.accept();
					System.out.println("connection from " + s.getInetAddress());
					players.add(new LoggedInPlayer(s));
				}
			}

		} catch (IOException ioe) {
			System.out.println("ioe (on purpose to exit Server loop): " + ioe.getMessage());
		} finally {
			try {
				if (ss != null) {
					ss.close();
				}
			} catch (IOException ioe) {
				System.out.println("ioe in Server run ginally: " + ioe.getMessage());
			}
		}
	}

	public void logout() {
		try {
			for (LoggedInPlayer pl : players) {
				if (pl.s != null)
					pl.sendMessage(new Message(Message.SERVER_LOGOUT));
			}
			for (LoggedInPlayer pl : players) {
				if (pl.s != null)
					pl.s.close();
			}
			if (ss != null) {
				ss.close();
			}
		} catch (IOException ioe) {
			System.out.println("ioe logout: " + ioe.getMessage());
		}
	}

	public class LoggedInPlayer extends Thread {
		private ObjectOutputStream oos;
		private ObjectInputStream ois;
		private Socket s;
		private User user;
		private long startTime;
		private long deathTime;
		private boolean playing;
		private boolean alive;

		public LoggedInPlayer(Socket s) {
			this.s = s;
			startTime = 0L;
			deathTime = 0L;
			playing = false;
			alive = true;
			try {
				oos = new ObjectOutputStream(s.getOutputStream());
				ois = new ObjectInputStream(s.getInputStream());
				this.start();
			} catch (IOException ioe) {
				System.out.println("ioe ServerThread constructor: " + ioe.getMessage());
			}
		}

		public void run() {
			try {
				while (alive) {
					Message m = (Message) ois.readObject();
					System.out.println("client said: " + m.type + " " + m.info);
					switch (m.type) {
					case Message.USER_LOGIN: {
						//if this username has already been chosen
						if (db.userExists(m.user)) {
							if (m.user.isNewUser()) {
								// new user, username taken
								sendMessage(new Message(false, "This username has already been chosen by another user", Message.LOGIN_RESPONSE));
							} else {
								// old user, username exists
								// check if password is correct!
								if (db.correctPassword(m.user)) {
									m.user.setHighscore(db.getScore(m.user));
									user = m.user;
									sendMessage(new Message(true, user, Message.LOGIN_RESPONSE));
								} else {
									sendMessage(new Message(false, "Incorrect Password!", Message.LOGIN_RESPONSE));
								}
							}
						} else if (m.user.isNewUser()) {
							m.user.setHighscore(db.getScore(m.user));
							user = m.user;
							sendMessage(new Message(true, user, Message.LOGIN_RESPONSE));
							db.addUser(user);
						} else {
							sendMessage(new Message(false, "User does not exist!", Message.LOGIN_RESPONSE));
						}
						break;
					}
					case Message.LOGOUT: {
						scoreList.logout(this);
//						if (gameStarted) {
//							sendMessage(new Message(Message.SERVER_LOGOUT));
//							//fix this
//						}
						break;
					}
					case Message.START_GAME: {
						playing = true;
						deathTime = 0L;
						startTime = System.currentTimeMillis();
						break;
					}
					case Message.END_GAME: {
						playing = false;
						deathTime = System.currentTimeMillis();
						if (getIntScore() > m.user.getIntHighscore()) {
							db.updateHighscore(m.user, getIntScore());
							sendMessage(new Message(getIntScore(), Message.END_RESPONSE));
						}
						break;
					}

					default:
						System.out.println("error: message has no type");
						break;
					}
				}
			} catch (IOException ioe) {
				System.out.println("ioe (on purpose to exit loop): " + ioe.getMessage());
			} catch (ClassNotFoundException cnfe) {
				System.out.println("cnfe: " + cnfe.getMessage());
			} finally {
				killThread();
			}

		}
		
		private void killThread() {
			try {
				scoreList.logout(this);
				if (oos != null)
					oos.close();
				if (ois != null)
					ois.close();
				if (s != null)
					s.close();
				if(user != null)
					System.out.println("killing LoggedInPlayer: " + user.getUsername());
				else
					System.out.println("killing LoggedInPlayer, username unknown");
			} catch (IOException ioe) {
				System.out.println("ioe: " + ioe.getMessage());
			}
		}
		
		public void kill() {
			alive = false;
		}

		public void sendMessage(Message m) {
			try {
				if(m.type != Message.HIGHSCORE)
					System.out.println("telling client: " + m.type + " " + m.info);
				if (playing)
					oos.reset();
				oos.writeObject(m);
				oos.flush();

			} catch (IOException ioe) {
				System.out.println("ioe in sendMessage: " + ioe.getMessage());
			}
		}
		
		public boolean isPlaying() {
			return playing;
		}
		
		public User getUser() {
			return user;
		}
		
		public int getIntScore() {
			return (int)((deathTime - startTime) / 1000);
		}
		
		public long getScore(long currentTime) {
			if(playing)
				return currentTime - startTime;
			return deathTime - startTime;
		}
	}

}
