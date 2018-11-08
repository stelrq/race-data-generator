package model.track;

import model.SpeedClass;
import model.Track;

import static java.lang.Math.PI;

/**
 * 
 * @author Myles Haynes
 */
public class OvalTrack extends Track {


  private double firstStraightAway;
  private double firstFirstHalfCurve;
  private double firstSecondHalfCurve;
  private double secondStraightAway;
  private double secondFirstHalfCurve;
  private double secondSecondHalfCurve;

  public OvalTrack(int distance) {
    super(distance);
    buildTrackMap();
  }

  @Override
  public SpeedClass getSpeedClass(double d) {
    if(d >= 0 && d < firstFirstHalfCurve) {
      return SpeedClass.MEDIUM_SLOW;
    } else if(d < firstSecondHalfCurve) {
      return SpeedClass.MEDIUM_FAST;
    } else if(d < firstStraightAway) {
      return SpeedClass.FAST;
    } else if(d < secondFirstHalfCurve) {
      return SpeedClass.MEDIUM_SLOW;
    } else if(d < secondSecondHalfCurve) {
      return SpeedClass.MEDIUM_FAST;
    } else if(d < secondStraightAway) {
      return SpeedClass.FAST;
    } else {
      return SpeedClass.FAST;
    }
  }

  private void buildTrackMap() {
    double straightDist = getWidth(trackLength) - getHeight(trackLength);
    double curveDistance = (getHeight(trackLength) * .5) * PI;
    firstFirstHalfCurve = (curveDistance / 2);
    firstSecondHalfCurve = curveDistance;
    firstStraightAway = curveDistance + straightDist;
    secondFirstHalfCurve = secondStraightAway + (curveDistance / 2);
    secondSecondHalfCurve = secondStraightAway + curveDistance;
    secondStraightAway = secondSecondHalfCurve + straightDist;
  }

  public String getTrackName() {
    return "OvalTrack";
  }


  /**
   * Curve is 25% of width, straight away is 50%
   * Curves are perfect half circles
   *
   * Distance = d
   * w = straight away distance
   *
   * d = 2PI * w + 2 w
   * d / 2 = PI * w + w
   * d /2 = w (PI + 1)
   * d / ( 2 * (PI + 1) )
   *
   *
   */
  public static double getWidth(int distance) {
    return distance / (2.0 *(PI + 1));
  }

  public static double getHeight(int distance) {
    return .5 * getWidth(distance);
  }


}
