package view.track;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import model.track.OvalTrack;
import model.track.Track;
import view.TrackController;
import view.util.IntListener;

import static java.lang.String.format;

/**
 * 
 * @author Myles Haynes
 */
public class OvalController extends TrackController {

	private int myLength;

	@FXML
	private TextField distanceField;
	
	@FXML
	private TextField xRatioField;
	
	@FXML
	private TextField yRatioField;

	@FXML
	private Text widthLabel;

	@FXML
	private Text heightLabel;
	
	private int xRatio;
	private int yRatio;
	private Track track;

	@FXML
	public void initialize() {
		xRatio = Integer.parseInt(xRatioField.textProperty().getValue());
		yRatio = Integer.parseInt(yRatioField.textProperty().getValue());
		myLength = Integer.parseInt(distanceField.textProperty().getValue());
		track = new OvalTrack(myLength,xRatio,yRatio);
		onChange();
		distanceField.textProperty().addListener(new IntListener(i -> {
			myLength = i;
			onChange();
		}));
		xRatioField.textProperty().addListener(new IntListener(i -> {
			xRatio = i;
			onChange();
		}));
		yRatioField.textProperty().addListener(new IntListener(i -> {
			yRatio = i;
			onChange();
		}));
		
	}

	@Override
	public Track getTrack() {
		return track;
	}
	
	public int getLength() {
		return myLength;
	}
	
	private void onChange() {
		track = new OvalTrack(myLength, xRatio, yRatio);
		widthLabel.setText(format("%.0f", track.getWidth()));
		heightLabel.setText(format("%.0f", track.getHeight()));
	}

}
