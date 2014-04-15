package controllers;

import static play.libs.Json.newObject;

import java.util.Date;

import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.WS;
import play.libs.WS.WSRequestHolder;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Sigfox extends Controller {

	private static final int ONE_MINUTE = 60000;

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

	public static Promise<Result> save(String time, String data) {
		ObjectNode json = newObject();
		json.put("tariff", extractTariff(data));
		json.put("transitionIndex", extractTransitionIndex(data));
		json.put("time", time + "000");
		ArrayNode values = json.putArray("values");
		copyInto(values, data);

		WSRequestHolder request = WS.url(dataStoreURL);
		request.setAuth(APP_KEY, MASTER_KEY);
		return request.post(json).map(new Function<WS.Response, Result>() {
			public Result apply(WS.Response response) {
				return status(response.getStatus(), response.getBody());
			}
		});
	}

	public static Result chart(long start, long end, String callback) {
		String resultString = callback + "(" + getData(start, end).toString() + ");";
		return ok(resultString);
	}

	private static ArrayNode getData(long start, long end) {
		ArrayNode dataNode = newObject().arrayNode();
	
		 System.out.println("Start " + new Date(start));
		 System.out.println("End " + new Date(end));
		 long interval = calculateInterval(start, end);
		 System.out.println(interval);
		 
		for (long i = start; i < end; i += interval) {
			ArrayNode line = dataNode.addArray();
			line.add(i );
			line.add(Math.random() * 100);
		}

		return dataNode;
	}

	private static long calculateInterval(long start, long end) {
		long step = (end - start) / 144;
		if (step < ONE_MINUTE) {
			step = ONE_MINUTE;
		}
		return step;
	}

	private static void copyInto(ArrayNode values, String data) {
		for (int i = 4; i < 24; i = i + 2) {
			String sVal = data.substring(i, i + 2);
			values.add(Integer.parseInt(sVal, 16));
		}
	}

	private static int extractTransitionIndex(String data) {
		return Integer.parseInt(data.substring(0, 2));
	}

	private static String extractTariff(String data) {
		return data.substring(2, 4).equals("01") ? "PEAK" : "OFF_PEAK";
	}
}