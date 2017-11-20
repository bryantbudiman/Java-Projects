package GUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JPanel;

import Util.AppearanceConstants;
import Util.AppearanceSettings;
import client.AIBubble;
import client.GameNode;
import client.GameObject;
import client.GameSimulation;
import client.PlayerBubble;
import client.User;

public class GamePlayPanel extends JPanel {

	public static final long serialVersionUID = 88L;

	// dimension of the panel
	private int width = 660;
	private int height = 660;
	// dimension of the actual game board excluding borders
	private int boardWidth = 660;
	private int boardHeight = 660;
	// border size
	private int border = 20;

//	private User loggedInUser;
	private Graphics graphics;
	private Image image = null;
//	private Graphics mGraphics;

	public GamePlayPanel(User user) {
//		loggedInUser = user;
		initializeVariables();
		createGUI();

		Thread repainter = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) { // I recommend setting a condition for your panel being open/visible
					repaint();
					try {
						Thread.sleep(30);
					} catch (InterruptedException ignored) {
					}
				}
			}
		});

		repainter.setPriority(Thread.MIN_PRIORITY);
		repainter.start();
	}

	public void initializeVariables() {
		AppearanceSettings.setBackground(AppearanceConstants.lightRed, this);
	}

	public void createGUI() {
		setSize(width, height);
	}

	public void render(GameSimulation gameSimulation) {
		//render the game - don't paint directly to avoid screen tearing
		//only want to update things if necessary - otherwise we will do an unnecessary amount of work
		if (width != getWidth() || height != getHeight() || image == null) {
			width = getWidth();
			height = getHeight();
			if (width <= 0 || height <= 0)
				return;
			image = createImage(width, height);

			//update the scaling of all the objects in the game
			double width = boardWidth / (double) 15;
			double height = boardHeight / (double) 15;
			for (GameObject object : gameSimulation.getObjects()) {
				//re-scale all of the objects, and adjust for borders
				object.resize(width, height);
				object.center(border);
			}
		}

		if (image != null)
			graphics = image.getGraphics();
		else
			return;

		if (graphics == null)
			return;

		//Clear the entire panel
		graphics.setColor(Color.GRAY);
		graphics.fillRect(0, 0, width, height);

		//Draw the background of the game floor
		graphics.setColor(Color.BLACK);
		graphics.drawRect(border, border, boardWidth, boardHeight);

		//Draw the rest of the game
		//set the font to the generic font
		graphics.setFont(new Font("TimesRoman", Font.PLAIN, border));
		//save the position of the mouse, important to know for drawing tool-tips

		//Draw nodes - nodes have other objects attached to them
		//when drawing nodes, their object will also be drawn
		for (GameNode[] nodes : gameSimulation.getNodes()) {
			for (GameNode node : nodes) {
				node.draw(graphics);
				validate();
			}
		}

		//Draw AI bubbles
		for (AIBubble bubble : gameSimulation.getAIBubbles()) {
			bubble.draw(graphics);
		}

		// Draw player's bubble
		PlayerBubble pb = gameSimulation.getPlayerBubble();
		pb.draw(graphics);

		//repaint();
		//revalidate();
	}

	public int returnWidth() {
		return width;
	}

	public int returnHeight() {
		return height;
	}

	//Actually paint the image rendered onto the panel
	public void paint() {
		Graphics g;
		g = this.getGraphics();
		if ((g != null) && (image != null)) {
			g.drawImage(image, 0, 0, null);
		}
		//Apparently this is needed for some systems
		Toolkit.getDefaultToolkit().sync();
		//We might have to uncomment this if we get errors
//		g.dispose();
	}

	@Override
	protected void paintComponent(Graphics g) {
		//This isn't part of the main loop, but will be called when re-scaling
		super.paintComponent(g);
		if (image != null) {
			g.drawImage(image, 0, 0, null);
		}
	}

	//Used to force a redraw of the game - Usually when a new game is loaded
	//Just set the size to 0 so everything is reset on the next paint call
	public void refresh() {
		width = 0;
		height = 0;
	}
}
