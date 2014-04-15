import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.Test;

import play.libs.F.Promise;
import play.mvc.Result;
import controllers.Sigfox;

/**
 * 
 * Simple (JUnit) tests that can call all parts of a play app. If you are
 * interested in mocking a whole application, see the wiki for more details.
 * 
 */
public class ApplicationTest {

	@Test
	/**
	 * Handle with care !
	 * @throws IOException
	 * @throws ParseException
	 */
	public void loadData() throws IOException, ParseException {
		for (String line : readSmallTextFile("/mnt/virtualdisk/data.csv")) {

			String[] splitLine = line.replaceAll("\"", "").split(";");
			String date = splitLine[0];
			String time = splitLine[1];
			String data = splitLine[2];

			Promise<Result> result = Sigfox.save(timeMillis(date, time), data);

			System.out.println(result.get(5, SECONDS).getWrappedResult());
		}
	}

	private long timeMillis(String date, String time) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd--HH:mm:ss");

		long result = dateFormat.parse(date + "--" + time).getTime();
		return result / 1000;
	}

	private List<String> readSmallTextFile(String aFileName) throws IOException {
		Path path = Paths.get(aFileName);
		return Files.readAllLines(path, Charset.defaultCharset());
	}
}
