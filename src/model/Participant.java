package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import model.track.TrackSpeed;
import race_constraints.ParticipantConstraint;
import race_constraints.TrackSectionConstraint;

import static java.lang.Math.ceil;

/**
 * 
 * @author Myles Haynes, Peter Bae
 */
public class Participant implements Comparable<Participant> {
	public static final double DEFAULT_ACCELERATION = 0.0015;
	public static final double DEFAULT_DECELERATION = 0.002;

	private static Random rng = new Random();
	private int myID;
	private String myName;
	private double myPosition;
	private ParticipantSpeed mySpeedBracket;
	private double myVelocity;
	private double myNextVelocity;
	private int myLapNum;
	private int myTrackLength;
	private Map<String, ParticipantConstraint> myConstraints;

	public Participant(int id, String name, double startDistance, int trackLength, ParticipantSpeed speed) {
		myID = id;
		myName = name;
		myPosition = startDistance;
		mySpeedBracket = speed;
		myVelocity = speed.getNewVelocity();
		myNextVelocity = speed.getNewVelocity();
		myTrackLength = trackLength;
		myConstraints = new HashMap<>();
	}

//	public double step(SpeedClass speedClass) {
//		changeSpeed(speedClass);
//		myPosition += myVelocity;
//		if (myPosition >= myTrackLength) {
//			myLapNum++;
//			myPosition -= myTrackLength;
//		}
//		return myPosition;
//	}

	public double step() {
		double velocity = myVelocity;
		for (String condition : myConstraints.keySet()) {
			velocity = myConstraints.get(condition).applyConstraint(velocity, mySpeedBracket);
		}

		myPosition += velocity;
		// This causes a problem where accelerations compound and we don't want that but
		// we need some way for acceleration to compound
//		myVelocity = velocity;

		if (myPosition >= myTrackLength) {
			myLapNum++;
			myPosition -= myTrackLength;
		}

		return myPosition;
	}

	public ParticipantSpeed getParticipantSpeed() {
		return mySpeedBracket;
	}

	public void addConstraint(String key, ParticipantConstraint rc) {
		myConstraints.put(key, rc);
	}

	public void removeConstraint(String key) {
		myConstraints.remove(key);
	}

	public boolean hasConstraint(String key) {
		return myConstraints.containsKey(key);
	}

	public double getPosition() {
		return myPosition;
	}

	public int getLapNum() {
		return myLapNum;
	}

	public void setVelocity(double myVelocity) {
		this.myVelocity = myVelocity;
	}

	// Whenever a participant crosses a gate we need to randomly select the next
	// speed it is going to go.
	public void calculateNextVelocity() {
		myNextVelocity = mySpeedBracket.getNewVelocity();
	}
	
	public double getNextVelocity() {
		return myNextVelocity;
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
//	public double timeUntilCrossingFinish() {
//		return (myTrackLength *  - myPosition) / myVelocity;
//	}

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
	private static String buildRacerName() {
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

	public double getVelocity() {
		return myVelocity;
	}
}
