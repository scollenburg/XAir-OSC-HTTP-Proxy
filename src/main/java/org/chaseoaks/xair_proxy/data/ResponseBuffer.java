package org.chaseoaks.xair_proxy.data;

import java.time.Instant;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.illposed.osc.OSCMessage;

/**
 * Buffer to store meter level results from the XAir. A custom Jackson JSON
 * serializer ( {@link LevelBufferSerializer} ) is used to return the results,
 * most recent first.
 * <p>
 * The internal buffer has a fixed size, and will loop discarding the oldest
 * results if results are not polled often enough.
 * <p>
 * {@link ByteBundle} is used as a tag class for the JSON serializer (Jackson
 * ObjectMapper) to force blob results to come out as a array instead of Base64,
 * which should greatly simpify the front end UI. See
 * {@link ByteArraySerializer} and
 * {@link org.chaseoaks.xair_proxy.data.Base#getMapper}.
 * 
 * @author scollenburg
 *
 */
public class ResponseBuffer extends Base {

	protected OSCMessage message = null;
	public String command = null;
	public long timeStampMillis = 0;

	public ResponseBuffer(OSCMessage message) {
		this.message = message;
		this.command = message.toString();
		Instant instant = Instant.now();
		timeStampMillis = instant.toEpochMilli();
	}

}
