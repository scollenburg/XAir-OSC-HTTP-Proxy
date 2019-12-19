package org.chaseoaks.xair_proxy.data;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;

public class OSCMessageSerializer<T> extends StdSerializer<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3261431830810511472L;

	public OSCMessageSerializer(Class<T> t) {
		super(t);
	}

	@Override
	public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		// TODO Auto-generated method stub
		if (value instanceof OSCBundle) {
			serialize((OSCBundle) value, gen, serializers);
			return;
		}

		if (value instanceof OSCMessage)
			serialize((OSCMessage) value, gen, serializers);

	}

	public void serialize(OSCMessage message, JsonGenerator jgen, SerializerProvider serializers) throws IOException {
		jgen.writeStartObject();

		jgen.writeFieldName("address");
		jgen.writeObject(message.getAddress());

		List<Object> margs = message.getArguments();
		if (margs != null && margs.size() > 0) {
			jgen.writeFieldName("arguments");
//			jgen.writeStartArray();
//			for (Iterator<Object> iterator = margs.iterator(); iterator.hasNext();) {
//				Object object = iterator.next();
//				if (object instanceof Float) {
//					jgen.writeString(Float.toString((Float) object));
//				} else {
//					jgen.writeObject(object);
//				}
//			}
//			jgen.writeEndArray();
			jgen.writeObject(margs);
		}

	}

	public void serialize(OSCBundle bundle, JsonGenerator jgen, SerializerProvider serializers) throws IOException {

	}
}
