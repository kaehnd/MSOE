package kaehnd;

import javafx.application.Application;
import javafx.application.Preloader;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Temporary test application for Meal Plan Monitor classes
 */
public class SimpleFXMSOEMealPlanMonitor extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("MSOEMealPlanMonitor.fxml"));
        Parent root = loader.load();


        primaryStage.setTitle("MSOE Meal Plan Monitor");

        //Create and show progress indicator

        HBox loadPane = new HBox();

        ProgressIndicator progressIndicator = new ProgressIndicator();

        loadPane.getChildren().add(progressIndicator);
        Scene loadScene = new Scene(loadPane, 400, 300);
        primaryStage.setScene(loadScene);
        loadPane.setAlignment(Pos.CENTER);
        primaryStage.show();

        ((JavaFXController) loader.getController()).build();

        primaryStage.setScene(new Scene(root, 1800 , 800));
        primaryStage.setMaximized(true);





//        Pane pane = (Pane) (primaryStage.getScene().getRoot().getChildrenUnmodifiable().get(0));
//        System.out.println(pane.getWidth());
//        System.out.println(pane.getHeight());
    }


    public static void main(String[] args) {
        launch(args);
    }

}
