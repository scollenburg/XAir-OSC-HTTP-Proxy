package org.greynite.commons.collections;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.RandomAccess;

/**
 * An circular ArrayList that by definition element [0] will be the most
 * recently added entry. Element [capacity-1] will be the oldest entry. Unlike
 * {@link ArrayList}, {@link #add(Object)} will never increase the capacity.
 * <ul>
 * <li>{@link #add(int, Object)} is <strong>not supported</strong>.</li>
 * </ul>
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If
 * multiple threads access an <tt>ArrayList</tt> instance concurrently, and at
 * least one of the threads modifies the list structurally, it <i>must</i> be
 * synchronized externally.
 * <p>
 * For now, use the un-optimized Iterator from AbstractList. A more performant
 * Iterator could be patterned after the ArrayList (see private class
 * {@link ArrayList#Itr} )
 * 
 * @author scollenburg
 *
 */
// See also: http://hg.openjdk.java.net/jdk8/jdk8/jdk/file/tip/src/share/classes/java/util/ArrayList.java
// See also: http://hg.openjdk.java.net/jdk8/jdk8/jdk/file/687fd7c7986d/src/share/classes/java/util/AbstractCollection.java

public class CircularLIFOArrayList<T> extends AbstractList<T> implements RandomAccess {

	protected ArrayList<T> arrayList;
	protected int bufHead = 0;
	protected int bufTail = -1;
	protected final int bufSize;

	public CircularLIFOArrayList(int arg0) {
		arrayList = new ArrayList<>(arg0);
		bufSize = arg0;
	}

	@Override
	public boolean add(T e) {
		// this.set(0, e, true);
		boolean result = false;

		if (bufTail < 0) {
			bufHead = bufTail = 0;
			return arrayList.add(e);
		}

		if (++bufHead >= bufSize) {
			bufHead = 0;
		}

		if (arrayList.size() == bufSize) {
			arrayList.set(bufHead, e);
			result = true;
		} else {
			result = arrayList.add(e);
		}

		if (bufTail == bufHead) {
			if (++bufTail >= bufSize)
				bufTail = 0;
		}

		return result;
	}

	@Override
	public T set(int index, T element) {
		return set(index, element, false);
	}

	protected T set(int index, T element, boolean next) {
		int target = bufHead;

		if (index >= bufSize || index < 0)
			throw new IndexOutOfBoundsException();

		if (index >= arrayList.size())
			throw new IndexOutOfBoundsException();

		{
			{
				target = bufHead - index;
				if (target < 0)
					target += bufSize;
			}
		}

		return arrayList.set(target, element);
	}

	/**
	 * <strong>Not supported.</strong> Use {@link #add(Object)} instead.
	 */
	@Override
	@Deprecated
	public void add(int index, T element) {
		throw new UnsupportedOperationException("Indexed add(int, Object) not supported. Use add(element)");

	}

	/**
	 * <strong>Not supported.</strong> Use {@link #add(Object)} instead.
	 */
	@Override
	@Deprecated
	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException("Method addAll(Collection) not supported. Use add(element)");
		// return false;
	}

	/**
	 * <strong>Not supported.</strong> Use {@link #add(Object)} instead.
	 */
	@Override
	@Deprecated
	public boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException("Method addAll(int, Collection) not supported. Use add(element)");
		// return false;
	}

	@Override
	public void clear() {
		bufTail = -1;
		bufHead = 0;
	}

	@Override
	public boolean contains(Object o) {
		return arrayList.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return arrayList.containsAll(c);
	}

	@Override
	public T get(int index) {
		if (bufTail < 0)
			return null;
		if (index >= this.size())
			return null;

		if (index < 0)
			throw new IndexOutOfBoundsException();
		if (index >= bufSize)
			throw new IndexOutOfBoundsException("Request exceeds capacity: " + String.valueOf(bufSize));

		int i = bufHead - index;
		if (i < 0)
			i += bufSize;

		return arrayList.get(i);
	}

	@Override
	public int indexOf(Object o) {
		int i = arrayList.indexOf(o);
		if (i < 0)
			return i;

		i = bufHead - i;
		if (i < 0)
			i += bufSize;

		return i;
	}

	@Override
	public boolean isEmpty() {
		if (bufTail < 0)
			return true;
		return false;
	}

	@Override
	public boolean remove(Object o) {
		// Not supported at this time
		throw new UnsupportedOperationException();
		// TODO Auto-generated method stub
		// return false;
	}

	@Override
	public T remove(int index) {
		// Not supported at this time
		throw new UnsupportedOperationException();
		// TODO Auto-generated method stub
		// return null;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// Not supported at this time
		throw new UnsupportedOperationException();
		// TODO Auto-generated method stub
		// return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// Not supported at this time
		throw new UnsupportedOperationException();
		// TODO Auto-generated method stub
		// return false;
	}

	@Override
	public int size() {
		if (bufTail < 0)
			return 0;

		// e.g. bufHead = 10, bufTail == 90, bufSize = 100;
		// ( 10 + 100 + 1 ) - 90 == 21
		if (bufHead < bufTail)
			return (bufHead + bufSize + 1) - bufTail;

		// e.g. bufHead == 87, bufTail == 23
		// 87 - 23 = 64 + 1 == 65
		return (bufHead + 1) - bufTail;
	}

	@Override
	public Object[] toArray() {
		Object[] elementData = arrayList.toArray();
		// System.arraycopy(elementData, 0, a, 0, size);
		return Arrays.copyOf(elementData, this.size());
	}

	@SuppressWarnings("hiding")
	@Override
	public <T> T[] toArray(T[] a) {
		T[] elementData = arrayList.toArray(a);
		// Object[] elementData = arrayList.toArray();
		// return Arrays.copyOf(elementData, this.size() );
		return elementData;
	}

}
