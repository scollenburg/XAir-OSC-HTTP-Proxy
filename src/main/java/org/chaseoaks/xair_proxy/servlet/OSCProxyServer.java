package org.chaseoaks.xair_proxy.servlet;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.chaseoaks.xair_proxy.FactoryMaster;
import org.chaseoaks.xair_proxy.data.OSCPortMap;
import org.chaseoaks.xair_proxy.data.ProxyStats;
import org.chaseoaks.xair_proxy.data.RequestAssoc;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

/**
 * TODO We should use a threadpool
 * https://github.com/NanoHttpd/nanohttpd/wiki/Example:-Using-a-ThreadPool
 */

public class OSCProxyServer extends NanoHTTPD {

	public ProxyStats stats = new ProxyStats();
	protected Map<String, HandlerInfo> handlers;

	public OSCProxyServer(int port) {
		super(port);
		this.buildHandlers();
	}

	public OSCProxyServer(String hostname, int port) {
		super(hostname, port);
		this.buildHandlers();
	}

	public Map<String, HandlerInfo> getHandlers() {
		return this.handlers;
	}

	protected void buildHandlers() {
		if (this.handlers != null)
			return;

		this.handlers = ServletFactory.getHandlers();
	}

	public Response serve(IHTTPSession session) {
		// return newFixedLengthResponse(String.valueOf(++helloCount));
		stats.countRequest(session.getUri());
		String[] path = session.getUri().split("[/]");

		String prefix = "/root";
		int offset = 0;
		// if (path.length == 1) {
		// if (path[0].length() > 1) {
		// prefix = path[0];
		// offset = prefix.length() + 1;
		// }
		// } else if (path.length > 1) {
		for (int i = 0; i < path.length; i++) {
			if (path[i] != null && path[i].length() > 0) {
				prefix = path[i];
				offset += prefix.length();
				break;
			}
			offset++;
		}
		// }

		if (handlers.containsKey(prefix)) {
			NanoReqResp reqResp = new NanoReqResp(this, session, session.getUri().substring(0, offset));
			HandlerInfo hinfo = handlers.get(prefix);
			hinfo.handler.handle(reqResp);
			// Response response = reqResp.getNanoResponse();
			if (reqResp.getNanoResponse() != null)
				return reqResp.getNanoResponse();
		}

		Response response = newFixedLengthResponse("Not found");
		response.setStatus(Status.NOT_FOUND);
		return response;
	}

	@Override
	public void stop() {
		OSCPortMap portMap = FactoryMaster.getMaster().getPortMap();
		Map<Integer, RequestAssoc> ports = portMap.registerPort(null);
		closeAllOSCInPorts(ports);

		super.stop();
	}

	public static void closeAllOSCInPorts(Map<Integer, RequestAssoc> ports) {
		for (Iterator<Entry<Integer, RequestAssoc>> iterator = ports.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, RequestAssoc> entry = iterator.next();
			// RequestAssoc ra = (RequestAssoc) iterator.next();
			try {
				// ra.close();
				entry.getValue().close();
			} catch (Exception e) {
				// TODO LOGGING
				// NanoHTTPD.LOG.log(Level.SEVERE, "Could not stop all connections", e);
			}
		}
	}
}
