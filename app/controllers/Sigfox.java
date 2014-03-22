package controllers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.DatatypeConverter;

import play.Logger.ALogger;
import play.mvc.Controller;
import play.mvc.Result;

public class Sigfox extends Controller {

	/**
	 * Kinvey App key
	 */
	static final String APP_KEY = System.getenv("APP_KEY");

	/**
	 * Kinvey App key
	 */
	static final String MASTER_KEY = System.getenv("MASTER_KEY");

	/**
	 * URL du datastore Kinvey
	 */
	static final String dataStoreURL = "https://baas.kinvey.com/appdata/" + APP_KEY + "/hackerz/";

	public static Result endPoint() {
		ALogger logger = play.Logger.of(Sigfox.class);

		logger.error("APP_KEY: " + APP_KEY);
		logger.error("MASTER_KEY: " + MASTER_KEY);

		Map<String, String[]> form = request().body().asFormUrlEncoded();

		String latitude = form.get("slot.latitude")[0];
		String longitude = form.get("slot.longitude")[0];

		// Données à passer en POST, au format JSON
		StringBuffer data = new StringBuffer();
		data.append("{\"latitude\":\"" + latitude + "\",");
		data.append("\"longitude\":\"" + longitude + "\",");
		data.append("\"timestamp\":\"" + System.currentTimeMillis() + "\"}");

		logger.error("Json : " + data.toString());

		try {
			return status(send(connection(), data.toString()));
		} catch (IOException ioe) {
			logger.error(ioe.getMessage());
			return status(INTERNAL_SERVER_ERROR);
		}
	}

	private static HttpsURLConnection connection() throws IOException {

		URL url = new URL(dataStoreURL);
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");

		String auth = new String(APP_KEY + ":" + MASTER_KEY);
		String encodedAuth = DatatypeConverter.printBase64Binary(auth.getBytes("UTF-8"));
		con.setRequestProperty("Authorization", "Basic " + encodedAuth);
		return con;
	}

	private static int send(HttpsURLConnection connection, String data) throws IOException {
		connection.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(data);
		wr.flush();
		wr.close();

		int responseCode = connection.getResponseCode();

		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return responseCode;
	}
}