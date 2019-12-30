package org.chaseoaks.xair_proxy.xair;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.illposed.osc.OSCBadDataEvent;
import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import com.illposed.osc.OSCPacketEvent;
import com.illposed.osc.OSCPacketListener;
import com.illposed.osc.OSCSerializeException;
import com.illposed.osc.transport.udp.OSCPortOut;

public class OSCEchoPacketListener implements OSCPacketListener {

	// protected RequestAssoc ra;
	// // protected OSCPacketEventEx lastEvent;
	// protected Map<String, Object> exMap;

	// public OSCEchoPacketListener(RequestAssoc rassoc) {
	// this.ra = rassoc;
	// }

	public OSCEchoPacketListener() {
	}

	@Override
	public void handlePacket(OSCPacketEvent originalEvent) {

		OSCPacketEventEx event;

		event = new OSCPacketEventEx(originalEvent);
		// if (exMap != null)
		// event.putAll(exMap);

		OSCPacket packet = event.getPacket();

		// if (packet instanceof OSCBundle) {
		// handleBundle(lastEvent.getSource(), (OSCBundle) packet);
		// } else {
		// OSCTimeTag64 timeStamp = OSCTimeTag64.IMMEDIATE;
		// handleMessage(new OSCMessageEvent(lastEvent.getSource(), timeStamp,
		// (OSCMessage) packet));
		// }
		if (packet instanceof OSCMessage) {
			System.out.printf("  EchoListener got messaage: %s\n", ((OSCMessage) packet).getAddress());
		}

		if (packet instanceof OSCBundle) {
			List<OSCPacket> pkts = ((OSCBundle) packet).getPackets();
			System.out.printf("  EchoListener got bundle length %d: [", pkts.size());
			String komma = "";
			for (Iterator<OSCPacket> iterator = pkts.iterator(); iterator.hasNext();) {
				OSCPacket oscPacket = (OSCPacket) iterator.next();
				if (oscPacket instanceof OSCMessage) {
					System.out.print(komma + ((OSCMessage) oscPacket).getAddress());
					komma = ", ";
				}
			}
			System.out.print("]\n");
		}

		if (originalEvent instanceof SentBy) {
			try {
				OSCPortOut portOut = new OSCPortOut(((SentBy) originalEvent).getSender());
				portOut.connect();
				portOut.send(packet);
				portOut.disconnect();
				portOut.close();
			} catch (IOException | OSCSerializeException e) {
				// TODO LOGGING Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void handleBadData(OSCBadDataEvent event) {
		// TODO LOGGING Auto-generated method stub
	}

	// public Map<String, Object> setMap(Map<String, Object> map) {
	// this.exMap = map;
	// return map;
	// }
	//
	// public Map<String, Object> getMap() {
	// return exMap;
	// }

	// public void clearLast() {
	// lastEvent = null;
	// }
	//
	// public OSCMessage getLastMessage() {
	// if (lastEvent == null)
	// return null;
	//
	// return (OSCMessage) lastEvent.getPacket();
	// }
	//
	// public OSCPacketEvent getLastEvent() {
	// return lastEvent;
	// }

}
