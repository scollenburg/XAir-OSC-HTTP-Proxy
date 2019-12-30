package org.chaseoaks.xair_proxy;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.chaseoaks.xair_proxy.data.IPMessage;
import org.chaseoaks.xair_proxy.data.OSCPortMap;
import org.chaseoaks.xair_proxy.data.RequestAssoc;
import org.chaseoaks.xair_proxy.xair.OSCEchoPacketListener;
import org.chaseoaks.xair_proxy.xair.OSCPortInEx;
import org.chaseoaks.xair_proxy.xair.OSCProxyPacketListener;
import org.chaseoaks.xair_proxy.xair.SentBy;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacketEvent;
import com.illposed.osc.OSCSerializeException;
import com.illposed.osc.OSCSerializerFactory;
import com.illposed.osc.transport.udp.OSCPortIn;
import com.illposed.osc.transport.udp.OSCPortOut;

public class OSCEchoListenerTest {

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
	public void directOSCPort() throws IOException, OSCSerializeException, InterruptedException {

		/**
		 * Make this longer when debugging
		 */
		int waitLen = 120000;

		RequestAssoc ra = new RequestAssoc(null, null);
		ArrayBlockingQueue<IPMessage<OSCPacketEvent>> queue = ra.set(new ArrayBlockingQueue<>(10, false));

		OSCPortMap portMap = new OSCPortMap();
		int outIPPort = portMap.getNextPort();
		int echoIPPort = 20024;
		System.out.format("Using sending port %d\n", outIPPort);

		OSCEchoPacketListener echoListener = new OSCEchoPacketListener();

		// OSCPortInBuilder inBuilder = new OSCPortInBuilder()
		// .setLocalSocketAddress(receiverInAddress)
		// .setRemoteSocketAddress(senderInAddress);

		OSCPortIn portInEcho = new OSCPortInEx(new InetSocketAddress(InetAddress.getByName("0.0.0.0"), echoIPPort));
		portInEcho.setDaemonListener(false);
		portInEcho.addPacketListener(echoListener);
		// portIn.connect();
		portInEcho.startListening();
		// assertEquals(portIn.isConnected(), true);

		// public OSCPortOut(final InetAddress remote, final int port) throws
		// IOException {

		OSCProxyPacketListener listener = new OSCProxyPacketListener(ra);
		OSCPortIn portIn = new OSCPortInEx(new InetSocketAddress(InetAddress.getByName("0.0.0.0"), outIPPort));
		portIn.setDaemonListener(false);
		portIn.addPacketListener(listener);
		portIn.startListening();

		// OSCPortOut portOut = new OSCPortOut(InetAddress.getLoopbackAddress(), 10124);
		OSCPortOut portOut = new OSCPortOut(OSCSerializerFactory.createDefaultFactory(),
				new InetSocketAddress("127.0.0.1", echoIPPort), new InetSocketAddress(outIPPort));
		portOut.connect();

		OSCMessage message = new OSCMessage("/xinfo");
		portOut.send(message);
		safeSleep(10);
		IPMessage<OSCPacketEvent> ipmessage;
		OSCMessage lastMessage = null; // = listener.getLastMessage();
		ipmessage = queue.poll(waitLen, TimeUnit.MILLISECONDS);
		if (ipmessage.data instanceof OSCPacketEvent)
			lastMessage = (OSCMessage) ipmessage.data.getPacket();

		assertNotNull(lastMessage, "lastMessage is NULL (xinfo)");
		String address = lastMessage.getAddress();
		assertEquals(address, "/xinfo");

		assertTrue(lastMessage instanceof SentBy, lastMessage.toString() + " does not support SentBy");
		if (lastMessage instanceof SentBy) {
			assertNotEquals(((SentBy) lastMessage).getSender().getPort(), outIPPort, "Unexpected sender port");
		}

		List<Object> origArguments = new ArrayList<>(3);
		origArguments.add(0.75f);

		listener.clearLast();

		lastMessage = listener.getLastMessage();
		assertNull(lastMessage, "lastMessage not cleared");

		message = new OSCMessage("/ch/01/mix/fader", origArguments);
		portOut.send(message);
		safeSleep(10);
		ipmessage = queue.poll(waitLen, TimeUnit.MILLISECONDS);
		if (ipmessage.data instanceof OSCPacketEvent)
			lastMessage = (OSCMessage) ipmessage.data.getPacket();

		assertNotNull(lastMessage, "lastMessage is NULL (fader)");

		address = lastMessage.getAddress();
		assertEquals(address, "/ch/01/mix/fader");

		List<Object> recvArguments = lastMessage.getArguments();
		assertNotNull(recvArguments, "lastMessage is NULL (fader)");
		assertEquals(recvArguments.size(), 1);
		Class<? extends Object> argClass = recvArguments.get(0).getClass();
		assertEquals(argClass.getName(), "java.lang.Float");
		assertEquals((float) recvArguments.get(0), 0.75f, 0.0001f);

		OSCBundle bundle = new OSCBundle();
		bundle.addPacket(new OSCMessage("/foo"));
		bundle.addPacket(new OSCMessage("/bar"));
		bundle.addPacket(new OSCMessage("/baz"));
		bundle.addPacket(new OSCMessage("/biffbiffbiff"));

		portOut.send(bundle);
		safeSleep(10);
		ipmessage = queue.poll(waitLen, TimeUnit.MILLISECONDS);
		OSCBundle lastBundle = null;
		assertNotNull(ipmessage, "ipmessage is NULL ([foo,bar,baz,biffbiffbiff])");
		assertNotNull(ipmessage.data, "ipmessage.data is NULL ([foo,bar,baz,biffbiffbiff])");
		if (ipmessage.data instanceof OSCPacketEvent)
			lastBundle = (OSCBundle) ipmessage.data.getPacket();
		assertNotNull(lastBundle, "lastBundle is NULL ([foo,bar,baz,biffbiffbiff])");

		assertNotNull(lastBundle.getPackets(), "getPackets() is NULL");
		assertEquals(lastBundle.getPackets().size(), 4, "getPackets() bad List size");

		ra.close();
		portIn.stopListening();
		portIn.close();
		portOut.close();

		portInEcho.stopListening();
		portInEcho.close();
	}

	private void safeSleep(int i) {
		try {
			TimeUnit.MILLISECONDS.sleep(i);
		} catch (InterruptedException e) {
			// e.printStackTrace();
		}
	}
}
