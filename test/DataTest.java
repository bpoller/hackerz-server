import static controllers.Sigfox.calculateQueryIntervalInMinutes;
import static controllers.Sigfox.getFirstDate;
import static controllers.Sigfox.getLastDate;
import static controllers.Sigfox.getRecords;
import static controllers.Sigfox.stepSize;
import static org.junit.Assert.assertEquals;
import static play.libs.Json.newObject;

import java.util.Date;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.Sigfox;

public class DataTest {

	private static final long ONE_DAY = 24 * 60 * 60 * 1000;
	private static final long NOW = System.currentTimeMillis();

	@Test
	public void shouldReturnValidData() {
		ArrayNode result = (ArrayNode) getRecords(NOW-ONE_DAY-ONE_DAY, NOW);

		System.out.println("NOw : " + new Date(NOW));

		System.out.println("Yesterday : " + new Date(NOW - ONE_DAY));

		Iterator<JsonNode> i = result.elements();
		while (i.hasNext()) {
			JsonNode node = i.next();
			System.out.println(new Date(node.get("time").asLong()));
		}
	}
	//query={"age":{"$gte": 31}}
	@Test
	public void testQueryInterval() {
		assertEquals(60 * 24, calculateQueryIntervalInMinutes(NOW - ONE_DAY, NOW));
	}

	@Test
	public void testCalcStepSize() {
		assertEquals(1, stepSize(60));
		assertEquals(13, stepSize(2000));
	}

	@Test
	public void testReduce() {
		ArrayNode result = Sigfox.reduce(testData(), 3);

		System.out.println(result.toString());
	}
	
	@Test
	public void DateTest(){
		System.out.println(new Date(1397689267000l));
		
	}
	
	@Test
	public void testDate()
	{
		Assert.assertTrue(getFirstDate() < getLastDate());
	}

	private ArrayNode testData() {

		ArrayNode result = newObject().arrayNode();

		for (int i = 1; i < 11; i++) {
			ObjectNode node = newObject();
			node.put("time", i);
			node.put("value", i);
			result.add(node);
		}
		return result;
	}
}
