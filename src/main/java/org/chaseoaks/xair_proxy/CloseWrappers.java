package org.chaseoaks.xair_proxy;

import java.util.concurrent.ArrayBlockingQueue;

import org.chaseoaks.xair_proxy.data.IPMessage;
import org.chaseoaks.xair_proxy.servlet.OSCHandler;

import com.illposed.osc.transport.udp.OSCPortIn;

public class CloseWrappers {

	public static void safeSleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
		}
	}

	public static void closePortIn(OSCPortIn portIn) {

		portIn.stopListening();

		new Thread(() -> {
			safeSleep(100);
			try {
				portIn.close();
			} catch (Exception e) {
			}
		}).start();
	}

	@SuppressWarnings("rawtypes")
	public static void closeABQueue(ArrayBlockingQueue<IPMessage> o) {
		o.offer(OSCHandler.COOLDOWN_IPMESSAGE);

		new Thread(() -> {
			safeSleep(100);
			try {
				o.clear();
			} catch (Exception e) {
			}
		}).start();

	}

}
