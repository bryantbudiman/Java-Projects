package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GameNode extends GameObject {

	//Each node can own a GameObject, doing so will render it in its place
	private GameObject gameObject;
	
	//This lock is used to lock the node or the object within the node
	private Lock nodeLock;
	
	//Used for path finding
	private ArrayList<GameNode> mNeighbors;
	
	final int x;
	final int y;
	
	//instance constructor
		{
			gameObject = null;
			nodeLock = new ReentrantLock();
			mNeighbors = new ArrayList<GameNode>();
		}
		
		public GameNode(int x, int y) {
			super(new Rectangle(x,y,1,1)); //each node is 1 tile
			this.x = x;
			this.y = y;
		}

		@Override
		public void draw(Graphics g) {
			g.setColor(Color.white);
			if(gameObject == null) {
				g.setColor(Color.WHITE);
				g.drawRect(renderBounds.x, renderBounds.y, renderBounds.width, renderBounds.height);
			} else {
				gameObject.draw(g);
			}
		}

		public void setObject(GameObject inFObject) {
			gameObject = inFObject;
		}
		
		public GameObject getObject() {
			return gameObject;
		}

		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		public void addNeighbor(GameNode neighbor) {
			mNeighbors.add(neighbor);
			mNeighbors.add(neighbor);
		}
		
		public ArrayList<GameNode> getNeighbors() {
			return mNeighbors;
		}
		
		public boolean aquireNode() {
			return nodeLock.tryLock();
		}
		public void releaseNode() {
			nodeLock.unlock();
		}

		//Below this line is the methods for path finding
		
		//We need a wrapper class for path finding
		//Otherwise, two threads couldn't path find at a time
		class PathNode {
			public GameNode gNode; //the actual node
			public int gScore; //cost from the start of the best known path
			public int hScore; //Manhattan distance to the end
			public int fScore; //g+h
			public PathNode parent;
		}
		private int heuristicCostEstimate(GameNode factoryNode) {
			//Manhattan distance between the nodes
			//This method assumes we are path finding TO "this"
			return ((int)Math.abs(this.x - factoryNode.x) + (int)Math.abs(this.y - factoryNode.y));
		}
		private PathNode lowestFScore(ArrayList<PathNode> openList) {
			PathNode toReturn = null;
			int lowest = Integer.MAX_VALUE;
			for(PathNode pn : openList) {
				if(pn.fScore < lowest) {
					toReturn = pn;
					lowest = pn.fScore;
				}
			}
			return toReturn;
		}
		private Stack<GameNode> makePath(PathNode start, PathNode end) {
			Stack<GameNode> shortestPath = new Stack<GameNode>();
			PathNode current = end;
			shortestPath.add(end.gNode);
			while(current.gNode != start.gNode) {
				shortestPath.add(current.parent.gNode);
				current = current.parent;
			}
			return shortestPath;
		}
		private PathNode containsNode(ArrayList<PathNode> list, GameNode node) {
			for(PathNode pn : list) {
				if(pn.gNode == node) return pn;
			}
			return null;
		}
		
		//A* path finding
		public Stack<GameNode> findShortestPath(GameNode mDestinationNode) {
			ArrayList<PathNode> openList = new ArrayList<PathNode>();
			ArrayList<PathNode> closedList = new ArrayList<PathNode>();
			
			PathNode start = new PathNode();
			start.gNode = this;
			start.gScore = 0;
			start.hScore = heuristicCostEstimate(start.gNode);
			start.fScore = start.gScore + start.hScore;
			openList.add(start);
			
			while(!openList.isEmpty()) {
				PathNode current = lowestFScore(openList);
				//if(current.fNode == mDestinationNode) return makePath(start, current);
				if(current.gNode == mDestinationNode){
					Stack<GameNode> path = makePath(start,current);
					Iterator<GameNode> iter = path.iterator();
					
					return makePath(start, current);
				}
				openList.remove(current);
				closedList.add(current);
				for(GameNode neighbor : current.gNode.mNeighbors) {
					if(neighbor.getObject() != null) {
						if(neighbor != mDestinationNode) continue;
					}
					if(containsNode(closedList,neighbor) != null) continue;
					int temp_gScore = current.gScore + 1;//nodes always have distance 1 in our case
					PathNode neighborPathNode = containsNode(openList,neighbor);
					if(neighborPathNode == null || (temp_gScore < neighborPathNode.gScore)) {
						if(neighborPathNode == null) neighborPathNode = new PathNode();
						neighborPathNode.gNode = neighbor;
						neighborPathNode.parent = current;
						neighborPathNode.gScore = temp_gScore;
						neighborPathNode.hScore = heuristicCostEstimate(neighbor);
						neighborPathNode.fScore = neighborPathNode.gScore + neighborPathNode.hScore;
						if(containsNode(openList,neighbor) == null) {
							openList.add(neighborPathNode);
						}
					}
				}
			}
			return null;//no path exists
		}
	
}
