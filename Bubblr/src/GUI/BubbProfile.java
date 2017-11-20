package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import Util.AppearanceConstants;
import Util.AppearanceSettings;
import client.User;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

@SuppressWarnings("serial")
public class BubbProfile extends JFrame{
	
	private User user;
	
	private JLabel welcomeLabel;
	private JLabel highscoreMessageLabel; // message, your high score is
	private JLabel highscoreScoreLabel; //just the number of the high score
	private JLabel choosePictureLabel;
	private JButton logout, startgame;
	private JPanel mainPanel;
	private JPanel welcomePanel;
	private JPanel highscorePanel;
	private JPanel chooseIconPanel;
	private JPanel iconPanel;
	private JPanel buttonPanel;

	JRadioButton img1, img2, img3, img4, img5, img6;
	ImageIcon i1, i2, i3, i4, i5, i6;
	ButtonGroup buttonGroup;

	public BubbProfile(User u){
		super("Create your Bubblr profile!");
		user = u;
		
		initializeComponents();
		createGUI();
		addListeners();
	}
	
	private void initializeComponents(){
		String userString = user.getUsername();
		welcomeLabel = new JLabel("Welcome to Bubblr, "+ userString);
		highscoreMessageLabel = new JLabel("Your current high score is:");
		
		String scoreString = new String();
		if (user.getIntHighscore() == -1) {
			scoreString = "Score: 0";
		} else {
			scoreString = user.getHighscore();
		}
		
		
		highscoreScoreLabel = new JLabel(scoreString);
		choosePictureLabel = new JLabel("Please choose your bubble's background below");
		logout = new JButton("Logout");
		startgame = new JButton("Start Game");
		mainPanel = new JPanel();
		welcomePanel = new JPanel();
		highscorePanel = new JPanel(new BorderLayout());
		chooseIconPanel = new JPanel(new BorderLayout());
		iconPanel = new JPanel(new GridLayout(2, 3));
		buttonPanel = new JPanel();
		img1 = new JRadioButton();
		img2 = new JRadioButton();
		img3 = new JRadioButton();
		img4 = new JRadioButton();
		img5 = new JRadioButton();
		img6 = new JRadioButton();
		i1 = new ImageIcon("img/flame.png");
		i2 = new ImageIcon("img/genius.png");
		i3 = new ImageIcon("img/rainbow.png");
		i4 = new ImageIcon("img/bike.png");
		i5 = new ImageIcon("img/pencil.png");
		i6 = new ImageIcon("img/rocket.png");
		ImageIcon flame = new ImageIcon("img/flame1.png");
		img1.setIcon(flame);
		img2.setIcon(i2);
		img3.setIcon(i3);
		img4.setIcon(i4);
		img5.setIcon(i5);
		img6.setIcon(i6);
		buttonGroup = new ButtonGroup();
		
		add(img1);
		add(img2);
		add(img3);
		add(img4);
		add(img5);
		add(img6);
		
	}
	
	private void createGUI(){
		
		AppearanceSettings.setOpaque(logout, startgame);
		AppearanceSettings.unSetBorderOnButtons(logout, startgame);
		AppearanceSettings.setSize(290, 80, logout, startgame);
		AppearanceSettings.setBackground(AppearanceConstants.lightRed, logout, startgame);
	
		AppearanceSettings.setBackground(AppearanceConstants.lightGold, mainPanel, iconPanel, welcomePanel, highscorePanel, 
				chooseIconPanel,  buttonPanel, img1, img2, img3, img4, img5, img6);
		AppearanceSettings.setFont(AppearanceConstants.fontSmall, logout, startgame);
		AppearanceSettings.setFont(AppearanceConstants.fontMedium, highscoreMessageLabel, choosePictureLabel);
		AppearanceSettings.setFont(AppearanceConstants.fontHellaLarge, welcomeLabel); // hella funny
		AppearanceSettings.setFont(AppearanceConstants.fontHuge, highscoreScoreLabel);
		AppearanceSettings.setForeground(Color.white, logout, startgame);
		
		
		welcomePanel.add(welcomeLabel);
		
		
		highscoreMessageLabel.setHorizontalAlignment(JLabel.CENTER);
		highscoreScoreLabel.setHorizontalAlignment(JLabel.CENTER);
		highscorePanel.add(highscoreMessageLabel, BorderLayout.NORTH);
		highscorePanel.add(highscoreScoreLabel, BorderLayout.CENTER);
		
		choosePictureLabel.setHorizontalAlignment(JLabel.CENTER);
		img1.setHorizontalAlignment(JRadioButton.CENTER);
		img2.setHorizontalAlignment(JRadioButton.CENTER);
		img3.setHorizontalAlignment(JRadioButton.CENTER);
		img4.setHorizontalAlignment(JRadioButton.CENTER);
		img5.setHorizontalAlignment(JRadioButton.CENTER);
		img6.setHorizontalAlignment(JRadioButton.CENTER);
		iconPanel.add(img1);
		iconPanel.add(img2);
		iconPanel.add(img3);
		iconPanel.add(img4);
		iconPanel.add(img5);
		iconPanel.add(img6);
		
		img1.setSelected(true);
		
		chooseIconPanel.add(choosePictureLabel, BorderLayout.NORTH);
		chooseIconPanel.add(iconPanel, BorderLayout.CENTER);
		
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

		
		AppearanceSettings.addGlue(buttonPanel, BoxLayout.LINE_AXIS, true, logout, startgame);
		
		mainPanel.add(welcomePanel);
		mainPanel.add(highscorePanel);
		mainPanel.add(chooseIconPanel);
		mainPanel.add(buttonPanel);
		add(mainPanel, BorderLayout.CENTER);
		setSize(1200, 800);
		setLocationRelativeTo(null);
	}
	
	private void addListeners(){
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		logout.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				bloop();
				new BubbLogin().setVisible(true);
				user.client.killThread();
				dispose();
			}
		});
		
		
		img1.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				plink();
				System.out.println("1 WAS CLICKED");
				img1.setSelected(true);
				if(img1.isSelected())
				{
					System.out.println("1 WAS SELECTED");
					ImageIcon i = new ImageIcon("img/flame1.png");
					img1.setIcon(i);
					img2.setIcon(i2);
					img3.setIcon(i3);
					img4.setIcon(i4);
					img5.setIcon(i5);
					img6.setIcon(i6);
					//img1.repaint();
				}
				else{
				//	img1.setIcon(i1);
				}
			}
		});
		img2.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				plink();
				System.out.println("2 WAS CLICKED");
				img2.setSelected(true);
				if(img2.isSelected())
				{
					System.out.println("2 WAS SELECTED");
					ImageIcon i = new ImageIcon("img/genius1.png");
					img1.setIcon(i1);
					img2.setIcon(i);
					img3.setIcon(i3);
					img4.setIcon(i4);
					img5.setIcon(i5);
					img6.setIcon(i6);
				}
				else{
					//img2.setIcon(i2);
				}
			}
		});
		img3.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				plink();
				System.out.println("3 WAS CLICKED");
				img3.setSelected(true);
				if(img3.isSelected())
				{
					System.out.println("3 WAS SELECTED");
					ImageIcon i = new ImageIcon("img/rainbow1.png");
					img1.setIcon(i1);
					img2.setIcon(i2);
					img3.setIcon(i);
					img4.setIcon(i4);
					img5.setIcon(i5);
					img6.setIcon(i6);
				}
				else{
				//	img3.setIcon(i3);
				}
			}
		});
		img4.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				plink();
				System.out.println("4 WAS CLICKED");
				img4.setSelected(true);
				if(img4.isSelected())
				{
					System.out.println("4 WAS SELECTED");
					ImageIcon i = new ImageIcon("img/bike1.png");
					img1.setIcon(i1);
					img2.setIcon(i2);
					img3.setIcon(i3);
					img4.setIcon(i);
					img5.setIcon(i5);
					img6.setIcon(i6);
				}
				else{
					//img4.setIcon(i4);
				}
			}
		});
		img5.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				plink();
				System.out.println("5 WAS CLICKED");
				img5.setSelected(true);
				if(img5.isSelected())
				{
					System.out.println("5 WAS SELECTED");
					ImageIcon i = new ImageIcon("img/pencil1.png");
					img1.setIcon(i1);
					img2.setIcon(i2);
					img3.setIcon(i3);
					img4.setIcon(i4);
					img5.setIcon(i);
					img6.setIcon(i6);
				}
				else{
					//img5.setIcon(i5);
				}
			}
		});
		img6.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				plink();
				System.out.println("6 WAS CLICKED");
				img6.setSelected(true);
				if(img6.isSelected())
				{
					System.out.println("6 WAS SELECTED");
					ImageIcon i = new ImageIcon("img/rocket1.png");
					img1.setIcon(i1);
					img2.setIcon(i2);
					img3.setIcon(i3);
					img4.setIcon(i4);
					img5.setIcon(i5);
					img6.setIcon(i);
					
				}
				else{
					//img6.setIcon(i6);
				}
			}
		});
		
		startgame.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				bloop();
				String url = "";
				if(img1.isSelected()){
					url = "img/flame.png";
				} if(img2.isSelected()){
					url = "img/genius.png";
				}if(img3.isSelected()){
					url = "img/rainbow.png";
				} if(img4.isSelected()){
					url = "img/bike.png";
				}if(img5.isSelected()){
					url = "img/pencil.png";
				} if(img6.isSelected()){
					url = "img/rocket.png";
				}
				
				Image image = null;
				try{
					image = ImageIO.read(new File(url));
				} catch (IOException ioe)
				{
					System.out.println("ioe");
				}
				
				user.setImg(image);					
				new GamePlayGUI(user).setVisible(true);
				dispose();
			}
		});
			
	}
	
	//sound effect
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
	
	public void plink() {
        AudioStream BGM;

        try
        {
            InputStream test = new FileInputStream("img/plink.wav");
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
