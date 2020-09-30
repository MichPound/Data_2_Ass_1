package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    public static Stage primaryStage;
    public static Scene main, colors, original;
    public static Image image;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Pixel");
        Parent root = FXMLLoader.load(getClass().getResource("1.fxml"));
        main = new Scene(root, 1000, 625);

        Parent root1 = FXMLLoader.load(getClass().getResource("2.fxml"));
        colors = new Scene(root1, 600, 625);

        Parent root2 = FXMLLoader.load(getClass().getResource("3.fxml"));
        original = new Scene(root2, 1920, 1055);

        primaryStage.setScene(main);
        primaryStage.show();
    }

    public static void openChannel(String name) {
        Stage color = new Stage();
        color.setTitle(name);
        color.setScene(colors);
        color.show();
        Controller2.controller2.colorChannel(name);
    }

    //changing channels
    public static void openOriginal() {
        Stage origin = new Stage();
        origin.setTitle("Original");
        origin.setScene(original);
        origin.show();
        Controller3.controller3.originalImage();
    }

    public static void fileChooser() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            File file = fileChooser.showOpenDialog(primaryStage);
            image = new Image(file.toURI().toString(), 400, 400, false, false);
            Controller.controller.redData = new int[(int)image.getWidth()*(int)image.getHeight()];
            Controller.controller.purpleData = new int[(int)image.getWidth()*(int)image.getHeight()];
            Controller.controller.image.setImage(image);
            Controller.controller.red.setImage(image);
            Controller.controller.purple.setImage(image);
            Controller.controller.white.setImage(image);
            Controller.controller.lowEnd.setValue(130);
            Controller.controller.highEnd.setValue(220);
            Controller.controller.setColor(image);
            Controller.controller.setInfo(image, Controller.controller.name, Controller.controller.size);
            Controller.controller.reset();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("File Error");
            alert.setContentText("Please Select a file again");
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
