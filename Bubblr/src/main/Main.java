package main;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import GUI.BubbLogin;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class Main {

	public static void main (String [] args){
		try {
		     GraphicsEnvironment ge = 
		         GraphicsEnvironment.getLocalGraphicsEnvironment();
		     ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("img/Early GameBoy.ttf")));
		} catch (IOException|FontFormatException e) {
		     //Handle exception
		}
		
		//music
		new Thread(new Runnable() {
			public void run() {
				//this stuff is for looping
				long start = System.currentTimeMillis();
				int count = 0;
				
				while(true) {
					if (count==0 || System.currentTimeMillis() >= start+84000) {
				        AudioStream BGM;
			
				        try
				        {
				            InputStream test = new FileInputStream("img/bubble_man.wav");
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
				        start = System.currentTimeMillis();
				        count = 1;
					}
				}
			}
		}).start();
		
		new BubbLogin().setVisible(true);
	}
}
