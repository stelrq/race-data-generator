package view;

import model.track.Track;

/**
 * 
 * @author Myles Haynes
 */
public abstract class TrackController {

	public abstract Track getTrack();
	
	public abstract int getLength();
}
