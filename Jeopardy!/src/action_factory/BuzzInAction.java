package action_factory;

import frames.MainGUINetworked;
import game_logic.ServerGameData;
import messages.BuzzInMessage;
import messages.Message;
import server.Client;

public class BuzzInAction extends Action{

	@Override
	public void executeAction(MainGUINetworked mainGUI, ServerGameData gameData,
			Message message, Client client) {
		
		BuzzInMessage buzzMessage = (BuzzInMessage) message;
		//update the team on the current question to be the one who buzzed in
		client.getCurrentQuestion().updateTeam(buzzMessage.getBuzzInTeam(), gameData);
		if(client.getTeamIndex() != buzzMessage.getBuzzInTeam()) {
			client.getCurrentQuestion().showFishAnimation();
		}
		mainGUI.moveClockAnimation(buzzMessage.getBuzzInTeam());
		mainGUI.buzzedIn();
		mainGUI.addUpdate("Team " + gameData.getTeamName(buzzMessage.getBuzzInTeam())+ " buzzed in!");
		mainGUI.setTotalSecond(20);
	}
	
}