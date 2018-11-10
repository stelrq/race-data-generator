
package view;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

/**
 * 
 * @author Michael Osborne
 */
public class VisibleRaceTrack extends RoundRectangle2D.Double {

	/** The serialization ID. */
	private static final long serialVersionUID = 7813589334818900395L;

	/** The amount of sides a rectangle has. */
	private static final int RECT_SIDES = 4;

	/** The amount of points on the straight track sections. */
	private double myPointsOnStraights;

	/** The amount of points on the turns. */
	private double myPointsOnTurns;

	/** The curve representing the first turn. */
	private CubicCurve2D.Double myFirstTurn;

	/** The curve representing the second turn. */
	private CubicCurve2D.Double mySecondTurn;

	/** The curve representing the third turn. */
	private CubicCurve2D.Double myThirdTurn;

	/** The curve representing the fourth turn. */
	private CubicCurve2D.Double myFourthTurn;

	/** The line representing the top straight. */
	private Line2D.Double myTopStraight;

	/** The line representing the bottom straight. */
	private Line2D.Double myBottomStraight;

	/** A PathIterator to traverse this shape. */
	private PathIterator myPathIterator;

	/** The last point, used with the PathIterator. */
	private Point myLastPoint;

	/** The length of the track (number of subdivisions). */
	private final int myLength;

	/**
	 * Creates a new RaceTrackRoundRect at the given (x, y) coordinate with the
	 * given width and height and the given length. The length is how many sections
	 * the track is divided into or how many points are on the track for use with
	 * the getPointAtDistance method.
	 * 
	 * @param theX      The X coordinate.
	 * @param theY      The Y coordinate.
	 * @param theWidth  The width.
	 * @param theHeight The height.
	 * @param theLength The length of the race track.
	 */
	public VisibleRaceTrack(final int theX, final int theY, final int theWidth, final int theHeight,
			final int theLength) {
		// Using height as the Arc width and the Arc height makes a nice conventional
		// race
		// track shape as long as height > width.
		super(theX, theY, theWidth, theHeight, theHeight, theHeight);

		myLength = theLength;

		if (theWidth < theHeight) {
			throw new IllegalArgumentException("Racetracks width must be longer than height");
		}

		calculateCurvesAndStraights();
		calculateDivisions();
	}

	/**
	 * Returns the track length.
	 * 
	 * @return The length of the track.
	 */
	public int getTrackLength() {
		return myLength;
	}

	/**
	 * Calculates how many divisions should be on certain parts of the track
	 * (straight sections/ curved sections) based on the length.
	 */
	private void calculateDivisions() {
		// Calculate how much horizontal percentage (of the whole width) the
		// straight sections take up given that the widths of the curves on
		// the left and right sides is equal to height.
		double straightToTurnPercentage = (double) (getWidth() - getHeight()) / getWidth();

		// Adjust the percentage to make sure points are evenly spaced.

//        final double almostCircular = 0.3;
//        final double roundExtreme = 0.1;
//
//        final double smallAdjustment = 0.01;
//        final double mediumAdjustment = 0.05;
//        final double largeAdjustment = 0.1;
//
//        if (straightToTurnPercentage > almostCircular) {
//            straightToTurnPercentage -= largeAdjustment;
//        } else if (straightToTurnPercentage > roundExtreme) {
//            straightToTurnPercentage -= mediumAdjustment;
//        } else if (straightToTurnPercentage > 0) {
//            straightToTurnPercentage -= smallAdjustment;
//        }

		// Use the percentage to figure out how many subdivisions (or points) should
		// fall
		// on the straights, and how many should fall on the turns.
		myPointsOnTurns = (myLength - myLength * straightToTurnPercentage) / RECT_SIDES;
		myPointsOnStraights = (myLength * straightToTurnPercentage) / 2;

	}

	/**
	 * Splits the track into four Curve2D turns and two Line2D straight sections to
	 * use to traverse the track.
	 */
	private void calculateCurvesAndStraights() {
		// A PathIterator to use to parameterize the curves.
		myPathIterator = this.getPathIterator(null);

		myLastPoint = new Point(0, 0);

		// A simple calculation to calculate the straight pieces of track, notice how
		// the top straight goes from right to left and the bottom straight goes from
		// left to right because the track is traversed clockwise.
		myTopStraight = new Line2D.Double(getX() + getWidth() - getHeight() / 2, getY(), getX() + getHeight() / 2,
				getY());
		myBottomStraight = new Line2D.Double(getX() + getHeight() / 2, getY() + getHeight(),
				getX() + getWidth() - getHeight() / 2, getY() + getHeight());

		// Calculate and store the curves
		mySecondTurn = advanceIteratorToNextCubicCurve();
		myThirdTurn = advanceIteratorToNextCubicCurve();
		myFourthTurn = advanceIteratorToNextCubicCurve();
		myFirstTurn = advanceIteratorToNextCubicCurve();
	}

	/**
	 * Advances the PathIterator to the next curved corner of this RoundedRectangle.
	 * 
	 * @return The CubicCurve2D.Double representing the curved corner
	 */
	private CubicCurve2D.Double advanceIteratorToNextCubicCurve() {
		myPathIterator.next();
		final int numberOfCoords = 6;
		final int ctrlpt1Index = 0;
		final int ctrlpt2Index = 2;
		final int x2Index = 4;

		double[] coordArray = new double[numberOfCoords];
		while (myPathIterator.currentSegment(coordArray) != PathIterator.SEG_CUBICTO) {
			myPathIterator.next();
			myLastPoint.setLocation(coordArray[0], coordArray[1]);
			coordArray = new double[numberOfCoords];
		}

		return new CubicCurve2D.Double(myLastPoint.getX(), myLastPoint.getY(), coordArray[ctrlpt1Index],
				coordArray[ctrlpt1Index + 1], coordArray[ctrlpt2Index], coordArray[ctrlpt2Index + 1],
				coordArray[x2Index], coordArray[x2Index + 1]);
	}

	/**
	 * Returns a Point2D.Double at the given distance on the race track. Note that
	 * the 0 distance is at the upper left of the race track, before the first left
	 * turn.
	 * 
	 * @param theDistance The distance around the race track. Must be between 0 and
	 *                    theLength inclusive.
	 * @return The Point2D.Double at the given distance around the race track.
	 */
	public Point2D.Double getPointAtDistance(final double theDistance) {
		double correctedDistance = theDistance;
		while (correctedDistance < 0) {
			correctedDistance += myLength;
		}
		while (correctedDistance > myLength) {
			correctedDistance -= myLength;
		}

		final double firstTurnDistance = myPointsOnTurns;
		final double secondTurnDistance = myPointsOnTurns * 2;
		final double firstStraightDistance = secondTurnDistance + myPointsOnStraights;
		final double thirdTurnDistance = myPointsOnTurns * 3 + myPointsOnStraights;
		final double fourthTurnDistance = myPointsOnTurns * 4 + myPointsOnStraights;
		final double secondStraightDistance = fourthTurnDistance + myPointsOnStraights;

		Shape specificShape = new Line2D.Double(0, 0, 0, 0);
		double t = 0.0;

		if (correctedDistance < firstTurnDistance) {
			// Lands on first turn
			t = correctedDistance / firstTurnDistance;
			specificShape = myFirstTurn;
		} else if (correctedDistance < secondTurnDistance) {
			// Lands on second turn
			t = (correctedDistance - firstTurnDistance) / myPointsOnTurns;
			specificShape = mySecondTurn;
		} else if (correctedDistance < firstStraightDistance) {
			// Lands on bottom straight
			t = (correctedDistance - secondTurnDistance) / myPointsOnStraights;
			specificShape = myBottomStraight;
		} else if (correctedDistance < thirdTurnDistance) {
			// Lands on third turn
			t = (correctedDistance - firstStraightDistance) / myPointsOnTurns;
			specificShape = myThirdTurn;
		} else if (correctedDistance < fourthTurnDistance) {
			// Lands on fourth turn
			t = (correctedDistance - thirdTurnDistance) / myPointsOnTurns;
			specificShape = myFourthTurn;
		} else if (correctedDistance < secondStraightDistance) {
			// Lands on top straight
			t = (correctedDistance - fourthTurnDistance) / myPointsOnStraights;
			specificShape = myTopStraight;
		}

		return parametricSolver(specificShape, t);

	}

	/**
	 * Solves the given Shape (as long as its a Line2D or a CubicCurve2D) for the
	 * point at the given parameter.
	 * 
	 * @param theShape  The Shape to solve, must be a Line2D or a CubicCurve2D.
	 * @param theTValue The parameter (between 0 and 1 inclusive)
	 * @return The Point2D.Double on theShape at theTValue.
	 */
	private Point2D.Double parametricSolver(final Shape theShape, final double theTValue) {
		final Point2D.Double returnPoint = new Point2D.Double(0, 0);

		if (theShape instanceof Line2D) {
			final Line2D.Double line = (Line2D.Double) theShape;

			// The parametric expression for a line.
			returnPoint.x = line.getX1() + theTValue * (line.getX2() - line.getX1());
			returnPoint.y = line.getY1() + theTValue * (line.getY2() - line.getY1());
		} else {
			final CubicCurve2D.Double curve = (CubicCurve2D.Double) theShape;

			final int cubed = 3;
			final int squared = 2;

			// The parametric expression for a cubic Bezier curve.
			returnPoint.x = Math.pow(1 - theTValue, cubed) * curve.x1
					+ cubed * theTValue * Math.pow(1 - theTValue, squared) * curve.ctrlx1
					+ cubed * Math.pow(theTValue, squared) * (1 - theTValue) * curve.ctrlx2
					+ Math.pow(theTValue, cubed) * curve.x2;
			returnPoint.y = Math.pow(1 - theTValue, cubed) * curve.y1
					+ cubed * theTValue * Math.pow(1 - theTValue, squared) * curve.ctrly1
					+ cubed * Math.pow(theTValue, squared) * (1 - theTValue) * curve.ctrly2
					+ Math.pow(theTValue, cubed) * curve.y2;
		}
		return returnPoint;
	}
}
