package race_constraints;

import model.ParticipantSpeed;
import model.track.TrackSpeed;

/**
 * 
 * @author Peter Bae
 *
 */
public class TrackSectionConstraint implements ParticipantConstraint {
	private TrackSpeed mySpeed;
	
	public TrackSectionConstraint(TrackSpeed speed) {
		mySpeed = speed;
	}
	
	public TrackSpeed getTracketSpeed() {
		return mySpeed;
	}
	
	@Override
	public double applyConstraint(double velocity, ParticipantSpeed theSpeed) {
//		System.out.println("SPEED CHANGED, " + velocity + " before " + velocity * mySpeed.getMultiplier() + " after");
		System.out.println(mySpeed);
		return velocity * mySpeed.getMultiplier();
	}
}
