package day01

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles

@DisplayName("Day 01 - Secret Entrance")
@TestMethodOrder(OrderAnnotation::class)
class SecretEntranceTest : DataFiles {
    private val sampleSolver by lazy {
        Solver(loadSampleInput())
    }
    private val solver by lazy {
        Solver(loadInput())
    }

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 3`() {
        assertEquals(3, sampleSolver.solvePartOne())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 6`() {
        assertEquals(6, sampleSolver.solvePartTwo())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 989`() {
        assertEquals(989, solver.solvePartOne())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 5941`() {
        assertEquals(5941, solver.solvePartTwo())
    }
}

class Solver(
    @Suppress("UNUSED_PARAMETER") data: List<String>,
) {
    val instructions: List<Instruction> = data.map { Instruction.fromString(it) }

    fun solvePartOne(): Int {
        val dial = Dial(currentPosition = 50)
        val dials =
            instructions.runningFold(dial) { currentDial, instruction ->
                currentDial.rotate(instruction)
            }
        return dials.filter { it.currentPosition == 0 }.size
    }

    fun solvePartTwo(): Int {
        val dial = Dial(currentPosition = 50)
        val dials =
            instructions.runningFold(dial) { currentDial, instruction ->
                currentDial.rotate(instruction)
            }
        return dials.sumOf { it.timesRotationHitZero }
    }
}

/**
 * Represents a dial that can be rotated left or right, tracking its current position.
 * When implementing part 1, I kind of assumed the dial would become much larger for part 2, which is why it accepts the
 * max value as a parameter. In reality, the dial is 0-99 for both parts, so could have removed that.
 */
data class Dial(
    val currentPosition: Int = 0,
    val timesRotationHitZero: Int = 0,
    val max: Int = 99,
) {
    fun rotate(instruction: Instruction): Dial {
        // Rotate the dial and find the new position
        val newPosition =
            if (instruction.direction == 'L') {
                (currentPosition - instruction.value).mod(max + 1)
            } else {
                (currentPosition + instruction.value).mod(max + 1)
            }

        return this.copy(currentPosition = newPosition, timesRotationHitZero = countZeroes(instruction))
    }

    /**
     * Inefficient way to count how many times the rotation hits zero by simulating each step.
     */
    @Suppress("unused")
    private fun countZeroesInefficient(instruction: Instruction): Int {
        var count = 0
        var position = currentPosition

        repeat(instruction.value) {
            position =
                if (instruction.direction == 'L') {
                    (position + max).mod(max + 1)
                } else {
                    (position + 1).mod(max + 1)
                }

            if (position == 0) {
                count++
            }
        }

        return count
    }

    /**
     * Efficient way to count how many times the rotation hits zero mathematically.
     * This works by calculating the first time the dial would hit zero during the rotation,
     * and then counting how many full cycles of (max + 1) fit into the remaining distance.
     */
    private fun countZeroes(instruction: Instruction): Int {
        var firstZero =
            if (instruction.direction == 'L') {
                currentPosition.mod(max + 1)
            } else {
                (max + 1 - currentPosition).mod(max + 1)
            }

        if (firstZero == 0) {
            firstZero = max + 1
        }

        val hitZero =
            if (instruction.value < firstZero) {
                0
            } else {
                val remaining = instruction.value - firstZero
                1 + (remaining / (max + 1))
            }
        return hitZero
    }
}

data class Instruction(
    val direction: Char,
    val value: Int,
) {
    companion object {
        fun fromString(input: String): Instruction {
            val direction = input[0]
            if (direction != 'L' && direction != 'R') {
                throw IllegalArgumentException("Invalid direction: $direction")
            }
            val value = input.substring(1).toInt()
            return Instruction(direction, value)
        }
    }
}
