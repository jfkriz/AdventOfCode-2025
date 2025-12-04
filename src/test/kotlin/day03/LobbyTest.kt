package day03

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles
import util.extensions.combinations

@DisplayName("Day 03 - Lobby")
@TestMethodOrder(OrderAnnotation::class)
class LobbyTest : DataFiles {
    private val sampleSolver by lazy {
        Solver(loadSampleInput())
    }
    private val solver by lazy {
        Solver(loadInput())
    }

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 357`() {
        assertEquals(357, sampleSolver.solvePartOne())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 3121910778619`() {
        assertEquals(3121910778619, sampleSolver.solvePartTwo())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 17144`() {
        assertEquals(17144, solver.solvePartOne())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 170371185255900`() {
        assertEquals(170371185255900, solver.solvePartTwo())
    }
}

class Solver(
    @Suppress("UNUSED_PARAMETER") data: List<String>,
) {
    val banks =
        data.map { line ->
            BatteryBank(
                batteries = line.map { it.digitToInt() },
            )
        }

    fun solvePartOne(): Long = banks.sumOf { it.findLargestJoltageOptimized(2) }

    fun solvePartTwo(): Long = banks.sumOf { it.findLargestJoltageOptimized(12) }
}

/**
 * A bank of batteries that can be used to power a device. The individual batteries
 * in the bank each has a joltage rating of 1 - 9.
 */
data class BatteryBank(
    val batteries: List<Int>,
) {
    /**
     * Find the largest joltage that can be produced by this battery bank when you turn on two batteries.
     */
    @Suppress("UNUSED")
    fun findLargestJoltage(): Long {
        var maxJoltage = 0L
        for (i in batteries.indices) {
            for (j in i + 1 until batteries.size) {
                val currentJoltage = batteries[i] * 10L + batteries[j]
                if (currentJoltage > maxJoltage) {
                    maxJoltage = currentJoltage
                }
            }
        }
        return maxJoltage
    }

    /**
     * I thought this would be a really clever solution using combinations, but it's
     * really inefficient compared to the optimized version.
     */
    @Suppress("UNUSED")
    fun findLargestJoltageUsingCombinations(size: Int = 2): Long =
        batteries
            .asSequence()
            .combinations(size)
            .map { it.joinToString("").toLong() }
            .max()

    /**
     * This is an optimized version that finds the largest possible joltage.
     * It seems a bit messy, and I think there is probably a cleaner way to do this,
     * but it gets the job done without generating all combinations.
     */
    fun findLargestJoltageOptimized(size: Int = 2): Long {
        val len = batteries.size

        val sb = StringBuilder()
        var start = 0
        var remaining = size

        while (remaining > 0) {
            val end = len - remaining
            var maxIdx = start
            var maxDigit = batteries[start]
            for (i in start + 1..end) {
                val d = batteries[i]
                if (d > maxDigit) {
                    maxDigit = d
                    maxIdx = i
                }
            }
            sb.append(maxDigit)
            start = maxIdx + 1
            remaining--
        }

        return sb.toString().toLong()
    }
}
