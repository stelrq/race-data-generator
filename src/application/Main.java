package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.Controller;

/**
 * 
 * @author Myles Haynes
 */
public class Main extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("305 Race Builder");
		Controller c = new Controller();
		Scene scene = new Scene(c);
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
