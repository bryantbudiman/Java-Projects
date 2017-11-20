package Util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import javax.swing.ImageIcon;

public class AppearanceConstants {
	
	//colors, fonts, ect that can be statically referenced by other classes
	private static final ImageIcon exitIconLarge = new ImageIcon("images/question_mark.png");
	private static final Image exitImage = exitIconLarge.getImage();
	private static final Image exitImageScaled = exitImage.getScaledInstance(50, 50,  java.awt.Image.SCALE_SMOOTH);
	
	public static final ImageIcon exitIcon = new ImageIcon(exitImageScaled);
	
	public static final Color lightGold = new Color(255,204,0);
	public static final Color lightRed = new Color(153,27,30);
	public static final Color mediumGray = new Color(100, 100, 100);
	
	public static final Font fontSmall = new Font("Early GameBoy", Font.BOLD,18);
	public static final Font fontSmallest = new Font("Early GameBoy", Font.BOLD,14);
	public static final Font fontMedium = new Font("Early GameBoy", Font.BOLD, 22);
	public static final Font fontLarge = new Font("Early GameBoy", Font.BOLD, 30);
	public static final Font fontHellaLarge = new Font("Early GameBoy", Font.BOLD, 40);
	public static final Font fontHuge = new Font("Early GameBoy", Font.BOLD, 70);
	//added a blue border variable used in StartWindowGUI
	//public static final Border blueLineBorder = BorderFactory.createLineBorder(darkBlue);
}
