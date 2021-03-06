package org.chaseoaks.xair_proxy.data;

import java.util.Map;
import java.util.SplittableRandom;
import java.util.TreeMap;

public class OSCPortMap extends Base {

	protected int nextCommand = 0;
	protected final int minCommand = 40000;
	protected final int maxCommand = 54999;
	protected final int dCommand;
	protected int nextMeter = 0;
	protected final int minMeter = 55000;
	protected final int maxMeter = 65499;
	protected final int dMeter;

	protected Map<Integer, RequestAssoc> leased = new TreeMap<Integer, RequestAssoc>();
	/**
	 * Fast randoms:
	 * https://lemire.me/blog/2016/02/01/default-random-number-generators-are-slow/
	 * http://prng.di.unimi.it/
	 */
	SplittableRandom rand = new SplittableRandom();

	public OSCPortMap() {
		dCommand = maxCommand - minCommand;
		dMeter = maxMeter - minMeter;
		this.init();
	}

	protected void init() {
		nextCommand = rand.nextInt(dCommand / 2) + minCommand;
		nextMeter = rand.nextInt(dMeter / 2) + minMeter;
		incCommand();
		incMeter();
		incCommand();
		incMeter();
	}

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
		nextCommand += rand.nextInt(157) + 1;
		if (nextCommand > maxCommand)
		// return (nextCommand = (minCommand + rand.nextInt(157) + 1));
		{
			int i = 5;
			while (nextCommand > maxCommand && i-- > 0) {
				nextCommand -= (dCommand);
			}
		}
		return nextCommand;
	}

	protected int incMeter() {
		nextMeter += rand.nextInt(157) + 1;
		if (nextMeter > maxMeter)
		// return (nextMeter = minMeter);
		{
			int i = 5;
			while (nextMeter > maxMeter && i-- > 0) {
				nextMeter -= (dMeter);
			}
		}
		return nextMeter;
	}

	// public Map<Integer, RequestAssoc> registerPort(int port, OSCPortIn portIn) {
	// if (port > 1000)
	// leased.put(Integer.valueOf(port), portIn);
	// return leased;
	// }
	//
	// public Map<Integer, OSCPortIn> getAllLeases() {
	// return leased;
	// }
	//
	// public Map<Integer, OSCPortIn> registerPort(OSCPortIn portIn, OSCPacket
	// packet, OSCPortOut portOut,
	// OSCPortIn portIn2, OSCProxyPacketListener listener) {
	//
	// return leased;
	// }

	public Map<Integer, RequestAssoc> registerPort(RequestAssoc ra) {
		if (ra != null && ra.inIPPort > 1000)
			leased.put(Integer.valueOf(ra.inIPPort), ra);
		return leased;
	}

}
