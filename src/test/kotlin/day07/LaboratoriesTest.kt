package day07

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles

@DisplayName("Day 07 - Laboratories")
@TestMethodOrder(OrderAnnotation::class)
class LaboratoriesTest : DataFiles {
    private val sampleSolver by lazy {
        Solver(loadSampleInput())
    }
    private val solver by lazy {
        Solver(loadInput())
    }

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 21`() {
        assertEquals(21, sampleSolver.solvePartOne())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 40`() {
        assertEquals(40, sampleSolver.solvePartTwo())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 1590`() {
        assertEquals(1590, solver.solvePartOne())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 20571740188555`() {
        assertEquals(20571740188555, solver.solvePartTwo())
    }
}

class Solver(
    data: List<String>,
) {
    private val laboratory = Laboratory.fromInput(data)

    fun solvePartOne(): Int = laboratory.simulateBeam().size

    fun solvePartTwo(): Long = laboratory.simulateTimelines()
}

data class Laboratory(
    val start: Pair<Int, Int>,
    val splitters: Set<Pair<Int, Int>>,
) {
    private val lastRow = splitters.maxOfOrNull { it.second } ?: start.second

    companion object {
        fun fromInput(data: List<String>): Laboratory {
            var start: Pair<Int, Int>? = null
            val splitters = mutableSetOf<Pair<Int, Int>>()
            data.forEachIndexed { y, line ->
                line.forEachIndexed { x, char ->
                    when (char) {
                        'S' -> start = Pair(x, y)
                        '^' -> splitters.add(Pair(x, y))
                    }
                }
            }
            if (start == null) {
                throw IllegalArgumentException("No starting point 'S' found in input data.")
            }

            return Laboratory(start, splitters)
        }

        fun Pair<Int, Int>.split(): Pair<Pair<Int, Int>, Pair<Int, Int>> {
            val (x, y) = this
            val left = Pair(x - 1, y + 1)
            val right = Pair(x + 1, y + 1)
            return Pair(left, right)
        }

        fun Pair<Int, Int>.down(): Pair<Int, Int> {
            val (x, y) = this
            return Pair(x, y + 1)
        }
    }

    fun simulateBeam(): Set<Pair<Int, Int>> {
        val splits = mutableSetOf<Pair<Int, Int>>()
        val currentBeams = mutableSetOf(start)

        (start.second + 1..lastRow).forEach { _ ->
            val nextBeams = mutableSetOf<Pair<Int, Int>>()

            for (beam in currentBeams) {
                val nextBeam = beam.down()
                if (splitters.contains(nextBeam)) {
                    val (left, right) = nextBeam.split()
                    nextBeams.add(left)
                    nextBeams.add(right)
                    splits.add(nextBeam)
                } else {
                    nextBeams.add(nextBeam)
                }
            }
            currentBeams.clear()
            currentBeams.addAll(nextBeams)
        }
        return splits
    }

    fun simulateTimelines(): Long {
        val currentBeams: MutableMap<Pair<Int, Int>, Long> = mutableMapOf(start to 1)

        (start.second + 1..lastRow).forEach { _ ->
            val nextBeams = mutableMapOf<Pair<Int, Int>, Long>()

            for ((beam, count) in currentBeams) {
                val nextBeam = beam.down()

                if (splitters.contains(nextBeam)) {
                    val (left, right) = nextBeam.split()

                    nextBeams.merge(left, count, Long::plus)
                    nextBeams.merge(right, count, Long::plus)
                } else {
                    nextBeams.merge(nextBeam, count, Long::plus)
                }
            }
            currentBeams.clear()
            currentBeams.putAll(nextBeams)
        }

        return currentBeams.values.sum()
    }
}
