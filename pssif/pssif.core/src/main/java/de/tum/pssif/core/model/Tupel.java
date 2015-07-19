package de.tum.pssif.core.model;

public class Tupel {

	private String first;
	private String second;

	public Tupel(String first, String second) {
		this.setFirst(first);
		this.setSecond(second);
	}

	public String getFirst() {
		return first;
	}

	public void setFirst(String first) {
		this.first = first;
	}

	public String getSecond() {
		return second;
	}

	public void setSecond(String second) {
		this.second = second;
	}
	
	public boolean contains(String searchString) {
		return first.equals(searchString) || second.equals(searchString);
	}
	
	public String toString() {
		return "[" + first + ", " + second + "]";
	}
}
