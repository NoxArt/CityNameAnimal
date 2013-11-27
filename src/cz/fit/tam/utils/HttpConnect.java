package cz.fit.tam.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cz.fit.tam.model.MessageQueue;

/**
 * Wrapper for HttpURLConnection
 * 
 * see @HttpURLConnection
 */
public class HttpConnect implements Connector, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2749074164793695726L;
	private URL rootUrl;

	public HttpConnect(URL rootUrl) {
		this.rootUrl = rootUrl;
	}

	class TrustEveryoneManager implements X509TrustManager {
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
		}

		public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

	public class NullHostNameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	/**
	 * @link 
	 *       http://www.xyzws.com/Javafaq/how-to-use-httpurlconnection-post-data-
	 *       to-web-server/139
	 * 
	 * @param parameters
	 * @return
	 * @throws IOException
	 */
	public String post(Map<String, String> parameters) throws IOException {
		HttpsURLConnection connection = (HttpsURLConnection) rootUrl
				.openConnection();

		connection.setHostnameVerifier(new NullHostNameVerifier());
		TrustManager[] myTrustManagerArray = new TrustManager[] { new TrustEveryoneManager() };

		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, myTrustManagerArray, new java.security.SecureRandom());
			connection.setSSLSocketFactory(sc.getSocketFactory());
		} catch (NoSuchAlgorithmException ex) {
			Logger.getLogger(HttpConnect.class.getName()).log(Level.SEVERE,
					null, ex);
		} catch (KeyManagementException ex) {
			Logger.getLogger(HttpConnect.class.getName()).log(Level.SEVERE,
					null, ex);
		}

		String params = buildHttpQuery(parameters);

		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Length",
				"" + Integer.toString(params.getBytes().length));

		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);

		addParameters(params, connection);

		String result = null;
		try {
			InputStream stream = connection.getInputStream();

			result = Strings.fromStream(stream);

			Logger.getLogger(parameters.get("command")).log(Level.WARNING,
					params);
			Logger.getLogger(parameters.get("command")).log(Level.WARNING,
					result);
		} catch (IOException ex) {
			Logger.getLogger(MessageQueue.class.getName()).log(Level.SEVERE,
					null, ex);
		} finally {
			connection.disconnect();
		}

		return result;
	}

	/**
	 * @source 
	 *         http://www.xyzws.com/Javafaq/how-to-use-httpurlconnection-post-data
	 *         -to-web-server/139
	 * @author XyzWS.com
	 */
	private void addParameters(String parameters, HttpsURLConnection connection)
			throws IOException {
		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(parameters);
		wr.flush();
		wr.close();
	}

	private static String buildHttpQuery(Map<String, String> parameters)
			throws UnsupportedEncodingException {
		String params = "";
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			if (params.length() > 0) {
				params += "&";
			}

			params += entry.getKey() + "="
					+ URLEncoder.encode(entry.getValue(), "UTF-8");
		}

		return params;
	}

}
