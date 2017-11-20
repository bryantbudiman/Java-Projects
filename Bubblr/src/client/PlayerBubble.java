package client;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.Condition;

import javax.imageio.ImageIO;

public class PlayerBubble extends GameObject {
	protected GameSimulation gameSimulation;

	//protected Lock lock;
	protected Condition atLocation;

//	private Timestamp finished;

	protected GameNode currentNode;
	protected GameNode nextNode;

	protected PlayerBubble(GameNode startNode, GameSimulation gameSimulation, User loggedInUser) {
		super(new Rectangle(startNode.getX(), startNode.getY(), 1, 1));
		this.gameSimulation = gameSimulation;
		currentNode = startNode;
		nextNode = startNode;
		if (loggedInUser != null) {
			image = loggedInUser.getImg();
		} else {
			try {
				image = ImageIO.read(new File("img/flame.png"));
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public GameNode getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(GameNode n) {
		currentNode = n;
	}

	/*public void moveRight() {
		if(!lock.tryLock()) return;
		
		nextNode = gameSimulation.nextNodeForPlayerBubble(currentNode, true);
		playerMoveTowards(nextNode);
		
		lock.unlock();
	}
	
	public void moveLeft() {
	//	if(!lock.tryLock()) return;
		
		nextNode = gameSimulation.nextNodeForPlayerBubble(currentNode, false);
		playerMoveTowards(nextNode);
		
	//	lock.unlock();
	}*/

	public void draw(Graphics g) {
		if (image == null) {
			System.out.println("player's image is null");
		}
		super.draw(g);
	}

}
