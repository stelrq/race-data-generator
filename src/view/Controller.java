package view;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import model.Participant;
import model.ParticipantSpeed;
import model.Race;
import model.track.OvalTrack;
import model.track.Track;
import model.track.TrackSpeed;
import view.util.IntListener;
import view.util.IntMaxListener;
import view.util.ToolTips;

/**
 *
 * @author Myles Haynes
 */
public class Controller extends BorderPane {
    private static final int DEFAULT_LAP_TIME = 60;
    private static final int DEFAULT_RACERS = 10;
    private static final int DEFAULT_NUM_LAPS = 1;

    private static Random rand = new Random();

    @FXML
    private TextField raceNameField;

    @FXML
    private Slider telemetryIntervalSlider;

//	@FXML
//	private Slider trackSpeedSlider;

    @FXML
    private Text fileDisplay;

    @FXML
    private Button outputFileButton;

    @FXML
    private TextField numLapsField;

    @FXML
    private Button submitRace;

    @FXML
    private BorderPane outerPane;

    @FXML
    private BorderPane innerPane;

//	@FXML
//	private TextField lapTimeField;

    @FXML
    private TextField numRacersField;

    private TextField trackLengthField;

    private TextField xRatioField;

    private TextField yRatioField;

    private FlowPane configPane;

    private FlowPane generationControlPane;

    private List<TextField> speedFields;

    private List<TextField> rangeFields;

    private List<ParticipantDisplay> participantDisplays;

    private List<TextField> trackSpeedMultiplierFields;

    private ScrollPane myParticipantScrollPane;

    private GridPane myParticipantPane;

    public ProgressBar progressBar;

    private File outputFile;

    private Track myTrack;

    private List<ComboBox<TrackSpeed>> myTrackSectionComboBoxes;

//	private TrackController controller;

    private List<String> linesToWrite;

    private int numLaps;
    private int numRacers;
    private int lapTime;
    private int numSpeedBrackets;
    private List<ParticipantSpeed> speedBracketList;
    private Map<ParticipantSpeed, Text> estimateTimes;

    /**
     * This is called when Controller.fxml is loaded by javafx, its essentially
     * a constructor.
     */
    public Controller() {
        super();
        numLaps = DEFAULT_NUM_LAPS;
        linesToWrite = new ArrayList<>();
        participantDisplays = new ArrayList<>();
        estimateTimes = new HashMap<>();

        instantiateComponents();

//		setUpTrackView();

//		setCenter(innerPane);
        setSpeedBracketList();
        setUpConfigPane();
        setUpGenerationControlPane();

//		numSpeedBrackets = (int) trackSpeedSlider.getValue();
        numRacers = Integer.parseInt(numRacersField.textProperty().getValue());

        setUpParticipantPane();

        outputFile = new File("myRace.rce");
        try {
            fileDisplay.setText(outputFile.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        outputFileButton.setOnAction(event -> chooseFile());
        submitRace.setOnAction(event -> onSubmit());

        final IntMaxListener racerNumListener = new IntMaxListener((i) -> {
            numRacers = i;
            ParticipantDisplay.clearNamesAndIDS();
            updateParticipantPane();
        }, 30);
        numRacersField.textProperty().addListener(racerNumListener);
        // Force the new IntListener to update
        racerNumListener.changed(numRacersField.textProperty(), "",
                Integer.toString(numRacers));

        numLapsField.textProperty()
                .addListener(new IntListener((i) -> numLaps = i));
        // lapTimeField.textProperty().addListener(new IntListener((i) ->
        // lapTime = i));
//		trackSpeedSlider.valueProperty().addListener((i, o, n) -> {
//			numSpeedBrackets = i.getValue().intValue();
//			resetSpeedBrackets();
//			setSpeedBracketList();
//			setupTrackSpeedView();
//			updateSpeedBracketComboBox();
//		});

        setToolTips();
        autosize();
    }

    /**
     * Use the {@link ToolTips} utility class to create tooltips for all fields.
     */
    private void setToolTips() {
        telemetryIntervalSlider.setTooltip(
                ToolTips.createTooltip(ToolTips.TELEMETRY_INTERVAL_SLIDER));
        numLapsField.setTooltip(ToolTips.createTooltip(ToolTips.NUM_LAPS));
        numRacersField.setTooltip(ToolTips.createTooltip(ToolTips.NUM_RACERS));
        raceNameField.setTooltip(ToolTips.createTooltip(ToolTips.RACE_NAME));
        trackLengthField.setTooltip(ToolTips.createTooltip(ToolTips.TRACK_LENGTH));
        xRatioField.setTooltip(ToolTips.createTooltip(ToolTips.X_RATIO));
        yRatioField.setTooltip(ToolTips.createTooltip(ToolTips.Y_RATIO));
        for (int i = 0; i < myTrackSectionComboBoxes.size(); i++) {
            myTrackSectionComboBoxes.get(i).setTooltip(ToolTips.createTooltip(ToolTips.TRACK_SECTION));
        }
        for (int i = 0; i < trackSpeedMultiplierFields.size(); i++) {
            trackSpeedMultiplierFields.get(i).setTooltip(ToolTips.createTooltip(ToolTips.TRACK_SPEED));
        }

    }

    private void instantiateComponents() {
        // Instantiation
        configPane = new FlowPane(Orientation.VERTICAL);
        generationControlPane = new FlowPane(Orientation.VERTICAL);

        trackSpeedMultiplierFields = new ArrayList<TextField>();
        myTrackSectionComboBoxes = new ArrayList<>();
        xRatioField = new TextField("2");
        yRatioField = new TextField("1");
        trackLengthField = new TextField("500000");
        myTrack = new OvalTrack(500000, 2, 1);
        raceNameField = new TextField("My305Race");
        telemetryIntervalSlider = new Slider();
        numRacersField = new TextField("10");
        fileDisplay = new Text();
        outputFileButton = new Button("Browse...");
        submitRace = new Button("Generate Race");
        numLapsField = new TextField("1");
        setLeft(configPane);
        progressBar = new ProgressBar();

    }

    private void setUpConfigPane() {
        // Instantiation
        GridPane raceConfigPane = new GridPane();
        GridPane trackSpeedConfigPane = new GridPane();
        GridPane estTimePane = new GridPane();
        GridPane trackSectionConfigPane = new GridPane();
        GridPane trackSectionSpeedConfigPane = new GridPane();

        speedFields = new ArrayList<>();
        rangeFields = new ArrayList<>();
        estimateTimes = new HashMap<>();

        // Formatting
        raceConfigPane.setHgap(10);
        raceConfigPane.setVgap(10);
        raceConfigPane.setPadding(new Insets(10));
        raceConfigPane.setAlignment(Pos.CENTER);

        trackSpeedConfigPane.setHgap(10);
        trackSpeedConfigPane.setVgap(10);
        trackSpeedConfigPane.setPadding(new Insets(10));
        trackSpeedConfigPane.setAlignment(Pos.CENTER);

        trackSectionConfigPane.setHgap(10);
        trackSectionConfigPane.setVgap(10);
        trackSectionConfigPane.setPadding(new Insets(10));
        trackSectionConfigPane.setAlignment(Pos.CENTER);

        trackSectionSpeedConfigPane.setHgap(10);
        trackSectionSpeedConfigPane.setVgap(10);
        trackSectionSpeedConfigPane.setPadding(new Insets(10));
        trackSectionSpeedConfigPane.setAlignment(Pos.CENTER);

        telemetryIntervalSlider.setMin(5);
        telemetryIntervalSlider.setMax(15);
        telemetryIntervalSlider.setBlockIncrement(1);
        telemetryIntervalSlider.setMajorTickUnit(1);
        telemetryIntervalSlider.setMinorTickCount(0);
        telemetryIntervalSlider.setShowTickLabels(true);
        telemetryIntervalSlider.setShowTickMarks(true);
        telemetryIntervalSlider.snapToTicksProperty().set(true);
        telemetryIntervalSlider.valueProperty().set(10);

        // Adding to raceConfigPane
        raceConfigPane.add(new Text("Telemetry Interval (ms)"), 0, 0);
        raceConfigPane.add(telemetryIntervalSlider, 1, 0);
        raceConfigPane.add(new Text("Number of Laps"), 0, 2);
        raceConfigPane.add(numLapsField, 1, 2);
        raceConfigPane.add(new Text("Number of Racers"), 0, 3);
        raceConfigPane.add(numRacersField, 1, 3);
        raceConfigPane.add(new Text("Race Name"), 0, 4);
        raceConfigPane.add(raceNameField, 1, 4);
        raceConfigPane.add(new Text("Track Length"), 0, 5);
        raceConfigPane.add(trackLengthField, 1, 5);

        // Set up ratio input
        GridPane xRatPane = new GridPane();
        GridPane yRatPane = new GridPane();

        xRatioField.setPrefColumnCount(2);
        yRatioField.setPrefColumnCount(2);
        xRatPane.add(new Text("Track xRatio"), 0, 0);
        xRatPane.add(xRatioField, 1, 0);
        yRatPane.add(new Text("Track yRatio"), 0, 0);
        yRatPane.add(yRatioField, 1, 0);

        raceConfigPane.add(xRatPane, 0, 6);
        raceConfigPane.add(yRatPane, 1, 6);

        // Add ChangeListeners
        TrackChangeListener trackListener = new TrackChangeListener();
        trackLengthField.textProperty().addListener(trackListener);
        xRatioField.textProperty().addListener(trackListener);
        yRatioField.textProperty().addListener(trackListener);

        // Setup TrackSectionSpeedConfig
        // 6 combo boxes for 6 sections of the track
        for (int i = 0; i < 6; i++) {
            ObservableList<TrackSpeed> options = FXCollections
                    .observableArrayList();
            options.addAll(TrackSpeed.values());
            ComboBox<TrackSpeed> comboBox = new ComboBox<TrackSpeed>(options);
            myTrackSectionComboBoxes.add(comboBox);
            trackSectionConfigPane.add(comboBox, 1, i);
        }
        trackSectionConfigPane.add(new Text("Turn 1 Speed: "), 0, 0);
        trackSectionConfigPane.add(new Text("Turn 2 Speed: "), 0, 1);
        trackSectionConfigPane.add(new Text("Bottom Straight Speed: "), 0, 2);
        trackSectionConfigPane.add(new Text("Turn 3 Speed: "), 0, 3);
        trackSectionConfigPane.add(new Text("Turn 4 Speed: "), 0, 4);
        trackSectionConfigPane.add(new Text("Top Straight Speed: "), 0, 5);

        // Set default values for the combo boxes
        myTrackSectionComboBoxes.get(0).getSelectionModel()
                .select(TrackSpeed.SLOW);
        myTrackSectionComboBoxes.get(1).getSelectionModel()
                .select(TrackSpeed.MEDIUM);
        myTrackSectionComboBoxes.get(2).getSelectionModel()
                .select(TrackSpeed.FAST);
        myTrackSectionComboBoxes.get(3).getSelectionModel()
                .select(TrackSpeed.SLOW);
        myTrackSectionComboBoxes.get(4).getSelectionModel()
                .select(TrackSpeed.MEDIUM);
        myTrackSectionComboBoxes.get(5).getSelectionModel()
                .select(TrackSpeed.FAST);

        // Adding to trackSpeedConfigPane
        for (int i = 0; i < speedBracketList.size(); i++) {
            final ParticipantSpeed currentSpeed = speedBracketList.get(i);
            trackSpeedConfigPane.add(new SpeedConfigDisplay(currentSpeed,
                    (newSpeed, range) -> updateSpeedBracket(currentSpeed,
                            newSpeed, range)),
                    0, i);
        }

        // Set up track section speed config pane
        trackSectionSpeedConfigPane
                .add(new Text("Slow Track Section Multiplier: "), 0, 0);
        trackSectionSpeedConfigPane
                .add(new Text("Medium Track Section Multiplier: "), 0, 1);
        trackSectionSpeedConfigPane
                .add(new Text("Fast Track Section Multiplier: "), 0, 2);

        // Default values
        trackSpeedMultiplierFields.add(new TextField("0.5"));
        trackSpeedMultiplierFields.add(new TextField("1.0"));
        trackSpeedMultiplierFields.add(new TextField("1.5"));

        for (int i = 0; i < trackSpeedMultiplierFields.size(); i++) {
            trackSectionSpeedConfigPane.add(trackSpeedMultiplierFields.get(i),
                    1, i);
            trackSpeedMultiplierFields.get(i).setPrefColumnCount(4);
        }

        // Add to ConfigPane
        // Add a message about the tooltips
        Text tooltipMessage = new Text(
                "Hover over Text Fields/Sliders to see help and usage tips.\n"
                        + "If you click submit and nothing happens, one of the fields must be invalid. Have fun!");
        tooltipMessage.setFont(new Font(15));
        tooltipMessage.setStyle("-fx-fill: red;");
        tooltipMessage.textAlignmentProperty().set(TextAlignment.CENTER);
        GridPane tempPane = new GridPane();
        tempPane.getChildren().add(tooltipMessage);
        tempPane.setAlignment(Pos.CENTER);

        setTop(tempPane);
        configPane.getChildren().addAll(raceConfigPane, trackSpeedConfigPane);

        // First instantiate all the values in estimateTimes and add them to
        // their own
        // pane
        for (int i = 0; i < ParticipantSpeed.values().length; i++) {
            Text estimate = new Text("Estimated time for a "
                    + ParticipantSpeed.values()[i]
                    + " racer to complete a lap: "
                    + String.format("%.2f", myTrack.getTrackLength()
                            / ParticipantSpeed.values()[i].getVelocity() / 1000)
                    + " seconds");
            estimateTimes.put(ParticipantSpeed.values()[i], estimate);
            estTimePane.add(estimate, 0, i);
        }

        configPane.getChildren().add(estTimePane);
        configPane.getChildren().add(trackSectionConfigPane);
        configPane.getChildren().add(trackSectionSpeedConfigPane);

        configPane.setPrefHeight(500);
    }

    private void setUpGenerationControlPane() {
        // Formatting
        generationControlPane.setColumnHalignment(HPos.CENTER);
        generationControlPane.setAlignment(Pos.CENTER);
        generationControlPane.setHgap(10);
        generationControlPane.setVgap(10);
        generationControlPane.setPrefHeight(150);
        generationControlPane.setPadding(new Insets(10));

        progressBar.setVisible(false);

        generationControlPane.getChildren().addAll(fileDisplay,
                outputFileButton, submitRace, progressBar);
        setBottom(generationControlPane);
    }

//	private void addTrackSpeedAndRange(ParticipantSpeed bracket) {
//
//		configPane.add(new Text(bracket + " Speed"), 0);
//
//		TextField speedField = new TextField(Double.toString(bracket.getVelocity()));
//		speedField.setPrefWidth(50);
//		speedField.textProperty().addListener(new DoubleListener((i) -> updateSpeedBracket(bracket, i)));
//		speedFields.add(speedField);
//		configPane.add(speedField, 1);
//
//		configPane.add(new Text(bracket + " Range (+/-)"), 2);
//
//		TextField rangeField = new TextField(Double.toString(bracket.getRange()));
//		rangeField.setPrefWidth(50);
//		rangeField.textProperty().addListener(new DoubleListener((i) -> updateRangeBracket(bracket, i)));
//		rangeFields.add(rangeField);
//		configPane.add(rangeField, 3, );
//
//		configPane.add(new Text(bracket + " Est. Finish"), 4);
//
//		Text estimate = new Text(String.format("%.2f", controller.getLength() / bracket.getVelocity() / 1000));
//		estimateTimes.put(bracket, estimate);
//		configPane.add(estimate, 5);
//	}

    private void updateSpeedBracket(ParticipantSpeed speedBracket,
            double newSpeed, double newRange) {
        speedBracket.setVelocity(newSpeed);
        speedBracket.setRange(newRange);
        updateEstimate(speedBracket);
    }

    private void updateEstimate(ParticipantSpeed speedBracket) {
        estimateTimes.get(speedBracket).setText("Estimated time for a "
                + speedBracket + " racer to complete a lap: " + String
                        .format("%.2f",
                                myTrack.getTrackLength()
                                        / speedBracket.getVelocity() / 1000)
                + " seconds");
    }

    private void setUpParticipantPane() {
        // Instantiation
        myParticipantScrollPane = new ScrollPane();
        myParticipantPane = new GridPane();

        // Formatting
        myParticipantScrollPane.setPrefHeight(getHeight());

        // Adding
        myParticipantScrollPane.setContent(myParticipantPane);
        setRight(myParticipantScrollPane);
    }

//	private void updateSpeedBracketComboBox() {
//		int num = 0;
//		List<Node> boxes = new ArrayList<>();
//		for (Node n : myParticipantPane.getChildren()) {
//			if (n instanceof ComboBox) {
//				boxes.add(n);
//				num++;
//			}
//		}
//
//		myParticipantPane.getChildren().removeAll(boxes);
//
//		for (int i = 0; i < num; i++) {
//			ComboBox<String> cb = new ComboBox<>(getOptions());
//			cb.getSelectionModel().select(cb.getItems().size() / 2);
//			myParticipantPane.add(cb, 4, i);
//		}
//	}

    private void setSpeedBracketList() {
        speedBracketList = new ArrayList<>();
        // Eventually add this functionality back in, for now just 3 speeds.
//		switch (numSpeedBrackets) {
//		case 3:
//			speedBracketList.add(ParticipantSpeed.FAST);
//			speedBracketList.add(ParticipantSpeed.MEDIUM);
//			speedBracketList.add(ParticipantSpeed.SLOW);
//			break;
//		case 5:
//			speedBracketList.add(ParticipantSpeed.FASTER);
//			speedBracketList.add(ParticipantSpeed.FAST);
//			speedBracketList.add(ParticipantSpeed.MEDIUM);
//			speedBracketList.add(ParticipantSpeed.SLOW);
//			speedBracketList.add(ParticipantSpeed.SLOWER);
//			break;
//		case 7:
//			speedBracketList.add(ParticipantSpeed.FASTEST);
//			speedBracketList.add(ParticipantSpeed.FASTER);
//			speedBracketList.add(ParticipantSpeed.FAST);
//			speedBracketList.add(ParticipantSpeed.MEDIUM);
//			speedBracketList.add(ParticipantSpeed.SLOW);
//			speedBracketList.add(ParticipantSpeed.SLOWER);
//			speedBracketList.add(ParticipantSpeed.SLOWEST);
//		}
        speedBracketList.add(ParticipantSpeed.FAST);
        speedBracketList.add(ParticipantSpeed.MEDIUM);
        speedBracketList.add(ParticipantSpeed.SLOW);
    }

    private void updateParticipantPane() {
        participantDisplays.clear();
        myParticipantPane.getChildren().clear();

        for (int i = 0; i < numRacers; i++) {
            ParticipantDisplay newPartDisp = new ParticipantDisplay();
            participantDisplays.add(newPartDisp);
            myParticipantPane.add(newPartDisp, 0, i);
        }
        myParticipantPane.setPrefWidth(new ParticipantDisplay().getPrefWidth());
    }

//	private ObservableList<String> getOptions() {
//		return FXCollections.observableArrayList(
//				speedBracketList.stream().map((name) -> name.toString()).collect(Collectors.toList()));
//	}

//	private void resetSpeedBrackets() {
//		ParticipantSpeed.FASTEST.setVelocity(100);
//		ParticipantSpeed.FASTER.setVelocity(100);
//		ParticipantSpeed.FAST.setVelocity(110);
//		ParticipantSpeed.MEDIUM.setVelocity(100);
//		ParticipantSpeed.SLOW.setVelocity(90);
//		ParticipantSpeed.SLOWER.setVelocity(100);
//		ParticipantSpeed.SLOWEST.setVelocity(100);
//	}

//	private void updateRangeBracket(ParticipantSpeed speedBracket, double value) {
//		speedBracket.setRange(value);
//		System.out.println(speedBracket + " Range " + value);
//	}

//	private void setUpTrackView() {
//		try {
//			FXMLLoader loader = new FXMLLoader(OvalController.class.getResource("OvalController.fxml"));
//			innerPane.setCenter(loader.load());
//			setCenter(innerPane);
//			controller = loader.getController();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

    private void chooseFile() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialFileName(outputFile.getName());
        chooser.getExtensionFilters()
                .add(new ExtensionFilter("Race File", "*.rce"));
        File previousOutputFile = outputFile;
        outputFile = chooser.showSaveDialog(getScene().getWindow());
        try {
            fileDisplay.setText(outputFile.getCanonicalPath());
        } catch (Exception e) {
            outputFile = previousOutputFile;
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
        int raceActuallyOver = (int) Double
                .parseDouble(linesToWrite.get(i).split(":")[1]);

        // Because this is technically an index, add one to get the length
        raceActuallyOver++;

        // Modify the time to be this new value
        linesToWrite.set(5, "#TIME:" + raceActuallyOver);

//		System.out.println(raceActuallyOver);
    }

    private class TrackChangeListener implements ChangeListener<String> {

        @Override
        public void changed(ObservableValue<? extends String> observable,
                String oldValue, String newValue) {
            // We know something updated, so just create a new track
            if (validInteger(trackLengthField.textProperty().get())
                    && validInteger(xRatioField.textProperty().get())
                    && validInteger(yRatioField.textProperty().get())) {
                myTrack = new OvalTrack(
                        Integer.parseInt(trackLengthField.textProperty().get()),
                        Integer.parseInt(xRatioField.textProperty().get()),
                        Integer.parseInt(yRatioField.textProperty().get()));

                // Update estimates for all speed brackets (only matters if
                // track length
                // changed)
                for (ParticipantSpeed ps : speedBracketList) {
                    updateEstimate(ps);
                }
            }
        }

        private boolean validInteger(String string) {
            try {
                Integer.parseInt(string);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

    }

    private class SimTask extends Task<Void> {
        @Override
        protected Void call() throws Exception {
            try {
                int telemetryInterval = (int) telemetryIntervalSlider
                        .getValue();
                List<Participant> participants = new ArrayList<>();
//				List<Integer> ids = participantIdFields.stream()
//						.map((id) -> Integer.parseInt(id.textProperty().getValue())).collect(Collectors.toList());
//				List<String> names = participantNameFields.stream().map((name) -> name.textProperty().getValue())
//						.collect(Collectors.toList());

                double start = 0;
                // System.out.println(speedBracket);
                // System.out.println(rangeBracket);
                for (ParticipantDisplay pd : participantDisplays) {
                    Participant p = new Participant(pd.getID(), pd.getName(),
                            start, myTrack.getTrackLength(), pd.getSpeed());
                    p.calculateNextVelocity();
                    participants.add(p);
                    start -= myTrack.getTrackLength() * 0.01;
                }

                // Get speeds from combo boxes
                List<TrackSpeed> trackSpeeds = new ArrayList<>();
                for (int i = 0; i < 6; i++) {
                    trackSpeeds.add(myTrackSectionComboBoxes.get(i)
                            .getSelectionModel().getSelectedItem());
                }
                myTrack.setSections(trackSpeeds);

                // Get multipliers from TextFields
                // This is bad, program will do nothing on submit if these
                // fields are wrong.
                TrackSpeed.SLOW.setMultiplier(
                        Double.parseDouble(trackSpeedMultiplierFields.get(0)
                                .textProperty().get()));
                TrackSpeed.MEDIUM.setMultiplier(
                        Double.parseDouble(trackSpeedMultiplierFields.get(1)
                                .textProperty().get()));
                TrackSpeed.FAST.setMultiplier(
                        Double.parseDouble(trackSpeedMultiplierFields.get(2)
                                .textProperty().get()));

                // The expected time is roughly the speed of the slowest
                // participant.
                int expectedTime = myTrack.getTrackLength()
                        / (int) ParticipantSpeed.SLOW.getVelocity();
                linesToWrite.clear();

                int currentTime = 0;
                linesToWrite.add("#RACE:" + raceNameField.getText());
                linesToWrite.add("#TRACK:" + myTrack.getTrackName());

                // TODO These are hard coded values, make things in the UI to
                // change this.
                linesToWrite.add("#WIDTH:" + myTrack.getWidthRatio());
                linesToWrite.add("#HEIGHT:" + myTrack.getHeightRatio());

                linesToWrite.add("#DISTANCE:" + myTrack.getTrackLength());
                linesToWrite.add("#TIME:" + expectedTime);
                linesToWrite.add("#PARTICIPANTS:" + numRacers);

                Race race = new Race(myTrack, numLaps, telemetryInterval,
                        participants);
                while (race.stillGoing()) {
//					System.out.println("going");
                    race.stepRace().forEach(linesToWrite::add);
                    currentTime++;
                    updateProgress(currentTime, expectedTime);

                }

                adjustForLastRacer();

//				System.out.println("Right before writing file");
                try (PrintWriter pw = new PrintWriter(outputFile)) {
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
