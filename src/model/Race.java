package model;

import java.util.*;
import java.util.stream.Collectors;

import com.sun.javafx.css.CalculatedValue;

import model.track.Track;
import race_constraints.AccelerationConstraint;
import race_constraints.DecelerationConstraint;
import race_constraints.TrackSectionConstraint;

import static java.lang.String.format;
import static java.util.Collections.sort;
import static java.util.stream.Collectors.joining;

/**
 *
 * @author Myles Haynes, Peter Bae
 */
public class Race {
	private final Track track;
	private List<Participant> participants;
	private final int numLaps;
	private int time;
	private Random rng;
	private Map<Participant, Integer> lastMessageTime;
	private List<Participant> participantsNotFinished;

	private final int timeSlice;

	public Race(Track track, int numLaps, int telemetryInterval, List<Participant> participants) {
		this.track = track;
		this.numLaps = numLaps;
		this.participants = participants;
		time = 0;
		timeSlice = telemetryInterval;
		lastMessageTime = new HashMap<>();
		participantsNotFinished = new ArrayList<>();
		participantsNotFinished.addAll(participants);

		// fix for now so lastMessageTime starts populated
		for (int i = 0; i < participants.size(); i++) {
			lastMessageTime.put(participants.get(i), 0);
		}
		rng = new Random();
	}

	public List<String> stepRace() {
//		System.out.println("Stepping race: " + time + " " + participants);
		List<String> messages = new ArrayList<>();
		if (time == 0) {
			messages.addAll(setUpMessages());
		}
		for (Participant participant : participants) {
			// Evaluate constraints
			double lastDistance = participant.getPosition();
			evaluateConstraints(participant);

			participant.step();

			// We passed a gate, remove acceleration
			if (track.getDistanceUntilNextTrackPiece(participant.getPosition()) > track
					.getDistanceUntilNextTrackPiece(lastDistance)) {
				participant.removeConstraint("Acceleration");
			}

			if (lastMessageTime.get(participant) % timeSlice == 0) {
				messages.add(format("$T:%d:%s:%.2f:%d", time, participant.getRacerId(), participant.getPosition(),
						participant.getLapNum()));
			}
			lastMessageTime.compute(participant, (_r, i) -> i + 1);
		}
		newLeaderBoard().ifPresent(messages::add);
		messages.addAll(crossingMessages());
		time++;
		return messages;
	}

	private void evaluateConstraints(Participant participant) {
		final double participantDistance = participant.getPosition();
		// Add the appropriate track constraint
		participant.addConstraint("track", new TrackSectionConstraint(track.getTrackSpeed(participant.getPosition())));

		// Determine if Acceleration/Deceleration is necessary
		// if ((Roughly the speed we have to be at the next gate)
		// - (Roughly the speed we're going now)) <=
		if (!participant.hasConstraint("Acceleration")) {
			double speedDifference = track.getNextTrackSpeed(participantDistance).getMultiplier()
					* participant.getParticipantSpeed().getVelocity()
					- track.getTrackSpeed(participantDistance).getMultiplier()
							* participant.getParticipantSpeed().getVelocity();

//			// make acceleration more drawn out.
//			speedDifference *= 1000;

			if (speedDifference > 0) {
				if (track.getDistanceUntilNextTrackPiece(participantDistance) <= calculateDistanceForAcceleration(
						track.getTrackSpeed(participantDistance).getMultiplier()
								* participant.getParticipantSpeed().getVelocity(),
						track.getNextTrackSpeed(participantDistance).getMultiplier()
								* participant.getParticipantSpeed().getVelocity(), Participant.DEFAULT_ACCELERATION)) {
					
					System.out.println("added acceleration " + track.getDistanceUntilNextTrackPiece(participantDistance) + " we are this far away " + calculateDistanceForAcceleration(
							track.getTrackSpeed(participantDistance).getMultiplier()
							* participant.getParticipantSpeed().getVelocity(),
					track.getNextTrackSpeed(participantDistance).getMultiplier()
							* participant.getParticipantSpeed().getVelocity(), Participant.DEFAULT_ACCELERATION) + " calculated distance we will travel " + (participant.getParticipantSpeed().getVelocity() * track.getTrackSpeed(participantDistance).getMultiplier()) + " current velocity " + track.getNextTrackSpeed(participantDistance).getMultiplier() * participant.getParticipantSpeed().getVelocity() + " velocity we have to be ");
					
					participant.addConstraint("Acceleration",
							new AccelerationConstraint(Participant.DEFAULT_ACCELERATION,
									participant.getParticipantSpeed().getVelocity(), speedDifference, 10000));
				}
			} else if (speedDifference < 0) {
				if (track.getDistanceUntilNextTrackPiece(participantDistance)
						/ Participant.DEFAULT_DECELERATION <= -speedDifference) {
					participant.addConstraint("Acceleration", new DecelerationConstraint(
							Participant.DEFAULT_DECELERATION, participant.getParticipantSpeed().getVelocity()));
				}
			}
		}
	}

	private double calculateDistanceForAcceleration(double initialVelocity, double finalVelocity, double acceleration) {
		// t = (vf - vi) /
		//         a
		double t = ((finalVelocity - initialVelocity) / acceleration);
		
		// s = vi*t + (1/2)*a*t^2
		final double s = initialVelocity * t + 0.5 * acceleration * Math.pow(t, 2);
		return s;
	}

	private List<String> setUpMessages() {
		List<String> returnList = participants.stream()
				.map(r -> "#" + r.getRacerId() + ":" + r.getName() + ":" + r.getPosition())
				.collect(Collectors.toList());
		returnList.add("$L:0:"
				+ participants.stream().map(Participant::getRacerId).map(Object::toString).collect(joining(":")));
		return returnList;

	}

	public boolean stillGoing() {
		for (int i = 0; i < participants.size(); i++) {
			if (participants.get(i).getLapNum() < numLaps) {
				return true;
			}
		}
		return false;
//		return participants.stream().map(Participant::getLapNum).anyMatch((l) -> l < numLaps);
	}

	private Optional<String> newLeaderBoard() {
		List<Participant> prevPlaces = new ArrayList<>(participants);
		sort(participants);
		if (prevPlaces.equals(participants)) {
			return Optional.empty();
		} else {
			String leaderBoard = "$L:" + time + ":";
			leaderBoard += participants.stream().map(Participant::getRacerId).map(Object::toString)
					.collect(joining(":"));
			return Optional.of(leaderBoard);
		}
	}

	private List<String> crossingMessages() {
		List<Participant> participantstoRemove = new ArrayList<>();
		List<String> messages = new ArrayList<>();
		for (Participant p : participantsNotFinished) {
			if (p.getLapNum() == numLaps) {
				double crossTime = time;
				messages.add(
						format("$C:%.2f:%s:%d:%b", crossTime, p.getRacerId(), p.getLapNum(), p.getLapNum() == numLaps));
				participantstoRemove.add(p);
			}
		}
		participantsNotFinished.removeAll(participantstoRemove);
		return messages;

	}

//	private List<Participant> buildRacers() {
//
//		if (numParticipants > 100) {
//			throw new UnsupportedOperationException("Cannot create more than 100 racers currently.");
//		}
//		List<Participant> racers = new ArrayList<>();
//
//		//double speed = track.getTrackLength() * 1.0 / avgLapTime;
//		//double minSpeed = speed * 0.98;
//		//double maxSpeed = speed * 1.02;
//
//		HashSet<Integer> usedIds = new HashSet<>();
//		while (usedIds.size() < numParticipants) {
//			usedIds.add(rng.nextInt(100));
//		}
//		int i = 0;
//		for (int id : usedIds) {
//			int startDistance = round(track.getTrackLength() * (i * .01f));
//			Participant racer = new Participant(id, startDistance, track.getTrackLength(), minSpeed, maxSpeed);
//			lastMessageTime.put(racer, i);
//			racers.add(racer);
//			i--;
//		}
//		return racers;
//	}

}
