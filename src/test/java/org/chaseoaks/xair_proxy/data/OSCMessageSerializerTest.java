package org.chaseoaks.xair_proxy.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.illposed.osc.OSCMessage;

/**
 * TODO: Convert this to using @RunWith(value = Parameterized.class)
 * https://www.mkyong.com/unittest/junit-4-tutorial-6-parameterized-test/
 * https://github.com/junit-team/junit4/wiki/Parameterized-tests
 */
public class OSCMessageSerializerTest {

	public Map<String, OSCMessage> getTests() {
		Map<String, OSCMessage> tests = new TreeMap<String, OSCMessage>();

		OSCMessage mut;
		List<Object> args;

		mut = new OSCMessage("/xinfo");
		tests.put("{\"address\":\"/xinfo\"}", mut);

		args = new ArrayList<Object>();
		args.add("foo");
		args.add("bar");

		mut = new OSCMessage("/args1", args);
		tests.put("{\"address\":\"/args1\",\"arguments\":[\"foo\",\"bar\"]}", mut);

		args = new ArrayList<Object>();
		args.add("meter/ch01");
		args.add(new Integer(22));

		mut = new OSCMessage("/args2", args);
		tests.put("{\"address\":\"/args2\",\"arguments\":[\"meter/ch01\",22]}", mut);

		args = new ArrayList<Object>();
		args.add(new Float(0.000001f));
		args.add(new Float(0.1f));
		args.add(new Float(0.5f));
		args.add("Biff!");
		args.add(new Float(0.8f));
		args.add(new Float(0.99f));
		args.add(new Float(0.9987f));

		mut = new OSCMessage("/args2", args);
		tests.put("{\"address\":\"/args2\",\"arguments\":[1.0E-6,0.1,0.5,\"Biff!\",0.8,0.99,0.9987]}", mut);

		args = new ArrayList<Object>();
		args.add(new Float(0.1f));
		args.add(new Float(0.77f));
		args.add("Biff!");
		args.add(new Float(0.0888f));

		mut = new OSCMessage("/deserializeCoerce1", args);
		tests.put("{\"address\":\"/deserializeCoerce1\",\"arguments\":[0.1,\"0.77\",\"Biff!\",8.88E-2]}", mut);

		return tests;
	}

	@Test
	public void serializeTest() {

		String json = "--nope--";

		Map<String, OSCMessage> tests = getTests();

		Iterator<Entry<String, OSCMessage>> it = tests.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, OSCMessage> pair = it.next();
			if (pair.getKey().contains("deserial"))
				continue;
			json = serialize(pair.getValue());
			assertEquals(json, pair.getKey());
		}

	}

	public String serialize(OSCMessage message) {

		ObjectMapper mapper = Base.getMapper();
		String jsonString = null;
		try {
			jsonString = mapper.writeValueAsString(message);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return jsonString;

	}

	@Test
	public void deserializeTest() {

		// String json = "--nope--";
		OSCMessage msg;

		Map<String, OSCMessage> tests = getTests();

		Iterator<Entry<String, OSCMessage>> it = tests.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, OSCMessage> pair = it.next();
			// if (pair.getKey().contains("deserial"))
			// continue;
			msg = deserialize(pair.getKey());
			// assertEquals(msg, pair.getValue());
			assertEquals(msg.getAddress(), pair.getValue().getAddress(), "Address does not match:");
			if (msg.getAddress().contains("Coerce")) {
				assertNotEquals(msg.getArguments(), pair.getValue().getArguments(), "Argument coercion did not work:");
			} else {
				assertEquals(msg.getArguments(), pair.getValue().getArguments(), "Arguments do not match:");
			}
		}
	}

	public OSCMessage deserialize(String smsg) {

		ObjectMapper mapper = Base.getMapper();
		OSCMessage message = null;
		try {
			message = mapper.readValue(smsg, OSCMessage.class);
		} catch (Exception e) {
			System.out.print("Exception during deserialize:\n\n  \"" + smsg + "\"\n");
			e.printStackTrace();
		}
		return message;
	}
}
