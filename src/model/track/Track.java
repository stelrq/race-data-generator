package model.track;

import java.util.List;

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
	
	public abstract void setSections(List<TrackSpeed> theSpeeds);
	
	public abstract double getWidth();
	
	public abstract double getHeight();
	
	public abstract int getHeightRatio();
	
	public abstract int getWidthRatio();

	public abstract TrackSpeed getTrackSpeed(double distance);
	
	public abstract TrackSpeed getNextTrackSpeed(double distance);
	
	public abstract double getDistanceUntilNextTrackPiece(double distance);

	public abstract String getTrackName();

}
