package main;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ControllerTest {

    Controller testController = new Controller();
    JFXPanel jfxPanel = new JFXPanel();
    Image rImage, pImage, wImage;
    WritableImage result;
    ImageView imageView = new ImageView();
    AnchorPane pane = new AnchorPane(), pane2 = new AnchorPane();
    MenuItem name = new MenuItem("0"), size = new MenuItem("0");
    Slider slider = new Slider();
    int[] redData = new int[400 * 400], purpleData = new int[400 * 400];
    HashSet<Integer> testA = new HashSet<>();
    HashMap<Integer, Integer> redSize = new HashMap<>();
    int index;

    @BeforeEach
    void setUp() {
        File rFile = new File("C:\\Users\\michw\\OneDrive\\Pictures\\red.png");
        rImage = new Image(rFile.toURI().toString(), 400, 400, false, false);

        File pFile = new File("C:\\Users\\michw\\OneDrive\\Pictures\\purple.png");
        pImage = new Image(pFile.toURI().toString(), 400, 400, false, false);

        File wFile = new File("C:\\Users\\michw\\OneDrive\\Pictures\\white.png");
        wImage = new Image(wFile.toURI().toString(), 400, 400, false, false);

        result = new WritableImage(400, 400);
        slider.setMax(400 * 400 + 2);
        imageView.setImage(rImage);
        index = 1;
    }

    @AfterEach
    void tearDown() {
        redData = null;
        purpleData = null;
        testA.clear();
        redSize.clear();
    }

    @Test
    void buildRedArray() {
        testController.build(rImage, redData, purpleData);
        for (int a : redData) {
            assertTrue(a != -1);
        }
    }

    @Test
    void buildPurpleArray() {
        testController.build(pImage, redData, purpleData);
        for (int a : purpleData) {
            assertTrue(a != -1);
        }
    }

    @Test
    void notWhite() {
        testController.build(wImage, redData, purpleData);
        for (int a : redData) {
            assertEquals(a, -1);
        }
        for (int a : purpleData) {
            assertEquals(a, -1);
        }
    }

    @Test
    void unionCheck() {
        testController.unionCheck(redData, testA, rImage);
        assertEquals(1, testA.size());
    }

    @Test
    void generateMaps() {
        testController.build(rImage, redData, purpleData);
        testController.unionCheck(redData, testA, rImage);
        testController.generateMaps(redData, testA, redSize);
        assertEquals((int) redSize.get(1), 400 * 400);
    }

    @Test
    void noiseReductionLessThan() {
        testController.build(rImage, redData, purpleData);
        testController.unionCheck(redData, testA, rImage);
        testController.generateMaps(redData, testA, redSize);

        slider.setValue(50);
        testController.noiseReduction(redData, testA, redSize, slider.getValue(), result);
        assertEquals(1, testA.size());
    }

    @Test
    void noiseReductionMoreThan() {
        testController.build(rImage, redData, purpleData);
        testController.unionCheck(redData, testA, rImage);
        testController.generateMaps(redData, testA, redSize);

        slider.setValue(400 * 400 + 1);
        testController.noiseReduction(redData, testA, redSize, slider.getValue(), result);
        assertEquals(0, testA.size());
    }

    @Test
    void draw() {
        assertEquals(0, pane.getChildren().size(), "Checking no children stored");

        testController.build(rImage, redData, purpleData);
        testController.unionCheck(redData, testA, rImage);
        testController.generateMaps(redData, testA, redSize);
        testController.draw(redData, testA, Color.GREEN, redSize, redSize.get(1), imageView, pane);

        assertEquals(1, pane.getChildren().size(), "Checking child has been successfully added");
    }

    @Test
    void sequentialNumbers() {
        assertEquals(0, pane2.getChildren().size(), "Checking no children stored");

        testController.build(rImage, redData, purpleData);
        testController.unionCheck(redData, testA, rImage);
        testController.generateMaps(redData, testA, redSize);
        testController.draw(redData, testA, Color.GREEN, redSize, redSize.get(1), imageView, pane);
        testController.sequentialNumbers(index, pane, pane2);

        assertEquals(1, pane2.getChildren().size(), "Checking child has been successfully added");
    }

    @Test
    void setInfo() {
        testController.setInfo(rImage, name, size);
        assertEquals(name.getText(), rImage.getUrl());
        assertEquals(size.getText(), rImage.getWidth() + "x" + rImage.getHeight());
    }
}