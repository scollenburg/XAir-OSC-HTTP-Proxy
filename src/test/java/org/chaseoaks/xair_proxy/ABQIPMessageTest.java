package org.chaseoaks.xair_proxy;

import static org.testng.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.chaseoaks.xair_proxy.data.IPMessage;
import org.testng.annotations.Test;

import com.illposed.osc.OSCMessage;

public class ABQIPMessageTest {

	Random random = new Random();

	class StatsHolder {
		public StringBuffer summary = new StringBuffer(1000);
		public int receiveCount = 0;
		public int flushCount = 0;
		public int sendCount = 0;
		public boolean trace = false;
	}

	public static void safeSleep(int timeout) {
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
		}

	}

	public class Producer implements Runnable {

		ArrayBlockingQueue<IPMessage> queue;
		IPMessage infos[];
		StatsHolder stats;

		public Producer(ArrayBlockingQueue<IPMessage> abq, StatsHolder stats, IPMessage[] messages) {
			this.queue = abq;
			this.stats = stats;
			infos = messages;
		}

		@Override
		public void run() {

			System.out.format("Little nap...\n");
			safeSleep(500);

			boolean notFull;

			for (int i = 0; i < infos.length; i++) {
				if (infos[i] == null)
					continue;
				if (stats.trace)
					System.out.format("SEND %d\n", infos[i].rc);

				// messanger.put(infos[i]);
				try {
					for (int j = 0; j < 5; j++) {
						notFull = queue.offer(infos[i], 10, TimeUnit.MILLISECONDS);
						if (notFull) {
							stats.sendCount++;
							break;
						}
						IPMessage trash = queue.take();
						if (stats.trace)
							System.out.format("     %d f-l-u-s-h-e-d\n", trash.rc);
						stats.flushCount++;
					}

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}

				safeSleep(random.nextInt(60) + 30);
			} // for
		} // run()
	} // class Producer

	public class Consumer implements Runnable {
		private ArrayBlockingQueue<IPMessage> queue;
		StatsHolder stats;

		public Consumer(ArrayBlockingQueue<IPMessage> abq, StatsHolder stats) {
			this.queue = abq;
			this.stats = stats;
		}

		public void run() {

			IPMessage message = null;
			boolean done = false;
			int guard = 100;

			while (!done) {

				try {
					message = queue.poll(750, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (message == null) {
					if (guard-- <= 0)
						break;
					continue;
				}

				done = message.message.equals("TAIL");

				stats.receiveCount++;
				stats.summary.append(message.toJson());
				if (stats.trace)
					System.out.format("     %d  RECEIVED: %s\n", message.rc, message.toJson());

				message = null;
				guard = 100;
				safeSleep(random.nextInt(300));

			}

			stats.receiveCount += 10000;
		}
	} // class Consumer

	@Test(timeOut = 5000)
	public void ashortQueueTest() {

		ArrayBlockingQueue<IPMessage> queue = new ArrayBlockingQueue<>(10, false);
		StatsHolder stats = new StatsHolder();

		IPMessage[] messages = shortMessageList();
		(new Thread(new Producer(queue, stats, messages))).start();
		(new Thread(new Consumer(queue, stats))).start();

		for (int i = 0; i < 20; i++) {
			safeSleep(500);
			if (stats.receiveCount >= 10000)
				break;
		}
		stats.receiveCount -= 10000;

		assertEquals(stats.receiveCount, 5);

		assertEquals(stats.summary.indexOf("Mares eat oats"), 12, "DONE not found");

		assertEquals(
				stats.summary.indexOf(
						"{\"address\":\"/messageDos\",\"arguments\":[\"foo\",\"bar\",\"bazzzz\"],\"rc\":12345}"),
				227, "OSCMessage with arguments not found");

		assertEquals(stats.summary.indexOf("TAIL"), 311, "TAIL not found");

	}

	public void longQueueTest(boolean doTrace) {

		ArrayBlockingQueue<IPMessage> queue = new ArrayBlockingQueue<>(10, false);
		StatsHolder stats = new StatsHolder();

		stats.trace = doTrace;
		int messageBufferSize = 100;
		IPMessage[] messages = longMessageList(messageBufferSize);
		(new Thread(new Producer(queue, stats, messages))).start();
		(new Thread(new Consumer(queue, stats))).start();

		int waitCount = 0;
		for (waitCount = 0; waitCount < 40; waitCount++) {
			safeSleep(500);
			if (stats.receiveCount >= 10000)
				break;
		}

		stats.receiveCount -= 10000;
		System.out.format("sendCount: %d  receiveCount: %d  flushCount: %d  waitCount: %d\n", stats.sendCount,
				stats.receiveCount, stats.flushCount, waitCount);

		assertTrue(stats.sendCount >= messageBufferSize, "Low sendCount: " + String.valueOf(stats.receiveCount));
		assertTrue(stats.receiveCount >= 44, "Low receiveCount: " + String.valueOf(stats.receiveCount));
		assertTrue(stats.receiveCount <= 67, "HIGH receiveCount: " + String.valueOf(stats.receiveCount));
		assertTrue(stats.flushCount >= 37, "Low flushCount: " + String.valueOf(stats.flushCount));
		assertTrue(stats.flushCount <= 54, "HIGH flushCount: " + String.valueOf(stats.flushCount));

		int iof;
		iof = stats.summary.indexOf("\"rc\":2", 300);
		System.out.format("rc 2x %d  ", iof);
		assertTrue((iof > 365 && iof < 705), "rc:2x not found (" + String.valueOf(iof) + ")");

		iof = stats.summary.indexOf("\"rc\":3", 300);
		System.out.format("rc 3x %d  ", iof);
		assertTrue((iof > 366 && iof < 900), "rc:3x not found (" + String.valueOf(iof) + ")");

		iof = stats.summary.indexOf("\"rc\":8", 600);
		System.out.format("rc 8x %d  \n", iof);
		assertTrue((iof > 1200 && iof < 1750), "rc:8x not found (" + String.valueOf(iof) + ")");

	}

	@Test(timeOut = 15000, threadPoolSize = 5, invocationCount = 1, successPercentage = 99)
	public void longQueueTest1() {
		longQueueTest(true);
	}

	@Test(timeOut = 15000, threadPoolSize = 10, invocationCount = 20, successPercentage = 95)
	public void longQueueTest2() {
		longQueueTest(false);
	}

	private IPMessage[] shortMessageList() {
		List<String> args = new ArrayList<String>(3);
		args.add("foo");
		args.add("bar");
		args.add("bazzzz");

		IPMessage messages[] = { new IPMessage("Mares eat oats"),
				new IPMessage("Does eat oats", ((Object) "Bucks they dotes"), 1),
				new IPMessage("Little lambs eat ivy", new OSCMessage("/messageUno"), 100),
				new IPMessage("A kid will eat ivy too", new OSCMessage("/messageDos", args), 12345),
				new IPMessage("TAIL", null, 99999) };

		return messages;
	}

	private IPMessage[] longMessageList(int max) {

		IPMessage messages[] = new IPMessage[max + 2];

		messages[0] = new IPMessage("HEAD");

		int i = 0;
		for (i = 0; i < max; i++) {
			messages[i] = new IPMessage(Base64.encodeBase64String(String.valueOf(i).getBytes()), null, i);
		}
		messages[i] = new IPMessage("TAIL", null, 99999);

		return messages;
	}

}
