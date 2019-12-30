package org.chaseoaks.xair_proxy.xair;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.illposed.osc.OSCPacket;
import com.illposed.osc.OSCPacketEvent;

public class OSCPacketEventEx extends OSCPacketEvent implements Map<String, Object>, SentBy {

	private static final long serialVersionUID = 193192584722955968L;
	// protected OSCPacketEvent wrappedEvent;
	protected final boolean inbound;
	protected final InetSocketAddress sender;
	Map<String, Object> map;

	public OSCPacketEventEx(final Object source, final OSCPacket packet) {
		this(source, packet, null, false);
		// super(source, packet);
		// this.map = new TreeMap<String, Object>();
		// this.map.put("source", source);
		// this.map.put("", packet);
	}

	public OSCPacketEventEx(final Object source, final OSCPacket packet, final InetSocketAddress sender) {
		this(source, packet, sender, true);
	}

	public OSCPacketEventEx(OSCPacketEvent originalEvent) {
		this(originalEvent.getSource(), originalEvent.getPacket(), null, false);
	}

	public OSCPacketEventEx(OSCPacketEvent originalEvent, final InetSocketAddress sender) {
		this(originalEvent.getSource(), originalEvent.getPacket(), sender, true);
	}

	public OSCPacketEventEx(OSCPacketEvent originalEvent, final InetSocketAddress sender, final boolean inbound) {
		this(originalEvent.getSource(), originalEvent.getPacket(), sender, inbound);
	}

	public OSCPacketEventEx(final Object source, final OSCPacket packet, final InetSocketAddress sender,
			final boolean inbound) {
		super(source, packet);
		this.sender = sender;
		this.inbound = inbound;
		this.map = new TreeMap<String, Object>();
		this.map.put("source", source);
		this.map.put("packet", packet);
		if (sender != null)
			this.map.put("sender", sender);
	}

	public final boolean isInbound() {
		return this.inbound;
	}

	public final InetSocketAddress getSender() {
		return this.sender;
	}

	@Override
	public void clear() {
		this.map.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		if (this.map == null)
			return false;
		return this.map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		if (this.map == null)
			return false;
		return this.map.containsValue(value);
	}

	@Override
	public Object get(Object key) {
		if (this.map == null)
			return null;
		return this.map.get(key);
	}

	@Override
	public boolean isEmpty() {
		if (this.map == null)
			return false;
		return this.map.isEmpty();
	}

	@Override
	public Object put(String key, Object value) {
		return this.map.put((String) key, value);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void putAll(Map m) {
		this.map.putAll(m);
	}

	@Override
	public Object remove(Object key) {
		if (this.map == null)
			return null;
		return this.map.remove(key);
	}

	@Override
	public int size() {
		if (this.map == null)
			return 0;
		return this.map.size();
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		if (this.map == null)
			return null;
		return this.map.entrySet();
	}

	@Override
	public Set<String> keySet() {
		if (this.map == null)
			return null;
		return this.map.keySet();
	}

	@Override
	public Collection<Object> values() {
		if (this.map == null)
			return null;
		return this.map.values();
	}

}
