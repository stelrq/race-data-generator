package model;

import java.util.Random;
import static java.lang.Math.ceil;

/**
 * 
 * @author Myles Haynes
 */
public class Racer implements Comparable<Racer> {
	private static Random rng = new Random();
	private double distance;
	private double fastMin;
	private int racerId;
	private double fastMax;
	private int trackDistance;
	private int lapNum;
	private String name;

	private double currentSpeed;

	private SpeedClass lastSpeed;

	public Racer(int id, double startDistance, int trackDistance, double fastMin, double fastMax) {
		this.distance = startDistance;
		this.racerId = id;
		this.trackDistance = trackDistance;
		this.fastMin = fastMin;
		this.fastMax = fastMax;
		this.name = buildRacerName();
		changeSpeed(SpeedClass.FAST);
		lastSpeed = SpeedClass.FAST;
	}

	public double step(SpeedClass speedClass) {
		changeSpeed(speedClass);
		distance += currentSpeed;
		if (distance >= trackDistance) {
			lapNum++;
			distance -= trackDistance;
		}
		return distance;
	}

	public double getDistance() {
		return distance;
	}

	public int getLapNum() {
		return lapNum;
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
		return (trackDistance - distance) / currentSpeed;
	}

	private void changeSpeed(SpeedClass speedClass) {
		if (speedClass != lastSpeed) {
			double currentMin = fastMin + speedClass.getOffset();
			double currentMax = fastMax + speedClass.getOffset();
			currentSpeed = currentMin + rng.doubles(0, currentMax - currentMin).findFirst().orElse(0);
			lastSpeed = speedClass;
		}
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
		return Integer.toString(racerId);
	}

	@Override
	public String toString() {
		return racerId + " : " + distance;
	}

	@Override
	public int compareTo(Racer o) {
		if (lapNum != o.lapNum) {
			return o.lapNum - lapNum;
		} else if (distance != o.distance) {
			return (int) ceil(o.distance - distance);
		} else {
			return 0;
		}
	}

	public String getName() {
		return name;
	}
}
