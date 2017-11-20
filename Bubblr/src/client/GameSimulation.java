package client;

import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

import GUI.GameOverPanel;
import server.Message;


public class GameSimulation {
	
	// dimension of the game board (# of rows and columns)
	private static final int mWidth = 15;
	private static final int mHeight = 15;
	
	//GameObject is the base (abstract) class for all the game objects 
	private Set<GameObject> gameObjects;
	private Set<AIBubble> AIBubbles;

	private GameNode gameNodes[][];
	private PlayerBubble playerBubble;
	private GamePlayManager gamePlayManager;
//	private GamePlayPanel gamePlayPanel;
	private User loggedInUser;
	private boolean started = false;
	private boolean gameOver = false;
//	private int totalTime; 
	//private int[] speeds = {3, 5, 8, 10, 14};
	private int speed = 3; 
		
	//instance constructor
	{
		gameObjects = new HashSet<GameObject>();
		AIBubbles = new HashSet<AIBubble>();
		gameNodes = new GameNode[mWidth][mHeight];
	}
	
	public GameSimulation(GamePlayManager gamePlayManager, User loggedInUser) {
		this.gamePlayManager = gamePlayManager;
		this.loggedInUser = loggedInUser;
		//Create the nodes of the game
		for(int height = 0; height < mWidth; height++) {
			for(int width = 0; width < mHeight; width++) {
				gameNodes[width][height] = new GameNode(width,height);
				gameObjects.add(gameNodes[width][height]);
			}
		}
		
		//Link all of the nodes together
		for(GameNode[] nodes: gameNodes) {
			for(GameNode node : nodes) {
				int x = node.getX();
				int y = node.getY();
				if(x != 0) node.addNeighbor(gameNodes[x-1][y]);
				if(x != mWidth - 1) node.addNeighbor(gameNodes[x+1][y]);
				if(y != 0) node.addNeighbor(gameNodes[x][y-1]);
				if(y != mHeight - 1) node.addNeighbor(gameNodes[x][y+1]);
			}
		}
				
		//Create the player bubble, which is located in the middle of the game's floor
		playerBubble = new PlayerBubble(gameNodes[7][13], this, loggedInUser); 
		gameObjects.add(playerBubble);
	
		//Create the floor for the game. The floor will be white tiles that are commonly found in bathrooms 
		for(int i = 0; i < 15; i++) {
			GameFloor gameFloor = new GameFloor(new Rectangle(i,14,1,1));
			gameObjects.add(gameFloor);
			gameNodes[gameFloor.getX()][gameFloor.getY()].setObject(gameFloor);
		}
		for(int i = 0; i < 15; i++) {
			GameFloor gameFloor = new GameFloor(new Rectangle(14,i,1,1));
			gameObjects.add(gameFloor);
			gameNodes[gameFloor.getX()][gameFloor.getY()].setObject(gameFloor);
		}
		for(int i = 0; i < 15; i++) {
			GameFloor gameFloor = new GameFloor(new Rectangle(i,0,1,1));
			gameObjects.add(gameFloor);
			gameNodes[gameFloor.getX()][gameFloor.getY()].setObject(gameFloor);
		}
		for(int i = 0; i < 15; i++) {
			GameFloor gameFloor = new GameFloor(new Rectangle(0,i,1,1));
			gameObjects.add(gameFloor);
			gameNodes[gameFloor.getX()][gameFloor.getY()].setObject(gameFloor);
		}
		
		for(int i = 0; i < 4; i++) {
			AIBubble aiBubble = new AIBubble(gameNodes[1][1], this, i);
			gameObjects.add(aiBubble);
			AIBubbles.add(aiBubble);
		}
		
		for(int i = 0; i < 4; i++) {
			AIBubble aiBubble = new AIBubble(gameNodes[8][1], this, i);
			gameObjects.add(aiBubble);
			AIBubbles.add(aiBubble);
		}
		
		for(int i = 0; i < 4; i++) {
			AIBubble aiBubble = new AIBubble(gameNodes[13][1], this, i);
			gameObjects.add(aiBubble);
			AIBubbles.add(aiBubble);
		}
		
		for(int i = 0; i < 4; i++) {
			AIBubble aiBubble = new AIBubble(gameNodes[13][8], this, i);
			gameObjects.add(aiBubble);
			AIBubbles.add(aiBubble);
		}
	
	}
	
	public void showGameOverPanel() {
		if(!gameOver) {
			new GameOverPanel(loggedInUser, gamePlayManager).setVisible(true);
			gameOver = true;
		}
	}
	
	public void stopGame() {
		this.gamePlayManager.stopAnimation();
		if (!loggedInUser.isGuest()) {
			loggedInUser.client.sendMessage(new Message(loggedInUser, Message.END_GAME));
		}
	}
	
	public GameNode[][] getNodes() {
		return gameNodes;
	}
	
	public void moveRight() {
		GameNode gameNode = playerBubble.getCurrentNode();
		if(gameNode.getX()+1 == 14) {
			playerBubble.moveTowards(this.gameNodes[gameNode.getX()][gameNode.getY()]);
		}
		else {
			playerBubble.moveTowards(this.gameNodes[gameNode.getX()+1][gameNode.getY()]);
			playerBubble.setCurrentNode(this.gameNodes[gameNode.getX()+1][gameNode.getY()]);
		}
	}
	
	public void moveLeft() {
		GameNode gameNode = playerBubble.getCurrentNode();
		if(gameNode.getX()-1 == 0) {
			playerBubble.moveTowards(this.gameNodes[gameNode.getX()][gameNode.getY()]);
		}
		else {
			playerBubble.moveTowards(this.gameNodes[gameNode.getX()-1][gameNode.getY()]);
			playerBubble.setCurrentNode(this.gameNodes[gameNode.getX()-1][gameNode.getY()]);
		}
	}
	
	public void moveUp() {
		GameNode gameNode = playerBubble.getCurrentNode();
		if(gameNode.getY()-1 == 0) {
			playerBubble.moveTowards(this.gameNodes[gameNode.getX()][gameNode.getY()]);
		}
		else {
			playerBubble.moveTowards(this.gameNodes[gameNode.getX()][gameNode.getY()-1]);
			playerBubble.setCurrentNode(this.gameNodes[gameNode.getX()][gameNode.getY()-1]);
		}
	}
	
	public void moveDown() {
		GameNode gameNode = playerBubble.getCurrentNode();
		if(gameNode.getY()+1 == 14) {
			playerBubble.moveTowards(this.gameNodes[gameNode.getX()][gameNode.getY()]);
		}
		else {
			playerBubble.moveTowards(this.gameNodes[gameNode.getX()][gameNode.getY()+1]);
			playerBubble.setCurrentNode(this.gameNodes[gameNode.getX()][gameNode.getY()+1]);
		}
	}
	
	public void update(double deltaTime) {	
		if(!started){
			started = true;
			if(loggedInUser.client != null)
				loggedInUser.client.sendMessage(new Message(Message.START_GAME));
			else
				gamePlayManager.getGPGUI().updateStartTime();
		}
		
		for(GameObject object : AIBubbles) object.update(deltaTime*speed);
		
		//increment the total time here
//		totalTime += (int)deltaTime;
		
		/*if(totalTime > 60) speed = speeds[0];
		else if(totalTime > 120) speed = speeds[1];
		else if(totalTime > 180) speed = speeds[2];
		else if(totalTime > 240) speed = speeds[3];
		else if(totalTime > 300) speed = speeds[4];*/
	}

	// GETTERS
	public int getWidth() {
		return mWidth;
	}
	
	public int getHeight() {
		return mHeight;
	}
	
	public Set<GameObject> getObjects() {
		return gameObjects;
	}
	
	public Set<AIBubble> getAIBubbles() {
		return AIBubbles;
	}
	
	public PlayerBubble getPlayerBubble() {
		return playerBubble;
	}
}
