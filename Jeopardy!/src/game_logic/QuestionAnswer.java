package game_logic;

import java.io.Serializable;

public class QuestionAnswer implements Serializable{
	
	protected String question;
	protected String answer;
	protected Boolean asked;
	protected String category;
	protected int pointValue;
	
	//indices of this question
	private int x;
	private int y;
	
	public QuestionAnswer(String question, String answer, String category, int pointValue, int x, int y){
		this.question = question;
		this.answer = answer;
		this.category = category;
		this.pointValue = pointValue;
		this.x = x;
		this.y = y;
		
		asked = false;
	}
	
	//GETTERS
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public String getQuestion(){
		return question;
	}
	
	public String getAnswer(){
		return answer;
	}
	
	public String getCategory(){
		return category;
	}
	
	public int getPointValue(){
		return pointValue;
	}
	
	public void setAsked(){
		asked = true;
	}
	
	public boolean isAsked(){
		return asked;
	}
	
	public static boolean correctAnswer(String givenAnswer, String expectedAnswer){
		if (givenAnswer.trim().toLowerCase().endsWith(expectedAnswer.toLowerCase())){
			return true;
		}
		
		return false; 
	}

}