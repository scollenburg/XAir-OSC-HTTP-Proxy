/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package org.chaseoaks.xair_proxy;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class AppTest {
	@Test
	public void appHasAGreeting() {
		App classUnderTest = new App();
		assertNotNull(classUnderTest.getGreeting(), "app should have a greeting");
	}
}
