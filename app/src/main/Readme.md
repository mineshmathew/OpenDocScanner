
OpenDocScanner
An opensource Document Scanner For Android Using OpenCV

The app lets you either capture or browse an image, correct its persepctive

Most of the code base is from here https://github.com/biokys/cropimage. Trapezoidal cropping part is taken from Textfairy app ; https://github.com/renard314/textfairy

The app requires OpenCV manager to run. (Opencv is not statistically linked; you need to have OpenCV manager installed in your device)

For developers : In android studio you can statistically link the opencv to the project by following the steps here . http://stackoverflow.com/questions/27406303/opencv-in-android-studio

Features: 1. If the corners of the document is visible, the app will automatically detect the corners and correct the perspective accordingly OpenCV's powerful edge detection method helps to detect the corners perfectly even when the background is not very distinctive in color

The image is binarized, using Otsus thresholding so that it would work well with OCRs
