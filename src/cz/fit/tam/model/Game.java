package cz.fit.tam.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.util.Log;

public class Game implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1143771934418399254L;

	public static final String PHASE_INITIALIZED = "initalize";
	public static final String PHASE_WAITING = "waiting";
	public static final String PHASE_RUNNING = "running";
	public static final String PHASE_FINISHED = "finished";

	public class GameIsStoppedException extends RuntimeException {
	}

	public class NotConnectedException extends RuntimeException {
	}

	public class AlreadyConnectedException extends RuntimeException {
	}

	public class NotAdminException extends RuntimeException {
	}

	private GameProperties properties;

	private GameClient client;

	private String phase = PHASE_INITIALIZED;

	private Integer startsAt = null;

	private List<ChatMessage> chatMessages = new ArrayList<ChatMessage>();

	public Game(GameProperties properties, GameClient client) {
		this.properties = properties;
		this.client = client;
	}

	public GameProperties getProperties() {
		return properties;
	}

	public void setProperties(GameProperties properties) {
		this.properties = properties;
	}

	public GameClient getClient() {
		return client;
	}

	public Integer getId() {
		return getProperties().getId();
	}

	public boolean isRunning() {
		return phase.compareTo(PHASE_RUNNING) == 0
				|| (startsAt != null && startsAt > (new Date()).getTime());
	}

	public boolean isStopped() {
		return phase.compareTo(PHASE_FINISHED) == 0;
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
		if (isConnected()) {
			throw new AlreadyConnectedException();
		}

		if (isStopped()) {
			throw new GameIsStoppedException();
		}

		client.connect(id);
		properties.incrementNumberOfPlayers();
		try {
			properties.setId(id);
		} catch (Exception ex) {
			Logger.getLogger(Game.class.getName()).log(Level.INFO, null, ex);
		}
	}

	public void startGame() {
		client.startGame(getId());
	}

	public List<GameProperties> getGames() {
		return client.getGames();
	}

	public List<Player> getPlayers() {
		if (isConnected() == false) {
			throw new NotConnectedException();
		}

		return client.getPlayers(getId());
	}

	public void stop() {
		Log.i("Stop game with id", String.valueOf(getId()));
		if (isConnected() == false) {
			throw new NotConnectedException();
		}

		if (isAdmin() == false) {
			throw new NotAdminException();
		}

		client.stop(properties.getId());
		phase = PHASE_FINISHED;
	}

	public void leave() {
		if (isConnected() == false) {
			throw new NotConnectedException();
		}

		client.leave(properties.getId());
		phase = PHASE_FINISHED;
	}

	public void sendWords(Integer round, String[] words) {
		if (isConnected() == false) {
			throw new NotConnectedException();
		}

		if (isStopped()) {
			throw new GameIsStoppedException();
		}

		client.sendWords(round, words);
	}

	/*
	 * public void sendEvaluations(Map<String, String[]> evaluations) { if
	 * (isConnected()) { throw new IllegalStateException(); }
	 * 
	 * if (isStopped()) { throw new GameIsStoppedException(); }
	 * 
	 * client.sendEvaluation(evaluations); }
	 */

}
