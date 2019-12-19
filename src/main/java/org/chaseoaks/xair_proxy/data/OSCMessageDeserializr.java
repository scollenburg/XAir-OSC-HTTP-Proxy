package org.chaseoaks.xair_proxy.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import com.illposed.osc.OSCMessage;

public class OSCMessageDeserializr<T extends OSCMessage> extends StdNodeBasedDeserializer<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1159340157733167487L;

	public OSCMessageDeserializr(JavaType targetType) {
		super(targetType);
	}

	public OSCMessageDeserializr(Class<T> targetType) {
		super(targetType);
	}

	public OSCMessageDeserializr(StdNodeBasedDeserializer<?> src) {
		super(src);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T convert(JsonNode root, DeserializationContext ctxt) throws IOException {

		if (this._valueClass.isAssignableFrom(OSCMessage.class)) {
			OSCMessage message = convertToOSCMesage(root, ctxt);
			return (T) message;
		}
		return null;
	}

	protected OSCMessage convertToOSCMesage(JsonNode root, DeserializationContext ctxt) throws IOException {

		String address = "";
		List<Object> args = null; // = new ArrayList<Object>();

		address = root.get("address").asText();

		if (root.has("arguments")) {
			if (args == null)
				args = new ArrayList<Object>();
			for (Iterator<JsonNode> iterator = root.get("arguments").elements(); iterator.hasNext();) {
				JsonNode e = iterator.next();
				if (e == null)
					continue;
				if (e.isFloatingPointNumber()) {
					args.add(new Float(e.asDouble()));
				} else if (e.isNumber()) {
					args.add(new Integer(e.asInt(0)));
				} else {
					args.add(e.asText());
				}
			}
		}

		if (args != null && args.size() > 0)

		{
			return new OSCMessage(address, args);
		} else {
			return new OSCMessage(address);
		}
	}

}
