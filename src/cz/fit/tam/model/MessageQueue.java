package cz.fit.tam.model;

import cz.fit.tam.utils.Connector;
import cz.fit.tam.utils.HttpConnect;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageQueue implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8427178621186811143L;

	public interface MessageFilter {
		public boolean isValid(Message msg);
	}

	private Integer lastMessageId = 0;

	private Integer gameId;

	private URL serverUrl;

	private Connector connection;

	private List<Message> messages = new ArrayList<Message>();

	public MessageQueue(String serverUrl) throws MalformedURLException {
		this.serverUrl = new URL(serverUrl);
	}

	private Connector getConnection() {
		if (connection == null) {
			connection = new HttpConnect(serverUrl);
		}

		return connection;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public List<Message> getMessages(MessageFilter filter) {
		List<Message> results = new ArrayList<Message>();
		for (Message msg : messages) {
			if (filter == null || filter.isValid(msg)) {
				results.add(msg);
			}
		}

		return results;
	}

	public Integer getLastMessageId() {
		return lastMessageId;
	}

	public JSONObject sendMessage(Map<String, String> arguments)
			throws IOException {
		try {
            String result = getConnection().post(arguments);
            
            if( result.length() == 0 ) {
                return null;
            } else {
                return new JSONObject(result);
            }
		} catch (JSONException ex) {
			Logger.getLogger(MessageQueue.class.getName()).log(Level.SEVERE,
					null, ex);
			return null;
		}
	}

	public List<Message> getMessages(Map<String, String> arguments,
			MessageFilter filter) throws IOException {
		try {
			JSONObject json = new JSONObject(getConnection().post(arguments));

			List<Message> newMessages = parseMessages(json, filter);
			messages.addAll(newMessages);

			return newMessages;
		} catch (JSONException ex) {
			Logger.getLogger(MessageQueue.class.getName()).log(Level.SEVERE,
					null, ex);
			return null;
		}
	}

	private List<Message> parseMessages(JSONObject json, MessageFilter filter)
			throws JSONException {
		List<Message> result = new ArrayList<Message>();

		Iterator iter = json.keys();
		while (iter.hasNext()) {
			String jsonkey = (String) iter.next();
			JSONObject msg = new JSONObject(json.getString(jsonkey));

			Message message = new Message(gameId, msg.getInt("id"),
					msg.getString("type"), msg.getString("data"), null);

			lastMessageId = message.getId();

			if (filter == null || filter.isValid(message)) {
				result.add(message);
			}
		}

		return result;
	}

}
