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

/**
 *
 * @author Myles Haynes
 */
public class Controller {

	private static final int DEFAULT_LAP_TIME = 60;
	private static final int DEFAULT_RACERS = 10;
	private static final int DEFAULT_NUM_LAPS = 1;
	public ProgressBar progressBar;

	@FXML
	private TextField raceName;

	@FXML
	private Slider telemetryIntervalSlider;

	private File outputFile;

	private TrackController controller;

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

	private int numLaps;
	private int numRacers;
	private int lapTime;

	/**
	 * This is called when Controller.fxml is loaded by javafx, its essentially a
	 * constructor.
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

	private class SimTask extends Task<Void> {

		@Override
		protected Void call() throws Exception {
			try (PrintWriter pw = new PrintWriter(outputFile)) {

				int telemetryInterval = (int) telemetryIntervalSlider.getValue();
				Track track = controller.getTrack();
				Race race = new Race(track, numLaps, numRacers, lapTime, telemetryInterval);

				int expectedTime = 1000 * lapTime * numLaps; // Convert seconds to millis
				int currentTime = 0;
				pw.println("#RACE:" + raceName.getText());
				pw.println("#TRACK:" + track.getTrackName());

				// HARD CODED VALUES
				pw.println("#WIDTH:5");
				pw.println("#HEIGHT:4");

				pw.println("#DISTANCE:" + track.getTrackLength());
				pw.println("#TIME:" + expectedTime);
				pw.println("#PARTICIPANTS:" + numRacers);

				while (race.stillGoing()) {
					race.stepRace().forEach(pw::println);
					currentTime++;
					updateProgress(currentTime, expectedTime);
				}
				System.out.println("RACE OVER");
				pw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

}
