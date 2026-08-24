package org.nebobrod.schulteplus.common

import kotlin.random.Random

/**
 * Замощение площади плитками разных размеров (squash-алгоритм).
 *
 * Перенос `TileSquashPaving.java` (этап 1, ТЗ «Мешанина», docs/TZ_Mishmash.md):
 * убраны статическое состояние, консольный вывод, debug-ветки и `Math.random()`;
 * детерминизм — `kotlin.random.Random(seed)`. Поведение алгоритма сохранено.
 *
 * Логическое поле 5×5 = 25 плиток; физическое поле `ROWS*2 × COLS*2` = 10×10.
 * Каждая плитка (requestor) случайно тянет/толкает одну из 4 сторон; соседи
 * отвечают: новые зависимости (причина рекурсивной проверки), пустые зависимости
 * (причина сдвига) или null (сдвиг невозможен). После цикла сжатий считается
 * индекс разнообразия размеров.
 */
class TilePaving(seed: Long) {

    private val random = Random(seed)

    // Main list of tiles
    private val tiles = ArrayList<Tile>(TILE_NUMBER)

    // Main field
    private val field = Array(ROWS * 2) { IntArray(COLS * 2) }
    private val height = field.size
    private val width = field[0].size

    private val tileQuantitiesBySize = IntArray(TILE_SIZES.size)

    /**
     * Выполняет замощение и возвращает лучшее поле 10×10:
     * каждая ячейка содержит номер плитки 1..25.
     */
    fun build(): Array<IntArray> {

        // Init the tile-list (num is assigned in Tile.set())
        for (i in 0 until TILE_NUMBER) {
            tiles.add(Tile())
        }

        // Define the field with tile.set() squared 2x2 by the field's coordinates
        newField()

        // Main cycle
        var cycle = 0
        var divMinReached = 0x7FFFFFFF // Max int
        var resultField: Array<IntArray>? = null

        while ((diversity10x() > DIVERSITY_MIN_TARGET) && (cycle <= CYCLES_LIMIT)) {
            // run Tightness
            for (t in tiles) {
                var dir = 1     // -1 or 1
                var side = 0
                var steps = 3
                while (steps-- > 0) {
                    dir = randomInt(0, 1) * 2 - 1   // -1 or 1
                    side = randomInt(0, 3)

                    // List of agreed neighbours
                    var dep = t.canMove(dir, side) ?: continue
                    dep = dependCheck(dir, side, dep, t.num) ?: continue
                    break
                }

                if (steps > 0) {
                    dependMove(dir, side, t.canMove(dir, side)!!, t.num)
                }
            }

            val div = diversity10x()
            if (divMinReached > div) {
                divMinReached = div
                resultField = copyField(field)
            }
            cycle++
        }

        return resultField ?: copyField(field)
    }

    /////////////////////////////////////
    /** Класс представляет плитку на {@link #field} */
    private inner class Tile {
        var num = 0             // number or other kind of symbol on the tile
        var rowAddress = 0      // y-coordinate of top-left cell
        var colAddress = 0      // x-coordinate of top-left cell
        var size = 0            // index in TILE_SIZES[]

        /**
         * Set the tile and stamp it on the field
         * @param size index in [TILE_SIZES] (0 means 2x2)
         */
        fun set(num: Int, rowAddress: Int, colAddress: Int, size: Int) {
            this.num = num
            this.rowAddress = rowAddress
            this.colAddress = colAddress
            if (this.size != size) {
                tileQuantitiesBySize[this.size]--
                tileQuantitiesBySize[size]++
                this.size = size
            }
            stamp()
        }

        /** Put tile's num into appropriate cells of the [field] */
        fun stamp() {
            for (row in rowAddress until rowAddress + TILE_SIZES[size][0]) {
                for (col in colAddress until colAddress + TILE_SIZES[size][1]) {
                    if ((field[row][col] == 0) || (field[row][col] == num)) {
                        field[row][col] = num                        // take a space
                    } else {
                        throw RuntimeException("NO SPACE TO STAMP!") // Check if not clean
                    }
                }
            }
        }

        fun setFieldFree() {
            for (row in rowAddress until rowAddress + TILE_SIZES[size][0]) {
                for (col in colAddress until colAddress + TILE_SIZES[size][1]) {
                    if ((field[row][col] == num) || (field[row][col] == 0)) {
                        field[row][col] = 0                          // set free a space
                    } else {
                        throw RuntimeException("NO FIELD!")          // Check if not clean
                    }
                }
            }
        }

        /**
         * Check borders and self-ability to move into new size
         * @param dir direction minus or plus 1 of ax
         * @param side 0 East, 1 South, 2 West, 3 North
         * @return list of neighbours should be moved also (before) excluding `this`,
         * or empty list (can move with no dependencies), or null (non-movable side)
         */
        fun canMove(dir: Int, side: Int): List<Int>? {
            val newSizeArrow = TILE_SIZES[size].copyOf()

            try {
                when (side) {
                    0 -> newSizeArrow[1] += dir
                    1 -> newSizeArrow[0] += dir
                    2 -> newSizeArrow[1] -= dir
                    3 -> newSizeArrow[0] -= dir
                    else -> {
                        newSizeArrow[0] = 0     // warranted future error of size-check
                        newSizeArrow[1] = 0
                    }
                }
            } catch (e: IndexOutOfBoundsException) {
                return null
            }

            val newSize = getTileSize(newSizeArrow)
            return if (newSize == -1) null else getDepends(side, num)
        }

        /**
         * Check every cell of field behind (outside) the requested side of move
         * @param side 0 East, 1 South, 2 West, 3 North
         * @param requestor 0 for self-request or requestor.num for recursive call
         * @return list of dependent tile.num (except of [requestor]),
         * or empty list (can move with no dependencies), or null (non-movable side)
         */
        fun getDepends(side: Int, requestor: Int): List<Int>? {
            var requestor = requestor
            if (requestor == 0) requestor = num
            val result = ArrayList<Int>()
            var checkNum = 0
            try {
                when (side) {
                    0 -> for (row in rowAddress until rowAddress + TILE_SIZES[size][0]) {
                        checkNum = field[row][colAddress + TILE_SIZES[size][1]]
                        if ((checkNum != 0) && (checkNum != num) && (checkNum != requestor)) {
                            if (!result.contains(checkNum)) result.add(checkNum)
                        }
                    }
                    1 -> for (col in colAddress until colAddress + TILE_SIZES[size][1]) {
                        checkNum = field[rowAddress + TILE_SIZES[size][0]][col]
                        if ((checkNum != 0) && (checkNum != num) && (checkNum != requestor)) {
                            if (!result.contains(checkNum)) result.add(checkNum)
                        }
                    }
                    2 -> for (row in rowAddress until rowAddress + TILE_SIZES[size][0]) {
                        checkNum = field[row][colAddress - 1]
                        if ((checkNum != 0) && (checkNum != num) && (checkNum != requestor)) {
                            if (!result.contains(checkNum)) result.add(checkNum)
                        }
                    }
                    3 -> for (col in colAddress until colAddress + TILE_SIZES[size][1]) {
                        checkNum = field[rowAddress - 1][col]
                        if ((checkNum != 0) && (checkNum != num) && (checkNum != requestor)) {
                            if (!result.contains(checkNum)) result.add(checkNum)
                        }
                    }
                    else -> result.clear()
                }
            } catch (e: IndexOutOfBoundsException) {
                return null
            }

            return result
        }

        fun move(dir: Int, side: Int) {
            val newSizeArrow = TILE_SIZES[size].copyOf()

            when (side) {
                0 -> newSizeArrow[1] += dir
                1 -> newSizeArrow[0] += dir
                2 -> {
                    colAddress += dir
                    newSizeArrow[1] -= dir
                }
                3 -> {
                    rowAddress += dir
                    newSizeArrow[0] -= dir
                }
                else -> {
                }
            }
            tileQuantitiesBySize[size]--
            size = getTileSize(newSizeArrow)
            stamp()
            tileQuantitiesBySize[size]++
        }
    }
    /////////////////////////////////////

    private fun dependCheck(dir: Int, side: Int, checkedTiles: List<Int>, requestor: Int): List<Int>? {
        var newTiles: List<Int> = ArrayList()

        for (t in checkedTiles) {
            if (tiles[t - 1].canMove(dir, getOpposite(side)) == null) return null

            newTiles = tiles[t - 1].getDepends(getOpposite(side), requestor) ?: return null
            newTiles = dependCheck(dir, getOpposite(side), newTiles, t) ?: return null
        }
        return newTiles
    }

    private fun dependMove(dir: Int, side: Int, checkedTiles: List<Int>, requestor: Int): List<Int>? {
        var newTiles: List<Int>? = null
        tiles[requestor - 1].setFieldFree()

        for (t in checkedTiles) {
            newTiles = tiles[t - 1].getDepends(getOpposite(side), requestor)

            if (newTiles.isNullOrEmpty()) {
                tiles[t - 1].setFieldFree()
                tiles[t - 1].move(dir, getOpposite(side))
            } else {
                val deps = dependMove(dir, getOpposite(side), newTiles, t) ?: return null
                newTiles = newTiles + deps
            }
        }

        tiles[requestor - 1].move(dir, side)
        return newTiles
    }

    private fun diversity10x(): Int {
        var div = 0f
        val ave = tiles.size.toFloat() / TILE_SIZES.size

        for (i in TILE_SIZES.indices) {
            div += (tileQuantitiesBySize[i] - ave) * (tileQuantitiesBySize[i] - ave)
        }
        return div.toInt()
    }

    private fun getOpposite(side: Int): Int {
        // 0 -> 2, 1 -> 3, 2 -> 0, 3 -> 1
        return if (side < 2) side + 2 else side - 2
    }

    private fun newField() {
        var i = 0
        for (row in 0 until height step 2) {
            for (col in 0 until width step 2) {
                tiles[i].set(i + 1, row, col, 0)    // size 0 is 2x2
                tileQuantitiesBySize[0]++
                i++
            }
        }
    }

    private fun randomInt(min: Int, max: Int): Int = random.nextInt(min, max + 1)

    private fun getTileSize(currentSize: IntArray): Int {
        for (i in TILE_SIZES.indices) {
            if (TILE_SIZES[i][0] == currentSize[0] && TILE_SIZES[i][1] == currentSize[1]) {
                return i
            }
        }
        return -1   // If no matching tile size is found
    }

    private fun copyField(src: Array<IntArray>): Array<IntArray> =
        Array(src.size) { src[it].copyOf() }

    companion object {
        /** Allowed [0]Row X [1]Cols */
        val TILE_SIZES = arrayOf(
            intArrayOf(2, 2),
            intArrayOf(1, 2), intArrayOf(2, 1),
            intArrayOf(2, 3), intArrayOf(3, 2),
            intArrayOf(1, 3), intArrayOf(3, 1),
            intArrayOf(1, 4), intArrayOf(4, 1),
            intArrayOf(1, 1)
        )

        private const val ROWS = 5
        private const val COLS = 5
        private const val TILE_NUMBER = ROWS * COLS
        private const val DIVERSITY_MIN_TARGET = 20
        private const val CYCLES_LIMIT = 39    // this value is enough for quick result
    }
}
