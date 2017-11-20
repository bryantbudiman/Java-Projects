package client;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import GUI.GamePlayGUI;
import GUI.GamePlayPanel;
import GUI.HighScorePanel;

public class GamePlayManager implements Runnable {
	private Lock animationLock; //This lock ensures that only one animation thread will go at a time
	private GameSimulation gameSimulation; //The simulation that is currently running, and where it will be displayed
	
	// GamePlayManager needs access to the panels because it needs to repaint() them continuously  
	private GamePlayPanel gamePlayPanel; //The panel with the bubble and the current score timer
	private HighScorePanel highScorePanel; //The panel with the highscore list
	private GamePlayGUI gamePlayGUI;
	
	private Thread animator; //This thread will update and draw all the changes within the game
	private volatile boolean running;
	
	private long period = 10; //magic number
	
	private User loggedInUser;
	

	public GamePlayManager(User loggedInUser, GamePlayGUI gamePlayGUI) {
		this.gamePlayGUI = gamePlayGUI;
		this.loggedInUser = loggedInUser;
		running = false;
		animationLock = new ReentrantLock();
		gameSimulation = new GameSimulation(this, loggedInUser);
		startAnimation();
	}
	
	public void setGamePlayPanel(GamePlayPanel gamePlayPanel) {
		this.gamePlayPanel = gamePlayPanel;
	}
	
	public void repaintAndRevalidate() {
		if(gamePlayPanel != null) {
			this.gamePlayPanel.repaint();
			this.gamePlayPanel.revalidate();
		}
	}
	
	public void updateTimer(int time) {
		this.gamePlayGUI.updateTimer(time);
	}
	
	public void setHighScorePanel(HighScorePanel highScorePanel) {
		this.highScorePanel = highScorePanel; 
	}
	
	public void disposeGameGUI() {
		this.gamePlayGUI.dispose();
	}
	
	public void moveRight() {
		gameSimulation.moveRight();
	}
	
	public void moveLeft() {
		gameSimulation.moveLeft();
	}
	
	public void moveUp() {
		gameSimulation.moveUp();
	}
	
	public void moveDown() {
		gameSimulation.moveDown();
	}
	
	public void startAnimation() {
		if(animator == null || !running) {
			animator = new Thread(this);
			animator.start();
		}
	}
	
	//halts the animation of the factorySimulation
	public void stopAnimation() {
		running = false;
		animator = null;
	}
	
	public GamePlayGUI getGPGUI() {
		return gamePlayGUI;
	}
	
	@Override
	public void run() {
		animationLock.lock();
		long beforeTime = 0, deltaTime = 0, sleepTime = 0;
		beforeTime = System.nanoTime();
		running = true;
		while(running) {
			//if we have a simulation to run, run the simulation
			if(gameSimulation != null) {
				gameSimulation.update(((double)deltaTime/(double)1000000000));
			}
			
			//if we have somewhere to render the gamePlayPanel, render it 
			if(gamePlayPanel != null) {
				gamePlayPanel.render(gameSimulation); //update the graphic
				gamePlayPanel.paint(); //paint the graphic onto the screen
			}
			
			//if we have somewhere to render the highScorePanel, render it 
			if(highScorePanel != null) {
				highScorePanel.render(gameSimulation); //update the graphic
			}
			
			deltaTime = System.nanoTime() - beforeTime;
			sleepTime = period - deltaTime;
			
			if(sleepTime <= 0L) {
				sleepTime = 5L;
			}
			
			try{ 
				Thread.sleep(sleepTime);
			} catch(InterruptedException ex){}
			
			beforeTime = System.nanoTime();
			
			if(loggedInUser.client == null)
				gamePlayGUI.updateEndTime();
		}
		animationLock.unlock(); 
	}
}