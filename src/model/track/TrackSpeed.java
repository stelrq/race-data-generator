package model.track;

public enum TrackSpeed {
	FASTEST,
	FASTER,
	FAST,
	MEDIUM,
	SLOW,
	SLOWER,
	SLOWEST;
	
	private double multiplier;
	
	TrackSpeed() {
		multiplier = 1;
	}
	
	public void setMultiplier(double mult) {
		multiplier = mult;
	}
	
	public double getMultiplier() {
		return multiplier;
	}
}
