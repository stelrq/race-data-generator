package view;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import model.Race;
import model.track.Track;
import view.track.OvalController;
import view.util.IntListener;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Myles Haynes
 */
public class Controller {

	private static final int DEFAULT_LAP_TIME = 60;
	private static final int DEFAULT_RACERS = 10;
	private static final int DEFAULT_NUM_LAPS = 1;

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
	private GridPane racerPane;

	public ProgressBar progressBar;

	private File outputFile;

	private TrackController controller;

	private List<String> linesToWrite = new ArrayList<String>();

	private int numLaps;
	private int numRacers;
	private int lapTime;

	/**
	 * This is called when Controller.fxml is loaded by javafx, its essentially
	 * a constructor.
	 */
	@FXML
	public void initialize() {
		numLaps = DEFAULT_NUM_LAPS;
		numRacers = DEFAULT_RACERS;
		lapTime = DEFAULT_LAP_TIME;

		outputFile = new File(numLaps + "lap-" + numRacers + "racer-" + lapTime + "timeRace.rce");
		try {
			fileDisplay.setText(outputFile.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}

		setUpTrackView();

		outputFileButton.setOnAction(event -> chooseFile());
		submitRace.setOnAction(event -> onSubmit());

		numRacersField.textProperty().addListener(new IntListener((i) -> numRacers = i));
		numLapsField.textProperty().addListener(new IntListener((i) -> numLaps = i));
		lapTimeField.textProperty().addListener(new IntListener((i) -> lapTime = i));

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
		SimTask task = new SimTask();
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
		int raceActuallyOver = (int)Double.parseDouble(linesToWrite.get(i).split(":")[1]);

		// Because this is technically an index, add one to get the length
		raceActuallyOver++;

		// Modify the time to be this new value
		linesToWrite.set(5, "#TIME:" + raceActuallyOver);

		System.out.println(raceActuallyOver);
	}

	private class SimTask extends Task<Void> {

		@Override
		protected Void call() throws Exception {

			int telemetryInterval = (int) telemetryIntervalSlider.getValue();
			Track track = controller.getTrack();
			Race race = new Race(track, numLaps, numRacers, lapTime, telemetryInterval);

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

			adjustForLastRacer();

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
			return null;
		}
	}

}
