package org.chaseoaks.xair_proxy;

import org.chaseoaks.xair_proxy.data.MixerRegistry;
import org.chaseoaks.xair_proxy.data.OSCPortMap;
import org.chaseoaks.xair_proxy.xair.MetersBuffer;

/**
 * Stubbable master Factory.
 * 
 * <ul>
 * <li>For performance reasons, {@link #setMaster(FactoryMaster)} or
 * {@link #setMaster()} (default master factory) must be called prior to being
 * used.</li>
 * </ul>
 * 
 * @author scollenburg
 *
 */
public class FactoryMaster {

	protected static FactoryMaster master;
	protected static MetersBuffer metersBuffer;
	protected static MixerRegistry mixers;
	protected static OSCPortMap portMap;

	/**
	 * Allow override of default master factory. Yes, there are other cooler, more
	 * advanced ways to stub (e.g. Beans), but the goal is to be minimalistic.
	 * 
	 * @param master
	 */
	public static void setMaster(FactoryMaster master) {
		FactoryMaster.master = master;
	}

	public static FactoryMaster getMaster() {
		if (FactoryMaster.master == null)
			FactoryMaster.master = new FactoryMaster();
		return FactoryMaster.master;
	}

	public MetersBuffer getMetersBuffer() {
		if (FactoryMaster.metersBuffer == null)
			FactoryMaster.metersBuffer = new MetersBuffer();

		return FactoryMaster.metersBuffer;
	}

	public MixerRegistry getMixerRegistry() {
		if (FactoryMaster.mixers == null) {
			FactoryMaster.mixers = new MixerRegistry();
			FactoryMaster.mixers.add(MixerRegistry.buildEchoRegistry());
		}

		return FactoryMaster.mixers;
	}

	public OSCPortMap getPortMap() {
		if (FactoryMaster.portMap == null)
			FactoryMaster.portMap = new OSCPortMap();

		return FactoryMaster.portMap;
	}

}
