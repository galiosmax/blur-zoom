# blur-zoom

This repo is Java/C++ projects that use blurring algorithm to expand image/video to 16:9 aspect ratio.

Used algorithm:
1. Cut out the pieces from original image.
2. Downscale 2-3 times.
3. Blur with 3-way linear box blur.
4. Upscale to needed size.

Java project only support images and C++ project supports AVI files but does not use sound.
