package race_constraints;

import model.Participant;
import model.ParticipantSpeed;

public class AccelerationConstraint implements ParticipantConstraint {

	private double myAcceleration;
	private double myCompoundingVelocity;
	
	public AccelerationConstraint(double theAcceleration, double theStartingVelocity) {
		myAcceleration = theAcceleration;
		myCompoundingVelocity = theStartingVelocity;
	}
	
	@Override
	public double applyConstraint(double velocity, ParticipantSpeed theSpeed) {
		myCompoundingVelocity += myAcceleration;
		double ratio = myCompoundingVelocity / velocity;
		return velocity * ratio;
	}
	
	public double getAcceleration() {
		return myAcceleration;
	}

}
