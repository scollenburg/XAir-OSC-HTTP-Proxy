package org.chaseoaks.xair_proxy.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.ObjectArrayDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.illposed.osc.OSCMessage;

public class OSCMessageDeserializrV1 extends StdDeserializer<Object> {

	private static final long serialVersionUID = -7337583058525448642L;

	protected OSCMessageDeserializrV1(Class vc) {
		super(vc);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		OSCMessage message = deserializeOSCMessage(p, ctxt);
		return message;

	}

	public OSCMessage deserializeOSCMessage(JsonParser p, DeserializationContext ctxt) throws IOException {

		String address = "";
		List<Object> args = null; // = new ArrayList<Object>();

		JsonToken t;
		while ((t = p.nextToken()) != null) {
			if (t == JsonToken.END_ARRAY)
				break;
			if (t == JsonToken.FIELD_NAME) {
				if (t.toString().equals("address")) {
					t = p.nextToken();
					address = t.asString();
				}

			}

		}

		if (args != null && args.size() > 0) {
			return new OSCMessage(address, args);
		} else {
			return new OSCMessage(address);

		}

	}

}
