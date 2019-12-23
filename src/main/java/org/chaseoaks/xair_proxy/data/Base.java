package org.chaseoaks.xair_proxy.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.illposed.osc.OSCMessage;

public abstract class Base {

	private static final String EMPTY_STRING = "";

	public String toJson() {
		ObjectMapper mapper = Base.getMapper();
		String jsonString = null;
		try {
			jsonString = mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonString;
	}

	/**
	 * Convenience method to return a ObjectMapper with the custom serializer
	 * modules registered.
	 * 
	 * @return
	 */
	public static ObjectMapper getMapper() {
		ObjectMapper mapper = new ObjectMapper();

		SimpleModule module = new SimpleModule();
		module.addSerializer(ByteBundle.class, new ByteArraySerializer<ByteBundle>());
		// module.addSerializer(LevelBuffer.class, new LevelBufferSerializer());
		module.addSerializer(OSCMessage.class, new OSCMessageSerializer<OSCMessage>(OSCMessage.class));
		module.addDeserializer(OSCMessage.class, new OSCMessageDeserializr<OSCMessage>(OSCMessage.class));
		mapper.registerModule(module);
		return mapper;
	}

	public static String safeString(String in) {
		if (in == null)
			return EMPTY_STRING;

		return in;
	}

}
