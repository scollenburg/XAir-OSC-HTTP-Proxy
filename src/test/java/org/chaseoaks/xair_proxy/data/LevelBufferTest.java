package org.chaseoaks.xair_proxy.data;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.illposed.osc.OSCMessage;

public class LevelBufferTest {

	@Test
	public void testBasic() {
		String result;
		// LevelBuffer lb = new LevelBuffer("/$testBasic");
		LevelBuffer lb = new LevelBuffer(new OSCMessage("/$testBasic"));

		// Empty buffer
		lb.timeStampMillis = 1;
		result = lb.toJson();
		// assertEquals(actual, expected);
		assertEquals(result, "{\"command\":\"/$testBasic\",\"levelBuffer\":[]}");

		// Three entries. Test most recent comes out first
		lb.add(new byte[] { 0x01 });
		lb.add(new byte[] { 0x02, 0x02 });
		lb.add(new byte[] { 0x03, 0x03, 0x03 });

		result = lb.toJson();
		assertEquals(result, "{\"command\":\"/$testBasic\",\"levelBuffer\":[[3,3,3],[2,2],[1]]}");

		// Over-fill buffer, test rollover
		for (int i = 0; i < 115; i++) {
			lb.add(new byte[] { (byte) i });
		}

		result = lb.toJson();
		assertEquals(result,
				"{\"command\":\"/$testBasic\",\"levelBuffer\":[[114],[113],[112],[111],[110],[109],[108],[107],[106],[105],[104],[103],[102],[101],[100],[99],[98],[97],[96],[95],[94],[93],[92],[91],[90],[89],[88],[87],[86],[85],[84],[83],[82],[81],[80],[79],[78],[77],[76],[75],[74],[73],[72],[71],[70],[69],[68],[67],[66],[65],[64],[63],[62],[61],[60],[59],[58],[57],[56],[55],[54],[53],[52],[51],[50],[49],[48],[47],[46],[45],[44],[43],[42],[41],[40],[39],[38],[37],[36],[35],[34],[33],[32],[31],[30],[29],[28],[27],[26],[25],[24],[23],[22],[21],[20],[19],[18],[17],[16],[15],[14],[13],[12],[11],[10],[9],[8],[7],[6],[5]]}");
	}

	// class BaseFrame extends org.chaseoaks.xair_proxy.data.Base {
	// public String hello = "Hello!";
	// public int two = 2;
	// public float threeQuarter = 0.75f;
	// public Map<String, Object> recent;
	// private byte[] levels = { 0x01, 0x02, 0x03, (byte) 0xff };
	//
	// public BaseFrame() {
	// this.recent = new HashMap<String, Object>();
	// ByteBundle bundle = new ByteBundle(this.levels);
	// this.recent.put("/$bundle", bundle);
	// }
	// }

	class LBFrame extends org.chaseoaks.xair_proxy.data.LevelBuffer implements Runnable {

		public int runExpected = 0;

		public LBFrame(OSCMessage message) {
			super(message);
		}

		public int tryAcquire() {
			boolean permit = false;
			int result = -2;

			try {
				permit = semaphore.tryAcquire(25, TimeUnit.MILLISECONDS);
				if (permit)
					result = 1;
				else
					result = 0;
			} catch (InterruptedException e) {
				result = -1;
			}
			return result;
		}

		public int release() {
			int result = -2;
			semaphore.release();
			result = 1;
			return result;
		}

		@Override
		public void run() {
			int runResult = -100;
			runResult = this.tryAcquire();
			assertEquals(runResult, runExpected);
		}
	}

	/* Test basic semaphore */
	@Test
	public void testSemaphore1() {

		int result = -100;

		LBFrame lbf = new LBFrame(new OSCMessage("/$testSemaphore1"));

		result = lbf.tryAcquire();
		assertEquals(result, 1);
		result = lbf.tryAcquire();
		assertEquals(result, 0);

		ExecutorService executor = Executors.newFixedThreadPool(1);
		lbf.runExpected = 0;
		executor.execute(lbf);

		try {
			TimeUnit.MILLISECONDS.sleep(300);
		} catch (InterruptedException e) {
			// noop
			// e.printStackTrace();
		}

		result = lbf.tryAcquire();
		assertEquals(result, 0);

		lbf.release();
		result = lbf.tryAcquire();
		assertEquals(result, 1);
		lbf.release();

		// Should be released
		// Acquire on the other thread
		lbf.runExpected = 1;
		executor.execute(lbf);
		try {
			TimeUnit.MILLISECONDS.sleep(300);
		} catch (InterruptedException e) {
			// noop
			// e.printStackTrace();
		}

		result = lbf.tryAcquire();
		assertEquals(result, 0);

		lbf.release();
		result = lbf.tryAcquire();
		assertEquals(result, 1);
		lbf.release();

	}

	/* Test semaphore checks */
	@Test
	public void testSemaphore2() {

		String result = null;

		LBFrame lbf = new LBFrame(new OSCMessage("/$testSemaphore2"));

		lbf.add(new byte[] { 0x01 });
		lbf.tryAcquire();
		lbf.add(new byte[] { 0x02 });
		lbf.release();

		result = lbf.toJson();
		assertEquals(result, "{\"command\":\"/$testSemaphore2\",\"levelBuffer\":[[1]]}");

		lbf.add(new byte[] { 0x03 });

		lbf.tryAcquire();
		result = lbf.toJson();
		assertEquals(result, "{\"command\":\"/$testSemaphore2\",\"levelBuffer\":[]}");
		lbf.release();

		lbf.add(new byte[] { 0x04 });

		result = lbf.toJson();
		assertEquals(result, "{\"command\":\"/$testSemaphore2\",\"levelBuffer\":[[4],[3]]}");

	}
	// // @ T e s t
	// public void testToJson() {
	//
	// BaseFrame frame = new BaseFrame();
	//
	// String result = frame.toJson();
	// assertEquals(result,
	// "{\"hello\":\"Hello!\",\"two\":2,\"threeQuarter\":0.75,\"recent\":{\"/$bundle\":[1,2,3,255]}}");
	// }

}
