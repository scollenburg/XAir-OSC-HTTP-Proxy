package org.chaseoaks.xair_proxy.data;

public class IPMessage<T> extends Base {
	public String message;
	public T data;
	public int rc;

	public IPMessage(String message) {
		this(message, null, -1);
	}

	public IPMessage(String message, T data, int rc) {
		this.message = message;
		this.data = data;
		this.rc = rc;
	}
}
