package info.jupiter;

import info.Constants;

public abstract class Entity {

	private int index = -1;
	private Location lastKnownRegion;
	private Location location;
	private final UpdateFlags updateFlags = new UpdateFlags();
	
	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
	
	public Entity() {
		setLocation(Constants.DEFAULT_LOCATION);
		this.lastKnownRegion = location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Location getLocation() {
		return location;
	}

	public Location getLastKnownRegion() {
		return lastKnownRegion;
	}
	
	public UpdateFlags getUpdateFlags() {
		return updateFlags;
	}

	public void setLastKnownRegion(Location lastKnownRegion) {
		this.lastKnownRegion = lastKnownRegion;
	}
	
	//
	// For Entity class. When setting location, get region like hyperion.
	// Region r = getRegion(x, y, z);
	//
	//To remove from a region do this
	// if (r != null) {
	//	r.getPlayers().remove(p);
	// }
	//
	// To add to a region do this
	// Region r = getRegion(x, y, z);
	// if (r == null) {
	//  r = setRegion(new Region(), x, y, z);
	// }
	// r.getPlayers().add(p);
	
	
}