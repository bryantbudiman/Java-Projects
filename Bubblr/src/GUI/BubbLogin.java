package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import Util.AppearanceConstants;
import Util.AppearanceSettings;
import client.User;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;



public class BubbLogin extends JFrame{
	
	private static final long serialVersionUID = 1L;
	
	//private MySQLDriver db;
	private JButton loginButton, createAccount, guestButton;
	private JTextField username, password, ipaddress, port;
	private JLabel alertLabel;
	
	
	public BubbLogin(){
		super("Play Bubblr!");
		//db = new MySQLDriver();
		//db.connect();
		
		initializeComponents();
		createGUI();
		addListeners();
	}
	

	private void initializeComponents(){
		alertLabel = new JLabel();
		loginButton = new JButton("Login");
		createAccount = new JButton("Create Account");
		guestButton = new JButton("Play as Guest");
		username = new JTextField("username");
		password = new JTextField("password");
		ipaddress = new JTextField("ip address");
		port = new JTextField("port");	
		
	}
	
	private void createGUI(){
		
		JPanel mainPanel = new JPanel();
		JPanel textFieldOnePanel = new JPanel();
		JPanel textFieldTwoPanel = new JPanel();
		JPanel textFieldThreePanel = new JPanel();
		JLabel welcome = new JLabel("login, create an account, or play as a guest", JLabel.CENTER);
		JLabel bubblrLabel = new JLabel("Bubblr!", JLabel.CENTER);
		JPanel alertPanel = new JPanel();
		JPanel textFieldsPanel = new JPanel();
		JPanel buttonsPanel = new JPanel();
		JPanel welcomePanel = new JPanel(new GridLayout(2,1));
		
		
		//set mass component appearances
		//AppearanceSettings.setForeground(Color.lightGray, createAccount, loginButton, password, username);
	//	AppearanceSettings.setSize(300, 60, password, username);
		
		AppearanceSettings.setSize(340, 80, loginButton, createAccount, guestButton);
		AppearanceSettings.setBackground(AppearanceConstants.lightRed, loginButton, createAccount, guestButton);
		
		AppearanceSettings.setOpaque(loginButton, createAccount, guestButton);
		AppearanceSettings.unSetBorderOnButtons(loginButton, createAccount, guestButton);
		
		AppearanceSettings.setTextAlignment(welcome, bubblrLabel);
		AppearanceSettings.setFont(AppearanceConstants.fontSmall, alertLabel, password, username, ipaddress, port, guestButton, loginButton, createAccount);
		
		AppearanceSettings.setBackground(AppearanceConstants.lightGold, mainPanel,  alertPanel, textFieldsPanel, 
				buttonsPanel,  textFieldOnePanel, textFieldTwoPanel, textFieldThreePanel);
		
		AppearanceSettings.setBackground(AppearanceConstants.lightRed, welcome, welcomePanel, bubblrLabel);
		AppearanceSettings.setForeground(Color.white, bubblrLabel, welcome, loginButton, createAccount, guestButton);
		
		//other appearance settings
		welcome.setFont(AppearanceConstants.fontMedium);
		bubblrLabel.setFont(AppearanceConstants.fontHellaLarge);
		
		alertPanel.add(alertLabel);
		
		username.setPreferredSize(new Dimension(200, 40));
		password.setPreferredSize(new Dimension(200, 40));
		ipaddress.setPreferredSize(new Dimension(200, 40));
		port.setPreferredSize(new Dimension(200, 40));
		
		
		loginButton.setEnabled(false);
		//loginButton.setBackground(AppearanceConstants.mediumGray);
		//createAccount.setBackground(AppearanceConstants.mediumGray);
		createAccount.setEnabled(false);
		guestButton.setEnabled(true);
		//guestButton.setBackground(AppearanceConstants.mediumGray);
		
		//add components to containers
		welcomePanel.add(welcome);
		welcomePanel.add(bubblrLabel);
		
		//alertPanel.add(alertLabel);
		textFieldOnePanel.add(username);
		textFieldTwoPanel.add(password);
		textFieldThreePanel.add(ipaddress);
		textFieldThreePanel.add(port);
		
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.LINE_AXIS));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		
		//adds components to the panel with glue in between each
		AppearanceSettings.addGlue(buttonsPanel, BoxLayout.LINE_AXIS, true, loginButton, createAccount, guestButton);
		
		AppearanceSettings.addGlue(mainPanel, BoxLayout.PAGE_AXIS, false, welcomePanel);
		//don't want glue in between the following two panels, so they are not passed in to addGlue
		mainPanel.add(alertPanel);
		mainPanel.add(textFieldOnePanel);
		AppearanceSettings.addGlue(mainPanel, BoxLayout.PAGE_AXIS, false, textFieldTwoPanel, textFieldThreePanel);
		mainPanel.add(buttonsPanel);
		
		add(mainPanel, BorderLayout.CENTER);
		setSize(1100, 600);
		setLocationRelativeTo(null);
	}
	
	//returns whether the buttons should be enabled
	private boolean canPressButtons(){
		return (!username.getText().isEmpty() && !username.getText().equals("username") && 
				!password.getText().equals("password") && !password.getText().isEmpty() && 
				!ipaddress.getText().isEmpty() && !ipaddress.getText().equals("ip address") &&
				!port.getText().isEmpty() && !port.getText().equals("port"));
	}
	
	public void exitLogin(User user) { 
		new BubbProfile(user).setVisible(true);
		dispose();
	}
	
	public void alert(String info) {
		alertLabel.setText(info);
	}
	
	private void addListeners(){
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		//focus listeners
		username.addFocusListener(new TextFieldFocusListener("username", username));
		password.addFocusListener(new TextFieldFocusListener("password", password));
		ipaddress.addFocusListener(new TextFieldFocusListener("ip address", ipaddress));
		port.addFocusListener(new TextFieldFocusListener("port", port));
		//document listeners
		username.getDocument().addDocumentListener(new MyDocumentListener());
		password.getDocument().addDocumentListener(new MyDocumentListener());
		ipaddress.getDocument().addDocumentListener(new MyDocumentListener());
		port.getDocument().addDocumentListener(new MyDocumentListener());
		//action listeners
		loginButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				bloop();
				String usernameString = username.getText();
				String passwordString = password.getText();
				Integer portInt = Integer.parseInt(port.getText());
				User checkUser = null; 
				if (portInt > 0 && portInt < 65535) {
					if(usernameString.length() <= 15 || passwordString.length() <= 15) {
						checkUser = new User(usernameString, passwordString, -1, ipaddress.getText(), 
								portInt, false);
						checkUser.client.setBubbLogin(BubbLogin.this);
					}
					else {
						alertLabel.setText("Username or Password exceeds maximum characters allowed");
					}
				}
				else {
					alertLabel.setText("Port number invalid");
				}
				
			}
			
		});
		
		createAccount.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				bloop();
				String usernameString = username.getText();
				String passwordString = password.getText();
				Integer portInt = Integer.parseInt(port.getText());
				User checkUser = null; 
				if (portInt > 0 && portInt < 65535) {
					if(usernameString.length() <= 15 || passwordString.length() <= 15) {
						checkUser = new User(usernameString, passwordString, -1, ipaddress.getText(), 
								portInt, true);
						checkUser.client.setBubbLogin(BubbLogin.this);
					}
					else {
						alertLabel.setText("Username or Password exceeds maximum characters allowed");
					}
				}
				else {
					alertLabel.setText("Port number invalid");
				}
			}
			
		});
		
		guestButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				bloop();
				
				User checkUser = new User("guest", "", -1);
				
				new GamePlayGUI(checkUser).setVisible(true);
				dispose();
			}
			
		});
	}

	//sets the buttons enabled or disabled
	private class MyDocumentListener implements DocumentListener{
		
		@Override
		public void insertUpdate(DocumentEvent e) {
			createAccount.setEnabled(canPressButtons());
			loginButton.setEnabled(canPressButtons());
		}
		
		@Override
		public void removeUpdate(DocumentEvent e) {
			createAccount.setEnabled(canPressButtons());
			loginButton.setEnabled(canPressButtons());
		}
		
		@Override
		public void changedUpdate(DocumentEvent e) {
			createAccount.setEnabled(canPressButtons());
			loginButton.setEnabled(canPressButtons());
		}
	}
	
	//makes the bloop noise when you click a button
	public void bloop() {
        AudioStream BGM;

        try
        {
            InputStream test = new FileInputStream("img/bloop.wav");
            BGM = new AudioStream(test);
            AudioPlayer.player.start(BGM);

        }
        catch(FileNotFoundException e){
            System.out.print(e.toString());
        }
        catch(IOException error)
        {
            System.out.print(error.toString());
        }
	}
	
}