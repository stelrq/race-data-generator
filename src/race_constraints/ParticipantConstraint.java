package race_constraints;

import model.ParticipantSpeed;

public interface ParticipantConstraint {
	public double applyConstraint(double velocity, ParticipantSpeed theSpeed);
}
