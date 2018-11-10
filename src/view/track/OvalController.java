package view.track;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import model.Track;
import model.track.OvalTrack;
import view.TrackController;
import view.util.IntListener;

import static java.lang.String.format;

/**
 * 
 * @author Myles Haynes
 */
public class OvalController extends TrackController {

	private int distance;

	@FXML
	private TextField distanceField;

	@FXML
	private Text widthLabel;

	@FXML
	private Text heightLabel;

	@FXML
	public void initialize() {
		onDistanceChange(Integer.parseInt(distanceField.textProperty().getValue()));
		distanceField.textProperty().addListener(new IntListener(this::onDistanceChange));

	}

	@Override
	public Track getTrack() {
		return new OvalTrack(distance);
	}

	private void onDistanceChange(int dist) {
		distance = dist;
		double width = OvalTrack.getWidth(distance);
		double height = OvalTrack.getHeight(distance);

		widthLabel.setText(format("%.0f", width));
		heightLabel.setText(format("%.0f", height));
	}

}
