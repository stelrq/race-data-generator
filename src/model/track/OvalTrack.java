package model.track;

import static java.lang.Math.PI;

import java.util.List;

/**
 *
 *
 * @author Myles Haynes
 * @author Michael Osborne
 * @author Peter Bae
 */
public class OvalTrack extends Track {

//	private List<TrackSectionConstraint> sections;
    private int xRatio;
    private int yRatio;
    private double width;
    private double height;

    // Hard coded speeds for the sections of the track traversing the track
    // counterclockwise starting with first turn, second turn, back straight
    // away,
    // etc...
    private TrackSpeed[] trackSpeeds = new TrackSpeed[6];

    private double frontStraightAway;
    private double firstTurn;
    private double secondTurn;
    private double backStraightAway;
    private double thirdTurn;
    private double fourthTurn;

    public OvalTrack(int distance, int xRatio, int yRatio) {
        super(distance);
        if (yRatio > xRatio) {
            throw new IllegalArgumentException(
                    "width must be greater than height");
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
        // Get the speed then add 1 to it to get the next track speed in the
        // array.
        int desiredIndex = (int) getSpeedIndexAndNextGate(distance)[0] + 1;

        // Make sure the array wraps
        desiredIndex = desiredIndex % trackSpeeds.length;

        return trackSpeeds[desiredIndex];
    }

    // Returns a double array with values: { speedIndex, nextGate }
    // corresponding to
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
        width = (xRatio * trackLength)
                / ((yRatio * Math.PI) + (2 * xRatio) - (2 * yRatio));
        height = (yRatio * trackLength)
                / ((yRatio * Math.PI) + (2 * xRatio) - (2 * yRatio));

        double straightDist = getWidth() - getHeight();
        double curveDistance = (getHeight() * .5) * PI;
        firstTurn = (curveDistance / 2);
        secondTurn = curveDistance;
        backStraightAway = curveDistance + straightDist;
        thirdTurn = backStraightAway + (curveDistance / 2);
        fourthTurn = backStraightAway + curveDistance;
        frontStraightAway = backStraightAway * 2;
    }

    @Override
    public String getTrackName() {
        return "OvalTrack";
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
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

    @Override
    public void setSections(List<TrackSpeed> theSpeeds) {
        Object[] tempArr = theSpeeds.toArray();
        for (int i = 0; i < tempArr.length; i++) {
            trackSpeeds[i] = (TrackSpeed) tempArr[i];
        }
    }

}
