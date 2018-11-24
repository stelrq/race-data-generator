package model;

import java.util.*;
import java.util.stream.Collectors;

import model.track.Track;

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
	
	
	private final int timeSlice;

	
	public Race(Track track, int numLaps, int telemetryInterval,
			List<Participant> participants) {
		this.track = track;
		this.numLaps = numLaps;
		this.participants = participants;
		time = 0;
		timeSlice = telemetryInterval;
		lastMessageTime = new HashMap<>();
		for (int i = 0; i < participants.size(); i++) {
			lastMessageTime.put(participants.get(i), i);
		}
		rng = new Random();
	}

	public List<String> stepRace() {
		List<String> messages = new ArrayList<>();
		if (time == 0) {
			messages.addAll(setUpMessages());
		}
		for (Participant participant : participants) {
			participant.step(track.getSpeedClass(participant.getPosition()));

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

	private List<String> setUpMessages() {
		List<String> returnList = participants.stream().map(r -> "#" + r.getRacerId() + ":" + r.getName() + ":" + r.getPosition())
				.collect(Collectors.toList());
		returnList.add("$L:0:" + participants.stream().map(Participant::getRacerId).map(Object::toString).collect(joining(":")));
		return returnList;

	}

	public boolean stillGoing() {
		return participants.stream().map(Participant::getLapNum).anyMatch((l) -> l < numLaps);
	}

	private Optional<String> newLeaderBoard() {
		List<Participant> prevPlaces = new ArrayList<>(participants);
		sort(participants);
		if (prevPlaces.equals(participants)) {
			return Optional.empty();
		} else {
			String leaderBoard = "$L:" + time + ":";
			leaderBoard += participants.stream().map(Participant::getRacerId).map(Object::toString).collect(joining(":"));
			return Optional.of(leaderBoard);
		}
	}

	private List<String> crossingMessages() {

		List<String> messages = new ArrayList<>();
		for (Participant p : participants) {
			if (p.timeUntilCrossingFinish() <= 1) {
				double crossTime = time + p.timeUntilCrossingFinish();
				int newLap = p.getLapNum() + 1;
				messages.add(format("$C:%.2f:%s:%d:%b", crossTime, p.getRacerId(), newLap, newLap == numLaps));
			}
		}
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
