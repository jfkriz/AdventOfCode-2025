package day09

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles
import kotlin.math.abs

@DisplayName("Day 09 - Movie Theater")
@TestMethodOrder(OrderAnnotation::class)
class MovieTheaterTest : DataFiles {
    private val sampleSolver by lazy {
        Solver(loadSampleInput())
    }
    private val solver by lazy {
        Solver(loadInput())
    }

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 50`() {
        assertEquals(50L, sampleSolver.solvePartOne())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 24`() {
        assertEquals(24L, sampleSolver.solvePartTwo())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 4771532800`() {
        assertEquals(4771532800L, solver.solvePartOne())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 1544362560`() {
        // 4652231070 too high
        // 1385414730 too low
        // 1544362560
        assertEquals(1544362560L, solver.solvePartTwo())
    }
}

class Solver(
    data: List<String>,
) {
    private val floor = Floor.fromInput(data)

    fun solvePartOne(): Long = floor.findLargestRectangleArea()

    fun solvePartTwo(): Long = floor.findLargestRectangleArea(stayWithinBounds = true)
}

data class Floor(
    val redTiles: Set<Pair<Long, Long>>,
) {
    private val redTileList = redTiles.toList()
    private val edges: List<Segment> by lazy { buildEdges(redTileList) }

    // Pre-compute all boundary tiles
    private val greenTiles: Set<Pair<Long, Long>> by lazy {
        val green = mutableSetOf<Pair<Long, Long>>()
        for ((x1, y1, x2, y2) in edges) {
            if (x1 == x2) {
                // Vertical edge
                val minY = minOf(y1, y2)
                val maxY = maxOf(y1, y2)
                for (y in minY..maxY) {
                    green.add(x1 to y)
                }
            } else {
                // Horizontal edge
                val minX = minOf(x1, x2)
                val maxX = maxOf(x1, x2)
                for (x in minX..maxX) {
                    green.add(x to y1)
                }
            }
        }
        green
    }

    private val insideCache = mutableMapOf<Pair<Long, Long>, Boolean>()

    fun findLargestRectangleArea(stayWithinBounds: Boolean = false): Long {
        var maxArea = 0L

        if (!stayWithinBounds) {
            // Part 1: Simple rectangle area calculation
            for (i in redTileList.indices) {
                for (j in i + 1 until redTileList.size) {
                    val (x1, y1) = redTileList[i]
                    val (x2, y2) = redTileList[j]

                    if (x1 != x2 && y1 != y2) {
                        val area = (abs(x2 - x1) + 1) * (abs(y2 - y1) + 1)
                        if (area > maxArea) maxArea = area
                    }
                }
            }
        } else {
            // Part 2: Check rectangle edges with smart sampling
            for (i in redTileList.indices) {
                for (j in i + 1 until redTileList.size) {
                    val (x1, y1) = redTileList[i]
                    val (x2, y2) = redTileList[j]

                    if (x1 != x2 && y1 != y2) {
                        val minX = minOf(x1, x2)
                        val maxX = maxOf(x1, x2)
                        val minY = minOf(y1, y2)
                        val maxY = maxOf(y1, y2)

                        if (isRectangleValid(minX, maxX, minY, maxY)) {
                            val area = (maxX - minX + 1) * (maxY - minY + 1)
                            if (area > maxArea) maxArea = area
                        }
                    }
                }
            }
        }

        return maxArea
    }

    private fun isRectangleValid(
        minX: Long,
        maxX: Long,
        minY: Long,
        maxY: Long,
    ): Boolean {
        // Sample points along edges intelligently
        val width = maxX - minX + 1
        val height = maxY - minY + 1

        // Determine step size based on edge length
        val stepX =
            when {
                width <= 100 -> 1L
                width <= 1000 -> maxOf(1L, width / 100)
                else -> maxOf(1L, width / 50)
            }

        val stepY =
            when {
                height <= 100 -> 1L
                height <= 1000 -> maxOf(1L, height / 100)
                else -> maxOf(1L, height / 50)
            }

        // Check top and bottom edges
        var x = minX
        while (x <= maxX) {
            if (!isPointValid(x to minY)) return false
            if (!isPointValid(x to maxY)) return false
            x += stepX
        }
        // Make sure we check the last point
        if ((maxX - minX) % stepX != 0L) {
            if (!isPointValid(maxX to minY)) return false
            if (!isPointValid(maxX to maxY)) return false
        }

        // Check left and right edges (skip corners)
        var y = minY + stepY
        while (y < maxY) {
            if (!isPointValid(minX to y)) return false
            if (!isPointValid(maxX to y)) return false
            y += stepY
        }

        // Check a few interior points for concave regions
        val midX = (minX + maxX) / 2
        val midY = (minY + maxY) / 2
        if (!isPointValid(midX to midY)) return false

        // Check quarter points if rectangle is large enough
        if (width > 20 && height > 20) {
            val quarterX1 = minX + width / 4
            val quarterX2 = minX + 3 * width / 4
            val quarterY1 = minY + height / 4
            val quarterY2 = minY + 3 * height / 4

            if (!isPointValid(quarterX1 to quarterY1)) return false
            if (!isPointValid(quarterX1 to quarterY2)) return false
            if (!isPointValid(quarterX2 to quarterY1)) return false
            if (!isPointValid(quarterX2 to quarterY2)) return false
        }

        return true
    }

    private fun isPointValid(point: Pair<Long, Long>): Boolean = point in redTiles || point in greenTiles || isInsideCached(point)

    private fun isInsideCached(point: Pair<Long, Long>): Boolean = insideCache.getOrPut(point) { isInside(point) }

    private fun isInside(point: Pair<Long, Long>): Boolean {
        val (px, py) = point
        var inside = false

        for (i in edges.indices) {
            val (x1, y1, x2, y2) = edges[i]

            if ((y1 > py) != (y2 > py)) {
                val xIntersect = x1 + (py - y1).toDouble() / (y2 - y1) * (x2 - x1)
                if (px < xIntersect) {
                    inside = !inside
                }
            }
        }

        return inside
    }

    companion object {
        fun fromInput(data: List<String>): Floor {
            val redTiles =
                data
                    .map {
                        val (x, y) = it.split(",")
                        x.toLong() to y.toLong()
                    }.toSet()
            return Floor(redTiles)
        }

        private data class Segment(
            val x1: Long,
            val y1: Long,
            val x2: Long,
            val y2: Long,
        )

        private fun buildEdges(redTileList: List<Pair<Long, Long>>): List<Segment> {
            val edges = mutableListOf<Segment>()
            val n = redTileList.size
            for (i in redTileList.indices) {
                val (x1, y1) = redTileList[i]
                val (x2, y2) = redTileList[(i + 1) % n]
                edges.add(Segment(x1, y1, x2, y2))
            }
            return edges
        }
    }
}
