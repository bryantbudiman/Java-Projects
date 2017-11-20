package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

public abstract class GameObject {

	protected static final double closeEnough = 0.05; //used to see when an AI bubble has gotten close enough to player's bubble
	protected Image image = null;
	protected String mLabel = "";
	
	//The "true" location and size of the object use this for updating
	protected double x = 0.0, y = 0.0;
	private int width = 0, height = 0;
		
	private double xScale;
	private double yScale;
	
	//Dictates whether a bubble is a player bubble or not 
	private boolean isPlayerBubble = false;
		
	//The location and size used for rendering
	protected Rectangle renderBounds = new Rectangle(0,0,0,0);
	private int gameBorderSize = 0;
	
	protected GameObject(Rectangle inDimensions) {
		x = (double)inDimensions.getX();
		y = (double)inDimensions.getY();
		width = inDimensions.width;
		height = inDimensions.height;
	}

	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y; 
	}

	public void isPlayerBubble() {
		isPlayerBubble = true; 
	}
	
	public void notPlayerBubble() {
		isPlayerBubble = false; 
	}
	
	public boolean nodeIsPlayer() {
		return isPlayerBubble; 
	}

	//Implement in child classes
	public void draw(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawImage(image, renderBounds.x, renderBounds.y, renderBounds.width, renderBounds.height, null);
	}
		
	//Scale the object for rendering.
	public void resize(double XScale, double YScale) {
		xScale = XScale; yScale = YScale;
		renderBounds.x = (int)(x*XScale); renderBounds.y = (int)(y*YScale);
		renderBounds.width = (int)(width*XScale); renderBounds.height = (int)(height*YScale);
	}
	//center the object if needed
	public void center(int inFactoryBordersize) {
		gameBorderSize = inFactoryBordersize;
		renderBounds.y += gameBorderSize;
		renderBounds.x += gameBorderSize;
	}
		
	//Don't need to implement this - some objects are static and don't need to update
	public void update(double deltaTime){}
		
	//These should be used by the child object, hides the extra work of scaling
	protected void changeX(double deltaX) {
		x += deltaX;
		renderBounds.x = (int)(x*xScale) + gameBorderSize;
	}
	protected void changeY(double deltaY) {
		y += deltaY;
		renderBounds.y = (int)(y*yScale) + gameBorderSize;
	}
		
	protected boolean moveTowards(GameObject gameObject, double deltaTime) {
		//Navigate to where we want to go
		boolean xMatch = false;
		double moveX = gameObject.x - this.x;
		if(Math.abs(moveX) <= closeEnough) {
			this.x = gameObject.x;
			xMatch = true;
		}
		else if(moveX < 0) this.changeX(deltaTime * -1);
		else if(moveX > 0) this.changeX(deltaTime * +1);
			
		boolean yMatch = false;
		double moveY = gameObject.y - this.y;
		if(Math.abs(moveY) <= closeEnough) {
			this.y = gameObject.y;
			yMatch = true;
		}
		else if(moveY < 0) this.changeY(deltaTime * -1);
		else if(moveY > 0) this.changeY(deltaTime * +1);
		
		return (xMatch & yMatch);
	}
	
	protected boolean moveTowards(GameObject gameObject) {
		//Navigate to where we want to go
		boolean xMatch = false;
		double moveX = gameObject.x - this.x;
		if(Math.abs(moveX) <= closeEnough) {
			this.x = gameObject.x;
			xMatch = true;
		}
		else if(moveX < 0) this.changeX(-1);
		else if(moveX > 0) this.changeX(1);
			
		boolean yMatch = false;
		double moveY = gameObject.y - this.y;
		if(Math.abs(moveY) <= closeEnough) {
			this.y = gameObject.y;
			yMatch = true;
		}
		else if(moveY < 0) this.changeY(-1);
		else if(moveY > 0) this.changeY(+1);
		
		return (xMatch & yMatch);
	}
	
	protected boolean playerMoveTowards(GameObject gameObject) {
		//Navigate to where we want to go
		boolean xMatch = false;
		double moveX = gameObject.x - this.x;
		if(Math.abs(moveX) <= closeEnough) {
			this.x = gameObject.x;
			xMatch = true;
		}
		else if(moveX < 0) this.changeX(-1);
		else if(moveX > 0) this.changeX(1);
			
		boolean yMatch = false;
		double moveY = gameObject.y - this.y;
		if(Math.abs(moveY) <= closeEnough) {
			this.y = gameObject.y;
			yMatch = true;
		}
		else if(moveY < 0) this.changeY(-1);
		else if(moveY > 0) this.changeY(+1);
		
		return (xMatch & yMatch);
	}
		
	public int getX() {
		return (int)x;
	}
		
	public int getY() {
		return (int)y;
	}
		
	//Helper functions for drawing
	protected int centerTextY(Graphics g) {
		return (int) (renderBounds.y+renderBounds.getHeight()) - (g.getFontMetrics().getHeight()/4);
	}
	protected int centerTextX(Graphics g, String toSize) {
		return (int) (renderBounds.getCenterX() - (g.getFontMetrics().stringWidth(toSize)/2));
	}
	
}