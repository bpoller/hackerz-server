package controllers;

import static play.libs.Json.newObject;

import java.util.Iterator;

import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.WS;
import play.libs.WS.WSRequestHolder;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;
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

	public static Promise<Result> save(long time, String data) {
		ObjectNode json = newObject();
		json.put("tariff", extractTariff(data));
		json.put("transitionIndex", extractTransitionIndex(data));
		json.put("time", time * 1000);
		ArrayNode values = json.putArray("values");
		copyInto(values, data);

		return getRequest().post(json).map(new Function<WS.Response, Result>() {
			public Result apply(WS.Response response) {
				return status(response.getStatus(), response.getBody());
			}
		});
	}

	public static Result chart(long start, long end, String callback) {
		String resultString = callback + "(" + getData(start, end).toString() + ");";
		return ok(resultString);
	}

	public static ArrayNode getData(long start, long end) {

		ArrayNode records = (start == -1 || end == -1) ? getRecords() : getRecords(start, end);

		return reduce(records, stepSize(calculateQueryIntervalInMinutes(start, end)));
	}

	public static ArrayNode reduce(ArrayNode data, long stepSize) {

		System.out.println("StepSize : " + stepSize);

		ArrayNode result = newObject().arrayNode();
		long counter = 0;
		int mem = 0;
		Iterator<JsonNode> it = data.elements();
		JsonNode reading = null;

		while (it.hasNext()) {
			reading = it.next();
			counter++;
			mem += reading.get("value").asInt();

			if (counter % stepSize == 0) {
				ArrayNode node = newObject().arrayNode();
				node.add(reading.get("time").asLong());
				node.add(mem);
				result.add(node);
				mem = 0;
			}
		}
		return result;
	}

	public static long stepSize(long queryInterval) {
		return queryInterval < 144 ? 1 : queryInterval / 144;
	}

	/**
	 * Calculate query interval length in minutes
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static long calculateQueryIntervalInMinutes(long start, long end) {
		return Math.abs((end - start) / 60000);
	}
	
	public static ArrayNode getRecords()
	{
		return getRecords("{}");
	}

	public static ArrayNode getRecords(long start, long end) {
		
		String query = "{\"time\":{\"$gte\":"+start+",\"$lte\":"+end+"}}";
		
		System.out.println(query.toString());
		return getRecords(query.toString());
	}

	public static ArrayNode getRecords(String query) {
		WSRequestHolder request = getRequest();
		request.setQueryParameter("query", query);
		request.setQueryParameter("sort", "time");

		return request.get().map(new Function<WS.Response, ArrayNode>() {
			public ArrayNode apply(WS.Response response) {
				return (ArrayNode) response.asJson();
			}
		}).get(5000l);
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

	private static WSRequestHolder getRequest() {
		WSRequestHolder request = WS.url(dataStoreURL);
		request.setAuth(APP_KEY, MASTER_KEY);
		return request;
	}
}