package race_constraints;

import model.ParticipantSpeed;

public class DecelerationConstraint implements ParticipantConstraint {

	private double myDeceleration;
	
	public DecelerationConstraint(double theDeceleration) {
		myDeceleration = theDeceleration;
	}
	
	@Override
	public double applyConstraint(double velocity, ParticipantSpeed theSpeed) {
		double decelPercentage = ((velocity - myDeceleration) / velocity);
		return velocity - myDeceleration;
	}
	
	public double getDeceleration() {
		return myDeceleration;
	}
}
