package org.chaseoaks.xair_proxy;

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
	protected MetersBuffer metersBuffer;

	/**
	 * Allow override of default master factory. Yes, there are other cooler, more
	 * advanced ways to stub (e.g. Beans), but the goal is to be minimalistic.
	 * 
	 * @param master
	 */
	public static void setMaster(FactoryMaster master) {
		FactoryMaster.master = master;
	}

	/**
	 * Generate default master factory
	 */
	public static void setMaster() {
		if (FactoryMaster.master == null)
			FactoryMaster.master = new FactoryMaster();
	}

	public static FactoryMaster getMaster() {
		return FactoryMaster.master;
	}

	public MetersBuffer getMetersBuffer() {
		if (this.metersBuffer == null)
			this.metersBuffer = new MetersBuffer();

		return this.metersBuffer;
	}
}
