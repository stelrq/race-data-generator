package view;

import java.util.Random;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import model.ParticipantSpeed;

public class ParticipantDisplay extends GridPane {

	private static final Random myRandom = new Random();
	
	private static final String[] DEFAULT_NAMES = { "Rebecka", "Lexie", "Betsey", "Elane", "Miss", "Daniele", "Angele",
			"Aja", "Floretta", "Patrice", "Jamison", "Myles", "Sally", "Viola", "Delicia", "Dwain", "Alfredia", "Mina",
			"Charlena", "Catheryn", "Bud", "Suellen", "Herbert", "Adelaida", "Carmine", "Sylvie", "Fawn", "Nathanial",
			"Gertrudis", "Marcellus", "Catrina", "Emelia", "Aliza", "Julieann", "Ronni", "Michael", "Bridgett", "Shira",
			"Evia", "Lorine", "Blanche", "Carita", "Perry", "Isela", "Yelena", "Florrie", "Angelica", "Kiyoko", "Kelli",
			"Stacie" };

	/** The lower bound for random Participant IDs (inclusive) */
	private static final int DEFAULT_ID_LOWER = 1;

	/** The upper bound for random Participant IDs (inclusive) */
	private static final int DEFAULT_ID_UPPER = 99;

	private TextField myRacerIDField;
	private TextField myNameTextField;
	private ComboBox<String> mySpeedComboBox;

	
	public ParticipantDisplay() {
		// Instantiation / setting default random values
		myNameTextField = new TextField(DEFAULT_NAMES[myRandom.nextInt(DEFAULT_NAMES.length)]);
		
		myRacerIDField = new TextField(
				((Integer) (myRandom.nextInt(DEFAULT_ID_UPPER - 1) + DEFAULT_ID_LOWER)).toString());
		ObservableList<String> options = FXCollections.observableArrayList();
		
		for (int i = 0; i < ParticipantSpeed.values().length; i++) {
			options.add(ParticipantSpeed.values()[i].prettyString());
		}
		mySpeedComboBox = new ComboBox<String>(options);
		mySpeedComboBox.getSelectionModel().select(myRandom.nextInt(options.size()));;		
		
		// Formatting
		myRacerIDField.setPrefColumnCount(2);
		myNameTextField.setPrefColumnCount(10);
		setMargin(myNameTextField, new Insets(0, 5, 0, 5));
		setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);

		// Add to the GridPane
		add(myRacerIDField, 0, 0);
		add(myNameTextField, 1, 0);
		add(mySpeedComboBox, 2, 0);
	}
	
	public String getName() {
		return myNameTextField.textProperty().get();
	}
	
	public int getID() {
		return Integer.parseInt(myRacerIDField.getText());
	}
	
	public String getSpeed() {
		return mySpeedComboBox.getSelectionModel().getSelectedItem();
	}

}
