@file:Suppress("unused")

package util.collections

import util.DataPoint
import util.Direction
import util.extensions.toward
import java.util.LinkedList
import java.util.Queue

@Suppress("MemberVisibilityCanBePrivate")
open class Matrix<T>(initialContents: List<List<T>>) : Iterable<List<T>> {
    private var grid: MutableList<MutableList<T>> =
        validate(initialContents).map { it.map { r -> r }.toMutableList() }.toMutableList()

    constructor(width: Int, height: Int, fill: T) : this(List(height) { List(width) { fill } })

    fun drawLine(
        start: Pair<Int, Int>,
        end: Pair<Int, Int>,
        fill: T,
    ) {
        if (start.first == end.first) {
            // Vertical line
            for (row in start.second toward end.second) {
                setPoint(row, start.first, fill)
            }
        } else if (start.second == end.second) {
            // Horizontal line
            for (col in start.first toward end.first) {
                setPoint(start.second, col, fill)
            }
        } else {
            TODO("Diagonal lines not implemented yet")
        }
    }

    /**
     * Get all neighboring points for the given row and column coordinates.
     * @param row the row coordinate of the starting point
     * @param col the column coordinate of the starting point
     * @param includeDiagonal a flag indicating if diagonal neighboring points should be returned. This is `false` by default, indicating that only vertical and horizontal neighbors are returned.
     * @param pointFilter an optional function to further inspect the value of a potential neighboring point. This is useful if you have a weighted graph, or otherwise want to only move in a direction under certain criteria.
     */
    fun getNeighboringPoints(
        row: Int,
        col: Int,
        includeDiagonal: Boolean = false,
        pointFilter: (currentPoint: DataPoint<T>, neighboringPoint: DataPoint<T>) -> Boolean = { _, _ -> true },
    ): Map<Direction, DataPoint<T>> =
        Direction.entries.filter { includeDiagonal || !it.diagonal }.filter {
            (row + it.yOffset < height) && (row + it.yOffset >= 0) &&
                (col + it.xOffset < width) && (col + it.xOffset >= 0)
        }.associateWith {
            pointAt(row + it.yOffset, col + it.xOffset)
        }.filter {
            pointFilter(pointAt(row, col), it.value)
        }

    /**
     * A Breadth-first search to find distances from any point in the matrix to the given end point. This will return a map with the key
     * being a starting point that is able to reach the end, and the value being the number of points in the path to the end (including the
     * start and end points).
     * @param end the end point where we want to traverse from to find valid starting points and distances
     * @param allowDiagonal indicates whether diagonal movement is allowed. By default, this is `false`, only allowing movement up, down, left, and right
     * @param pointFilter an optional function to further inspect the value of a potential neighboring point. This is useful if you have a weighted graph, or otherwise want to only move in a direction under certain criteria.
     * @return a Map of Point to Int values, indicating the distance from the end for each starting point
     */
    fun findAllPaths(
        end: DataPoint<T>,
        allowDiagonal: Boolean = false,
        pointFilter: (currentPoint: DataPoint<T>, neighboringPoint: DataPoint<T>) -> Boolean = { _, _ -> true },
    ): Map<DataPoint<T>, List<DataPoint<T>>> {
        val queue = LinkedList(listOf(end to Path(end))) as Queue<Pair<DataPoint<T>, Path<T>>>

        val pointDistances = mutableMapOf(end to Path(end))

        while (queue.any()) {
            val (point, path) = queue.remove()
            queue.addAll(
                getNeighboringPoints(point.y, point.x, allowDiagonal) { current, neighbor ->
                    pointFilter(current, neighbor) && !pointDistances.containsKey(neighbor)
                }.map {
                    val newPath =
                        path.clone().apply {
                            add(it.value.copy())
                        }
                    pointDistances[it.value] = newPath
                    it.value to newPath
                },
            )
        }

        return pointDistances.map { it.key to it.value.points.reversed() }.toMap()
    }

    /**
     * A Breadth-First Search to find the shortest distance from start to end. This will return a List of Points, in order, to follow to get from start to end.
     * @param start the starting point where we want to traverse from
     * @param end the ending point where we want to traverse to
     * @param allowDiagonal indicates whether diagonal movement is allowed. By default, this is `false`, only allowing movement up, down, left, and right
     * @param pointFilter an optional function to further inspect the value of a potential neighboring point. This is useful if you have a weighted graph, or otherwise want to only move in a direction under certain criteria.
     * @return a List of Points, from start to end, indicating the steps to take. This will be an empty list of no path can be found from start to end.
     */
    fun findShortestPath(
        start: DataPoint<T>,
        end: DataPoint<T>,
        allowDiagonal: Boolean = false,
        pointFilter: (currentPoint: DataPoint<T>, neighboringPoint: DataPoint<T>) -> Boolean = { _, _ -> true },
    ) = findAllPaths(end, allowDiagonal, pointFilter).filter { it.key == start }.ifEmpty { emptyMap() }.values.first()

    fun findLongestPath(
        start: DataPoint<T>,
        end: DataPoint<T>,
        allowDiagonal: Boolean = false,
        pointFilter: (currentPoint: DataPoint<T>, neighboringPoint: DataPoint<T>) -> Boolean = { _, _ -> true },
    ) = findAllPaths(end, allowDiagonal, pointFilter).filter { it.key == start }.ifEmpty { emptyMap() }.values.last()

    /**
     * Turn this matrix on it's side, and return the new representation. By transposing, the first row becomes the first column,
     * the second row becomes the second column, and so on.
     *
     * For example, given the following data:
     * ```
     * 10 11 12 13
     * 20 21 22 23
     * 30 31 32 33
     * ```
     * After the [transpose] operation, the data will look like this:
     * ```
     * 10 20 30
     * 11 21 31
     * 12 22 32
     * 13 23 33
     * ```
     */
    fun transpose(): Matrix<T> {
        val rows = grid.size
        val cols = grid[0].size

        val transposed =
            Array<Array<Any>>(cols) {
                Array(rows) { }
            }

        for (i in 0 until rows) {
            for (j in 0 until cols) {
                transposed[j][i] = grid[i][j] as Any
            }
        }

        grid =
            transposed.map {
                @Suppress("UNCHECKED_CAST")
                it.toMutableList() as MutableList<T>
            }.toMutableList()
        return this
    }

    fun expand(
        left: Int = 0,
        right: Int = 0,
        up: Int = 0,
        down: Int = 0,
        fill: T,
    ): Matrix<T> {
        var newMatrix = Matrix(grid)
        if (down > 0) {
            newMatrix = Matrix(newMatrix.grid + List(down) { List(newMatrix.width) { fill } })
        }

        if (up > 0) {
            newMatrix = Matrix(List(up) { List(newMatrix.width) { fill } } + newMatrix.grid)
        }

        if (left > 0) {
            newMatrix = Matrix(newMatrix.grid.map { List(left) { fill } + it })
        }

        if (right > 0) {
            newMatrix = Matrix(newMatrix.grid.map { it + List(right) { fill } })
        }

        return newMatrix
    }

    private fun validate(contents: List<List<T>>): List<List<T>> {
        val columns = contents[0].size
        contents.all { row -> row.size == columns } || throw IllegalArgumentException("All rows in a matrix must have $columns columns")

        return contents
    }

    val width: Int
        get() = grid[0].size

    val height: Int
        get() = grid.size

    fun row(rowNum: Int): List<T> = grid[rowNum]

    fun column(colNum: Int): List<T> =
        grid.map {
            it[colNum]
        }

    fun pointAt(
        row: Int,
        col: Int,
    ): DataPoint<T> = DataPoint(col, row, grid[row][col])

    fun find(pointFilter: (currentPoint: T) -> Boolean): List<DataPoint<T>> {
        return grid.mapIndexed { y, row ->
            row.mapIndexedNotNull { x, t ->
                if (pointFilter(t)) {
                    DataPoint(x, y, t)
                } else {
                    null
                }
            }
        }.flatten()
    }

    fun points(): Set<DataPoint<T>> {
        return grid.mapIndexed { y, row ->
            row.mapIndexed { x, t -> DataPoint(x, y, t) }
        }.flatten().toSet()
    }

    fun setPoint(
        row: Int,
        col: Int,
        value: T,
    ) = if (isValidPoint(row, col)) {
        grid[row][col] = value
        true
    } else {
        false
    }

    fun isValidPoint(
        row: Int,
        col: Int,
    ) = (row in 0 until height) && (col in 0..width)

    fun pointsWithinDistance(
        row: Int,
        col: Int,
        distance: Int,
        includeOffGrid: Boolean = false,
        fill: T? = null,
    ): List<DataPoint<T>> {
        if (includeOffGrid) {
            requireNotNull(fill) {
                "Fill is required if including off-grid points"
            }
        }

        val start = pointAt(row, col)

        return (col - distance - 1 toward col + distance).map { y ->
            (row - distance - 1 toward row + distance).mapNotNull { x ->
                val next =
                    if (isValidPoint(y, x)) {
                        pointAt(y, x)
                    } else if (includeOffGrid) {
                        DataPoint<T>(x, y, fill!!)
                    } else {
                        null
                    }

                if (next != null && start.distanceFrom(next) <= distance && next != start) {
                    next
                } else {
                    null
                }
            }
        }.flatten()
    }

    override fun iterator() = grid.iterator()

    override fun equals(other: Any?): Boolean {
        return other != null && this::class == other::class && this.grid == (other as Matrix<*>).grid
    }

    override fun hashCode(): Int {
        return grid.hashCode()
    }

    override fun toString(): String {
        return grid.joinToString("\n") { row -> row.joinToString("") }
    }
}

internal data class Path<T>(val start: DataPoint<T>) : Cloneable {
    private val _points = linkedSetOf(start)

    val points: List<DataPoint<T>>
        get() = _points.toList()

    fun add(point: DataPoint<T>) = _points.add(point)

    public override fun clone() =
        Path(this.start).apply {
            this@Path.points.map { it.copy() }.forEach {
                add(it)
            }
        }
}
