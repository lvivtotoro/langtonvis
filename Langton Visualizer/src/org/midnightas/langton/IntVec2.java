package org.midnightas.langton;

public class IntVec2 {

	public int x, y;

	public IntVec2(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public IntVec2(IntVec2 other) {
		this.x = other.x;
		this.y = other.y;
	}

	public IntVec2 copy() {
		return new IntVec2(x, y);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IntVec2 other = (IntVec2) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("IntVec2[x=%s, y=%s]", x, y);
	}

}
