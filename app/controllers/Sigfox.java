package controllers;

import java.text.SimpleDateFormat;
import java.util.Date;

import play.libs.Json;
import play.libs.WS;
import play.libs.WS.WSRequestHolder;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
	static final String dataStoreURL = "https://baas.kinvey.com/appdata/" + APP_KEY + "/trackEDF/";

	public static Result saveData(String data) {
		ObjectNode json = Json.newObject();
		json.put("tariff", extractTariff(data));
		json.put("transitionIndex", extractTransitionIndex(data));
		json.put("date", new SimpleDateFormat().format(new Date()));
		ArrayNode values = json.putArray("values");
		copyInto(values,data);
		
		WSRequestHolder request = WS.url(dataStoreURL);
		request.setAuth(APP_KEY, MASTER_KEY);
		request.post(json);
		return ok();
	}

	private static void copyInto(ArrayNode values, String data) {
		for(int i = 4; i < 24; i=i+2){
			String sVal = data.substring(i,i+2);
			values.add(Integer.parseInt(sVal,16));
		}
	}

	private static int extractTransitionIndex(String data) {
		return Integer.parseInt(data.substring(0,2));
	}

	private static String extractTariff(String data) {
		return data.substring(2, 4).equals("01")? "PEAK": "OFF_PEAK";
	}	
}