@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package util.collections

/**
 * Wrote this as a replacement for the built-in [LongRange] for the [2023 Advent of Code, day 5](https://adventofcode.com/2023/day/5).
 * Using elementAt and indexOf on the LongRange proved to take way too long, so this works much quicker. These methods don't
 * handle steps or other more complex functions found in the kotlin LongRange, but for this purpose, they don't need to. I
 * just needed a quick way to encapsulate a Range, and be able to check if a number is in the range, and to get the index
 * of such a number in the range, and get the Nth element in the range - again, assuming the range is sequential, positive
 * increment, step by 1, etc.
 */
class FastLongRange(val start: Long, val length: Long) {
    fun contains(n: Long): Boolean = n >= start && n <= start + length - 1

    fun indexOf(n: Long): Long = n - start - 1

    fun elementAt(n: Long): Long = start + n + 1
}

class FastIntRange(val start: Int, val length: Int) {
    fun contains(n: Int): Boolean = n >= start && n <= start + length - 1

    fun indexOf(n: Int): Int = n - start - 1

    fun elementAt(n: Int): Int = start + n + 1
}
