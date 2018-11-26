package race_constraints;

import model.Participant;
import model.ParticipantSpeed;

public class DecelerationConstraint implements ParticipantConstraint {

	private double myDeceleration;
	private double myCompoundingVelocity;
	
	public DecelerationConstraint(double theDeceleration, double theStartingVelocity) {
		myDeceleration = theDeceleration;
		myCompoundingVelocity = theStartingVelocity;
	}
	
	@Override
	public double applyConstraint(double velocity, ParticipantSpeed theSpeed) {
		myCompoundingVelocity -= myDeceleration;
		double ratio = myCompoundingVelocity / velocity;
		return velocity * ratio;
	}
	
	public double getDeceleration() {
		return myDeceleration;
	}
}
