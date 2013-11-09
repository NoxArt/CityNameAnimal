package cz.fit.tam.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Chat {
	
	private Player currentPlayer;
	private List<ChatMessage> messages = new ArrayList<ChatMessage>();
	
	private String timeFormat = "dd.MM. HH:mm:ss";

	public Chat(Player currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}

	public List<ChatMessage> getMessages() {
		return messages;
	}
	
	public List<String> getFormattedMessages() {
		List<String> result = new ArrayList<String>();
		String line;
		
		SimpleDateFormat format = new SimpleDateFormat(timeFormat);
		
		for(ChatMessage msg: messages) {
			line  = "[" + format.format( msg.getPostedTime().getTime() ) + "] ";
			line += msg.getPlayerName() + ": ";
			line += msg.getMessage().replace(currentPlayer.getName(), "[currentPlayer]" + currentPlayer.getName() + "[/currentPlayer]");
			
			result.add(line);
		}
		
		return result;
	}
	
}
