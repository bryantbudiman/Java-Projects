package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.Timer;

import messages.CurrentSecondMessage;

public class GameTimer {
	private ServerToClientThread[] communicationThreads;
	private ReentrantLock lock; 
	private int currentSecond;
	protected Timer timer = new Timer(1000, new AnimationListener());
	
	public GameTimer(ServerToClientThread[] communicationThreads) {
		this.communicationThreads = new ServerToClientThread[communicationThreads.length];
		for(int i = 0; i < communicationThreads.length; i++) {
			this.communicationThreads[i] = communicationThreads[i];
		}
		lock = new ReentrantLock();
	}
	
	public void resetTimer() {
		timer.stop();
		currentSecond = 0; 
	}
	
	public void startTimer() {
		timer.start();
	}
	
	private class AnimationListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) { 
        	currentSecond++;
        	
        	lock.lock();
    		for(ServerToClientThread c : communicationThreads) {
    			if(c != null) c.sendMessage(new CurrentSecondMessage(currentSecond));
    		}
    		lock.unlock();
        }
	}
}	
