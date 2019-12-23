package org.chaseoaks.xair_proxy.data;

import java.time.LocalDateTime;
import java.util.concurrent.ArrayBlockingQueue;

import org.chaseoaks.xair_proxy.OSCProxyPacketListener;
import org.chaseoaks.xair_proxy.servlet.NanoReqResp;

import com.illposed.osc.OSCPacket;
import com.illposed.osc.transport.udp.OSCPortIn;
import com.illposed.osc.transport.udp.OSCPortOut;

public class RequestAssoc {
	public NanoReqResp reqResp;
	public OSCPacket packet;
	public OSCPortIn portIn;
	public OSCPortOut portOut;
	public OSCProxyPacketListener listener;
	public ArrayBlockingQueue<IPMessage> abQueue;
	public LocalDateTime sendTime;

	public RequestAssoc(NanoReqResp rr, OSCPacket op) {
		this.reqResp = rr;
		this.packet = op;
	}

	public ArrayBlockingQueue<IPMessage> set(ArrayBlockingQueue<IPMessage> abq) {
		this.abQueue = abq;
		return abq;
	}

	public OSCPortIn set(OSCPortIn port) {
		this.portIn = port;
		return port;
	}

	public OSCProxyPacketListener set(OSCProxyPacketListener l) {
		this.listener = l;
		return l;
	}

	public OSCPortOut set(OSCPortOut port) {
		this.portOut = port;
		return port;
	}

	public LocalDateTime setSendTime() {
		this.sendTime = LocalDateTime.now();
		return this.sendTime;
	}
}
