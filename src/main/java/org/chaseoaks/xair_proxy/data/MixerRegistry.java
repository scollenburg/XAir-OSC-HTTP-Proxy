package org.chaseoaks.xair_proxy.data;

import java.util.Map;
import java.util.TreeMap;

public class MixerRegistry extends Base {

	protected Map<String, MixerInfo> registry = new TreeMap<String, MixerInfo>();
	protected int mixerCount = 1;

	public Map<String, MixerInfo> add(MixerInfo mixer) {
		registry.put(mixer.alias, mixer);

		if (mixer.alias.equalsIgnoreCase("loopback")) {
			registry.put("127", mixer);
		} else {
			registry.put(String.valueOf(mixerCount++), mixer);
		}
		return registry;
	}

	public MixerInfo get(String id) {
		return registry.get(id);
	}

	public Map<String, MixerInfo> getAll() {
		return registry;
	}

	public static MixerInfo buildLoopback() {
		MixerInfo mi = new MixerInfo();
		mi.alias = "loopback";
		mi.mixerId = 127;
		mi.mixerAddress = "127.0.0.1";
		mi.mixerPort = 12701;
		mi.active = true;
		return mi;
	}
	
	public static void startLoopback() {
		
	}
}
