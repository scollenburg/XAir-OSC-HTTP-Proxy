package org.chaseoaks.xair_proxy.servlet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.chaseoaks.xair_proxy.FactoryMaster;
import org.chaseoaks.xair_proxy.data.Base;
import org.chaseoaks.xair_proxy.data.IPMessage;
import org.chaseoaks.xair_proxy.data.MixerInfo;
import org.chaseoaks.xair_proxy.data.MixerRegistry;
import org.chaseoaks.xair_proxy.data.OSCPortMap;
import org.chaseoaks.xair_proxy.data.RequestAssoc;
import org.chaseoaks.xair_proxy.xair.OSCProxyPacketListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import com.illposed.osc.OSCPacketEvent;
import com.illposed.osc.OSCSerializerFactory;
import com.illposed.osc.transport.udp.OSCPortIn;
import com.illposed.osc.transport.udp.OSCPortOut;

import fi.iki.elonen.NanoHTTPD.Response.Status;

public class OSCHandler implements IGenericServlet {

	public static final String NUMBER_PATTERN = "^\\d+([.E+]{1,2}\\d+|$)";
	public static final Pattern numberPattern = Pattern.compile(NUMBER_PATTERN);
	public static final Pattern FIRST_SEGMENT = Pattern.compile("([/](\\w*))([/].*)");

	public static final String COOLDOWN = "CoolDown";
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final IPMessage COOLDOWN_IPMESSAGE = new IPMessage("COOLDOWN", null, -999999);

	protected MetersHandler metersHandler = new MetersHandler();
	// handler = new MetersHandler();
	// hi = handler.getInfo();
	// handlers.put(hi.handlerId, hi);

	@Override
	public HandlerInfo getInfo() {
		HandlerInfo h = new HandlerInfo();
		h.handlerId = "xap";
		h.mapping = h.handlerId;
		h.handlerInfo = "OSC Command Processor";
		h.handler = this;

		return h;
	}

	@Override
	// public Response handle(NanoHTTPD server, IHTTPSession session, String
	// subPath) {
	public void handle(NanoReqResp reqResp) {

		String subPath = reqResp.getSubPath();
		Matcher segments = FIRST_SEGMENT.matcher(subPath);
		segments.matches();

		if (segments.group(3).startsWith("/meters")) {
			metersHandler.handle(reqResp.updateContextPath(reqResp.getContextPath() + "/stats"));
			return;
		}

		OSCPacket packet = marshallOSC(reqResp);

		if (packet != null) {
			dispatchOSC(segments.group(2), packet, reqResp);
			waitForReponse(reqResp);
		}

		return;

	}

	protected OSCPacket marshallOSC(NanoReqResp reqResp) {

		String subPath = reqResp.getSubPath();
		if (subPath == null || subPath.length() == 0)
			return streamToOSC(reqResp);

		String[] split = subPath.split("/");

		if (split.length == 0)
			return null;

		List<Object> args = new ArrayList<>();
		String address = "/" + split[2].replace('~', '/');

		Matcher m;
		for (int i = 3; i < split.length; i++) {
			if (split[i] == null || split[i].length() == 0)
				continue;
			m = numberPattern.matcher(split[i]);
			if (m.matches()) {
				if (m.groupCount() >= 1 && m.group(1).length() > 0) {
					args.add(Float.valueOf(split[i]));
				} else {
					args.add(Integer.valueOf(split[i]));
				}
			} else {
				args.add(split[i]);
			}
		}

		if (args.size() > 0) {
			return new OSCMessage(address, args);
		} else {
			return new OSCMessage(address);
		}

		// return null;
	}

	protected OSCPacket streamToOSC(NanoReqResp reqResp) {
		// TODO FIXME Auto-generated method stub
		return null;
	}

	protected void dispatchOSC(String mixer, OSCPacket packet, NanoReqResp reqResp) {

		MixerRegistry mReg = FactoryMaster.getMaster().getMixerRegistry();

		MixerInfo mixerInfo = mReg.get(mixer);

		if (mixerInfo == null || mixerInfo.mixerAddress == null || mixerInfo.mixerAddress.length() == 0) {
			reqResp.setResponse("Mixer '" + mixer + "' not registered");
			reqResp.setStatus(Status.INTERNAL_ERROR);
			return;
		}

		try {
			RequestAssoc ra = new RequestAssoc(reqResp, packet);
			reqResp.setRequestAssoc(ra);
			ra.set(buildABQueue()); // ArrayBlockingQueue<IPMessage> abQueue =

			OSCProxyPacketListener listener = ra.set(buildListener(ra));
			OSCPortIn portIn = buildOSCPortIn(ra);
			portIn.addPacketListener(listener);

			OSCPortOut portOut = ra.set(buildOSCPortOut(ra, mixerInfo));
			if (portOut == null) {
				if (portIn != null)
					portIn.close();
				reqResp.setResponse("Could not build PortOut for Mixer '" + mixer + "'");
				reqResp.setStatus(Status.INTERNAL_ERROR);
				return;
			}

			OSCPortMap portMap = FactoryMaster.getMaster().getPortMap();
			portMap.registerPort(ra);

			portIn.startListening();
			ra.setSendTime();
			portOut.send(packet);

		} catch (Exception e) {
			// TODO LOGGING
			reqResp.setStatus(Status.INTERNAL_ERROR);
			reqResp.setResponse("Could not send request to mixer '" + mixer + "': " + e.getMessage());
			return;
		}

	}

	protected OSCProxyPacketListener buildListener(RequestAssoc ra) {
		OSCProxyPacketListener listener = new OSCProxyPacketListener(ra);
		return listener;
	}

	protected ArrayBlockingQueue<IPMessage<OSCPacketEvent>> buildABQueue() {
		ArrayBlockingQueue<IPMessage<OSCPacketEvent>> queue = new ArrayBlockingQueue<>(10, false);
		return queue;
	}

	protected OSCPortIn buildOSCPortIn(RequestAssoc ra) throws IOException {
		OSCPortMap portMap = FactoryMaster.getMaster().getPortMap();
		int inIPPort;

		OSCPortIn port = null;
		for (int i = 0; i < 5; i++) {
			inIPPort = portMap.getNextPort();
			port = new OSCPortIn(inIPPort);
			port.setDaemonListener(false);
			port.setResilient(true);
			port.startListening();
			if (port.isListening()) {
				ra.portIn = port;
				ra.inIPPort = inIPPort;
				break;
			}
			port.isConnected();
			port = null;
		}

		return port;
	}

	protected OSCPortOut buildOSCPortOut(RequestAssoc ra, MixerInfo mixerInfo)
			throws UnknownHostException, IOException {
		// return new OSCPortOut(InetAddress.getByName(mixerInfo.mixerAddress),
		// mixerInfo.mixerPort);
		// OSCPortOut portOut =
		return new OSCPortOut(OSCSerializerFactory.createDefaultFactory(),
				new InetSocketAddress(mixerInfo.mixerAddress, mixerInfo.mixerPort), new InetSocketAddress(ra.inIPPort));

	}

	protected void waitForReponse(NanoReqResp reqResp) {

		IPMessage<OSCPacketEvent> message = null;
		boolean done = false;
		int guard = 4;

		while (!done) {
			try {
				message = reqResp.requestAssoc.abQueue.poll(25, TimeUnit.MILLISECONDS);
				if (message == null)
					continue;
				if (message.rc >= 0)
					done = true;
				if (message.data != null) {
					if (message.data instanceof OSCPacketEvent) {
						ObjectMapper om = Base.getMapper();
						reqResp.setResponse(om.writeValueAsString(message.data.getPacket()));
						reqResp.setStatus(Status.OK);
					}
				}

			} catch (InterruptedException | JsonProcessingException e) {
				// TODO LOGGING Auto-generated catch block
				// e.printStackTrace();
			}
			if (message == null) {
				if (guard-- <= 0)
					break;
				continue;
			}
		} // while
	}

}
