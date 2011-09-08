package info.jupiter.util;

public class LinkedNodeList<T> {
	private Node last;
	private int size;

	public void add(T t) {
		Node toAdd = new Node(t, last, null);
		if (last != null)
			last.next = toAdd;
		last = toAdd;
		size++;
	}

	public void clear() {
		size = 0;
		last = null;
	}

	public boolean contains(T t) {
		return get(t) != null;
	}

	public boolean isEmpty() {
		return last == null;
	}

	public void remove(T t) {
		Node toRemove = get(t);
		if (toRemove != null) {
			if (toRemove.previous != null)
				toRemove.previous.next = toRemove.next;
			if (toRemove.next != null)
				toRemove.next.previous = toRemove.previous;
			size--;
		}
	}

	public int size() {
		return size;
	}

	private Node get(T t) {
		Node last = this.last;
		while (last != null) {
			if (last.t == t)
				return last;
			last = last.previous;
		}
		return null;
	}

	private class Node {
		private Node next;
		private Node previous;
		private T t;

		public Node(T t, Node previous, Node next) {
			this.t = t;
			this.previous = previous;
			this.next = next;
		}
	}
}
