package org.chaseoaks.xair_proxy.data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.greynite.commons.collections.CircularLIFOArrayList;

import com.illposed.osc.OSCMessage;

/**
 * Buffer to store meter level results from the XAir. A custom Jackson JSON
 * serializer ( {@link LevelBufferSerializer} ) is used to return the results,
 * most recent first.
 * <p>
 * The internal buffer has a fixed size, and will loop discarding the oldest
 * results if results are not polled often enough.
 * <p>
 * {@link ByteBundle} is used as a tag class for the JSON serializer (Jackson
 * ObjectMapper) to force blob results to come out as a array instead of Base64,
 * which should greatly simpify the front end UI. See
 * {@link ByteArraySerializer} and
 * {@link org.chaseoaks.xair_proxy.data.Base#getMapper}.
 * 
 * @author scollenburg
 *
 */
public class LevelBuffer extends ResponseBuffer {

	// public ByteBundle[] buffer = new ByteBundle[110];
	protected int levelBufferSize = 110;
	public CircularLIFOArrayList<ByteBundle> levelBuffer;
	/**
	 * Object cache, to avoid needless GC thrash
	 */
	private ArrayList<ByteBundle> bbcache;
	private int lastBundle = -1;

	protected Semaphore semaphore = new Semaphore(1);

	public LevelBuffer(OSCMessage message) {
		super(message);

		levelBuffer = new CircularLIFOArrayList<ByteBundle>(levelBufferSize);
		bbcache = new ArrayList<ByteBundle>(levelBufferSize);

		for (int i = 0; i < levelBufferSize; i++) {
			bbcache.add(new ByteBundle());
		}
	}

	public boolean add(byte[] bytes) {

		// boolean permit = false;
		// boolean success = false;
		//
		// try {
		// permit = semaphore.tryAcquire(25, TimeUnit.MILLISECONDS);
		//
		// if (!permit)
		// return false;
		//
		// if (bufTail < 0) {
		// buffer[bufHead].setBundle(bytes);
		// bufTail = bufHead;
		// success = true;
		// } else {
		// if (++bufHead >= bufSize) {
		// bufHead = 0;
		// }
		// buffer[bufHead].setBundle(bytes);
		// if (bufTail == bufHead) {
		// if (++bufTail >= bufSize)
		// bufTail = 0;
		// }
		// success = true;
		// }
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// // nothing, really
		// success = false;
		// } finally {
		// if (permit) {
		// semaphore.release();
		// }
		// }
		// return success;

		if (++lastBundle >= levelBufferSize)
			lastBundle = 0;

		synchronized (levelBuffer) {
			ByteBundle bundle = bbcache.get(lastBundle);
			bundle.bundle = bytes;
			levelBuffer.add(bundle);
			return true;
		}

	}

	public void clear() {
		synchronized (levelBuffer) {
			levelBuffer.clear();
			lastBundle = -1;
		}
	}

	// public ByteBundle[] getRecent() {
	// ByteBundle[] recent = null;
	// if (bufTail < 0) {
	// return new ByteBundle[0];
	// }
	//
	// boolean permit = false;
	//
	// try {
	// permit = semaphore.tryAcquire(25, TimeUnit.MILLISECONDS);
	// if (!permit)
	// return new ByteBundle[0];
	//
	// // 30 - 31 = -1; +1 + Size = 110
	// // 30 - 0 = 30
	// int len = bufHead + 1 - bufTail;
	// if (len <= 0)
	// len += bufSize;
	//
	// recent = new ByteBundle[len];
	// for (int i = 0, p = bufHead; i < len; i++) {
	// recent[i] = buffer[p];
	// if (--p < 0)
	// p = bufSize - 1;
	// }
	//
	// bufTail = -1;
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// // nothing, really
	// return new ByteBundle[0];
	// } finally {
	// if (permit) {
	// semaphore.release();
	// }
	// }
	//
	// return recent;
	// }

}
