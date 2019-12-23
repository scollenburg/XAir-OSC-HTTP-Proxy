package org.chaseoaks.xair_proxy.servlet;

/**
 * For now, only NanoHTTPD invocation is supported.
 * 
 * @author scollenburg
 */
public interface IGenericServlet {

	/**
	 * Get information, e.g. description. Note mapping & handlerId may be change.
	 * 
	 * @return {@link HandlerInfo}
	 */
	public HandlerInfo getInfo();

	/**
	 * Handle a single request.
	 * 
	 * @param session
	 * @return {@link fi.iki.elonen.NanoHTTPD.Response} The response. If null, a
	 *         HTTP 404 will be returned.
	 */
	public void handle(NanoReqResp reqResp);

}
