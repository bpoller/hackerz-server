package controllers;

import java.util.Map;

import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.WS;
import play.libs.WS.WSRequestHolder;
import play.mvc.Controller;
import play.mvc.Result;

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
	static final String dataStoreURL = "https://baas.kinvey.com/appdata/" + APP_KEY + "/hackerz/";

	public static Promise<Result> lastPosition() {

		WSRequestHolder request = WS.url(dataStoreURL);
		request.setAuth(APP_KEY, MASTER_KEY);
		request.setQueryParameter("sort", "{\"timestamp\":-1}");
		request.setQueryParameter("limit", "1");

		final Promise<Result> resultPromise = request.get().map(
				new Function<WS.Response, Result>() {
					public Result apply(WS.Response response) {
						return ok(response.asJson().get(0));
					}
				});

		return resultPromise;
	}

	public static Result savePosition() {
		Map<String, String[]> form = request().body().asFormUrlEncoded();
		
		ObjectNode data = Json.newObject();
		data.put("latitude", form.get("slot.latitude")[0]);
		data.put("longitude", form.get("slot.longitude")[0]);
		data.put("timestamp", String.valueOf(System.currentTimeMillis()));

		WSRequestHolder request = WS.url(dataStoreURL);
		request.setAuth(APP_KEY, MASTER_KEY);
		request.post(data);

		return ok();
	}
}