package main;

import javafx.scene.image.ImageView;

public class Controller3 {
    public static Controller3 controller3;

    public ImageView originalImage;

    //loads up original image full screen
    public void originalImage() {
        originalImage.setImage(Controller.controller.image.getImage());
    }

    //allows access to this controller
    public void initialize() {
        controller3 = this;
    }
}
