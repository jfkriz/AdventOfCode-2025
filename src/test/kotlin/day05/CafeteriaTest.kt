package day05

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles
import util.extensions.chunked
import util.extensions.reduceLong

@DisplayName("Day 05 - Cafeteria")
@TestMethodOrder(OrderAnnotation::class)
class CafeteriaTest : DataFiles {
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
    fun `Part 2 Sample Input should return 14`() {
        assertEquals(14, sampleSolver.solvePartTwo())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 623`() {
        assertEquals(623, solver.solvePartOne())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 353507173555373`() {
        assertEquals(353507173555373, solver.solvePartTwo())
    }
}

class Solver(
    @Suppress("UNUSED_PARAMETER") data: List<String>,
) {
    val database = CafeteriaDatabase.fromInput(data)

    fun solvePartOne(): Int = database.freshIngredientsOnHand.size

    fun solvePartTwo(): Long = database.freshIngredientsCount
}

data class CafeteriaDatabase(
    val freshIngredientRanges: List<LongRange>,
    val ingredients: List<Long>,
) {
    val freshIngredientsOnHand: List<Long> =
        ingredients.filter { ingredient ->
            freshIngredientRanges.any { range -> ingredient in range }
        }

    val freshIngredientsCount: Long = freshIngredientRanges.reduceLong().sumOf { it.last - it.first + 1 }

    companion object {
        fun fromInput(input: List<String>): CafeteriaDatabase {
            val chunks = input.chunked()
            val freshIngredientRanges =
                chunks[0].map { line ->
                    val (min, max) = line.split("-").map { it.toLong() }
                    min..max
                }
            val ingredients = chunks[1].map { it.toLong() }
            return CafeteriaDatabase(freshIngredientRanges, ingredients)
        }
    }
}
