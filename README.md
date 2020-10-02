# Datastructures_2_Assignment_1

The aim for this assignment was to make a system that the user could load a blood cell image for analysis.
This system requires that the image be a prepared stained slide that would be used under a microscope.
Once the image has been selected through the file explorer some basic colour manipulation is done to produce a 
simple image that only contains red, purple and white pixels. Depending on what stain was used the colour manipulation
might not have correctly identified the different cells for this image, I have included two sliders which control the colour 
ranges for red and purple pixels, this allows the user to manually adjust the colour ranges to better suit the image used.

![alt text](https://github.com/MichPound/Datastructures_2_Assignment_1/commit/d2c22cd8ce84fe2f86ce03d46684c0e5807c5fe2)

Once this is done the image is scanned and the system will individually mark each cell or cell cluster with a rectangle, 
the colour of the rectangle is dependent on whether it is a red/white blood cell or if it is a cluster of cells. The system
displays information gathered in the gui and tooltip is installed on each rectangle, this gives the ability to hover
over the rectangle and the system will estimate how many cells are contained in that rectangle. If the image is noisy there is a 
slider and the user can adjust the minimum possible size of a cell, this will help to remove any noise from the image and allow 
for more accurate readings.
