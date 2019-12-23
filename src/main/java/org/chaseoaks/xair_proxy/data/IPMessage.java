package org.chaseoaks.xair_proxy.data;

public class IPMessage extends Base {
	public String message;
	public Object data;
	public int rc;

	public IPMessage(String message) {
		this(message, null, -1);
	}

	public IPMessage(String message, Object data, int rc) {
		this.message = message;
		this.data = data;
		this.rc = rc;
	}
}
