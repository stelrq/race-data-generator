package view;

import java.util.function.BiConsumer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import model.ParticipantSpeed;

public class SpeedConfigDisplay extends GridPane {

    private BiConsumer<Double, Double> myBiConsumer;

    private TextField velocityField;

    private TextField rangeField;

    private Text speedText;

    private Text rangeText;

    // theConsumer is called when any change is made to the fields in
    // SpeedConfigDisplay
    public SpeedConfigDisplay(ParticipantSpeed participantSpeed,
            BiConsumer<Double, Double> theConsumer) {
        super();
        myBiConsumer = theConsumer;

        // Instantiation
        velocityField = new TextField(
                Double.toString(participantSpeed.getVelocity()));
        rangeField = new TextField(
                Double.toString(participantSpeed.getRange()));
        speedText = new Text(participantSpeed.toString());
        Text bracketText = new Text(
                participantSpeed.toString() + " Participant Settings");

        // Formatting
        velocityField.setPrefColumnCount(4);
        rangeField.setPrefColumnCount(4);
        setHalignment(bracketText, HPos.CENTER);
        bracketText.setFont(new Font(15));
        setPadding(new Insets(5));
        setHgap(3);
        velocityField.setTooltip(new Tooltip(
                "The base speed of the participants in this speed bracket class in track length units per millisecond"));
        rangeField.setTooltip(new Tooltip(
                "The variation allowed in the velocity, participants randomly select a speed for each track section within\n"
                        + "Speed - Variability and Speed + Variability\n"
                        + "This basically controls how volatile a participant is, higher variability means the participant can get really lucky and win by a\n"
                        + "mile or get very unlucky and lose to everyone or everything could even out and they finish about where they should given their base speed\n"
                        + "NOTE: Speed must be greater than variability (no moving backwards!)"));

        // Add Listeners
        FieldChangeListener fcl = new FieldChangeListener();
        velocityField.textProperty().addListener(fcl);
        rangeField.textProperty().addListener(fcl);
        velocityField.setPrefColumnCount(5);
        rangeField.setPrefColumnCount(5);

        // Adding
        add(bracketText, 0, 0, 4, 1);
        add(new Text("Speed:"), 0, 1);
        add(velocityField, 1, 1);
        add(new Text("Variability(+/-):"), 2, 1);
        add(rangeField, 3, 1);
    }

    class FieldChangeListener implements ChangeListener<String> {

        @Override
        public void changed(ObservableValue<? extends String> obsVal,
                String oldString, String newString) {
            // We don't care about the new and old values, we know we want to
            // try and send
            // the updated velocity and range regardless
            if (validDouble(velocityField.textProperty().get())
                    && validDouble(rangeField.textProperty().get())) {
                myBiConsumer.accept(
                        Double.parseDouble(velocityField.textProperty().get()),
                        Double.parseDouble(rangeField.textProperty().get()));
            }
        }

        private boolean validDouble(String theString) {
            try {
                Double.parseDouble(theString);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

    }

//	configPane.add(new Text(bracket + " Speed"), 0);
//
//	TextField speedField = new TextField(Double.toString(bracket.getVelocity()));
//	speedField.setPrefWidth(50);
//	speedField.textProperty().addListener(new DoubleListener((i) -> updateSpeedBracket(bracket, i)));
//	speedFields.add(speedField);
//	configPane.add(speedField, 1);
//
//	configPane.add(new Text(bracket + " Range (+/-)"), 2);
//
//	TextField rangeField = new TextField(Double.toString(bracket.getRange()));
//	rangeField.setPrefWidth(50);
//	rangeField.textProperty().addListener(new DoubleListener((i) -> updateRangeBracket(bracket, i)));
//	rangeFields.add(rangeField);
//	configPane.add(rangeField, 3, );
//
//	configPane.add(new Text(bracket + " Est. Finish"), 4);
//
//	Text estimate = new Text(String.format("%.2f", controller.getLength() / bracket.getVelocity() / 1000));
//	estimateTimes.put(bracket, estimate);
//	configPane.add(estimate, 5);
}
