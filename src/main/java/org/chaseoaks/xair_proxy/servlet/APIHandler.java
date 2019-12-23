package org.chaseoaks.xair_proxy.servlet;

import org.chaseoaks.xair_proxy.data.Base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.iki.elonen.NanoHTTPD.Response.Status;

public class APIHandler implements IGenericServlet {

	protected StatsHandler statsHandler = new StatsHandler();

	@Override
	public HandlerInfo getInfo() {
		// TODO Auto-generated method stub
		HandlerInfo h = new HandlerInfo();
		h.handlerId = "api";
		h.mapping = h.handlerId;
		h.handlerInfo = "API dispatcher";
		h.handler = this;

		return h;
	}

	@Override
	// public Response handle(NanoHTTPD server, IHTTPSession session, String
	// subPath) {
	public void handle(NanoReqResp reqResp) {

		String subPath = reqResp.getSubPath();
		if (subPath.startsWith("/stats")) {

			statsHandler.handle(reqResp.updateContextPath(reqResp.getContextPath() + "/stats"));
			return;
		}

		if (subPath.startsWith("/servlets")) {

			ObjectMapper mapper = Base.getMapper();
			String jsonString = null;
			try {
				jsonString = mapper.writeValueAsString(reqResp.getServer().getHandlers());
			} catch (JsonProcessingException e) {
				// e.printStackTrace();
			}

			if (jsonString != null && jsonString.length() > 0)
				reqResp.setResponse(jsonString);
			else
				reqResp.setStatus(Status.NOT_FOUND);

			return;
		}

		return;

	}

}
