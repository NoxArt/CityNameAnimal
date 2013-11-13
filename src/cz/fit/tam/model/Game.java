package cz.fit.tam.model;

import java.util.ArrayList;
import java.util.List;

public class Game {
	
	private Integer id;
	
	private GameProperties properties;
	
	private GameClient client;
	
	private boolean running;	
	
	private List<ChatMessage> chatMessages = new ArrayList<ChatMessage>();

	public Game(GameProperties properties, GameClient client) {
		this.properties = properties;
		this.client = client;
	}

	public GameProperties getProperties() {
		return properties;
	}

	public Integer getId() {
		return id;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public boolean isConnected() {
		return client.isConnected();
	}

	public List<ChatMessage> getChatMessages() {
		return chatMessages;
	}

	public void create() {
		if( isConnected() ) {
			throw new IllegalStateException();
		}
		
		client.createGame(properties);
	}
	
	public void connect(Integer id) {
		if( isConnected() ) {
			throw new IllegalStateException();
		}
	
		this.id = id;
		
		client.connect(id);
	}
	
}
