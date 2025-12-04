package day04

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles

@DisplayName("Day 04 - Printing Department")
@TestMethodOrder(OrderAnnotation::class)
class PrintingDepartmentTest : DataFiles {
    private val sampleSolver by lazy {
        Solver(loadSampleInput())
    }
    private val solver by lazy {
        Solver(loadInput())
    }

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 13`() {
        assertEquals(13, sampleSolver.solvePartOne())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 43`() {
        assertEquals(43, sampleSolver.solvePartTwo())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 1505`() {
        assertEquals(1505, solver.solvePartOne())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 9182`() {
        assertEquals(9182, solver.solvePartTwo())
    }
}

class Solver(
    @Suppress("UNUSED_PARAMETER") data: List<String>,
) {
    val departmentMap: DepartmentMap = DepartmentMap.fromInput(data)

    fun solvePartOne(): Int = departmentMap.getAccessibleRolls().size

    fun solvePartTwo(): Int {
        var currentMap = departmentMap
        var totalRemoved = 0
        while (true) {
            val accessibleRolls = currentMap.getAccessibleRolls()
            if (accessibleRolls.isEmpty()) {
                break
            }
            totalRemoved += accessibleRolls.size
            currentMap = currentMap.removeAccessibleRolls()
        }
        return totalRemoved
    }
}

data class DepartmentMap(
    val grid: List<List<Char>>,
) {
    fun getAccessibleRolls(): List<Pair<Int, Int>> {
        // A roll ('@') is accessible if it has less than 4 neighbors that are not also rolls ('@')
        val accessibleRolls = mutableSetOf<Pair<Int, Int>>()
        for (y in grid.indices) {
            for (x in grid[0].indices) {
                if (grid[y][x] == '@') {
                    val neighbors = getNeighbors(x, y)
                    val rollNeighbors = neighbors.count { (nx, ny) -> grid[ny][nx] == '@' }
                    if (rollNeighbors < 4) {
                        accessibleRolls.add(Pair(x, y))
                    }
                }
            }
        }
        return accessibleRolls.toList()
    }

    fun removeAccessibleRolls(): DepartmentMap {
        val newGrid = grid.map { it.toMutableList() }
        val accessibleRolls = getAccessibleRolls()
        for ((x, y) in accessibleRolls) {
            newGrid[y][x] = '.'
        }
        return DepartmentMap(newGrid)
    }

    private fun getNeighbors(
        x: Int,
        y: Int,
    ): List<Pair<Int, Int>> {
        val neighbors = mutableListOf<Pair<Int, Int>>()
        val directions =
            listOf(
                Pair(0, -1), // Up
                Pair(0, 1), // Down
                Pair(-1, 0), // Left
                Pair(1, 0), // Right
                Pair(-1, -1), // Up-Left
                Pair(1, -1), // Up-Right
                Pair(-1, 1), // Down-Left
                Pair(1, 1), // Down-Right
            )

        for (direction in directions) {
            val newX = x + direction.first
            val newY = y + direction.second
            if (newX in grid[0].indices && newY in grid.indices) {
                neighbors.add(Pair(newX, newY))
            }
        }
        return neighbors
    }

    companion object {
        fun fromInput(data: List<String>): DepartmentMap {
            val grid =
                data.map { line ->
                    line.toList()
                }
            return DepartmentMap(grid)
        }
    }
}
