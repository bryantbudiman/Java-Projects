package client;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

//implements Serializable
public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String password;
	private String username;
	private boolean guest;
	private boolean newUser;
	private Image img;
	private int highscore;

	public transient Client client;
	
	public User(String username, String password, int highscore) {
		this.username = username;
		this.password = password;
		this.highscore = highscore;
		guest = true;
		try{
			img = ImageIO.read(new File("img/flame.png"));
		} catch (IOException ioe)
		{
			System.out.println("ioe");
		}
	}

	public User(String username, String password, int highscore, String ip, int port, boolean newUser) {
		this.username = username;
		this.password = password;
		this.highscore = highscore;
		guest = false;
		this.newUser = newUser;
		client = new Client(ip, port, this);
	}

	public boolean isGuest() {
		return guest;
	}
	
	public boolean isNewUser() {
		return newUser;
	}

	public String getHighscore() {
		return username + ": " + Integer.toString(highscore);
	}
	
	public int getIntHighscore() {
		return highscore;
	}
	
	public void setHighscore(int newHighscore) {
		highscore = newHighscore;
	}

	public String getUsername(){
		return username;
	}
	 
	public String getPassword(){
		return password;
	}

	public Image getImg() {
		return img;
	}

	public void setImg(Image img) {
		this.img = img;
	}

}