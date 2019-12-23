package org.chaseoaks.xair_proxy.servlet;

import fi.iki.elonen.NanoHTTPD.Response.Status;

public class MetersHandler implements IGenericServlet {

	@Override
	public HandlerInfo getInfo() {
		// TODO Auto-generated method stub
		HandlerInfo h = new HandlerInfo();
		h.handlerId = "meters";
		h.mapping = h.handlerId;
		h.handlerInfo = "Channel level meters";
		h.handler = this;

		return h;
	}

	@Override
	// public Response handleold(NanoHTTPD server, IHTTPSession session, String
	// subPath) {
	public void handle(NanoReqResp reqResp) {

		String subPath = reqResp.getSubPath();
		if (subPath.startsWith("/start")) {
			this.startMeter(reqResp, subPath);
			return;
		}
		if ((subPath == null || subPath.length() == 0) && reqResp.getQueryParameterString().startsWith("status")) {
			this.serveStatus(reqResp, subPath);
			return;
		}

		// return NanoHTTPD
		// .newFixedLengthResponse((session.getQueryParameterString() == null ?
		// "queryParameterString == null"
		// : session.getQueryParameterString()));

		reqResp.setResponse("Bad request");
		reqResp.setStatus(Status.BAD_REQUEST);
		return;

	}

	protected void startMeter(NanoReqResp reqResp, String subPath) {
		// TODO Auto-generated method stub
		reqResp.setResponse("TODO");
		return;
	}

	protected void serveStatus(NanoReqResp reqResp, String subPath) {
		reqResp.setResponse("{\"meters\";1}");
	}

}
