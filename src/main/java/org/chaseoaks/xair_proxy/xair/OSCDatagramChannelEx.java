package org.chaseoaks.xair_proxy.xair;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import com.illposed.osc.OSCParseException;
import com.illposed.osc.OSCParser;
import com.illposed.osc.OSCParserFactory;
import com.illposed.osc.OSCSerializerFactory;
import com.illposed.osc.transport.channel.OSCDatagramChannel;

public class OSCDatagramChannelEx extends OSCDatagramChannel {

	protected final DatagramChannel underlyingChannel;
	protected final OSCParser parser;

	public OSCDatagramChannelEx(DatagramChannel underlyingChannel, OSCParserFactory parserFactory,
			OSCSerializerFactory serializerFactory) {
		super(underlyingChannel, parserFactory, serializerFactory);
		this.underlyingChannel = underlyingChannel;
		OSCParser tmpParser = null;
		if (parserFactory != null) {
			tmpParser = parserFactory.create();
		}
		this.parser = tmpParser;

	}

	@Override
	public OSCPacket read(final ByteBuffer buffer) throws IOException, OSCParseException {

		boolean completed = false;
		OSCPacket oscPacket;
		SocketAddress sender = null;
		InetSocketAddress senderip = null;
		try {
			begin();
			buffer.clear();
			// NOTE From the doc of `read()` and `receive()`:
			// "If there are fewer bytes remaining in the buffer
			// than are required to hold the datagram
			// then the remainder of the datagram is silently discarded."
			if (underlyingChannel.isConnected()) {
				sender = underlyingChannel.getRemoteAddress();
				underlyingChannel.read(buffer);
			} else {
				sender = underlyingChannel.receive(buffer);
			}
			// final int readBytes = buffer.position();
			// if (readBytes == buffer.capacity()) {
			// // TODO In this case it is very likely that the buffer was actually too
			// small, and the remainder of the datagram/packet was silently discarded. We
			// might want to give a warning, like throw an exception in this case, but
			// whether this happens should probably be user configurable.
			// }
			buffer.flip();
			if (buffer.limit() == 0) {
				throw new OSCParseException("Received a packet without any data");
			} else {
				oscPacket = parser.convert(buffer);
				completed = true;
			}
		} finally {
			end(completed);
		}

		if (sender instanceof InetSocketAddress)
			senderip = (InetSocketAddress) sender;

		if (senderip == null)
			return oscPacket;

		if (oscPacket instanceof OSCMessage) {
			return new OSCMessageEx(((OSCMessage) oscPacket).getAddress(), ((OSCMessage) oscPacket).getArguments(),
					((OSCMessage) oscPacket).getInfo(), senderip);
		}

		if (oscPacket instanceof OSCBundle) {
			// return new OSCMessageEx(((OSCMessage) oscPacket).getAddress(), ((OSCMessage)
			// oscPacket).getArguments(),
			// ((OSCMessage) oscPacket).getInfo(), senderip);
			return new OSCBundleEx(((OSCBundle) oscPacket).getPackets(), ((OSCBundle) oscPacket).getTimestamp(),
					senderip);
		}

		return oscPacket;
	}
}
