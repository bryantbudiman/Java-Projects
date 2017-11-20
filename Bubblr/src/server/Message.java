package server;

import java.io.Serializable;
import java.util.Vector;

import client.User;

public class Message implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final int SERVER_LOGOUT = -2;
	public static final int LOGOUT = -1;
	public static final int USER_LOGIN = 0;
	public static final int LOGIN_RESPONSE = 1;
	public static final int START_GAME = 5;
	public static final int END_GAME = 6;
	public static final int END_RESPONSE = 7;
	public static final int HIGHSCORE = 10;
	
	public int type;
	public String info;
	public User user;
	public boolean bool;
	public Vector<String> highScoreList;
	public int score;
	
	public Message(int type) {
		this.type = type;
	}
	
	public Message(boolean bool, int type) {
		this.bool = bool;
		this.type = type;
	}
	
	public Message(String info, int type) {
		this.type = type;
		this.info = info;
	}
	
	public Message(int score, int type) {
		this.type = type;
		this.score = score;
	}
	
	public Message(boolean bool, User user, int type) {
		this.type = type;
		this.user = user;
		this.bool = bool;
	}
	
	public Message(boolean bool, String info, int type) {
		this.type = type;
		this.info = info;
		this.bool = bool;
	}
	
	public Message(User user, int type) {
		this.type = type;
		this.user = user;
	}
	
	public Message(Vector<String> highScoreList, int score) {
		type = HIGHSCORE;
		this.highScoreList = highScoreList;
		this.score = score;
	}
	
}
