package org.chaseoaks.xair_proxy.servlet;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class HandlerInfo {

	public String mapping;
	public String handlerId;
	public String handlerInfo;
	@JsonIgnore
	public IGenericServlet handler;

}
