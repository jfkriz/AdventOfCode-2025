package day06

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles
import util.extensions.chunked
import util.extensions.padToMaxLength
import util.extensions.rotateLeft

@DisplayName("Day 06 - Trash Compactor")
@TestMethodOrder(OrderAnnotation::class)
class TrashCompactorTest : DataFiles {
    private val sampleSolver by lazy {
        Solver(loadSampleInput())
    }
    private val solver by lazy {
        Solver(loadInput())
    }

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 4277556`() {
        assertEquals(4277556, sampleSolver.solvePartOne())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 3263827`() {
        assertEquals(3263827, sampleSolver.solvePartTwo())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 5322004718681`() {
        assertEquals(5322004718681, solver.solvePartOne())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 9876636978528`() {
        assertEquals(9876636978528, solver.solvePartTwo())
    }
}

class Solver(
    data: List<String>,
) {
    val worksheet = Worksheet.fromInput(data)
    val cephalopodWorksheet = Worksheet.fromCephalopodInput(data)

    fun solvePartOne(): Long = worksheet.total

    fun solvePartTwo(): Long = cephalopodWorksheet.total
}

data class Worksheet(
    val entries: List<WorksheetEntry>,
) {
    companion object {
        fun fromInput(input: List<String>): Worksheet {
            val parsed = input.map { it.trim().split("\\s+".toRegex()) }.rotateLeft()
            return Worksheet(
                parsed.map { WorksheetEntry.fromStrings(it) },
            )
        }

        fun fromCephalopodInput(input: List<String>): Worksheet {
            val parsed = input.padToMaxLength(' ').map { it.toCharArray().map { c -> c.toString() } }.rotateLeft()
            val groups = parsed.map { it.joinToString("") }.chunked()
            return Worksheet(
                groups.map { WorksheetEntry.fromCephalopodNumbers(it) },
            )
        }
    }

    val total: Long
        get() = entries.sumOf { it.result }
}

data class WorksheetEntry(
    val numbers: List<Long>,
    val operation: Char,
) {
    companion object {
        fun fromStrings(entryStrings: List<String>): WorksheetEntry {
            val numbers =
                entryStrings
                    .dropLast(1)
                    .map { it.trim().toLong() }
            val operation = entryStrings.last().trim().first()
            return WorksheetEntry(numbers, operation)
        }

        fun fromCephalopodNumbers(entryStrings: List<String>): WorksheetEntry {
            val numbers =
                entryStrings
                    .map { it.replace("\\D+".toRegex(), "") }
                    .map { it.toLong() }
            val operation = entryStrings.last().last()
            return WorksheetEntry(numbers, operation)
        }
    }

    val result: Long
        get() =
            when (operation) {
                '+' -> numbers.sum()
                '*' -> numbers.reduce { acc, n -> acc * n }
                else -> throw IllegalArgumentException("Unsupported operation: $operation")
            }
}
