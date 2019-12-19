package org.chaseoaks.xair_proxy;

import java.io.IOException;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Perhaps use REST Assured, instead of Karate?
 * https://github.com/rest-assured/rest-assured Answer: Yes.
 * 
 * Recommended Statics: io.restassured.RestAssured.*
 * io.restassured.matcher.RestAssuredMatchers.* org.hamcrest.Matchers.*
 * 
 * @author scollenburg
 *
 */

public class OSCProxyServerTest {

	// @Test(timeOut = 1000)
	@Test
	public void OSCProxyServer() throws IOException {

		org.chaseoaks.xair_proxy.OSCProxyServer server = new OSCProxyServer("localhost", 8123);

		server.start(3000);

		try {
			for (int i = 0; i < 6; i++) {
				if (server.helloCount > 20)
					break;
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			// noop
		}

		server.stop();

	}

}
