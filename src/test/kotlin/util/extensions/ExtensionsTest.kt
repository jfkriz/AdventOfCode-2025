package util.extensions

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExtensionsTest {
    @Test
    fun `lcm should work for large numbers`() {
        assertEquals(10818234074807L, listOf(17141L, 16579L, 18827L, 12083L, 13207L, 22199L).lcm())
        assertEquals(10241191004509L, listOf(13207L, 22199L, 14893L, 16579L, 20513L, 12083L).lcm())
    }

    @Test
    fun `rotateRight should properly rotate a non-square grid of data`() {
        val input =
            listOf(
                listOf('a', 'b', 'c', 'd'),
                listOf('e', 'f', 'g', 'h'),
                listOf('i', 'j', 'k', 'l'),
            )
        val expected =
            listOf(
                listOf('i', 'e', 'a'),
                listOf('j', 'f', 'b'),
                listOf('k', 'g', 'c'),
                listOf('l', 'h', 'd'),
            )
        assertEquals(expected, input.rotateRight())
    }

    @Test
    fun `rotateRight should properly rotate a square grid of data`() {
        val input =
            listOf(
                listOf(1, 2, 3),
                listOf(4, 5, 6),
                listOf(7, 8, 9),
            )
        val expected =
            listOf(
                listOf(7, 4, 1),
                listOf(8, 5, 2),
                listOf(9, 6, 3),
            )
        assertEquals(expected, input.rotateRight())
    }

    @Test
    fun `rotateLeft should properly rotate a non-square grid of data`() {
        val input =
            listOf(
                listOf('a', 'b', 'c', 'd'),
                listOf('e', 'f', 'g', 'h'),
                listOf('i', 'j', 'k', 'l'),
            )
        val expected =
            listOf(
                listOf('d', 'h', 'l'),
                listOf('c', 'g', 'k'),
                listOf('b', 'f', 'j'),
                listOf('a', 'e', 'i'),
            )
        assertEquals(expected, input.rotateLeft())
    }

    @Test
    fun `rotateLeft should properly rotate a square grid of data`() {
        val input =
            listOf(
                listOf(1, 2, 3),
                listOf(4, 5, 6),
                listOf(7, 8, 9),
            )
        val expected =
            listOf(
                listOf(3, 6, 9),
                listOf(2, 5, 8),
                listOf(1, 4, 7),
            )
        assertEquals(expected, input.rotateLeft())
    }
}
