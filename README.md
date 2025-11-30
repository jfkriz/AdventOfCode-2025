# 🎄 Advent Of Code 2024 🎄
My solutions for the [Advent of Code - 2024](https://adventofcode.com/2024)

## 🌟 My Awesome Badges! 🌟
[![Advent of Code 2024 - Run Tests](https://github.com/jfkriz/AdventOfCode-2024/actions/workflows/RunTests.yaml/badge.svg)](https://github.com/jfkriz/AdventOfCode-2024/actions/workflows/RunTests.yaml)

[![](https://img.shields.io/badge/day%20📅-25-blue)](https://adventofcode.com/2024) [![](https://img.shields.io/badge/stars%20⭐-50-yellow)](https://adventofcode.com/2024) [![](https://img.shields.io/badge/days%20completed-25-red)](https://adventofcode.com/2024)

## Usage
Create each Day's solutions in a structure like this under [src/test/kotlin]():
```
./day01 +
        |
        +- CalorieCounterTest.kt
        +- input.txt
        +- test-input.txt
        +- README.md
./day02 +
        |
        +- Day02Test.kt
        +- input.txt
        +- test-input.txt
        +- README.md
./dayNN +
        |
        +- TodayTest.kt
        +- input.txt
        +- test-input.txt
        +- README.md
```

Assuming each day's solutions follow the above directory structure and naming conventions, you will be able to run each solution with a command like:
```shell
./gradlew run test --tests="day01**"
```

If the tests are named appropriately, you can also use a command like this to just run a specific test:
```shell
./gradlew run test --tests="day01**Part 1**Sample**"
```
This would run just the Part One solution for the Sample input (test-input.txt).

For each new Day, you can simply copy the "template" [dayNN](./src/test/kotlin/dayNN) package, and rename/implement as appropriate, or follow these destructions...
- [ ] Create a dayNN directory
- [ ] In the dayNN directory, create a SomethingTest.kt for the tests for the challenges. Naming the tests appropriately will allow the above gradle commands to work as described.
```kotlin
package dayNN

import Helpers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Day N - Description")
class TodayTest : Helpers() {
    @Test
    fun `Day 1 Part 1 Sample Input should return 42`() {
        assertEquals(42, Solver(loadSampleInput()).solvePartOne())
    }

    @Test
    fun `Day 1 Part 2 Sample Input should return 90210`() {
        assertEquals(90210, Solver(loadSampleInput()).solvePartTwo())
    }

    @Test
    fun `Day 1 Part 1 Real Input should return 42`() {
        assertEquals(42, Solver(loadInput()).solvePartOne())
    }

    @Test
    fun `Day 1 Part 2 Real Input should return 90210`() {
        assertEquals(90210, Solver(loadInput()).solvePartTwo())
    }
}

class Solver(data: List<String>) {
    fun solvePartOne(): Int {
        return 42
    }

    fun solvePartTwo(): Int {
        return 90210
    }
}
```
- [ ] In the dayNN directory, create an input.txt file with the input for the challenge; optionally create a test-input.txt with the sample input, for easier testing
- [ ] Copy the source of the final HTML page after solving both challenges, and paste into [CodeBeautify](https://codebeautify.org/html-to-markdown) to get a markdown version of the page
- [ ] Add a README each day, with the markdown from CodeBeautify
- [ ] Commit the day's solution, only after the day is over.
- [ ] The .gitignore should keep the input.txt from being committed, but please be sure to not commit this, per [the author's request](https://www.reddit.com/r/adventofcode/comments/e7khy8/comment/fa13hb9/?utm_source=share&utm_medium=web2x&context=3)