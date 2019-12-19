package org.chaseoaks.xair_proxy;

import fi.iki.elonen.NanoHTTPD;

/**
 * TODO We should use a threadpool
 * https://github.com/NanoHttpd/nanohttpd/wiki/Example:-Using-a-ThreadPool
 */

public class OSCProxyServer extends NanoHTTPD {

	public int helloCount = 0;

	public OSCProxyServer(int port) {
		super(port);
	}

	public OSCProxyServer(String hostname, int port) {
		super(hostname, port);
	}

	public Response serve(IHTTPSession session) {
		return newFixedLengthResponse(String.valueOf(++helloCount));
	}

}
