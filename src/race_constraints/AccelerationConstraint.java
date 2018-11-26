package race_constraints;

import model.Participant;
import model.ParticipantSpeed;

public class AccelerationConstraint implements ParticipantConstraint {

	private double myAcceleration;
	private double myCompoundingVelocity;
	private double percentage;
	private double mySpeedDifference;
	private int constraintCounter;
	
	public AccelerationConstraint(double theAcceleration, double theStartingVelocity, double speedDifference, double distanceStartedAccelerating) {
		myAcceleration = theAcceleration;
		myCompoundingVelocity = theStartingVelocity;
		mySpeedDifference = speedDifference;
		percentage = 0;
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
