package org.chaseoaks.xair_proxy.servlet;

import java.util.Map;
import java.util.TreeMap;

public class ServletFactory {

	// public ServletFactory() {
	// // TODO Auto-generated constructor stub
	// }

	protected static ServletFactory sf = null;

	public static Map<String, HandlerInfo> getHandlers() {
		if (sf != null)
			return sf._getHandlers();

		return getDefaultHandlers();
	}

	public static void setFactory(ServletFactory ex) {
		sf = ex;
	}

	protected Map<String, HandlerInfo> _getHandlers() {
		return null;
	}

	protected static Map<String, HandlerInfo> getDefaultHandlers() {

		Map<String, HandlerInfo> handlers = new TreeMap<String, HandlerInfo>();

		IGenericServlet handler;
		HandlerInfo hi;

		handler = new APIHandler();
		hi = handler.getInfo();
		handlers.put(hi.handlerId, hi);

		// handler = new MetersHandler();
		// hi = handler.getInfo();
		// handlers.put(hi.handlerId, hi);
		handler = new OSCHandler();
		hi = handler.getInfo();
		handlers.put(hi.handlerId, hi);

		return handlers;
	}

}
