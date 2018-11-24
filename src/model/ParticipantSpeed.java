package model;

public enum ParticipantSpeed {
	FASTEST,
	FASTER,
	FAST,
	MEDIUM,
	SLOW,
	SLOWER,
	SLOWEST;
	
	private double myVelocity;
	private double myRange;
	
	ParticipantSpeed() {
		myVelocity = 0;
		myRange = 0;
	}
	
	public void setParameters(double velocity, double range) {
		myVelocity = velocity;
		myRange = range;
	}
	
	public double getVelocity() {
		return myVelocity;
	}
	
	public double getRange() {
		return myRange;
	}
}
