package org.chaseoaks.xair_proxy.servlet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.chaseoaks.xair_proxy.FactoryMaster;
import org.chaseoaks.xair_proxy.OSCProxyPacketListener;
import org.chaseoaks.xair_proxy.data.IPMessage;
import org.chaseoaks.xair_proxy.data.MixerInfo;
import org.chaseoaks.xair_proxy.data.MixerRegistry;
import org.chaseoaks.xair_proxy.data.OSCPortMap;
import org.chaseoaks.xair_proxy.data.RequestAssoc;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import com.illposed.osc.transport.udp.OSCPortIn;
import com.illposed.osc.transport.udp.OSCPortOut;

import fi.iki.elonen.NanoHTTPD.Response.Status;

public class OSCHandler implements IGenericServlet {

	public static final String NUMBER_PATTERN = "^\\d+([.E+]{1,2}\\d+|$)";
	public static final Pattern numberPattern = Pattern.compile(NUMBER_PATTERN);
	public static final Pattern FIRST_SEGMENT = Pattern.compile("([/](\\w*))([/].*)");

	public static final String COOLDOWN = "CoolDown";
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
			ra.set(buildABQueue()); // ArrayBlockingQueue<IPMessage> abQueue =

			OSCPortMap portMap = FactoryMaster.getMaster().getPortMap();
			int inIPPort = portMap.getNextPort();

			OSCPortIn portIn = ra.set(buildOSCPortIn(inIPPort));

			OSCProxyPacketListener listener = ra.set(buildListener(ra));
			portIn.addPacketListener(listener);

			OSCPortOut portOut = ra.set(buildOSCPortOut(mixerInfo));
			if (portOut == null) {
				if (portIn != null)
					portIn.close();
				reqResp.setResponse("Could not build PortOut for Mixer '" + mixer + "'");
				reqResp.setStatus(Status.INTERNAL_ERROR);
				return;
			}

			portMap.registerPort(ra);

			portIn.startListening();
			ra.setSendTime();
			portOut.send(packet);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			reqResp.setStatus(Status.INTERNAL_ERROR);
			reqResp.setResponse("Could not send request to mixer '" + mixer + "': " + e.getMessage());
			return;
		}

	}

	protected OSCProxyPacketListener buildListener(RequestAssoc ra) {
		// TODO Auto-generated method stub
		return null;
	}

	protected ArrayBlockingQueue<IPMessage> buildABQueue() {
		ArrayBlockingQueue<IPMessage> queue = new ArrayBlockingQueue<>(10, false);
		return queue;
	}

	protected OSCPortIn buildOSCPortIn(int inIPPort) throws IOException {
		return new OSCPortIn(inIPPort);
	}

	protected OSCPortOut buildOSCPortOut(MixerInfo mixerInfo) throws UnknownHostException, IOException {
		return new OSCPortOut(InetAddress.getByName(mixerInfo.mixerAddress), mixerInfo.mixerPort);
	}

}
