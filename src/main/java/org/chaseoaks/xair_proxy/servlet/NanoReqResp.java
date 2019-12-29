package org.chaseoaks.xair_proxy.servlet;

import java.io.Closeable;
import java.io.IOException;

import org.chaseoaks.xair_proxy.data.RequestAssoc;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class NanoReqResp implements Cloneable, Closeable {

	protected final static String EMPTY_STRING = "";

	protected OSCProxyServer server;
	protected IHTTPSession session;
	protected String contextPath;
	protected Response nanoResponse;
	protected String responseString;
	protected Status responseStatus;

	protected RequestAssoc requestAssoc;

	public NanoReqResp(OSCProxyServer server, IHTTPSession session, String contextPath) {
		this.server = server;
		this.session = session;
		this.contextPath = contextPath;
	}

	@Deprecated
	public NanoReqResp(NanoReqResp reqResp, String contextPath) {
		this.server = reqResp.server;
		this.session = reqResp.session;
		this.contextPath = contextPath;
		this.responseString = reqResp.responseString;
		this.responseStatus = reqResp.responseStatus;
		this.nanoResponse = reqResp.nanoResponse;
	}

	public String getContextPath() {
		return contextPath;
	}

	public NanoReqResp updateContextPath(String contextPath) {
		this.contextPath = contextPath;
		return this;
	}

	public OSCProxyServer getServer() {
		return server;
	}

	public String getSubPath() {
		if (contextPath != null && contextPath.length() > 0) {
			String u = session.getUri();
			return u.substring(contextPath.length(), u.length());
		}

		return EMPTY_STRING;
	}

	@Override
	public NanoReqResp clone() {
		NanoReqResp theClone = new NanoReqResp(this.server, this.session, this.contextPath);
		theClone.responseString = this.responseString;
		theClone.responseStatus = this.responseStatus;
		theClone.nanoResponse = this.nanoResponse;
		return theClone;
	}

	public NanoReqResp setResponse(String responseString) {
		this.responseString = responseString;
		this.nanoResponse = null;
		return this;
	}

	/**
	 * Helper method to enable late binding of buffered response data & build a
	 * {@link fi.iki.elonen.NanoHTTPD.Response Nano.Reponse}
	 * 
	 * @return fi.iki.elonen.NanoHTTPD.Response
	 */
	public Response getNanoResponse() {
		if (this.nanoResponse != null)
			return this.nanoResponse;

		if (this.nanoResponse != null) {
			if (this.responseString == null && this.responseStatus != null) {
				if (this.responseStatus == Status.OK) {
					this.responseString = "OK";
				} else
					this.responseString = "Error";
			}
		}

		if (this.responseString != null) {
			this.nanoResponse = NanoHTTPD.newFixedLengthResponse(this.responseString);
		}

		if (this.responseStatus != null)
			this.nanoResponse.setStatus(responseStatus);

		return this.nanoResponse;
	}

	public void setStatus(Status responseStatus) {
		this.responseStatus = responseStatus;
	}

	public String getQueryParameterString() {
		if (this.session.getQueryParameterString() == null)
			return EMPTY_STRING;
		return this.session.getQueryParameterString();
	}

	@Override
	public void close() throws IOException {
		// noop
	}

	public void setRequestAssoc(RequestAssoc ra) {
		this.requestAssoc = ra;
	}

}
