package org.chaseoaks.xair_proxy;

import java.util.Map;

public class OSCPortRegistry {

	protected int lastMeteringPort = 60000;
	protected int lastCommandPort = 30000;
	
	protected Map<Integer,Integer> meters;
	protected Map<Integer,Integer> commands;
	

}
