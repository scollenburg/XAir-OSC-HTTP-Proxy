package org.chaseoaks.xair_proxy.xair;

import java.util.Map;
import java.util.TreeMap;

import org.chaseoaks.xair_proxy.data.LevelBuffer;

/**
 * Buffer for various metering requests
 * 
 * <p>
 * Each request uses a #LevelBuffer {@link LevelBuffer} to store the returned
 * levels.
 * 
 * @author scollenburg
 *
 */
// public class MetersBuffer implements Map<Integer,LevelBuffer> {
public class MetersBuffer {

	protected Map<Integer, LevelBuffer> portMap;
	protected Map<String, LevelBuffer> commandMap;

	public MetersBuffer() {
		this.portMap = new TreeMap<Integer, LevelBuffer>();
		this.commandMap = new TreeMap<String, LevelBuffer>();
	}

}
