package view;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import model.Participant;
import model.ParticipantSpeed;
import model.Race;
import model.track.Track;
import model.track.TrackSpeed;
import view.track.OvalController;
import view.util.DoubleListener;
import view.util.IntListener;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author Myles Haynes
 */
public class Controller {
	private static final int DEFAULT_LAP_TIME = 60;
	private static final int DEFAULT_RACERS = 10;
	private static final int DEFAULT_NUM_LAPS = 1;

	private static Random rand = new Random();

	@FXML
	private TextField raceName;

	@FXML
	private Slider telemetryIntervalSlider;

	@FXML
	private Text fileDisplay;

	@FXML
	private Button outputFileButton;

	@FXML
	private TextField numLapsField;

	@FXML
	private Button submitRace;

	@FXML
	private BorderPane pane;

	@FXML
	private TextField lapTimeField;

	@FXML
	private TextField numRacersField;

	@FXML
	private GridPane trackSectionPane;

	@FXML
	private List<TextField> speedFields;

	@FXML
	private List<TextField> rangeFields;

	private ScrollPane myParticipantScrollPane;

	private GridPane myParticipantPane;

	private List<ParticipantDisplay> participantDisplays;

	public ProgressBar progressBar;

	private File outputFile;

	private TrackController controller;

	private List<String> linesToWrite = new ArrayList<String>();

	private int numLaps;
	private int numRacers;
	private int lapTime;

	private Map<ParticipantSpeed, Double> speedBracket;
	private Map<ParticipantSpeed, Double> rangeBracket;

	/**
	 * This is called when Controller.fxml is loaded by javafx, its essentially a
	 * constructor.
	 */
	@FXML
	public void initialize() {
		numLaps = DEFAULT_NUM_LAPS;
		numRacers = DEFAULT_RACERS;
		lapTime = DEFAULT_LAP_TIME;

		speedBracket = new HashMap<>();
		rangeBracket = new HashMap<>();
		participantDisplays = new ArrayList<>();

		setUpParticipantPane();

		outputFile = new File(numLaps + "lap-" + numRacers + "racer-" + lapTime + "timeRace.rce");
		try {
			fileDisplay.setText(outputFile.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}

		setUpTrackView();

		outputFileButton.setOnAction(event -> chooseFile());
		submitRace.setOnAction(event -> onSubmit());

		final IntListener racerNumListener = new IntListener((i) -> {
			numRacers = i;
			updateParticipantPane();
		});
		numRacersField.textProperty().addListener(racerNumListener);
		// Force the new IntListener to update
		racerNumListener.changed(numRacersField.textProperty(), "", Integer.toString(numRacers));
		numLapsField.textProperty().addListener(new IntListener((i) -> numLaps = i));
		lapTimeField.textProperty().addListener(new IntListener((i) -> lapTime = i));
		setupTrackSpeedView();

	}

	private void setUpParticipantPane() {
		// Instantiation
		myParticipantScrollPane = new ScrollPane();
		myParticipantPane = new GridPane();

		// Formatting
		myParticipantScrollPane.setPrefHeight(pane.getHeight());

		// Adding
		myParticipantScrollPane.setContent(myParticipantPane);
		pane.setRight(myParticipantScrollPane);
	}

	private void updateParticipantPane() {
		// Clear everything.
		participantDisplays.clear();
		myParticipantPane.getChildren().clear();

		// Make the correct number of new ParticipantDisplays
		for (int i = 0; i < numRacers; i++) {
			ParticipantDisplay newPartDisp = new ParticipantDisplay();
			participantDisplays.add(newPartDisp);
			myParticipantPane.add(newPartDisp, 0, i);
		}
		myParticipantPane.setPrefWidth((new ParticipantDisplay().getPrefWidth()));
	}

	private void setupTrackSpeedView() {
		speedFields = new ArrayList<>();
		rangeFields = new ArrayList<>();

		trackSectionPane.add(new Text("Fast Speed:"), 0, 0);
		TextField fastSpeedField = new TextField("0");
		fastSpeedField.setPrefWidth(50);
		fastSpeedField.textProperty().addListener(new DoubleListener((i) -> updateSpeedBracket(ParticipantSpeed.FAST)));
		speedFields.add(fastSpeedField);
		trackSectionPane.add(fastSpeedField, 1, 0);

		trackSectionPane.add(new Text("Fast Range:"), 2, 0);
		TextField fastRangeField = new TextField("0");
		fastRangeField.setPrefWidth(50);
		fastRangeField.textProperty().addListener(new DoubleListener((i) -> updateRangeBracket(ParticipantSpeed.FAST)));
		rangeFields.add(fastRangeField);
		trackSectionPane.add(fastRangeField, 3, 0);

		trackSectionPane.add(new Text("Medium Speed:"), 0, 1);
		TextField mediumSpeedField = new TextField("0");
		mediumSpeedField.setPrefWidth(50);
		mediumSpeedField.textProperty()
				.addListener(new DoubleListener((i) -> updateSpeedBracket(ParticipantSpeed.MEDIUM)));
		speedFields.add(mediumSpeedField);
		trackSectionPane.add(mediumSpeedField, 1, 1);

		trackSectionPane.add(new Text("Medium Range:"), 2, 1);
		TextField mediumRangeField = new TextField("0");
		mediumRangeField.setPrefWidth(50);
		mediumRangeField.textProperty()
				.addListener(new DoubleListener((i) -> updateRangeBracket(ParticipantSpeed.MEDIUM)));
		rangeFields.add(mediumRangeField);
		trackSectionPane.add(mediumRangeField, 3, 1);

		trackSectionPane.add(new Text("Slow Speed:"), 0, 2);
		TextField slowSpeedField = new TextField("0");
		slowSpeedField.setPrefWidth(50);
		slowSpeedField.textProperty().addListener(new DoubleListener((i) -> updateSpeedBracket(ParticipantSpeed.SLOW)));
		speedFields.add(slowSpeedField);
		trackSectionPane.add(slowSpeedField, 1, 2);

		trackSectionPane.add(new Text("Slow Range:"), 2, 2);
		TextField slowRangeField = new TextField("0");
		slowRangeField.setPrefWidth(50);
		slowRangeField.textProperty().addListener(new DoubleListener((i) -> updateRangeBracket(ParticipantSpeed.SLOW)));
		rangeFields.add(slowRangeField);
		trackSectionPane.add(slowRangeField, 3, 2);
	}

	private void updateSpeedBracket(ParticipantSpeed speedBracket) {

	}

	private void updateRangeBracket(ParticipantSpeed speedBracket) {

	}

	private void setUpTrackView() {
		try {
			FXMLLoader loader = new FXMLLoader(OvalController.class.getResource("OvalController.fxml"));
			pane.setCenter(loader.load());
			controller = loader.getController();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void chooseFile() {
		FileChooser chooser = new FileChooser();
		outputFile = chooser.showSaveDialog(null);
		try {
			fileDisplay.setText(outputFile.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void onSubmit() {
		progressBar.setVisible(true);
		BuildTask task = new BuildTask();
		progressBar.progressProperty().bind(task.progressProperty());
		task.setOnSucceeded(wse -> progressBar.setVisible(false));
		new Thread(task).start();
	}

	// This is a fix for the randomness causing the last racer to
	// actually finish after the specified time, this method is a
	// hack to change the time to the last racer
	private void adjustForLastRacer() {

		// Start at the end of the array and look back until we find when
		// the last racer crossed the finish line.
		int i = linesToWrite.size() - 1;
		while (!linesToWrite.get(i).contains("$C")) {
			i--;
		}

		// Extract the millisecond from the string
		int raceActuallyOver = (int) Double.parseDouble(linesToWrite.get(i).split(":")[1]);

		// Because this is technically an index, add one to get the length
		raceActuallyOver++;

		// Modify the time to be this new value
		linesToWrite.set(5, "#TIME:" + raceActuallyOver);

		System.out.println(raceActuallyOver);
	}

	private class BuildTask extends Task<Void> {

		@Override
		protected Void call() throws Exception {
			try {
				System.out.println("Inside call");

				int telemetryInterval = (int) telemetryIntervalSlider.getValue();
				Track track = controller.getTrack();
				List<Participant> participants = new ArrayList<>();
				int start = 0;
				System.out.println("HERE");
				speedBracket.put(ParticipantSpeed.MEDIUM, 1.0);
				for (ParticipantDisplay disp : participantDisplays) {
					participants.add(new Participant(disp.getID(), disp.getName(), start, track.getTrackLength(),
							speedBracket.get(ParticipantSpeed.MEDIUM), speedBracket.get(ParticipantSpeed.MEDIUM)));
				}
				Race race = new Race(track, numLaps, telemetryInterval, participants);

				System.out.println("Starting to write or something");

				// Convert seconds to millis
				int expectedTime = 1000 * lapTime * numLaps;

				int currentTime = 0;
				linesToWrite.add("#RACE:" + raceName.getText());
				linesToWrite.add("#TRACK:" + track.getTrackName());

				// TODO These are hard coded values, make things in the UI to
				// change this.
				linesToWrite.add("#WIDTH:5");
				linesToWrite.add("#HEIGHT:4");

				linesToWrite.add("#DISTANCE:" + track.getTrackLength());
				linesToWrite.add("#TIME:" + expectedTime);
				linesToWrite.add("#PARTICIPANTS:" + numRacers);

				while (race.stillGoing()) {
					race.stepRace().forEach(linesToWrite::add);
					currentTime++;
					updateProgress(currentTime, expectedTime);
				}

//				adjustForLastRacer();

				System.out.println("Right before writing file");
				try (PrintWriter pw = new PrintWriter(outputFile)) {
					System.out.println("writing file");
					for (int i = 0; i < linesToWrite.size(); i++) {
						pw.println(linesToWrite.get(i));
					}
					pw.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

}
