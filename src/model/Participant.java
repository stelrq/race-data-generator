package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static java.lang.Math.ceil;

/**
 * 
 * @author Myles Haynes, Peter Bae
 */
public class Participant implements Comparable<Participant> {
	private static Random rng = new Random();
	
	private int myID;
	private String myName;
	private double myPosition;
	private double myVelocity;
	private double myRange;
	private double myAcceleration;	
	private int myLapNum;
	private int myTrackLength;
	private List<RaceConstraint> myConstraints;

	public Participant(int id, double startDistance, int trackLength, double velocity, double range) {
		myID = id;
		myName = buildRacerName();
		myPosition = startDistance;
		myVelocity = velocity;
		myRange = range;
		myAcceleration = 0;
		myTrackLength = trackLength;
		myConstraints = new ArrayList<>();
	}

	public double step(SpeedClass speedClass) {
		changeSpeed(speedClass);
		myPosition += myVelocity;
		if (myPosition >= myTrackLength) {
			myLapNum++;
			myPosition -= myTrackLength;
		}
		return myPosition;
	}
	
	public double step() {
		double velocity = myVelocity;
		for (RaceConstraint rc : myConstraints) {
			velocity = rc.applyContraint(velocity);
		}
		
		myPosition += myVelocity;
		
		return myPosition;
	}

	public void addConstraint(RaceConstraint rc) {
		myConstraints.add(rc);
	}
	
	public void removeConstraint(RaceConstraint rc) {
		myConstraints.remove(rc);
	}
	
	public void removeConstraint(int index) {
		myConstraints.remove(index);
	}
	
	public double getPosition() {
		return myPosition;
	}

	public int getLapNum() {
		return myLapNum;
	}

	/**
	 *
	 * Calculates the time until you'll cross the finish.
	 *
	 * D = Total distance of track d = current distance s = current speed t = time
	 * until crossing finish
	 *
	 * Math: (based on Y = mx+b) D = st + d D - d = st (D - d) / s = t
	 *
	 */
	public double timeUntilCrossingFinish() {
		return (myTrackLength - myPosition) / myVelocity;
	}

	private void changeSpeed(SpeedClass speedClass) {
//		if (speedClass != lastSpeed) {
//			double currentMin = fastMin + speedClass.getOffset();
//			double currentMax = fastMax + speedClass.getOffset();
//			currentSpeed = currentMin + rng.doubles(0, currentMax - currentMin).findFirst().orElse(0);
//			lastSpeed = speedClass;
//		}
	}

	/**
	 *
	 * @return Random string like "Akdkdjfk"
	 */
	private String buildRacerName() {
		int lowerA = 97; // ascii value of lowercase 'a'
		int upperA = 65; // ascii value of uppercase 'A'

		StringBuilder name = new StringBuilder();

		name.append((char) (upperA + rng.nextInt(26))); // 26 letters in alphabet.
		for (int i = 0; i < 8; i++) {
			name.append((char) (lowerA + rng.nextInt(26)));
		}

		return name.toString();

	}

	public String getRacerId() {
		return Integer.toString(myID);
	}

	@Override
	public String toString() {
		return myID + " : " + myPosition;
	}

	@Override
	public int compareTo(Participant o) {
		if (myLapNum != o.myLapNum) {
			return o.myLapNum - myLapNum;
		} else if (myPosition != o.myPosition) {
			return (int) ceil(o.myPosition - myPosition);
		} else {
			return 0;
		}
	}

	public String getName() {
		return myName;
	}
}
