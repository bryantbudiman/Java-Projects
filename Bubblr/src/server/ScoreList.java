package server;

import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import server.Server.LoggedInPlayer;

public class ScoreList extends Thread {
	private Vector<LoggedInPlayer> players;
	private Vector<LoggedInPlayer> delete;
	private ReentrantLock lock;

	public ScoreList(Vector<LoggedInPlayer> p) {
		players = p;
		delete = new Vector<LoggedInPlayer>();
		lock = new ReentrantLock();
		this.start();
	}

	public void sendScoreList() {
		Vector<String> scores = new Vector<String>();
		long currTime = System.currentTimeMillis();
		for (LoggedInPlayer p : players) {
			if(!delete.contains(p) && p.isAlive())
				scores.add(p.getUser().getUsername() + ": " + Long.toString( (p.getScore(currTime)) / 1000 ));
		}
		if (!scores.isEmpty()) {
			for (LoggedInPlayer p : players) {
				if(!delete.contains(p) && p.isAlive())
					p.sendMessage(new Message(scores, ( (int)(p.getScore(currTime)) / 1000) ));
			}
		}
		lock.lock();
		for(LoggedInPlayer p : delete) {
			players.remove(p);
			p.kill();
		}
		lock.unlock();
		delete.clear();
	}

	public void run() {
		try {
			while (true) {
				sendScoreList();
				Thread.sleep(1000);
			}
		} catch (InterruptedException ie) {
			System.out.println("ie in ScoreList: " + ie.getMessage());
		}
	}
	
	public void logout(LoggedInPlayer p) {
		lock.lock();
		delete.add(p);
		lock.unlock();
	}
}
