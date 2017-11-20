package server;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import com.mysql.jdbc.Driver;

import client.User;



public class MySQLDriver {
	private Connection con;
	
	private final static String selectName = "SELECT * FROM users WHERE username=?";
	private final static String addUser = "INSERT INTO users (username, password, highscore) VALUES(?, ?, ?)";
	private final static String updateHighscore = "UPDATE users SET highscore=? WHERE username=?";
	private final static String getHighscore = "SELECT highscore FROM users WHERE username=?";
	
	public MySQLDriver() {
		try {
			new Driver();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void connect() {
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bubblr?user=root&password=root");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		try {
			con.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getScore(User checkUser){
		int score=-1;
		try{
			PreparedStatement ps = con.prepareStatement(getHighscore);
			ps.setString(1, checkUser.getUsername());
			ResultSet result = ps.executeQuery();
			while(result.next()) {
				score = result.getInt(1);
			}
		} catch (SQLException e){
			e.printStackTrace();
			return -1;
		}
		return score;
	}
	
	public boolean userExists(User checkUser) {
		try {
			PreparedStatement ps = con.prepareStatement(selectName);
			ps.setString(1, checkUser.getUsername());
			ResultSet result = ps.executeQuery();
			
			if (!result.isBeforeFirst() ) {
				//query contains no result
				return false;
			}
			else {
				//query contains a user
				return true;
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean correctPassword(User checkUser) {
		try {
			PreparedStatement ps = con.prepareStatement(selectName);
			ps.setString(1, checkUser.getUsername());
			ResultSet result = ps.executeQuery();
			while(result.next()) {
				if (checkUser.getUsername().equals(result.getString(1)) && checkUser.getPassword().equals(result.getString(2))) {
					return true;
				}
			}
			return false;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void addUser (User newUser){
		try{
			PreparedStatement ps = con.prepareStatement(addUser);
			ps.setString(1,newUser.getUsername() ); //set username from login screen
			ps.setString(2,  newUser.getPassword()); //set password from login screen
			ps.setInt(3,  0); //set high score at zero because new user has no previous high score
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateHighscore(User user, int highscore){
		try{
			PreparedStatement ps = con.prepareStatement(updateHighscore);
			ps.setInt(1, highscore);
			ps.setString(2, user.getUsername());
			ps.executeUpdate();
		} catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	 
}

