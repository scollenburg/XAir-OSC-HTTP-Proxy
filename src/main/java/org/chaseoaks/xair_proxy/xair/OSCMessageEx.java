package org.chaseoaks.xair_proxy.xair;

import java.net.InetSocketAddress;
import java.util.List;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCMessageInfo;

public class OSCMessageEx extends OSCMessage implements SentBy {

	private static final long serialVersionUID = -4436292112061953108L;
	protected InetSocketAddress sender;

	public OSCMessageEx(String address) {
		super(address);
		this.sender = null;
	}

	public OSCMessageEx(String address, InetSocketAddress sender) {
		super(address);
		this.sender = sender;
	}

	public OSCMessageEx(String address, List<?> arguments) {
		super(address, arguments);
		this.sender = null;
	}

	public OSCMessageEx(String address, List<?> arguments, InetSocketAddress sender) {
		super(address, arguments);
		this.sender = sender;
	}

	public OSCMessageEx(String address, List<?> arguments, OSCMessageInfo info) {
		super(address, arguments, info);
		this.sender = null;
	}

	public OSCMessageEx(String address, List<?> arguments, OSCMessageInfo info, InetSocketAddress sender) {
		super(address, arguments, info);
		this.sender = sender;
	}

	public InetSocketAddress getSender() {
		return this.sender;
	}

}
