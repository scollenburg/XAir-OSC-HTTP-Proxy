package org.chaseoaks.xair_proxy;

import static io.restassured.RestAssured.get;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;

import org.chaseoaks.xair_proxy.data.Base;
import org.chaseoaks.xair_proxy.servlet.OSCProxyServer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

/**
 * Perhaps use REST Assured, instead of Karate?
 * https://github.com/rest-assured/rest-assured Answer: Yes.
 * 
 * Also:
 * https://www.ontestautomation.com/creating-data-driven-api-tests-with-rest-assured-and-testng/
 * 
 * Recommended Statics: io.restassured.RestAssured.*
 * io.restassured.matcher.RestAssuredMatchers.* org.hamcrest.Matchers.*
 * 
 * Custom matchers:
 * https://stackoverflow.com/questions/45224187/hamcrest-how-to-instanceof-and-cast-for-a-matcher
 * https://www.vogella.com/tutorials/Hamcrest/article.html
 * 
 * @author scollenburg
 *
 */

public class OSCProxyServerTest {

	org.chaseoaks.xair_proxy.servlet.OSCProxyServer server = new OSCProxyServer("localhost", 8123);

	@BeforeClass
	public void startTheServer() throws IOException {

		RestAssured.defaultParser = Parser.JSON;

		server.start(3000);

		ObjectMapper mapper = Base.getMapper();
		String jsonString = null;
		try {
			jsonString = mapper.writeValueAsString(server.getHandlers());
			System.out.print("Active mappings / handlers:\n" + jsonString + "\n\n");
		} catch (JsonProcessingException e) {
			// noop
		}
	}

	@AfterClass
	public void stopTheServer() throws IOException {
		server.stop();
	}

	// @Test(timeOut = 1000)
	// @Test
	public void runTheServer() throws IOException {

		try {
			for (int i = 0; i < 600; i++) {
				if (server.stats.totalRequests > 200)
					break;
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			// noop
		}

		server.stop();

	}

	@Test(priority = -1)
	public void simpleGet() throws IOException {

		// get("http://localhost:8123/api/stats").then().body(greaterThanOrEqualTo("1"));
		// get("http://localhost:8123/api/stats").then().body("totalRequests",
		// equalTo(5));
		Response response = get("http://localhost:8123/api/stats");
		// assertEquals(response.asString(), "foo");
		assertTrue(response.asString().contains("totalRequests"), "Bad /api/stats response: " + response.asString());

		JsonPath jp = response.jsonPath();

		assertEquals((int) jp.get("totalRequests"), 1);

		get("http://localhost:8123/lorum/ipsum/should/not/be/found").then().statusCode(404);

		// Matchers.greaterThanOrEqualTo(1)
		// body("lotto.lottoId", equalTo(5));
	}

	@Test(priority = -1)
	public void simpleMeters() throws IOException {

		Response response;

		get("http://localhost:8123/meters/ipsum/should/not/be/found").then().statusCode(400);
		
		response = get("http://localhost:8123/xap/loopback/meters?status");
		assertTrue(response.asString().contains("meters"), "Bad '/meters?status' response: " + response.asString() + " ::");


	}

	@Test
	public void apiServlets() throws IOException {

		RestAssured.defaultParser = Parser.JSON;

		Response response = get("http://localhost:8123/api/servlets");
		assertTrue(response.asString().contains("API dispatcher"), "Bad /api/stats servlets: " + response.asString());

		JsonPath jp = response.jsonPath();

		// {"api":{"mapping":"api","handlerId":"api","handlerInfo":"API dispatcher"}}
		assertEquals(jp.get("api.handlerInfo"), "API dispatcher");

	}

}
