package org.nebobrod.schulteplus.common

/**
 * Отображение ячеек физического поля 10×10 ↔ плиток 1..25
 * (п. 4.2 ТЗ «Мешанина», docs/TZ_Mishmash.md).
 *
 * Чистый домен без Android-зависимостей; позиции ячеек — row-major 0..99
 * (порядок позиций GridView). `field` — результат [TilePaving.build].
 */
class PavingMap(private val field: Array<IntArray>) {

    init {
        require(field.size == 10 && field.all { it.size == 10 }) { "field must be 10x10" }
    }

    /** 0..99 → 1..25 (row-major, как позиции GridView) */
    fun tileAt(position10x10: Int): Int {
        require(position10x10 in 0..99) { "position out of 0..99: $position10x10" }
        return tileAt(position10x10 / 10, position10x10 % 10)
    }

    fun tileAt(row: Int, col: Int): Int {
        require(row in 0 until 10 && col in 0 until 10) { "cell out of bounds: ($row, $col)" }
        return field[row][col]
    }

    /** Все ячейки плитки (позиции 0..99, row-major) */
    fun cellsOfTile(tileNum: Int): List<Int> {
        require(tileNum in 1..25) { "tile out of 1..25: $tileNum" }
        val result = ArrayList<Int>()
        for (row in field.indices) for (col in field[row].indices) {
            if (field[row][col] == tileNum) result.add(row * 10 + col)
        }
        return result
    }

    /** Границы плитки в ячейках: [minRow, minCol, maxRow, maxCol] (TP-15 — масштаб числа) */
    fun tileBounds(tileNum: Int): IntArray {
        require(tileNum in 1..25) { "tile out of 1..25: $tileNum" }
        return boundsOf(tileNum).copyOf()
    }

    /** Репрезентативная ячейка плитки для подсказки (ближайшая к центру bounding-box) */
    fun anchorCell(tileNum: Int): Int {
        val b = boundsOf(tileNum)
        return ((b[0] + b[2]) / 2) * 10 + (b[1] + b[3]) / 2
    }

    private fun boundsOf(tileNum: Int): IntArray {
        require(tileNum in 1..25) { "tile out of 1..25: $tileNum" }
        var minRow = 10
        var maxRow = -1
        var minCol = 10
        var maxCol = -1
        for (row in field.indices) for (col in field[row].indices) {
            if (field[row][col] == tileNum) {
                minRow = minOf(minRow, row)
                maxRow = maxOf(maxRow, row)
                minCol = minOf(minCol, col)
                maxCol = maxOf(maxCol, col)
            }
        }
        require(minRow != 10) { "tile $tileNum not found on field" }
        return intArrayOf(minRow, minCol, maxRow, maxCol)
    }

    /**
     * Является ли сторона ячейки внешней границей её плитки.
     * @param side 0 East, 1 South, 2 West, 3 North (согласовано с [TilePaving])
     */
    fun isOuterSide(row: Int, col: Int, side: Int): Boolean {
        require(row in 0 until 10 && col in 0 until 10) { "cell out of bounds: ($row, $col)" }
        val tile = field[row][col]
        return when (side) {
            0 -> col + 1 >= 10 || field[row][col + 1] != tile
            1 -> row + 1 >= 10 || field[row + 1][col] != tile
            2 -> col - 1 < 0 || field[row][col - 1] != tile
            3 -> row - 1 < 0 || field[row - 1][col] != tile
            else -> error("side out of 0..3: $side")
        }
    }
}
