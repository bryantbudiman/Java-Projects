package other_gui;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import frames.MainGUI;
import game_logic.GameData;
import messages.BuzzInMessage;
import messages.QuestionAnsweredMessage;
import messages.QuestionClickedMessage;
import server.Client;

//inherits from QuestionGUIElement
public class QuestionGUIElementNetworked extends QuestionGUIElement {

	private Client client;
	//very similar variables as in the AnsweringLogic class
	public Boolean hadSecondChance;
	private TeamGUIComponents currentTeam;
	private TeamGUIComponents originalTeam;
	int teamIndex;
	int numTeams;
	//stores team index as the key to a Boolean indicating whether they have gotten a chance to answer the question
	private HashMap<Integer, Boolean> teamHasAnswered;
	
	public QuestionGUIElementNetworked(String question, String answer, String category, int pointValue, int indexX, int indexY) {
		super(question, answer, category, pointValue, indexX, indexY, true);
	}
	
	//set the client and also set the map with the booleans to all have false
	public void setClient(Client client, int numTeams){
		this.client = client;
		this.numTeams = numTeams;
		teamIndex = client.getTeamIndex();
		teamHasAnswered = new HashMap<>();
		for (int i = 0; i< numTeams; i++) teamHasAnswered.put(i, false);
	}
	
	//returns whether every team has had a chance at answering this question
	public Boolean questionDone(){
		Boolean questionDone = true;
		for (Boolean currentTeam : teamHasAnswered.values()) questionDone = questionDone && currentTeam;
		return questionDone;
	}
	
	public void repaintFishAnimation() {
		fishAnimation.repaint();
	}
	
	public void repaintCountDownAnimation() {
		countDownAnimation.repaint();
	}
	
	public void repaintClockAnimation() {
		clockAnimation.repaint();
	}
	
	
	//overrides the addActionListeners method in super class
	@Override
	public void addActionListeners(MainGUI mainGUI, GameData gameData){
		passButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				//send a buzz in message
				client.sendMessage(new BuzzInMessage(teamIndex));
			}
			
		});
		
		gameBoardButton.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//send a question clicked message
				client.sendMessage(new QuestionClickedMessage(getX(), getY()));
			}
		});
		
		submitAnswerButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//send question answered message
				client.sendMessage(new QuestionAnsweredMessage(answerField.getText()));
			}
			
		});
	}
	
	//override the resetQuestion method
	@Override
	public void resetQuestion(){
		super.resetQuestion();
		hadSecondChance = false;
		currentTeam = null;
		originalTeam = null;
		teamHasAnswered.clear();
		teamLabel2.setText("");
		//reset teamHasAnswered map so all teams get a chance to answer again
		for (int i = 0; i< numTeams; i++) teamHasAnswered.put(i, false);
	}
	
	@Override
	public void populate(){
		super.populate();
		passButton.setText("Buzz In!");
		// Modify the question GUI to include all the animations 
		addAnimations();
	}
	
	public int getOriginalTeam(){
		return originalTeam.getTeamIndex();
	}

	public void updateAnnouncements(String announcement){
		announcementsLabel.setText(announcement);
		announcementsLabel2.setText(announcement);
	}
	
	public void setOriginalTeam(int team, GameData gameData){
		originalTeam = gameData.getTeamDataList().get(team);
		teamLabel2.setText(gameData.getCurrentTeam().getTeamName());
		updateTeam(team, gameData);
	}
	
	//update the current team of this question
	public void updateTeam(int team, GameData gameData){
		currentTeam = gameData.getTeamDataList().get(team);
		passButton.setVisible(false);
		answerField.setText("");
		//if the current team is this client
		if (team == teamIndex){
			AppearanceSettings.setEnabled(true, submitAnswerButton, answerField);
			announcementsLabel.setText("Answer within 20 seconds!");
		}
		//if the current team is an opponent
		else{
			AppearanceSettings.setEnabled(false, submitAnswerButton, answerField);
			announcementsLabel.setText("It's "+currentTeam.getTeamName()+"'s turn to try to answer the question.");
			announcementsLabel2.setText(currentTeam.getTeamName() + " buzzed in to answer.");
			AppearanceSettings.setEnabled(false, submitAnswerButton, answerField);
		}
		//mark down that this team has had a chance to answer
		teamHasAnswered.replace(team, true);
		hadSecondChance = false;
		teamLabel.setText(currentTeam.getTeamName());
	}
	
	//called from QuestionAnswerAction when there is an ill formated answer
	public void illFormattedAnswer(){
		
		if (currentTeam.getTeamIndex() == teamIndex){
			announcementsLabel.setText("You had an illformatted answer. Please try again");
			announcementsLabel2.setText("You had an illformatted answer. Please try again");
		}
		else{
			announcementsLabel.setText(currentTeam.getTeamName()+" had an illformatted answer. They get to answer again.");
			announcementsLabel2.setText(currentTeam.getTeamName()+" had an illformatted answer. They get to answer again.");
		}
	}
	
	//set the gui to be a buzz in period, also called from QuestionAnswerAction
	public void setBuzzInPeriod(int whoseTurn){
		showClockPanel();
		passButton.setVisible(true);
		teamLabel.setText("");
		hideFishAnimation();
		
		if (teamHasAnswered.get(teamIndex)){
			AppearanceSettings.setEnabled(false, submitAnswerButton, answerField, passButton);
			if(client.getTeamIndex() == whoseTurn) {
				announcementsLabel.setText("It's time to buzz in! But you've already had your chance..");
				announcementsLabel2.setText("It's time to buzz in! But you've already had your chance..");
			}
			else {
				announcementsLabel.setText("You cannot buzz in again. Waiting for another team to buzz in.");
				announcementsLabel2.setText("You cannot buzz in again. Waiting for another team to buzz in.");
			}
		}
		else{
			announcementsLabel.setText("Buzz in to answer the question!");
			announcementsLabel2.setText("Buzz in to answer the question!");
			passButton.setEnabled(true);
			AppearanceSettings.setEnabled(false, submitAnswerButton, answerField);
		}
	}
	
	public TeamGUIComponents getCurrentTeam(){
		return currentTeam;
	}
}