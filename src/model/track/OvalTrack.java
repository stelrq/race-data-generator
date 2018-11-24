package model.track;

import model.SpeedClass;

import static java.lang.Math.PI;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Myles Haynes
 */
public class OvalTrack extends Track {
	
	private List<TrackSection> sections;
	private int xRatio;
	private int yRatio;
	private double width;
	private double height;

	private double frontStraightAway;
	private double firstTurn;
	private double secondTurn;
	private double backStraightAway;
	private double thirdTurn;
	private double fourthTurn;

	public OvalTrack(int distance, int xRatio, int yRatio) {
		super(distance);
		if (yRatio > xRatio) {
			throw new IllegalArgumentException("width must be greater than height");
		}
		sections = new ArrayList<>();
		this.xRatio = xRatio;
		this.yRatio = yRatio;
		buildTrackMap();
	}

	@Override
	public SpeedClass getSpeedClass(double d) {
		if (d >= 0 && d < firstTurn) {
			return SpeedClass.MEDIUM_SLOW;
		} else if (d < secondTurn) {
			return SpeedClass.MEDIUM_FAST;
		} else if (d < frontStraightAway) {
			return SpeedClass.FAST;
		} else if (d < thirdTurn) {
			return SpeedClass.MEDIUM_SLOW;
		} else if (d < fourthTurn) {
			return SpeedClass.MEDIUM_FAST;
		} else if (d < backStraightAway) {
			return SpeedClass.FAST;
		} else {
			return SpeedClass.FAST;
		}
	}

	private void buildTrackMap() {
		width = (xRatio*trackLength) / ((yRatio*Math.PI)+(2*xRatio)-(2*yRatio));
		height = (yRatio*trackLength) / ((yRatio*Math.PI)+(2*xRatio)-(2*yRatio));
		sections.add(new TrackSection(TrackSpeed.SLOW, height*Math.PI/4));
		sections.add(new TrackSection(TrackSpeed.MEDIUM, height*Math.PI/4));
		sections.add(new TrackSection(TrackSpeed.FAST, width-height));
		sections.add(new TrackSection(TrackSpeed.SLOW, height*Math.PI/4));
		sections.add(new TrackSection(TrackSpeed.MEDIUM, height*Math.PI/4));
		sections.add(new TrackSection(TrackSpeed.FAST, width-height));
		
		double straightDist = getWidth() - getHeight();
		double curveDistance = (getHeight() * .5) * PI;
		firstTurn = (curveDistance / 2);
		secondTurn = curveDistance;
		frontStraightAway = curveDistance + straightDist;
		thirdTurn = backStraightAway + (curveDistance / 2);
		fourthTurn = backStraightAway + curveDistance;
		backStraightAway = fourthTurn + straightDist;
	}

	public String getTrackName() {
		return "OvalTrack";
	}

	/**
	 * Curve is 25% of width, straight away is 50% Curves are perfect half circles
	 *
	 * Distance = d w = straight away distance
	 *
	 * d = 2PI * w + 2 w d / 2 = PI * w + w d /2 = w (PI + 1) d / ( 2 * (PI + 1) )
	 *
	 *
	 */
	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

}
