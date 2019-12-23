package org.chaseoaks.xair_proxy.data;

import java.util.Map;
import java.util.TreeMap;

import org.chaseoaks.xair_proxy.OSCProxyPacketListener;

import com.illposed.osc.OSCPacket;
import com.illposed.osc.transport.udp.OSCPortIn;
import com.illposed.osc.transport.udp.OSCPortOut;

public class OSCPortMap extends Base {

	protected int nextCommand = 40001;
	protected final int minCommand = 40000;
	protected final int maxCommand = 54999;
	protected int nextMeter = 55001;
	protected final int minMeter = 55000;
	protected final int maxMeter = 65499;

	protected Map<Integer, OSCPortIn> leased = new TreeMap<Integer, OSCPortIn>();

	public int getNextPort() {
		return this.getNextPort(false);
	}

	public int getNextPort(boolean forMetering) {
		int p = -1;
		int w = 0;
		if (!forMetering) {
			for (int i = 0; i < 1000; i++) {
				w = incCommand();
				if (!leased.containsKey(Integer.valueOf(w))) {
					p = w;
					break;
				}
			}
		} else {
			for (int i = 0; i < 1000; i++) {
				w = incMeter();
				if (!leased.containsKey(Integer.valueOf(w))) {
					p = w;
					break;
				}
			}

		}
		// if (p > 1000)
		// leased.put(Integer.valueOf(p), portIn);

		return p;
	}

	protected int incCommand() {
		if (nextCommand > maxCommand)
			return (nextCommand = minCommand);
		return nextCommand++;
	}

	protected int incMeter() {
		if (nextMeter > maxMeter)
			return (nextMeter = minMeter);
		return nextMeter++;
	}

	public Map<Integer, OSCPortIn> registerPort(int port, OSCPortIn portIn) {
		if (port > 1000)
			leased.put(Integer.valueOf(port), portIn);

		return leased;
	}

	public Map<Integer, OSCPortIn> getAllLeases() {
		return leased;
	}

	public Map<Integer, OSCPortIn> registerPort(OSCPortIn portIn, OSCPacket packet, OSCPortOut portOut,
			OSCPortIn portIn2, OSCProxyPacketListener listener) {

		return leased;
	}

	public void registerPort(RequestAssoc ra) {
		// TODO Auto-generated method stub
		
	}

}
