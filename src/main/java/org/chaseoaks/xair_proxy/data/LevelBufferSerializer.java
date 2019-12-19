package org.chaseoaks.xair_proxy.data;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Custom JSON serializer for {@link LevelBuffer}. Meter level results are returned most recent first.
 * <p>
 * <strong>TODO:</strong> Insead of putting a custom serializer over the entire LevelBuffer, create a MRUBuffer class et al.
 * @author scollenburg
 *
 * @param <T>
 */
public class LevelBufferSerializer<T> extends JsonSerializer<T> {

	@Override
	public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		serializeMostRecent((LevelBuffer) value, gen, serializers);
	}

	public void serializeMostRecent(LevelBuffer levelBuffer, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException {
		jgen.writeStartObject();

		// jgen.writeStringField("command", levelBuffer.command);
		jgen.writeFieldName("command");
		jgen.writeObject(levelBuffer.command);

		jgen.writeFieldName("buffer");
		// jgen.writeStartArray();
		// ByteBundle[] buffer = levelBuffer.getRecent();
		// jgen.writeObject(buffer);
		// jgen.writeEndArray();

		jgen.writeEndObject();
	}

}
