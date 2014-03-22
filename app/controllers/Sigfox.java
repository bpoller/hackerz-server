package controllers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.DatatypeConverter;

import play.Logger.ALogger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class Sigfox extends Controller {

	static final ALogger logger = play.Logger.of(Sigfox.class);

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

	public static Result lastPosition() {
		ObjectNode result = Json.newObject();
		result.put("lat", 43.2537875);
		result.put("lon", 1.2096094);
		return ok(result);
	}

	public static Result endPoint() {

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
			return status(post(connection(dataStoreURL), data.toString()));
		} catch (IOException ioe) {
			logger.error(ioe.getMessage());
			return status(INTERNAL_SERVER_ERROR);
		}
	}

	private static HttpsURLConnection connection(String theURL) throws IOException {
		URL url = new URL(theURL);
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		con.setRequestProperty("Content-Type", "application/json");
		return authenticate(con);
	}

	private static HttpsURLConnection authenticate(HttpsURLConnection con)
			throws UnsupportedEncodingException {
		String auth = new String(APP_KEY + ":" + MASTER_KEY);
		String encodedAuth = DatatypeConverter.printBase64Binary(auth.getBytes("UTF-8"));
		con.setRequestProperty("Authorization", "Basic " + encodedAuth);
		return con;
	}

	private static int post(HttpsURLConnection connection, String data) throws IOException {
		connection.setRequestMethod("POST");
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