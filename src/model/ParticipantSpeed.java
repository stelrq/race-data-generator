package model;

public enum ParticipantSpeed {
	FASTEST("Fastest"),
	FASTER("Faster"),
	FAST("Fast"),
	MEDIUM("Medium"),
	SLOW("Slow"),
	SLOWER("Slower"),
	SLOWEST("Slowest");
	
	private double myVelocity;
	private double myRange;
	private String myName;
	
	ParticipantSpeed(String name) {
		myVelocity = 100;
		myRange = 100;
		myName = name;
	}
	
	public void setVelocity(double velocity) {
		myVelocity = velocity;
	}
	
	public void setRange(double range) {
		myRange = range;
	}
	
	public double getVelocity() {
		return myVelocity;
	}
	
	public double getRange() {
		return myRange;
	}
	
	public double getNewVelocity() {
		return Math.random() * myRange * 2 + (myVelocity - myRange);
	}
	
	@Override
	public String toString() {
		return myName;
	}
}
