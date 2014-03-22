package controllers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.DatatypeConverter;

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
		Map<String, String[]> form = request().body().asFormUrlEncoded();

		String latitude = form.get("slot.latitude")[0];
		String longitude = form.get("slot.longitude")[0];

		// Données à passer en POST, au format JSON
		StringBuffer data = new StringBuffer();
		data.append("{\"latitude\":\"" + latitude + "\"}");
		data.append("{\"longitude\":\"" + longitude + "\"}");
		data.append("{\"timestamp\":\"" + System.currentTimeMillis() + "\"}");

		try {
			send(connection(), data.toString());
		} catch (IOException ioe) {
			return status(INTERNAL_SERVER_ERROR);
		}

		return ok();
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

	private static void send(HttpsURLConnection connection, String data) throws IOException {
		connection.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(data);
		wr.flush();
		wr.close();
	}
}