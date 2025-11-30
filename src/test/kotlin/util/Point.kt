@file:Suppress("unused")

package util

import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.sign

open class Point(
    var x: Int,
    var y: Int,
) {
    companion object {
        val UP = Point(0, -1)
        val DOWN = Point(0, 1)
        val LEFT = Point(-1, 0)
        val RIGHT = Point(1, 0)
    }

    operator fun plus(other: Point) = Point(x + other.x, y + other.y)

    operator fun minus(other: Point) = Point(x - other.x, y - other.y)

    operator fun times(scalar: Int) = Point(x * scalar, y * scalar)

    fun move(direction: Direction): Point = Point(x + direction.xOffset, y + direction.yOffset)

    fun move(
        xCount: Int,
        yCount: Int,
    ): Point = Point(x + xCount, y + yCount)

    fun isSameLocation(other: Point) = this.x == other.x && this.y == other.y

    fun isNeighboringLocation(
        other: Point,
        includeDiagonal: Boolean = true,
    ) = Direction.entries
        .filter {
            if (includeDiagonal) {
                true
            } else {
                !it.diagonal
            }
        }.map {
            (x + it.xOffset) to (y + it.yOffset)
        }.any {
            it.first == other.x && it.second == other.y
        }

    fun differenceWith(other: Point) = (this.x - other.x) to (this.y - other.y)

    fun distanceFrom(other: Point) = abs(this.x - other.x) + abs(this.y - other.y)

    fun lineTo(other: Point): List<Point> {
        val xDelta = (other.x - x).sign
        val yDelta = (other.y - y).sign
        val steps = maxOf((x - other.x).absoluteValue, (y - other.y).absoluteValue)
        return (1..steps).scan(this) { last, _ -> Point(last.x + xDelta, last.y + yDelta) }
    }

    val neighbors: Map<Direction, Point>
        get() = Direction.entries.associateWith { Point(x + it.xOffset, y + it.yOffset) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Point

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }

    open fun copy(): Point = Point(x, y)
}

open class DataPoint<T>(
    x: Int,
    y: Int,
    var value: T,
) : Point(x, y) {
    fun lineTo(
        other: DataPoint<*>,
        fill: T,
    ) = lineTo(other).map { DataPoint(it.x, it.y, fill) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataPoint<*>

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }

    override fun copy(): DataPoint<T> = DataPoint(x, y, value)
}

enum class Direction(
    val xOffset: Int,
    val yOffset: Int,
    val diagonal: Boolean = false,
) {
    Up(0, -1),
    Down(0, 1),
    Left(-1, 0),
    Right(1, 0),
    UpLeft(-1, -1, true),
    DownLeft(-1, 1, true),
    UpRight(1, -1, true),
    DownRight(1, 1, true),
    ;

    fun rotateClockwise(includeDiagonal: Boolean = true): Direction =
        when (this) {
            Up -> if (includeDiagonal) UpRight else Right
            Right -> if (includeDiagonal) DownRight else Down
            Down -> if (includeDiagonal) DownLeft else Left
            Left -> if (includeDiagonal) UpLeft else Up
            UpRight -> if (includeDiagonal) Right else throw IllegalArgumentException("Cannot rotate diagonal direction")
            DownRight -> if (includeDiagonal) Down else throw IllegalArgumentException("Cannot rotate diagonal direction")
            DownLeft -> if (includeDiagonal) Left else throw IllegalArgumentException("Cannot rotate diagonal direction")
            UpLeft -> if (includeDiagonal) Up else throw IllegalArgumentException("Cannot rotate diagonal direction")
        }

    fun rotateCounterClockwise(includeDiagonal: Boolean = true): Direction =
        when (this) {
            Up -> if (includeDiagonal) UpLeft else Left
            Right -> if (includeDiagonal) UpRight else Up
            Down -> if (includeDiagonal) DownRight else Right
            Left -> if (includeDiagonal) DownLeft else Down
            UpRight -> if (includeDiagonal) Up else throw IllegalArgumentException("Cannot rotate diagonal direction")
            DownRight -> if (includeDiagonal) Right else throw IllegalArgumentException("Cannot rotate diagonal direction")
            DownLeft -> if (includeDiagonal) Down else throw IllegalArgumentException("Cannot rotate diagonal direction")
            UpLeft -> if (includeDiagonal) Left else throw IllegalArgumentException("Cannot rotate diagonal direction")
        }

    companion object {
        fun fromChar(char: Char): Direction =
            when (char) {
                in listOf('U', 'u', '^') -> Up
                in listOf('D', 'd', 'V', 'v') -> Down
                in listOf('L', 'l', '<') -> Left
                in listOf('R', 'r', '>') -> Right
                else -> throw IllegalArgumentException("Unknown Direction: $char")
            }
    }
}
