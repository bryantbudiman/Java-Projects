package animation;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.io.Serializable;

import javax.swing.JPanel;

import other_gui.AppearanceConstants;

public class CountDownAnimation extends JPanel {

	private static final long serialVersionUID = -838853382870315825L;
	private Font font = new Font("Palatino", Font.BOLD, 30);
	private int currentSecond; 
	private String timerString; 
	private boolean blue; 
	
	public CountDownAnimation(int seconds, boolean blue) {
		currentSecond = seconds; 
		timerString = ":" + currentSecond;
		setFont(AppearanceConstants.fontLarge);
		this.blue = blue; 
		if(this.blue == true) setForeground(AppearanceConstants.lightBlue);
		this.setVisible(true);
	}

	@Override
	public void paint(Graphics frame) {
		super.paintComponent(frame);
		frame.setFont(font);
		if(blue == true) frame.setColor(AppearanceConstants.lightBlue);
		frame.drawString(timerString, 0, 27);
	}
	
	public void decrementCurrentSecond() {
		if(currentSecond > 0) {
			currentSecond--;
			if(currentSecond < 10) this.timerString = ":0" + currentSecond;
			else this.timerString = ":" + currentSecond;
		}
	}
	
	public void setCurrentSecond(int seconds) {
		this.currentSecond = seconds;
		this.timerString = ":" + currentSecond;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return (new Dimension(38,38));
	}
}