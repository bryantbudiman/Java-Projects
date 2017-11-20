package action_factory;

import frames.MainGUINetworked;
import game_logic.ServerGameData;
import messages.CurrentSecondMessage;
import messages.Message;
import server.Client;

public class CurrentSecondAction extends Action {
	
	@Override
	public void executeAction(MainGUINetworked mainGUI, ServerGameData gameData, Message message, Client client) {
		
		CurrentSecondMessage currentSecondMessage = (CurrentSecondMessage)message;
		mainGUI.updateCurrentSecond(currentSecondMessage.getCurrentSecond());
	
	}
}