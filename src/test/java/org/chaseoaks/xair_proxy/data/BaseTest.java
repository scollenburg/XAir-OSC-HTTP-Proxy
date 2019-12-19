package org.chaseoaks.xair_proxy.data;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class BaseTest {

	@Test
	public void basic() {

	}

	class BaseFrame extends org.chaseoaks.xair_proxy.data.Base {
		public String hello = "Hello!";
		public int two = 2;
		public float threeQuarter = 0.75f;
		public Map<String, Object> recent;
		private byte[] levels = { 0x01, 0x02, 0x03, (byte) 0xff };

		public BaseFrame() {
			this.recent = new HashMap<String, Object>();
			ByteBundle bundle = new ByteBundle(this.levels);
			this.recent.put("/$bundle", bundle);
		}
	}

	@Test
	public void testToJson() {

		BaseFrame frame = new BaseFrame();
		String result = frame.toJson();
		// assertEquals(actual, expected);
		assertEquals(result,
				"{\"hello\":\"Hello!\",\"two\":2,\"threeQuarter\":0.75,\"recent\":{\"/$bundle\":[1,2,3,255]}}");
	}

}
