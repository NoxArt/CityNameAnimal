package cz.fit.tam.model;

import android.text.TextUtils;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class GameClient {
	
	public class CommandFailedException extends RuntimeException {
		public CommandFailedException(Throwable throwable) {
			super(throwable);
		}
	}
	
	public class NotConnectedException extends IllegalAccessException {}
	
	private static String COMMAND_GET_GAMES = "get_games";
	private static String COMMAND_CREATE_GAME = "create_game";
	private static String COMMAND_JOIN_GAME = "join_game";
	private static String COMMAND_GET_MESSAGES = "get_messages";
	
	private MessageQueue messaging;
	
	private String playerName;
	
	private Player player;
	
	private Integer gameId;
	
	private boolean connected = false;

	public GameClient(String serverUrl, String playerName) throws MalformedURLException {
		this.messaging = new MessageQueue(serverUrl);
		this.playerName = playerName;
	}
	
	public boolean isConnected() {
		return connected;
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
		arguments.put("categories", TextUtils.join(",", game.getCategories()));
		
		try {
			JSONObject identifiers = messaging.sendMessage(arguments);
			
			Token playerToken = new Token(identifiers.getString("player_token"));
			Token adminToken = new Token(identifiers.getString("admin_token"));
			player = new Admin(adminToken, playerName, playerToken, identifiers.getInt("player_id"));
			
			gameId = identifiers.getInt("game_id");
			connected = true;
		} catch (JSONException ex) {
			Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
			throw new CommandFailedException(ex);
		} catch (IOException ex) {
			Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
			throw new CommandFailedException(ex);
		}
	}
	
	public void connect(Integer joiningGameId) {
		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("command", GameClient.COMMAND_JOIN_GAME);
		arguments.put("game_id", joiningGameId.toString());
		
		try {
			JSONObject identifiers = messaging.sendMessage(arguments);
			
			Token playerToken = new Token(identifiers.getString("player_token"));
			player = new Player(playerName, playerToken, identifiers.getInt("player_id"));
			
			gameId = joiningGameId;
			connected = true;
		} catch (JSONException ex) {
			Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
			throw new CommandFailedException(ex);
		} catch (IOException ex) {
			Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
			throw new CommandFailedException(ex);
		}
	}
	
	public List<GameProperties> getGames() {
		return getGames(null);
	}
	
	public List<GameProperties> getGames(Map<String, String> filter) {
		List<GameProperties> games = new ArrayList<GameProperties>();
		
		String filters;
		if( filter == null ) {
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
			Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
			throw new CommandFailedException(ex);
		} catch (IOException ex) {
			Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
			throw new CommandFailedException(ex);
		}

	}
	
	public List<Message> getNewMessages() throws MalformedURLException, NotConnectedException {
		return getNewMessages(null);
	}
	
	public List<Message> getNewChatMessages() throws MalformedURLException, NotConnectedException {
		return getNewMessages(new MessageQueue.MessageFilter() {

			public boolean isValid(Message msg) {
				return msg.getType().compareTo("chat") == 0;
			}
		});
	}

	public List<Message> getNewMessages(MessageQueue.MessageFilter filter) throws MalformedURLException, NotConnectedException {
		if( isConnected() == false ) {
			throw new NotConnectedException();
		}
		
		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("command", GameClient.COMMAND_GET_MESSAGES);
		arguments.put("game_id", gameId.toString());
		arguments.put("token", player.getToken().getValue());
		
		if( messaging.getLastMessageId() > 0 ) {
			arguments.put("since_id", messaging.getLastMessageId().toString());
		}
		try {
			return messaging.getMessages(arguments, filter);
		} catch (IOException ex) {
			Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}
	
}
