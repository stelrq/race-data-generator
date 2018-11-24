package model.track;

import model.RaceConstraint;

/**
 * 
 * @author Peter Bae
 *
 */
public class TrackSection implements RaceConstraint {
	private TrackSpeed mySpeed;
	private double myLength;
	
	public TrackSection(TrackSpeed speed, double length) {
		mySpeed = speed;
		myLength = length;
	}
	
	public TrackSpeed getTracketSpeed() {
		return mySpeed;
	}
	
	public double getLength() {
		return myLength;
	}

	@Override
	public double applyContraint(double velocity) {
		return velocity * mySpeed.getMultiplier();
	}
}
