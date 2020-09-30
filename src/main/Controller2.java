package main;

import javafx.scene.image.ImageView;

public class Controller2 {
    public static Controller2 controller2;

    public ImageView image2;

    //loads up image dependent on its color
    public void colorChannel(String name) {
        if (name.equals("red")) {
            image2.setImage(Controller.controller.red.getImage());
        } else if (name.equals("purple")) {
            image2.setImage(Controller.controller.purple.getImage());
        } else {
            image2.setImage(Controller.controller.white.getImage());
        }
    }

    //allows access to this controller
    public void initialize() {
        controller2 = this;
    }
}
