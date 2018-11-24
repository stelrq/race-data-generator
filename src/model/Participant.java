package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	private ParticipantSpeed mySpeedBracket;
	private double myVelocity;
	private double myAcceleration;	
	private int myLapNum;
	private int myTrackLength;
	private Map<String, RaceConstraint> myConstraints;

	public Participant(int id, double startDistance, int trackLength, ParticipantSpeed speed) {
		this(id, buildRacerName(), startDistance, trackLength, speed);
	}
	
	public Participant(int id, String name, double startDistance, int trackLength, ParticipantSpeed speed) {
		myID = id;
		myName = name;
		myPosition = startDistance;
		mySpeedBracket = speed;
		myVelocity = mySpeedBracket.getVelocity();
		myAcceleration = 0;
		myTrackLength = trackLength;
		myConstraints = new HashMap<>();
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
		for (String condition : myConstraints.keySet()) {
			velocity = myConstraints.get(condition).applyContraint(velocity);
		}
		
		myPosition += velocity;
		
		if (myPosition >= myTrackLength) {
			myLapNum++;
			myPosition -= myTrackLength;
		}
		
		return myPosition;
	}

	public void addConstraint(String key, RaceConstraint rc) {
		myConstraints.put(key, rc);
	}
	
	public void removeConstraint(String key) {
		myConstraints.remove(key);
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
}
