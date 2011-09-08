package info.jupiter;

public class Location {

	private int x;
	private int y;
	private int z;

	public Location(int absX, int absY, int absZ) {
		this.x = absX;
		this.y = absY;
		this.z = absZ;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public int getLocalX() {
		return getLocalX(this);
	}

	public int getLocalY() {
		return getLocalY(this);
	}

	public int getLocalX(Location l) {
		return x - 8 * l.getRegionX();
	}

	public int getLocalY(Location l) {
		return y - 8 * l.getRegionY();
	}

	public int getRegionX() {
		return (x >> 3) - 6;
	}

	public int getRegionY() {
		return (y >> 3) - 6;
	}

	public boolean isWithinDistance(Location other) {
		if (z != other.z) {
			return false;
		}
		int deltaX = other.x - x, deltaY = other.y - y;
		return deltaX <= 14 && deltaX >= -15 && deltaY <= 14 && deltaY >= -15;
	}

	public void transform(int absX, int absY) {
		this.x = absX;
		this.y = absY;
	}

	public void transform(int absX, int absY, int absZ) {
		this.x = absX;
		this.y = absY;
		this.z = absZ;
	}

	@Override
	public String toString() {
		return "[" + x + "," + y + "," + z + "]";
	}
}