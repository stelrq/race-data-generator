package model.track;

import model.SpeedClass;
import race_constraints.TrackSectionConstraint;

import static java.lang.Math.PI;
import static model.track.TrackSpeed.SLOW;
import static model.track.TrackSpeed.MEDIUM;
import static model.track.TrackSpeed.FAST;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Myles Haynes
 */
public class OvalTrack extends Track {

//	private List<TrackSectionConstraint> sections;
	private int xRatio;
	private int yRatio;
	private double width;
	private double height;

	// Hard coded speeds for the sections of the track traversing the track
	// counterclockwise starting with first turn, second turn, back straightaway,
	// etc...
	private TrackSpeed[] trackSpeeds = { SLOW, MEDIUM, FAST, SLOW, MEDIUM, FAST };

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
//		sections = new ArrayList<>();
		this.xRatio = xRatio;
		this.yRatio = yRatio;
		buildTrackModel();
	}

	@Override
	public TrackSpeed getTrackSpeed(double d) {
		return trackSpeeds[(int) getSpeedIndexAndNextGate(d)[0]];
	}

	@Override
	public TrackSpeed getNextTrackSpeed(double distance) {
		// Get the speed then add 1 to it to get the next track speed in the array.
		int desiredIndex = (int)getSpeedIndexAndNextGate(distance)[0] + 1;

		// Make sure the array wraps
		desiredIndex = desiredIndex % trackSpeeds.length;

		return trackSpeeds[desiredIndex];
	}

	// Returns a double array with values: { speedIndex, nextGate } corresponding to
	// the given distance around the track.
	private double[] getSpeedIndexAndNextGate(double distance) {
		final double[] returnVals = new double[2];
		if (distance >= 0 && distance < firstTurn) {
			returnVals[0] = 0;
			returnVals[1] = firstTurn;
		} else if (distance < secondTurn) {
			returnVals[0] = 1;
			returnVals[1] = secondTurn;
		} else if (distance < backStraightAway) {
			returnVals[0] = 2;
			returnVals[1] = backStraightAway;
		} else if (distance < thirdTurn) {
			returnVals[0] = 3;
			returnVals[1] = thirdTurn;
		} else if (distance < fourthTurn) {
			returnVals[0] = 4;
			returnVals[1] = fourthTurn;
		} else if (distance < frontStraightAway) {
			returnVals[0] = 5;
			returnVals[1] = frontStraightAway;
		} else {
			// Should never happen
			returnVals[0] = 0;
			returnVals[1] = firstTurn;
		}

		return returnVals;
	}

	@Override
	public double getDistanceUntilNextTrackPiece(double distance) {
		return getSpeedIndexAndNextGate(distance)[1] - distance;
	}

	private void buildTrackModel() {
		width = (xRatio * trackLength) / ((yRatio * Math.PI) + (2 * xRatio) - (2 * yRatio));
		height = (yRatio * trackLength) / ((yRatio * Math.PI) + (2 * xRatio) - (2 * yRatio));
//		sections.add(new TrackSectionConstraint(TrackSpeed.SLOW, height*Math.PI/4));
//		sections.add(new TrackSectionConstraint(TrackSpeed.MEDIUM, height*Math.PI/4));
//		sections.add(new TrackSectionConstraint(TrackSpeed.FAST, width-height));
//		sections.add(new TrackSectionConstraint(TrackSpeed.SLOW, height*Math.PI/4));
//		sections.add(new TrackSectionConstraint(TrackSpeed.MEDIUM, height*Math.PI/4));
//		sections.add(new TrackSectionConstraint(TrackSpeed.FAST, width-height));

		double straightDist = getWidth() - getHeight();
		double curveDistance = (getHeight() * .5) * PI;
		firstTurn = (curveDistance / 2);
		secondTurn = curveDistance;
		backStraightAway = curveDistance + straightDist;
		thirdTurn = backStraightAway + (curveDistance / 2);
		fourthTurn = backStraightAway + curveDistance;
		frontStraightAway = backStraightAway * 2;
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

	@Override
	public int getHeightRatio() {
		return yRatio;
	}

	@Override
	public int getWidthRatio() {
		return xRatio;
	}

}
