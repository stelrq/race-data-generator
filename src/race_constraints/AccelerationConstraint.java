package race_constraints;

import model.ParticipantSpeed;

public class AccelerationConstraint implements ParticipantConstraint {

	private double myAcceleration;
	
	public AccelerationConstraint(double theAcceleration) {
		myAcceleration = theAcceleration;
	}
	
	@Override
	public double applyConstraint(double velocity, ParticipantSpeed theSpeed) {
		double accelPercentage = ((velocity + myAcceleration) / velocity);
		return velocity + myAcceleration;
	}
	
	public double getAcceleration() {
		return myAcceleration;
	}

}
