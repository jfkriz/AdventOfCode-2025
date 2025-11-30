@file:Suppress("unused")

package util.extensions

import util.Point

fun IntRange.encloses(other: IntRange) = this.contains(other.first) && this.contains(other.last)

fun IntRange.overlaps(other: IntRange) =
    (this.contains(other.first) || this.contains(other.last)) ||
        (other.contains(this.first) || other.contains(this.last))

/**
 * Chunks the List<String> into a List<List<String>>, where chunks are broken up by the delimiter indicated (default is blank).
 * @return a List of Lists of Strings - List<List<String>> - with each sublist representing the groups of
 * lines from the input.
 */
fun List<String>.chunked(delimiter: String = ""): List<List<String>> =
    fold<String, MutableList<MutableList<String>>>(mutableListOf(mutableListOf())) { groups, next ->
        if (next.trim() == delimiter) {
            groups.add(mutableListOf())
        } else {
            groups.last().add(next)
        }
        groups
    }

/**
 * Given a list of Strings, make sure all strings are the same length, and pad them with the given [padChar] if not.
 * This is mainly here because IntelliJ will trim trailing spaces from lines in files by default, so input lines
 * that would otherwise be the same length end up not being the proper length. This is important if you expect to
 * split an input line on a certain delimiter or at a certain chunk size, and then turn that into a [util.collections.Matrix],
 * since the Matrix requires all input lines to be equal length.
 */
fun List<String>.padToMaxLength(padChar: Char): List<String> =
    with(this.maxOf { it.length }) {
        map {
            it.padEnd(this, padChar)
        }
    }

/**
 * A convenience so that you can make a range without having to worry about positive or negative
 * step. If the starting position were greater than the ending position, you'd normally need to
 * add a -1 for the step - this takes care of that.
 */
infix fun Int.toward(to: Int): IntProgression {
    val step = if (this > to) -1 else 1
    return IntProgression.fromClosedRange(this, to, step)
}

/**
 * Take a list of int ranges and reduce them so that any overlapping segments are removed. This would be the
 * same as creating a [Set] for each range, and then doing a [Set.union] on them. But for really large
 * ranges, like those found on [Day 15 of the 2022 Advent of Code](https://adventofcode.com/2022/day/15). With
 * the resulting list of ranges, you can easily count the number of unique positions represented by each.
 */
fun List<IntRange>.reduce(): List<IntRange> =
    if (this.size <= 1) {
        this
    } else {
        val sorted = this.sortedBy { it.first }
        sorted.drop(1).fold(mutableListOf(sorted.first())) { reduced, range ->
            val lastRange = reduced.last()
            if (range.first <= lastRange.last) {
                reduced[reduced.lastIndex] = (lastRange.first..maxOf(lastRange.last, range.last))
            } else {
                reduced.add(range)
            }
            reduced
        }
    }

/**
 * Replace the last occurrence of [oldValue] with [newValue] in the string. This was originally added for
 * [Day 1 of the 2023 Advent of Code](https://adventofcode.com/2023/day/1), so we could easily find the last
 * occurrence of a number word (like "one") in a string, and replace it with the numeric value ("1").
 */
fun String.replaceLast(
    oldValue: String,
    newValue: String,
): String = this.reversed().replaceFirst(oldValue.reversed(), newValue.reversed()).reversed()

/**
 * Compute the lowest common multiple for a list of numbers. This is the smallest number that is evenly
 * divisible by every number in the list.
 */
fun List<Long>.lcm(): Long =
    assertValue(
        this,
        this.size >= 2,
    ) { "You must provide at least two numbers to compute lcm" }.reduce { acc, l -> acc.lcm(l) }

/**
 * Compute the lowest common multiple for this number and another. This is the smallest number that is evenly
 * divisible by both numbers.
 */
fun Long.lcm(other: Long): Long = (this * other) / this.gcd(other)

/**
 * Compute the greatest common denominator for this number and another number. This is the largest number that
 * divides evenly with zero remainder into both numbers.
 */
fun Long.gcd(other: Long): Long =
    if (other == 0L) {
        this
    } else {
        other.gcd(this % other)
    }

/**
 * Compute the greatest common denominator for a list of numbers. This is the largest number that
 * divides evenly with zero remainder into all numbers in the list.
 */
fun List<Long>.gcd(): Long =
    assertValue(
        this,
        this.size >= 2,
    ) { "You must provide at least two numbers to compute gcd" }.reduce { acc, l -> acc.gcd(l) }

/**
 * A convenience method to assert that a given [value] passes the specified boolean [test]. This will
 * return the [value] if it passes the test, or will throw an exception with the given [lazyMessage]
 * if it fails.
 */
fun <T> assertValue(
    value: T,
    test: Boolean,
    lazyMessage: () -> Any,
): T {
    assert(test) { lazyMessage }
    return value
}

/**
 * Determine the number of distinct combinations of [size] are possible given all the elements in the given sequence.
 * This was initially implemented for [Day 11 of the 2023 Advent of Code](https://adventofcode.com/2023/day/11).
 */
fun <T> Sequence<T>.combinations(size: Int): Sequence<List<T>> =
    sequence {
        if (size > 0) {
            for ((i, element) in withIndex()) {
                val remaining = drop(i + 1)
                for (combination in remaining.combinations(size - 1)) {
                    yield(combination + listOf(element))
                }
            }
        } else {
            yield(emptyList())
        }
    }

/**
 * Determine the number of distinct permutations of [size] are possible given all the elements in the given sequence.
 * This was initially implemented for [Day 7 of the 2024 Advent of Code](https://adventofcode.com/2024/day/7).
 */
fun <T> Sequence<T>.permutations(size: Int): Sequence<List<T>> {
    require(size >= 0) { "Size must be non-negative" }

    if (size == 0) return sequenceOf(emptyList())

    return sequence {
        for (item in this@permutations) {
            for (perm in this@permutations.permutations(size - 1)) {
                yield(listOf(item) + perm)
            }
        }
    }
}

/**
 * Repeat a string a number of [times], with a [separator] between each repeated string.
 * For example, "foo".repeat(3, "bar-") will return "foobar-foobar-foobar"
 */
fun String.repeatWithSeparator(
    times: Int,
    separator: String,
): String = (1..times).joinToString(separator) { this }

/**
 * Create a String by repeating any Iterable a number of [times], with an [iterableSeparator] between each item in the
 * iterable, and a [separator] between each copy of the iterable.
 * For example, listOf("foo", "bar", "baz").repeat(3, "-", ":") will return "foo-bar-baz:foo-bar-baz:foo-bar-baz"
 */
fun <T> Iterable<T>.repeatWithSeparator(
    times: Int,
    iterableSeparator: String = ",",
    separator: String = ",",
): String = (1..times).joinToString(separator) { this.joinToString(iterableSeparator) }

/**
 * Rotate a 2D grid clockwise, 90 degrees to the right.
 * Given an initial grid:
 * 1 2 3
 * 4 5 6
 * 7 8 9
 *
 * After rotating right, it will be:
 * 7 4 1
 * 8 5 2
 * 9 6 3
 */
inline fun <reified T> List<List<T>>.rotateRight(): List<List<T>> {
    val rows = this.size
    val cols = this[0].size

    val rotatedGrid = Array(cols) { Array<T?>(rows) { null } }

    for (row in 0 until rows) {
        for (col in 0 until cols) {
            rotatedGrid[col][rows - 1 - row] = this[row][col]
        }
    }

    @Suppress("UNCHECKED_CAST")
    return rotatedGrid.map { it.toList() } as List<List<T>>
}

/**
 * Rotate a 2D grid counter-clockwise, 90 degrees to the left.
 * Given an initial grid:
 * 1 2 3
 * 4 5 6
 * 7 8 9
 *
 * After rotating left, it will be:
 * 3 6 9
 * 2 5 8
 * 1 4 7
 */
inline fun <reified T> List<List<T>>.rotateLeft(): List<List<T>> = this.rotateRight().map { it.reversed() }.reversed()

operator fun List<List<*>>.contains(point: Point): Boolean = point.x in this[0].indices && point.y in this.indices

operator fun <T> List<List<T>>.get(point: Point): T = this[point.y][point.x]

operator fun <T> List<MutableList<T>>.set(
    point: Point,
    value: T,
) {
    this[point.y][point.x] = value
}

fun <T> List<List<T>>.getOrNull(point: Point): T? = if (point in this) this[point] else null

fun <T> List<List<T>>.getOrDefault(
    point: Point,
    default: T,
): T = getOrNull(point) ?: default
