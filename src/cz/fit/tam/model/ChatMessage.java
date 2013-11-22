package cz.fit.tam.model;

import java.io.Serializable;
import java.util.Calendar;

public class ChatMessage implements Serializable {

	private Calendar postedTime;
	private String playerName;
	private String message;

	public ChatMessage(Calendar postedTime, String playerName, String message) {
		this.postedTime = postedTime;
		this.playerName = playerName;
		this.message = message;
	}

	public String getPlayerName() {
		return playerName;
	}

	public Calendar getPostedTime() {
		return postedTime;
	}

	public String getMessage() {
		return message;
	}

}
