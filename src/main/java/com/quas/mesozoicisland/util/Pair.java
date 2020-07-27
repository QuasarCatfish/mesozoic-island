package com.quas.mesozoicisland.util;

public class Pair<T extends Comparable<T>, V extends Comparable<V>> implements Comparable<Pair<T, V>>, Cloneable {

	private T t;
	private V v;
	
	public Pair(T t, V v) {
		this.t = t;
		this.v = v;
	}

	public T getFirstValue() {
		return t;
	}
	
	public V getSecondValue() {
		return v;
	}
	
	@Override
	public int compareTo(Pair<T, V> that) {
		if (this.t.compareTo(that.t) != 0) return this.t.compareTo(that.t);
		if (this.v.compareTo(that.v) != 0) return this.v.compareTo(that.v);
		return 0;
	}
	
	@Override
	public boolean equals(Object that) {
		return that instanceof Pair<?, ?> && this.equals((Pair<?, ?>)that);
	}
	
	public boolean equals(Pair<?, ?> that) {
		return this.t.equals(that.t) && this.v.equals(that.v);
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new Pair<T, V>(this.t, this.v);
	}
	
	@Override
	public String toString() {
		return String.format("[%s, %s]", t.toString(), v.toString());
	}
}
