package day10

import com.microsoft.z3.Context
import com.microsoft.z3.Status
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles

@DisplayName("Day 10 - Factory")
@TestMethodOrder(OrderAnnotation::class)
class FactoryTest : DataFiles {
    private val sampleSolver by lazy {
        Solver(loadSampleInput())
    }
    private val solver by lazy {
        Solver(loadInput())
    }

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 7`() {
        assertEquals(7, sampleSolver.solvePartOne())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 33`() {
        assertEquals(33, sampleSolver.solvePartTwo())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 514`() {
        assertEquals(514, solver.solvePartOne())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 21824`() {
        assertEquals(21824, solver.solvePartTwo())
    }
}

class Solver(
    data: List<String>,
) {
    val machines = data.map { Machine.fromInput(it) }

    fun solvePartOne(): Int = machines.sumOf { it.findButtonPressesForTargetButtonState() }

    fun solvePartTwo(): Int =
        machines
            .mapIndexed { index, machine ->
                println("Solving machine ${index + 1}/${machines.size} for Part Two")
                machine.findButtonPressesForTargetButtonJoltages()
            }.sum()
}

data class Machine(
    val targetButtonState: List<Boolean>,
    val targetButtonJoltages: List<Int>,
    val buttons: List<List<Boolean>>,
) {
    fun findButtonPressesForTargetButtonState(): Int {
        val numLights = targetButtonState.size
        val numButtons = buttons.size

        val mat =
            Array(numLights) { light ->
                IntArray(numButtons + 1) { col ->
                    if (col < numButtons) {
                        if (buttons[col][light]) 1 else 0
                    } else {
                        if (targetButtonState[light]) 1 else 0
                    }
                }
            }

        var row = 0
        val pivotCols = mutableListOf<Int>()
        for (col in 0 until numButtons) {
            var sel = row
            while (sel < numLights && mat[sel][col] == 0) sel++
            if (sel == numLights) continue

            val tmp = mat[row]
            mat[row] = mat[sel]
            mat[sel] = tmp

            for (r in 0 until numLights) {
                if (r != row && mat[r][col] == 1) {
                    for (c in col..numButtons) {
                        mat[r][c] = mat[r][c] xor mat[row][c]
                    }
                }
            }

            pivotCols.add(col)
            row++
            if (row == numLights) break
        }

        for (r in row until numLights) {
            if (mat[r][numButtons] == 1) {
                error("No solution exists for this machine")
            }
        }

        val rank = pivotCols.size
        val isPivot = BooleanArray(numButtons)
        pivotCols.forEach { isPivot[it] = true }
        val freeCols = (0 until numButtons).filter { !isPivot[it] }

        if (freeCols.isEmpty()) {
            val x = IntArray(numButtons)
            for (r in 0 until rank) {
                val pc = pivotCols[r]
                x[pc] = mat[r][numButtons]
            }
            return x.sum()
        }

        var best = Int.MAX_VALUE
        val freeCount = freeCols.size
        val maxMask = 1 shl freeCount
        for (mask in 0 until maxMask) {
            val x = IntArray(numButtons)
            for ((i, fc) in freeCols.withIndex()) {
                x[fc] = (mask shr i) and 1
            }
            for (r in 0 until rank) {
                val pc = pivotCols[r]
                var value = mat[r][numButtons]
                for (fc in freeCols) {
                    if (mat[r][fc] == 1) value = value xor x[fc]
                }
                x[pc] = value
            }
            val ones = x.sum()
            if (ones < best) best = ones
        }

        return best
    }

    fun findButtonPressesForTargetButtonJoltages(): Int {
        val ctx = Context()
        val optimizer = ctx.mkOptimize()

        val buttonVars =
            (0 until buttons.size).map { i ->
                ctx.mkIntConst("k$i")
            }

        // Each button press count must be >= 0
        buttonVars.forEach { v ->
            optimizer.Add(ctx.mkGe(v, ctx.mkInt(0)))
        }

        // For each joltage counter, the sum of button presses must equal target
        for (counterIdx in 0 until targetButtonJoltages.size) {
            val terms = mutableListOf<com.microsoft.z3.ArithExpr<*>>()
            for (buttonIdx in 0 until buttons.size) {
                // If this button affects this counter, add it to the sum
                if (buttons[buttonIdx][counterIdx]) {
                    terms.add(buttonVars[buttonIdx])
                }
            }

            if (terms.isEmpty()) {
                // No button affects this counter, must be 0
                if (targetButtonJoltages[counterIdx] != 0) {
                    error("Counter $counterIdx cannot reach ${targetButtonJoltages[counterIdx]}")
                }
            } else {
                val sum = ctx.mkAdd(*terms.toTypedArray())
                optimizer.Add(ctx.mkEq(sum, ctx.mkInt(targetButtonJoltages[counterIdx])))
            }
        }

        // Minimize total button presses
        val total = ctx.mkAdd(*buttonVars.toTypedArray())
        optimizer.MkMinimize(total)

        // Solve
        if (optimizer.Check() == Status.SATISFIABLE) {
            val model = optimizer.model
            return model.eval(total, false).toString().toInt()
        } else {
            error("No solution found for joltage configuration")
        }
    }

    companion object {
        fun fromInput(input: String): Machine {
            val desiredState = input.substringAfter("[").substringBefore("]").map { it == '#' }

            val buttonPattern = Regex("""\(([0-9,]+)\)""")
            val buttons =
                buttonPattern
                    .findAll(input)
                    .map { match ->
                        val indices = match.groupValues[1].split(",").map { it.toInt() }
                        List(desiredState.size) { lightIndex -> lightIndex in indices }
                    }.toList()

            val joltages =
                input
                    .substringAfter("{")
                    .substringBefore("}")
                    .split(",")
                    .map { it.trim().toInt() }

            return Machine(desiredState, joltages, buttons)
        }
    }
}
