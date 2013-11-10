package cz.fit.tam.utils;

import cz.fit.tam.model.GameClient;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;

/**
 * Wrapper for HttpURLConnection
 * 
 * see @HttpURLConnection
 */
public class HttpConnect {

	private URL rootUrl;
	
	public HttpConnect(URL rootUrl) {
		this.rootUrl = rootUrl;
	}
	
	/**
	 * @link http://www.xyzws.com/Javafaq/how-to-use-httpurlconnection-post-data-to-web-server/139
	 * 
	 * @param parameters
	 * @return
	 * @throws IOException 
	 */
	public String post(Map<String, String> parameters) throws IOException {
		HttpsURLConnection connection = (HttpsURLConnection)rootUrl.openConnection();
		String params = buildHttpQuery(parameters);
		
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Length", "" + Integer.toString(params.getBytes().length));
		
		connection.setUseCaches (false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		
		addParameters(params, connection);
		
		try {
			InputStream stream = connection.getInputStream();
			
			return Strings.fromStream(stream);
		} catch (IOException ex) {
			Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			connection.disconnect();
		}
		
		return null;
	}
	
	/**
	 * @source http://www.xyzws.com/Javafaq/how-to-use-httpurlconnection-post-data-to-web-server/139
	 * @author XyzWS.com
	 */
	private void addParameters(String parameters, HttpsURLConnection connection) throws IOException {
		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(parameters);
		wr.flush();
		wr.close();
	}
	
	
	public static String buildHttpQuery(Map<String, String> parameters) throws UnsupportedEncodingException {
		String params = "";
		for (Map.Entry<String, String> entry: parameters.entrySet()) {
			if( params.length() > 0 ) {
				params += "&";
			}
			
			params += entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8");
		}
		
		return params;
	}
	
}
