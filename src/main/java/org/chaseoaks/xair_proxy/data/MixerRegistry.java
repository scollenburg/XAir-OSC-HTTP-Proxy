package org.chaseoaks.xair_proxy.data;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.TreeMap;

import org.chaseoaks.xair_proxy.FactoryMaster;
import org.chaseoaks.xair_proxy.xair.OSCEchoPacketListener;
import org.chaseoaks.xair_proxy.xair.OSCPortInEx;

import com.illposed.osc.transport.udp.OSCPortIn;

public class MixerRegistry extends Base {

	protected Map<String, MixerInfo> registry = new TreeMap<String, MixerInfo>();
	protected int mixerCount = 1;

	public Map<String, MixerInfo> add(MixerInfo mixer) {
		registry.put(mixer.alias, mixer);

		if (mixer.alias.equalsIgnoreCase("echo")) {
			registry.put("127", mixer);
		} else {
			registry.put(String.valueOf(mixerCount++), mixer);
		}
		return registry;
	}

	public MixerInfo get(String id) {
		if (id == null)
			return null;
		if (id.equalsIgnoreCase("echo")) {
			if (!registry.containsKey("echo"))
				this.add(buildEchoRegistry());
		}
		return registry.get(id);
	}

	public Map<String, MixerInfo> getAll() {
		return registry;
	}

	public static MixerInfo buildEchoRegistry() {
		MixerInfo mi = new MixerInfo();
		mi.alias = "echo";
		mi.mixerId = 127;
		mi.mixerAddress = "127.0.0.1";
		mi.mixerPort = 12701;
		mi.active = true;
		return mi;
	}

	public static int startEchoListener() {
		// Probably "me", but be nice and pull it from the factory
		MixerRegistry mr = FactoryMaster.getMaster().getMixerRegistry();

		MixerInfo mixer = mr.get("echo");

		if (mixer == null)
			return -1;

		OSCEchoPacketListener echoListener = new OSCEchoPacketListener();
		OSCPortIn portInEcho = null;
		int retry = 10000;
		while (retry >= 1) {
			try {
				portInEcho = new OSCPortInEx(new InetSocketAddress(InetAddress.getByName("0.0.0.0"), mixer.mixerPort));
				portInEcho.setDaemonListener(false);
				portInEcho.addPacketListener(echoListener);
				portInEcho.startListening();
				break;
			} catch (UnknownHostException e) {
				break;
			} catch (IOException e) {
				// TODO LOGGING
				mixer.mixerPort++;
				retry--;
			}
		}

		if (portInEcho == null) {
			// TODO LOGGING
			return -1;
		}

		RequestAssoc ra = new RequestAssoc(null, null);
		ra.extAlias = "echo";
		ra.inIPPort = mixer.mixerPort;
		ra.set(portInEcho);

		OSCPortMap portMap = FactoryMaster.getMaster().getPortMap();
		portMap.registerPort(ra);

		return mixer.mixerPort;
	}
}
