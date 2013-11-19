package cz.fit.tam.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Game implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1143771934418399254L;

	public class GameIsStoppedException extends RuntimeException {}
    
    public class NotConnectedException extends RuntimeException {}
    
    public class AlreadyConnectedException extends RuntimeException {}
    
    public class NotAdminException extends RuntimeException {}

	private GameProperties properties;

	private GameClient client;

	private boolean running;

	private boolean stopped = false;

	private List<ChatMessage> chatMessages = new ArrayList<ChatMessage>();

	public Game(GameProperties properties, GameClient client) {
		this.properties = properties;
		this.client = client;
	}

	public GameProperties getProperties() {
		return properties;
	}

	public GameClient getClient() {
		return client;
	}

	public Integer getId() {
		return getProperties().getId();
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isStopped() {
		return stopped;
	}

	public boolean isAdmin() {
		return client.getPlayer() instanceof Admin;
	}

	public boolean isConnected() {
		return client.isConnected();
	}

	public List<ChatMessage> getChatMessages() {
		return chatMessages;
	}

	public void create() {
		if (isConnected()) {
			throw new AlreadyConnectedException();
		}

		client.createGame(properties);
		properties.incrementNumberOfPlayers();
	}

	public void connect(Integer id) {
		if (isConnected() || this.getProperties().getId() != null) {
			throw new AlreadyConnectedException();
		}

		if (isStopped()) {
			throw new GameIsStoppedException();
		}

		try {
			this.getProperties().setId(id);
		} catch (Exception ex) {
			Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
		}

		client.connect(id);
		properties.incrementNumberOfPlayers();
		try {
			properties.setId(id);
		} catch (Exception ex) {
			Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public List<GameProperties> getGames() {
		return client.getGames();
	}

	public void stop() {
		if (isConnected() == false) {
			throw new NotConnectedException();
		}
        
        if (isAdmin() == false) {
            throw new NotAdminException();
        }

		client.stop(properties.getId());
		stopped = true;
	}

	public void leave() {
		if (isConnected() == false) {
			throw new NotConnectedException();
		}

		client.leave(properties.getId());
		stopped = true;
	}

	public void sendWords(Integer round, String[] words) {
		if (isConnected()) {
			throw new NotConnectedException();
		}

		if (isStopped()) {
			throw new GameIsStoppedException();
		}

		client.sendWords(round, words);
	}

    /*
	public void sendEvaluations(Map<String, String[]> evaluations) {
		if (isConnected()) {
			throw new IllegalStateException();
		}

		if (isStopped()) {
			throw new GameIsStoppedException();
		}

		client.sendEvaluation(evaluations);
	}
    */

}
