package frames;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

import animation.ClockAnimation;
import animation.CountDownAnimation;
import game_logic.Category;
import game_logic.GameData;
import game_logic.ServerGameData;
import game_logic.User;
import listeners.ExitWindowListener;
import messages.QuestionClickedMessage;
import messages.ResetBuzzedInMessage;
import messages.ResetGameTimerMessage;
import messages.StartGameTimerMessage;
import other_gui.AppearanceConstants;
import other_gui.AppearanceSettings;
import other_gui.FinalJeopardyGUI;
import other_gui.QuestionGUIElement;
import other_gui.QuestionGUIElementNetworked;
import other_gui.TeamGUIComponents;
import server.Client;

public class MainGUI extends JFrame {

	private static final long serialVersionUID = 88L;
	private JPanel mainPanel;
	private JPanel currentPanel;

	private JPanel questionsPanel;
	protected GameData gameData;
	protected JButton[][] questionButtons;

	protected static final int QUESTIONS_LENGTH_AND_WIDTH = 5;

	private JTextArea updatesTextArea;
	protected JMenuBar menuBar;
	protected JMenu menu;

	protected JMenuItem logoutButton;
	protected JMenuItem exitButton;
	protected JMenuItem restartThisGameButton;
	protected JMenuItem chooseNewGameFileButton;
	//in case we need to know which user is logged in
	protected User loggedInUser;
	
	protected ClockAnimation clockAnimation = new ClockAnimation();
	protected CountDownAnimation countDownAnimation = new CountDownAnimation(15, false);
	
	//protected Timer clockTimer = new Timer(1000, new ClockAnimationListener());

	protected int currentSecond = 0;
	protected int totalSecond; // this varies depending on the situation 
	
	private boolean networked = false; 
	
	protected JPanel[] clockAnimationPanels;
	protected JPanel[] clockAnimationCardLayoutPanels;
	protected JPanel[] clockAnimationHiderPanels;
	
	final static protected String HIDECLOCK = "Hide clock";
	final static protected String SHOWCLOCK = "Show clock";
	
	protected Client client;
	//has a networked game data
	protected ServerGameData serverGameData;
	protected int pointValue; 
	
	protected boolean questionOpened = false; 
	protected boolean buzzIn = false; 
	protected int buzzInCount = 0;

	public MainGUI(GameData gameData) {
		super("Jeopardy!!");
		make(gameData);
	}
	//called from constructor of MainGUINetworked
	public MainGUI(User loggedInUser){
		super("Jeopardy!!  "+loggedInUser.getUsername());
		networked = true; 
	}
	
	//move constructor logic to a protected method so MainGUINetworked can call it
	protected void make(GameData gameData){
		this.gameData = gameData;
		initializeComponents();
		createGUI();
		addListeners();
	}

	// public methods
	public void addUpdate(String update) {
		updatesTextArea.append("\n" + update);
	}

	// this method changes the current panel to the provided panel
	public void changePanel(JPanel panel) {
		remove(currentPanel);
		currentPanel = panel;
		add(currentPanel, BorderLayout.CENTER);
		// must repaint or the change won't show
		repaint();
		revalidate();
	}

	public void showMainPanel() {
		changePanel(mainPanel);
	}

	// private methods
	private void initializeComponents() {
		mainPanel = new JPanel();
		currentPanel = mainPanel;
		exitButton = new JMenuItem("Exit Game");
		restartThisGameButton = new JMenuItem("Restart This Game");
		chooseNewGameFileButton = new JMenuItem("Choose New Game File");
		logoutButton = new JMenuItem("Logout");
		updatesTextArea = new JTextArea("Welcome to Jeopardy!");
		menu = new JMenu("Menu");
		questionButtons = new JButton[QUESTIONS_LENGTH_AND_WIDTH][QUESTIONS_LENGTH_AND_WIDTH];
		menuBar = new JMenuBar();
		questionsPanel = new JPanel(new GridLayout(QUESTIONS_LENGTH_AND_WIDTH, QUESTIONS_LENGTH_AND_WIDTH));
	}
	
	private void createGUI() {
		createMenu();
		createMainPanel();

		add(mainPanel, BorderLayout.CENTER);
		add(createProgressPanel(), BorderLayout.EAST);
		setSize(1500, 825);
	}

	// creating the JMenuBar
	protected void createMenu() {
		menu.add(restartThisGameButton);
		menu.add(chooseNewGameFileButton);
		menu.add(logoutButton);
		menu.add(exitButton);
		menuBar.add(menu);
		setJMenuBar(menuBar);
	}

	// creating the main panel (the game board)
	private void createMainPanel() {
		mainPanel.setLayout(new BorderLayout());

		// getting the panel that holds the 'jeopardy' label
		JPanel jeopardyPanel = createJeopardyPanel();
		// getting the cateogries panel
		JPanel categoriesPanel = createCategoriesAndQuestionsPanels();
		JPanel northPanel = new JPanel();

		northPanel.setLayout(new BorderLayout());
		northPanel.add(jeopardyPanel, BorderLayout.NORTH);
		northPanel.add(categoriesPanel, BorderLayout.SOUTH);

		mainPanel.add(northPanel, BorderLayout.NORTH);
		mainPanel.add(questionsPanel, BorderLayout.CENTER);
	}

	// creates the panel with the jeopardy label
	private JPanel createJeopardyPanel() {
		JPanel jeopardyPanel = new JPanel();
		JLabel jeopardyLabel = new JLabel("Jeopardy");
		AppearanceSettings.setBackground(AppearanceConstants.lightBlue, jeopardyPanel, jeopardyLabel,
				countDownAnimation);
		jeopardyLabel.setFont(AppearanceConstants.fontLarge);
		jeopardyPanel.add(jeopardyLabel);
		if(networked) jeopardyPanel.add(countDownAnimation);
		renderCountDownAnimation();
		return jeopardyPanel;
	}
	
	private void renderCountDownAnimation() {
		countDownAnimation.setPreferredSize(countDownAnimation.getPreferredSize());
		countDownAnimation.setVisible(true);
	}
	
	private void renderClockAnimation() {
		clockAnimation.setPreferredSize(clockAnimation.getPreferredSize());
		clockAnimation.setVisible(true);
	}
	
	public void moveClockAnimation(int teamNumber) {
		CardLayout cl = (CardLayout)(clockAnimationCardLayoutPanels[teamNumber].getLayout());
		cl.show(clockAnimationCardLayoutPanels[teamNumber], SHOWCLOCK);
		
		GridBagConstraints gbc = new GridBagConstraints();
		clockAnimationPanels[teamNumber].add(clockAnimation, gbc);
	}
	
	public void hideClocks() {
		for(int i = 0; i < gameData.getNumberOfTeams(); i++) {
			CardLayout cl = (CardLayout)(clockAnimationCardLayoutPanels[i].getLayout());
			cl.show(clockAnimationCardLayoutPanels[i], HIDECLOCK);
		}
	}
	
	public void questionPicked() {
		questionOpened = true; 
		
		client.sendMessage(new ResetGameTimerMessage());//clockTimer.stop();
		
		clockAnimation.resetCurrentFrame();
		clockAnimation.repaint();
		countDownAnimation.setCurrentSecond(20);
		countDownAnimation.repaint();
		
		totalSecond = 20;
		client.sendMessage(new StartGameTimerMessage());//clockTimer.start();
	}
	
	public void setBuzzIn(boolean bool) {
		buzzIn = bool; 
	}
	
	//we want to override this in MainGUINetworked which is why I pulled the logic out into its own method
	protected void populateQuestionButtons(){
		// place the question buttons in the proper indices in 'questionButtons'
		for (QuestionGUIElement question : gameData.getQuestions()) {

			// adding action listeners to the question
			question.addActionListeners(this, gameData);
			questionButtons[question.getX()][question.getY()] = question.getGameBoardButton();
		}

	}
	
	public void setTotalSecond(int totalSecond) {
		this.totalSecond = totalSecond; 
	}
	
	public ClockAnimation getClockAnimation() {
		return this.clockAnimation;
	}
	
	public CountDownAnimation getCountDownAnimation() {
		return this.countDownAnimation;
	}

	// creates both the categories panel and the questions panel
	private JPanel createCategoriesAndQuestionsPanels() {
		JPanel categoriesPanel = new JPanel(new GridLayout(1, QUESTIONS_LENGTH_AND_WIDTH));
		AppearanceSettings.setBackground(Color.darkGray, categoriesPanel, questionsPanel);

		Map<String, Category> categories = gameData.getCategories();
		JLabel[] categoryLabels = new JLabel[QUESTIONS_LENGTH_AND_WIDTH];

		// iterate through the map of categories, and place them in the correct index
		for (Map.Entry<String, Category> category : categories.entrySet()) {
			categoryLabels[category.getValue().getIndex()] = category.getValue().getCategoryLabel();
		}

		populateQuestionButtons();
		// actually adding the categories and questions into their respective panels
		for (int i = 0; i < QUESTIONS_LENGTH_AND_WIDTH; i++) {

			categoryLabels[i].setPreferredSize(new Dimension(100, 70));
			categoryLabels[i].setIcon(Category.getIcon());
			categoriesPanel.add(categoryLabels[i]);

			for (int j = 0; j < QUESTIONS_LENGTH_AND_WIDTH; j++) {
				// have to use opposite indices because of how GridLayout adds components
				questionsPanel.add(questionButtons[j][i]);
			}
		}

		return categoriesPanel;
	}

	// creates the panel with the team points, and the Game Progress area
	private JPanel createProgressPanel() {
		// create panels
		JPanel pointsPanel;
		if(networked == true) {
			pointsPanel = new JPanel(new GridLayout(gameData.getNumberOfTeams(), 3));
		}
		else {
			pointsPanel = new JPanel(new GridLayout(gameData.getNumberOfTeams(), 2));
		}
		JPanel southEastPanel = new JPanel(new BorderLayout());
		JPanel eastPanel = new JPanel();
		// other local variables
		JLabel updatesLabel = new JLabel("Game Progress"); 
		JLabel teamName = null;
		if(networked == true) {
			teamName = new JLabel("        " + client.getTeamName());
			teamName.setFont(AppearanceConstants.fontSmall);
			AppearanceSettings.setBackground(AppearanceConstants.lightBlue, teamName);
		}
		JScrollPane updatesScrollPane = new JScrollPane(updatesTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		// setting appearances
		AppearanceSettings.setBackground(AppearanceConstants.lightBlue, southEastPanel, updatesLabel, updatesScrollPane,
				updatesTextArea);
		AppearanceSettings.setSize(400, 400, pointsPanel, updatesScrollPane);
		AppearanceSettings.setTextComponents(updatesTextArea);

		updatesLabel.setFont(AppearanceConstants.fontLarge);
		pointsPanel.setBackground(Color.darkGray);
		if(networked == false) updatesLabel.setBorder(BorderFactory.createLineBorder(Color.darkGray));
		updatesScrollPane.setBorder(null);

		updatesTextArea.setText("Welcome to Jeopardy!");
		updatesTextArea.setFont(AppearanceConstants.fontSmall);
		updatesTextArea.append("The team to go first will be " + gameData.getCurrentTeam().getTeamName());

		eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.PAGE_AXIS));
		JPanel temp = null;
		if(networked == true) {
			temp = new JPanel();
			AppearanceSettings.setBackground(AppearanceConstants.lightBlue, temp);
			temp.setBorder(BorderFactory.createLineBorder(Color.darkGray));
			temp.add(updatesLabel);
			temp.add(teamName);
		}
		// adding components/containers}
		if(networked == true) {
			southEastPanel.add(temp, BorderLayout.NORTH);
		}
		else {
			southEastPanel.add(updatesLabel, BorderLayout.NORTH);
		}
		southEastPanel.add(updatesScrollPane, BorderLayout.CENTER);

		clockAnimationPanels = new JPanel[gameData.getNumberOfTeams()];
		clockAnimationCardLayoutPanels = new JPanel[gameData.getNumberOfTeams()];
		clockAnimationHiderPanels = new JPanel[gameData.getNumberOfTeams()];
		
		// adding team labels, which are stored in the TeamGUIComponents class,
		// to the appropriate panel
		for (int i = 0; i < gameData.getNumberOfTeams(); i++) {
			TeamGUIComponents team = gameData.getTeamDataList().get(i);
			if(networked == true) {
				clockAnimationPanels[i] = new JPanel(new GridBagLayout());
				clockAnimationPanels[i].setBackground(Color.darkGray);
				
				clockAnimationHiderPanels[i] = new JPanel();
				clockAnimationHiderPanels[i].setBackground(Color.darkGray);
				clockAnimationHiderPanels[i].add(new JLabel("     "));
				
				clockAnimationCardLayoutPanels[i] = new JPanel(new CardLayout());
				clockAnimationCardLayoutPanels[i].setBackground(Color.darkGray);
				clockAnimationCardLayoutPanels[i].add(clockAnimationPanels[i], SHOWCLOCK);
				clockAnimationCardLayoutPanels[i].add(clockAnimationHiderPanels[i], HIDECLOCK);
				
				CardLayout cl = (CardLayout)(clockAnimationCardLayoutPanels[i].getLayout());
				cl.show(clockAnimationCardLayoutPanels[i], HIDECLOCK);
			}
			pointsPanel.add(team.getMainTeamNameLabel());
			if(networked == true) pointsPanel.add(clockAnimationCardLayoutPanels[i]);
			pointsPanel.add(team.getTotalPointsLabel());
		}
		
		if(networked == true) {
			renderClockAnimation();
			GridBagConstraints gbc = new GridBagConstraints();
			clockAnimationPanels[gameData.getWhoseTurn()].add(this.clockAnimation, gbc);	
		}
		
		eastPanel.add(pointsPanel);
		eastPanel.add(southEastPanel);

		return eastPanel;
	}
	
	public void resetGame(){
		updatesTextArea.setText("Game has been restarted.");
		gameData.restartGame();
		// reset all data
		// repaint the board to show updated data
		showMainPanel();
		addUpdate("The team to go first will be " + gameData.getCurrentTeam().getTeamName());
	}
	
	// adding even listeners, made this protected so MainGUINetworked can override it
	protected void addListeners() {

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		//add window listener
		addWindowListener(new ExitWindowListener(this));
		//add action listeners
		exitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				System.exit(0);
			}

		});

		restartThisGameButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				resetGame();
			}

		});

		chooseNewGameFileButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				new StartWindowGUI(loggedInUser).setVisible(true);
			}

		});
		
		logoutButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				new LoginGUI().setVisible(true);
			}
			
		});
	}
	
	private void repaintCountDownAnimation() {
		countDownAnimation.repaint();
	}
	
	private void repaintClockAnimation() {
		clockAnimation.repaint();
	}
	
	public void buzzedIn() {
		clockAnimation.resetCurrentFrame();
		countDownAnimation.setCurrentSecond(20);
		buzzIn = false; 
		repaintClockAnimation();
    	repaintCountDownAnimation();
		QuestionGUIElementNetworked currentQuestion = client.getCurrentQuestion();
		currentQuestion.getCountDownAnimation().setCurrentSecond(20);
		currentQuestion.getCountDownAnimation().repaint();
		currentQuestion.getClockAnimation().resetCurrentFrame();
		currentQuestion.getClockAnimation().repaint();
	}
	
	private void checkReadyForFinalJeopardy(){
		//have all the questions been asked? if so, time for final jeopardy
		if (gameData.readyForFinalJeoaprdy()){
			//in case we are playing a Quick Play game, disable remaining buttons
			gameData.disableRemainingButtons();
			addUpdate("It's time for Final Jeopardy!");
			//figure out the teams that qualified for final jeopardy
			gameData.determineFinalists();
			//if there are no qualifying teams, pop up a WinnersGUI (showing no one won)
			if (gameData.getFinalistsAndEliminatedTeams().getFinalists().size() == 0){
				showMainPanel();
				new WinnersAndRatingGUI(gameData).setVisible(true);
			}
			//if there are final teams, change the current panel to the final jeopardy view
			else{
				changePanel(new FinalJeopardyGUI(gameData, this));
			}
		}
		//not ready, go back to main GUI
		else{
			buzzIn = false; 
			showMainPanel();
			totalSecond = 15; 
			this.currentSecond = 0;
			questionOpened = false; 
			
			clockAnimation.resetCurrentFrame();
			countDownAnimation.setCurrentSecond(15);

			CardLayout cl = (CardLayout)(clockAnimationCardLayoutPanels[gameData.getWhoseTurn()].getLayout());
			cl.show(clockAnimationCardLayoutPanels[gameData.getWhoseTurn()], SHOWCLOCK);
			
			GridBagConstraints gbc = new GridBagConstraints();
			clockAnimationPanels[gameData.getWhoseTurn()].add(clockAnimation, gbc);
			
			addUpdate("Now it's team " + gameData.getCurrentTeam().getTeamName() + "'s turn to choose a question."); 
			
			client.sendMessage(new StartGameTimerMessage());
		}
	}
	
	public synchronized void updateCurrentSecond(int currentSecond) {
		
		this.currentSecond = currentSecond;
		
		if (currentSecond < totalSecond - 1)  {
    		clockAnimation.incrementCurrentFrame();
    		countDownAnimation.decrementCurrentSecond();
    		
    		if(questionOpened == true) {
        		QuestionGUIElementNetworked currentQuestion = client.getCurrentQuestion();
        		if(currentQuestion != null) {
        			currentQuestion.getClockAnimation().incrementCurrentFrame();
        			currentQuestion.getCountDownAnimation().decrementCurrentSecond();
        			currentQuestion.getFishAnimation().incrementCurrentFrame();
        		}
    		}
    		
    		currentSecond++;
		}
		else if (currentSecond == totalSecond - 1){
			client.sendMessage(new ResetGameTimerMessage());//clockTimer.stop();
			clockAnimation.resetCurrentFrame();
			clockAnimation.repaint();
			currentSecond = 0;
			
			if(totalSecond == 15) {    				
				CardLayout cl = (CardLayout)(clockAnimationCardLayoutPanels[gameData.getWhoseTurn()].getLayout());
				cl.show(clockAnimationCardLayoutPanels[gameData.getWhoseTurn()], HIDECLOCK);
				
				addUpdate("Team " + gameData.getCurrentTeam().getTeamName() + " did not "
						+ "choose a question in time");
				
				gameData.nextTurn();
				
				if (gameData.getCurrentTeam().getTeamIndex() == client.getTeamIndex()) {
					for (QuestionGUIElement question : gameData.getQuestions()){
						if (!question.isAsked()){
							question.getGameBoardButton().setEnabled(true);
						}
						else {							
							question.getGameBoardButton().setEnabled(false);
						}
					}
				}
				else {
					for (QuestionGUIElement question : gameData.getQuestions()){						
						question.getGameBoardButton().setEnabled(false);
					}
				}
				
				CardLayout cl2 = (CardLayout)(clockAnimationCardLayoutPanels[gameData.getWhoseTurn()].getLayout());
				cl2.show(clockAnimationCardLayoutPanels[gameData.getWhoseTurn()], SHOWCLOCK);
				 				
				addUpdate("Now it's team " + gameData.getCurrentTeam().getTeamName() + "'s turn to choose"
						+ " a question.");
				
				totalSecond = 15;
				clockAnimation.resetCurrentFrame();
				countDownAnimation.setCurrentSecond(15);

				GridBagConstraints gbc = new GridBagConstraints();
				clockAnimationPanels[gameData.getWhoseTurn()].add(clockAnimation, gbc);
				
				client.sendMessage(new StartGameTimerMessage());
			}
			else if (totalSecond == 20 && buzzIn == false) { // waiting for team to answer before buzz in 
				buzzIn = true;
				client.sendMessage(new ResetGameTimerMessage());
				
				
				client.getCurrentQuestion().getCurrentTeam().deductPoints(client.getCurrentQuestion().getPointValue());
				addUpdate("Team " + client.getCurrentQuestion().getCurrentTeam().getTeamName() + " didn't answer in time! "
						+ client.getCurrentQuestion().getPointValue() + " will be deducted.");
				
				if (client.getCurrentQuestion().questionDone()){
					client.sendMessage(new ResetGameTimerMessage());
					//set the next turn to be the team in clockwise order from original team, add an update, and see whether it is time for FJ
					gameData.setNextTurn(gameData.nextTurn(client.getCurrentQuestion().getOriginalTeam()));
					addUpdate("No one answered the question correctly. The correct answer was: "+client.getCurrentQuestion().getAnswer());
					checkReadyForFinalJeopardy();
				}
				else {
					totalSecond = 20;
					clockAnimation.resetCurrentFrame();
					countDownAnimation.setCurrentSecond(20);
					
					QuestionGUIElementNetworked currentQuestion = client.getCurrentQuestion();
					currentQuestion.getCountDownAnimation().setCurrentSecond(20);
					currentQuestion.getClockAnimation().resetCurrentFrame();
					
					this.hideClocks();
					client.getCurrentQuestion().setBuzzInPeriod(gameData.getCurrentTeam().getTeamIndex());
					client.sendMessage(new ResetBuzzedInMessage());
					client.sendMessage(new StartGameTimerMessage());
				}
			}
			else if (totalSecond == 20 && buzzIn == true) {
				client.sendMessage(new ResetGameTimerMessage());
				addUpdate("No teams had buzzed in, the correct answer was: " + client.getCurrentQuestion().getAnswer() 
						+ ". ");
				
				gameData.nextTurn();
				client.getCurrentQuestion().hideFishAnimation();
				checkReadyForFinalJeopardy();
			}
		}
    	
    	if(questionOpened == true) {
    		QuestionGUIElementNetworked currentQuestion = client.getCurrentQuestion();
    		if(currentQuestion != null) {
    			currentQuestion.repaintClockAnimation();
    			currentQuestion.repaintCountDownAnimation();
    			currentQuestion.repaintFishAnimation();
    		}
		}

    	repaintClockAnimation();
    	repaintCountDownAnimation();
	}
}