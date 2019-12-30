package org.chaseoaks.xair_proxy.xair;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.List;

import com.illposed.osc.OSCBadDataEvent;
import com.illposed.osc.OSCPacket;
import com.illposed.osc.OSCPacketListener;
import com.illposed.osc.OSCParseException;
import com.illposed.osc.OSCParserFactory;
import com.illposed.osc.transport.channel.OSCDatagramChannel;
import com.illposed.osc.transport.udp.OSCPort;
import com.illposed.osc.transport.udp.OSCPortIn;

public class OSCPortInEx extends OSCPortIn implements Closeable {

	protected Thread listeningThread;
	protected volatile boolean listening;
	protected boolean resilient;
	protected boolean daemonListener;
	protected OSCParserFactory parserFactory;
	protected List<OSCPacketListener> packetListeners;

	public OSCPortInEx() throws IOException {
		super();
		// TODO Auto-generated constructor stub
	}

	public OSCPortInEx(int port) throws IOException {
		super(port);
		// TODO Auto-generated constructor stub
	}

	public OSCPortInEx(OSCParserFactory parserFactory, int port) throws IOException {
		super(parserFactory, port);
		// TODO Auto-generated constructor stub
	}

	public OSCPortInEx(OSCParserFactory parserFactory, List<OSCPacketListener> packetListeners, SocketAddress local,
			SocketAddress remote) throws IOException {
		super(parserFactory, packetListeners, local, remote);

		this.listening = false;
		this.daemonListener = true;
		this.resilient = true;
		this.parserFactory = parserFactory;
		this.packetListeners = packetListeners;

		// TODO Auto-generated constructor stub
	}

	public OSCPortInEx(OSCParserFactory parserFactory, List<OSCPacketListener> packetListeners, SocketAddress local)
			throws IOException {
		this(parserFactory, packetListeners, local, new InetSocketAddress(OSCPort.generateWildcard(local), 0));
	}

	public OSCPortInEx(OSCParserFactory parserFactory, SocketAddress local) throws IOException {
		this(parserFactory, defaultPacketListeners(), local);
	}

	public OSCPortInEx(SocketAddress local) throws IOException {
		this(OSCParserFactory.createDefaultFactory(), local);
	}

	@Override
	public void run() {
		listeningThread = Thread.currentThread();

		final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		final DatagramChannel channel = getChannel();
		final OSCDatagramChannel oscChannel = new OSCDatagramChannelEx(channel, parserFactory, null);
		while (listening) {
			try {
				final OSCPacket oscPacket = oscChannel.read(buffer);

				OSCPacketEventEx event;
				if (oscPacket instanceof SentBy)
					event = new OSCPacketEventEx(this, oscPacket, ((SentBy) oscPacket).getSender(), true);
				else
					event = new OSCPacketEventEx(this, oscPacket, null, true);

				for (final OSCPacketListener listener : packetListeners) {
					listener.handlePacket(event);
				}
			} catch (final IOException ex) {
				// TODO LOGGING
				// if (isListening()) {
				// stopListening(ex);
				// } else {
				stopListening();
				// }
			} catch (final OSCParseException ex) {
				badPacketReceived(ex, buffer);
			}
		}
		try {
			oscChannel.close();
		} catch (IOException e) {
		}
	}

	public void startListening() {
		super.startListening();
		listening = true;
	}

	public void stopListening() {
		super.stopListening();
		listening = false;
	}

	protected void badPacketReceived(final OSCParseException exception, final ByteBuffer data) {

		final OSCBadDataEvent badDataEvt = new OSCBadDataEvent(this, data, exception);

		for (final OSCPacketListener listener : packetListeners) {
			listener.handleBadData(badDataEvt);
		}
	}

	/**
	 * Closeable interface version of close...
	 */
	public void close() throws IOException {
		stopListening();
		super.close();
	}
}
