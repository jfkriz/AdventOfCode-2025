package day08

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles

@DisplayName("Day 08 - Playground")
@TestMethodOrder(OrderAnnotation::class)
class PlaygroundTest : DataFiles {
    private val sampleSolver by lazy {
        Solver(loadSampleInput())
    }
    private val solver by lazy {
        Solver(loadInput())
    }

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 40`() {
        assertEquals(40, sampleSolver.solvePartOne(10))
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 25272`() {
        assertEquals(25272, sampleSolver.solvePartTwo())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 69192`() {
        assertEquals(69192, solver.solvePartOne(1000))
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 7264308110`() {
        assertEquals(7264308110, solver.solvePartTwo())
    }
}

class Solver(
    data: List<String>,
) {
    private val junctions =
        data
            .map { it.trim().split(",") }
            .map { it.map(Integer::parseInt) }
            .map { Junction(it[0], it[1], it[2]) }

    private val numJunctions = junctions.size

    private val distancePairs =
        (0 until numJunctions)
            .flatMap { i ->
                (i + 1 until numJunctions).map { j ->
                    DistancePair(
                        junctions[i].distanceTo(junctions[j]),
                        i,
                        j,
                    )
                }
            }.sortedBy { it.dist }

    fun solvePartOne(numPairs: Int): Long = solve(numPairs)

    fun solvePartTwo(): Long = solve(-1)

    private fun solve(k: Int = 1000): Long {
        val dsu = DSU(numJunctions)

        if (k > 0) {
            // Part 1 - connect first k edges and return product of sizes of largest 3 components
            val firstK = distancePairs.take(k)
            for (e in firstK) {
                dsu.union(e.a, e.b)
            }

            // Count component sizes
            val counts = mutableMapOf<Int, Int>()
            for (i in 0 until numJunctions) {
                val r = dsu.find(i)
                counts[r] = (counts[r] ?: 0) + 1
            }

            val sizes = counts.values.sortedDescending()
            return sizes.take(3).fold(1L) { acc, s -> acc * s }
        } else {
            // Part 2 - fully connect the graph and return product of last edge's x-coordinates
            var components = numJunctions
            var lastDistancePair: DistancePair?

            for (e in distancePairs) {
                val ra = dsu.find(e.a)
                val rb = dsu.find(e.b)

                if (ra != rb) {
                    dsu.union(ra, rb)
                    components--
                    lastDistancePair = e

                    if (components == 1) {
                        // fully connected!
                        val p1 = junctions[lastDistancePair.a]
                        val p2 = junctions[lastDistancePair.b]
                        return p1.x.toLong() * p2.x.toLong()
                    }
                }
            }

            error("Graph should always become fully connected")
        }
    }
}

data class Junction(
    val x: Int,
    val y: Int,
    val z: Int,
) {
    fun distanceTo(other: Junction): Long {
        val dx = (x - other.x).toLong()
        val dy = (y - other.y).toLong()
        val dz = (z - other.z).toLong()
        return dx * dx + dy * dy + dz * dz
    }
}

data class DistancePair(
    val dist: Long,
    val a: Int,
    val b: Int,
)

// Basic union-find (DSU)
class DSU(
    n: Int,
) {
    private val parent = IntArray(n) { it }
    private val rank = IntArray(n)

    fun find(x: Int): Int {
        if (parent[x] != x) {
            parent[x] = find(parent[x])
        }
        return parent[x]
    }

    fun union(
        a: Int,
        b: Int,
    ) {
        val ra = find(a)
        val rb = find(b)
        if (ra == rb) return

        if (rank[ra] < rank[rb]) {
            parent[ra] = rb
        } else if (rank[rb] < rank[ra]) {
            parent[rb] = ra
        } else {
            parent[rb] = ra
            rank[ra]++
        }
    }
}
