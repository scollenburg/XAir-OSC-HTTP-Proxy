package org.chaseoaks.xair_proxy.servlet;

import org.chaseoaks.xair_proxy.data.Base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StatsHandler implements IGenericServlet {

	@Override
	public HandlerInfo getInfo() {
		// TODO Auto-generated method stub
		HandlerInfo h = new HandlerInfo();
		h.handlerId = "stats";
		h.mapping = h.handlerId;
		h.handlerInfo = "Usage statistics";
		h.handler = this;

		return h;
	}

	@Override
//	public Response handle(NanoHTTPD server, IHTTPSession session, String subPath) {
	public void handle(NanoReqResp reqResp) {

		ObjectMapper mapper = Base.getMapper();
		String jsonString = null;
		try {
			jsonString = mapper.writeValueAsString(reqResp.getServer().stats);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		reqResp.setResponse(jsonString);
		return;

	}

}
