package race_constraints;

import model.ParticipantSpeed;
import model.track.TrackSpeed;

/**
 * A TrackSectionConstraint modifies the
 *
 * @author Michael Osborne
 * @author Peter Bae
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
    public double applyConstraint(final double velocity, final ParticipantSpeed theSpeed) {
        return velocity * mySpeed.getMultiplier();
    }
}
