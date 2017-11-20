package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import Util.AppearanceConstants;
import Util.AppearanceSettings;
import client.GameSimulation;
import client.User;

public class HighScorePanel extends JPanel{

	public static final long serialVersionUID = 8L;
	// dimension of the panel
	private int mWidth = 200;
	private int mHeight = 800;
	private JLabel highScore;
	private JPanel subPanel;
	private JPanel scorePanel;
//	private User loggedInUser;
	private GameSimulation gameSimulation;
	
	public HighScorePanel(User user) {
//		loggedInUser = user;
		initializeVariables();
		createGUI();
		addListener();
	}
	
	public void initializeVariables() {
		subPanel = new JPanel();
		highScore = new JLabel("<html>High<br>scores</html>");
		AppearanceSettings.setFont(AppearanceConstants.fontMedium, highScore);
	}
	
	public void createGUI() {
		setSize(200, 800);

		scorePanel = new JPanel();
		scorePanel.setLayout(new GridLayout(10,1));

		AppearanceSettings.setSize(200, 700, scorePanel);
		AppearanceSettings.setSize(200, 100, highScore);
		AppearanceSettings.setTextAlignment(highScore);
	
		AppearanceSettings.setBackground(AppearanceConstants.lightRed, highScore);
		AppearanceSettings.setForeground(Color.white, highScore);
		AppearanceSettings.setBackground(AppearanceConstants.lightGold, scorePanel);
		AppearanceSettings.setOpaque(highScore, scorePanel);
		
		setLayout(new BorderLayout());
		subPanel.add(scorePanel);
		
		add(highScore, BorderLayout.NORTH);
		add(subPanel, BorderLayout.CENTER);
	}
	
	public void render(GameSimulation gameSimulation) {
		this.gameSimulation = gameSimulation;
	}
	
	public void addListener() {
		
	}
	
	public int returnWidth() {
		return mWidth;
	}
	
	public int returnHeight() {
		return mHeight;
	}
	
	public void updateHighscores(Vector<String> hs) {
		scorePanel.removeAll();
		for(String str : hs) {
			scorePanel.add(new ScoreLabel(str));
		}
		scorePanel.revalidate();
	}
	
	private class ScoreLabel extends JLabel {
		private static final long serialVersionUID = 1L;

		public ScoreLabel(String str) {
			super(str, SwingConstants.CENTER);
			AppearanceSettings.setFont(AppearanceConstants.fontSmall, this);
			AppearanceSettings.setBackground(AppearanceConstants.lightGold, this);
		}
	}

}
