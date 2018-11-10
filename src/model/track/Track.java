package model.track;

import model.SpeedClass;

/**
 * 
 * @author Myles Haynes
 */
public abstract class Track {

	protected int trackLength;

	public Track(int trackLength) {
		this.trackLength = trackLength;
	}

	public int getTrackLength() {
		return trackLength;
	}

	public abstract SpeedClass getSpeedClass(double distance);

	public abstract String getTrackName();

}
