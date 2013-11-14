package cz.fit.tam.model;

import android.content.Context;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Game {
	
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
		return getProperties().getId();
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
		if( isConnected() || this.getProperties().getId() != null ) {
			throw new IllegalStateException();
		}
		try {
			this.getProperties().setId(id);
		} catch (Exception ex) {
			Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		client.connect(id);
	}
	
	public List<GameProperties> getGames(Context c) {
		return client.getGames();
	}
	
}
