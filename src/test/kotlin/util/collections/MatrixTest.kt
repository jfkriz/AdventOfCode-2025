package util.collections

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import util.Direction

class MatrixTest {
    @Test
    fun `Should properly transpose square matrix`() {
        val data =
            listOf(
                listOf(10, 11, 12),
                listOf(20, 21, 22),
                listOf(30, 31, 32),
            )

        val expected =
            Matrix(
                listOf(
                    listOf(10, 20, 30),
                    listOf(11, 21, 31),
                    listOf(12, 22, 32),
                ),
            )

        val fixture = Matrix(data)
        val transposed = fixture.transpose()
        assertEquals(expected, transposed)
    }

    @Test
    fun `Should properly transpose non-square matrix`() {
        val data =
            listOf(
                listOf(10, 11, 12, 13),
                listOf(20, 21, 22, 23),
                listOf(30, 31, 32, 33),
            )

        val expected =
            Matrix(
                listOf(
                    listOf(10, 20, 30),
                    listOf(11, 21, 31),
                    listOf(12, 22, 32),
                    listOf(13, 23, 33),
                ),
            )

        val fixture = Matrix(data)
        val transposed = fixture.transpose()
        assertEquals(expected, transposed)
    }

    @Test
    fun `Should get proper row`() {
        val data =
            listOf(
                listOf(10, 11, 12, 13),
                listOf(20, 21, 22, 23),
                listOf(30, 31, 32, 33),
            )

        assertEquals(listOf(20, 21, 22, 23), Matrix(data).row(1))
    }

    @Test
    fun `Should get proper column`() {
        val data =
            listOf(
                listOf(10, 11, 12, 13),
                listOf(20, 21, 22, 23),
                listOf(30, 31, 32, 33),
            )

        assertEquals(listOf(11, 21, 31), Matrix(data).column(1))
    }

    @Test
    fun `Should get 4 neighboring points when diagonals are not allowed`() {
        val data =
            listOf(
                listOf(10, 11, 12),
                listOf(20, 21, 22),
                listOf(30, 31, 32),
            )

        val m = Matrix(data)
        val neighboring = m.getNeighboringPoints(1, 1)
        assertEquals(4, neighboring.size)
        assertTrue(neighboring.containsKey(Direction.Up))
        assertEquals(11, neighboring[Direction.Up]!!.value)
        assertTrue(neighboring.containsKey(Direction.Down))
        assertEquals(31, neighboring[Direction.Down]!!.value)
        assertTrue(neighboring.containsKey(Direction.Left))
        assertEquals(20, neighboring[Direction.Left]!!.value)
        assertTrue(neighboring.containsKey(Direction.Right))
        assertEquals(22, neighboring[Direction.Right]!!.value)
    }

    @Test
    fun `Should get 2 neighboring points when diagonals are not allowed and filter is applied`() {
        val data =
            listOf(
                listOf(10, 11, 12),
                listOf(20, 21, 22),
                listOf(30, 31, 32),
            )

        val m = Matrix(data)
        val neighboring =
            m.getNeighboringPoints(1, 1) { currentPoint, neighboringPoint ->
                currentPoint.value > neighboringPoint.value
            }
        assertEquals(2, neighboring.size)
        assertTrue(neighboring.containsKey(Direction.Up))
        assertEquals(11, neighboring[Direction.Up]!!.value)
        assertTrue(neighboring.containsKey(Direction.Left))
        assertEquals(20, neighboring[Direction.Left]!!.value)
    }
}
