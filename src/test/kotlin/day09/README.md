# [Day 9: Movie Theater](https://adventofcode.com/2025/day/9)
## Part One

You slide down the [firepole](https://en.wikipedia.org/wiki/Fireman%27s_pole)
in the corner of the playground and land in the North Pole base movie theater!

The movie theater has a big tile floor with an interesting pattern. Elves here
are redecorating the theater by switching out some of the square tiles in the
big grid they form. Some of the tiles are _red_ ; the Elves would like to find
the largest rectangle that uses red tiles for two of its opposite corners.
They even have a list of where the red tiles are located in the grid (your
puzzle input).

For example:

    
    
    7,1
    11,1
    11,7
    9,7
    9,5
    2,5
    2,3
    7,3
    

Showing red tiles as `#` and other tiles as `.`, the above arrangement of red
tiles would look like this:

    
    
    ..............
    .......#...#..
    ..............
    ..#....#......
    ..............
    ..#......#....
    ..............
    .........#.#..
    ..............
    

You can choose any two red tiles as the opposite corners of your rectangle;
your goal is to find the largest rectangle possible.

For example, you could make a rectangle (shown as `O`) with an area of `24`
between `2,5` and `9,7`:

    
    
    ..............
    .......#...#..
    ..............
    ..#....#......
    ..............
    .. _O_ OOOOOOO....
    ..OOOOOOOO....
    ..OOOOOOO _O_.#..
    ..............
    

Or, you could make a rectangle with area `35` between `7,1` and `11,7`:

    
    
    ..............
    ....... _O_ OOOO..
    .......OOOOO..
    ..#....OOOOO..
    .......OOOOO..
    ..#....OOOOO..
    .......OOOOO..
    .......OOOO _O_..
    ..............
    

You could even make a thin rectangle with an area of only `6` between `7,3`
and `2,3`:

    
    
    ..............
    .......#...#..
    ..............
    .. _O_ OOOO _O_......
    ..............
    ..#......#....
    ..............
    .........#.#..
    ..............
    

Ultimately, the largest rectangle you can make in this example has area
`_50_`. One way to do this is between `2,5` and `11,1`:

    
    
    ..............
    ..OOOOOOOOO _O_..
    ..OOOOOOOOOO..
    ..OOOOOOOOOO..
    ..OOOOOOOOOO..
    .. _O_ OOOOOOOOO..
    ..............
    .........#.#..
    ..............
    

Using two red tiles as opposite corners, _what is the largest area of any
rectangle you can make?_

Your puzzle answer was `4771532800`.

## Part Two

The Elves just remembered: they can only switch out tiles that are _red_ or
_green_. So, your rectangle can only include red or green tiles.

In your list, every red tile is connected to the red tile before and after it
by a straight line of _green tiles_. The list wraps, so the first red tile is
also connected to the last red tile. Tiles that are adjacent in your list will
always be on either the same row or the same column.

Using the same example as before, the tiles marked `X` would be green:

    
    
    ..............
    .......#XXX#..
    .......X...X..
    ..#XXXX#...X..
    ..X........X..
    ..#XXXXXX#.X..
    .........X.X..
    .........#X#..
    ..............
    

In addition, all of the tiles _inside_ this loop of red and green tiles are
_also_ green. So, in this example, these are the green tiles:

    
    
    ..............
    .......#XXX#..
    .......XXXXX..
    ..#XXXX#XXXX..
    ..XXXXXXXXXX..
    ..#XXXXXX#XX..
    .........XXX..
    .........#X#..
    ..............
    

The remaining tiles are never red nor green.

The rectangle you choose still must have red tiles in opposite corners, but
any other tiles it includes must now be red or green. This significantly
limits your options.

For example, you could make a rectangle out of red and green tiles with an
area of `15` between `7,3` and `11,1`:

    
    
    ..............
    .......OOOO _O_..
    .......OOOOO..
    ..#XXXX _O_ OOOO..
    ..XXXXXXXXXX..
    ..#XXXXXX#XX..
    .........XXX..
    .........#X#..
    ..............
    

Or, you could make a thin rectangle with an area of `3` between `9,7` and
`9,5`:

    
    
    ..............
    .......#XXX#..
    .......XXXXX..
    ..#XXXX#XXXX..
    ..XXXXXXXXXX..
    ..#XXXXXX _O_ XX..
    .........OXX..
    ......... _O_ X#..
    ..............
    

The largest rectangle you can make in this example using only red and green
tiles has area `_24_`. One way to do this is between `9,5` and `2,3`:

    
    
    ..............
    .......#XXX#..
    .......XXXXX..
    .. _O_ OOOOOOOXX..
    ..OOOOOOOOXX..
    ..OOOOOOO _O_ XX..
    .........XXX..
    .........#X#..
    ..............
    

Using two red tiles as opposite corners, _what is the largest area of any
rectangle you can make using only red and green tiles?_

Your puzzle answer was `1544362560`.

Both parts of this puzzle are complete! They provide two gold stars: **

At this point, you should [return to your Advent calendar](https://adventofcode.com/2025) and try
another puzzle.

If you still want to see it, you can [get your puzzle input](https://adventofcode.com/2025/day/9/input).
