package cz.fit.tam.model;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class MessageQueue {
	
	public interface MessageFilter {
		public boolean isValid(Message msg);
	}
	
	private Integer lastMessageId = 0;
	
	private Integer gameId;
	
	private URL serverUrl;
	
	private Player player;
	
	private List<Message> messages = new ArrayList<Message>();

	public MessageQueue(Integer gameId, String serverUrl, Player player) throws MalformedURLException {
		this.gameId = gameId;
		this.serverUrl = new URL(serverUrl);
		this.player = player;
	}
	
	public void sendMessage(Message message) {
		
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
		HttpURLConnection urlConnection = (HttpURLConnection)serverUrl.openConnection();
		try {
			JSONObject json = stringToJson(urlConnection.getInputStream());
			return parseMessages(json, filter);
		} catch (IOException ex) {
			Logger.getLogger(MessageQueue.class.getName()).log(Level.SEVERE, null, ex);
		} catch (JSONException ex) {
			Logger.getLogger(MessageQueue.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			urlConnection.disconnect();
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
	
	private JSONObject stringToJson(InputStream stream) throws JSONException, IOException {
		InputStream in = new BufferedInputStream(stream);
		
		return new JSONObject(fromStream(in));
	}
	
	public static String fromStream(InputStream in) throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder out = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			out.append(line);
		}
		return out.toString();
	}
	
}
