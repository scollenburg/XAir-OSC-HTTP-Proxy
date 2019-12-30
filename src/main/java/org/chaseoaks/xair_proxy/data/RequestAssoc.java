package org.chaseoaks.xair_proxy.data;

import java.io.Closeable;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.chaseoaks.xair_proxy.CloseWrappers;
import org.chaseoaks.xair_proxy.servlet.NanoReqResp;
import org.chaseoaks.xair_proxy.xair.OSCProxyPacketListener;

import com.illposed.osc.OSCPacket;
import com.illposed.osc.OSCPacketEvent;
import com.illposed.osc.transport.udp.OSCPortIn;
import com.illposed.osc.transport.udp.OSCPortOut;

public class RequestAssoc implements Closeable {
	public String extAlias;
	public NanoReqResp reqResp;
	public OSCPacket packet;
	public int inIPPort;
	public OSCPortIn portIn;
	public OSCPortOut portOut;
	public OSCProxyPacketListener listener;
	public ArrayBlockingQueue<IPMessage<OSCPacketEvent>> abQueue;
	public LocalDateTime sendTime;

	protected List<Object> unclosed;

	public RequestAssoc(NanoReqResp rr, OSCPacket op) {
		unclosed = new ArrayList<Object>(10);
		this.reqResp = rr;
		this.packet = op;
		addUnclosed(rr);
		addUnclosed(op);

		this.buildExtAlias();
	}

	protected void addUnclosed(Object o) {
		if (o == null)
			return;
		if (o instanceof Closeable) {
			unclosed.add(o);
			return;
		}
	}

	protected String buildExtAlias() {
		Integer hc;
		if (packet != null)
			hc = new Integer(packet.hashCode());
		else {
			hc = new Integer(this.hashCode());
		}
		this.extAlias = String.valueOf(hc.byteValue());
		return this.extAlias;
	}

	/**
	 * Close all the things. Hopefully, they implement {@link Closeable} ...
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void close() throws IOException {

		Object o;

		for (Iterator<Object> iterator = unclosed.iterator(); iterator.hasNext();) {
			o = iterator.next();
			try {
				if (o instanceof Closeable) {
					((Closeable) o).close();
					continue;
				}

				if (o instanceof OSCPortIn) {
					CloseWrappers.closePortIn((OSCPortIn) o);
					continue;
				}

				if (o instanceof ArrayBlockingQueue) {
					// CloseWrappers.closeABQueue((ArrayBlockingQueue<IPMessage<OSCPacketEvent>>) o);
					CloseWrappers.closeABQueue((ArrayBlockingQueue) o);
				}
			} catch (Exception E) {
				// LOGGING
			}

		}
	}

	protected void tryClose(OSCPortIn port) {
		port.stopListening();
	}

	public ArrayBlockingQueue<IPMessage<OSCPacketEvent>> set(ArrayBlockingQueue<IPMessage<OSCPacketEvent>> abq) {
		this.abQueue = abq;
		return abq;
	}

	public OSCPortIn set(OSCPortIn port) {
		this.portIn = port;
		return port;
	}

	public OSCProxyPacketListener set(OSCProxyPacketListener l) {
		this.listener = l;
		return l;
	}

	public OSCPortOut set(OSCPortOut port) {
		this.portOut = port;
		return port;
	}

	public LocalDateTime setSendTime() {
		this.setSendTime(LocalDateTime.now());
		return this.sendTime;
	}

	public LocalDateTime setSendTime(LocalDateTime now) {
		this.sendTime = now;
		return this.sendTime;
	}

}
