package org.chaseoaks.xair_proxy.data;

import java.util.Map;
import java.util.TreeMap;

public class ProxyStats extends Base {

	class MutableInt {
		public int value = 0;

		public MutableInt() {
		};

		public MutableInt(int startValue) {
			this.value = startValue;
		}
	}

	public int totalRequests;
	public Map<String, MutableInt> requestCounts;

	public ProxyStats() {
		this.totalRequests = 0;
		this.requestCounts = new TreeMap<String, MutableInt>();
	}

	public void countRequest(String request) {
		totalRequests++;

		if (request == null || request.length() < 1)
			return;

		if (requestCounts.containsKey(request)) {
			requestCounts.get(request).value++;
		} else {
			requestCounts.put(request, new MutableInt(1));
		}
	}

}
