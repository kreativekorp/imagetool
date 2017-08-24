# ImageTool
Perform simple operations on images and animated GIFs.

## ListImages
List images with their dimensions.

Options:

`-r`, `-R` - Recursively list images in subdirectories encountered.

Sample output:

```
$ listimages .
Width	Height	Left	Top	Right	Bottom	Frames	Name
98	110	0	0	0	0	4	./000 Reimu.gif
128	118	0	0	0	0	4	./001 Marisa.gif
512	512	0	0	0	0		./lena.png
258	200	0	0	0	0		./RGB_24bits_palette_color_test_chart.png
150	200	0	0	0	0		./RGB_24bits_palette_sample_image.jpg
224	56	2	2	6	2		./sisters-in-name.png
56	56	3	2	5	2		./too-school-for-cool.png
375	230	0	0	0	0	8	./xFRcRZI.gif
3000	230	0	0	0	0		./xFRcRZI.gif.png

total 9

Width	Height	Left	Top	Right	Bottom	Frames	Name
56	56	0	0	0	0		(min)
3000	512	3	2	6	2		(max)
```

## TransformImages
Perform simple transformations on images and animated GIFs.

Options for transformations:

`-a`, `--addmargin` *top* *left* *bottom* *right* - Adds the specified number of empty pixels to each edge of the image.

`-b`, `--backgroundcolor` *color* - Renders the image on top of the specified background color. *Color* must be a string of 3, 4, 6, or 8 hexadecimal digits. (Not currently supported for GIFs.)

`-c`, `--canvassize` *width* *height* *anchor* - Changes the width and height of the image without scaling the image. *Anchor* is `NW`, `N`, `NE`, `E`, `SE`, `S`, `SW`, `W`, or `CENTER`.

`-d`, `--flipdiagonal` - Flips the image about its diagonal, i.e. swaps the x and y axes.

`-g`, `--grayscale` - Converts the image to grayscale according to the following formula: *gray* = 0.30 * *red* + 0.59 * *green* + 0.11 * *blue*

`-h`, `--fliphorizontal` - Flips the image horizontally, i.e. about the vertical axis.

`-i`, `--invert` - Inverts the colors of the image.

`-j`, `--invertgrays` *threshold* - Inverts each color of the image only if it is sufficiently desaturated. *Threshold* varies from 0 (invert nothing) to 256 (invert everything).

`-k`, `--colorize` *color* - Recolors the image using tints and shades of the specified color. *Color* must be a string of 3, 4, 6, or 8 hexadecimal digits.

`-l`, `--rotateleft` - Rotates the image counter-clockwise. Alias for `--flipdiagonal` `--flipvertical`.

`-m`, `--removemargin` *top* *left* *bottom* *right* - Removes the specified number of pixels from each edge of the image.

`-p`, `--pebble` - Converts the colors of the image to the Pebble color palette.

`-r`, `--rotateright` - Rotates the image clockwise. Alias for `--flipdiagonal` `--fliphorizontal`.

`-s`, `--scale` *sx* *sy* - Scales the image by the specified factors horizontally and vertically.

`-t`, `--trim` *top* *left* *bottom* *right* - Removes transparent pixels from the specified edges of the image. Each parameter is either `TRUE` or `FALSE`.

`-u`, `--rotate180` - Rotates the image 180 degrees. Alias for `--fliphorizontal` `--flipvertical`.

`-v`, `--flipvertical` - Flips the image vertically, i.e. about the horizontal axis.

`-w`, `--websafe` - Converts the colors of the image to the web-safe color palette.

`-x`, `--speed` *multiplier* - Speeds up or slows down an animation. Has no effect on static images.

`-z`, `--imagesize` *width* *height* - Scales the image to the specified width and height.

Options for output:

`-f`, `--format` *format* - Specifies the output format of the transformed image. Default is gif if the original is gif or png otherwise.

`-o`, `--output` *path* - Specifies the output file or directory path for the transformed image.

## ConvertAnimation
Convert between static images and animated GIFs.

Options for input:

`-ica`, `--inputcellanchor` *anchor* - Sets the position of images read from a directory. *Anchor* is `NW`, `N`, `NE`, `E`, `SE`, `S`, `SW`, `W`, or `CENTER`.

`-icb`, `--inputcellorigin` *x* *y* - Determines the position of the first frame within a static image.

`-ics`, `--inputcellsize` *width* *height* - Determines the width and height of each frame within a static image.

`-icd`, `--inputcelldelta` *dx* *dy* - Determines the X and Y offset between each frame within a static image.

`-icc`, `--inputcellcount` *columns* *rows* - Determines the number of frames horizontally and vertically.

`-ico`, `--inputcellorder` *order* - Determines the ordering of frames (`LTR-TTB`, `TTB-LTR`, etc.).

Options for transformations:

`-D`, `--durations` *list of frame durations* - Sets the duration of each frame in the final output, e.g. `"1;0.5;3.14"` to set the duration of frames 1, 2, and 3 to one second, half a second, and 3.14 seconds, respectively. The list is repeated until the number of frames in the animation is reached when applied.

`-F`, `--frames` *list of frame indices* - Determines which frames will appear in the final output, e.g. `"1-4;7;2"` for frames 1, 2, 3, 4, 7, and 2 again, in that order.

All of the options for transformations used with TransformImages can also be used with ConvertAnimation.

Options for output:

`-f`, `--format` *format* - Specifies the output format of the converted animation. Default is `"d"` for a directory of still frames.

`-n`, `--loopcount` *count* - Specifies the number of times an animation loops (for animated GIFs).

`-o`, `--output` *path* - Specifies the output file or directory path for the converted animation.

`-ocb`, `--outputcellorigin` *x* *y* - Determines the position of the first frame within a static image.

`-ocd`, `--outputcelldelta` *dx* *dy* - Determines the X and Y offset between each frame within a static image.

`-occ`, `--outputcellcount` *columns* *rows* - Determines the number of frames horizontally and vertically.

`-oco`, `--outputcellorder` *order* - Determines the ordering of frames (`LTR-TTB`, `TTB-LTR`, etc.).

`-ois`, `--outputimagesize` *width* *height* - Determines the total width and height of the static image.
