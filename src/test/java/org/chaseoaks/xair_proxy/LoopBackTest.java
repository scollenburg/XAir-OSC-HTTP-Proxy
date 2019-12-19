package org.chaseoaks.xair_proxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacketEvent;
import com.illposed.osc.OSCSerializeException;
import com.illposed.osc.OSCSerializerFactory;
import com.illposed.osc.transport.udp.OSCPortIn;
import com.illposed.osc.transport.udp.OSCPortOut;

public class LoopBackTest {

	@Test
	public void loopBack() throws IOException, OSCSerializeException {
		OSCProxyPacketListener listener = new OSCProxyPacketListener();

		// OSCPortInBuilder inBuilder = new OSCPortInBuilder()
		// .setLocalSocketAddress(receiverInAddress)
		// .setRemoteSocketAddress(senderInAddress);

		OSCPortIn portIn = new OSCPortIn(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 10124));
		portIn.addPacketListener(listener);
		// portIn.connect();
		portIn.startListening();
		// assertEquals(portIn.isConnected(), true);

		// public OSCPortOut(final InetAddress remote, final int port) throws
		// IOException {

		OSCPortOut portOut = new OSCPortOut(InetAddress.getLoopbackAddress(), 10124);
		portOut.connect();

		OSCMessage message = new OSCMessage("/xinfo");
		portOut.send(message);
		sleep(100);
		OSCMessage lastMessage = listener.getLastMessage();

		assertNotNull(lastMessage, "lastMessage is NULL (xinfo)");
		String address = lastMessage.getAddress();
		assertEquals(address, "/xinfo");

		List<Object> origArguments = new ArrayList<>(3);
		origArguments.add(0.75f);

		listener.clearLast();

		lastMessage = listener.getLastMessage();
		assertNull(lastMessage, "lastMessage not cleared");

		message = new OSCMessage("/ch/01/mix/fader", origArguments);
		portOut.send(message);
		sleep(100);
		lastMessage = listener.getLastMessage();
		assertNotNull(lastMessage, "lastMessage is NULL (fader)");

		address = lastMessage.getAddress();
		assertEquals(address, "/ch/01/mix/fader");

		List<Object> recvArguments = lastMessage.getArguments();
		assertNotNull(recvArguments, "lastMessage is NULL (fader)");
		assertEquals(recvArguments.size(), 1);
		Class<? extends Object> argClass = recvArguments.get(0).getClass();
		assertEquals(argClass.getName(), "java.lang.Float");
		assertEquals((float) recvArguments.get(0), 0.75f, 0.0001f);

		portIn.stopListening();
		portOut.close();
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void loopBack2() throws IOException, OSCSerializeException {

		Map<String, Object> exMap = new TreeMap<String, Object>();

		exMap.put("port", 60001);
		OSCProxyPacketListener listener = new OSCProxyPacketListener();
		listener.setMap(exMap);

		// OSCPortInBuilder inBuilder = new OSCPortInBuilder()
		// .setLocalSocketAddress(receiverInAddress)
		// .setRemoteSocketAddress(senderInAddress);

		OSCPortIn portIn = new OSCPortIn(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 10124));
		portIn.addPacketListener(listener);
		// portIn.connect();
		portIn.startListening();
		// assertEquals(portIn.isConnected(), true);

		// public OSCPortOut(final InetAddress remote, final int port) throws
		// IOException {

		// OSCPortOut portOut = new OSCPortOut(InetAddress.getLoopbackAddress(), 10124);
		InetSocketAddress remote = new InetSocketAddress(InetAddress.getLoopbackAddress(), 10124);
		InetSocketAddress local = new InetSocketAddress(60001);
		OSCPortOut portOut = new OSCPortOut(OSCSerializerFactory.createDefaultFactory(), remote, local);

		portOut.connect();

		OSCMessage message = new OSCMessage("/xinfo");
		portOut.send(message);
		sleep(100);
		OSCMessage lastMessage = listener.getLastMessage();
		lastMessage.getInfo();

		assertNotNull(lastMessage, "lastMessage is NULL (xinfo)");

		String address = lastMessage.getAddress();
		assertEquals(address, "/xinfo");

		portIn.stopListening();
		portOut.close();

		OSCPacketEvent lastEvent = listener.getLastEvent();
		@SuppressWarnings("unused")
		Object eSource = lastEvent.getSource();
		int inport = 0;
		if (lastEvent instanceof Map) {
			inport = (int) ((Map) lastEvent).get("port");
		}
		assertEquals(inport, 60001);

	}

	private void sleep(int i) {
		try {
			TimeUnit.MILLISECONDS.sleep(i);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}
}
