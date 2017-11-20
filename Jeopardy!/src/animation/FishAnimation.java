package animation;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class FishAnimation extends JPanel {
	
	private static final long serialVersionUID = -2812885088083828882L;
	private List<Image> frames;
	private int currentFrame;
	
	public FishAnimation() {
		currentFrame = 0;
		frames = new ArrayList<Image>(8);
		for(int i = 0; i < 8; i++) {
			BufferedImage img = null;
			String index = Integer.toString(i);
			try {
				img = ImageIO.read(new File("waiting_animation_frames/frame" + index + ".jpg"));
			} catch (IOException e) { 
				System.err.println(e.getMessage());
			}
			Image scaledImg = img.getScaledInstance(114, 100, Image.SCALE_SMOOTH);
			frames.add(scaledImg);
		}
		setVisible(true);
	}
	
	public void resetCurrentFrame() {
		currentFrame = 0;
	}
	
	public void incrementCurrentFrame() {
		if(currentFrame == 7) currentFrame = 0;
		else currentFrame++;
	}
	
	public int getCurrentFrame() {
		return currentFrame; 
	}
	
	@Override
	public void paint(Graphics frame) {
		super.paintComponent(frame);
		Graphics2D frame2D = (Graphics2D) frame;
		if(frames.get(currentFrame) != null) {
			frame2D.drawImage(frames.get(currentFrame), 0, 0 , this);
		}
	}
	
	@Override
	public Dimension getPreferredSize() {
		return (new Dimension(114,100));
	}
	
	/*
	public static void main(String[] args) {
	    JFrame frame = new JFrame("TimerBasedAnimation");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.add(new FishAnimation(15));
	    frame.setSize(350, 250);
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	 }
	 */
}