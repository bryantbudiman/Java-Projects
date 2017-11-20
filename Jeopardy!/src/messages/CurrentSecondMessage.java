package messages;

public class CurrentSecondMessage implements Message {
	private int currentSecond; 
	
	public CurrentSecondMessage(int currentSecond) {
		this.currentSecond = currentSecond;
	}
	
	public int getCurrentSecond() {
		return currentSecond;
	}
}
