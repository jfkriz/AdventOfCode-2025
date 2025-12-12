package day11

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles

@DisplayName("Day 11 - Reactor")
@TestMethodOrder(OrderAnnotation::class)
class ReactorTest : DataFiles {
    private val sampleSolver by lazy {
        Solver(loadSampleInput())
    }
    private val partTwoSampleSolver by lazy {
        Solver(loadOtherInput("test-input-part2.txt"))
    }
    private val solver by lazy {
        Solver(loadInput())
    }

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 5`() {
        assertEquals(5, sampleSolver.solvePartOne())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 2`() {
        assertEquals(2L, partTwoSampleSolver.solvePartTwo())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 662`() {
        assertEquals(662, solver.solvePartOne())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 429399933071120`() {
        // 1987718928 too low - haha, had to switch to Long for this one, doh!
        assertEquals(429399933071120L, solver.solvePartTwo())
    }
}

class Solver(
    data: List<String>,
) {
    private val devices: List<Device> = data.map { Device.fromString(it) }

    fun solvePartOne(): Int = findAllPathsToTerminalOutputsFromStart(devices.first { it.isYouDevice }).size

    fun solvePartTwo(): Long = countAllPathsFromStartToTerminalOutputsWithDacAndFft()

    private fun countAllPathsFromStartToTerminalOutputsWithDacAndFft(): Long {
        val deviceMap = devices.associateBy { it.id }
        val serverDevice = devices.first { it.isServerDevice }
        val start = deviceMap[serverDevice.id]!!

        // Memoization: (deviceId, visitedDac, visitedFft) -> count of valid paths
        val memo = mutableMapOf<Triple<String, Boolean, Boolean>, Long>()

        fun dfs(
            current: Device,
            visitedDac: Boolean,
            visitedFft: Boolean,
        ): Long {
            val newVisitedDac = visitedDac || current.isDacDevice
            val newVisitedFft = visitedFft || current.isFftDevice

            val state = Triple(current.id, newVisitedDac, newVisitedFft)
            memo[state]?.let { return it }

            if (current.isTerminalOutput) {
                val result = if (newVisitedDac && newVisitedFft) 1L else 0L
                memo[state] = result
                return result
            }

            var count = 0L
            for (outputId in current.outputs) {
                val nextDevice = deviceMap[outputId] ?: continue
                count += dfs(nextDevice, newVisitedDac, newVisitedFft)
            }

            memo[state] = count
            return count
        }

        return dfs(start, false, false)
    }

    private fun findAllPathsToTerminalOutputsFromStart(start: Device): List<List<Device>> {
        val paths = mutableListOf<List<Device>>()

        fun dfs(
            current: Device,
            path: List<Device>,
        ) {
            if (current.isTerminalOutput) {
                paths.add(path + current)
                return
            }
            for (outputId in current.outputs) {
                val nextDevice = devices.find { it.id == outputId } ?: continue
                dfs(nextDevice, path + current)
            }
        }

        dfs(start, emptyList())
        return paths
    }
}

data class Device(
    val id: String,
    val outputs: List<String>,
) {
    val isYouDevice = id == "you"

    val isServerDevice = id == "svr"

    val isTerminalOutput = outputs.size == 1 && outputs[0] == "out"

    val isDacDevice = id == "dac"

    val isFftDevice = id == "fft"

    companion object {
        fun fromString(input: String): Device {
            val parts = input.split(":").map { it.trim() }
            val id = parts[0]
            val outputs = parts[1].split(" ").map { it.trim() }
            return Device(id, outputs)
        }
    }
}
