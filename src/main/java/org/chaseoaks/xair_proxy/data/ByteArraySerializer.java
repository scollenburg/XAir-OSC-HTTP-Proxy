package org.chaseoaks.xair_proxy.data;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ByteArraySerializer<T> extends JsonSerializer<T> {

	@Override
	public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		serializeToNumericBytes((ByteBundle) value, gen, serializers);
	}

	public void serializeToNumericBytes(ByteBundle bundle, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException {
		jgen.writeStartArray();

		if (bundle.bundle != null && bundle.bundle.length > 0) {
			for (byte b : bundle.bundle) {
				jgen.writeNumber(unsignedToBytes(b));
			}
		}

		jgen.writeEndArray();
	}

	private static int unsignedToBytes(byte b) {
		return b & 0xFF;
	}
}
