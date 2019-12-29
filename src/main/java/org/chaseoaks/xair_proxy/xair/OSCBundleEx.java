package org.chaseoaks.xair_proxy.xair;

import java.net.InetSocketAddress;
import java.util.List;

import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCPacket;
import com.illposed.osc.argument.OSCTimeTag64;

public class OSCBundleEx extends OSCBundle implements SentBy {

	private static final long serialVersionUID = -4134518466465264565L;
	protected InetSocketAddress sender;

	public OSCBundleEx() {
		super();
		this.sender = null;
	}

	public OSCBundleEx(InetSocketAddress sender) {
		this.sender = sender;
	}

	public OSCBundleEx(OSCTimeTag64 timestamp) {
		super(timestamp);
		this.sender = null;
	}

	public OSCBundleEx(OSCTimeTag64 timestamp, InetSocketAddress sender) {
		super(timestamp);
		this.sender = sender;
	}

	public OSCBundleEx(List<OSCPacket> packets) {
		super(packets);
		this.sender = null;
	}

	public OSCBundleEx(List<OSCPacket> packets, InetSocketAddress sender) {
		super(packets);
		this.sender = sender;
	}

	public OSCBundleEx(List<OSCPacket> packets, OSCTimeTag64 timestamp) {
		super(packets, timestamp);
		this.sender = null;
	}

	public OSCBundleEx(List<OSCPacket> packets, OSCTimeTag64 timestamp, InetSocketAddress sender) {
		super(packets, timestamp);
		this.sender = sender;
	}

	public InetSocketAddress getSender() {
		return this.sender;
	}

}
