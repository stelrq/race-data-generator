package model;

public enum ParticipantSpeed {
//	FASTEST("Fastest"),
//	FASTER("Faster"),
	FAST("Fast", 15),
	MEDIUM("Medium", 10),
	SLOW("Slow", 5);
//	SLOWER("Slower"),
//	SLOWEST("Slowest");
	
	private final String myPrettyString;
	private final double mySpeed;
	
	private ParticipantSpeed(final String thePrettyString, final double theSpeed) {
		myPrettyString = thePrettyString;
		mySpeed = theSpeed;
	}
	
	public double speed() {
		return mySpeed;
	}
	
	public String prettyString() {
		return myPrettyString;
	}
}
