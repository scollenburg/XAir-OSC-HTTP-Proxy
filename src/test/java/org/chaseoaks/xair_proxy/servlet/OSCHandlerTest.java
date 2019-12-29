package org.chaseoaks.xair_proxy.servlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import org.chaseoaks.xair_proxy.data.MixerInfo;
import org.chaseoaks.xair_proxy.data.RequestAssoc;
import org.testng.annotations.Test;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import com.illposed.osc.transport.udp.OSCPortOut;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class OSCHandlerTest {

	class OSCHandlerMock extends OSCHandler {
		public OSCPacket lastMessage = null;
		public OSCPortOut lastPortOut = null;

		protected OSCPacket marshallOSC(NanoReqResp reqResp) {
			lastMessage = super.marshallOSC(reqResp);
			return lastMessage;
		}

		protected OSCPortOut buildOSCPortOut(MixerInfo mixerInfo) throws UnknownHostException, IOException {

			RequestAssoc ra = new RequestAssoc(null, null);
			lastPortOut = super.buildOSCPortOut(ra, mixerInfo);
			return lastPortOut;
		}
	}

	class NanoReqRespMock extends NanoReqResp {

		public String subPath = null;

		public NanoReqRespMock(OSCProxyServer server, IHTTPSession session, String contextPath) {
			super(server, session, contextPath);
			// TODO Auto-generated constructor stub
		}

		public NanoReqRespMock setSubPath(String subPath) {
			this.subPath = subPath;
			return this;
		}

		public String getSubPath() {
			return this.subPath;
		}

		public Status getStatus() {
			return this.responseStatus;
		}
	}

	@Test(timeOut = 1000)
	public void basicPathMarshalling() {
		NanoReqRespMock rr = new NanoReqRespMock(null, null, "");

		OSCHandlerMock h = new OSCHandlerMock();

		OSCMessage message;
		List<Object> args;

		rr.setSubPath("/loopback/xinfo");
		h.handle(rr);
		message = (OSCMessage) h.lastMessage;
		assertEquals(((OSCMessage) h.lastMessage).getAddress(), "/xinfo");

		rr.setSubPath("/MixerNotRegistered/xinfo");
		h.handle(rr);
		message = (OSCMessage) h.lastMessage;
		assertEquals(((OSCMessage) h.lastMessage).getAddress(), "/xinfo");
		assertEquals(rr.getStatus(), Status.INTERNAL_ERROR);

		rr.setSubPath("/loopback/ch01~levels/123/4.5");
		h.handle(rr);
		message = (OSCMessage) h.lastMessage;
		assertEquals(message.getAddress(), "/ch01/levels");
		args = message.getArguments();
		assertNotNull(args, "arguments are null");
		assertEquals(args.size(), 2, "Wrong argument count (" + args.size() + ")");
		assertEquals(args.get(0).getClass(), Integer.class);
		assertEquals(((Integer) args.get(0)).intValue(), 123);

		assertEquals(args.get(1).getClass(), Float.class);
		float actual = ((Float) args.get(1)).floatValue();
		assertEquals(actual, 4.5f, 0.01f);
	}

}
