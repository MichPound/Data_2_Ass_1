package main;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Controller {
    public static Controller controller;
    public Slider reduction, lowEnd, highEnd;
    public ImageView image, image2, red, purple, white;
    public MenuItem name, size;
    public AnchorPane anchor, anchor2;
    public Label countRed, countWhite, countTotal, countCluster;
    public RadioButton visible1, dark, originalImage;

    HashSet<Integer> redRoots = new HashSet<>(), purpleRoots = new HashSet<>();
    HashMap<Integer, Integer> redSize = new HashMap<>(), purpleSize = new HashMap<>();
    WritableImage resultAll;
    int[] redData, purpleData;
    int index = 1;
    double average = 0;

    //sets color for different views dependent on sliders
    public void setColor(Image sourceImage) {
        PixelReader pr = sourceImage.getPixelReader();
        int width = (int) sourceImage.getWidth();
        int height = (int) sourceImage.getHeight();

        WritableImage resultR = new WritableImage(width, height);
        PixelWriter pwR = resultR.getPixelWriter();
        WritableImage resultP = new WritableImage(width, height);
        PixelWriter pwP = resultP.getPixelWriter();
        WritableImage resultW = new WritableImage(width, height);
        PixelWriter pwW = resultW.getPixelWriter();
        resultAll = new WritableImage(width, height);
        PixelWriter pwAll = resultAll.getPixelWriter();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color col = pr.getColor(x, y);
                if ((((col.getRed() * 255) <= lowEnd.getValue()))) {
                    pwP.setColor(x, y, new Color(0.4, 0.2, 0.8, 1.0));
                    pwAll.setColor(x, y, new Color(0.4, 0.2, 0.8, 1.0));
                } else {
                    pwP.setColor(x, y, new Color(1, 1, 1, 1.0));
                    pwAll.setColor(x, y, new Color(1, 1, 1, 1.0));
                }

                if ((((col.getRed() * 255) > lowEnd.getValue())) && (((col.getRed() * 255) <= highEnd.getValue()))) {
                    pwR.setColor(x, y, new Color(1, 0, 0, 1.0));
                    pwAll.setColor(x, y, new Color(1, 0, 0, 1.0));
                } else {
                    pwR.setColor(x, y, new Color(1, 1, 1, 1.0));
                }

                if ((col.getRed() * 255 > highEnd.getValue())) {
                    pwW.setColor(x, y, new Color(1, 1, 1, 1.0));
                    pwAll.setColor(x, y, new Color(1, 1, 1, 1.0));
                } else {
                    pwW.setColor(x, y, new Color(col.getRed(), 0, 0, 1.0));
                }
            }
        }
        red.setImage(resultR);
        purple.setImage(resultP);
        white.setImage(resultW);
        image2.setImage(resultAll);
    }

    //greyscales the image
    public void greyScale(ActionEvent actionEvent) {
        double r, g, b;
        Image image2 = Main.image;
        PixelReader pixelReader = image2.getPixelReader();
        WritableImage wImage = new WritableImage(
                (int) image2.getWidth(),
                (int) image2.getHeight());
        PixelWriter pixelWriter = wImage.getPixelWriter();
        for (int y = 0; y < image2.getHeight(); y++) {
            for (int x = 0; x < image2.getWidth(); x++) {
                Color color = pixelReader.getColor(x, y);
                r = color.getRed();
                g = color.getGreen();
                b = color.getBlue();
                int rgb = (int) ((r + g + b) / 3 * 255);
                color = Color.rgb(rgb, rgb, rgb);
                pixelWriter.setColor(x, y, color);
            }
        }
        image.setImage(wImage);
    }

    //builds arrays of pixel numbers for each color
    public void build(Image sourceImage, int[] red, int[] purple) {
        PixelReader pixelReader = sourceImage.getPixelReader();

        int height = (int) sourceImage.getHeight();
        int width = (int) sourceImage.getWidth();

        int i = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (pixelReader.getColor(x, y).equals(new Color(1, 0, 0, 1.0))) {
                    red[i] = i;
                    purple[i] = -1;

                } else if (pixelReader.getColor(x, y).equals(new Color(0.4, 0.2, 0.8, 1.0))) {
                    red[i] = -1;
                    purple[i] = i;

                } else {
                    red[i] = -1;
                    purple[i] = -1;

                }
                i++;
            }
        }
    }

    //unions pixels in arrays and adds cell roots to hashsets
    public void unionCheck(int[] a, HashSet<Integer> b, Image sourceImage) {
        int width = (int) sourceImage.getWidth();

        for (int i = 0; i < a.length - 1; i++) {
            if ((a[i] != -1)) {
                if ((a[i + 1] != -1) && (i % width != 0)) {
                    Union.union(a, a[i], a[i + 1]);
                }
                if ((i + width < a.length) && (a[i + width] != -1)) {
                    Union.union(a, a[i], a[i + width]);
                }
            }
        }
        //checking amount of cells in the picture
        for (int i : a) {
            if (i != -1)
                b.add(Union.find(a, i));
        }
    }

    //making hashmaps that store cell sizes for roots
    public void generateMaps(int[] a, HashSet<Integer> b, HashMap<Integer, Integer> cellSize) {
        int size;
        for (int cell : b) {
            size = 0;

            for (int j = 0; j < a.length; j++) {
                if (a[j] != -1 && Union.find(a, j) == cell) {
                    size++;
                }
            }
            cellSize.put(cell, size);
        }
    }

    //removes cells under certain size from hashset of roots and paints white
    public void noiseReduction(int[] a, HashSet<Integer> b, HashMap<Integer, Integer> cellSize, double slider, WritableImage reduceImage) {
        int width = (int) reduceImage.getWidth();
        WritableImage reduced;

        for (int i = 0; i < a.length; i++) {
            if (a[i] != -1 && cellSize.get(Union.find(a, i)) < slider) {//slider
                b.remove(Union.find(a, i));
                reduced = reduceImage;
                reduced.getPixelWriter().setColor(i % width, i / width, new Color(1, 1, 1, 1.0));
                reduceImage = reduced;
            }
        }
    }

    //calculates average, clusters and sets cell info
    public void count() {
        double temp = 0;
        int extra = 0;
        int clusterCount = 0;

        //calculate average
        for (int cell : redRoots) {
            temp += redSize.get(cell);
        }

        average = temp / redRoots.size();

        //calculate amount of clusters
        for (int cell : redRoots) {
            if (redSize.get(cell) > 1.35 * average) {
                extra += redSize.get(cell) / average;
                clusterCount++;
            }
        }

        countCluster.setText(": " + clusterCount);
        countRed.setText(": " + (redRoots.size() - clusterCount + extra));
        countWhite.setText(": " + purpleRoots.size());
        countTotal.setText(": " + (redRoots.size() - clusterCount + extra + purpleRoots.size()));
    }

    //calculates and draw rectangles around each cell
    public void draw(int[] a, HashSet<Integer> b, Color c, HashMap<Integer, Integer> size, double avg, ImageView imgV, AnchorPane pane) {
        int lastIndex, width = (int) imgV.getImage().getWidth();
        double x = 0, y = 0, w = 0, h = 0;
        boolean firstHeight = false;
        Tooltip tooltip;

        for (int cell : b) {
            x = cell % width;
            w = 0;
            h = 0;
            lastIndex = 0;

            for (int i = 0; i < a.length; i++) {
                if (a[i] != -1 && Union.find(a, i) == cell && i % width != 0) {

                    if ((i % width) < x) {
                        x = (i % width);
                    }

                    if (!firstHeight) {
                        y = ((double) i / width);
                        if (y > 0) {
                            y -= 1;
                        }
                        firstHeight = true;
                    }

                    if ((i % width) - x > w) {
                        w = (i % width) - x;
                    }

                    lastIndex = i;
                } else if (i % width == 0 && lastIndex > i - width) {
                    h++;
                }
            }
            firstHeight = false;

            Rectangle rectangle = new Rectangle(x, y, w + 2, h);
            rectangle.setFill(Color.rgb(0, 0, 0, 0));

            if (c == Color.GREEN && size.get(cell) > 1.35 * avg) {//red blood cells
                rectangle.setStroke(Color.BLUE);
                if (Math.round(size.get(cell) / avg) > 1) {
                    tooltip = new Tooltip("Estimated number of cells: " + Math.round(size.get(cell) / avg));
                } else {
                    tooltip = new Tooltip("Estimated number of cells: 2");
                }
            } else {
                rectangle.setStroke(c);
                tooltip = new Tooltip("Estimated number of cells: 1");
            }
            Tooltip.install(rectangle, tooltip);
            pane.getChildren().add(rectangle);
        }
    }

    //numbers cell rectangles from top to bottom
    public void sequentialNumbers(int num, AnchorPane pane, AnchorPane pane2) {
        ArrayList<Rectangle> check = new ArrayList<>();

        for (Node node : pane.getChildren()) {
            check.add(((Rectangle) node));
        }
        check.sort((x, y) -> (int) (x.getY() - y.getY()));

        for (Rectangle rectangle : check) {
            double x = rectangle.getX(), y = rectangle.getY(), h = rectangle.getHeight();

            Label label = new Label();
            label.setLayoutX(x);
            label.setLayoutY(y + h - 14);
            label.setText(String.valueOf(num));
            num++;
            pane2.getChildren().add(label);
        }
    }

    //sets 2nd anchor pane with numbers to visible/invisible
    public void visible2(ActionEvent actionEvent) {
        if (visible1.isSelected()) {
            anchor2.setVisible(false);
        } else {
            anchor2.setVisible(true);
        }
    }

    //updates color on slider update
    public void colorCorrection(MouseEvent mouseEvent) {
        setColor(Main.image);
        originalImage.setSelected(false);
    }

    //changes to a darker/lighter color selection
    public void darker(ActionEvent actionEvent) {
        if (dark.isSelected()) {
            lowEnd.setMin(30);
            lowEnd.setMax(100);
            highEnd.setMin(100);
            highEnd.setMax(200);
        } else {
            lowEnd.setMin(60);
            lowEnd.setMax(200);
            highEnd.setMin(200);
            highEnd.setMax(245);
        }
        setColor(Main.image);
    }

    //toggles between original image and image used for calculations
    public void setOriginal(ActionEvent actionEvent) {
        if (originalImage.isSelected()) {
            image2.setImage(Main.image);
        } else {
            image2.setImage(resultAll);
        }
    }

    //calls methods needed for process
    public void process(ActionEvent actionEvent) {
        build(resultAll, redData, purpleData);
        redRoots.clear();
        purpleRoots.clear();
        unionCheck(redData, redRoots, image.getImage());
        unionCheck(purpleData, purpleRoots, image.getImage());
        generateMaps(redData, redRoots, redSize);
        generateMaps(purpleData, purpleRoots, purpleSize);
        noiseReduction(redData, redRoots, redSize, reduction.getValue(), resultAll);
        noiseReduction(purpleData, purpleRoots, purpleSize, reduction.getValue(), resultAll);
        anchor.getChildren().clear();
        anchor2.getChildren().clear();
    }

    //calls methods needed for calculations
    public void calculate(ActionEvent actionEvent) {
        average = 0;
        count();
        anchor.getChildren().clear();
        anchor2.getChildren().clear();
        draw(redData, redRoots, Color.GREEN, redSize, average, image, anchor);
        draw(purpleData, purpleRoots, Color.PURPLE, purpleSize, average, image, anchor);
        index = 1;
        sequentialNumbers(index, anchor, anchor2);
    }

    //reloads original image to be reprocessed
    public void reload(ActionEvent actionEvent) {
        setColor(Main.image);
        originalImage.setSelected(false);
    }

    //reset variables for new image input
    public void reset() {
        image.setImage(Main.image);
        anchor.getChildren().clear();
        anchor2.getChildren().clear();
        average = 0;
        redRoots.clear();
        purpleRoots.clear();
        redSize.clear();
        purpleSize.clear();
        index = 1;
        originalImage.setSelected(false);
    }

    //allows for image selection
    public void openFileExplorer(ActionEvent actionEvent) {
        Main.fileChooser();
    }

    //set image info
    public void setInfo(Image sourceImage, MenuItem setName, MenuItem setSize) {
        setName.setText(sourceImage.getUrl());
        setSize.setText(sourceImage.getWidth() + "x" + sourceImage.getHeight());
    }

    //red image view
    public void redChannel(ActionEvent actionEvent) {
        Main.openChannel("red");
    }

    //purple image view
    public void purpleChannel(ActionEvent actionEvent) {
        Main.openChannel("purple");
    }

    //white image view
    public void whiteChannel(ActionEvent actionEvent) {
        Main.openChannel("white");
    }

    //original image view
    public void originImage(ActionEvent actionEvent) {
        Main.openOriginal();
    }

    //exits application
    public void exit(ActionEvent actionEvent) {
        Platform.exit();
    }

    //allows access to this controller
    public void initialize() {
        controller = this;
    }
}
