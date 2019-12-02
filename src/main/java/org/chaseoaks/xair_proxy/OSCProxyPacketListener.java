package org.chaseoaks.xair_proxy;

import java.util.Map;

import com.illposed.osc.OSCBadDataEvent;
import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCMessageEvent;
import com.illposed.osc.OSCPacket;
import com.illposed.osc.OSCPacketEvent;
import com.illposed.osc.OSCPacketListener;
import com.illposed.osc.argument.OSCTimeTag64;

public class OSCProxyPacketListener implements OSCPacketListener {

	protected OSCPacketEventEx lastEvent;
	protected Map<String, Object> exMap;

	@Override
	public void handlePacket(OSCPacketEvent originalEvent) {

		lastEvent = new OSCPacketEventEx(originalEvent);
		if (exMap != null)
			lastEvent.putAll(exMap);

		OSCPacket packet = lastEvent.getPacket();

		if (packet instanceof OSCBundle) {
			handleBundle(lastEvent.getSource(), (OSCBundle) packet);
		} else {
			OSCTimeTag64 timeStamp = OSCTimeTag64.IMMEDIATE;
			handleMessage(new OSCMessageEvent(lastEvent.getSource(), timeStamp, (OSCMessage) packet));
		}
	}

	protected void handleMessage(OSCMessageEvent oscMessageEvent) {
		OSCMessage message = oscMessageEvent.getMessage();
	}

	protected void handleBundle(Object source, OSCBundle packet) {

	}

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

	public OSCPacketEvent getLastEvent() {
		return lastEvent;
	}

}
