package org.chaseoaks.xair_proxy.data;

import java.util.Map;

import org.chaseoaks.xair_proxy.data.OSCRequest;

public class MixerInfo {

	public String alias;
	public int mixerId;
	public String mixerAddress;
	public int mixerPort;
	public Map<String, OSCRequest> requestMap;
	public boolean active;
}
