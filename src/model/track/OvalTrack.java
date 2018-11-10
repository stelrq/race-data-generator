package model.track;

import model.SpeedClass;

import static java.lang.Math.PI;

/**
 * 
 * @author Myles Haynes
 */
public class OvalTrack extends Track {

	private double frontStraightAway;
	private double firstTurn;
	private double secondTurn;
	private double backStraightAway;
	private double thirdTurn;
	private double fourthTurn;

	public OvalTrack(int distance) {
		super(distance);
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
		double straightDist = getWidth(trackLength) - getHeight(trackLength);
		double curveDistance = (getHeight(trackLength) * .5) * PI;
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
	public static double getWidth(int distance) {
		return distance / (2.0 * (PI + 1));
	}

	public static double getHeight(int distance) {
		return .5 * getWidth(distance);
	}

}
