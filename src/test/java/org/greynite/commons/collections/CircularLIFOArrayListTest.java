package org.greynite.commons.collections;

import org.chaseoaks.xair_proxy.data.Base;
import org.chaseoaks.xair_proxy.data.ByteBundle;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CircularLIFOArrayListTest {

	/**
	 * Warm things up so per-test timings are more accurate
	 */
	@BeforeClass
	public static void preloads() {
		CircularLIFOArrayList<Integer> lifo = new CircularLIFOArrayList<Integer>(20);

		assertEquals(lifo.size(), 0);

		lifo.add(new Integer(1));

		ObjectMapper mapper = Base.getMapper();
		@SuppressWarnings("unused")
		String jsonString = null;

		try {
			jsonString = mapper.writeValueAsString(lifo);
		} catch (JsonProcessingException e) {
			// e.printStackTrace();
		}

	}

	/**
	 * Basic test, using integer:
	 * <ul>
	 * <li>add</li>
	 * <li>size</li>
	 * <li>set</li>
	 * </ul>
	 * Also, indirectly test JSON serialization
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void BasicIntegerTest() throws JsonProcessingException {

		CircularLIFOArrayList<Integer> lifo = new CircularLIFOArrayList<Integer>(20);

		assertEquals(lifo.size(), 0);

		for (int i = 1; i < 4; i++) {
			lifo.add(new Integer(i));
		}

		assertEquals(lifo.size(), 3);

		ObjectMapper mapper = Base.getMapper();
		String jsonString = null;

		jsonString = mapper.writeValueAsString(lifo);
		assertEquals(jsonString, "[3,2,1]");

		lifo.set(0, new Integer(33));

		jsonString = mapper.writeValueAsString(lifo);
		assertEquals(jsonString, "[33,2,1]");

		lifo.set(2, new Integer(111));

		jsonString = mapper.writeValueAsString(lifo);
		assertEquals(jsonString, "[33,2,111]");

	}

	/**
	 * Test wrap around (circular) logic by adding >capacity entries. Also, test
	 * clear()
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void CircularIntegerTest() throws JsonProcessingException {
		CircularLIFOArrayList<Integer> lifo = new CircularLIFOArrayList<Integer>(20);

		subTestCircularInteger(lifo);
		lifo.clear();
		subTestCircularInteger(lifo);
	}

	public void subTestCircularInteger(CircularLIFOArrayList<Integer> lifo) throws JsonProcessingException {

		assertEquals(lifo.size(), 0);

		for (int i = 0; i < 24; i++) {
			lifo.add(new Integer(i));
		}

		assertEquals(lifo.size(), 20);

		lifo.add(new Integer(100));

		ObjectMapper mapper = Base.getMapper();
		String jsonString = null;

		jsonString = mapper.writeValueAsString(lifo);
		assertEquals(jsonString, "[100,23,22,21,20,19,18,17,16,15,14,13,12,11,10,9,8,7,6,5]");
	}

	/**
	 * Test using an alternate <T> type than Integer. Test add(), size(), and wrap
	 * around.
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void basicByteBufferTest() throws JsonProcessingException {
		CircularLIFOArrayList<ByteBundle> lifo = new CircularLIFOArrayList<ByteBundle>(5);

		// lb.add(new byte[] { 0x01 });
		// lb.add(new byte[] { 0x02, 0x02 });
		// lb.add(new byte[] { 0x03, 0x03, 0x03 });
		lifo.add(new ByteBundle(new byte[] { 0x01 }));
		lifo.add(new ByteBundle(new byte[] { 0x02, 0x02 }));
		lifo.add(new ByteBundle(new byte[] { 0x03, 0x03, 0x03 }));

		assertEquals(lifo.size(), 3);

		ObjectMapper mapper = Base.getMapper();
		String jsonString = null;

		jsonString = mapper.writeValueAsString(lifo);
		assertEquals(jsonString, "[[3,3,3],[2,2],[1]]");

		lifo.add(new ByteBundle(new byte[] { 0x04 }));
		lifo.add(new ByteBundle(new byte[] { 0x05, 0x06 }));
		lifo.add(new ByteBundle(new byte[] { 0x07, 0x08, 0x09 }));

		jsonString = mapper.writeValueAsString(lifo);
		assertEquals(jsonString, "[[7,8,9],[5,6],[4],[3,3,3],[2,2]]");

	}

}
