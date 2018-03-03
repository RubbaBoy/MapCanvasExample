# MapCanvasExample
Example plugin for the project MapCanvas API. You must have the MapCanvas API on your server for this to work.

This example plugin is optimised for a MapWall of 40x24 blocks (Which created a MapCanvas of 5120x3072 pixels).

## Usage

The example comes with a command with three subcommands:

**/canvas create [width] [height]** - Created a MapWall with the given width and height in blocks.

**/canvas use [width] [height] [startingID]** - Makes the system use an existing MapWall. In this example, the startingID will always be 0.

**/canvas draw** - Draws the example objects to the canvas

## Screenshots

This is a screenshot of the example running on a 40x24 block canvas. Black and yellow blocks were added a block away from the map wall to show its size.
![MapCanvas Example](https://rubbaboy.me/images/uduh16x)
