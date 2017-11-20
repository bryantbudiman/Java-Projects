package client;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.imageio.ImageIO;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class AIBubble extends GameObject implements Runnable {

	protected GameSimulation gameSimulation;
	
	protected Lock lock;
	protected Condition atLocation;
	
	protected GameNode currentNode;
	protected GameNode nextNode;
	protected GameNode destinationNode;
	protected Stack<GameNode> path;
	private int counter;
	
	private boolean touchPlayerBubble = false;
	
	protected AIBubble(GameNode startNode, GameSimulation gameSimulation, int i) {
		super(new Rectangle(startNode.getX(), startNode.getY(), 1, 1));
		this.gameSimulation = gameSimulation;
		this.currentNode = startNode;
		counter = 0;
		
		// Randomly picking colors of the bubble
		if(i%2 == 0) {
			try {
				image = ImageIO.read(new File("images/yellow_evil_bubble.png"));
			} catch (IOException e) { 
				System.err.println(e.getMessage());
			}
		}
		else if(i%2 != 0) {
			try {
				image = ImageIO.read(new File("images/blue_evil_bubble.png"));
			} catch (IOException e) { 
				System.err.println(e.getMessage());
			}
		}

		lock = new ReentrantLock();
		atLocation = lock.newCondition();
		new Thread(this).start();
	}
	
	public GameNode bounce() {

		Random randX = new Random();
		Random randY = new Random();
		int newX = 8; // dummy value
		int newY = 8; // dummy value
		
		GameNode newDestination = null;
		// currentNode is in north side
		if( ((currentNode.getX() > 0) && (currentNode.getX() < 14)) && currentNode.getY() == 1) {
			newY = randY.nextInt(8)+5; // between 8 and 13
			if(newY == 13) { // If bottom side, the new X coordinate can be between 1 and 13
				newX = randX.nextInt(13);
			}
			else if(newY >= 5 && newY < 13) { // If not bottom side, the new X coordinate can only be either 0 or 13
				newX = (randX.nextInt() % 2 == 0) ? 1 : 13;
			}
			newDestination = gameSimulation.getNodes()[newX][newY];
		}
		// currentNode is in south side
		else if( ((currentNode.getX() > 0) || (currentNode.getX() < 14)) && currentNode.getY() == 13) {
			newY = randY.nextInt(10)+1; // between 0 and 8
			if(newY == 0) { // If top side, the new X coordinate can be between 1 and 13
				newX = randX.nextInt(13);
			}
			else if(newY > 0 && newY <= 10) { // If not top side, the new X coordinate can only be either 0 or 13
				newX = (randX.nextInt() % 2 == 0) ? 1 : 13;
			}
			newDestination = gameSimulation.getNodes()[newX][newY];
		}
		// currentNode is in east side 
		else if( currentNode.getX() == 13 && ((currentNode.getY() > 0) || (currentNode.getY() < 14))) {
			newX = randX.nextInt(10)+1; // between 0 and 8
			if(newX == 0) { // If west side, the new Y coordinate can be between 1 and 13
				newY = randY.nextInt(13);
			}
			else if(newX > 0 && newX <= 10) { // If not west side, the new Y coordinate can only be either 0 or 13
				newY = (randY.nextInt() % 2 == 0) ? 1 : 13;
			}
			newDestination = gameSimulation.getNodes()[newX][newY];
		}
		// currentNode is in west side 
		else if( currentNode.getX() == 1 && ((currentNode.getY() > 0) || (currentNode.getY() < 14))) {
			newX = randX.nextInt(8)+5; // between 0 and 8
			if(newX == 13) { // If east side, the new Y coordinate can be between 0 and 13
				newY = randY.nextInt(13);
			}
			else if(newX >= 5 && newX < 13) { // If not east side, the new Y coordinate can only be either 1 or 13
				newY = (randY.nextInt() % 2 == 0) ? 1 : 13;
			}
			newDestination = gameSimulation.getNodes()[newX][newY];
		}

		return newDestination;
	}
	
	@Override
	public void draw(Graphics g) {
		if(image == null) {
			System.out.println("AI bubble's image is null");
		}
		super.draw(g);
	}
	
	@Override
	public void update(double deltaTime) {
		if(!lock.tryLock()) return;
		//if we have somewhere to go, go there
		if(destinationNode != null) {
			if(moveTowards(nextNode,deltaTime*2)) {
				//if we arrived, save our current node
				currentNode = nextNode;
				
				// test if the AI bubble touches the player bubble
				testTouchPlayerBubble();
				if(touchPlayerBubble && counter < 1) {
					counter++;
					deathSound();
					gameSimulation.stopGame();
					gameSimulation.showGameOverPanel();
					System.out.println("called gameoverpanel");
				}
				
				if(!path.isEmpty()) {
					//if we have somewhere else to go, save that location
					nextNode = path.pop();
				}//if we arrived at the location, signal the worker thread so they can do more actions
				if(currentNode == destinationNode){
					atLocation.signal();
				}
			}
		}
		lock.unlock();
	}

	//Move the AI bubble downwards 
	@Override
	public void run() {
		lock.lock();
		try {
			while(true) {
				destinationNode = bounce();
				path = currentNode.findShortestPath(destinationNode);
				
				nextNode = path.pop();
				this.atLocation.await();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		lock.unlock();
	}
	
	public void testTouchPlayerBubble() {
		int playerX = gameSimulation.getPlayerBubble().getX();
		int playerY = gameSimulation.getPlayerBubble().getY();
		if(currentNode.getX() == playerX && currentNode.getY() == playerY) {
			touchPlayerBubble = true;
		}
		else {
			touchPlayerBubble = false;
		}
	}
	
	public void deathSound() {
		AudioStream BGM;

        try
        {
            InputStream test = new FileInputStream("img/death.wav");
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