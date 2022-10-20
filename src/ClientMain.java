import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class ClientMain extends Application{

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		ClientPane root = new ClientPane(primaryStage);
		Scene scene = new Scene(root, 850,700);
		primaryStage.setTitle("Snake Image categorizer");
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}

}
