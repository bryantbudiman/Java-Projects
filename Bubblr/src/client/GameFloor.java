package client;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GameFloor extends GameObject {

	protected GameFloor(Rectangle inDimensions) {
		super(inDimensions);
		try {
			image = ImageIO.read(new File("images/floor.png"));
		} catch (IOException e) { 
			System.err.println(e.getMessage());
		}
	}

}