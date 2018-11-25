package model.track;

public enum TrackSpeed {
//	FASTEST,
//	FASTER,
	FAST(1.5, 0.01),
	MEDIUM(1.0, 0.01),
	SLOW(0.5, 0.01);
//	SLOWER,
//	SLOWEST;
	
	private double multiplier;
	private double variance;
	
	TrackSpeed(double theMultiplier, double theVariance) {
		multiplier = theMultiplier;
		variance = theVariance;
	}
	
	public void setMultiplier(double mult) {
		multiplier = mult;
	}
	
	public void setVariance(double var) {
		variance = var;
	}
	
	public double getMultiplier() {
		return multiplier;
	}
	
	public double getVariance() {
		return variance;
	}
}
