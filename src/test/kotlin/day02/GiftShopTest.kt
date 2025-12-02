package day02

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles

@DisplayName("Day 02 - Gift Shop")
@TestMethodOrder(OrderAnnotation::class)
class GiftShopTest : DataFiles {
    private val sampleSolver by lazy {
        Solver(loadSampleInput())
    }
    private val solver by lazy {
        Solver(loadInput())
    }

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 1227775554`() {
        assertEquals(1227775554, sampleSolver.solvePartOne())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 4174379265`() {
        assertEquals(4174379265, sampleSolver.solvePartTwo())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 37314786486`() {
        assertEquals(37314786486, solver.solvePartOne())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 47477053982`() {
        assertEquals(47477053982, solver.solvePartTwo())
    }
}

class Solver(
    @Suppress("UNUSED_PARAMETER") data: List<String>,
) {
    private val ranges = data.joinToString("").split(",").map { IdRange.fromString(it) }

    fun solvePartOne(): Long = ranges.map { it.findInvalidIds() }.flatten().sum()

    fun solvePartTwo(): Long = ranges.map { it.findInvalidIds(true) }.flatten().sum()
}

data class IdRange(
    val start: Long,
    val end: Long,
) {
    private val range: LongRange = start..end

    fun findInvalidIds(extended: Boolean = false): List<Long> =
        if (extended) {
            range.filterNot { isValidIdExtended(it.toString()) }
        } else {
            range.filterNot { isValidId(it.toString()) }
        }

    /**
     * An ID is invalid if it is made only of some sequence of digits repeated twice.
     * For example, the following are invalid:
     * * 55 - '5' repeated twice
     * * 6464 - '64' repeated twice
     * * 123123 - '123' repeated twice
     */
    private fun isValidId(id: String): Boolean {
        // IDs with an odd number of digits cannot be made of a sequence repeated twice
        if (id.length % 2 != 0) return true

        // Split the ID into two halves and compare them
        val (firstHalf, secondHalf) = id.chunked(id.length / 2)

        return firstHalf != secondHalf
    }

    /**
     * An extended validity check where an ID is invalid if it consists of any sequence of digits
     * repeated two or more times to form the entire ID.
     * For example, the following are invalid:
     * * 12341234 - '1234' repeated twice
     * * 123123123 - '123' repeated three times
     * * 1212121212 - '12' repeated five times
     * * 1111111 - '1' repeated seven times
     */
    private fun isValidIdExtended(id: String): Boolean {
        // Loop over possible sequence lengths from 1 up to half the ID length
        for (seqLength in 1..(id.length / 2)) {
            // Only consider sequence lengths that are evenly divisible into the ID length
            if (id.length % seqLength != 0) continue

            val seq = id.take(seqLength)
            val repetitions = id.length / seqLength
            // Repeat the sequence and see if it matches the ID - if it does, it's invalid
            if (seq.repeat(repetitions) == id) {
                return false
            }
        }
        return true
    }

    /**
     * An optimized extended validity check where an ID is invalid if it consists of any sequence of digits
     * repeated two or more times to form the entire ID.
     * For example, the following are invalid:
     * * 12341234 - '1234' repeated twice
     * * 123123123 - '123' repeated three times
     * * 1212121212 - '12' repeated five times
     * * 1111111 - '1' repeated seven times
     *
     * This is the solution Copilot gave me, and uses an algorithm described in detail here:
     * https://algo.monster/liteproblems/459
     *
     * This runs almost twice as fast as the previous implementation.
     */
    @Suppress("unused")
    private fun isValidIdExtendedOptimized(id: String): Boolean {
        val doubled = (id + id).drop(1).dropLast(1)
        return !doubled.contains(id)
    }

    companion object {
        fun fromString(range: String): IdRange {
            val (start, end) = range.split("-").map { it.toLong() }
            return IdRange(start, end)
        }
    }
}
