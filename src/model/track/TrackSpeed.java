package model.track;

public enum TrackSpeed {
//	FASTEST,
//	FASTER,
	FAST(1.5, "Fast"), 
	MEDIUM(1.0, "Medium"), 
	SLOW(0.5, "Slow");
//	SLOWER,
//	SLOWEST;

	private double multiplier;
	private String myName;

	TrackSpeed(double theMultiplier, String theName) {
		multiplier = theMultiplier;
		myName = theName;
	}

	public void setMultiplier(double mult) {
		multiplier = mult;
	}

	public double getMultiplier() {
		return multiplier;
	}
	
	@Override
	public String toString() {
		return myName;
	}
}
