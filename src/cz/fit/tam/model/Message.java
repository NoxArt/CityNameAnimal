package cz.fit.tam.model;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Message {
	
	private Integer gameId;
	
	private Integer id;
	
	private String type;
	
	private String data;
	
	private Calendar validUntil;

	public Message(Integer gameId, int id, String type, String data, Calendar validUntil) {
		this.gameId = gameId;
		this.id = id;
		this.type = type;
		this.data = data;
		this.validUntil = validUntil;
	}

	public Integer getGameId() {
		return gameId;
	}

	public Integer getId() {
		return id;
	}
	
	public String getType() {
		return type;
	}

	public String getData() {
		return data;
	}

	public Calendar getValidUntil() {
		return validUntil;
	}
	
	public boolean isValid() {
		return validUntil.compareTo(getNow()) > 0;
	}
	
	private GregorianCalendar getNow() {
		return new GregorianCalendar();
	}
	
}
