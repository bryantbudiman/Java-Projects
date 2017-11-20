package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import Util.AppearanceConstants;
import Util.AppearanceSettings;
import client.GamePlayManager;
import client.User;
import server.Message;

public class GamePlayGUI extends JFrame {

	public static final long serialVersionUID = 1;
	
	private JMenu menu;
	private JMenuItem logout;
	private JMenuItem restart;
	private GamePlayPanel gamePlayPanel;
	private HighScorePanel highScorePanel; // high score list
	private JPanel timerPanel;
	private JLabel timerLabel; // the actual timer that is shown 
	private GamePlayManager gamePlayManager;
	private User loggedInUser;
	private long guestStartTime;
	private long guestEndTime;
	
	
	public GamePlayGUI(User user) {
		super("Bubblr Game Play");
		guestStartTime = 0L;
		guestEndTime = -1L;
		loggedInUser = user;
		gamePlayManager = new GamePlayManager(loggedInUser, this);
		if(loggedInUser.client != null) loggedInUser.client.setGamePlayGUI(this);
		initializeVariable();
		createGUI();	
		addListener();
	}
	
	public void initializeVariable() {
		// menu
		JMenuBar menuBar = new JMenuBar();
		menu = new JMenu("Menu");
		logout = new JMenuItem("Logout");
		restart = new JMenuItem("Restart");
		menu.add(logout);
		menu.add(restart);
		menuBar.add(menu);
		setJMenuBar(menuBar);
		AppearanceSettings.setFont(AppearanceConstants.fontSmallest, logout, restart);
		AppearanceSettings.setFont(AppearanceConstants.fontSmall, menu);
		
		//Create the gamePlayPanel
		gamePlayPanel = new GamePlayPanel(loggedInUser);
		//Create the highScorePanel 
		if(!loggedInUser.isGuest()) highScorePanel = new HighScorePanel(loggedInUser);
		
		//Now add the gamePlayPanel and leftPanel to the gameManager
		gamePlayManager.setGamePlayPanel(this.gamePlayPanel);
		gamePlayManager.setHighScorePanel(this.highScorePanel);
		
		highScorePanel = new HighScorePanel(loggedInUser);
		
		timerPanel = new JPanel();
		timerPanel.setSize(200, 900);
		timerLabel = new JLabel("0");
		JLabel currentTimeIs = new JLabel(" Score:");
		currentTimeIs.setHorizontalAlignment(JLabel.CENTER);
		timerPanel.setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints = new GridBagConstraints();   
	    gridBagConstraints.anchor = GridBagConstraints.SOUTH;
	    gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        timerPanel.add(currentTimeIs, gridBagConstraints);
	    gridBagConstraints.anchor = GridBagConstraints.NORTH;	   
        gridBagConstraints.gridy = 1;
        timerPanel.add(timerLabel, gridBagConstraints);
        AppearanceSettings.setBackground(Color.GRAY, timerPanel);
        AppearanceSettings.setForeground(Color.white, timerLabel, currentTimeIs);
  
		AppearanceSettings.setFont(AppearanceConstants.fontSmallest, logout, restart);
		AppearanceSettings.setFont(AppearanceConstants.fontSmall, menu);
		AppearanceSettings.setFont(AppearanceConstants.fontLarge, timerLabel, currentTimeIs);
	}	
	
	public void createGUI() {
		setSize(930, 900);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		if(!loggedInUser.isGuest()) {
			setSize(1300, 900);
			add(highScorePanel, BorderLayout.WEST);
		}
		add(gamePlayPanel, BorderLayout.CENTER);
		add(timerPanel, BorderLayout.EAST);
	}
	
	public void addListener() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		logout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new BubbLogin().setVisible(true);
				gamePlayManager.stopAnimation();
				loggedInUser.client.sendMessage(new Message(Message.LOGOUT));
				dispose();
			}
			
		});
		
		restart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (loggedInUser.isGuest()) {
					new BubbLogin().setVisible(true);
				} else {
					new BubbProfile(loggedInUser).setVisible(true);
				}
				gamePlayManager.stopAnimation();
				dispose();
			}
			
		});
		// If player is a guest, then the logout menu will be not used 
		if(loggedInUser.getUsername().equals("guest")) {
			logout.setEnabled(false);
			logout.setVisible(false);
		}
		
		this.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent ke) {
				if(ke.getKeyCode() == KeyEvent.VK_LEFT){
					gamePlayManager.moveLeft();
				}else if(ke.getKeyCode() == KeyEvent.VK_RIGHT){
					gamePlayManager.moveRight();
				}else if(ke.getKeyCode() == KeyEvent.VK_UP){
					gamePlayManager.moveUp();
				}
				else if(ke.getKeyCode() == KeyEvent.VK_DOWN){
					gamePlayManager.moveDown();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {}
			
		});
		
		this.setFocusable(true);
	}
	
	public void updateTimer(int newTime) {
		if(timerLabel != null) timerLabel.setText(Integer.toString(newTime));
	}
	
	public void updateStartTime() {
		guestStartTime = System.currentTimeMillis();
	}
	
	public void updateEndTime() {
		guestEndTime = System.currentTimeMillis();
		int newTime = (int)((guestEndTime - guestStartTime) / 1000);
		updateTimer(newTime);
	}
	
	public void updateHighscores(Vector<String> hs) {
		if(highScorePanel != null)
			highScorePanel.updateHighscores(hs);
	}
	
}