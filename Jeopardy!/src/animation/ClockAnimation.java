package animation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ClockAnimation extends JPanel {

	private static final long serialVersionUID = -8860851782231828870L;
	private List<Image> clockFrames;
	private int currentFrame; 
	
	public ClockAnimation() {
		setPreferredSize((new Dimension(69,69)));
		currentFrame = 0;
		clockFrames = new ArrayList<Image>();
		for(int i = 0; i < 20; i++) {
			BufferedImage img = null;
			String index = Integer.toString(i);
			try {
				img = ImageIO.read(new File("clock_frames/frame" + index + ".jpg"));
			} catch (IOException e) { 
				System.err.println(e.getMessage());
			}
			Image scaledImg = img.getScaledInstance(69, 69, Image.SCALE_SMOOTH);
			clockFrames.add(scaledImg);
		}
		
		this.setVisible(true);
	}
	
	@Override
	public void paint(Graphics frame) {
		super.paintComponent(frame);
		Graphics2D frame2D = (Graphics2D) frame;
		frame2D.drawImage(clockFrames.get(currentFrame), 0, 0, this);
	}
	
	public void resetCurrentFrame() {
		currentFrame = 0; 
	}
	
	public void incrementCurrentFrame() {
		if(currentFrame < 19) currentFrame++;
	}
	
	public int getCurrentFrame() {
		return currentFrame; 
	}
	
	@Override
	public Dimension getPreferredSize() {
		return (new Dimension(69,69));
	}
	
	/*
	public static void main(String[] args) {
	    JFrame frame = new JFrame("TimerBasedAnimation");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.add(new ClockAnimation(15));
	    frame.setSize(350, 250);
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	 }
	 */
}