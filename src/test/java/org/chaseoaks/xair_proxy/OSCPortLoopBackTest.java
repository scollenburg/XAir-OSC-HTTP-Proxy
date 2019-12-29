package org.chaseoaks.xair_proxy;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.chaseoaks.xair_proxy.data.IPMessage;
import org.chaseoaks.xair_proxy.data.OSCPortMap;
import org.chaseoaks.xair_proxy.data.RequestAssoc;
import org.chaseoaks.xair_proxy.xair.OSCPortInEx;
import org.chaseoaks.xair_proxy.xair.OSCProxyPacketListener;
import org.chaseoaks.xair_proxy.xair.SentBy;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCSerializeException;
import com.illposed.osc.OSCSerializerFactory;
import com.illposed.osc.transport.udp.OSCPortIn;
import com.illposed.osc.transport.udp.OSCPortOut;

public class OSCPortLoopBackTest {

	@BeforeClass
	public void setThreadDefault() {
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				System.out.println("catching exception " + e.getMessage());
				e.printStackTrace();
			}
		});

	}

	@Test
	public void loopBack() throws IOException, OSCSerializeException, InterruptedException {
		RequestAssoc ra = new RequestAssoc(null, null);
		ArrayBlockingQueue<IPMessage> queue = ra.set(new ArrayBlockingQueue<>(10, false));

		OSCProxyPacketListener listener = new OSCProxyPacketListener(ra);

		// OSCPortInBuilder inBuilder = new OSCPortInBuilder()
		// .setLocalSocketAddress(receiverInAddress)
		// .setRemoteSocketAddress(senderInAddress);

		OSCPortIn portIn = new OSCPortInEx(new InetSocketAddress(InetAddress.getByName("0.0.0.0"), 10124));
		portIn.setDaemonListener(false);
		portIn.addPacketListener(listener);
		// portIn.connect();
		portIn.startListening();
		// assertEquals(portIn.isConnected(), true);

		// public OSCPortOut(final InetAddress remote, final int port) throws
		// IOException {

		// OSCPortOut portOut = new OSCPortOut(InetAddress.getLoopbackAddress(), 10124);
		OSCPortMap portMap = new OSCPortMap();
		int outIPPort = portMap.getNextPort();
		System.out.format("Using sending port %d\n", outIPPort);
		OSCPortOut portOut = new OSCPortOut(OSCSerializerFactory.createDefaultFactory(),
				new InetSocketAddress("127.0.0.1", 10124), new InetSocketAddress(outIPPort));
		portOut.connect();

		OSCMessage message = new OSCMessage("/xinfo");
		portOut.send(message);
		safeSleep(10);
		IPMessage ipmessage;
		OSCMessage lastMessage = null; // = listener.getLastMessage();
		ipmessage = queue.poll(5000, TimeUnit.MILLISECONDS);
		if (ipmessage.data instanceof OSCMessage)
			lastMessage = (OSCMessage) ipmessage.data;

		assertNotNull(lastMessage, "lastMessage is NULL (xinfo)");
		String address = lastMessage.getAddress();
		assertEquals(address, "/xinfo");

		assertTrue(lastMessage instanceof SentBy, lastMessage.toString() + " does not support SentBy");
		if (lastMessage instanceof SentBy) {
			assertEquals(((SentBy) lastMessage).getSender().getPort(), outIPPort, "Unexpected sender port");
		}

		List<Object> origArguments = new ArrayList<>(3);
		origArguments.add(0.75f);

		listener.clearLast();

		lastMessage = listener.getLastMessage();
		assertNull(lastMessage, "lastMessage not cleared");

		message = new OSCMessage("/ch/01/mix/fader", origArguments);
		portOut.send(message);
		safeSleep(10);
		// lastMessage = listener.getLastMessage();
		ipmessage = queue.poll(5000, TimeUnit.MILLISECONDS);
		if (ipmessage.data instanceof OSCMessage)
			lastMessage = (OSCMessage) ipmessage.data;

		assertNotNull(lastMessage, "lastMessage is NULL (fader)");

		address = lastMessage.getAddress();
		assertEquals(address, "/ch/01/mix/fader");

		List<Object> recvArguments = lastMessage.getArguments();
		assertNotNull(recvArguments, "lastMessage is NULL (fader)");
		assertEquals(recvArguments.size(), 1);
		Class<? extends Object> argClass = recvArguments.get(0).getClass();
		assertEquals(argClass.getName(), "java.lang.Float");
		assertEquals((float) recvArguments.get(0), 0.75f, 0.0001f);

		ra.close();
		portIn.stopListening();
		portIn.close();
		portOut.close();
	}

	@Test
	public void OSCPacketEventMapTest() throws IOException, OSCSerializeException, InterruptedException {

		RequestAssoc ra = new RequestAssoc(null, null);
		ArrayBlockingQueue<IPMessage> queue = ra.set(new ArrayBlockingQueue<>(10, false));

		OSCProxyPacketListener listener = new OSCProxyPacketListener(ra);

		Map<String, Object> exMap = new TreeMap<String, Object>();

		exMap.put("outIPport", 60001);
		// OSCProxyPacketListener listener = new OSCProxyPacketListener();
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
		safeSleep(10);
		// OSCMessage lastMessage = listener.getLastMessage();
		IPMessage ipmessage;
		OSCMessage lastMessage = null; // = listener.getLastMessage();
		ipmessage = queue.poll(5000, TimeUnit.MILLISECONDS);
		if (ipmessage.data instanceof OSCMessage)
			lastMessage = (OSCMessage) ipmessage.data;

		lastMessage.getInfo();

		assertNotNull(lastMessage, "lastMessage is NULL (xinfo)");

		String address = lastMessage.getAddress();
		assertEquals(address, "/xinfo");

		portIn.stopListening();
		portOut.close();

		// OSCPacketEvent lastEvent = listener.getLastEvent();

		// @SuppressWarnings("unused")
		// Object eSource = lastEvent.getSource();
		// int inport = 0;
		// if (lastEvent instanceof Map) {
		// inport = (int) ((Map) lastEvent).get("outIPport");
		// }
		// assertEquals(inport, 60001);

	}

	private void safeSleep(int i) {
		try {
			TimeUnit.MILLISECONDS.sleep(i);
		} catch (InterruptedException e) {
			// e.printStackTrace();
		}
	}
}
