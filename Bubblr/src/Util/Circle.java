package Util;

public class Circle {

	private double x, y;
	private int width, height;
	
	public Circle(double x, double y, int w, int h) {
		this.x = x;
		this.y = y;
		width = w;
		height = h;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public double getCenterX() {
		double centerX = x + width/2;
		return centerX;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public void setWidth(int w) {
		width = w;
	}
	
	public void setHeight(int h) {
		height = h;
	}
	
}
