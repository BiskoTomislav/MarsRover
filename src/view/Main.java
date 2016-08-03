package view;
	
import controller.ViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class Main extends Application {
	private static final int MAX_HEIGHT = 590;
	private static final int MAX_WIDTH = 590;
	
	private Stage primaryStage;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			AnchorPane root = new AnchorPane();

			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();

			loader.setLocation(Main.class.getResource("SatelliteView.fxml"));
			root = (AnchorPane) loader.load();
			
			// Give the controller access to the main app.
			ViewController controller = loader.getController();
			controller.setMain(this);
			
			Scene scene = new Scene(root, MAX_WIDTH, MAX_HEIGHT);

			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			primaryStage.setTitle("Satellite link to robot on Mars");
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();
			
			this.setPrimaryStage(primaryStage);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
}
