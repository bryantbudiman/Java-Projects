package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Util.AppearanceConstants;
import Util.AppearanceSettings;
import client.GamePlayManager;
import client.User;

public class GameOverPanel extends JFrame{

	private static final long serialVersionUID = 5898800981778898020L;
	private JPanel announcementPanel, buttonsPanel;
	private JLabel announcementLabel;
	private User user; 
	private JButton restartButton, exitButton; 
	private GamePlayManager gamePlayManager; 
	
	public GameOverPanel(User user, GamePlayManager gamePlayManager){
		this.user = user;
		this.gamePlayManager = gamePlayManager; 
		initializeComponents();
		createGUI();
		addListeners();
	}
	
	//private methods
	private void initializeComponents(){
		announcementPanel = new JPanel();
		announcementLabel = new JLabel("Sorry your bubble just got bRUINED!");
		buttonsPanel = new JPanel();
		restartButton = new JButton("Restart");
		exitButton = new JButton("Exit");
	}
	
	private void createGUI(){
		setSize(800, 300);
		setLocationRelativeTo(null);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		announcementPanel.setSize(800, 300);
		buttonsPanel.setSize(800, 300);
		AppearanceSettings.setFont(AppearanceConstants.fontMedium, announcementLabel);
		AppearanceSettings.setFont(AppearanceConstants.fontLarge, restartButton, exitButton);
		announcementLabel.setHorizontalAlignment(JLabel.CENTER);
		announcementLabel.setVerticalAlignment(JLabel.CENTER);
		AppearanceSettings.setBackground(AppearanceConstants.lightRed, announcementPanel);
		AppearanceSettings.setBackground(AppearanceConstants.lightGold, buttonsPanel);
		AppearanceSettings.setBackground(Color.GRAY, restartButton, exitButton);
		
		announcementPanel.setLayout(new BorderLayout());
		announcementPanel.add(announcementLabel, BorderLayout.CENTER);
		JPanel temp = new JPanel();
		temp.add(restartButton);
		temp.add(exitButton);
		AppearanceSettings.setBackground(AppearanceConstants.lightGold, temp);
		buttonsPanel.setLayout(new BorderLayout());
		buttonsPanel.add(temp, BorderLayout.CENTER);

		
		add(announcementPanel);
		add(buttonsPanel);
	}
	
	private void addListeners(){
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		restartButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(!user.isGuest()) {
					new BubbProfile(user).setVisible(true);
				}
				else {
					new BubbLogin().setVisible(true);
				}
				gamePlayManager.disposeGameGUI();
				dispose();
			}
			
		});
		
		exitButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
			
		});
	
	}
}