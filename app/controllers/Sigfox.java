package controllers;

import play.Logger.ALogger;
import play.mvc.Controller;
import play.mvc.Http.RequestBody;
import play.mvc.Result;

public class Sigfox extends Controller {

	public static Result endPoint() {

		ALogger logger = play.Logger.of(Sigfox.class);

		RequestBody body = request().body();

		logger.error("lat: " + body.asFormUrlEncoded().get("slot.latitude")[0]);

		logger.error("long: " + body.asFormUrlEncoded().get("slot.longitude")[0]);

		return ok("OK.");
	}
}