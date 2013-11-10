package cz.fit.tam.model;

public class Game {
	
	private String name;
	
	private MessageQueue client;
	
	private Integer playerLimit;

	public Game(MessageQueue client) {
		this.client = client;
	}

	public String getName() {
		return name;
	}

	public Integer getPlayerLimit() {
		return playerLimit;
	}
		
}
