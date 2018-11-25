package model;

public enum ParticipantSpeed {
//	FASTEST("Fastest"),
//	FASTER("Faster"),
	FAST("Fast", 11, 0.1),
	MEDIUM("Medium", 10, 0.1),
	SLOW("Slow", 9, 0.1);
//	SLOWER("Slower"),
//	SLOWEST("Slowest");
	
	private double myVelocity;
	private double myRange;
	private String myName;
	
	ParticipantSpeed(String theName, double theVelocity, double theRange) {
		myVelocity = theVelocity;
		myRange = theRange;
		myName = theName;
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
