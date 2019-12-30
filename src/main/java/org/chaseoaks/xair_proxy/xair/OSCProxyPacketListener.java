package org.chaseoaks.xair_proxy.xair;

import java.util.Map;

import org.chaseoaks.xair_proxy.data.IPMessage;
import org.chaseoaks.xair_proxy.data.RequestAssoc;

import com.illposed.osc.OSCBadDataEvent;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacketEvent;
import com.illposed.osc.OSCPacketListener;

public class OSCProxyPacketListener implements OSCPacketListener {

	protected RequestAssoc ra;
	protected OSCPacketEventEx lastEvent;
	protected Map<String, Object> exMap;

	public OSCProxyPacketListener(RequestAssoc rassoc) {
		this.ra = rassoc;
	}

	public OSCProxyPacketListener() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handlePacket(OSCPacketEvent originalEvent) {

		lastEvent = new OSCPacketEventEx(originalEvent);
		if (exMap != null)
			lastEvent.putAll(exMap);

		// OSCPacket packet = lastEvent.getPacket();

		// if (packet instanceof OSCBundle) {
		// handleBundle(lastEvent.getSource(), (OSCBundle) packet);
		// } else {
		// OSCTimeTag64 timeStamp = OSCTimeTag64.IMMEDIATE;
		// handleMessage(new OSCMessageEvent(lastEvent.getSource(), timeStamp,
		// (OSCMessage) packet));
		// }
		if (ra != null && ra.abQueue != null) {
			IPMessage<OSCPacketEvent> event = new IPMessage<OSCPacketEvent>(null, originalEvent, 0);
			ra.abQueue.offer(event);
		}

	}

	// protected void handleMessage(OSCMessageEvent oscMessageEvent) {
	// IPMessage<OSCMessageEvent> event = new IPMessage(null, oscMessageEvent, 0);
	// if (ra != null && ra.abQueue != null)
	// ra.abQueue.offer(event);
	// }
	//
	// protected void handleBundle(Object source, OSCBundle packet) {
	//
	// }

	@Override
	public void handleBadData(OSCBadDataEvent event) {
		// TODO Auto-generated method stub

	}

	public Map<String, Object> setMap(Map<String, Object> map) {
		this.exMap = map;
		return map;
	}

	public Map<String, Object> getMap() {
		return exMap;
	}

	public void clearLast() {
		lastEvent = null;
	}

	public OSCMessage getLastMessage() {
		if (lastEvent == null)
			return null;

		return (OSCMessage) lastEvent.getPacket();
	}

	@Deprecated
	public OSCPacketEvent getLastEvent() {
		return lastEvent;
	}

}
