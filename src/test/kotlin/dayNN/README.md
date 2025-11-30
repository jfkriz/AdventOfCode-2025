# Day Template

## Usage
Copy the contents of this package to a new "daynn" package, then get to work modifying the [TodayTest.kt](TodayTest.kt) as needed.

Run the tests in the IDE, or with commands like:
```shell
# Run the Part 1 solutions (both sample and real) for day01
$ ./gradlew test --tests="day01**Part 1**"
# Run the Part 1 Sample solution for day01
$ ./gradlew test --tests="day01**Part 1**Sample**"
# Run the Part 2 Real solution for day02
$ ./gradlew test --tests="day02**Part 2**Real**"
```

## Put a Bow on It
View the source of the Advent of Code page once you solve the puzzles, 
convert the source to markdown using something like [CodeBeautify HTML-to-Markdown](https://codebeautify.org/html-to-markdown),
then replace this README's contents with that markdown. This makes it easy to go back and review your solutions in the context of the actual problem. Some of these can be pretty obscure...

When you commit your solution, be careful to not commit your input.txt. Everyone's input is supposed to be different, but [the author requests](https://www.reddit.com/r/adventofcode/comments/e7khy8/comment/fa13hb9/?utm_source=share&utm_medium=web2x&context=3) that we don't post our input. The [.gitignore](../../../../.gitignore) should take care of that for you, but doesn't hurt to double-check.