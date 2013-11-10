package cz.fit.tam.model;

import cz.fit.tam.utils.HttpConnect;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class MessageQueue {
	
	public interface MessageFilter {
		public boolean isValid(Message msg);
	}
	
	private static String COMMAND_GET_MESSAGES = "get_messages";
	
	private Integer lastMessageId = 0;
	
	private Integer gameId;
	
	private URL serverUrl;
	
	private HttpConnect connection;
	
	private Player player;
	
	private List<Message> messages = new ArrayList<Message>();

	public MessageQueue(String serverUrl, Player player) throws MalformedURLException {
		this.serverUrl = new URL(serverUrl);
		this.player = player;
	}
	
	private HttpConnect getConnection() {
		if( connection == null ) {
			connection = new HttpConnect(serverUrl);
		}
		
		return connection;
	}
	
	public List<Message> sendMessage(HashMap<String, String> arguments) throws IOException {
		HttpConnect urlConnection = getConnection();
		
		return getResponse(urlConnection.post(arguments), null);
	}

	public List<Message> getMessages() {
		return messages;
	}
	
	public List<Message> getNewMessages() throws MalformedURLException, IOException {
		return getNewMessages(null);
	}
	
	public List<Message> getNewChatMessages() throws MalformedURLException, IOException {
		return getNewMessages(new MessageFilter() {

			public boolean isValid(Message msg) {
				return msg.getType().compareTo("chat") == 0;
			}
		});
	}

	public List<Message> getNewMessages(MessageFilter filter) throws MalformedURLException, IOException {
		HttpConnect urlConnection = getConnection();
		
		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("command", MessageQueue.COMMAND_GET_MESSAGES);
		arguments.put("game_id", gameId.toString());
		arguments.put("token", player.getToken().getValue());
		
		if( lastMessageId > 0 ) {
			arguments.put("since_id", lastMessageId.toString());
		}
		
		return getResponse(urlConnection.post(arguments), filter);
	}
	
	private List<Message> getResponse(String result, MessageFilter filter) {
		try {
			JSONObject json = new JSONObject(result);
			
			List<Message> newMessages = parseMessages(json, filter);
			messages.addAll(newMessages);
			
			return newMessages;
		} catch (JSONException ex) {
			Logger.getLogger(MessageQueue.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return null;
	}
	
	private List<Message> parseMessages(JSONObject json, MessageFilter filter) throws JSONException {
		List<Message> result = new ArrayList<Message>();
		
		Iterator iter = json.keys();
		while(iter.hasNext()) {
			String jsonkey = (String)iter.next();
			JSONObject msg = new JSONObject( json.getString(jsonkey) );
			
			Message message = new Message(
				gameId,
				msg.getInt("id"),
				msg.getString("type"),
				msg.getString("data"),
				null
			);
			
			lastMessageId = message.getId();
			
			if( filter == null || filter.isValid(message) ) {
				result.add(message);
			}
		}
		
		return result;
	}
	
}
