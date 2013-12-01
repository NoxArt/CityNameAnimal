package cz.fit.tam.model;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import java.util.Iterator;

public class GameClient implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9185321872573695849L;

	public class CommandFailedException extends RuntimeException {
		public CommandFailedException(Throwable throwable) {
			super(throwable);
		}
	}

	public class NotConnectedException extends IllegalAccessException {
	}

	private static String COMMAND_GET_GAMES = "get_games";
	private static String COMMAND_GET_PLAYERS = "get_players";
	private static String COMMAND_CREATE_GAME = "create_game";
	private static String COMMAND_STOP_GAME = "stop_game";
	private static String COMMAND_START_GAME = "start_game";
	private static String COMMAND_JOIN_GAME = "join_game";
	private static String COMMAND_LEAVE_GAME = "leave_game";
	private static String COMMAND_GET_MESSAGES = "get_messages";
	private static String COMMAND_POST_MESSAGE = "post_message";
    private static String COMMAND_GET_SCORES = "get_scores";
	private static String ACTION_SEND_WORDS = "send_words";
	public static String CHATMESSAGE_TYPE = "chat";
	public static String ROUND_STARTED_TYPE = "round_started";
	public static String GAME_FINISHED_TYPE = "game_finished";

	private MessageQueue messaging;

	private String playerName;

	private Player player;

	private Integer gameId;

	private boolean connected = false;

	public GameClient(String serverUrl, String playerName)
			throws MalformedURLException {
		this.messaging = new MessageQueue(serverUrl);
		this.playerName = playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public boolean isConnected() {
		return connected;
	}

	public Player getPlayer() {
		return player;
	}

	public void createGame(GameProperties game) {
		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("command", GameClient.COMMAND_CREATE_GAME);
		arguments.put("name", game.getName());
		arguments.put("language", game.getLanguage());
		arguments.put("player_limit", game.getPlayerLimit().toString());
		arguments.put("time_limit", game.getTimeLimit().toString());
		arguments.put("round_limit", game.getRoundLimit().toString());
		arguments.put("evaluation", game.getEvaluation().toString());
		arguments.put("categories", combine(game.getCategories()));
		arguments.put("player_name", playerName);

		try {
			JSONObject identifiers = messaging.sendMessage(arguments)
					.getJSONObject("result");
			Token playerToken = new Token(identifiers.getString("player_token"));
			Token adminToken = new Token(identifiers.getString("admin_token"));
			player = new Admin(adminToken, playerName, playerToken,
					identifiers.getInt("player_id"));

			gameId = identifiers.getInt("id");
			try {
				game.setId(gameId);
			} catch (Exception e) {
				Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE,
						null, e);
			}
			connected = true;
		} catch (JSONException ex) {
			Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE,
					null, ex);
			throw new CommandFailedException(ex);
		} catch (IOException ex) {
			Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE,
					null, ex);
			throw new CommandFailedException(ex);
		}
	}

	public void connect(Integer joiningGameId) {
		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("command", GameClient.COMMAND_JOIN_GAME);
		arguments.put("game_id", joiningGameId.toString());
		arguments.put("player_name", playerName);

		try {
			JSONObject identifiers = messaging.sendMessage(arguments)
					.getJSONObject("result");

			Token playerToken = new Token(identifiers.getString("player_token"));
			player = new Player(playerName, playerToken,
					identifiers.getInt("player_id"));

			gameId = joiningGameId;
			connected = true;
			messaging.setGameId(joiningGameId);
		} catch (JSONException ex) {
			Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE,
					null, ex);
			throw new CommandFailedException(ex);
		} catch (IOException ex) {
			Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE,
					null, ex);
			throw new CommandFailedException(ex);
		}
	}

	public void stop(Integer gameId) {
		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("command", GameClient.COMMAND_STOP_GAME);
		arguments.put("id", gameId.toString());
		arguments.put("admin_token", ((Admin) player).getAdminToken()
				.getValue());

		try {
			messaging.sendMessage(arguments);
			connected = false;
		} catch (IOException ex) {
			Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE,
					null, ex);
			throw new CommandFailedException(ex);
		}
	}

	public void leave(Integer gameId) {
		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("command", GameClient.COMMAND_LEAVE_GAME);
		arguments.put("game_id", gameId.toString());
		arguments.put("token", player.getToken().getValue());

		try {
			messaging.sendMessage(arguments);
			connected = false;
		} catch (IOException ex) {
			Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE,
					null, ex);
			throw new CommandFailedException(ex);
		}
	}

	public void sendChatMessage(String message) {
		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("command", GameClient.COMMAND_POST_MESSAGE);
		arguments.put("game_id", gameId.toString());
		arguments.put("token", player.getToken().getValue());
		arguments.put("type", GameClient.CHATMESSAGE_TYPE);
		arguments.put("data", message);
		try {
			messaging.sendMessage(arguments);
		} catch (IOException ex) {
			Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE,
					null, ex);
			throw new CommandFailedException(ex);
		}
	}

	public void sendWords(Integer round, String[] words) {
		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("command", GameClient.COMMAND_POST_MESSAGE);
		arguments.put("game_id", gameId.toString());
		arguments.put("token", player.getToken().getValue());
		arguments.put("type", GameClient.ACTION_SEND_WORDS);

		Map<String, String> data = new HashMap<String, String>();
		data.put("round", round.toString());
		data.put("words", combine(words));
		arguments.put("data", (new JSONObject(data)).toString());

		try {
			messaging.sendMessage(arguments);
			// connected = false;
		} catch (IOException ex) {
			Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE,
					null, ex);
			throw new CommandFailedException(ex);
		}
	}

	public void startGame(Integer gameId) {
		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("command", GameClient.COMMAND_START_GAME);
		arguments.put("id", gameId.toString());
		Admin admin = (Admin) player;
		arguments.put("admin_token", admin.getAdminToken().getValue());
		// arguments.put("token", admin.getToken().getValue());

		try {
			messaging.sendMessage(arguments);
		} catch (IOException ex) {
			Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE,
					null, ex);
			throw new CommandFailedException(ex);
		}
	}

	/*
	 * public void sendEvaluation(Map<String, String[]> evaluations) {
	 * Map<String, String> arguments = new HashMap<String, String>();
	 * arguments.put("command", GameClient.COMMAND_POST_MESSAGE);
	 * arguments.put("game_id", gameId.toString()); arguments.put("token",
	 * player.getToken().getValue());
	 * 
	 * Map<String, String> data = new HashMap<String, String>();
	 * data.put("action", GameClient.ACTION_SEND_EVALUATION);
	 * data.put("evaluations", combine(evaluations));
	 * 
	 * 
	 * try { messaging.sendMessage(arguments); connected = false; } catch
	 * (IOException ex) {
	 * Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
	 * throw new CommandFailedException(ex); } }
	 */

	public List<GameProperties> getGames() {
		return getGames(null);
	}

	public List<GameProperties> getGames(Map<String, String> filter) {
		List<GameProperties> games = new ArrayList<GameProperties>();

		String filters;
		if (filter == null) {
			filters = "{}";
		} else {
			filters = (new JSONObject(filter)).toString();
		}

		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("command", GameClient.COMMAND_GET_GAMES);
		arguments.put("game_filter", filters);

		try {
			JSONObject result = messaging.sendMessage(arguments);
			JSONArray results = result.getJSONArray("result");

			for (int i = 0; i < results.length(); i++) {
				JSONObject game = results.getJSONObject(i);

				games.add(GameProperties.jsonToGame(game));
			}

			return games;
		} catch (JSONException ex) {
			Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE,
					null, ex);
			throw new CommandFailedException(ex);
		} catch (IOException ex) {
			Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE,
					null, ex);
			throw new CommandFailedException(ex);
		}

	}
    
    public Map<String, Integer> getScores() {
		Map<String, Integer> scores = new HashMap<String, Integer>();

		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("command", GameClient.COMMAND_GET_SCORES);
		arguments.put("game_id", gameId.toString());

		try {
			JSONObject result = messaging.sendMessage(arguments);
            if( result == null ) {
                return scores;
            }
            
            result = result.getJSONObject("result");
            Iterator<?> keys = result.keys();
            while(keys.hasNext()) {
                String key = (String)keys.next();
                
                scores.put(key, result.getInt(key));
            }

			return scores;
		} catch (JSONException ex) {
			Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE,
					null, ex);
			throw new CommandFailedException(ex);
		} catch (IOException ex) {
			Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE,
					null, ex);
			throw new CommandFailedException(ex);
		}

	}

	public List<Player> getPlayers(Integer gameId) {
		List<Player> players = new ArrayList<Player>();

		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("command", GameClient.COMMAND_GET_PLAYERS);
		arguments.put("token", player.getToken().getValue());
		arguments.put("game_id", gameId.toString());

		try {
			// String results = messaging.sendMessage(arguments).getString(
			// "result");
			JSONArray results = messaging.sendMessage(arguments).getJSONArray(
					"result");
			for (int i = 0; i < results.length(); i++) {
				players.add(new Player(results.getString(i)));
			}

			return players;
		} catch (JSONException ex) {
			Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE,
					null, ex);
			throw new CommandFailedException(ex);
		} catch (IOException ex) {
			Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE,
					null, ex);
			throw new CommandFailedException(ex);
		}

	}

	public List<Message> getNewMessages() throws MalformedURLException,
			NotConnectedException, JSONException {
		return getNewMessages(null);
	}

	public List<Message> getNewChatMessages() throws MalformedURLException,
			NotConnectedException, JSONException {
		return getNewMessages(new MessageQueue.MessageFilter() {

			public boolean isValid(Message msg) {
				return msg.getType().compareTo("chat") == 0;
			}
		});
	}

	public List<Message> getNewMessages(MessageQueue.MessageFilter filter)
			throws MalformedURLException, NotConnectedException, JSONException {
		if (isConnected() == false) {
			throw new NotConnectedException();
		}

		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("command", GameClient.COMMAND_GET_MESSAGES);
		arguments.put("game_id", gameId.toString());
		arguments.put("token", player.getToken().getValue());

		if (messaging.getLastMessageId() > 0) {
			arguments.put("since_id", messaging.getLastMessageId().toString());
		}
		try {
			return messaging.getMessages(arguments, filter);
		} catch (IOException ex) {
			Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE,
					null, ex);
			return null;
		}
	}

	private String combine(Map<String, String[]> values) {
		String evaluation[] = new String[values.size()];
		int i = 0;
		for (String[] words : values.values()) {
			evaluation[i++] = combine(words);
		}

		return combine(evaluation, "|");
	}

	private String combine(String[] strings) {
		return combine(strings, ",");
	}

	private String combine(String[] strings, String glue) {
		return TextUtils.join(glue, strings);
	}

}
